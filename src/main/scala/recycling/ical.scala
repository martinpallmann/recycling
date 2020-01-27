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
  private val df = DateTimeFormatter.ofPattern("yyyyMMdd")
  val formatDate: LocalDate => String = d => d.format(df)
}

trait VEvent {
  override def toString: String =
    props.map({case (k, v) => s"$k:$v"}).mkString("\r\n")
  def props: List[(String, String)]
}

case class Anniversary(
                        dtstart: LocalDate,
                        dtend: LocalDate,
                        uid: String,
                        summary: String,
                        alarm: List[Alarm]
                      ) extends VEvent {

  import VCalendar._

  def props: List[(String, String)] = List(
    "BEGIN" -> "VEVENT",
    "UID" -> uid,
    "DTSTART;VALUE=DATE" -> formatDate(dtstart),
    "DTEND;VALUE=DATE" -> formatDate(dtend),
    "SUMMARY" -> summary
  ) ++ alarm.flatMap(_.props) ++
    List("END" -> "VEVENT")
}

trait Trigger {
  def param: String
  def value: String
  def prop: (String, String) = s"TRIGGER;$param" -> value
}

abstract class Duration(pos: Boolean) {
  def asString: String
  override def toString: String =
    (if (pos) "" else "-") + "P" + asString
}

case class DurTime(pos: Boolean, v: DurTimeAdt) extends Duration(pos) {
  def asString: String = s"T$v"
}

sealed trait DurTimeAdt
case class DurSecond(d: Int) extends DurTimeAdt {
  override def toString: String = s"${d}S"
}

case class DurMinute(d: Int, s: Option[DurSecond] = None) extends DurTimeAdt {
  override def toString: String = s"${d}M${s.getOrElse("")}"
}

case class DurHour(d: Int, m: Option[DurMinute] = None) extends DurTimeAdt {
  override def toString: String = s"""${d}H${m.getOrElse("")}"""
}

case class DurDate(pos: Boolean, d: DurDay, t: Option[DurTime]) extends Duration(pos) {
  def asString: String = s"""$d${t.getOrElse("")}"""
}

case class DurDay(d: Int) {
  override def toString: String = s"${d}D"
}

case class DurWeek(pos: Boolean, d: Int) extends Duration(pos) {
  override def asString: String = s"${d}W"
}

case class RelatedTrigger(
                           duration: Duration,
                           relatedTo: RelatedTrigger.RelatedTo = RelatedTrigger.Start
                         ) extends Trigger {
  def param: String = s"RELATED=$relatedTo"
  def value: String = s"$duration"
}

object RelatedTrigger {
  sealed trait RelatedTo
  case object Start extends RelatedTo {
    override def toString: String = "START"
  }
  case object End extends RelatedTo {
    override def toString: String = "END"
  }
}

trait Alarm {
  def props: List[(String, String)]
}

case class DisplayAlarm(description: String, trigger: Trigger) extends Alarm {
  def props: List[(String, String)] = List(
    "BEGIN" -> "VALARM",
    "ACTION" -> "DISPLAY",
    "DESCRIPTION" -> description,
    trigger.prop,
    "END" -> "VALARM"
  )
}
