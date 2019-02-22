package jthesia.game.components;

import java.util.Collections;
import java.util.List;

import jthesia.midi.io.MidiIn;
import jthesia.midi.io.MidiKey;

public class EmptyMidiIn implements MidiIn {

	@Override
	public List<MidiKey> getActiveNotes() {
		return Collections.emptyList();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
