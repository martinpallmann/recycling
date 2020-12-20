package recycling

import java.time.LocalDate

object Main {

  def main(args: Array[String]): Unit = {

    System.setProperty("java.awt.headless", "true")

    val bsr = BSR.dates.map(event("Hausmüll", "BSR"))
    val veolia = Veolia.dates.map(event("Papier und Pappe", "Veolia"))
    println(VCalendar(bsr ++ veolia))
  }

  def event(descr: String, company: String)(d: LocalDate): Anniversary =
    Anniversary(
      d,
      d,
      s"$d-$company@martinpallmann.de",
      descr,
      List(
        DisplayAlarm(descr, RelatedTrigger(DurTime(pos = false, DurHour(8))))
      )
    )
}
