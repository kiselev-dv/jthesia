package jthesia.game.musiclib;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;

import org.apache.commons.lang3.StringUtils;

import jthesia.JThesia;
import jthesia.midi.MidiSong;

public class LocalLibLoader extends ALibCategoryLoader {

	public static final String TITLE = "Library";
	public static final String NAME = "lib";

	private static final class DirectoriesFilter implements FileFilter {
		public boolean accept(File f) {
			return f.isDirectory();
		}
	}
	
	private static final class MidiFileFilter implements FileFilter {
		public boolean accept(File f) {
			String name = f.getName().toLowerCase();
			return f.isFile() && (
					name.endsWith(".mid") || name.endsWith(".midi"));
		}
	}

	private static final DirectoriesFilter DIRECTORIES_FILTER = new DirectoriesFilter();
	public static final MidiFileFilter MIDI_FILE_FILTER = new MidiFileFilter();
	
	private static boolean isInSubDirectory(File dir, File file) {

	    if (file == null)
	        return false;

	    if (file.equals(dir))
	        return true;

	    return isInSubDirectory(dir, file.getParentFile());
	}
	
	public static final File MUSIC_LIB_DIR;
	static {
		File baseDir = new File(JThesia.getBaseDir());
		MUSIC_LIB_DIR = new File(baseDir, "music");
	}
	
	public LocalLibLoader(String id) {
		super(id);
	}

	@Override
	public List<LibraryItem> listSongs() {
		return listItems(MIDI_FILE_FILTER, false);
	}

	@Override
	public List<LibraryItem> listCategories() {
		return listItems(DIRECTORIES_FILTER, true);
	}

	private List<LibraryItem> listItems(FileFilter filter, boolean categories) {
		List<LibraryItem> items = new ArrayList<>();
		for (File f : getFile(id).listFiles(filter)) {
			String title = f.getName();
			title = StringUtils.replaceChars(title, "_", " ");
			
			String localId = getId(f); 
			
			if (!categories) {
				title = StringUtils.removeEndIgnoreCase(title, ".mid");
				title = StringUtils.removeEndIgnoreCase(title, ".midi");
			}
			
			items.add(new LibraryItem(title, localId, categories));
		}
		return items;
	}

	private String getId(File f) {
		return StringUtils.replace(f.toString(), MUSIC_LIB_DIR.toString(), "lib://");
	}

	private static File getFile(String id) {
		String libPath = StringUtils.substringAfter(id, "lib://");
		return new File(MUSIC_LIB_DIR, libPath);
	}

	@Override
	public LibraryItem back() {
		File parentFile = getFile(id).getParentFile();
		if (isInSubDirectory(MUSIC_LIB_DIR, parentFile)) {
			return new LibraryItem(parentFile.getName(), getId(parentFile), true);
		}
		else {
			return MusicLibNavigator.ROOT;
		}
	}

	@Override
	public MidiSong load(LibraryItem selected) {
		
		File file = getFile(selected.getId());
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


}
