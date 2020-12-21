package recycling

import java.time.LocalDate

import java.time.format.DateTimeFormatter

object BSR {

  val fmt: DateTimeFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")

  val dates: List[LocalDate] = List(
    "30.12.2020",
    "13.1.2021",
    "27.1.2021",
    "10.2.2021",
    "24.2.2021",
    "11.3.2021",
    "24.3.2021",
    "8.4.2021",
    "21.4.2021",
    "5.5.2021",
    "19.5.2021",
    "2.6.2021",
    "16.6.2021",
    "30.6.2021",
    "14.7.2021",
    "28.7.2021",
    "11.8.2021",
    "25.8.2021",
    "8.9.2021",
    "22.9.2021",
    "6.10.2021",
    "20.10.2021",
    "3.11.2021",
    "17.11.2021",
    "1.12.2021",
    "15.12.2021",
    "29.12.2021"
  ).map(LocalDate.parse(_, fmt))
}
