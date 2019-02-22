package jthesia.game.engine;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;

import jthesia.midi.MidiSong;

import javax.sound.midi.MidiDevice.Info;

public class GameState {
	
	private MidiSong song;

	public Info getMidiDeviceInput() {
		try {
			for(Info info : MidiSystem.getMidiDeviceInfo()) {
				System.out.println("IN: " + info.toString());
				if (info.toString().contains("hw:")) {
					return info;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public Info getMidiDeviceOutput() {
		try {
			for(Info info : MidiSystem.getMidiDeviceInfo()) {
				if (info.toString().contains("hw:")) {
					System.out.println("OUT: " + info.toString());
					try {
						MidiDevice device = MidiSystem.getMidiDevice(info);
						device.getReceiver();
						
						return info;
					}
					catch (Exception e) {
						
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public MidiSong getSong() {
		return this.song;
	}
	
	public void setSong(MidiSong song) {
		this.song = song;
	}

}
