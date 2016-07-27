/** The exmaples shows the transformation, rotation, and shearing.
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

public class TransformDemo2 extends CloseableJFrame {
    JPanel drawingPane = new JPanel();

    public TransformDemo2( String title ) {
	super(title);
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(drawingPane, BorderLayout.CENTER);
	setSize(400, 300);
	setVisible(true);
	MyThread mthd = new MyThread(drawingPane);
	mthd.start();	
    }

    public void paint(Graphics g1 ) {
	super.paint(g1);
    }

    public static void main(String argv[]) {
	new TransformDemo2("Transform");
    }
}

class MyThread extends Thread {
    final  double PI = 3.14159;
    private JPanel p = null ;
    Graphics2D g = null ;
    boolean needErase = false;
	MyThread(JPanel p1) { p = p1; g = (Graphics2D) p.getGraphics(); p.setBackground(Color.black); }
	public void run() {
	  int i = 0;
          int x = p.getWidth()/2, y = p.getHeight()/2 ;
	  AffineTransform tfm = AffineTransform.getRotateInstance(0, x, y );
	  g.setFont( new Font("roman", Font.BOLD, 10)) ;
	  g.setXORMode(Color.blue);
	  while ( true )  {
	    try {

		if ( needErase) g.drawString("Java 2D" ,x, y);
		else needErase = true ; 
		tfm.setToRotation(PI/16 * i, x, y) ;
		g.setTransform( tfm ) ;

		g.drawString("Java 2D" , x, y);
		i = (i + 1) % 16;
		Thread.sleep(100);
	   } catch ( InterruptedException e ) { System.out.println("Waked up"); }
	  }
	}
}
	