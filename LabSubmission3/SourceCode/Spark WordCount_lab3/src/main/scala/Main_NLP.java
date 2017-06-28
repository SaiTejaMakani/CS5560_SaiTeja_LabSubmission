
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


/**
 * Created by saiteja on 12-Jun-17.
 */
public class Main_NLP {
    public void QA_process() {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        String Question = new String();//question variable
        String Q_part1 = new String();//part 1 mostlly contains "who", "what", "where", "when", "how" and "when" etc.
        String Q_part2 = new String();//Part 2 mostlly contains "is" , "are" and "many" etc.
        String Q_part3 = new String();//Part 3 contains location, people and organization etc.
        String Q_token3 = new String();// As token 3 is not distinguishable we should track exack word.
        String Answer = new String();// Building answer and printing on console.
        Properties props = new Properties();
        Properties props_Q = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);


// read some text in the text variable
        String text = null;
        //Reading file from the give input datasets.
        try {

            String content = new String(Files.readAllBytes(Paths.get("E:\\KDM\\Spark WordCount_lab3\\scala_Output\\part-00000")));
            text = content;
        } catch (IOException e) {
            System.out.println("Exception occured while reading file");
        }

        ArrayList<String> Persons = new ArrayList<String>();
        ArrayList<String> Locations = new ArrayList<String>();
        ArrayList<String> Organizations = new ArrayList<String>();

// create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

// run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the NER label of the token
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

//                System.out.println(token + "->:" + ne + "\n");testing

                if(ne.contentEquals("ORGANIZATION")){Organizations.add(token.toString());}
                else
                if(ne.contentEquals("PERSON")) {Persons.add(token.toString());}
                else
                if(ne.contentEquals("LOCATION")){Locations.add(token.toString());}
                else
                    continue;

            }
        }
        //refining stored data
        Set<String> hs = new HashSet<>();
        hs.addAll(Persons);
        Persons.clear();
        Persons.addAll(hs);// removed all duplicates in Persons

        hs.clear();//now removing duplicates from organizations
        hs.addAll(Organizations);
        Organizations.clear();
        Organizations.addAll(hs);//removed all duplicates from organizations

        hs.clear();//now removing all duplicates from locations
        hs.addAll(Locations);
        Locations.clear();
        Locations.addAll(hs);//removed all duplicates from locations
        ///

//        System.out.println("org"+ Organizations.size());size of araay lists printed just for testing purpose
//        System.out.println("per"+ Persons.size());
//        System.out.println("Loc"+ Locations.size());

        // Reading the Question from user
        try {
            Scanner in = new Scanner(System.in);

            System.out.println("Please ask relevent Question. Ex: (how many persons?)(what are organizations?)");
            Question = in.nextLine();//reading question.
//            System.out.println("Welcome " + Question);//testing
            //reading question done till here.

        }catch(Exception e){System.out.print("Null pointer exception while reading question.");
        }

        // process the question also through Natural Language Processing.
        props_Q.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");


        // create an empty Annotation just with the given text
        Annotation document_Q = new Annotation(Question);
        StanfordCoreNLP pipeline_Q = new StanfordCoreNLP(props_Q);
        // run all Annotators on this text
        pipeline_Q.annotate(document_Q);


        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences_Q = document_Q.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence_Q : sentences_Q) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token_Q : sentence_Q.get(CoreAnnotations.TokensAnnotation.class)) {

                //Reading Pos
                String pos = token_Q.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                System.out.println(token_Q + ":->" + pos);//printing token for testing purpose every toke is having its key at the end like "PERSON-3"
                if(pos.contentEquals("WP")) {
                    Q_part1 = pos;
                }
                else if(pos.contentEquals("WRB")){
                    Q_part1 = pos;
                }
                else if(pos.contentEquals("JJ")){
                    Q_part2 = pos;
                }
                else if(pos.contentEquals("VBZ")){
                    Q_part2 = pos;
                }
                else if(pos.contentEquals("VBP")){
                    Q_part2 = pos;
                }
                else if (pos.contentEquals("NNS")){
                    Q_part3 = pos;
                    Q_token3 = token_Q.toString();
                }
            }

            //refining stored data

            //
//                    System.out.println(Q_part1 + Q_part2 + Q_part3);// printing the question again
            // Analyzing question and building answer
            // constructing for how many people, places and organization.
            if(Q_part1.contentEquals("WRB") && Q_part2.contentEquals("JJ")){
                if(Q_part3.contentEquals("NNS")){
                    //System.out.print(Q_token3 +"mmm__" + Q_token3.toUpperCase().contentEquals("PERSONS"));
                    if(Q_token3.toUpperCase().contentEquals("PERSONS-3")){
                        Answer = "There are "+Persons.size() + " Persons";
                    }else if(Q_token3.toUpperCase().contentEquals("PLACES-3")){
                        Answer = "There are "+Locations.size() + " Places";
                    }else if(Q_token3.toUpperCase().contentEquals("ORGANIZATIONS-3")){
                        Answer = "There are "+Organizations.size() + " Organizations";
                    }else{Answer = "Unrelevent Question, Please try with a relevent Question!!";}

                }
                else {Answer = "Unrelevent Question, Please try with a relevent Question!!";}
            }
            else if (Q_part1.contentEquals("WP") && Q_part2.contentEquals("VBP")){

                if(Q_part3.contentEquals("NNS")){
                    //System.out.print(Q_token3 +"mmm__" + Q_token3.toUpperCase().contentEquals("PERSONS"));
                    if(Q_token3.toUpperCase().contentEquals("PERSONS-3")){
                        Answer = "There are "+Persons.size() + " Persons listed \n";
                        Print_list(Persons);
                    }else if(Q_token3.toUpperCase().contentEquals("PLACES-3")){
                        Answer = "There are "+Locations.size() + " Places listed \n";
                        Print_list(Locations);
                    }else if(Q_token3.toUpperCase().contentEquals("ORGANIZATIONS-3")){
                        Answer = "There are "+Organizations.size() + " Organizations listed \n";
                        Print_list(Organizations);
                    }else{Answer = "Unrelevent Question, Please try with a relevent Question!!";}

                }
                else {Answer = "Unrelevent Question, Please try with a relevent Question!!";}

            }
            else {Answer = "Unrelevent Question, Please try with a relevent Question!!";}
        }

        System.out.println(Answer);//printting answer
    }//public static void main end

    //This function will print all of the places, persons and organizations when asked a question.
    private void Print_list(ArrayList<String> Print_list) {
        for (int i=0; i<Print_list.size(); i++){
            System.out.println( Print_list.get(i).substring(0,Print_list.get(i).indexOf('-'))+ "\n");
        }
    }
}
