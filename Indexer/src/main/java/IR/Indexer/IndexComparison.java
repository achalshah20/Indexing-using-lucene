package IR.Indexer;

import java.io.IOException;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class IndexComparison {

	public void testAnalysers(String standardIndexPath, String kwdIndexPath, String stopIndexPath,
			String simpleIndexPath, String docsPath) {

		GenerateIndex gIndexObj = new GenerateIndex(true);
		try {
			System.out.println("Indexing using Standard Analyzer: ");
			gIndexObj.buildIndex(standardIndexPath, docsPath, new StandardAnalyzer());
			gIndexObj.analyseIndexes(standardIndexPath);

			System.out.println("Indexing using Keyword Analyzer");
			gIndexObj.buildIndex(kwdIndexPath, docsPath, new KeywordAnalyzer());
			gIndexObj.analyseIndexes(kwdIndexPath);

			System.out.println("Indexing using Stop Analyzer");
			gIndexObj.buildIndex(stopIndexPath, docsPath, new StopAnalyzer());
			gIndexObj.analyseIndexes(stopIndexPath);

			System.out.println("Indexing using Simple Analyzer");
			gIndexObj.buildIndex(simpleIndexPath, docsPath, new SimpleAnalyzer());
			gIndexObj.analyseIndexes(simpleIndexPath);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
