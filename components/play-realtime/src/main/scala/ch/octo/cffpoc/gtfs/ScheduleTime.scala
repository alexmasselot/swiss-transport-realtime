package ch.octo.cffpoc.gtfs

/**
 * we must get our own Time, as stopTimes can be with hour >=24 (26: tein comes at 2 in the morning.
 * Joda time does not handle such
 *
 * @param hours
 * @param minutes
 * @param
 * Created by alex on 06/05/16.
 *
 */
case class ScheduleTime(hours: Int, minutes: Int, seconds: Int) {

}

case class CannotParseScheduleTimeException(message: String) extends Exception(message)

object ScheduleTime {
  val timeRegex = """(\d\d):(\d\d):(\d\d)""".r

  def apply(s: String): ScheduleTime = s match {
    case timeRegex(h, m, s) => ScheduleTime(h.toInt, m.toInt, s.toInt)
    case _ => throw CannotParseScheduleTimeException(s)
  }
}