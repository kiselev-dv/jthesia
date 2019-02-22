package jthesia.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

import jthesia.game.musiclib.LibraryItem;

public class DAO {
	
	public static final DAO INSTANCE = new DAO();
	
	private JdbcPooledConnectionSource connectionSource;
	private Dao<SongPlayed, Long> statisticsDao;
	private Dao<Recent, String> recentDao;
	
	private DAO() {
		try {
			connectionSource = new JdbcPooledConnectionSource("jdbc:sqlite:jthesia.db");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			TableUtils.createTableIfNotExists(connectionSource, SongPlayed.class);
			TableUtils.createTableIfNotExists(connectionSource, Recent.class);
			
			statisticsDao = DaoManager.createDao(connectionSource, SongPlayed.class);
			recentDao = DaoManager.createDao(connectionSource, Recent.class);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void save(SongPlayed stats) {
		try {
			statisticsDao.createIfNotExists(stats);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public SongStatistics getStatistics(String song) {
		String fromWhereClause = " FROM " + statisticsDao.getTableName() + "play where play.songId = ?";
		
		String q = "SELECT count(*) " + fromWhereClause;
		
		try {
			long timesPlayed = statisticsDao.queryRawValue(q, song);
			return new SongStatistics(song, timesPlayed, 0, new java.util.Date());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void close() {
		try {
			connectionSource.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Recent> listRecent() {
		try {
			return recentDao.queryBuilder()
					.orderBy("lastPlayed", false)
					.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	public void recent(LibraryItem libItem, String hash) {
		Recent recent = new Recent();
		
		recent.setTitle(libItem.getTitle());
		recent.setHash(hash);
		recent.setLastPlayed(new java.util.Date());
		recent.setSong(libItem.getId());
		
		try {
			recentDao.createOrUpdate(recent);
			String tbl = recentDao.getTableName();
			recentDao.updateRaw("delete from " + tbl + 
					" where hash not in (" + 
						"select hash from " + tbl + " order by lastPlayed desc limit 25" + 
					")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
