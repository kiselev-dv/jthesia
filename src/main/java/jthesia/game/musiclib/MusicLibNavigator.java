package jthesia.game.musiclib;

import java.util.ArrayList;
import java.util.List;

import jthesia.dao.SongPlayed;
import jthesia.midi.MidiSong;

public class MusicLibNavigator {

	public static final LibraryItem ROOT = new LibraryItem("", "root://", true, true);

	public static final MusicLibNavigator INSTANCE = new MusicLibNavigator();
	
	private LibraryItem curent;
	
	private MusicLibNavigator() {
		curent = ROOT;
	}
	
	public List<LibraryItem> listCurentCategories() {
		List<LibraryItem> items = new ArrayList<LibraryItem>();
		
		if (curent.isCategoty()) {
			ALibCategoryLoader loader = CategoryLoadersFactory.getLoader(curent.getId());
			return loader.listCategories();
		}
		
		return items;
	}


	public void selectCategory(LibraryItem category) {
		if (category.isCategoty()) {
			this.curent = category;
		}
	}
	
	public boolean isRootCategory() {
		return curent.getId().startsWith("root://");
	}

	public void goBack() {
		curent = CategoryLoadersFactory.getLoader(curent.getId()).back();
	}

	public List<LibraryItem> listSongs() {
		return CategoryLoadersFactory.getLoader(curent.getId()).listSongs();
	}

	public MidiSong getSequence(LibraryItem selected) {
		return CategoryLoadersFactory.getLoader(selected.getId()).load(selected);
	}

	public SongPlayed getStatistics(LibraryItem selected) {
		// Fake for now
		return null;
	}

}
