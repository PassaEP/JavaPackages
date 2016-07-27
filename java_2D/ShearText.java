/** The exmaples shows shearing.
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

public class ShearText extends CloseableJFrame {
    JPanel drawingPane = new JPanel();

    public ShearText( String title ) {
	super(title);
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(drawingPane, BorderLayout.CENTER);
	setSize(400, 350);
	setVisible(true);
	ShearThread mthd = new ShearThread(drawingPane);
	mthd.start();	
    }

    public static void main(String argv[]) {
	new ShearText("Scaling Text");
    }
}

class ShearThread extends Thread {
    final  double PI = 3.14159;
    String s = "Shear & Scale";
    private JPanel p = null ;
    Graphics2D g = null ;
    Random rnd = new Random();
	ShearThread(JPanel p1) { p = p1; g = (Graphics2D) p.getGraphics();
		p.setBackground(Color.black); }
	public void run() {
          	int x = p.getWidth()/4, y = p.getHeight()/4 ;
	  	g.setFont( new Font("Roman", Font.BOLD, 50)) ;
	  	AffineTransform tfm = AffineTransform.getShearInstance(0, 0 );
		g.setXORMode( Color.white ) ;
		g.drawString(s , x, y);
	   while (true) {
		g.drawString(s , x, y);
		tfm.setToShear(rnd.nextInt() % 100 / 100.0, rnd.nextInt() % 100 / 100);
		tfm.scale(Math.random(), Math.random());  // Concatenate scale with shear.
		g.setTransform( tfm ) ;
		g.drawString(s , x, y);
		try { Thread.sleep(1000); } catch(InterruptedException e) {}
	   }
	}
}
	