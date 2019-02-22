package jthesia.game.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.jogamp.opengl.GL2;

import jthesia.game.Clock;
import jthesia.game.MouseState;
import jthesia.game.scenes.TrackOptions;
import jthesia.graphics.Graphics;
import jthesia.midi.MidiNote;
import jthesia.midi.MidiSong;
import jthesia.midi.MidiTrack;
import jthesia.midi.io.MidiIn;
import jthesia.midi.io.MidiKey;
import jthesia.midi.io.MidiOut;

public class NotesFrame extends ButtonSprite implements InteractiveFrame {

	protected final List<GameNote> noteSprites = new ArrayList<GameNote>();
	protected final Set<GameNote> visibleSprites = new HashSet<GameNote>();
	
	private Set<GameNote> waitingForUser = new HashSet<GameNote>();

	private final Clock clock = new Clock();
	private float clockSpeed = 1.0f;
	private long gameTime = -2000;
	
//	private final float tempo;
	private MidiSong song;
	
	private final MidiOut midiOut;
	private final MidiIn midiIn;

	protected float width;
	protected float height;
	
	private float keyWidth = 1.5f;
	private float middleC = 60 * keyWidth;

	private boolean paused;
	
	private Scores scores = new Scores();
	
	public static interface ProgressListener {
		public void update(long curentTimeMks);
	}
	
	private ProgressListener progressListener = null;

	public NotesFrame(MidiSong song, List<TrackOptions> options, MidiIn in, MidiOut out) {
		super(0, 0, 0, 0);
		
		this.song = song;
		
		createNotes(song, options);
//		this.tempo = song.getTempo() / 6000;
		
		this.midiIn = in;
		this.midiOut = out;
	}
	
	public void setProgressListener(ProgressListener listener) {
		progressListener = listener;
	}
	
	public void setView(float keyWidth, int middleKey) {
		this.keyWidth = keyWidth;
		this.middleC = middleKey * keyWidth;
	}
	
	public void setDimensions(float x, float y, float width, float height) {
		this.width = width;
		this.height = height;
		
		super.w = width;
		super.h = height;
		super.x = x;
		super.y = y;
	}
	
	@Override
	public void onClick() {
		super.onClick();
		toggle();
	}
	
	private void toggle() {
		if (paused) {
			resume();
		}
		else {
			pause();
		}
	}

	public void resume() {
		if (waitingForUser.isEmpty()) {
			clock.resume();
		}
		paused = false;
	}

	public void pause() {
		clock.pause();
		paused = true;
		midiOut.allOff();
	}
	
	private void waitForNote() {
		clock.pause();
		midiOut.allOff();
	}
	
	protected float getGameClock() {
		// Game time in milliseconds, minus 2 seconds to get ready
		gameTime += clock.getTick() * clockSpeed;
		return gameTime;
	}
	
	public void start() {
		clock.start();
	}

	protected void createNotes(MidiSong song, List<TrackOptions> options) {
		
		int colorPaleteIndex = 0;
		for (TrackOptions trackO : options) {
			MidiTrack track = trackO.getTrack();
			
			for(MidiNote n : track.getNotes()) {
				float x = n.getKey() * keyWidth - middleC + 50;
				float widthUnits = keyWidth;
				
				float[] color = GameNote.isBlack(n.getKey()) ? 
						Graphics.COLOR_PALETTE[colorPaleteIndex] : 
							Graphics.COLOR_PALETTE_BRIGHT[colorPaleteIndex];
				
				GameNote note = new GameNote(n, x, 0, 
						widthUnits, 0, color);
				
				note.setMode(trackO.isPlay(), trackO.isWait(), trackO.isScore());
						
				noteSprites.add(note);
			}
			colorPaleteIndex = colorPaleteIndex + 1 % 5;
		}
		
		Collections.sort(noteSprites, new Comparator<GameNote>() {

			@Override
			public int compare(GameNote o1, GameNote o2) {
				return Long.compare(o1.getNote().getTimeUS(), o2.getNote().getTimeUS());
			}
			
		});
	}
	
	public void render(GL2 gl) {
		
		Graphics.setScissorBox(gl, 0, 0, width, height);
		
		for(GameNote s : visibleSprites) {
			s.render(gl);
		}
		
		if (paused) {
			drawPause(gl);
		}
		
		Graphics.resetScissorBox(gl);
		
		Graphics.setClor(new float[] {1, 1, 1, 1});

		long songLengthMS = song.getMicrosecodsLength() / 1000;
		int percent = Math.round(gameTime / (float)songLengthMS * 100.0f);
		
		String progressString = String.format("%d", percent) + "%";
		String durationString = String.format("%.3fs", gameTime / 1000.0f);

		Graphics.drawText(gl, "Time: " + durationString + " (" + progressString + ")", 0, 4);
	}

	protected void drawPause(GL2 gl) {
		Graphics.setClor(0, 0, 1, 0.5f);
		Graphics.fillRect(gl, 0, height / 3.0f, width, height / 3.0f);
	}

	public void screenReshape() {
		
	}

