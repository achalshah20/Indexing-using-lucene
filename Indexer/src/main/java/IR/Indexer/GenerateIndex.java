package IR.Indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.document.Field;

public class GenerateIndex {

	private Boolean isOnlyTextTag = false;

	public GenerateIndex() {
	}

	public GenerateIndex(Boolean _isOnlyTextTag) {
		this.isOnlyTextTag = _isOnlyTextTag;
		// TODO Auto-generated constructor stub
	}

	public void buildIndex(String indexPath, String docsPath, Analyzer analyzer) throws IOException {

		// System.out.println(indexPath);
		File fileslisttodelete = new File(indexPath);
		for (File file : fileslisttodelete.listFiles())
			if (!file.isDirectory())
				file.delete();

		Directory dir = FSDirectory.open(Paths.get(indexPath));
		// Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, iwc);

		File filesList = new File(docsPath);
		//System.out.println(filesList.getAbsolutePath());

		System.out.println("Total number of files in corpus: " + filesList.listFiles().length);

		for (File file : filesList.listFiles()) {

			// System.out.println(file.getAbsolutePath());
			StringBuffer fileText = new StringBuffer(readTextFile(file.getAbsolutePath()));
			// System.out.println(fileText);

			String docStartTag = "<DOC>";
			String docEndTag = "</DOC>";

			// Find the index of <DOC>
			int startDocIndex = fileText.indexOf(docStartTag);
			while (startDocIndex != -1) {
				startDocIndex += docStartTag.length();

				// Find the index of </DOC>
				int endDocIndex = fileText.indexOf(docEndTag, startDocIndex);
				Document doc = new Document();

				if (endDocIndex > 0) {

					String docText = fileText.substring(startDocIndex, endDocIndex);
					StringBuffer docSb = new StringBuffer(docText);


					// System.out.println(extractTag(docSb, "DOCNO"));
					if (!isOnlyTextTag) {
						doc.add(new StringField("DOCNO", extractTag(docSb, "DOCNO"), Field.Store.YES));
						doc.add(new TextField("HEAD", extractTag(docSb, "HEAD"), Field.Store.YES));
						doc.add(new TextField("BYLINE", extractTag(docSb, "BYLINE"), Field.Store.YES));
						doc.add(new StringField("DATELINE", extractTag(docSb, "DATELINE"), Field.Store.YES));
					}

					// Always extract text tags and add in doc
					doc.add(new TextField("TEXT", extractTag(docSb, "TEXT"), Field.Store.YES));

					
				}
				writer.addDocument(doc);
				// Find all the doc tags and process
				startDocIndex = fileText.indexOf(docStartTag, endDocIndex);
			}

		}

		writer.forceMerge(1);
		writer.commit();
		writer.close();
	}

	private String readTextFile(String file) throws IOException {

		StringBuilder sb = new StringBuilder();
		BufferedReader buffreader = null;
		try {
			FileReader freader = new FileReader(new File(file));
			buffreader = new BufferedReader(freader);
			String ls = System.getProperty("line.separator");

			String line = null;
			while ((line = buffreader.readLine()) != null) {
				sb.append(line);
				sb.append(ls);
			}

			buffreader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (buffreader != null)
				buffreader.close();

		}

		return sb.toString();

	}

	private String extractTag(StringBuffer sbuff, String tag) {
		String startTag = "<" + tag + ">";
		String endTag = "</" + tag + ">";

		String extractedString = new String();
		int startTagIndex = sbuff.indexOf(startTag);
		while (startTagIndex > 0) {
			startTagIndex += startTag.length();
			int endTagIndex = sbuff.indexOf(endTag, startTagIndex);

			if (endTagIndex >= 0) {
				extractedString += " " + sbuff.substring(startTagIndex, endTagIndex);
			}

			startTagIndex = sbuff.indexOf(startTag, endTagIndex);
		}
		return extractedString;
	}

	public void analyseIndexes(String indexPath) throws IOException {
		IndexReader iReader = DirectoryReader.open(FSDirectory.open(Paths.get((indexPath))));

		System.out.println("==================================================================");
		// Print the total number of documents in the corpus
		System.out.println("Total number of documents in the corpus: " + iReader.maxDoc());

		Terms vocabulary = MultiFields.getTerms(iReader, "TEXT");

		// Print the size of the vocabulary for <field>TEXT</field>, applicable
		// when the index has only one segment.
		System.out.println("Size of the vocabulary for this field: " + vocabulary.size());

		// Print the total number of documents that have at least one term for
		// <field>TEXT</field>
		System.out
				.println("Number of documents that have at least one term for this field: " + vocabulary.getDocCount());

		// Print the total number of tokens for <field>TEXT</field>
		System.out.println("Number of tokens for this field: " + vocabulary.getSumTotalTermFreq());

		// Print the total number of postings for <field>TEXT</field>
		System.out.println("Number of postings for this field: " + vocabulary.getSumDocFreq());

		System.out.println("==================================================================");

		// Print the vocabulary for <field>TEXT</field>
		/*
		 * TermsEnum iterator = vocabulary.iterator(); BytesRef byteRef = null;
		 * System.out.println(
		 * "========================Vocabulary-Start==========================")
		 * ; int i = 0; while ((byteRef = iterator.next()) != null) { String
		 * term = byteRef.utf8ToString(); System.out.print(term + " "); i++;
		 * 
		 * if (i > 10) break; } System.out.println(); System.out.println(
		 * "========================Vocabulary-End==========================");
		 */
		System.out.println();
		iReader.close();
	}

}
