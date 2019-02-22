package jthesia.game.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jogamp.opengl.GL2;

import jthesia.game.MouseState;
import jthesia.game.scenes.SongScene;
import jthesia.graphics.UnitsConverter;
import jthesia.midi.io.MidiIn;
import jthesia.midi.io.MidiKey;

public class PianoFrame implements InteractiveFrame {
	
	public interface KeyListener {
		public void keyUp(ScreenPianoKey key);
		public void keyDown(ScreenPianoKey key);
	}

	// 108  C8
	private static final int MAX_KEY = 108;
	// 21   A0
	private static final int MIN_KEY = 21;

	private MidiIn in;
	private KeyListener listener;
	
	private ArrayList<ScreenPianoKey> keys = new ArrayList<ScreenPianoKey>();
	
	private float keyWidth = 1.5f;
	private float middleC = 60 * keyWidth;
	private ScreenPianoKey activeKey;

	public PianoFrame(MidiIn in, KeyListener listener) {
		this.in = in;
		this.listener = listener;
		
		createKeys();
	}

	private void createKeys() {
		for(int i = MIN_KEY; i <= MAX_KEY; i++) {
			float x = i * keyWidth - middleC + 50;
			
			float y = UnitsConverter.INSTANCE.getHeightUnits() * SongScene.NOTES_FRAME_FACTOR;
			float h = UnitsConverter.INSTANCE.getHeightUnits() - y;
			
			keys.add(new ScreenPianoKey(i, x, y, keyWidth, h));
		}
	}
	
	public void render(GL2 gl) {
		for(ScreenPianoKey k : keys) {
			if(!GameNote.isBlack(k.getKey())) {
				k.render(gl);
			}
		}
		
		for(ScreenPianoKey k : keys) {
			if(GameNote.isBlack(k.getKey())) {
				k.render(gl);
			}
		}
	}
	
	public void setView(float keyWidth, int middleKey) {
		this.keyWidth = keyWidth;
		this.middleC = middleKey * keyWidth;
	}

	public void screenReshape() {
		keys.clear();
		
		createKeys();
	}

	public boolean update(final MouseState mouse, boolean select) {
		// Probabbly not the best way,
		// but will work for 88 keys
		// not a big deal
		for(ScreenPianoKey k : keys) {
			k.down(false);
		}
		
		updateMidiInput();
		
		return updateMouse(mouse);
		
	}

	private boolean updateMouse(final MouseState mouse) {
		if (mouse.down) {
			// We only have to process clicks,
			// because I don't use mouse in/out
			// for piano keys
			
			int keyIndex = Collections.binarySearch(keys, null, new Comparator<ScreenPianoKey>() {
				public int compare(ScreenPianoKey o1, ScreenPianoKey o2) {
					// Overshoot
					if (mouse.x > o1.getX() + o1.getWidth()) {
						return -1;
					}
					// Undershoot
					if(mouse.x < o1.getX()) {
						return 1;
					}
					return 0;
				}
			});
			
			if (keyIndex >= 0) {
				// Start with black keys, because they are on top
				for (int i = Math.max(keyIndex - 1, 0); i < Math.min(keyIndex + 2, keys.size()); i++) {
					if(GameNote.isBlack(keys.get(i).getKey())) {
						if(keys.get(i).update(mouse, false)) {
							updateKey(keys.get(i));
							return true;
						}
					}
				}
				// Then go to white keys
				for (int i = Math.max(keyIndex - 1, 0); i < Math.min(keyIndex + 2, keys.size()); i++) {
					if(!GameNote.isBlack(keys.get(i).getKey())) {
						if(keys.get(i).update(mouse, false)) {
							updateKey(keys.get(i));
							return true;
						}
					}
				}
			}
			
		}
		
		updateKey(null);
		
		return false;
	}

	private void updateKey(ScreenPianoKey key) {
		if (key != null && this.activeKey == null) {
			this.activeKey = key;
			keyDown(key);
		}
		
		// We can compare by instance
		if (key != null && key != this.activeKey) {
			this.activeKey = key;
			keyDown(key);
		}
		
		if (this.activeKey != null && key == null) {
			keyUp(this.activeKey);
			this.activeKey = null;
		}
	}

	private void keyUp(ScreenPianoKey key) {
		if (listener != null) {
			listener.keyUp(key);
		}
	}

	private void keyDown(ScreenPianoKey key) {
		if (listener != null) {
			listener.keyDown(key);
		}
	}

	protected void updateMidiInput() {
		for(final MidiKey k : in.getActiveNotes()) {
			if (!k.isClosed()) {
				int i = Collections.binarySearch(keys, null, new Comparator<ScreenPianoKey>() {
					
					public int compare(ScreenPianoKey o1, ScreenPianoKey o2) {
						return Integer.compare(o1.getKey(), k.getKey());
					}
					
				});
				
				if (i > 0) {
					keys.get(i).down(true);
				}
			}
		}
	}

}
