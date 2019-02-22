package jthesia.game.musiclib;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jthesia.midi.MidiSong;

public class RootLibLoader extends ALibCategoryLoader {

	public static final String NAME = "root";

	public RootLibLoader(String id) {
		super(id);
	}

	@Override
	public List<LibraryItem> listCategories() {
		return Arrays.asList(
				new LibraryItem(RecentCategoryLoader.TITLE, RecentCategoryLoader.NAME + "://", true, true),
				new LibraryItem(DownloadsCategoryLoader.TITLE, DownloadsCategoryLoader.NAME + "://", true),
				new LibraryItem(LocalLibLoader.TITLE, LocalLibLoader.NAME + "://", true),
				new LibraryItem(BitMidiCategoryLoader.TITLE, BitMidiCategoryLoader.NAME + "://", true),
				new LibraryItem(RecordsCategoryLoader.TITLE, RecordsCategoryLoader.NAME + "://new", true));
	}

	@Override
	public LibraryItem back() {
		return MusicLibNavigator.ROOT;
	}

	@Override
	public List<LibraryItem> listSongs() {
		return Collections.emptyList();
	}

	@Override
	public MidiSong load(LibraryItem selected) {
		throw new UnsupportedOperationException();
	}

}
