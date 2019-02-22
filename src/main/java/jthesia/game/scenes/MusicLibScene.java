package jthesia.game.scenes;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;

import jthesia.game.MouseState;
import jthesia.game.components.LibNavigatorFrame;
import jthesia.game.components.LibNavigatorFrame.SongSelectHandler;
import jthesia.game.components.SongStatsFrame;
import jthesia.game.components.TextButton;
import jthesia.game.musiclib.LibraryItem;
import jthesia.game.musiclib.MusicLibNavigator;
import jthesia.graphics.Graphics;
import jthesia.midi.MidiSong;
import jthesia.resource.Loader;

public class MusicLibScene extends AScene implements SongSelectHandler {
	
	private SongStatsFrame songStatsFrame = new SongStatsFrame();
	
	private MidiSong song;
	private LibraryItem libSong;
	private TextButton playButton;
	
	@Override
	public void load(GLProfile glProfile) {

		addFrame(songStatsFrame);
		addFrame(new LibNavigatorFrame(this));
		
	}
	
	@Override
	public SceneStateUpdate onUpdate(MouseState mouseState) {
		if(playButton != null) {
			playButton.update(mouseState, false);
		}
		return null;
	}

	@Override
	public void selectSong(final LibraryItem selected) {
		
		playButton = null;
		
		if (selected != null) {
			
			if("records://new".equals(selected.getId())) {
				goToNewScene(new RecordSongScene());
				return;
			}
			
			Loader.loadAssync(() -> {
				return MusicLibNavigator.INSTANCE.getSequence(selected);
			},
					(loaded) -> {
						setSong(loaded, selected);
					} 
			);
		}
		
	}

	public MidiSong getSong() {
		return song;
	}

	public void setSong(MidiSong song, LibraryItem libItem) {
		this.song = song;
		this.libSong = libItem;

		songStatsFrame.selectSong(libItem, song);
		
		float top = 5.5f;
		float w = 15f;
		float h = 2.5f;
		
		playButton = new TextButton("Play", 0.5f, 0.5f, 
				Graphics.COLOR_PALETTE[3], 32.5f, top, w, h) {
			
			@Override
			public void onClick() {
				goToNewScene(new SongTracksSetupScene(song, libSong));
			}
			
		};
	}
	
	@Override
	public void render(GL2 gl) {
		super.render(gl);
		if(playButton != null) {
			playButton.render(gl);
		}
	}

}
