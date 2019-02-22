package jthesia.midi.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BaseMidiIn implements MidiIn {
	
	private Map<Integer, MidiKey> pressedKeys = new HashMap<Integer, MidiKey>();
	protected List<MidiKey> keys = new ArrayList<MidiKey>();

	// Quater of the second in microseconds
	private static final long DISPOSAL_TIMEOUT_US = 250 * 1000;

	private static final boolean DISPOSE_CLOSED_ONLY = true;
	
	/**
	 * timestamp of the last midi message
	 * used to dispose old keys
	 * */
	private long lastTimestamp;

	/**
	 * Difference between midi device clock and system.nanoTime clock
	 * */
	private long timestampDifference;
	
	protected MidiKey closeNote(long timeStamp, int k) {
		MidiKey key = pressedKeys.remove(k);
		if(key != null) {
			key.close(timeStamp);
		}
		return key;
	}

	protected void addNoteOn(long timeStamp, int key, int velocity) {
		lastTimestamp = timeStamp;
		timestampDifference = System.nanoTime() / 1000 - timeStamp;
		
		MidiKey midiKey = new MidiKey(timeStamp, key, velocity);
		keys.add(midiKey);
		
		MidiKey prev = pressedKeys.put(midiKey.getKey(), midiKey);
		if (prev != null) {
			// That might be an error, we have two
			// NOTE_ON events, without NOTE_OFF events
			prev.close(timeStamp);
		}
	}
	
	protected void disposeOldKeys(long now) {
		Iterator<MidiKey> iterator = keys.iterator();
		
		while(iterator.hasNext()) {
			MidiKey k = iterator.next();
			
			// Might be better to get rid
			// of unclosed keys as well
			// so the user have to play 
			// chords properly
			
			if (k.isClosed() || !DISPOSE_CLOSED_ONLY) {
				long diff = now - k.getTimeStamp();
				if (diff > DISPOSAL_TIMEOUT_US) {
					iterator.remove();
				}
			}
		}
	}

	@Override
	public List<MidiKey> getActiveNotes() {
		disposeOldKeys(System.nanoTime() / 1000 - timestampDifference);
		return new ArrayList<MidiKey>(keys);
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	

}
