package util

import java.io.PrintWriter

object FileUtils {

  implicit class FileWriter(data: Iterable[String]) {
    def writeToFile(filename: String) {
      val pw = new PrintWriter(filename)
      data foreach pw.println
      pw.close()
    }
  }

}
