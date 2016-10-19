package IR.Indexer;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class App {
	public static void main(String[] args) {

		String standardIndexPath = args[0];
		String kwdIndexPath = args[1];
		String stopIndexPath = args[2];
		String simpleIndexPath = args[3];

		String docsPath = args[4];
		
		System.out.println("Indexing using Standard Analyzer: ");
		GenerateIndex gIndexObj = new GenerateIndex();
		try {
			gIndexObj.buildIndex(standardIndexPath, docsPath, new StandardAnalyzer());
			gIndexObj.analyseIndexes(standardIndexPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IndexComparison indexWriterObj = new IndexComparison();
		indexWriterObj.testAnalysers(standardIndexPath, kwdIndexPath, stopIndexPath, simpleIndexPath, docsPath);

	}

}
