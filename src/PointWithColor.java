import java.awt.Color;


public class PointWithColor {
	private final int mouseX;
	
	private final int mouseY;
	
	private final String color;
	
	private final String shape;

	public PointWithColor(int mouseX, int mouseY, String color, String shape) {
		super();
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.color = color;
		this.shape = shape;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public String getColor() {
		return color;
	}

	public String getShape() {
		return shape;
	}
}
