package dependency;

import java.util.List;

import edu.stanford.nlp.ling.TaggedWord;

public class DependencyFormatter {

	public void format (List<TaggedWord> words, List<SimpleDependency> relations) {
		for (int i = 0; i < words.size(); i++) {
			TaggedWord word = words.get(i);
			SimpleDependency depRel = getRelation(relations, word, i);
			System.out.print(i);
			System.out.print("\t");
			System.out.print(word.word());
			System.out.print("\t");
			System.out.print(word.tag());
			System.out.print("\t");
			if (depRel == null)
				System.out.print("-\t-");
			else {
				System.out.print(depRel.relation);
				System.out.print("\t");
				System.out.print(depRel.govIndex);
			}
			System.out.println();
		}
	}
	
	private SimpleDependency getRelation (List<SimpleDependency> relations, TaggedWord word, int index) {
		for (SimpleDependency sd : relations) {
			if (sd.dependent.equals(word) && sd.depIndex == index)
				return sd;
			
		}
		return null;
	}
}
