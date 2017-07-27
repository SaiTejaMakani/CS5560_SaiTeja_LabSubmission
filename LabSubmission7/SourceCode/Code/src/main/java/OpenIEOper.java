
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Quadruple;
import rita.RiWordNet;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;


/**
 * Created by shalin on 7/3/2017.
 */

public class OpenIEOper {

    NLPOperations nlpOper=new NLPOperations();

    static String dataPropertyFile = "E:\\Knowledge Discovery Management\\QASystem\\data\\propertiesYahoo.txt";
    static String individualsFile = "E:\\Knowledge Discovery Management\\QASystem\\data\\individualYahoo.txt";
    static String classFile = "E:\\Knowledge Discovery Management\\QASystem\\data\\classYahoo.txt";
    static String objectPropertyFile = "E:\\Knowledge Discovery Management\\QASystem\\data\\objectPropertiesYahoo.txt";
    static String tripletFile = "E:\\Knowledge Discovery Management\\QASystem\\data\\TripletFileYahoo.txt";

    List<String> tempList;
    Map<String, String> individualList;
    Map<String, List<String>> objectProperties;
    Map<String, String> dataProperties;
    List<String> subjects;
    String relationType;




    public void GenerateFinalTriplets(String sen) throws IOException {
        StringBuilder dataPropertyStrBuilder = new StringBuilder();
        StringBuilder objPropertyStrBuilder = new StringBuilder();
        StringBuilder individualPropertyStrBuilder = new StringBuilder();

        Document docs = new Document(sen);
        for (Sentence sent : docs.sentences()) {
            Collection<Quadruple<String, String, String, Double>> triplets = sent.openie();
            if (!triplets.isEmpty()) {
                for (Quadruple<String, String, String, Double> triplet : triplets) {
                    //retrieving subject from the quadruple and saving it in a list
                    String subject = triplet.first;
                    String relation = triplet.second;
                    String object = triplet.third;
                    String subjectNER = nlpOper.GetNER(subject);
                    String objNER = nlpOper.GetNER(object);
                    tempList.clear();

                    if (subjectNER.equals("O")) {
                        subjects.add(subject);
                        tempList.add(subject);
                        if (objNER.equals("O")) {
                            tempList.add(object);
                        } else {
                            tempList.add(objNER);
                        }
                        objectProperties.put(relation, tempList);
                    } else {
                        individualList.put(subjectNER, subject);
                        tempList.add(subjectNER);
                        if (objNER.equals("O")) {
                            tempList.add(object);
                        } else {
                            tempList.add(objNER);
                        }
                    }

                    //retrieving object from the quadruple and saving it in a list
                    if (objNER.equals("O")) {
                        subjects.add(object);
                    } else {
                        dataProperties.put("relatedTo", objNER);
                    }

                    //retrieving predicates from the quadruple

                    if (objNER.equals("O"))
                        relationType = "Obj";
                    else
                        relationType = "Data";

                    String generatedTriplet = subject + "," + relation + "," + object + "," + relationType;
                    ReadWriteOperation.WriteToFile(generatedTriplet, tripletFile);

                    if (objectProperties.isEmpty()) {

                        for (String key : dataProperties.keySet()) {
                            dataPropertyStrBuilder.append(key);
                        }
                        for (String val : dataProperties.values()) {
                            dataPropertyStrBuilder.append(",");
                            dataPropertyStrBuilder.append(val);
                        }
                        ReadWriteOperation.WriteToFile(dataPropertyStrBuilder.toString(), dataPropertyFile);
                    } else {
                        String keyObj = "";
                        for (String key : objectProperties.keySet()) {
                            objPropertyStrBuilder.append(key);
                            keyObj = key;
                        }
                        for (String val : objectProperties.get(keyObj)) {
                            objPropertyStrBuilder.append(",");
                            objPropertyStrBuilder.append(val);
                        }
                        objPropertyStrBuilder.append(",Func");
                        ReadWriteOperation.WriteToFile(objPropertyStrBuilder.toString(), objectPropertyFile);
                    }
                    for (String val : subjects) {
                        ReadWriteOperation.WriteToFile(val, classFile);
                    }


                    for (String key : individualList.keySet()) {
                        individualPropertyStrBuilder.append(key);
                    }
                    for (String val : individualList.values()) {
                        individualPropertyStrBuilder.append(",");
                        individualPropertyStrBuilder.append(val);
                    }
                    //indLst.add(individualPropertyStrBuilder.toString());
                    ReadWriteOperation.WriteToFile(individualPropertyStrBuilder.toString(), individualsFile);



/*
                stripDuplicatesFromFile("ObjectProperties");
                stripDuplicatesFromFile("Individuals");
                stripDuplicatesFromFile("DataProperties");
                stripDuplicatesFromFile("Clas");*/
                }
            }
        }
    }

    public static HashSet<String> getSynonyms(String word) {
        RiWordNet wordnet = new RiWordNet("E:\\Knowledge Discovery Management\\WordNet-3.0\\WordNet-3.0");
        String[] poss = wordnet.getPos(word);
        HashSet<String> synonym = new HashSet<>();
        for (int j = 0; j < poss.length; j++) {
            System.out.println("\n\nSynonyms for " + word + " (pos: " + poss[j] + ")");
            String[] synonyms = wordnet.getAllSynonyms(word, poss[j], 10);
            for (int i = 0; i < synonyms.length; i++) {
                synonym.add(synonyms[i]);
            }
        }
        return synonym;
    }

    public static void RemoveDuplicatesFromFile (String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            Set<String> lines = new HashSet<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for (String unique : lines) {
                writer.write(unique);
                writer.newLine();
            }
            writer.close();

        } catch (IOException io) {

        }
    }
}

