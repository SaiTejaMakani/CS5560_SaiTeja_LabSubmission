import java.io.{File, IOException}
import java.nio.charset.StandardCharsets
import java.util.List

import util.control.Breaks._
import java.util

import scala.collection._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{HashingTF, IDF, Word2Vec, Word2VecModel}

import scala.collection.immutable.HashMap
import com.sun.xml.internal.bind.api.impl.NameConverter.Standard

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.io.{Source, StdIn}

/**
  * Created by shalin on 7/2/2017.
  */
object Main {

  def main(args: Array[String]): Unit = {
    val configurationSpark = new SparkConf()
      .setMaster("local[*]")
      .setAppName("QuestionAnsweringSystem")
    System.setProperty("hadoop.home.dir", "C:\\winutils")


    // val processedData = "E:\\Knowledge Discovery Management\\Tutorials\\ScalaTutorial1\\output\\processedData.txt"
    val sContext = new SparkContext(configurationSpark)

    val yahooFileData = sContext.textFile("data\\yahoodata.txt").collect()
    val fileData = sContext.textFile("data\\AlltheSentences.txt").collect()

    val openIEOper = new OpenIEOper()

    val stopwords = sContext.textFile("data/stopwords.txt").collect()

    //XmlParser.readTextFromYahooData(sContext)
    //ReadWriteOperation.RemoveEmptyLines();

    sContext.parallelize(fileData).collect().foreach(line => {
      if (!line.isEmpty) {
        openIEOper.GenerateFinalTriplets(line)
      }
    })

    sContext.parallelize(yahooFileData).collect().foreach(line => {
      if (!line.isEmpty) {
        openIEOper.GenerateFinalTriplets(line)
      }
    })


    //Remove duplicate lines from text file
    OpenIEOper.RemoveDuplicatesFromFile("E:\\Knowledge Discovery Management\\QASystem\\data\\propertiesYahoo.txt");
    OpenIEOper.RemoveDuplicatesFromFile("E:\\Knowledge Discovery Management\\QASystem\\data\\individualYahoo.txt");
    OpenIEOper.RemoveDuplicatesFromFile("E:\\Knowledge Discovery Management\\QASystem\\data\\ClassYahoo.txt");
    OpenIEOper.RemoveDuplicatesFromFile("E:\\Knowledge Discovery Management\\QASystem\\data\\objectPropertiesYahoo.txt");
    OpenIEOper.RemoveDuplicatesFromFile("E:\\Knowledge Discovery Management\\QASystem\\data\\TripletFileYahoo.txt");



    Ontology.GenerateOntology("data/MyDataSet","ObamaOnto")
    Ontology.GenerateOntology("data/YahooAnswer","ObamaOnto")

  }
}







