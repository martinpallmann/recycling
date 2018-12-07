package recycling

import java.time.{LocalDate, Month}
import java.time.format.DateTimeFormatter

import org.jsoup.Connection.Method
import org.jsoup.Jsoup
import io.circe.Json
import io.circe.parser.parse

import scala.collection.JavaConverters._

object BSR {
  def apply(address: Address, christmastree: Boolean): List[(LocalDate, String)] = {

    val houseNo = address.houseNo
    val streetP = address.street
    val zip = address.zip

    val res = Jsoup
      .connect("https://www.bsr.de/weihnachtsbaumabfuhr-23335.php")
      .method(Method.GET)
      .execute()

    val res2 = Jsoup
      .connect(s"https://www.bsr.de/abfuhrkalender_ajax.php?script=dynamic_search&step=1&q=$streetP")
      .method(Method.GET)
      .cookies(res.cookies())
      .execute()

    def extractChildren(arr: Vector[Json]): Map[String, String] = {
      arr.map(_.asObject).foldLeft(Map.empty[String, String]) {
        case (acc, elem) =>
          elem.fold(acc)(e => {
            (e("value").flatMap(_.asString), e("streetkey").flatMap(_.asString)) match {
              case (Some(k), Some(v)) => acc.updated(k, v)
              case _ => acc
            }
          })
      }
    }

    def fuzzyGet(k: String, m: Map[String, String]): Option[(String, String)] = {
      m.keys.filter(_.contains(k)).toList.foldLeft(None: Option[String]) {
        case (acc, elem) => acc.fold(Some(elem): Option[String])(_ => None)
      }.flatMap(x => m.get(x).map(s => (x, s)))
    }

    val street = for {
      json <- parse(res2.body()).toOption
      elems <- json.asArray
      value <- fuzzyGet(zip, extractChildren(elems))
    } yield value

    street.map { case (name, _) =>
      Jsoup
        .connect(s"""https://www.bsr.de/abfuhrkalender_ajax.php?script=dynamic_search&step=2&q=$name""")
        .cookies(res.cookies())
        .method(Method.POST)
        .execute()
    }

    val now = LocalDate.now()

    val sm = DateTimeFormatter.ofPattern("MM yyyy").format(now)
    val dp = DateTimeFormatter.ofPattern("dd.MM.yyyy").format(now)

    def items(items: String) = {
      val params: Map[String, String] = Map(
        "abf_strasse" -> streetP,
        "abf_hausnr" -> houseNo,
        "tab_control" -> "Liste",
        "abf_config_weihnachtsbaeume" -> (if (christmastree) "on" else ""),
        "abf_config_restmuell" -> "on",
        "abf_config_biogut" -> "on",
        "abf_config_wertstoffe" -> "on",
        "abf_config_laubtonne" -> "on",
        "abf_selectmonth" -> sm,
        "abf_datepicker" -> dp,
        "listitems" -> items
      )

      val res4 =
        Jsoup
          .connect("https://www.bsr.de/abfuhrkalender_ajax.php?script=dynamic_kalender_ajax")
          .method(Method.POST)
          .cookies(res.cookies())
          .data(params.toList.flatMap({ case (k, v) => List(k, v) }): _*)
          .execute()
      val doc = res4.parse()
      val elems = doc.select(".TerminItem")
      elems.iterator().asScala.map(e => {

        val d = e.select("time").attr("datetime")
        val t = e.select(".TerminFraktionen img").attr("alt")
        (LocalDate.parse(d), t)

      }).toList
    }


    if (!christmastree) {
      items("7") ++
      items("14") ++
      items("21") ++
      items("28")
    } else {
      items("7")
    }
  }
}
