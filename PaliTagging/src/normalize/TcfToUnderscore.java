package normalize;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.IllegalDataException;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

/**
 * Converts a TCF tagged file into a tagged file where each 
 * tag is appended to its word by an underscore and 
 * each sentence is on one line
 * (Format required by the POS Trainer)
 * @author David
 *
 */
public class TcfToUnderscore {

	private byte debugLevel = 3;
	
	public void normalize(String file, String output) throws JDOMException, IOException {
		if (debugLevel > 1)
			System.err.println("Initializing normalizer");
		SAXBuilder jdomBuilder = new SAXBuilder();
		Document doc = jdomBuilder.build(file);
		Element root = doc.getRootElement();
		Namespace namespace = Namespace.getNamespace("", "http://www.dspin.de/data/textcorpus");
		Element elem = root.getChild("TextCorpus",namespace);
		Element tokenRoot = elem.getChild("tokens", namespace);
		Element tagRoot = elem.getChild("POStags", namespace);
		List<Element> tokens = tokenRoot.getChildren();
		List<Element> tags = tagRoot.getChildren();
		if (!(tokens.size() == tags.size())) {
			throw new IllegalDataException("The number of tokens must be equal to the number of token tags!");
		}
		if (debugLevel > 2) {
			System.err.println("Aligning data...");
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i).getText()).append("_").append(reduce(tags.get(i).getText()));
			sb.append(tags.get(i).getText().equals("point")?"\n":" ");
		}
		if (debugLevel > 1)
			System.err.println("Writing output");
		write(sb.toString(), output);
	}

	private String reduce(String text) {
		switch(text) {
		case "commonNoun":
		case "adjective":
		case "properNoun":
		case "ordinalAdjective": return "NOUN";
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
		case "indefinitePronoun": return "PRON";
		case "cardinalNumeral": return "NUM";
		case "comma":
		case "invertedComma":
		case "suspensionPoints":
		case "semiColon": return "PUNCT";
		case "point": return "SENT";
			default: break;
		}
		return "UNK";
	}

	private void write (String data, String filename) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF-8"));
		bw.write(data);
		bw.flush();
		bw.close();
	}
	
	public static void main(String[] args) {
		String filename = "C:/senereko.david/misc/pali-trainingskorpus-part-1.tcf";
		String output = "C:/senereko.david/misc/pali-trainingskorpus-normalized-simpl.txt";
		try {
			new TcfToUnderscore().normalize(filename, output);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}
}
