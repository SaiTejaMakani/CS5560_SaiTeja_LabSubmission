

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.{Files, Paths}

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.commons.io.FileUtils
import java.io.IOException
//**created by sai Teja 6/19/2017
 // *
 // *
// **
object SparkWordCount {

  def main(args: Array[String]) {

    deleteDirectory();//function to delete scala directory if existed.
    System.setProperty("hadoop.home.dir","E:\\winutils");

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc=new SparkContext(sparkConf)

    val input= sc.textFile("E:\\KDM\\bbc\\sport\\011.txt")//scala transform function
    //write rdd transformations here
    println("contains - in each sentence:");
    input.foreach( x => println(x.contains("-")));
    println("first line in input file are::"+input.first());//scala actions
    val inter_input = input.filter(_.contains("-"));//filters "-" charecters
    println("input text file contains:" +inter_input.count() + " lines")//scala action methodes
    //write rdd transformations above
    inter_input.saveAsTextFile("scala_Output")//scala transform function

    val main_nlp: Main_NLP = new Main_NLP//creating an object for java program
    main_nlp.QA_process()//processing QA system.



//   val wc=input.flatMap(line=>{line.split(" ")}).map(word=>(word.charAt(0)+"=>",word+",")).cache().distinct()
//   val output=wc.reduceByKey(_+_)

//    output.saveAsTextFile("output")

//    val o=output.collect()
//
//    var s:String="Words:Count \n"
//    o.foreach{case(word,count)=>{
//
//     s+=word+" : "+count+"\n"
//
//    }}

  }



  @throws[IOException]
  def deleteDirectory(): Unit = {
    try
      FileUtils.deleteDirectory(new File("scala_Output"))
    catch {
      case ioe: IOException =>
        // log the exception here
        ioe.printStackTrace()
        throw ioe
    }
  }

}
