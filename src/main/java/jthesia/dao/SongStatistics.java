package jthesia.dao;

import java.util.Date;

public class SongStatistics {
	
	private final String song;
	private final long timesPlayed;
	private final long maxScore;
	private final Date lastPlay;
	
	public SongStatistics(String song, long timesPlayed, long maxScore, Date lastPlay) {
		this.song = song;
		this.timesPlayed = timesPlayed;
		this.maxScore = maxScore;
		this.lastPlay = lastPlay;
	}

	public String getSong() {
		return song;
	}

	public long getTimesPlayed() {
		return timesPlayed;
	}

	public long getMaxScore() {
		return maxScore;
	}

	public Date getLastPlay() {
		return lastPlay;
	}
	
}
