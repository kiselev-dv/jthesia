package jthesia.midi;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class TestPlayer {
	
	public TestPlayer(Sequence s) {
		try {
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(s);
			sequencer.start();
		}
		catch (Exception e) {
			
		}
	}

}
