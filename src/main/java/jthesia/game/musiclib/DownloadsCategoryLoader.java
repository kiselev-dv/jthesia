package jthesia.game.musiclib;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.midi.MidiSystem;

import org.apache.commons.lang3.StringUtils;

import jthesia.game.engine.Game;
import jthesia.midi.MidiSong;
import jthesia.midi.Parser;

public class DownloadsCategoryLoader extends ALibCategoryLoader {

	public static final String NAME = "downloads";
	public static final String TITLE = "Downloads";

	private static final File[] downloadLocation = Game.getDownloadFolders();

	public DownloadsCategoryLoader(String id) {
		super(id);
	}
	
	@Override
	public List<LibraryItem> listCategories() {
		return Collections.emptyList();
	}

	@Override
	public LibraryItem back() {
		return  MusicLibNavigator.ROOT;
	}

	@Override
	public List<LibraryItem> listSongs() {
		List<LibraryItem> items = new ArrayList<>();
		for(File folder : downloadLocation) {
			for(File f :folder.listFiles(LocalLibLoader.MIDI_FILE_FILTER)) {
					
				String id = getId(f);
				
				String title = f.getName();
				title = StringUtils.replaceChars(title, "_", " ");
				title = StringUtils.removeEndIgnoreCase(title, ".mid");
				title = StringUtils.removeEndIgnoreCase(title, ".midi");
				
				items.add(new LibraryItem(title, id, false));
			} 
		}
		
		return items;
	}

	private String getId(File f) {
		return "downloads://" + f.toString();
	}

	@Override
	public MidiSong load(LibraryItem selected) {
		try {
			File file = new File(StringUtils.substringAfter(selected.getId(), "downloads://"));
			return new Parser().parse(MidiSystem.getSequence(file));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
