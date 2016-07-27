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

public class TransformDemo extends CloseableJFrame {
    final  double PI = 3.14159;
    JPanel drawingPane = new JPanel();

    public TransformDemo( String title ) {
	super(title);
	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(drawingPane, BorderLayout.CENTER);
	drawingPane.setBackground(Color.black);
	setSize(400, 300);
	setVisible(true);
    }

    public void paint(Graphics g1 ) {
	super.paint(g1);
	Graphics2D g = (Graphics2D) drawingPane.getGraphics();
	g.setFont( new Font("Roman", Font.BOLD, 30) );
	for (int i = 1; i < 8; i ++) {
		g.setTransform( AffineTransform.getRotateInstance(PI/4*i,
				Math.random() * (drawingPane.getWidth() -200) + 100,
				Math.random() * (drawingPane.getHeight() -200) + 100 ) ) ;
		g.setPaint( new Color( (int) (Math.random() * 255),
			(int) (Math.random() * 255), (int)(Math.random() * 255))) ;
		g.drawString("Hello", drawingPane.getWidth()/2, drawingPane.getHeight()/2);
	}
    }

    public static void main(String argv[]) {
	new TransformDemo("Transform");
    }
}