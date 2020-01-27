package recycling

import java.time.LocalDate

object Main {

  def main(args: Array[String]): Unit = {

    System.setProperty("java.awt.headless", "true")

    if (args.length != 3) {
      println("need three arguments")
      System.exit(1)
    }
    val address = Address(args(0), args(1), args(2))
    val bsr = BSR.dates(address).map(event("Waste", "BSR"))
    val veolia = Veolia.dates.map(event("Papier und Pappe", "Veolia"))
    println(VCalendar(bsr ++ veolia))
  }

  def event(descr: String, company: String)(d: LocalDate): Anniversary =
    Anniversary(
      d,
      d,
      s"$d-$company@martinpallmann.de",
      descr,
      List(DisplayAlarm(descr, RelatedTrigger(DurTime(pos = false, DurHour(8)))))
    )
}
