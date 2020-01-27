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

    val v = Veolia.dates(10).map(x => event(x, "Papier und Pappe", "Veolia"))
    println(VCalendar(v))
  }

  def event(d: LocalDate, descr: String, company: String): Anniversary =
    Anniversary(
      d,
      d,
      s"$d-$company@martinpallmann.de",
      descr,
      List(DisplayAlarm(descr, RelatedTrigger(DurTime(pos = false, DurHour(8)))))
    )
}
