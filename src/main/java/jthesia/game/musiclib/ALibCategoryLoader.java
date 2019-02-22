package jthesia.game.musiclib;

import java.util.List;

import jthesia.midi.MidiSong;
import jthesia.midi.Parser;

public abstract class ALibCategoryLoader {
	
	protected static final Parser parser = new Parser();
	
	protected String id;
	
	public ALibCategoryLoader(String id) {
		this.id = id;
	}

	public abstract List<LibraryItem> listCategories();

	public abstract LibraryItem back();

	public abstract List<LibraryItem> listSongs();

	public abstract MidiSong load(LibraryItem selected);

}
