import dispatch.classic._
import util.DateFormat
import util.FileUtils._
import spray.json._
import DefaultJsonProtocol._ // if you don't supply your own Protocol (see below)
import util.DateUtils._

object Gold extends App {
  implicit class JsonObject(value: JsValue) {
    def \(fieldName: String): JsValue = value.asJsObject.fields.getOrElse(fieldName, JsString("-"))
    def asInt = value.toString.replaceAll("\"","").toInt
    def asString = value.toString.replaceAll("\"","")
    def asLong = value.toString.replaceAll("\"","").toLong
  }

  implicit val DATE_FORMAT = new DateFormat("yyyyMMdd")

  val basedir = "/Users/Szymon/Dropbox/dev/gold/data/"
  val urlF = "http://query.nytimes.com/search/sitesearch/#/*/from%sto%s/articles/1/allauthors/relevance/%s/"
  val h = new Http

  ("2014-09-06" ->> "2014-09-30").foreach { date =>
  //  val date = "20140101"
    println(date)
    List("world", "business").foreach { section =>
      //  val section = "world"
      val json = getPageJson(date, section)
      List(json.prettyPrint).writeToFile(basedir+"%s-%s.json".format(date, section))
      val res = extractFields(json)

      val totalArticles = json \ "meta" \ "results_estimated_total"
      println("total:"+totalArticles)
      // get JSON, extract fields, deduplicate
      val data = (res ++ (1 to totalArticles.asInt/10).flatMap{ page =>
        Thread.sleep(100)
        println("Page "+page+".")
        extractFields(getPageJson(date, section, Some(page)))
      }.toList).groupBy(x => x(0)+x(3)).map(_._2.head.mkString("\t"))

      data.writeToFile(basedir+"%s-%s.tsv".format(date, section))
    }
  }

  def extractFields(json: JsValue): List[List[String]] =
    (json \ "results").convertTo[Array[JsValue]].map { article =>
      List(
        article \ "utime" asString,
        article \ "ptime" asString,
        article \ "article:section" asString,
        article \ "author" asString,
        article \ "og:title" asString,
        article \ "og:description" asString
      )
    }.toList

  def getPageJson(date: String, section: String, pageOpt: Option[Int] = None): JsValue = {
    val queryParams = Map(
      "date_range_upper" -> date,
      "date_range_lower" -> date,
      "pt" -> "article",
      "vertical" -> section,
      "sort_order" -> "r")
    val pageParam: Map[String,String] =
      pageOpt match {
        case Some(page) => Map("page" -> page.toString)
        case None => Map[String,String]()
      }
    val req = url("http://query.nytimes.com/svc/cse/v2pp/sitesearch.json") <<? (queryParams ++ pageParam)
    println(req.to_uri.toString)
    JsonParser(h(req as_str)) \ "results"
  }

}
