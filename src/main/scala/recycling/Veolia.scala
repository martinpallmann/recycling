package recycling

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Veolia {

  val fmt: DateTimeFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")

  // go to https://www.veolia.de/abfuhrplaene
  // check tour 10

  val dates: List[LocalDate] = List(
    "21.12.2020",
    "19.1.2021",
    "16.2.2021",
    "16.3.2021",
    "13.4.2021",
    "11.5.2021",
    "8.6.2021",
    "6.7.2021",
    "3.8.2021",
    "31.8.2021",
    "28.9.2021",
    "26.10.2021",
    "23.11.2021",
    "21.12.2021",
    "18.1.2022"
  ).map(LocalDate.parse(_, fmt))

}
