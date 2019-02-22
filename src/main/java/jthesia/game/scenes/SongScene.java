package jthesia.game.scenes;

import java.util.List;

import com.jogamp.opengl.GLProfile;

import jthesia.dao.DAO;
import jthesia.game.MouseState;
import jthesia.game.components.NotesFrame;
import jthesia.game.components.PianoFrame;
import jthesia.game.components.ProgressBar;
import jthesia.game.components.TextButton;
import jthesia.game.musiclib.LibraryItem;
import jthesia.graphics.Graphics;
import jthesia.midi.MidiSong;
import jthesia.midi.io.MidiIn;
import jthesia.midi.io.MidiOut;
import jthesia.resource.Loader;

public class SongScene extends AScene {

	public static final float NOTES_FRAME_FACTOR = 0.75f;
	
	private NotesFrame notesFrame;
	private PianoFrame pianoFrame;
	private ProgressBar progressBar;

	private MidiIn midiIn;
	private MidiOut midiOut;

	private LibraryItem libItem;
	private MidiSong song;
	
	private TextButton backButton;

	
	public SongScene(MidiSong song, LibraryItem libItem, List<TrackOptions> options, MidiIn in,
			MidiOut out) {
		this.midiIn = in;
		this.midiOut = out;
		
		this.libItem = libItem;
		this.song = song;
		
		// This proxy listens for midi keys pressed on screen as well as for
		// midi keys from actual device
		
		ScreenPianoProxy screenPianoMidiInProxy = new ScreenPianoProxy(in, out);
		pianoFrame = new PianoFrame(in, screenPianoMidiInProxy);

		long songLengthMS = song.getMicrosecodsLength() / 1000;
		progressBar = new ProgressBar(songLengthMS, 12, 0, width - 12, 2);

		// instead of screenPianoMidiInProxy you can pass 'in' 
		// to brake connection between screen keyboard and 
		// and midi in for NotesFrame, so notes from screen
		// keyboard still will be played, but will be ignored
		// by NotesFrame
		notesFrame = new NotesFrame(song, options, screenPianoMidiInProxy, out);
		notesFrame.setProgressListener((timeUS) -> {
			if (timeUS > 0) {
				progressBar.setPosition(timeUS / 1000);
			}
		});
		
		progressBar.setMouseListener(clickPosition -> {
			notesFrame.pause();
			notesFrame.setPosition((long) (clickPosition) * 1000);
		});
		
		backButton = new TextButton("Back", 0.5f, 0.5f, Graphics.COLOR_PALETTE[2], 0, 0, 10, 2) {
			@Override
			public void onClick() {
				goToNewScene(new MusicLibScene());
			}
		};
		
		// Update recent in separate thread
		Loader.loadAssync(() -> {
			DAO.INSTANCE.recent(libItem, song.getHash());
			return null;
		});
		
		
		addFrame(notesFrame);
		addFrame(pianoFrame);
		addFrame(progressBar);
		addFrame(backButton);
	}

	@Override
	public void reshape(float width, float height) {
		super.reshape(width, height);
		this.notesFrame.setDimensions(0, 0, width, height * NOTES_FRAME_FACTOR);
		this.progressBar.setDimensions(12, 0, width - 12, 2);
	}

	public SceneStateUpdate onUpdate(MouseState mouseState) {
		if (notesFrame.isDone()) {
			goToNewScene(new ScoreScene(song, libItem, notesFrame.getScore()));
		}
		
		return null;
	}

	public void load(GLProfile glProfile) {
		notesFrame.start();
	}

	public void destroy() {
		this.midiIn.close();
		this.midiOut.close();
	}
}
