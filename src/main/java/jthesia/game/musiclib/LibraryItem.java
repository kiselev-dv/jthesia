package jthesia.game.musiclib;

public class LibraryItem {
	
	private final String id;
	private final String title;
	private final boolean categoty;
	private final boolean selectedByDefault;
	
	public LibraryItem(String title, String id, boolean category, boolean selectedByDefault) {
		this.id = id;
		this.title = title;
		this.categoty = category;
		this.selectedByDefault = selectedByDefault;
	}
	
	public LibraryItem(String title, String id, boolean category) {
		this(title, id, category, false);
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public boolean isCategoty() {
		return categoty;
	}

	public boolean isSelectedByDefault() {
		return selectedByDefault;
	}
	
}
