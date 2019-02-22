package jthesia.game.musiclib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jthesia.dao.DAO;
import jthesia.dao.Recent;
import jthesia.midi.MidiSong;

public class RecentCategoryLoader extends ALibCategoryLoader {

	public static final String NAME = "recent";
	public static final String TITLE = "Recent";

	public RecentCategoryLoader(String id) {
		super(id);
	}

	@Override
	public List<LibraryItem> listCategories() {
		return Collections.emptyList();
	}

	@Override
	public LibraryItem back() {
		return MusicLibNavigator.ROOT;
	}

	@Override
	public List<LibraryItem> listSongs() {
		List<LibraryItem> items = new ArrayList<>();
		for (Recent recent : DAO.INSTANCE.listRecent()) {
			items.add(new LibraryItem(recent.getTitle(), recent.getSong(), false));
		}
		
		return items;
	}

	@Override
	public MidiSong load(LibraryItem selected) {
		return CategoryLoadersFactory.getLoader(selected.getId()).load(selected);
	}

}
