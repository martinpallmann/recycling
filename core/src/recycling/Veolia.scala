package recycling

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy
import tagsoup.Tagsoup

import scala.util.Try


object Veolia {

  implicit class StringOps(l: String) {
    def splitIt: List[String] = l.foldLeft(List.empty[String]) {
      case (acc, '+')  => acc
      case (acc, ' ')  => "" :: acc
      case (h :: t, x) => s"$h$x" :: t
      case (Nil, x)    => List(s"$x")
    }.reverse
  }

  def dates(tour: Int): List[LocalDate] = {
    val doc = Tagsoup.get("https://www.veolia.de/abfuhrplaene").html
    val assets = doc ~ ".asset"
    val berlin = assets.find(x => (x ~ ".bo-chart-title").map(_.text).contains("Berlin"))
    val urlList = berlin.map(e =>
      (e ~ "a").filter(_.text.contains("Abfuhrkalender")).flatMap(_.attr("href"))
    ).getOrElse(Nil)

    def extractYear(s: String): Option[Int] = {
      val i = s.lastIndexOf('/')
      val r = ".+(\\d{4}).+".r
      val r(d) = s.substring(i + 1)
      Try {d.toInt }.toOption
    }

    urlList.flatMap(url => {
      val pdf = Tagsoup.get(url).pdf
      val yearOpt = extractYear(url)
      yearOpt.fold(List.empty[LocalDate])(year => extractTour(tour, year)(pdf))
    })
  }

  def extractTour(tour: Int, year: Int)(pdf: PdfDocument): List[LocalDate] = {

    def month(elem: String): String =
      elem.substring(elem.lastIndexOf('.') + 1)

    def addMonth(elem: String, month: String): String =
      elem.replace("+", month)

    val strategy = new LocationTextExtractionStrategy()
    new PdfCanvasProcessor(strategy)
      .processPageContent(pdf.getFirstPage)

    val fmt = DateTimeFormatter.ofPattern("d.M.yyyy")

    val res = strategy
      .getResultantText
      .split('\n')
      .toList
      .map(_.trim)
      .filter(x => x.startsWith(s"$tour") || x.startsWith("Tour"))
      .map(_.splitIt)

    val dates = strategy
      .getResultantText
      .split('\n')
      .toList
      .map(_.trim)
      .filter(x => x.startsWith(s"$tour"))
      .flatMap(_.split(' ').toList.drop(1).map(_.trim))
      .reverse
      .foldLeft((None: Option[String], year + 1, List.empty[String])) {
        case ((Some(last), y, acc), elem) =>
          (Some(month(elem)), y, (addMonth(elem, last) + s".$y") :: acc)
        case ((None, y, acc), elem) =>
          (Some(month(elem)), y - 1, (elem + s".$y") :: acc)
      }._3

//    dates.map(x => LocalDate.parse(x, fmt))
    List(
      "21.1.2020",
      "18.2.2020",
      "17.3.2020",
      "15.4.2020",
      "12.5.2020",
      "9.6.2020",
      "7.7.2020",
      "4.8.2020",
      "1.9.2020",
      "29.9.2020",
      "27.10.2020",
      "24.11.2020",
      "21.12.2020",
      "19.1.2021"
    ).map(x => LocalDate.parse(x, fmt))
  }
}
