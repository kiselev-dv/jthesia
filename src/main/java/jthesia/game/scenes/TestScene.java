package jthesia.game.scenes;

import com.jogamp.opengl.GLProfile;

import jthesia.game.components.TextButton;
import jthesia.graphics.Graphics;
import jthesia.resource.Sounds;

public class TestScene extends AScene {
	
	private TextButton button;
	
	@Override
	public void load(GLProfile glProfile) {
		
		button = new TextButton("Test, very very long string with text", 
				0.5f, 0.5f, Graphics.COLOR_PALETTE[0], 10, 10, 20, 20);
		
		button.attachHoverSound(Sounds.MENU2);
		
		addFrame(button);
	}

}
