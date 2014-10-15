package util

import org.joda.time.DateTime

class DateFormat(df: String) { def get = df }

trait DateUtils {
  implicit class DateRange(dateStr: String) {
    val MILLIS_IN_DAY = 1000 * 60 * 60 * 24
    lazy val date = DateTime.parse(dateStr)
    def ->>(endDateStr: String) (implicit df: DateFormat = new DateFormat("yyyy-MM-dd")) = {
      val endDate = DateTime.parse(endDateStr)
      if (endDate.isBefore(date.getMillis))
        throw new Exception("end date is before start date")
      else
        (0 to ((endDate.getMillis - date.getMillis) / MILLIS_IN_DAY).toInt).map{ d => date.plusDays(d).toString(df.get) }
    }
  }
}
