package util

trait StatsUtils {

  implicit class Summaries(list: List[Any]) {
    def table: Map[Any, Int] = list.groupBy(identity).map{ case (name,list) => (name, list.size) }
    def tableTsv(colnames: List[Any]) : List[Int] = {
      val summary = list.table
      colnames.map{ col => summary.getOrElse(col, 0) }
    }
  }
}
