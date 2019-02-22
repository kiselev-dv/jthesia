package jthesia.midi.io;

import java.util.List;

public interface MidiIn {

	public List<MidiKey> getActiveNotes();
	void close();


}