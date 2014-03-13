import java.io.PrintWriter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;


public class Oblig4 {
	private Model rdfSchema;
	private Model rdfData;
	private Model inferredModel;
	private Model resultModel;
	
	public static Oblig4 create() {
		return new Oblig4();
	}
	
	public Oblig4(){
		rdfSchema = ModelFactory.createDefaultModel();
		rdfData = ModelFactory.createDefaultModel();
		inferredModel = ModelFactory.createDefaultModel();
	}
	
	private void loadSchema(String schemaFile) {
		loadFileIntoModel(rdfSchema, schemaFile);
	}

	private void loadRdfGraph(String rdfDataFile) {
		loadFileIntoModel(rdfData, rdfDataFile, "TURTLE");
	}
	
	private void loadFileIntoModel(Model model, String rdfFile) {
		model.read( rdfFile, FileUtils.guessLang(rdfFile) );
	}
	
	private void loadFileIntoModel(Model model, String rdfFile, String language) {
		model.read( rdfFile, language );
	}
	
	private void reasoning() {
		inferredModel = ModelFactory.createRDFSModel(rdfSchema, rdfData);
		inferredModel = rdfData;
	}
	
	private void execute(String queryFile) {
		try {
			Query query = QueryFactory.read(queryFile);
			QueryExecution qe = QueryExecutionFactory.create(query, inferredModel);
			resultModel = qe.execConstruct();
		} catch (Exception e) {
			System.out.printf("Something went wrong while trying execute "
					+ " the query in file: %s", queryFile + "\n");
		}
	}
	
	private void saveTo(String foafFile) {
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
		foafFamilyRelationsCreator.loadSchema(schemaFile);
		foafFamilyRelationsCreator.loadRdfGraph("http://sws.ifi.uio.no/inf3580/v14/oblig/3/simpsons.ttl");
		foafFamilyRelationsCreator.reasoning();
		foafFamilyRelationsCreator.execute(queryFile);
		foafFamilyRelationsCreator.saveTo(foafFile);
	}
}
