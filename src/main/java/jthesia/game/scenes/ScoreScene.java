package jthesia.game.scenes;

import java.util.concurrent.Callable;

import com.jogamp.opengl.GLProfile;

import jthesia.dao.DAO;
import jthesia.dao.SongPlayed;
import jthesia.game.components.Scores;
import jthesia.game.components.TextButton;
import jthesia.game.components.TextLabel;
import jthesia.game.musiclib.LibraryItem;
import jthesia.graphics.Graphics;
import jthesia.graphics.UnitsConverter;
import jthesia.midi.MidiSong;
import jthesia.resource.Loader;

public class ScoreScene extends AScene {
	
	private final Scores score;
	private final MidiSong song;
	private LibraryItem libItem;

	public ScoreScene(MidiSong song, LibraryItem libItem, Scores score) {
		this.libItem = libItem;
		this.song = song;
		this.score = score;
	}

	@Override
	public void load(GLProfile glProfile) {
		float screenH = UnitsConverter.INSTANCE.getHeightUnits();


		addFrame(new TextLabel(song.getName(), 5, 5, 25, 5));
		
		addFrame(new TextLabel("Notes hit: " + score.getNotesHit(), 10, 10, 25, 5));

		// Shouldn't be null if song selected via UI
		// but I want to be able to play song from eg. CLI
		// and in this case libItem might be null
		if (libItem != null) {
			Loader.loadAssync(() -> {
				DAO.INSTANCE.save(new SongPlayed(libItem, score.getNotesHit()));
				return null;
			});
		}
		
		addFrame(new TextButton("Open Song Library", 
				0.5f, 0.5f, 
				Graphics.COLOR_PALETTE[3], 5, screenH - 5.0f, 30, 2.5f) {
			
			@Override
			public void onClick() {
				goToNewScene(new MusicLibScene());
			}
			
		});
		
	}
	

}
