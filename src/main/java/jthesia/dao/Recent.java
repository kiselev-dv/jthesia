package jthesia.dao;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Recent")
public class Recent {
	
	public Recent() {
		
	};
	
	@DatabaseField(id = true)
	private String hash;

	@DatabaseField(canBeNull = false)
	private String title;
	
	@DatabaseField(canBeNull = false)
	private String song;
	
	@DatabaseField(canBeNull = false)
	private Date lastPlayed;
	
	public String getSong() {
		return song;
	}
	
	public void setSong(String song) {
		this.song = song;
	}
	
	public Date getLastPlayed() {
		return lastPlayed;
	}
	
	public void setLastPlayed(Date lastPlayed) {
		this.lastPlayed = lastPlayed;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
