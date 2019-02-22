package jthesia.game.components;

import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL2;

import jthesia.graphics.Graphics;
import jthesia.resource.ImageResource;

public class ImgSprite extends Sprite {
	
	private BufferedImage img;
	
	public ImgSprite(BufferedImage img, float x, float y, float w, float h) {
		super(x, y, w, h);
		this.img = img;
	}

	public void render(GL2 gl) {
		ImageResource image = new ImageResource(img, gl.getGLProfile());
		Graphics.drawImage(gl, image, x, y, w, h, 0.0f);
	}

}
