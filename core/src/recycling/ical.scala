package recycling

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class VCalendar(
  events: List[VEvent],
  prodId: String = "-//Martin Pallmann//VCalendar.scala//EN"
) {

  override def toString: String = (
    List(
      "BEGIN:VCALENDAR",
      "VERSION:2.0",
      s"PRODID:$prodId"
    ) ++
      events.map(_.toString) ++
      List("END:VCALENDAR")
  ).mkString("\r\n")
}

object VCalendar {
  val df = DateTimeFormatter.ofPattern("yyyyMMdd")
  val formatDate: LocalDate => String = d => d.format(df)
}

trait VEvent {
  override def toString: String = (
    "BEGIN:VEVENT" :: props.map({case (k, v) => s"$k:$v"}) ++ List("END:VEVENT")
    ).mkString("\r\n")
  def props: List[(String, String)]
}

case class Anniversary(
                        dtstart: LocalDate,
                        dtend: LocalDate,
                        uid: String,
                        summary: String
                      ) extends VEvent {

  import VCalendar._

  def props: List[(String, String)] = List(
    "UID" -> uid,
    "DTSTART;VALUE=DATE" -> formatDate(dtstart),
    "DTEND;VALUE=DATE" -> formatDate(dtend),
    "SUMMARY" -> summary
  )
}