	public boolean update(MouseState mouse, boolean select) {
		
		super.update(mouse, select);
		
		// Three seconds (how many seconds we want to show on the screen)
		int millisecondsToShow = 3 * 1000;
		long timeWindowUS = millisecondsToShow * 1000;
		
		long localTimeUS = (long) (getGameClock() * 1000.0);
		
		if (progressListener != null) {
			progressListener.update(localTimeUS);
		}

		visibleSprites.clear();
		
		int startIndex = getClosestIndex(localTimeUS, noteSprites);
		while(startIndex > 0 && getEndTime(noteSprites.get(startIndex).getNote()) + 1000 > localTimeUS - timeWindowUS) {
			startIndex--;
		}
		
		for (int i = Math.max(0, startIndex - 8); i < noteSprites.size(); i++) {
			GameNote note = noteSprites.get(i);
			
			// Add some margin (assume note starts earlier and ends later)
			// to be sure that all notes hits note start / ends
			// later when we determine what's playing
			long startsUS = note.getNote().getTimeUS() - 1000;
			long endsUS = note.getNote().getTimeUS() + note.getNote().getLengthUS() + 1000;
			
			if (endsUS >= localTimeUS && endsUS <= localTimeUS + timeWindowUS) {
				visibleSprites.add(note);
			}
			else if(startsUS >= localTimeUS && startsUS <= localTimeUS + timeWindowUS) {
				visibleSprites.add(note);
			}
			
			visibleSprites.add(note);
			if(startsUS > localTimeUS + timeWindowUS + 1000) {
				break;
			}
		}

		// how many units of height are in one mkSecond to fit 3 seconds
		// of time into height of the frame
		float usHeight = this.height / timeWindowUS;
		
		for (GameNote sprite : visibleSprites) {
			MidiNote note = sprite.getNote();
			long noteOffsetUS = (localTimeUS - note.getTimeUS());
			sprite.h = note.getLengthUS() * usHeight - 0.2f;
			sprite.y = this.height + noteOffsetUS * usHeight - sprite.h + 0.1f;
		}
		
		Set<GameNote> notesToSendToMidi = new HashSet<GameNote>();
		Set<GameNote> notesToMatch = new HashSet<GameNote>();
		
		for(GameNote s : visibleSprites) {
			long noteStarts = s.getNote().getTimeUS();
			long noteEnds = noteStarts + s.getNote().getLengthUS();
			
			if (noteEnds < localTimeUS) {
				midiOut.noteOff(s.getNote().getKey(), s.getNote().getVelocity());
			}
			
			if (noteStarts < localTimeUS) {
				if (s.isMatch() && !s.isMatched()) {
					notesToMatch.add(s);
				}

				if (s.isPlay() && !s.isSent()) {
					s.setSent(true);
					// s.setMatched(true);
					notesToSendToMidi.add(s);
				}
			}
		}
		
		List<GameNote> matched = new ArrayList<GameNote>();
		List<MidiKey> activeNotes = midiIn.getActiveNotes();
		waitingForUser.clear();		
		
		// Ignore matched note between frames,
		// other case if user have something
		// like the series of the same note
		// playing one after another
		// they can just hold button down and
		// don't care about pressing them on time at all
		
		for (GameNote waitNote : notesToMatch) {
			if(matchAndRemoveMatched(waitNote, activeNotes)) {
				matched.add(waitNote);
			}
			else if(waitNote.isWaitForUser()) {
				waitingForUser.add(waitNote);
			}
		}
		
		if(waitingForUser.isEmpty()) {
			if(!paused) {
				clock.resume();
			}
			
			updateMatchedNotes(matched);
			
			scoreMatched(matched, activeNotes);
			
			sendNotes(notesToSendToMidi);
		}
		else {
			waitForNote();
		}
		
		return false;
	}

	private void sendNotes(Set<GameNote> notesToSendToMidi) {
		for(GameNote n : notesToSendToMidi) {
			midiOut.noteOn(n.getNote().getKey(), n.getNote().getVelocity());
		}
	}

	protected void updateMatchedNotes(List<GameNote> matched) {
		for(GameNote gn : matched) {
			gn.setMatched(true);
		}
	}

	protected static long getEndTime(MidiNote note) {
		return note.getTimeUS() + note.getLengthUS();
	}

	protected static int getClosestIndex(long localTimeUS, List<GameNote> noteSprites) {
		if(localTimeUS < noteSprites.get(0).getNote().getTimeUS()) {
            return 0;
        }
        if(localTimeUS > noteSprites.get(noteSprites.size() - 1).getNote().getTimeUS()) {
            return noteSprites.size() - 1;
        }

        int lo = 0;
        int hi = noteSprites.size() - 1;

        while (lo <= hi) {
            int mid = (hi + lo) / 2;

            if (localTimeUS < noteSprites.get(mid).getNote().getTimeUS()) {
                hi = mid - 1;
            } else if (localTimeUS > noteSprites.get(mid).getNote().getTimeUS()) {
                lo = mid + 1;
            } else {
                return mid;
            }
        }
        // lo == hi + 1 we want to get closest from the bottom
        return Math.min(lo, hi);
	}

	@SuppressWarnings("unused")
	private void printDebug(List<MidiKey> activeNotes) {
		String waiting = "";
		for(GameNote n : waitingForUser) {
			waiting += ", " + n.getNote().getKey();
		}
		
		String pressed = "";
		for(MidiKey n : activeNotes) {
			pressed += ", " + n.getKey();
		}
	}

	private void scoreMatched(List<GameNote> matched, List<MidiKey> activeNotes) {
		this.scores.notesHit(matched.size());
	}

	private boolean matchAndRemoveMatched(GameNote waitNote, List<MidiKey> activeNotes) {
		Iterator<MidiKey> iterator = activeNotes.iterator();
		
		while(iterator.hasNext()) {
			MidiKey pressedNote = iterator.next();
			if (pressedNote.getKey() == waitNote.getNote().getKey()) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	public boolean isDone() {
		long localTimeUS = (long) (getGameClock() * 1000.0);
		
		// wait a half of a seconds after all notes are played
		return localTimeUS > song.getMicrosecodsLength() + 500 * 1000;
	}

	public Scores getScore() {
		return scores;
	}

	public void setPosition(long timeUS) {
		for(GameNote n : noteSprites) {
			n.setMatched(false);
			n.setSent(false);
		}
		gameTime = timeUS / 1000;
	}

}
