package server.model.players.content;

/**
 * Made on 7-jan-2011
 * 
 * @author Satan666
 */
public class InGameHighscores implements Comparable<Object> {

	String name = "";
	public static String type = "";
	int score = 0;

	public InGameHighscores(String name, int score) {
		this.name = name;
		this.score = score;
	}

	public int compareTo(Object o1) {
		if (this.score == ((InGameHighscores) o1).score) {
			return 0;
		} else if ((this.score) > ((InGameHighscores) o1).score) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return String.format("%s @or1@- %s: @gre@%d", name, type, score);
	}
}