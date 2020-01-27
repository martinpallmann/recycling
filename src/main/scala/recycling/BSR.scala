package recycling

import java.time.LocalDate

import org.jsoup.Connection.Method
import org.jsoup.Jsoup

object BSR {

  def p(o: Any): Unit = System.err.println(o)

  def dates(address: Address): List[LocalDate] = {
    val res1 = Jsoup
      .connect("https://www.bsr.de/abfuhrkalender-20520.php")
      .method(Method.GET)
      .execute()
    val res2 = Jsoup
      .connect(s"https://www.bsr.de/abfuhrkalender_ajax.php?script=dynamic_search&step=1&q=${address.street}")
      .method(Method.GET)
      .cookies(res1.cookies)
      .execute()

    Nil
  }
}
