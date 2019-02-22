package jthesia.game.components;

import java.util.Arrays;

import com.jogamp.opengl.GL2;

import jthesia.graphics.Graphics;
import jthesia.midi.MidiNote;

public class GameNote extends Sprite {
	
	private float[] color;
	private MidiNote note;
	
	private boolean play = true;
	private boolean waitForUser = true;
	private boolean scoreNote = true;
	
	private boolean matched = false;
	private boolean sent = false;
	
	public static final int[] BLACK_NOTES = new int[] {1, 3, 6, 8, 10};
	
	public static int getOctave(int key) {
		return (key / 12) - 1;
	}
	
	public static boolean isBlack(int key) {
		return Arrays.binarySearch(BLACK_NOTES, key % 12) >= 0; 
	}
	
	public GameNote(MidiNote n, float x, float y, float w, float h, float[] color) {
		super(x, y, w, h);
		
		this.color = color;
		this.note = n;
	}
	
	public void setMode(boolean play, boolean wait, boolean score) {
		this.play = play;
		this.waitForUser = wait;
		this.scoreNote = score;
	}
	
	public boolean isPlay() {
		return play;
	}
	
	public boolean isWaitForUser() {
		return waitForUser;
	}

	public boolean isMatch() {
		return scoreNote;
	}

	public void render(GL2 gl) {
		Graphics.setClor(color[0], color[1], color[2], color[3]);
		if (this.matched) {
			// Replace with HSL saturation boost later
			Graphics.setClor(1, 1, 1, 1);
		}
		Graphics.fillRect(gl, x, y, w, h);

	}

	public MidiNote getNote() {
		return note;
	}
	
	public float getHeight() {
		return h;
	}

	public void setMatched(boolean b) {
		this.matched = b;
	}

	public boolean isMatched() {
		return matched;
	}

	public void setSent(boolean b) {
		this.sent = b;
	}

	public boolean isSent() {
		return sent;
	}

	public void setColor(float[] fs) {
		this.color = fs;
	}	

}
