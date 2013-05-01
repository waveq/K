import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

class DrawArea extends JPanel {
	
	public static final int DRAW_WIDTH = 550;
	
	public static final int DRAW_HEIGHT = 400;
	
	private DrawPointReceiver drawPointReceiver;
		
	private ArrayList<String> pointWithColor = new ArrayList<String>();
	
	private int mouseX;
	
	private int mouseY;
	
	private String myCoords;
	
	private Color selectedColor = Color.black;
	
	Color colorOfDot = Color.black;
	
	private String dotShape = "dot";
	
	private boolean imDrawer = false;
	
	public DrawArea(DrawPointReceiver drawPointReceiver) {
		setPreferredSize(new Dimension(DRAW_WIDTH,DRAW_HEIGHT));
		setMinimumSize(new Dimension (550, 400));
		
		setBorder(BorderFactory.createEtchedBorder());
		
		addMouseListener(new MouseListener());
		addMouseMotionListener(new MouseListener());
		
		this.drawPointReceiver = drawPointReceiver;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		Rectangle2D background = new Rectangle2D.Double(0,0,DRAW_WIDTH,DRAW_HEIGHT);
		g2.setPaint(Color.white);
		g2.fill(background);
			
		
		/**
		 * draw all points on area
		 * String pointWithColor contains coordinates of dot and color
		 */
		for(String p: pointWithColor) {
			String stringToParse = p.toString();
			
			String delims = "[|]";
			String[] tokens = stringToParse.split(delims);
			int x = Integer.parseInt(tokens[0]);
			int y = Integer.parseInt(tokens[1]);
			changeColor(tokens[2]);
			dotShape = tokens[3];
			
				
			
			Ellipse2D smallEllipse = new Ellipse2D.Double(x-5,y-5,10,10);
			Ellipse2D bigEllipse = new Ellipse2D.Double(x-10,y-10,20,20);
			Rectangle2D smallSquare = new Rectangle2D.Double(x-5, y-5, 10, 10);
			Rectangle2D bigSquare = new Rectangle2D.Double(x-10, y-10, 20, 20);
			Rectangle2D dot = new Rectangle2D.Double(x, y, 2, 2);
			
			g2.setPaint(colorOfDot);
			if(dotShape.equals("smallCircle")) 
				g2.fill(smallEllipse);
			else if(dotShape.equals("smallSquare"))
				g2.fill(smallSquare);
			else if(dotShape.equals("bigCircle"))
				g2.fill(bigEllipse);
			else if(dotShape.equals("bigSquare"))
				g2.fill(bigSquare);
			else if(dotShape.equals("dot"));
				g2.fill(dot);
		}
	}
	
	/**
	 * changes global var colorOfDot to one that was contained in pointWithColor
	 * @param myColor String that contains information about color
	 * in format: "java.awt.Color[r=x,g=x,b=x]"
	 */
	private void changeColor(String myColor) {
		if(myColor.equals("java.awt.Color[r=0,g=0,b=0]")) {
			colorOfDot = Color.black;
		}
		else if(myColor.equals("java.awt.Color[r=255,g=0,b=0]")) {
			colorOfDot = Color.red;
		}
		else if(myColor.equals("java.awt.Color[r=255,g=255,b=255]")) {
			colorOfDot = Color.white;
		}
		else if(myColor.equals("java.awt.Color[r=0,g=0,b=255]")) {
			colorOfDot = Color.blue;
		}
	}
	
	/**
	 * Adapters that listens to mouse(pressed, dragged).
	 */
	private class MouseListener extends MouseAdapter {
		public void mouseDragged(MouseEvent e) {
			if(imDrawer) {
				mouseX=e.getX();
				mouseY=e.getY();
				pointWithColor.add(mouseX+"|"+mouseY+"|"+selectedColor.toString()+"|"+dotShape);
				myCoords = (mouseX +"|"+mouseY+"|"+selectedColor.toString()+"|"+dotShape);
				drawPointReceiver.dotToSend(myCoords);
				repaint();
			}
		}
		public void mousePressed(MouseEvent e) {
			if(imDrawer) {
				mouseX=e.getX();
				mouseY=e.getY();
				pointWithColor.add(mouseX+"|"+mouseY+"|"+selectedColor.toString()+"|"+dotShape);
				
				myCoords = (mouseX +"|"+mouseY+"|"+selectedColor.toString()+"|"+dotShape);
				drawPointReceiver.dotToSend(myCoords);
				repaint();
			}
		}
	}
	
	public void setColor(Color color) {
		selectedColor = color;
	}
	
	public void imDrawer() {
		imDrawer = true;
	}
	
	public void imNotDrawer() {
		imDrawer = false;
	}
	
	
	public void setShape(String shape) {
		dotShape = shape;
	}
	
	public void wipeArea() {
			pointWithColor.clear();
			repaint();
	}
	
	public boolean imIDrawer() {
		return imDrawer;
	}
	
	/**
	 * adds new point to points and specifies shape and color
	 * @param x coordinate x
	 * @param y coordinate y
	 * @param color color of dot
	 * @param shape shape of color:smallCircle/bigCircle/smallSquare/bigSquare/dot.
	 */
	public void addCoordinates(int x, int y,String color, String shape) {
		if(color != null) {
			pointWithColor.add(x+"|"+y+"|"+color+"|"+shape);
		}
		else {
			pointWithColor.add(x+"|"+y+"|"+selectedColor.toString()+"|"+shape);
		}
		repaint();
	}
}
