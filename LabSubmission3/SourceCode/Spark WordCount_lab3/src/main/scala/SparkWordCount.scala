import org.apache.commons.io.FileUtils
import java.io.IOException


import java.io.File



import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{HashingTF, IDF, Word2Vec, Word2VecModel}

import scala.collection.immutable.HashMap
//**created by sai Teja 6/19/2017
 // *
 // *
// **
object SparkWordCount {

  def main(args: Array[String]) {
    val input_file : String = "data/Article.txt";//change file name here to reflecton all operations
    deleteDirectory();//function to delete scala directory if existed.
    System.setProperty("hadoop.home.dir","E:\\winutils");

    val sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local[*]")

    val sc=new SparkContext(sparkConf)

    val input_txt= sc.textFile(input_file)//scala transform function

    //lab3 code added from here

    var number_of_tf_ids = 4;
    //Reading the Text File
    val documents = sc.textFile(input_file)

    //Getting the Lemmatised form of the words in TextFile
    val documentseq = documents.map(f => {
      val lemmatised = CoreNLP.returnLemma(f)
      val splitString = lemmatised.split(" ")//lemmanatized words. if you dont want lemms then replace it with f.split()
      splitString.toSeq
    })

    //Creating an object of HashingTF Class
    val hashingTF = new HashingTF()

    //Creating Term Frequency of the document
    val tf = hashingTF.transform(documentseq)
    tf.cache()


    val idf = new IDF().fit(tf)

    //Creating Inverse Document Frequency
    val tfidf = idf.transform(tf)

    val tfidfvalues = tfidf.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val values = ff(2).replace("]", "").replace(")", "").split(",")
      values
    })

    val tfidfindex = tfidf.flatMap(f => {
      val ff: Array[String] = f.toString.replace(",[", ";").split(";")
      val indices = ff(1).replace("]", "").replace(")", "").split(",")
      indices
    })

    //tfidf.foreach(f => println(f))

    val tfidfData = tfidfindex.zip(tfidfvalues)

    var hm = new HashMap[String, Double]

    tfidfData.collect().foreach(f => {
      hm += f._1 -> f._2.toDouble
    })

    val mapp = sc.broadcast(hm)

    val documentData = documentseq.flatMap(_.toList)
    val dd = documentData.map(f => {
      val i = hashingTF.indexOf(f)
      val h = mapp.value
      (f, h(i.toString))
    })
    val tf_words = new Array[String](number_of_tf_ids);//creating an array with size  strings
    var index = 0;//to track IFIDF top 10 indexes
    var ngram_var : String = ""
    val dd1 = dd.distinct().sortBy(_._2, false)
    dd1.take(number_of_tf_ids).foreach(f => {
     // println(f)//to print only words use "f._1"
      tf_words(index) = f._1;
      index+=1//incrementing size.
      ngram_var+=f._1.toString()+ " " //string building for ngram approach
    })
    print ("===ngram_var========"+ngram_var+"============")
    var a1 = getNGrams(ngram_var,2)
    a1.foreach(f=>println(f.mkString(" ")))
    /// w2v from here///

    val input = sc.textFile(input_file).map(line => line.split(" ").toSeq)//for w2v input file

    val modelFolder = new File("myModelPath")

    //tf_words.foreach     //just changed to a1 to sww how ngram works on this
    tf_words.foreach(i =>
      if (modelFolder.exists()) {
        println(i.toString()+"-->  W2V values are below printed:")
        val sameModel = Word2VecModel.load(sc, "myModelPath")
        val synonyms = sameModel.findSynonyms(i.toString, 2)//i.todtring to get tf-ids values

        for ((synonym, cosineSimilarity) <- synonyms) {
          println(s"$synonym $cosineSimilarity")
        }
      }
      else {

        println(i.toString()+"-->  W2V values are below printed:")
        val word2vec = new Word2Vec().setVectorSize(10000).setMinCount(1)

        val model = word2vec.fit(input)

        val synonyms = model.findSynonyms(i.toString, 2)//i.tostring to get tf-ids values

        for ((synonym, cosineSimilarity) <- synonyms) {
          println(s"$synonym $cosineSimilarity")
        }

        model.getVectors.foreach(f => println(f._1 + ":" + f._2.length))

        // Save and load model
        model.save(sc, "myModelPath")

      }

    )
    /// w2v ends here




  //
    input_txt.saveAsTextFile("scala_Output")//scala transform function

    val main_nlp: Main_NLP = new Main_NLP//creating an object for java program
    main_nlp.QA_process()//processing QA system.


  }
  def getNGrams(sentence: String, n:Int): Array[Array[String]] = {
    val words = sentence
    val ngrams = words.split(' ').sliding(n)
    ngrams.toArray
  }//ngram fucntion



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


