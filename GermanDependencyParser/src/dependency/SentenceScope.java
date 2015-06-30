package dependency;

import java.util.ArrayList;
import java.util.List;

public class SentenceScope {

	private List<Scope> scopes;
	
	public SentenceScope() {
		scopes = new ArrayList<Scope>();
	}
	
	public SentenceScope(String pennString) {
		this();
		calculateScopes(pennString);
	}

	
	private void calculateScopes(String pennString) {
		int i = 0;
		int w = 0;
		for (String s : pennString.split(" ")) {
			// if s starts with open parenthesis, increase level counter
			if (s.startsWith("(")) {
				i++;
				continue;
			}
			// if s ends with closing parenthesis (and possible whitespace)
			if (s.matches(".+\\)\\s*")) {
				// create new scope
				scopes.add(new Scope(w,i));
				// increase word counter
				w++;
				// find the last index of )
				// while there is a closing brace, decrease i
				for (int j = s.lastIndexOf(")"); j > 0 && s.charAt(j) == ')'; j--) {
					i--;	
				}
			}
		}
	}

	public boolean inScope(int depIndex, int govIndex) {
		Scope s1 = scopes.get(depIndex);
		Scope s2 = scopes.get(govIndex);
		
		return s2.getVisibilityLevel() <= s1.getVisibilityLevel();
	}

}
