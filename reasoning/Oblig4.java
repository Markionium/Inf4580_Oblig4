import java.io.PrintWriter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;

/**
 * @author Mark Polak
 */
public class Oblig4 {
	private Model rdfSchema;
	private Model rdfData;
	private InfModel inferredModel;
	private Model resultModel;
	
	private final static boolean DEBUG = false;
	
	/**
	 * Creates an Oblig4 object
	 * 
	 * @return Oblig4
	 */
	public static Oblig4 create() {
		return new Oblig4();
	}
	
	private Oblig4(){
		rdfSchema = ModelFactory.createDefaultModel();
		rdfData = ModelFactory.createDefaultModel();
		resultModel = ModelFactory.createDefaultModel();
	}
	
	/**
	 * Loads an rdfSchema into the rdfSchema model
	 * 
	 * @param schemaFile
	 */
	public void loadSchema(String schemaFile) {
		loadFileIntoModel( rdfSchema, schemaFile );
	}

	/**
	 * Loads an rdfGraph into the rdfData model
	 * @param rdfDataFile
	 */
	public void loadRdfGraph(String rdfDataFile) {
		loadFileIntoModel( rdfData, rdfDataFile, "TURTLE" );
	}
	
	private void loadFileIntoModel(Model model, String rdfFile) {
		loadFileIntoModel( model, rdfFile, FileUtils.guessLang(rdfFile) );
	}
	
	private void loadFileIntoModel(Model model, String rdfFile, String language) {
		try {
			model.read( rdfFile, language );
		} catch (Exception e) {
			System.out.printf("Something went wrong while trying load "
					+ " the model with data from the file: %s", rdfFile + "\n");
		}
	}
	
	/**
	 * Create an inferred model from the rdfData based on the given rdfSchema
	 */
	public void reasoning() {
		inferredModel = ModelFactory.createRDFSModel( rdfSchema, rdfData );
	}
	
	/**
	 * Execute a query on the inferred model 
	 * 
	 * @param queryFile
	 */
	public void execute(String queryFile) {
		if (inferredModel == null)
			reasoning();
		
		try {
			Query query = QueryFactory.read( queryFile );
			QueryExecution qe = QueryExecutionFactory.create(
					query, inferredModel
			);
			resultModel = qe.execConstruct();
		} catch (Exception e) {
			System.out.printf("Something went wrong while trying execute "
					+ " the query in file: %s", queryFile + "\n");
		}
	}
	
	/**
	 * Save the resultmodel to the given file
	 * 
	 * @param foafFile
	 */
	public void saveTo(String foafFile) {
		writeFile(resultModel, foafFile);
	}
	
	/**
	 * Writes the model to the given outputFile
	 * 
	 * @param outputFile
	 */
	public void writeFile(Model model, String outputFile) {
		try (PrintWriter pw = new PrintWriter(outputFile)) {
			model.write( pw, "TURTLE" );
		} catch (Exception e) {
			System.out.printf("Something went wrong while trying to "
					+ " write to the file: %s", e.getMessage() + "\n");
		}
	}
	
	public void printStuff() {
		System.out.println(
				"\n\nRDF Schema ==========================================\n");
		rdfSchema.write(System.out, "TURTLE");
		System.out.println(
				"\n\nRDF data ==========================================\n");
		rdfData.write(System.out, "TURTLE");
		System.out.println(
				"\n\nInfered model ==========================================\n");
		inferredModel.write(System.out, "TURTLE");
		System.out.println(
				"\n\nQuery result ==========================================\n");
		resultModel.write(System.out, "TURTLE");
	}
	
	/**
	 * Main method to run the program
	 * @param args
	 */
	public static void main(String[] args) {
		String schemaFile, queryFile, foafFile;
		Oblig4 foafFamilyRelationsCreator;
		
		//Check for valid file names
		try {
			schemaFile = args[0];
			queryFile = args[1];
			foafFile = args[2];
		} catch (Exception e) {
			System.out.println("Please supply a input and output file. \n");
			return;
		}
		
		foafFamilyRelationsCreator = Oblig4.create();
		foafFamilyRelationsCreator.loadSchema( schemaFile );
		foafFamilyRelationsCreator.loadRdfGraph(
				"http://sws.ifi.uio.no/inf3580/v14/oblig/3/simpsons.ttl"
		);
		foafFamilyRelationsCreator.reasoning();
		foafFamilyRelationsCreator.execute( queryFile );
		foafFamilyRelationsCreator.saveTo( foafFile );
		
		if (DEBUG) {
			foafFamilyRelationsCreator.printStuff();
		}
	}
}
