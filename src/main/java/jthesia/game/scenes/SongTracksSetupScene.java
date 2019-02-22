package jthesia.game.scenes;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GLProfile;

import jthesia.game.components.TextButton;
import jthesia.game.components.TextLabel;
import jthesia.game.engine.GameState;
import jthesia.game.musiclib.LibraryItem;
import jthesia.graphics.Graphics;
import jthesia.graphics.UnitsConverter;
import jthesia.midi.MidiSong;
import jthesia.midi.MidiTrack;
import jthesia.midi.io.MidiInDevice;
import jthesia.midi.io.SoftwareSynthesizerMidiOut;

public class SongTracksSetupScene extends AScene {
	
	private MidiSong sequence;

	private TrackOptions leftHand;
	private TrackOptions rightHand;

	private LibraryItem selectedSong;

	public SongTracksSetupScene(MidiSong sequence, LibraryItem libSong) {
		this.sequence = sequence;
		this.selectedSong = libSong;
		
		assignTracks();
	}

	private void assignTracks() {
		List<MidiTrack> tracks = this.sequence.getTracks();
		for(MidiTrack t : tracks) {
			if (t.getClef() == MidiTrack.BASS_CLEF) {
				leftHand = new TrackOptions(t);
				leftHand.setColor(Graphics.COLOR_PALETTE[0]);
			}
			else if (t.getClef() == MidiTrack.TREBLE_CLEF) {
				rightHand = new TrackOptions(t);
				rightHand.setColor(Graphics.COLOR_PALETTE[1]);
			}
		}
	}

	private void setListen(TrackOptions hand) {
		if (hand != null) {
			hand.setPlay(true);
			hand.setScore(false);
			hand.setWait(false);
		}
	}
	
	private void setNotListen(TrackOptions hand) {
		if (hand != null) {
			hand.setPlay(false);
			hand.setScore(false);
			hand.setWait(false);
		}
	}
	
	private void setLearn(TrackOptions hand) {
		if (hand != null) {
			hand.setPlay(true);
			hand.setScore(true);
			hand.setWait(true);
		}
	}
	
	private void setChalenge(TrackOptions hand) {
		if (hand != null) {
			hand.setPlay(true);
			hand.setScore(true);
			hand.setWait(false);
		}
	}
	
