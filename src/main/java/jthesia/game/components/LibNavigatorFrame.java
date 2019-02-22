package jthesia.game.components;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;

import jthesia.game.MouseState;
import jthesia.game.musiclib.LibraryItem;
import jthesia.game.musiclib.MusicLibNavigator;
import jthesia.graphics.Graphics;

public class LibNavigatorFrame implements InteractiveFrame  {

	/* 
	 * Use navigator instead of direct file navigation
	 * to be able to easily switch to online library
	 * or to have a virtual categories like favorites
	 * or recent which don't directly reflect folder
	 * structure
	 * */
	private MusicLibNavigator nav = MusicLibNavigator.INSTANCE;
	
	private List<TextButton> categories = new ArrayList<>();
	
	private SongSelectHandler selectCallback;
	
	public static interface SongSelectHandler {
		public void selectSong(LibraryItem selected);
	}

	public LibNavigatorFrame (SongSelectHandler listener) {
		createUiItems();
		this.selectCallback = listener;
	}
	
	private TextButton backButton;

	private void createUiItems() {
		int i = 0;
		
		float w = 15f;
		float h = 2.5f;
		float m = w * 0.1f;
		
		for (LibraryItem li : nav.listCurentCategories()) {
			LibItemButton lb = new LibItemButton(this, li, m + w * i + m * i, m, w, h);
			categories.add(lb);
			i++;
		}
		
		backButton = new TextButton("Back", 0.5f, 0.5f, Graphics.COLOR_PALETTE[3], m, m * 2.0f + h, w, h) {

			@Override
			public void onClick() {
				nav.goBack();
				updateUI();
				unselectSong();
			}
			
		};

		i = 0;
		float top = m * 3.0f + h * 2.0f;
		for (LibraryItem li : nav.listSongs()) {
			LibItemButton lb = new LibItemButton(this, li, m, top + (m + h) * i, w * 2, h);
			categories.add(lb);
			i++;
		}
	}
	
	protected void unselectSong() {
		this.selectCallback.selectSong(null);
	}

	public void render(GL2 gl) {
		
		for(TextButton lb: categories) {
			lb.render(gl);
		}

		if (!nav.isRootCategory()) {
			backButton.render(gl);
		}
	}

	public void screenReshape() {
		updateUI();
	}

	private void updateUI() {
		categories.clear();
		createUiItems();
	}

	public boolean update(MouseState mouse, boolean select) {
		
		for(TextButton lb: categories) {
			if(lb.update(mouse, false)) {
				return true;
			};
		}
		
		return backButton.update(mouse, false);
	}

	public void selectLibraryItem(LibraryItem itm) {
		if (itm.isCategoty()) {
			nav.selectCategory(itm);
			updateUI();
		}
		else {
			this.selectCallback.selectSong(itm);
		}
	}

}
