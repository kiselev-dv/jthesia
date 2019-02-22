package jthesia.game.components;

import com.jogamp.opengl.GL2;

import jthesia.game.MouseState;
import jthesia.game.musiclib.LibraryItem;
import jthesia.midi.MidiSong;

public class SongStatsFrame implements InteractiveFrame {

	@Override
	public void render(GL2 gl) {
		
	}

	@Override
	public void screenReshape() {
		
	}

	@Override
	public boolean update(MouseState mouse, boolean select) {
		return false;
	}

	public void selectSong(LibraryItem selected, MidiSong song) {
		
	}

}
