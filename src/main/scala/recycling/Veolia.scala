package recycling

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Veolia {

  val fmt: DateTimeFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")

  def dates(tour: Int): List[LocalDate] = List(
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
