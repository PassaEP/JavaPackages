/** The exmaples shows how to roatate strings.
   1. A transformation matrix may contain information about one or all of
 the following operations: rotation, scaling, shearing and translating of
 graphics contents drawn on the graphics.
   2. The key class that contains graphics transformation methods is
 AffineTransform class. There are several basic set of methods in the
 class.
   3. A set of static methods in AffineTransform allow you to create Rotate,
 Scale, Shear, and Translate transform easily:
	AffineTransform getToRotateInstance(..)
	AffineTransform getToScaleInstance(..)
	AffineTransform getToShearInstance(..)
	AffineTransform getToTranslateInstance(..)

   4. A set of methods in AffineTranform allow you replace current tranformation
 information in the current object with another tranformation and old transfomation
 information is lost.
	void setToRotation(..)
	void setToScale(..)
	void setToShear(..)
	void setToTranslation(..)

   5. Another set of methods in AffineTranform allows to add/concatenate new transformation
 information with existing one:
	void rotate(..)
	void scale(..)
	void shear(..)
	void translate(..)

   6. After a call to setTransform( AffineTransform ) from a Graphics2D, every graphics
draw on the graphics object will be transformed by the information contained in AffineTransform
object.

*/

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.*;

public class RotateText extends CloseableJFrame {
    JPanel drawingPane = new JPanel();
    public RotateText( String title ) {
	super(title);
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(drawingPane, BorderLayout.CENTER);
	setSize(400, 300);
	setVisible(true);
	drawingPane.setBackground(Color.black);
	RotateTextThread mthd = new RotateTextThread(drawingPane,
		"Java 2D", new Font("Roman", Font.BOLD, 30), Color.magenta, 1 );
	mthd.start();	
	new RotateTextThread(drawingPane,
		"Rotating ...", new Font("Roman", Font.BOLD, 20), Color.lightGray, -1 ).start();
    }

    public void paint(Graphics g1 ) {
	super.paint(g1);
    }

    public static void main(String argv[]) {
	new RotateText("Text Rotation");
    }
}

class RotateTextThread extends Thread {
    final  double PI = 3.14159;
    private JPanel p = null ;
    Graphics2D g = null ;
    boolean needErase = false;
    int i = 0, k = 0, d = 1;
    int x, y, sw, ww;
    String s;
    Font f = null ;
    Color c = null ;
	RotateTextThread(JPanel p1, String txt, Font fnd, Color clr, int dir ) {
		p = p1; g = (Graphics2D) p.getGraphics();
		ww = p.getWidth();
		s = txt; c = clr; f = fnd; d = dir;
		g.setFont( f ) ;
		sw = g.getFontMetrics().stringWidth(s);
		x = (d == 1)? sw: ww - sw; y = sw;
	}
	public void run() {
	  g.setXORMode(c);
	  while ( true )  {
	    try {
		g.setTransform(AffineTransform.getRotateInstance(PI/8 * i * d, x, y )) ;
		i = (i + 1) % 32;
		g.drawString(s , x, y);
		Thread.sleep(100);
		if ( d == 1) g.drawString(s, x, y);
	   } catch ( InterruptedException e ) { System.out.println("Waked up"); }
	  }
	}
}
	