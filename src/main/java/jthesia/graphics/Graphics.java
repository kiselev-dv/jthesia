package jthesia.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;

import jthesia.resource.ImageResource;

public class Graphics {
	
	private static float red = 1;
	private static float green = 1;
	private static float blue = 1;
	private static float alpha = 1;
	
	private static final int DEFAULT_TEXT_LINE = 10;

	public static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	
	private static TextRenderer textRenderer = new TextRenderer(DEFAULT_FONT);
	
	public static final float[][] COLOR_PALETTE = new float[5][];
	static {
		COLOR_PALETTE[0] = fromHex("#751242");
		COLOR_PALETTE[1] = fromHex("#18557B");
		COLOR_PALETTE[2] = fromHex("#BCD086");
		COLOR_PALETTE[3] = fromHex("#FB8913");
		COLOR_PALETTE[4] = fromHex("#D33F11");
	}
	
	public static final float[][] COLOR_PALETTE_BRIGHT = new float[5][];
	static {
		COLOR_PALETTE_BRIGHT[0] = bright(fromHex("#751242"), 1.75f);
		COLOR_PALETTE_BRIGHT[1] = bright(fromHex("#18557B"), 1.75f);
		COLOR_PALETTE_BRIGHT[2] = bright(fromHex("#BCD086"), 1.75f);
		COLOR_PALETTE_BRIGHT[3] = bright(fromHex("#FB8913"), 1.75f);
		COLOR_PALETTE_BRIGHT[4] = bright(fromHex("#D33F11"), 1.75f);
	}

	public static final float[] COLOR_WHITE = new float[] {1, 1, 1, 1};
	public static final float[] TEXT_COLOR = COLOR_WHITE;
	
	private static float[] fromHex(String hex) {
		Color c = Color.decode(hex);
		return new float[] {
				(float)c.getRed() / 255.0f, 
				(float)c.getGreen() / 255.0f, 
				(float)c.getBlue() / 255.0f, 
				1
		};
	}
	
	private static float[] bright(float[] c, float mul) {
		return new float[] {c[0] * mul, c[1] * mul, c[2] * mul, 1};
	}
	
	public static void fillRect(GL2 gl, float x, float y, float width, float height) {
		gl.glColor4f(red, green, blue, alpha);
		
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glVertex2f(x, y);
		gl.glVertex2f(x + width, y);
		gl.glVertex2f(x + width, y + height);
		gl.glVertex2f(x, y + height);
		
		gl.glEnd();
	}
	

	public static void fillRectCentered(GL2 gl, float x, float y, float width, float height, float rotation) {
		gl.glColor4f(red, green, blue, alpha);

		gl.glTranslatef(x, y, 0.0f);
		gl.glRotatef(rotation, 0, 0, 1);
		
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glVertex2f(- width / 2, - height / 2);
		gl.glVertex2f(  width / 2, - height / 2);
		gl.glVertex2f(  width / 2,   height / 2);
		gl.glVertex2f(- width / 2,   height / 2);
		
		gl.glEnd();
		
		gl.glRotatef(-rotation, 0, 0, 1);
		gl.glTranslatef(-x, -y, 0.0f);
	}
	
	public static void drawImage(GL2 gl, ImageResource img, float x, float y, float width, float height, float rotation) {
		gl.glColor4f(red, green, blue, alpha);

		gl.glTranslatef(x, y, 0.0f);
		gl.glRotatef(rotation, 0, 0, 1);
		
		gl.glBegin(GL2.GL_QUADS);
		
		Texture texture = img.getTexture();
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureObject());
		
		gl.glTexCoord2f(0, 0);
		gl.glVertex2f(- width / 2, - height / 2);
		
		gl.glTexCoord2f(1, 0);
		gl.glVertex2f(  width / 2, - height / 2);
		
		gl.glTexCoord2f(1, 1);
		gl.glVertex2f(  width / 2,   height / 2);
		
		gl.glTexCoord2f(0, 1);
		gl.glVertex2f(- width / 2,   height / 2);
		
		gl.glEnd();
		gl.glFlush();
		
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		gl.glRotatef(-rotation, 0, 0, 1);
		gl.glTranslatef(-x, -y, 0.0f);
	}
	
	public static void drawText(GL2 gl, String text, float x, float y) {

		/* Prepare text renderer to display text. */
        textRenderer.beginRendering(
        		UnitsConverter.INSTANCE.getWidthPixels(), 
        		UnitsConverter.INSTANCE.getHeightPixels());

        /* Set drawing colour for control's text. */
        textRenderer.setColor(red, green, blue, alpha);
        
        /* Draw control's text. */
        textRenderer.draw(text, 
        		UnitsConverter.INSTANCE.toPixelsX(x), 
        		UnitsConverter.INSTANCE.toPixelsY(y) - DEFAULT_TEXT_LINE);

        /* Complete text rendering. */
        textRenderer.endRendering();
	}
	
	public static Rectangle2D getTextBounds(String text) {
		return textRenderer.getBounds(text);
	}

	public static void setClor(float r, float g, float b, float a) {
		red = norm(r);
		green = norm(g);
		blue = norm(b);
		alpha = norm(a);
	}
	
	public static void setClor(float[] c) {
		red   = norm(c[0]);
		green = norm(c[1]);
		blue  = norm(c[2]);
		alpha = norm(c[3]);
	}


	private static float norm(float f) {
		return Math.min(1, Math.max(0, f)) ;
	}

	public static void setScissorBox(GL2 gl, float x, float y, float width, float height) {
		
		UnitsConverter u = UnitsConverter.INSTANCE;

		int px = u.toPixelsX(x);
		int ph = u.toPixelsHeight(height);
		int py = u.toPixelsY(y) - ph;
		int pw = u.toPixelsX(width);
		gl.glScissor(px, py, pw, ph);
	}
	
	public static void resetScissorBox(GL2 gl) {
		UnitsConverter u = UnitsConverter.INSTANCE;
		gl.glScissor(0, 0, u.getWidthPixels(), u.getHeightPixels());
	}

	public static void destroy() {
		// Free resources
	}

}
