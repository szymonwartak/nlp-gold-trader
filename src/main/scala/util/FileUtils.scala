package util

import java.io.PrintWriter

trait FileUtils {
  val DATA_DIR = "/Users/Szymon/Dropbox/dev/gold/data/"

  implicit class FileWriter(data: Iterable[String]) {
    def writeToFile(filename: String) {
      val pw = new PrintWriter(filename)
      data foreach pw.println
      pw.close()
    }
  }

}
