package recycling

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy
import tagsoup.Tagsoup

import scala.util.Try


object Veolia {
  def dates(tour: Int): List[LocalDate] = {
    val doc = Tagsoup.get("https://www.veolia.de/abfuhrplaene").html
    val assets = doc ~ ".asset"
    val berlin = assets.find(x => (x ~ ".bo-chart-title").map(_.text).contains("Berlin"))
    val urlOpt = berlin.flatMap(e =>
      (e ~ "a").find(_.text.contains("Abfuhrkalender")).flatMap(_.attr("href"))
    )
    def extractYear(s: String): Option[Int] = {
      val i = s.lastIndexOf('/')
      val r = ".+(\\d{4}).+".r
      val r(d) = s.substring(i + 1)
      Try {d.toInt }.toOption
    }
    for {
      url <- urlOpt
      pdf = Tagsoup.get(url).pdf
      year <- extractYear(url)
    } yield {
      extractTour(tour, year)(pdf)
    }
  }.getOrElse(Nil)

  def extractTour(tour: Int, year: Int)(pdf: PdfDocument): List[LocalDate] = {

    def month(elem: String): String =
      elem.substring(elem.lastIndexOf('.') + 1)

    def addMonth(elem: String, month: String): String =
      elem.replace("+", month)

    val strategy = new LocationTextExtractionStrategy()
    new PdfCanvasProcessor(strategy)
      .processPageContent(pdf.getFirstPage)

    val fmt = DateTimeFormatter.ofPattern("d.M.yyyy")

    strategy
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
      .map(x => LocalDate.parse(x, fmt))
  }
}