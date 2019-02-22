package jthesia.graphics;

public class UnitsConverter {
	
	private static final float widthUnits = 100;

	public static final UnitsConverter INSTANCE = new UnitsConverter();

	private int width = 640;
	private int height = 480;
	
	private float heightUnits;

	public void update(int width, int height) {
		this.width = width;
		this.height = height;
		
		float aspect = (float)width / (float)height;
		heightUnits = widthUnits / aspect;
	}

	public float getWidthUnits() {
		return widthUnits;
	}
	
	public float getHeightUnits() {
		return heightUnits;
	}
	
	public float getAspectRatio() {
		return (float)width / (float)height;
	}

	public float toUnitsX(int x) {
		return (float)x / (float)width * widthUnits;
	}
	
	public float toUnitsY(int y) {
		return (float)y / (float)height * heightUnits;
	}

	public int getWidthPixels() {
		return width;
	}
	
	public int getHeightPixels() {
		return height;
	}

	public int toPixelsX(float x) {
		return (int) (x / widthUnits * width);
	}
	
	public int toPixelsY(float y) {
		return height - (int) (y / heightUnits * height);
	}
	
	public int toPixelsHeight(float h) {
		return (int) (h / heightUnits * height);
	}
}
