/** 
	
*/

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.geom.*;

public class D2Draw extends CloseableJFrame implements ChangeListener {
	JSlider sds[] = { new JSlider(JSlider.VERTICAL, 0, 255, 125),
			  new JSlider(JSlider.HORIZONTAL, 0, 255, 125),
			  new JSlider(JSlider.VERTICAL, 0, 255, 125) };
	String  strColor[] = { "Red", "Green", "Blue"};
	JLabel  lbs[] = { new JLabel("Red    "), new JLabel("Green  "), new JLabel("Blue   ") };
	JPanel   pn = new JPanel() ;
	JPanel   sliderPane = new JPanel();
	JPanel   labPane = new JPanel();

	public D2Draw(String title) {
		super(title);
		sliderPane.setLayout(new GridLayout(1, 3) ) ;
		for ( int i = 0 ; i < lbs.length; i ++ ) {
			labPane.add(lbs[i]);
		}
		for ( int i = 0 ; i < lbs.length; i ++ ) {	
			sds[i].addChangeListener(this);
			sds[i].setToolTipText(strColor[i]);	// set ToolTipText()
		}
		getContentPane().setLayout(new BorderLayout(10, 10) );
		getContentPane().add(sds[0], BorderLayout.WEST);
		getContentPane().add(sds[1], BorderLayout.NORTH);
		getContentPane().add(sds[2], BorderLayout.EAST);
		getContentPane().add(labPane, BorderLayout.SOUTH);
		getContentPane().add(pn, BorderLayout.CENTER);
		pn.setToolTipText("Show Color");
		setSize(250, 250);
		setVisible(true);
	}

	public void stateChanged(ChangeEvent e) {
		for ( int i = 0; i < lbs.length; i ++ )
			lbs[i].setText(strColor[i] + ": " + sds[i].getValue());
		repaint();
	}

	public void paint( Graphics g1) {
		super.paint(g1);
		Graphics2D g = (Graphics2D) pn.getGraphics();
		g.setStroke(new BasicStroke(3.0f));
		g.setPaint(new Color(sds[0].getValue(), sds[1].getValue(), sds[2].getValue()));
		g.draw( new Ellipse2D.Double (1,1, pn.getWidth()-4, pn.getHeight()-4));
	}
	public static void main(String argv[]) {
		new D2Draw("Java 2D Draw");
	}
}
		

	