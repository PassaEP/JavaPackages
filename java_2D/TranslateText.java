/** The exmaples shows Transformation
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

public class TranslateText extends CloseableJFrame {
    JPanel drawingPane = new JPanel();

    public TranslateText( String title ) {
	super(title);
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(drawingPane, BorderLayout.CENTER);
	setSize(400, 350);
	setVisible(true);
	drawingPane.setBackground(Color.black);
	TranslateThread mthd = new TranslateThread(drawingPane);
	mthd.start();	
    }

    public static void main(String argv[]) {
	new TranslateText("Translate & Rotate Text & Shape");
    }
}

class TranslateThread extends Thread {
    final  double PI = 3.14159;
    String s = "Rectangle";
    private JPanel p = null ;
    Random rnd = new Random();
    Graphics2D g = null ;
    boolean inc = true ;
	TranslateThread(JPanel p1) { p = p1; g = (Graphics2D) p.getGraphics();}
	public void run() {
		try { Thread.sleep(600); } catch(InterruptedException e) {}
	  	int i = 0;
		int w = p.getWidth(), h = p.getHeight();
          	int x = w/2 , y = h/2;
	  	g.setFont( new Font("Roman", Font.BOLD, 20)) ;
	  	AffineTransform tfm = new AffineTransform();
		g.setXORMode(Color.white)) ;
		RoundRectangle2D.Double rct = new RoundRectangle2D.Double (0, 0, 100, 50, 20, 20);
	   while (true) {
		x = (int) (rnd.nextInt() % 20 + x);
		y = (int) (rnd.nextInt() % 20 + y);
		x = x > (w-30) ? w/2 : x < 0? 0 : x;
		y = y > (h-30) ? h/2 : y < 0? 0 : y;
		tfm.setToTranslation(x, y);
		g.setTransform( tfm ) ;
		g.rotate(PI * 2 * Math.random()); // Rotate btw 0..2 Pi

		g.draw(rct);
		g.drawString(s, 0, 0);

		try { Thread.sleep(600); } catch(InterruptedException e) {}
		// Erase the drawings
		g.drawString(s, 0, 0);
		g.draw( rct );
		i = i + (inc? 1 : -1);
		if ( i == 42 || i == 0 ) inc = !inc ; // change direction after i reach 0 or 150.
	   }
	}
}
	