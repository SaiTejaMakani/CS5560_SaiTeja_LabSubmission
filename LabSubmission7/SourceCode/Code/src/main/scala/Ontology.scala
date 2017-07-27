import java.io.FileOutputStream
import com.clarkparsia.owlapi.explanation.util.OntologyUtils
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat
import org.semanticweb.owlapi.model._
import org.semanticweb.owlapi.util.DefaultPrefixManager
import scala.io.Source


  object Ontology {
    def GenerateOntology(path: String,ontName: String ): Unit = {

      val OntURI = "http://www.semanticweb.org/shalin/ontologies/2017/6/"

      val owlManager = OWLManager.createOWLOntologyManager
      //creating owlManager manager
      val dataFactory = owlManager.getOWLDataFactory //In order to create objects that represent entities

      val obamaOnto = owlManager.createOntology(IRI.create(OntURI, ontName+"#"))
      //Prefix for all the entities
      val prefixManager = new DefaultPrefixManager(null, null, OntURI + ontName+"#")


      // Declaration Axiom for creating Classes
      val classes = Source.fromFile(path+"/Classes").getLines()

      classes.foreach(f => {
        val ontClass = dataFactory.getOWLClass(f, prefixManager)
        val declarationAxiomclass = dataFactory.getOWLDeclarationAxiom(ontClass)
        owlManager.addAxiom(obamaOnto, declarationAxiomclass)
      })

      // Creating SubClassOfAxiom
      val subClasses = Source.fromFile(path+"/SubClasses").getLines()

      subClasses.foreach(line => {
        val str = line.split(",")
        val ontClass = dataFactory.getOWLClass(str(0), prefixManager)
        val subCls = dataFactory.getOWLClass(str(1), prefixManager)
        val declarationAxiom = dataFactory.getOWLSubClassOfAxiom(subCls, ontClass)
        owlManager.addAxiom(obamaOnto, declarationAxiom)
      })


      //Creating Object Properties for obama ontology
      val objprop = Source.fromFile(path+"/ObjectProperties").getLines()
      objprop.foreach(f => {
        val str = f.split(",")
        val obamaDomain = dataFactory.getOWLClass(str(1), prefixManager)
        val dataRange = dataFactory.getOWLClass(str(2), prefixManager)
        //Creating Object property ‘hasGender’
        val objpropaxiom = dataFactory.getOWLObjectProperty(str(0), prefixManager)

        val dataRangeAxiom = dataFactory.getOWLObjectPropertyRangeAxiom(objpropaxiom, dataRange)
        val domainAxiom = dataFactory.getOWLObjectPropertyDomainAxiom(objpropaxiom, obamaDomain)

        //Adding Axioms for obama ontology
        owlManager.addAxiom(obamaOnto, dataRangeAxiom)
        owlManager.addAxiom(obamaOnto, domainAxiom)
      })


      val dataprop = Source.fromFile(path+"/DataProperties").getLines()

      dataprop.foreach(f => {
        val str = f.split(",")
        val obamaDomain = dataFactory.getOWLClass(str(1), prefixManager)

        // Data Properties creation

        val fullName = dataFactory.getOWLDataProperty(str(0), prefixManager)
        val domainAxiomfullName = dataFactory.getOWLDataPropertyDomainAxiom(fullName, obamaDomain)
        owlManager.addAxiom(obamaOnto, domainAxiomfullName)
        //Defining String Datatype
        if (str(2) == "string") {
          val stringDatatype = dataFactory.getStringOWLDatatype()
          val rangeAxiomfullName = dataFactory.getOWLDataPropertyRangeAxiom(fullName, stringDatatype)
          //Adding this Axiom to Ontology
          owlManager.addAxiom(obamaOnto, rangeAxiomfullName)
        }
        else if (str(2) == "int") {
          //Defining Integer Datatype
          val Datatype = dataFactory.getIntegerOWLDatatype()
          val rangeAxiomfullName = dataFactory.getOWLDataPropertyRangeAxiom(fullName, Datatype)
          //Adding this Axiom to Ontology
          owlManager.addAxiom(obamaOnto, rangeAxiomfullName)
        }
      })


      //Creating NamedIndividuals using ClassAssertionAxiom
      val individuals = Source.fromFile(path+"/Individuals").getLines()

      individuals.foreach(line => {
        val str = line.split(",")
        val cls = dataFactory.getOWLClass(str(0), prefixManager)
        val ind = dataFactory.getOWLNamedIndividual(str(1), prefixManager)
        val classAssertion = dataFactory.getOWLClassAssertionAxiom(cls, ind)
        owlManager.addAxiom(obamaOnto, classAssertion)
      })

      val triplets = Source.fromFile(path+"/Triplets").getLines()
      triplets.foreach(f => {
        val str = f.split(",")
        val sub = dataFactory.getOWLNamedIndividual(str(0), prefixManager)


        if (str(3) == "Data") {
          val pred = dataFactory.getOWLDataProperty(str(1), prefixManager)
          val dat = dataFactory.getOWLLiteral(str(2))
          val datAsser = dataFactory.getOWLDataPropertyAssertionAxiom(pred, sub, dat)
          owlManager.addAxiom(obamaOnto, datAsser)
        }
        if (str(3) == "Obj") {
          val pred = dataFactory.getOWLObjectProperty(str(1), prefixManager)
          val obj = dataFactory.getOWLNamedIndividual(str(2), prefixManager)
          val objAsser = dataFactory.getOWLObjectPropertyAssertionAxiom(pred, sub, obj)
          owlManager.addAxiom(obamaOnto, objAsser)
        }
      })

      val os = new FileOutputStream(path+ontName+".owl")
      val owlxmlFormat = new OWLXMLDocumentFormat
      owlManager.saveOntology(obamaOnto, owlxmlFormat, os)
      System.out.println(ontName+" Created")

    }
  }


