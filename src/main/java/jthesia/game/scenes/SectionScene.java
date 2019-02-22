package jthesia.game.scenes;

import com.jogamp.opengl.GLProfile;

import jthesia.game.components.EmptyMidiIn;
import jthesia.game.components.NotesFrame;
import jthesia.game.components.PianoFrame;
import jthesia.game.components.Scroller;
import jthesia.game.components.SelectionNotesFrame;
import jthesia.game.musiclib.LibraryItem;
import jthesia.midi.MidiSong;
import jthesia.midi.io.MidiOut;

public class SectionScene extends AScene {

	private MidiSong song;
	private LibraryItem libItem;
	private MidiOut out;
	private PianoFrame pianoFrame;
	private Scroller scroller;
	private NotesFrame notesFrame;

	public SectionScene(MidiSong song, LibraryItem libItem, MidiOut out) {
		this.song = song;
		this.libItem = libItem;
		this.out = out;

		pianoFrame = new PianoFrame(new EmptyMidiIn(), null);
		
		float songLengthMS = song.getMicrosecodsLength() / 1000;
		scroller = new Scroller(songLengthMS, 12, 0, width - 12, 2);
		
		notesFrame = new SelectionNotesFrame(song, out);
		notesFrame.setProgressListener((timeUS) -> {
			if (timeUS > 0) {
				scroller.setWindow(timeUS / 1000, 3000);
			}
		});
		
		scroller.setMouseListener(clickPosition -> {
			notesFrame.pause();
			notesFrame.setPosition((long) (clickPosition) * 1000);
		});
		
		addFrame(notesFrame);
		addFrame(scroller);
		addFrame(pianoFrame);
	}
	
	@Override
	public void load(GLProfile glProfile) {
		
	}
	
	@Override
	public void reshape(float width, float height) {
		super.reshape(width, height);
		this.notesFrame.setDimensions(0, 0, width, height * SongScene.NOTES_FRAME_FACTOR);
		this.scroller.setDimensions(12, 0, width - 12, 2);
	}
	

}
