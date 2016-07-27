/** Single Threaded Sorting:
    When a program is single thread (single execution sequence), the program
can only do one job at a time. You cannot submit another job until the
current one is finished if jobs are requested by user through input device.

    The program use single thread show you that once sort button is clicked,
not generate or shuffle button can be clicked until sort is finished. Multithreaded
programs allows you submit one job after another without waiting for previous submitted
jobs to finish.
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class SingleThreadSort extends CloseableJFrame implements ActionListener {

   JButton btnSort[] = { new JButton("Generate"), new JButton("Sort"), new JButton("Shuffle") };
   JPanel  panSouth = new JPanel();
   Canvas  drawingBoard = new Canvas();
   SingleThreadSorter sorter;
   int	   data[] = new int [SortCons.ARRAY_SIZE];

   public SingleThreadSort() {
	super("Single Thread Selection Sort");
	Container c = getContentPane();
	setSize(600, 500);

	c.setLayout(new BorderLayout() );
	for ( int i = 0; i < btnSort.length; i ++ ) {
		btnSort[i].addActionListener(this);
		panSouth.add(btnSort[i]);
	}
	c.add(panSouth, BorderLayout.SOUTH);
	c.add(drawingBoard, BorderLayout.CENTER);
	setVisible( true );
	sorter = new SingleThreadSorter( data, drawingBoard );
   }

   public void actionPerformed ( ActionEvent e ) {
	int k = -1;
	Object o = e.getSource();
	for ( int i = 0; i < btnSort.length; i ++ ) {
		if ( o == btnSort[i] ) { k = i; break; }
	}
	switch ( k ) {
		case 0: sorter.generateData(); break;
		case 1: sorter.sort(); break;
		case 2: sorter.shuffle(); break;
	}
   }

   public void paint(Graphics g) {
	super.paint(g);
   }

   public static void main( String argv[] ) {
	new SingleThreadSort();
   }
}

class SingleThreadSorter {

   int data[] ;
   Canvas cvs;
   Graphics2D g2d;
   Random rnd = new Random();
   Color bkColor = Color.black, lineColor = Color.white, rectColor = Color.yellow;
   int cvsWidth, cvsHeight, drawingWidth, drawingHeight;
   float gap;

   SingleThreadSorter ( int [] d, Canvas v ) {
	data = d; cvs = v; g2d = (Graphics2D) v.getGraphics();
	cvs.setBackground(bkColor);
	g2d.setColor(Color.white); g2d.setFont( new Font("Aril", Font.BOLD, 14));
   }

   public void generateData() {
	for ( int i = 0; i < data.length; i ++ )
		data[i] = rnd.nextInt( SortCons.MAX_INT );
	showData();
   }

   public void sort() {
	int k, tmp ;
	for ( int i = 0; i < data.length - 1; i ++ ) {
		k  = i;
		for ( int j = i + 1; j < data.length; j ++ ) 
			if ( data[j] < data[k] ) k = j;
		// flashLine(i, Color.green, data[i]);
		// flashLine(k, Color.green, data[k]);
		flash2Lines(i, k, data[i], data[k], Color.green);
		tmp = data[i]; data[i] = data[k]; data[k] = tmp;
		showData();
		try {Thread.sleep(400); } catch (InterruptedException e) {}
	}
   }

   public void shuffle() {
	int k, tmp;
	for ( int i = 0; i < data.length; i ++ ) {
		k = rnd.nextInt(data.length);
		tmp = data[i]; data[i] = data[k]; data[k] = tmp;
	}
	showData();
   }
   public void showData() {
	setDrawingParameter();

	// g2d.clearRect(0, 0, drawingWidth, drawingHeight);
	g2d.setColor(rectColor);
	g2d.drawRect( SortCons.LEFT_INSET, SortCons.TOP_INSET, drawingWidth, drawingHeight);
	g2d.drawString("Simple Sort Algorithm and Single Threaded Code: An function starts after the other completes.",
			50, SortCons.TOP_INSET - 5);
	for ( int i = 0; i < SortCons.ARRAY_SIZE; i ++ ) {
		drawLineAt(i, bkColor, SortCons.MAX_INT - 1); // Erase a line
		drawLineAt(i, lineColor, data[i]); // draw a line.
		/*  Display data in text.
		System.out.print( ScreenIO.expandString(ScreenIO.formatInt(data[i]), 5));
		if ( (i + 1) % 20 == 0 ) System.out.println("");
		*/
	}
   }

   private void flash2Lines( int line1, int line2, int height1, int height2, Color clr) {
	int i = SortCons.FLASH_TIMES;
	while ( i-- > 0 ) {
		drawLineAt(line1, bkColor, SortCons.MAX_INT - 1);
		drawLineAt(line2, bkColor, SortCons.MAX_INT - 1);
		try { Thread.sleep(SortCons.FLASH_LENGTH); } catch (InterruptedException e) {}
		drawLineAt(line1, clr, height1);
		drawLineAt(line2, clr, height2);
		try { Thread.sleep(SortCons.FLASH_LENGTH); } catch (InterruptedException e) {}
	}
   }

   private void flashLine( int line, int height, Color clr) {
	int i = SortCons.FLASH_TIMES;
	while ( i-- > 0 ) {
		drawLineAt(line, bkColor, SortCons.MAX_INT - 1);
		try { Thread.sleep(SortCons.FLASH_LENGTH); } catch (InterruptedException e) {}
		drawLineAt(line, clr, height);
		try { Thread.sleep(SortCons.FLASH_LENGTH); } catch (InterruptedException e) {}
	}
   }

   private void setDrawingParameter() {
	cvsWidth = cvs.getWidth(); cvsHeight = cvs.getHeight();
	drawingWidth = cvs.getWidth() - SortCons.RIGHT_INSET - SortCons.LEFT_INSET;
	drawingHeight = cvs.getHeight() - SortCons.TOP_INSET - SortCons.BOTTOM_INSET;
	gap = (float) drawingWidth / SortCons.ARRAY_SIZE;
   }

   private void drawLineAt( int line, Color lineColor, int height) {
	// Erase a line
	g2d.setColor(bkColor);
	g2d.drawLine((int ) ( (line + 0.5)  * gap + SortCons.LEFT_INSET) , cvsHeight - SortCons.BOTTOM_INSET,
		     (int ) ( (line + 0.5)  * gap + SortCons.LEFT_INSET), 
		     cvsHeight - SortCons.BOTTOM_INSET - 
		     (int) ((float) height / SortCons.MAX_INT * drawingHeight) );
	// draw a line.
	g2d.setColor(lineColor);
	g2d.drawLine((int ) ( (line + 0.5)  * gap + SortCons.LEFT_INSET) , cvsHeight - SortCons.BOTTOM_INSET,
		     (int ) ( (line + 0.5)  * gap + SortCons.LEFT_INSET), 
		     cvsHeight - SortCons.BOTTOM_INSET - 
		     (int) ((float) height / SortCons.MAX_INT * drawingHeight) );
   }
	
}
