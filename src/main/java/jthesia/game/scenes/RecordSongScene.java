package jthesia.game.scenes;

import java.io.IOException;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import com.jogamp.opengl.GLProfile;

import jthesia.game.MouseState;
import jthesia.game.components.PianoFrame;
import jthesia.game.components.RecordFrame;
import jthesia.game.components.ScreenPianoKey;
import jthesia.game.components.Scroller;
import jthesia.game.components.TextButton;
import jthesia.game.engine.GameState;
import jthesia.game.musiclib.RecordsCategoryLoader;
import jthesia.graphics.Graphics;
import jthesia.graphics.UnitsConverter;
import jthesia.midi.Parser;
import jthesia.midi.io.MidiInDevice;

public class RecordSongScene extends AScene {

	private final class RecordMidiMixer implements PianoFrame.KeyListener {
		@Override
		public void keyUp(ScreenPianoKey key) {
			try {
				ShortMessage msg = new ShortMessage(Parser.NOTE_OFF, 0, key.getKey(), 0);
				receiver.send(msg, getCurentMidiRecordTime());
				screenPianoProxy.keyUp(key);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void keyDown(ScreenPianoKey key) {
			startRecord();
			
			try {
				ShortMessage msg = new ShortMessage(Parser.NOTE_ON, 0, key.getKey(), 0);
				receiver.send(msg, getCurentMidiRecordTime());
				screenPianoProxy.keyDown(key);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static final float NOTES_FRAME_FACTOR = 0.75f;
	
	private PianoFrame pianoFrame;
	private TextButton backButton;
	
	private MidiInDevice midiIn;
	private Sequence record;
	private Sequencer sequencer;
	private Receiver receiver;

	private TextButton startButton;
	
	private long startedUS;

	private ScreenPianoProxy screenPianoProxy;
	
	public RecordSongScene() {
		this.midiIn = new MidiInDevice(new GameState().getMidiDeviceInput());
		
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			
			record = new Sequence(Sequence.PPQ, 24);
			sequencer.setSequence(record);
			sequencer.setTickPosition(0);
			
			receiver = sequencer.getReceiver();
			this.midiIn.setReceiver(receiver);
			
			startRecord();
		}
		catch (Exception e) { 
	        e.printStackTrace(); 
	    }
	}
	
	@Override
	public void load(GLProfile glProfile) {
		
		backButton = new TextButton("Back", 0.5f, 0.5f, 
				Graphics.COLOR_PALETTE[2], 0, 0, 15, 3) {
			
			@Override
			public void onClick() {
				stopRecord();
				goToNewScene(new MusicLibScene());
			}
			
		};
		
		screenPianoProxy = new ScreenPianoProxy(midiIn, null);
		pianoFrame = new PianoFrame(midiIn, new RecordMidiMixer());
		
		addFrame(new RecordFrame(screenPianoProxy));
		
		addFrame(pianoFrame);
		addFrame(backButton);
		
		startButton = new TextButton("Waiting", 0.5f, 0.5f, 
				new float[] {0.5f, 0.5f, 0.5f, 1}, 16, 0, 15, 3) {
			
			@Override
			public void onClick() {
				if(isRecording()) {
					stopRecord();
				}
			}
			
		};
		
		addFrame(startButton);
		
	}
	
	private boolean isRecording() {
		return sequencer.isRecording();
	}
	
	private void startRecord() {
		if (!sequencer.isRecording()) {
			startedUS = System.nanoTime() / 1000;
			
			sequencer.recordEnable(record.createTrack(), -1);
			sequencer.startRecording();
		}

		if(startButton != null) {
			startButton.setLable("Recording");
			startButton.setColor(new float[] {1, 0, 0, 1});
		}
		
		if (backButton != null) {
			backButton.setLable("Save");
		}
	}
	
	private void stopRecord() {
		this.midiIn.setReceiver(null);
		receiver.close();
		
		if (sequencer.isRecording()) {
			sequencer.stopRecording();

			try {
				Track track = record.getTracks()[0];
				
				trimTrack(track);
				
				System.out.println("Track has " + track.size() + " midi events");
				
				MidiSystem.write(record, 0, RecordsCategoryLoader.getNewRecordFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void trimTrack(Track track) {
		long offset = track.get(0).getTick();
		for(int i = 0; i < track.size(); i++) {
			MidiEvent evnt = track.get(i);
			evnt.setTick(evnt.getTick() - offset);
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		this.midiIn.close();
		if(this.sequencer.isOpen()) {
			this.sequencer.close();
		}
	}

	@Override
	protected SceneStateUpdate onUpdate(MouseState mouseState) {
		// Wait for first note to be pressed
		if(!isRecording() && !this.midiIn.getActiveNotes().isEmpty()) {
			startRecord();
		}
		
		return null;
	}

	private long getCurentMidiRecordTime() {
		return System.nanoTime() / 1000 - startedUS;
	}
}
