package external;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import dependency.Relation;
import dependency.RelationMapper;
import dependency.SentenceScope;
import dependency.SimpleDependency;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.Dependency;
import edu.stanford.nlp.trees.HeadFinder;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.international.negra.NegraPennLanguagePack;

public class StanfordGerman {

	/**
	 * Grammar file to use
	 */
	private final static String GRAMMAR = "edu/stanford/nlp/models/lexparser/germanPCFG.ser.gz";
	private RelationMapper rm;
	
	private List<TaggedWord> taggedWords;
	
	public StanfordGerman() {
		rm = new RelationMapper();
	}
	
	public List<SimpleDependency> parse (String sentence) {
		// Initialize output list
		List<SimpleDependency> relations = new ArrayList<>();
		
		// Lexicalized parser
		LexicalizedParser lp = LexicalizedParser.loadModel(GRAMMAR);
		TreebankLanguagePack tlp = lp.getOp().langpack();
		
		// Load tokenizer and tokenize sentence
		Tokenizer<? extends HasWord> toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(sentence));
		List<? extends HasWord> sentence2 = toke.tokenize();
		
		// Parse sentence
		Tree parse = lp.parse(sentence2);
		
		// Head finder for head percolation
		HeadFinder hf = new NegraPennLanguagePack().headFinder();
		
		// Indexing and percolation
		parse.indexLeaves(); // I think this does nothing?
		parse.percolateHeads(hf); // Important for dependencies
		
		// Tagged word as list
		ArrayList<TaggedWord> list = parse.taggedYield();
		// Set this field for later retrieval
		taggedWords = list;
		
		// Sentence scope for scope resolution
		SentenceScope scope = new SentenceScope(parse.pennString());
		
		for (Dependency<Label, Label, Object> d : parse.dependencies()) {
			// Get dependent and governor labels
			Label dep = d.dependent();
			Label gov = d.governor();
			
			// Get indexes of dep and gov
			int depIndex = indexOf(list, dep, gov, scope).getX();
			int govIndex = indexOf(list, dep, gov, scope).getY();
					
			// Get tagged words for dep and gov
			TaggedWord depTag = list.get(depIndex);
			TaggedWord govTag = list.get(govIndex);
			
			// Calculate type of relation
			Relation relation = rm.getRelation(depTag, govTag, depIndex, govIndex);
			
			if (relation != null) {
				// Create output dependency entry
				relations.add(new SimpleDependency(depTag, depIndex, govTag, govIndex, relation));
			} else {
				throw new IllegalStateException("Relation type must not be null!");
			}
		}
		return relations;
	}
	
	/**
	 * Finds the index of the dependent and governing words in the original sentence
	 * @param list sentence as words
	 * @param dep dependent
	 * @param gov governor
	 * @param scope sentence scope
	 * @return indices
	 */
	private Doublet indexOf (List<TaggedWord> list, Label dep, Label gov, SentenceScope scope) {
		// Initialize x and y index
		List<Integer> index = new ArrayList<Integer>();
		List<Integer> indey = new ArrayList<Integer>();
		
		// For each word
		for (int i = 0; i < list.size(); i++) {
			Label item = list.get(i);
			// Add to x list if dependent equals word
			if (item.value().equals(dep.value())) {
				index.add(i);
			}
			// Add to y list if governor equals word
			if (item.value().equals(gov.value())) {
				indey.add(i);
			}
		}
		// If x and y list both have size 1, return unambiguous result
		if (index.size() == 1 && indey.size() == 1) {
			return new Doublet(index.get(0), indey.get(0));
		}
		// If x list is unambiguous
		if (index.size() == 1) {
			int x = index.get(0);
			// Try elimination by scope violation
			for (int k = 0; k < indey.size(); k++) {
				int y = indey.get(k);
				if (!scope.inScope(x, y)) {
					// discard 
					indey.remove(k);
				}
			}
			// If elimination by scope violation was successful
			// 	return unambiguous result 
			if (indey.size() == 1) {
				return new Doublet(x, indey.get(0));
			} 
			//  else 
			// Find element from list y with minimum distance to x
			int l = minDistance(x, indey);
			return new Doublet(x, l);
		}
		// If y list is unambiguous
		if (indey.size() == 1) {
			int y = indey.get(0);
			// Elimination by scope violation
			for (int k = 0; k < index.size(); k++) {
				int x = index.get(k);
				if (!scope.inScope(x, y)) {
					// discard 
					index.remove(k);
				}
			}
			// Successful scope violation elimination
			if (index.size() == 1) {
				return new Doublet(index.get(0), y);
			} 
			// Minimum distance return
			int l = minDistance(y, index);
			return new Doublet(l, y);
		}
		// If both lists are ambiguous
		// Initialize intermediary output list
		List<Doublet> dlist = new ArrayList<Doublet>();
		for (int i = 0; i < index.size(); i++) {
			for (int j = 0; j < indey.size(); j++) {
				// Skip scope violating combinations
				if (!scope.inScope(i, j))
					continue;
				// Set x as constant
				int x = index.get(i);
				// Find closest by distance to x
				dlist.add(new Doublet(x, minDistance(x, indey)));
			}
		}
		// Return Doublet with minimum internal distance
		return minDistance(dlist);
	}
	
	/**
	 * Returns the Doublet from a list of Doublet 
	 * where the internal distance is minimal
	 * @param dlist list of Doublet
	 * @return Doublet
	 */
	private Doublet minDistance(List<Doublet> dlist) {
		int minDistance = Integer.MAX_VALUE;
		int index = -1;
		for (int i = 0; i < dlist.size(); i++) {
			Doublet d = dlist.get(i);
			int distance = Math.abs(d.x - d.y);
			if (distance < minDistance) {
				minDistance = distance;
				index = i;
			}
		}
		return dlist.get(index);
	}

	/**
	 * Returns the Integer from a list of Integer
	 * where the distance between x and Integer is minimal
	 * @param x x
	 * @param indey list of Integer
	 * @return Integer
	 */
	private int minDistance(int x, List<Integer> indey) {
		int index = -1;
		int minDistance = Integer.MAX_VALUE;
		for (int i = 0; i < indey.size(); i++) {
			int distance = Math.abs(x-indey.get(i));
			if (distance < minDistance) {
				minDistance = distance;
				index = i;
			}
		}
		return indey.get(index);
	}
	
	/**
	 * Returns the list of tagged words
	 * @return tagged words
	 */
	public List<TaggedWord> taggedYield() {
		return taggedWords;
	}

	/**
	 * Inner class Doublet
	 * @author s2daalft
	 *
	 */
	class Doublet {
		private int x,y;
		
		public Doublet(int i, int j) {
		x = i;
		y = j;
		}
		
		public int getX () {
			return x;
		}
		
		public int getY () {
			return y;
		}
	}
}
