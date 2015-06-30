package dependency;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.TaggedWord;

public class RelationMapper {

	private final static String RELMAP = "./data/relmap.txt";
	private List<TagRule> rules;
	private Map<String, Relation> map;
	
	public RelationMapper() {
		rules = new ArrayList<>();
		map = new HashMap<>();
		try {
			init();
			populateMap();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void populateMap() {
		for (Relation r : Relation.values()) {
			map.put(r.toString(), r);
		}
	}

	private void init () throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(RELMAP)));
		String l = "";
		while ((l = br.readLine())!=null) {
			rules.add(new TagRule(l));
		}
		br.close();
	}
	
	public Relation getRelation (TaggedWord tw1, TaggedWord tw2, int i1, int i2) {
		String ctag = tw1.tag() + " " + tw2.tag();
		
		for (TagRule tr : rules) {
			if (tr.matches(ctag,i1,i2)) {
				return map.get(tr.getRelation());
			}
		}
		return map.get("Y");
	}
}
