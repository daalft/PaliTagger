package test;

import java.util.List;

import dependency.DependencyFormatter;
import dependency.SimpleDependency;
import edu.stanford.nlp.ling.TaggedWord;
import external.StanfordGerman;

public class ParserTest {

	public void run () {
		String sentence = "Die Idee, nach Italien zu gehen kam ihm beim Frühstück.";
		DependencyFormatter df = new DependencyFormatter();
		StanfordGerman sg = new StanfordGerman();
		List<SimpleDependency> relations = sg.parse(sentence);
		List<TaggedWord> words = sg.taggedYield();
		df.format(words, relations);
		//sg.parse("Ohne Geld lebt sich schwer.");
		//sg.parse("Die Idee, nach Italien zu gehen kam ihm beim Frühstück.");
		//sg.parse("Der Hund läuft, der Hund schläft.");
	}
	
	public static void main(String[] args) {
		new ParserTest().run();
	}
}
