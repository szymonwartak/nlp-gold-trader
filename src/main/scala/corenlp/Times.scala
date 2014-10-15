package corenlp

import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations._
import edu.stanford.nlp.trees.TreeCoreAnnotations._
import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations._

import java.util.Properties
import collection.JavaConversions._
import java.io.File
import util.{FileUtils, StatsUtils}

object TimesNLP extends StatsUtils with FileUtils {
  val basedir = DATA_DIR+"nytimes/"

  val props = new Properties()
  props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, sentiment")
  val pipeline = new StanfordCoreNLP(props)

  val sentiments = new File(basedir).list().map { filename =>
    val articles = io.Source.fromFile(basedir+filename).getLines().toList
    val texts = articles.map(_.split("\t",-1) match { case Array(utime, ptime, section, author, title, desc) => desc })

    (filename, texts.par.flatMap { text =>
//      val texts = io.Source.fromFile(basedir+"20140914-world.tsv").getLines().toList.map(_.split("\t",-1) match { case Array(utime, ptime, section, author, title, desc) => desc })
      val document = new Annotation(text)
      pipeline.annotate(document)
      val sentences = document.get(classOf[SentencesAnnotation])
      sentences.map{ sentence => sentence.get(classOf[ClassName]) }
    }.toList)
  }

  object FileInfo {
    val FIELDS = List("date", "section")
    val fileR = """(\d+)\-(\w+)\.tsv""".r
    def process(filename: String): List[String] = filename match { case fileR(date, section) => List(date, section) }
  }

  val SEP = "\t"
  val colnames = List("Very positive", "Positive", "Neutral", "Negative", "Very negative")
  val res = (FileInfo.FIELDS ::: colnames).mkString(SEP) :: sentiments.map(d => (FileInfo.process(d._1) ::: d._2.tableTsv(colnames)).mkString(SEP)).toList
  res.writeToFile(basedir+"sentiment.tsv")

}
