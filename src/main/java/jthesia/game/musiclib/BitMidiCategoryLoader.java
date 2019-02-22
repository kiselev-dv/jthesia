package jthesia.game.musiclib;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.midi.MidiSystem;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import jogamp.opengl.util.av.JavaSoundAudioSink;
import jthesia.midi.MidiSong;
import jthesia.midi.Parser;

public class BitMidiCategoryLoader extends ALibCategoryLoader {

	public static final String NAME = "bitmidi";
	public static final String TITLE = "BitMidi";

	public BitMidiCategoryLoader(String id) {
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
		
		try {
			List<LibraryItem> items = new ArrayList<>();
			
			String html = IOUtils.toString(new URL("https://bitmidi.com/").openStream(), "utf8");
			String json = StringUtils.substringBetween(html, "window.initStore =", "</script>");
			
			JSONObject pageJSON = new JSONObject(json);
			JSONObject midis = pageJSON.getJSONObject("data").getJSONObject("midis");
			
			midis.keys().forEachRemaining(k -> {
				JSONObject midi = midis.getJSONObject(k);
				String title = midi.getString("name");
				title = StringUtils.removeEndIgnoreCase(title, ".mid");
				title = StringUtils.removeEndIgnoreCase(title, ".midi");
				items.add(new LibraryItem(title, "bitmidi://" + midi.getString("downloadUrl"), false));
			});
			
			return items;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public MidiSong load(LibraryItem selected) {
		try {
			String url = "https://bitmidi.com" + StringUtils.substringAfter(selected.getId(), "bitmidi://");
			return new Parser().parse(MidiSystem.getSequence(new URL(url).openStream()));
		}
		catch (Exception e) {
			throw new Error(e);
		}
	}

}
