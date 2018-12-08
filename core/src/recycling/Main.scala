package recycling

import java.time.LocalDate
import java.util.UUID

import com.softwaremill.sttp.quick._
import io.circe.Json
import io.circe.parser.parse

import scala.util.Try

object Main {

  val serviceDate: Json => Either[String, String] = json =>
    (json \\ "Service_date")
      .headOption
      .flatMap(_.asString)
      .fold(Left("no service date found in json"): Either[String, String])(x => Right(x))

  val toDate: String => Either[String, LocalDate] = s => Try {
    val y = s.substring(0, 4).toInt
    val m = s.substring(4, 6).toInt
    val d = s.substring(6, 8).toInt
    (y, m, d)
  }.fold(
    _ => Left(s"could not extract date from string: $s"),
    { case (y, m, d) => Right(LocalDate.of(y, m, d)) }
  )

  def recyclingDate: Either[String, LocalDate] = {
    val page = sttp
      .get(uri"https://trenntstadt-berlin.de/api-abfuhr.php?adrkey=6990282&step=2")
      .send()
    for {
      rawJson <- page.body
      json <- parse(rawJson).left.map(_.toString)
      dateString <- serviceDate(json)
      date <- toDate(dateString)
    } yield date
  }

  def main(args: Array[String]): Unit = {
    System.setProperty("java.awt.headless", "true")

    def event(d: LocalDate, descr: String): Anniversary =
      Anniversary(d, d, UUID.randomUUID().toString, descr)

    if (args.length != 3) {
      println("need three arguments")
      System.exit(1)
    }
    val a =  Address(args(0), args(1), args(2))
    val m = BSR(a, false).map {
      case (d, s) => Anniversary(d, d, UUID.randomUUID().toString, s)
    }
    val rd = recyclingDate.fold(
      _ => List.empty[VEvent],
      d => List(event(d, "Wertstoffe"))
    )
    val veolia = List(
      event(LocalDate.of(2018, 12, 24), "Papier und Pappe"),
      event(LocalDate.of(2019, 1, 22), "Papier und Pappe")
    )
    println(VCalendar(rd ++ m ++ veolia))
  }
}
