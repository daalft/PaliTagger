package train;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import opennlp.tools.postag.POSDictionary;

public class TagDictionaryCreator {

	private POSDictionary dict;
	
	public TagDictionaryCreator(boolean caseSensitive) {
		if (dict == null)
			dict = new POSDictionary(caseSensitive);
	}
	
	public void put (String word, String...tags) {
		dict.put(word, tags);
	}
	
	public void serialize (String outputFilename) throws IOException {
		OutputStream output = new BufferedOutputStream(new FileOutputStream(outputFilename));
		dict.serialize(output);
		output.close();
	}
}