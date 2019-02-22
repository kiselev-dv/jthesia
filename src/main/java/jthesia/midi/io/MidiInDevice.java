package jthesia.midi.io;

import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import jthesia.midi.Parser;

public class MidiInDevice extends BaseMidiIn {

	private MidiDevice device;
	
	// Additional reciever
	private Receiver reciever;

	public MidiInDevice(MidiDevice.Info info) {
		
		if (info != null) {
			try {
				device = MidiSystem.getMidiDevice(info);
				List<Transmitter> transmitters = device.getTransmitters();
				
				for (int j = 0; j < transmitters.size(); j++) {

					transmitters.get(j).setReceiver(
							new MidiInputReceiver(device.getDeviceInfo().toString()));
				}
				
				Transmitter trans = device.getTransmitter();
				trans.setReceiver(new MidiInputReceiver(device.getDeviceInfo().toString()));
				
				device.open();
				
				System.out.println("DeviceMidiIn: " + device.getDeviceInfo() + " Was Opened");
				
			} catch (MidiUnavailableException e) {
			}
		}
		
	}
	
	public void setReceiver(Receiver reciever) {
		this.reciever = reciever;
	}

	private class MidiInputReceiver implements Receiver {
		//public String name;

		public MidiInputReceiver(String name) {
			//this.name = name;
		}

		public void send(MidiMessage msg, long timeStamp) {
			
			if (reciever != null) {
				System.out.println("sent: @" + timeStamp);
				reciever.send(msg, timeStamp);
			}
			
			if(msg instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) msg;
				
				if(sm.getCommand() == Parser.NOTE_ON) {
					
					int velocity = sm.getData2();
					
					/*
					some midi instruments use NOTE_ON with zero velocity
					as NOTE_OFF event, so if we got two NOTE_ON
					for the same key, without NOTE_OFF
					use second NOTE_ON as NOTE_OFF
					
					if velocity == 0, close previous note,
					and don't add into actively pressed keys
					*/

					if (velocity > 0) {
						addNoteOn(timeStamp, sm.getData1(), velocity);
					}
					else {
						closeNote(timeStamp, sm.getData1());
					}
					
				}
				else if(sm.getCommand() == Parser.NOTE_OFF) {
					if(closeNote(timeStamp, sm.getData1()) == null ) {
						System.err.println("Piano NOTE_OFF event recieved before NOTE_ON");
					}
				}
			}
			
			disposeOldKeys(timeStamp);
		}

		public void close() {
			
		}
	}

	@Override
	public void close() {
		if (this.device != null) {
			this.device.close();
		}
	}
	
}