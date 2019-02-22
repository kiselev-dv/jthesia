package jthesia.game.musiclib;

import org.apache.commons.lang3.StringUtils;

public class CategoryLoadersFactory {
	
	public static ALibCategoryLoader getLoader(String id) {
		String location = getLocation(id);
		
		if(LocalLibLoader.NAME.equals(location)) {
			return new LocalLibLoader(id);
		}
		
		if(RootLibLoader.NAME.equals(location)) {
			return new RootLibLoader(id);
		}
		
		if(DownloadsCategoryLoader.NAME.equals(location)) {
			return new DownloadsCategoryLoader(id);
		}
		
		if(RecentCategoryLoader.NAME.equals(location)) {
			return new RecentCategoryLoader(id);
		}
		
		if(BitMidiCategoryLoader.NAME.equals(location)) {
			return new BitMidiCategoryLoader(id);
		}
		
		if(RecordsCategoryLoader.NAME.equals(location)) {
			return new RecordsCategoryLoader(id);
		}
		
		return null;
	}
	
	public static String getLocation(String id) {
		return StringUtils.substringBefore(id, "://");
	}

}
