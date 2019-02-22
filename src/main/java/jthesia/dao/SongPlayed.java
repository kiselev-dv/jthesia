package jthesia.dao;

import java.sql.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import jthesia.game.musiclib.LibraryItem;

@DatabaseTable(tableName = "SongPlayed")
public class SongPlayed {

	@DatabaseField(generatedId = true)
    private long id;
	
	@DatabaseField(canBeNull = false)
	private String songId;

	@DatabaseField(canBeNull = false)
    private Date date;

	@DatabaseField
	private int score;
	
	public SongPlayed() {
		
	}

	public SongPlayed(LibraryItem song, int score) {
		this.songId = song.getId();
		this.score = score;
		// Now
		this.date = new Date(new java.util.Date().getTime());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSongId() {
		return songId;
	}

	public void setSongId(String songId) {
		this.songId = songId;
	}
	
}
