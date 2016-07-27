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

public class ScaleText extends CloseableJFrame {
    JPanel drawingPane = new JPanel();

    public ScaleText( String title ) {
	super(title);
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(drawingPane, BorderLayout.CENTER);
	setSize(450, 150);
	setBackground(Color.black);
	setVisible(true);
	ScaleThread mthd = new ScaleThread(drawingPane);
	mthd.start();	
    }

    public static void main(String argv[]) {
	new ScaleText("Scaling Text");
    }
}

class ScaleThread extends Thread {
    final  double PI = 3.14159;
    String s = "Scaling String ...";
    private JPanel p = null ;
    Graphics2D g = null ;
    boolean inc = true ;
	ScaleThread(JPanel p1) {p = p1; g = (Graphics2D) p.getGraphics();
			p.setBackground(Color.black); }
	public void run() {
	  	int i = 0;
          	int x = 20 , y = 40 ;
	  	g.setFont( new Font("Roman", Font.BOLD, 50)) ;
	  	AffineTransform tfm = new AffineTransform();
		g.setXORMode(new Color(255, 255, 255)) ;
	   while (true) {
		tfm.setToScale(0.02 * i, 0.02 * i);
		g.setTransform( tfm ) ;
		g.drawString(s , x+i, y+i);
		try { Thread.sleep(30 + i); } catch(InterruptedException e) {}
		g.drawString(s , x+i, y+i);
		i = i + (inc? 1 : -1);
		if ( i == 42 || i == 0 ) inc = !inc ; // change direction after i reach 0 or 150.
	   }
	}
}
	