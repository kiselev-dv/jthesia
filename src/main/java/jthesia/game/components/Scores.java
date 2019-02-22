package jthesia.game.components;

public class Scores {
	
	private int notesHit = 0;
	private int errors = 0;

	private int notesTotal;
	private float speed;
	
	private int timeSong;
	private int timePlayed;
	
	private int mode;
	
	public int getScore() {
		return 100;
	}

	public int getNotesTotal() {
		return notesTotal;
	}

	public int getNotesHit() {
		return notesHit;
	}

	public int getErrors() {
		return errors;
	}

	public float getSpeed() {
		return speed;
	}

	public int getTimeSong() {
		return timeSong;
	}

	public int getTimePlayed() {
		return timePlayed;
	}

	public int getMode() {
		return mode;
	}

	public void notesHit(int hit) {
		this.notesHit += hit;
	}
	
	public void notesError(int errors) {
		this.errors += errors;
	}
	
}
