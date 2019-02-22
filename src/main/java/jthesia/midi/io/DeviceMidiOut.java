package jthesia.midi.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;

import jthesia.midi.Parser;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class DeviceMidiOut implements MidiOut {

	private MidiDevice device;
	private Receiver receiver;

	private Set<Integer> activeNotes = new HashSet<Integer>();
	
	public DeviceMidiOut(Info info) {
		if (info != null) {
			try {
				device = MidiSystem.getMidiDevice(info);

				this.receiver = device.getReceiver();
				
				device.open();
				
				System.out.println("DeviceMidiOut: " + device.getDeviceInfo() + " Was Opened");
				
			} catch (MidiUnavailableException e) {
			}
		}
	}
	
	public void noteOn(int key, int velocity) {
		try {
			ShortMessage msg = new ShortMessage(Parser.NOTE_ON, 0, key, velocity);
			this.receiver.send(msg, System.nanoTime() / 1000);
			activeNotes.add(key);
		
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	public void noteOff(int key, int velocity) {
		try {
			ShortMessage msg = new ShortMessage(Parser.NOTE_OFF, 0, key, velocity);
			this.receiver.send(msg, System.nanoTime() / 1000);
			activeNotes.remove(key);
			
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	public void allOff() {
		for(Integer i : new ArrayList<Integer>(activeNotes)) {
			noteOff(i, 0);
		}
	}

	public void close() {
		this.device.close();
	}


}
