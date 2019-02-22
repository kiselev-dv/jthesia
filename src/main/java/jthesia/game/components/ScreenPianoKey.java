package jthesia.game.components;

import com.jogamp.opengl.GL2;

import jthesia.graphics.Graphics;

public class ScreenPianoKey extends ButtonSprite {

	private static final int C = 0;
	private static final int D = 2;
	private static final int E = 4;
	private static final int F = 5;
	private static final int G = 7;
	private static final int A = 9;
	private static final int B = 11;
	
	private static final float[] whiteKey = new float[] {0.9f, 0.9f, 0.9f, 1};
	private static final float[] blackKey = new float[] {0.3f, 0.3f, 0.3f, 1};
	
	private static final float[] whiteKeyDown = new float[] {0.7f, 0.7f, 0.7f, 1};
	private static final float[] blackKeyDown = new float[] {0.1f, 0.1f, 0.1f, 1};

	private int key;
	private boolean down = false;

	public ScreenPianoKey(int key, float x, float y, float w, float h) {
		super(x, y, w, h);
		
		if (GameNote.isBlack(key)) {
			this.h = h * 0.7f;
		}
		else {
			int keyInOctave = key % 12;
			if (keyInOctave == C) {
				this.w *= 1.6f;
			}
			else if (keyInOctave == D) {
				this.x -= this.w * 0.3f;
				this.w *= 1.6f;
			}
			else if (keyInOctave == E) {
				this.x -= this.w * 0.6f;
				this.w *= 1.5f;
			}
			// Same as C
			else if (keyInOctave == F) {
				this.w *= 1.6f;
			}
			else if (keyInOctave == G) {
				this.x -= this.w * 0.3f;
				this.w *= 1.6f;
			}
			else if (keyInOctave == A) {
				this.x -= this.w * 0.6f;
				this.w *= 1.7f;
			}
			else if (keyInOctave == B) {
				this.x -= this.w * 0.8f;
				this.w *= 1.7f;
			}
		}
		
		this.key = key;
	}
	
	@Override
	public void onMouseDown() {
		down = true;
	}

	public void render(GL2 gl) {
		if (down) {
			Graphics.setClor(GameNote.isBlack(key) ? blackKeyDown : whiteKeyDown);
		}
		else {
			Graphics.setClor(GameNote.isBlack(key) ? blackKey : whiteKey);
		}
		Graphics.fillRect(gl, x, y, w, h);
	}

	public int getKey() {
		return key;
	}
	
	public void down(boolean down) {
		this.down = down;
	}

	public float getX() {
		return x;
	}

	public float getWidth() {
		return w;
	}

}
