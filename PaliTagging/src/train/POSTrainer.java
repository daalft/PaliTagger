package train;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.unitrier.daalft.pali.morphology.MorphologyGenerator;
import de.unitrier.daalft.pali.morphology.element.ConstructedWord;
import de.unitrier.daalft.pali.morphology.paradigm.ParadigmAccessor;
import net.didion.jwnl.dictionary.MorphologicalProcessor;
import opennlp.tools.postag.POSDictionary;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.model.ModelType;

public class POSTrainer {

	@SuppressWarnings("deprecation")
	public void train (String input, String output) throws Exception {
		POSModel model = null;
		InputStream dataIn = null;
		try {
			dataIn = new FileInputStream(input);
			
			ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
			
			ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);
			POSDictionary posDict = new POSDictionary();
			populatePosDict(posDict);
			model = 
					POSTaggerME
					.train("pi", 
							sampleStream, 
							ModelType.MAXENT, posDict,  
							null, 0, 5);
			OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(output));
			model.serialize(modelOut);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dataIn != null) {
				try {
					dataIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private MorphologyGenerator mg;
	
	private void populatePosDict(POSDictionary posDict) throws Exception {
		mg = new MorphologyGenerator(new ParadigmAccessor());
		String pdf = "D:/Users/s2daalft/Downloads/DPPN/DPPN-Lemmata_Arbeitsdatei.csv";
		BufferedReader br = new BufferedReader(new FileReader(new File(pdf)));
		String l = "";
		// discard first line
		br.readLine();
		while ((l=br.readLine())!=null) {
			String[] sp = l.split(";");
			if (sp.length < 2) {
				continue;
			}
			String lemma = sp[0];
			String pos = map(sp[1]);
			System.out.print("Generating " + lemma + " (" + pos + ")");
			List<ConstructedWord> list = mg.generate(null, lemma, pos);
			System.out.println(" " + list.size() + "]]");
			for (ConstructedWord cw : list) {
				if (pos.equals("noun"))
					posDict.put(cw.getWord(), "NOUN");
				else {
					String pos2 = cw.getFeatureSet().getFeature("paradigm");
					posDict.put(cw.getWord(), "NOUN", simplify(pos2));
				}
			}
			
		}
		posDict.put("ca", "INDECL");
		
		HashMap<String, String> imap = new HashMap<>();
		// pronouns
		imap.put( "asu", "pronoun");
		imap.put( "ayaṃ", "pronoun");
		imap.put( "ko", "pronoun");
		imap.put( "yo", "pronoun");
		
		// irregular forms
		imap.put( "sa", "noun");
		imap.put( "ma", "noun");
		imap.put( "go", "noun");
		imap.put( "raha", "noun");
		imap.put( "sakha", "noun");
		imap.put( "paccakkhadhamma", "noun");
		imap.put( "gaṇḍīvandhavāha", "noun");
		
		// irregular numerals
		imap.put( "eka", "numeral");
		imap.put( "caturo", "numeral");
		imap.put( "tayo", "numeral");
		imap.put( "dve", "numeral");
		imap.put( "ubho", "numeral");
		
		for (Entry<String, String> e : imap.entrySet()) {
			System.err.println("Irregular " + e.getKey());
			String pos = e.getValue();
			List<ConstructedWord> list = mg.generate(null, e.getKey(), pos);
			
			for (ConstructedWord cw : list) {
				if (pos.equals("noun"))
					posDict.put(cw.getWord(), "NOUN");
				else if (pos.equals("pronoun"))
					posDict.put(cw.getWord(), "PRON");
				else if (pos.equals("numeral")){
					
					posDict.put(cw.getWord(), "NUM");
				}
			}
		}
		
		br.close();
	}

	private String simplify(String text) {
		switch(text) {
		case "commonNoun":
		case "adjective":
		case "properNoun":
		case "noun":
		case "ordinalAdjective": return "NOUN";
		case "mainVerb":
		case "verb":
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
		case "numeral":
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

	private String map(String string) {
		switch(string) {
		case "unclear":
		case "":
		case "multiple": return "";
		
		case "group":
		case "grop":
		case "place":
		case "miscellanea":
		case "thing":
		case "person": return "noun";
		
		default: return "";
		}
	}

	public static void main(String[] args) {
		try {
			new POSTrainer().train("./data/pali-combined-a.train", "./data/pali-pos-comb-irr-a-2.bin");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
