/** The example shows some Graphics2D class methods.
   1. Graphics2D is derived from Graphics.
   2. The draw(Shape) method takes a 2D shape to draw with current color, stroke and other
	features.
   3. The setStroke(Stroke) method allows you to change the pen size
   4. The setPaint(Paint) methods allows you to change the color, gradient color or texture
	pattern used during object rendering.
*/

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.geom.*;

public class J2DDrawShapes extends CloseableJFrame {
	JPanel   plShapes = new JPanel() ;
	final int ROWS = 4, COLS = 3;
	final int BORDER = 40;
	final float PI = 3.14159f;
	int       cellHeight, cellWidth;

	public J2DDrawShapes(String title) {
		super(title);
		getContentPane().add(plShapes, BorderLayout.CENTER);
		plShapes.setToolTipText("Show Color");
		setSize(600, 450);
		setVisible(true);
	}

	public void paint( Graphics g1) {
		cellWidth = plShapes.getWidth() / COLS;
		cellHeight = plShapes.getHeight() / ROWS;
		super.paint(g1);
		Graphics2D g = (Graphics2D) plShapes.getGraphics();
		drawGrid(g, ROWS, COLS);
		drawEllipseAt("Ellipse", g, 0, 0);
		drawRectangleAt("Rectangle", g, 0, 1);
		drawRoundRectangleAt("Rond Rectangle", g, 0, 2);
		drawArcAt("Open Arc", g, 1, 0, Arc2D.OPEN);
		drawArcAt("Arc w. Chord", g, 1, 1, Arc2D.CHORD);
		drawArcAt("Pie Shape", g, 1, 2, Arc2D.PIE);	
		drawLineAt("Line", g, 2, 0);
		drawPolygon("Polygon", g, 2, 1);
		drawCubicCurveAt("Cubic Curve", g, 2, 2);
		drawQuadCurveAt("Quadratic Curve", g, 3, 0);
		fillRoundRectangleAt("Fill Solid", g, 3, 1);
		fillWithGradientPaintAt("Fill Gradient Color", g, 3, 2);
	}

	// ---------------- Draw Grid --------------------------
	private void drawGrid(Graphics2D g, int rows, int cols) {
	    g.setPaint(Color.blue);
	    for ( int i = 1; i < rows; i ++ )
		g.draw( new Line2D.Double (0, cellHeight * i, plShapes.getWidth(), cellHeight * i));
	    for ( int i = 1; i < cols; i ++ )
		g.draw( new Line2D.Double (cellWidth * i, 0, cellWidth * i, plShapes.getHeight()));

	}
	//-----------------------------------------------------------


	// ---------------- Draw Ellipse --------------------------
	// Ellipse2D() : Abstract constructor.
	// Ellipse2D.Float(float x=0, float y=0, float w=0, float h=0)
	// Ellipse2D.Double(double x=0, double y=0, double w=0, double h=0)
	private void drawEllipseAt(String s, Graphics2D g, int i, int j) {
		g.setStroke(new BasicStroke(1.0f)); // thinkness of line draw
		g.setPaint(Color.black);		    // line/text color
		g.draw( new Ellipse2D.Double (cellWidth * j + BORDER, cellHeight * i + BORDER, 
				cellWidth-2*BORDER, cellHeight-2*BORDER));
		g.drawString(s, cellWidth*j+BORDER+cellWidth/5,
					cellHeight*i+BORDER+cellHeight/2);
	}
	//-----------------------------------------------------------

	// ---------------- Draw Rectangle --------------------------
	// Rectangle2D() : Abstract constructor.
	// RoundRectangle2D.Float(float x=0, float y=0, float w=0, float h=0)
	// RoundRectangle2D.Double(double x=0, double y=0, double w=0, double h=0)

	private void drawRectangleAt(String s, Graphics2D g, int i, int j) {
		g.draw( new Rectangle2D.Double (cellWidth * j + BORDER, cellHeight * i + BORDER, 
				cellWidth-2*BORDER, cellHeight-2*BORDER));
		g.drawString(s, cellWidth*j+BORDER+cellWidth/6,
					cellHeight*i+BORDER+cellHeight/2);
	}
	//-----------------------------------------------------------

	// ---------------- Draw Round Rectangle --------------------------
	// RoundRectangle2D() : Abstract constructor.
	// Rectangle2D.Float(float x=0, float y=0, float w=0, float h=0, float arcw, float arch)
	// Rectangle2D.Double(double x=0, double y=0, double w=0, double h=0, double arcw, double arch) 
	private void drawRoundRectangleAt(String s, Graphics2D g, int i, int j) {
		g.draw( new RoundRectangle2D.Double (cellWidth * j + BORDER, cellHeight * i + BORDER, 
				cellWidth-2 * BORDER, cellHeight- 2*BORDER, 20, 20));
		g.drawString(s, cellWidth*j+BORDER+cellWidth/8,
					cellHeight*i+BORDER+cellHeight/2);
	}
	//-----------------------------------------------------------

	// ---------------- Fill Round Rectangle Solid Color --------
	private void fillRoundRectangleAt(String s, Graphics2D g, int i, int j) {
		g.setPaint(Color.green);
		g.fill( new RoundRectangle2D.Double (cellWidth * j + BORDER, cellHeight * i + BORDER, 
				cellWidth-2 * BORDER, cellHeight- 2*BORDER, 20, 20));
		g.setPaint(Color.black);
		g.drawString(s, cellWidth*j+BORDER+cellWidth/8,
					cellHeight*i+BORDER+cellHeight/2);
	}
	//-----------------------------------------------------------

