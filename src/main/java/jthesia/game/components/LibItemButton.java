package jthesia.game.components;

import jthesia.game.musiclib.LibraryItem;
import jthesia.graphics.Graphics;

public class LibItemButton extends TextButton {

	private LibNavigatorFrame parent;
	private LibraryItem itm;

	public LibItemButton(LibNavigatorFrame parent, LibraryItem itm, float x, float y, float w, float h) {
		super(itm.getTitle(), 0.5f, 0.5f, Graphics.COLOR_PALETTE[4], x, y, w, h);
		this.parent = parent;
		this.itm = itm;
	}

	@Override
	public void onClick() {
		parent.selectLibraryItem(this.itm);
	}
}
