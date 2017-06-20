import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
/**
 * Created by saiteja on 12-Jun-17.
 */
public class Main_NLP {
    String text = null;//input text will be stored
    String lemm_text = null; //lemnatized text will be loaded here later

    Main_NLP() {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
// read some text in the text variable

        //Reading file from the give input datasets.
        try {

            String content = new String(Files.readAllBytes(Paths.get("E:\\KDM\\bbc\\sport\\010.txt")));
            text = content;
        } catch (IOException e) {
            System.out.println("Exception occured while reading file");
        }

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
                // this is the lemm tag of the token
                String Lemm = token.get(CoreAnnotations.LemmaAnnotation.class);
                lemm_text += Lemm+" ";

                // this is the NER label of the token
//                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                System.out.print("NER->");
//                System.out.println(token + ":" + ne + "\n");
            }
        }
        //System.out.println(lemm_text);//testing purpose
    }
    public String Get_lemma(){//this function return the lemmanatized words directlly to the scala program.
        return lemm_text;
    }
}
