package jthesia.game.components;

import com.jogamp.opengl.GL2;

import jthesia.graphics.Graphics;
import jthesia.graphics.UnitsConverter;

public class TextButton extends ButtonSprite {

	private float[] color;
	private float[] colorBright;
	
	private String label;
	private String labelT;

	private boolean hower;
	
	private float labelX;
	private float labelY;
	private boolean visible;

	public TextButton(String label, float labelX, float labelY, 
			float[] color, float x, float y, float w, float h) {
		
		super(x, y, w, h);
		this.color = color;
		this.colorBright = new float[] {color[0] * 1.5f, color[1] * 1.5f, color[2] * 1.5f, color[3]};
		
		this.labelX = labelX;
		this.labelY = labelY;
		
		this.label = label;
		this.labelT = trimLabel(label);
	}

	protected String trimLabel(String text) {
		
		int labelwidth = (int) Graphics.getTextBounds(text).getWidth();
		float w = UnitsConverter.INSTANCE.toUnitsX(labelwidth) + labelX;
		float fraction = this.w / w;
	
		if (fraction < 1.0f) {
			int newLength = (int) ((float)text.length() * fraction);
			return text.substring(0, newLength);
		}
		
		return text;
	}

	public String getLabel() {
		return label;
	}
	
	public String getVisibleLable() {
		return labelT;
	}
	
	@Override
	public void onMouseIn() {
		this.hower = true;
	}
	
	@Override
	public void onMouseOut() {
		this.hower = false;
	}
	
	@Override
	public void onClick() {
	}

	public void render(GL2 gl) {
		Graphics.setClor(hower ? colorBright : color);
		Graphics.fillRect(gl, x, y, w, h);
		
		Graphics.setClor(1, 1, 1, 1);
		Graphics.drawText(gl, labelT, x + labelX, y + labelY);
	}

	public void screenReshape() {
		this.labelT = trimLabel(label);
	}
	
	public void setLable(String label) {
		this.label = label;
		this.labelT = trimLabel(label);
	}
	
	public void setColor(float[] color) {
		this.color = color;
	}

}
