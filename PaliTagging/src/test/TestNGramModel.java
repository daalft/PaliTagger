package test;

import opennlp.tools.ngram.NGramModel;
import opennlp.tools.util.StringList;

public class TestNGramModel {

	public void run () {
		NGramModel model = new NGramModel();
		StringList sl = new StringList("hell");
		model.add("hello", 1, 4);
		model.add("hell",1,4);
		model.add(sl);
		System.out.println(model.getCount(new StringList("lz")));
		for (StringList s : model) {
			System.out.println(s);
		}
	}
	
	public static void main(String[] args) {
		new TestNGramModel().run();
	}
}
