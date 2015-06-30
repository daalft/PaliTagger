package tagger;

import java.io.*;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class PrecisionRun {

	public void run (String file, POSModel model) throws Exception {
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(new File(file)));
		String l = "";
		POSTaggerME tagger = new POSTaggerME(model);
		int correct = 0; 
		int wrong = 0;
		
		while ((l = br.readLine())!=null) {
			String strip = l.replaceAll("_[A-Z]+", "");
			String[] tags1 = l.replaceAll("[^_\\s]+?_", "").split(" ");
//			System.out.println(strip);
//			System.out.println(tags);
			String[] tags2 = tagger.tag(strip.split(" "));
			String[] strips = strip.split(" ");
			if (tags2.length != tags1.length) {
				// something went wrong
				throw new Exception("Tag lists unequal size");
			}
			for (int i = 0; i < tags2.length; i++) {
				if (tags1[i].equals(tags2[i])) {
					
					correct++;
				} else {
					System.err.println(strip);
					System.err.print(strips[i] + " ");
					System.err.println(tags1[i] + " tagged as " + tags2[i]);
					wrong++;
				}
			}
		}
		br.close();
		System.out.println("Total: " + (correct+wrong));
		System.out.println("Correct: " + correct);
		System.out.println("Wrong: " + wrong);
		System.out.println("Precision: " + (((double)correct)/(correct+wrong)));
	}
	
	public static void main(String[] args) throws Exception {
		InputStream modelIn = new FileInputStream("./data/pali-pos-comb-irr-a-2.bin");
		POSModel model = new POSModel(modelIn);
		new PrecisionRun().run("./data/pali-2.train", model);
	}
}
