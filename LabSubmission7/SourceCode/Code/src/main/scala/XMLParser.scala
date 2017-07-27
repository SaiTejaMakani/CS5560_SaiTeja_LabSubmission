import org.apache.spark.SparkContext
import scala.collection.JavaConversions._

object XMLParser {
  var isTxt=false
  def getText(sparkContext: SparkContext): Unit =   {
    val allFile = ReadWriteOperation.GetListOfFileFromFolder("E:\\Knowledge Discovery Management\\QASystem\\data\\Dataset")
    allFile.foreach(filename=> {
      sparkContext.textFile(filename).collect().foreach(a => {
        if (isTxt) {
          ReadWriteOperation.WriteToFile(a, "data/yahoodata.txt")
        }
        if (a.equals("<TEXT>")) {
          isTxt = true
        }
        if (a.equals("</TEXT>")) {
          isTxt = false
        }
      })
    })
  }
}
