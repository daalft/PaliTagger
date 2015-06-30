package dependency;

public class Scope {

	private int wordIndex, visibilityLevel;
	
	public Scope() {}
	
	public Scope(int wordIndex, int visibilityLevel) {
		this.wordIndex = wordIndex;
		this.visibilityLevel = visibilityLevel;
	}
	
	@Override
	public String toString() {
		return wordIndex + ":" + visibilityLevel;
	}

	public int getVisibilityLevel() {
		return visibilityLevel;
	}
}
