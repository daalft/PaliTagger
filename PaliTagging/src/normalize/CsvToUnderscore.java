package normalize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CsvToUnderscore {

	public void batch (String[] filenames, String outname) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String filename : filenames) {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String l = "";
			br.readLine(); // discard first line
			while ((l = br.readLine())!= null) {
				String[] sp = l.split("\t");
				if (sp == null)
					continue;
				if (sp.length < 4) {
					continue;
				}
				String word = sp[2];
				String tag = sp[3];
				if (tag == null)
					continue;
				if (tag.trim().isEmpty())
					continue;
				sb.append(word).append("_").append(reduce(tag)).append(word.matches("[.!?]")?"\n":" ");
			}
			br.close();
		}
		write(sb.toString(), outname);
	}
	
	public void run (String filename, String outname) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
		String l = "";
		br.readLine(); // discard first line
		StringBuilder sb = new StringBuilder();
		while ((l = br.readLine())!= null) {
			String[] sp = l.split("\t");
			if (sp == null)
				continue;
			if (sp.length < 4) {
				continue;
			}
			String word = sp[2];
			String tag = sp[3];
			if (tag == null)
				continue;
			if (tag.trim().isEmpty())
				continue;
			sb.append(word).append("_").append(reduce(tag)).append(word.matches("[.!?]")?"\n":" ");
		}
		br.close();
		write(sb.toString(), outname);
	}
	
	private void write (String data, String filename) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF-8"));
		bw.write(data);
		bw.flush();
		bw.close();
	}
	
	private String reduce(String text) {
		switch(text) {
		case "commonNoun":
		case "adjective":
		
		case "ordinalAdjective": return "NOUN";
		case "properNoun": return "NE";
		case "mainVerb":
		case "lightVerb": return "VERB";
		case "particle":
		case "coordinationParticle":
		case "negativeParticle":
		case "preposition":
		case "conjunction":
		case "adverb":
		case "copula":
		case "interjection":
		case "coordinatingConjunction": return "INDECL";
		case "personalPronoun":
		case "demonstrativePronoun":
		case "pronoun":
		case "relativePronoun":
		case "interrogativePronoun":
		case "reciprocalPronoun":
		case "indefinitePronoun": return "PRON";
		case "numeral":
		case "numeralLetter":
		case "cardinalNumeral": return "NUM";
		case "comma":
		case "invertedComma":
		case "suspensionPoints":
		case "semiColon": return "PUNCT";
		case "point": return "SENT";
			default: break;
		}
		System.err.println(text);
		return "UNK";
	}
	
	public static void main(String[] args) {
		try {
			new CsvToUnderscore().batch(new String[]{"./data/Pali-Trainingskorpus-part-1.csv", "./data/Pali-Trainingskorpus-part-2.csv"}, "./data/pali-combined-b.train");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
