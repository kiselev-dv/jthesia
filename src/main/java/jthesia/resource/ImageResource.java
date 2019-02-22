package jthesia.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class ImageResource {
	
	private final Texture texture;

	public ImageResource(BufferedImage image, GLProfile profile) {
		texture = AWTTextureIO.newTexture(profile, image, true);
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public static ImageResource load(URL url, GLProfile profile) {
		return new ImageResource(loadBufferedImage(url), profile);
	}
	
	public static BufferedImage loadBufferedImage(URL url) {
		try {
			return ImageIO.read(url);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
