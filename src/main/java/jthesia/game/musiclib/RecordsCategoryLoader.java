package jthesia.game.musiclib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import org.apache.commons.lang3.StringUtils;

import jthesia.JThesia;
import jthesia.midi.MidiSong;

public class RecordsCategoryLoader extends ALibCategoryLoader {

	public static final File RECORDS_FOLDER;
	static {
		File baseDir = new File(JThesia.getBaseDir());
		RECORDS_FOLDER = new File(baseDir, "records");
	}
	
	public static final String TITLE = "Records";
	public static final String NAME = "records";

	public RecordsCategoryLoader(String id) {
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
		items.add(new LibraryItem("New", NAME + "://new", false));
		
		for(File f : RECORDS_FOLDER.listFiles(LocalLibLoader.MIDI_FILE_FILTER)) {
			String title = f.getName();
			title = StringUtils.removeEnd(title, ".mid");
			title = StringUtils.replaceChars(title, "_", " ");
			
			items.add(new LibraryItem(title, NAME + "://" + f.getName(), false));
		}
		
		return items;
	}
	
	@Override
	public MidiSong load(LibraryItem selected) {
		String name = StringUtils.substringAfter(selected.getId(), NAME + "://");
		
		File file = new File(RECORDS_FOLDER, name);
		try {
			MidiSong parsed = parser.parse(MidiSystem.getSequence(file));
			if (parsed.getName() == null) {
				parsed.setName(selected.getTitle());
			}
			return parsed;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static File getNewRecordFile() {
		int count = RECORDS_FOLDER.listFiles().length;
		
		File newFile = new File(RECORDS_FOLDER, "record" + count + ".mid");
		while (newFile.exists()) {
			newFile = new File(RECORDS_FOLDER, "record" + (count++) + ".mid");
		}
		
		return newFile;
	}

}
