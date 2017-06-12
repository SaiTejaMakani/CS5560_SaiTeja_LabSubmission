/**
  * Created by makanist on 6/12/2017.
  */


object WordCount {
  def main(args: Array[String]) {
    val inputFile = Array("sai","sai","teja","sai","sai","teja","teja")
    var x=0;
    var count1=0;
    if(inputFile.contains("sai")){
      println("sai" + "found in the array" + "   "+ inputFile.contains("sai"))
    }
    if(inputFile.contains("teja")){
      println("Teja" + "Found in the array");
    }
    if(!inputFile.contains("makani")){
      println("Makani is not forund in array");
    }

     }
}
