package jthesia.game.components;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;

import jthesia.game.MouseState;
import jthesia.game.scenes.TrackOptions;
import jthesia.graphics.Graphics;
import jthesia.midi.MidiSong;
import jthesia.midi.MidiTrack;
import jthesia.midi.io.MidiOut;

public class SelectionNotesFrame extends NotesFrame {
	
	private long sectionStart = -1;
	private long sectionEnd = -1;
	private TextButton sectionStartButton;
	private TextButton sectionEndButton;

	public SelectionNotesFrame(MidiSong song, MidiOut out) {
		super(song, createOptions(song), new EmptyMidiIn(), out);
	}

	private static List<TrackOptions> createOptions(MidiSong song) {
		List<TrackOptions> result = new ArrayList<>();
		for(MidiTrack t : song.getTracks()) {
			TrackOptions trackOptions = new TrackOptions(t);
			trackOptions.setWait(false);
			trackOptions.setPlay(true);
			result.add(trackOptions);
		}
		return result;
	}
	
	@Override
	public boolean update(MouseState mouse, boolean select) {
		super.update(mouse, select);
		
		if (mouse.click) {
			for(GameNote n : visibleSprites) {
				if (mouse.x > n.x && mouse.x < n.x + n.w 
						&& mouse.y > n.y && mouse.y < n.y + n.h) {
					
					n.setMatched(true);
					
					if (sectionStart == -1) {
						sectionStart = n.getNote().getTimeUS();
						this.sectionStartButton = new TextButton("^", 0.5f, 0.5f, 
								Graphics.COLOR_PALETTE[3], 0, 0, 2.5f, 2.5f);
					}
					else {
						sectionStart = Math.min(sectionStart, n.getNote().getTimeUS()) ;
					}
					
					if (sectionEnd == -1) {
						sectionEnd = n.getNote().getTimeUS() + n.getNote().getLengthUS();
						this.sectionEndButton = new TextButton("v", 0.5f, 0.5f, 
								Graphics.COLOR_PALETTE[3], 0, 0, 2.5f, 2.5f);
					}
					else {
						sectionEnd = Math.max(sectionEnd, 
								n.getNote().getTimeUS() + n.getNote().getLengthUS());
					}
					
				}
			}
		}
		
		int millisecondsToShow = 3 * 1000;
		long timeWindowUS = millisecondsToShow * 1000;
		float usHeight = this.height / timeWindowUS;
		
		long localTimeUS = (long) (getGameClock() * 1000.0);
		
		if (this.sectionStartButton != null) {
			long offsetUS = (localTimeUS - sectionStart);
			float y = this.height + offsetUS * usHeight - 2.5f;

			sectionStartButton.y = y;
			
			sectionStartButton.update(mouse, select);
		}
		
		if (this.sectionEndButton != null) {
			long offsetUS = (localTimeUS - sectionEnd);
			float y = this.height + offsetUS * usHeight - 2.5f;

			sectionEndButton.y = y;
			
			sectionEndButton.update(mouse, select);
		}
		
		return false;
	}
	
	@Override
	protected void drawPause(GL2 gl) {
		// Do nothing
	}
	
	@Override
	protected void updateMatchedNotes(List<GameNote> matched) {
		// Do nothing
	}

	@Override
	public void render(GL2 gl) {
		super.render(gl);
		
		if (this.sectionStartButton != null) {
			sectionStartButton.render(gl);
		}
		
		if (this.sectionEndButton != null) {
			sectionEndButton.render(gl);
		}
	}
}
