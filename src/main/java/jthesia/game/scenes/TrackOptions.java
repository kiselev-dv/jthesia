package jthesia.game.scenes;

import jthesia.midi.MidiTrack;

public class TrackOptions {
	private final MidiTrack track;
	private float[] color;

	private boolean play;
	private boolean wait;
	private boolean score;

	public TrackOptions(MidiTrack t) {
		this.track = t;
	}

	public boolean isPlay() {
		return play;
	}

	public boolean isWait() {
		return wait;
	}

	public float[] getColor() {
		return color;
	}

	public MidiTrack getTrack() {
		return track;
	}

	public void setPlay(boolean play) {
		this.play = play;
	}

	public void setWait(boolean wait) {
		this.wait = wait;
	}

	public void setColor(float[] color) {
		this.color = color;
	}

	public boolean isScore() {
		return score;
	}

	public void setScore(boolean score) {
		this.score = score;
	}
	
}
