package tagger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;

/**
 * Combines a trained language model and a dictionary lookup
 * @author s2daalft
 *
 */
public class CombinedTagger {

	private String model = "./data/pali-pos-maxent-1.bin";
	private POSTaggerME tagger;
	
	public CombinedTagger() {
		try {
			loadModel(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public CombinedTagger (String pathToModel) {
		try {
			loadModel(pathToModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadModel (String path) throws InvalidFormatException, IOException {
		InputStream modelIn = new FileInputStream(path);
		POSModel model = new POSModel(modelIn);
		tagger = new POSTaggerME(model);
	}
	
	public String[] tag (String[] sentence) {
		return tagger.tag(sentence);
	}
}
