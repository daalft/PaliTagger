package dependency;

import edu.stanford.nlp.ling.TaggedWord;

public class SimpleDependency {

	TaggedWord dependent, governor;
	Relation relation;
	int depIndex, govIndex;
	
	public SimpleDependency(TaggedWord dep, int depIndex, TaggedWord gov, int govIndex, Relation rel) {
		dependent = dep;
		governor = gov;
		relation = rel;
		this.govIndex = govIndex;
		this.depIndex = depIndex;
	}
	
	public int getGovIndex () {
		return govIndex;
	}
	
}
