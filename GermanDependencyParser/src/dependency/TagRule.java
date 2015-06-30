package dependency;

import java.util.ArrayList;
import java.util.List;

public class TagRule {

	private List<String> regex;
	private boolean fromLeft;
	private String relation;
	
	public TagRule() {
		regex = new ArrayList<>();
	}
	
	public TagRule(String l) {
		this();
		String[] sp = l.split("\t");
		fromLeft = sp[1].equals("l");
		relation = sp[0];
		for (String s : sp[2].split(";"))
			regex.add(s);
	}

	public boolean matches(String ctag, int i1, int i2) {
		for (String reg : regex) {
			if (ctag.matches(reg)) {
				if (fromLeft && (i1 < i2))
					return true;
				if (!fromLeft && (i1 > i2))
					return true;
			}
		}
		return false;
	}

	public String getRelation() {
		return relation;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(relation);
		sb.append(":");
		for (String s : regex) 
			sb.append(s).append(";");
		sb.append((fromLeft?"->":"<-"));
		return sb.toString();
	}
}
