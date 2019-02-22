package jthesia.midi.io;

import java.io.IOException;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;

public class SoftwareSynthesizerMidiOut implements MidiOut {

	private int channel = 1;
	private MidiChannel[] mChannels;
	private Synthesizer midiSynth;
	private int instrument = 0;

	public SoftwareSynthesizerMidiOut() {
		try {
			midiSynth = MidiSystem.getSynthesizer();
			midiSynth.open();
			this.mChannels = midiSynth.getChannels();

			loadSoundBank();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadSoundBank() throws InvalidMidiDataException, IOException {
		System.out.println("Loading Piano soundbank");
		Soundbank soundbank = MidiSystem.getSoundbank(
				getClass().getResourceAsStream("/sf/Steinway+Grand+Piano+ER3A.sf2"));
		
		Instrument[] instr = soundbank.getInstruments();
		
		Patch patch = instr[instrument].getPatch();
		midiSynth.unloadInstruments(soundbank, new Patch[] {patch});
		
		midiSynth.loadInstrument(instr[instrument]);

		this.mChannels[channel].programChange(
				patch.getBank(), 
				patch.getProgram());
		
	}
	
	public void noteOn (int key, int velocity) {
		mChannels[channel].noteOn(key, velocity);
	};
	
	public void noteOff(int key, int velocity) {
		mChannels[channel].noteOff(key);
	}

	public void allOff() {
		mChannels[channel].allNotesOff();
		mChannels[channel].allSoundOff();
	};

	public void close() {
		this.midiSynth.close();
	}


}