	@Override
	public void load(GLProfile glProfile) {
		
		float topMargin = 2.5f;
		float leftMargin = 2.5f;
		
		addFrame(new TextLabel(getName(), leftMargin, topMargin, 50, 5));
		addFrame(new TextLabel("tempo: " + sequence.getBPM() + "bpm", leftMargin , topMargin + 2.5f, 35, 5));
		
		String timeSignature = sequence.getTimeSignature()[0] + "/" + sequence.getTimeSignature()[1];
		addFrame(new TextLabel("Time signature: " + timeSignature, leftMargin , topMargin + 5.0f, 35, 5));
		
		float column = (100.0f - leftMargin * 2) / 3.0f;
		
		float typeSelectorOffset = 8.5f;
		
		addFrame(new TextLabel("Listen", 	leftMargin, 						topMargin + typeSelectorOffset, column, 5));
		addFrame(new TextLabel("Learn", 	leftMargin * 2.0f + column, 		topMargin + typeSelectorOffset, column, 5));
		addFrame(new TextLabel("Challenge", leftMargin * 2.0f + column * 2.0f,	topMargin + typeSelectorOffset, column, 5));

		float buttonsOffset = 3.0f + typeSelectorOffset;
		float lrOffset = 4.0f;
		// Listen
		addFrame(new TextButton("Both Hands", 0.5f, 0.5f, 
				Graphics.COLOR_PALETTE[3], 
				leftMargin, 
				topMargin + buttonsOffset, 
				column - leftMargin, 
				2.5f) {
					@Override
					public void onClick() {
						setListen(leftHand);
						setListen(rightHand);
						play();
					}

		});
		
		if (leftHand != null && rightHand != null) {
			
			addFrame(new TextButton("Left", 0.5f, 0.5f, 
					Graphics.COLOR_PALETTE[0], 
					leftMargin, 
					topMargin + buttonsOffset + lrOffset, 
					(column - leftMargin) / 2.0f - 1.0f, 
					2.5f) {
						@Override
						public void onClick() {
							setListen(leftHand);
							setNotListen(rightHand);
							play();
						}
			});
			
			addFrame(new TextButton("Right", 0.5f, 0.5f, 
					Graphics.COLOR_PALETTE[1], 
					leftMargin + (column - leftMargin) / 2.0f + 1.0f, 
					topMargin + buttonsOffset + lrOffset, 
					(column - leftMargin) / 2.0f - 1.0f, 
					2.5f) {
						@Override
						public void onClick() {
							setNotListen(leftHand);
							setListen(rightHand);
							play();
						}
			});
			
		}
		
		// Learn
		addFrame(new TextButton("Both Hands", 0.5f, 0.5f,	
				Graphics.COLOR_PALETTE[3], 
				leftMargin + column + leftMargin, 
				topMargin + buttonsOffset, 
				column - leftMargin * 2.0f, 
				2.5f) {
					@Override
					public void onClick() {
						setLearn(leftHand);
						setLearn(rightHand);
						play();
					}
		});
		
		if (leftHand != null && rightHand != null) {
			
			addFrame(new TextButton("Left", 0.5f, 0.5f, 
					Graphics.COLOR_PALETTE[0], 
					leftMargin + column + leftMargin, 
					topMargin + buttonsOffset + lrOffset, 
					(column - leftMargin * 2.0f) / 2.0f - 1.0f, 
					2.5f) {
						@Override
						public void onClick() {
							setLearn(leftHand);
							setListen(rightHand);
							play();
						}
			});
			
			addFrame(new TextButton("Right", 0.5f, 0.5f, 
					Graphics.COLOR_PALETTE[1], 
					leftMargin + column + leftMargin + (column - leftMargin * 2.0f) / 2.0f + 1.0f, 
					topMargin + buttonsOffset + lrOffset, 
					(column - leftMargin * 2.0f) / 2.0f - 1.0f, 
					2.5f) {
						@Override
						public void onClick() {
							setListen(leftHand);
							setLearn(rightHand);
							play();
						}
			});
			
		}
		
		// Challenge
		addFrame(new TextButton("Both Hands", 0.5f, 0.5f, 
				Graphics.COLOR_PALETTE[3], 
				leftMargin + column * 2.0f + leftMargin,
				topMargin + buttonsOffset, 
				column - leftMargin, 
				2.5f) {
					@Override
					public void onClick() {
						setChalenge(leftHand);
						setChalenge(rightHand);
						play();
					}
		});
		
		if (leftHand != null && rightHand != null) {
			
			addFrame(new TextButton("Left", 0.5f, 0.5f, 
					Graphics.COLOR_PALETTE[0], 
					leftMargin + column * 2.0f + leftMargin, 
					topMargin + buttonsOffset + lrOffset, 
					(column - leftMargin) / 2.0f - 1.0f, 
					2.5f) {
						@Override
						public void onClick() {
							setChalenge(leftHand);
							setListen(rightHand);
							play();
						}
			});
			
			addFrame(new TextButton("Right", 0.5f, 0.5f, 
					Graphics.COLOR_PALETTE[1], 
					leftMargin + column * 2.0f + leftMargin + (column - leftMargin) / 2.0f + 1.0f, 
					topMargin + buttonsOffset + lrOffset, 
					(column - leftMargin) / 2.0f - 1.0f, 
					2.5f) {
						@Override
						public void onClick() {
							setListen(leftHand);
							setChalenge(rightHand);
							play();
						}
			});
		}
		
		addFrame(new TextButton("Add Section", 0.5f, 0.5f, 
				Graphics.COLOR_PALETTE[0], 
				leftMargin, 
				topMargin + buttonsOffset + lrOffset + 3.5f, 
				(column - leftMargin) / 2.0f - 1.0f, 
				2.5f) {
					@Override
					public void onClick() {
						goToNewScene(new SectionScene(
								sequence, 
								selectedSong, 
								new SoftwareSynthesizerMidiOut()));
					}
		});
		
		float screenH = UnitsConverter.INSTANCE.getHeightUnits();
		addFrame(new TextButton("Back", 0.5f, 0.5f, 
				Graphics.COLOR_PALETTE[4], 
				leftMargin, screenH - 5f, 25, 2.5f) {
			@Override
			public void onClick() {
				nextScene = new MusicLibScene();
			}
		});
	}
	
	private String getName() {
		if (sequence.getName() != null) {
			return sequence.getName();
		}
		if(selectedSong != null) {
			return selectedSong.getTitle();
		}
		
		return "";
	}

	private void play() {
		List<TrackOptions> options = new ArrayList<>(2);
		if(leftHand != null) {
			options.add(leftHand);
		}
		
		if(rightHand != null) {
			options.add(rightHand);
		}
		
		SongScene songScene = new SongScene(sequence, selectedSong, 
				options, 
				new MidiInDevice(new GameState().getMidiDeviceInput()), 
				new SoftwareSynthesizerMidiOut());
		
		goToNewScene(songScene);
	}

}
