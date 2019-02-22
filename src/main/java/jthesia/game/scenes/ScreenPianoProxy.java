package jthesia.game.scenes;

import java.util.ArrayList;
import java.util.List;

import jthesia.game.components.PianoFrame;
import jthesia.game.components.ScreenPianoKey;
import jthesia.midi.io.BaseMidiIn;
import jthesia.midi.io.MidiIn;
import jthesia.midi.io.MidiKey;
import jthesia.midi.io.MidiOut;

/**
 * This class acts like MidiIn returning pressed keys
 * from <code>midiIn</code> and adds notes clicked
 * on virtual keyboard
 * */
public final class ScreenPianoProxy extends BaseMidiIn implements PianoFrame.KeyListener {
	private MidiIn midiIn;
	private MidiOut midiOut;
	
	private long timeOffset = 0;
	
	public ScreenPianoProxy(MidiIn in, MidiOut out) {
		this.midiIn = in;
		this.midiOut = out;
	}

	public void keyDown(ScreenPianoKey key) {
		if (timeOffset == 0) {
			timeOffset = System.nanoTime() / 1000;
		}
		
		if (midiOut != null) {
			midiOut.noteOn(key.getKey(), 100);
		}
		addNoteOn(getMidiTimestamp(), key.getKey(), 100);
	}


	public void keyUp(ScreenPianoKey key) {
		if(midiOut != null) {
			midiOut.noteOff(key.getKey(), 0);
		}

		long ts = getMidiTimestamp();

		closeNote(ts, key.getKey());
		disposeOldKeys(ts);
	}

	// We don't actually use real midi timestamps here
	// we need these timestamps, only for sorting
	// and disposal of closed notes
	private long getMidiTimestamp() {
		return System.nanoTime() / 1000 - timeOffset;
	}

	@Override
	public List<MidiKey> getActiveNotes() {
		disposeOldKeys(getMidiTimestamp());
		// M.B. Change to hash set later
		List<MidiKey> keys = new ArrayList<>(midiIn.getActiveNotes());
		
		// These are notes from virtual keyboard, for desctop
		// there will be only one pressed key, so this array
		// is smaller and has lower priority
		List<MidiKey> screenPressedKeys = super.getActiveNotes();
		
		// Add only those screen notes, which are not open
		// on the midiIn (which is physical device)
		
		// That might be complete overkill, but if it
		// wount slows down app, it's wort to filter user input
		for (MidiKey screenK : screenPressedKeys) {
			boolean foundOnDevice = false;
			for(MidiKey deviceK : keys) {
				// we found active key on device
				if(deviceK.getKey() == screenK.getKey() && !deviceK.isClosed()) {
					foundOnDevice = true;
					break;
				}
			}
			if (!foundOnDevice) {
				keys.add(screenK);
			}
		}
		
		return keys;
	}

	@Override
	public void close() {
		midiIn.close();
	}
}