

import java.io.{BufferedWriter, File, FileWriter}
import org.apache.spark.{SparkConf, SparkContext}
//**created by sai Teja 6/19/2017
 // *
 // *
// **
object SparkWordCount {

  def main(args: Array[String]) {

    System.setProperty("hadoop.home.dir","E:\\winutils");

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc=new SparkContext(sparkConf)

    val main_nlp: Main_NLP = new Main_NLP
    val lemmtext:String = main_nlp.Get_lemma()

    val file = new File("input.txt")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(lemmtext.toString)
    bw.close()
    val input= sc.textFile("input.txt")

    println(input)//testing
    //val wc=input.flatMap(line=>{line.split(" ")}).map(word=>(word,1)).cache()

    //val wc=input.flatMap(line=>{line.split(" ")}).map(word=>(word.charAt(0)+"=>",word+",")).cache().distinct()

   val wc=input.flatMap(line=>{line.split(" ")}).map(word=>(word.charAt(0)+"=>",word+",")).cache().distinct()
   val output=wc.reduceByKey(_+_)

    output.saveAsTextFile("output")

    val o=output.collect()

    var s:String="Words:Count \n"
    o.foreach{case(word,count)=>{

     s+=word+" : "+count+"\n"

    }}

  }
//def testing(word: String){println(word)testing
//
//}
}
