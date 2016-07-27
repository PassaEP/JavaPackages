import javax.swing.*;
import java.awt.*;

public class TestDrawCircle extends JFrame {

   JPanel     dwPane = new JPanel();
   Graphics   grp = null;
   int	      width,height;

   public TestDrawCircle() {
	Container c = getContentPane();
	c.add(dwPane);
	setSize(600, 500);
	setVisible( true );
	grp = dwPane.getGraphics();
	dwPane.setBackground( Color.black );
	grp.setXORMode( Color.red );
	width = dwPane.getWidth();
	height = dwPane.getHeight();

	try { Thread.sleep ( 1000 ) ; } catch (InterruptedException e) {} 
	for ( int i = 0; i < 50 ; i ++) {
		Thread crl = new DrawCircle(grp, width, height);
		crl.start() ;
	}
   }
	
   public static void main (String ags[] ) {
	new TestDrawCircle();
   }
}