	// ---------------- Fill Round Rectangle Gradient Color --------
	// Color, GradientPaint and TexturePaint are 3 classes implmenting Paint which is
	// class for setPaint( Paint ).
	// GradientPaint(float x1, float y1, Color color1, float x2, float y2, Color color2) 
	// GradientPaint(float x1, float y1, Color color1, float x2, float y2, Color color2, boolean cyclic) 
	// GradientPaint(Point2D pt1, Color color1, Point2D pt2, Color color2) 
	// GradientPaint(Point2D pt1, Color color1, Point2D pt2, Color color2, boolean cyclic)  

	private void fillWithGradientPaintAt(String s, Graphics2D g, int i, int j) {
		g.setPaint(new GradientPaint(cellWidth * j + BORDER, cellHeight * i + BORDER,
				Color.red, 
				cellWidth * (j+1) -  2 * BORDER, cellHeight * i + BORDER,
				Color.gray));
		g.fill( new RoundRectangle2D.Double (cellWidth * j + BORDER, cellHeight * i + BORDER, 
				cellWidth-2 * BORDER, cellHeight- 2*BORDER, 20, 20));
//		g.setPaint(Color.black);
		g.drawString(s, cellWidth*j+BORDER,
					cellHeight*i+BORDER+cellHeight/2);
	}
	//-----------------------------------------------------------

	// ---------------- Draw Arc --------------------------
	// Arc2D.Float(int type = 0) 
	// Arc2D.Float(float x=0, float y=0, float w=0, float h=0)
	// Arc2D.Float(float x, float y, float w, float h, float start, float extent, int type)  
	// Arc2D.Float(Rectangle2D ellipseBounds, float start, float extent, int type) 
	// Arc2D.Double() has 4 similar constructors.

	private void drawArcAt(String s, Graphics2D g, int i, int j, int type) {
		g.draw( new Arc2D.Double (cellWidth * j + BORDER, cellHeight * i + BORDER, 
				cellWidth-2*BORDER, cellHeight-2*BORDER, 45, 90, type));
		g.drawString(s, cellWidth*j+BORDER+cellWidth/6,
					cellHeight*i+BORDER+cellHeight/2);
	}
	//-----------------------------------------------------------

	// ---------------- Draw Line --------------------------
	// Line2D.Float(): Initializes a Line with coordinates (0, 0) -> (0, 0). 
	// Line2D.Float(float X1, float Y1, float X2, float Y2) 
	// Line2D.Float(Point2D p1, Point2D p2)  
	// Line2D.Double(): has similar constructors.

	private void drawLineAt(String s, Graphics2D g, int i, int j) {
		g.draw( new Line2D.Double (cellWidth * j + BORDER, cellHeight * i + BORDER, 
				cellWidth*(j+1) - 2 * BORDER, cellHeight * (i+1) - 2 * BORDER));
		g.drawString(s, cellWidth*j+BORDER+cellWidth/6,
					cellHeight*i+BORDER+cellHeight/2);
	}
	//-----------------------------------------------------------

	// ---------------- Draw Polygon --------------------------
	// Polygon() : Creates an empty polygon. 
	// Polygon(int[] xpoints, int[] ypoints, int npoints)  
	private void drawPolygon(String s, Graphics2D g, int i, int j) {
		int x[] = { cellWidth * j + BORDER, cellWidth * j + BORDER + 10,
			cellWidth * j + BORDER + 30, cellWidth * j + BORDER + 65 };
		int y[] = { cellHeight * i + BORDER + 10, cellHeight * i + BORDER + 45,
			cellHeight * i + BORDER + 45, cellHeight * i + BORDER + 60 };
		g.draw( new Polygon(x, y, 4 ) ); 
		g.drawString(s, cellWidth*j+BORDER+cellWidth/6,
					cellHeight*i+BORDER+cellHeight/2);
	}
	//-----------------------------------------------------------

	// ---------------- Draw Cubic Curve --------------------------
	// CubicCurve2D.Float() : Initializes a CubicCurve with coordinates (0, 0, 0, 0, 0, 0). 
	// CubicCurve2D.Float(float x1, float y1, float ctrlx1, float ctrly1,
	//			float ctrlx2, float ctrly2, float x2, float y2)  
	// Two similar constructors exist for double.

	private void drawCubicCurveAt(String s, Graphics2D g, int i, int j) {
		g.draw( new CubicCurve2D.Double (cellWidth * j + BORDER, cellHeight * i + BORDER, 
						cellWidth * (j+1) -2 * BORDER, cellHeight * i + BORDER, 
						cellWidth * (j+1) -2 * BORDER, cellHeight * (i+1) - 2 * BORDER, 
				cellWidth*(j+1) - 2 * BORDER, cellHeight * (i+1) - 2 * BORDER));
		g.drawString(s, cellWidth*j+BORDER+cellWidth/6,
					cellHeight*i+BORDER+cellHeight/2);
	}
	//-----------------------------------------------------------

	// ---------------- Draw Quad Curve --------------------------
	// QuadCurve2D.Float() : Initializes a QuadCurve with coordinates (0, 0, 0, 0, 0, 0). 
	// QuadCurve2D.Float(float x1, float y1, float ctrlx1, float ctrly1, float x2, float y2)  
	// Two similar constructors exist for double.

	private void drawQuadCurveAt(String s, Graphics2D g, int i, int j) {
		g.draw( new QuadCurve2D.Double (cellWidth * j + BORDER, cellHeight * i + BORDER, 
						cellWidth * (j+1) - BORDER, cellHeight * i + BORDER, 
				cellWidth*(j+1) - 2 * BORDER, cellHeight * (i+1) - 2 * BORDER));
		g.drawString(s, cellWidth*j+BORDER+cellWidth/6,
					cellHeight*i+BORDER+cellHeight/2);
	}
	//-----------------------------------------------------------
	public static void main(String argv[]) {
		new J2DDrawShapes("Java2D: Draw Shapes");
	}
}
