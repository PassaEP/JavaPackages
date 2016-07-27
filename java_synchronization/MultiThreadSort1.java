/** Multiple Thread Selection Sort:

   Why multithread? Compare this program with SingleThread sorting program, you
will find:
	1. The single thread sorting program will block when "Sort" button is
	   pressed. The single execution/thread must wait until sorting is
	   done. The other button cannot be pushed during sorting.
        2. With multithread, each button press will generate a thread which
	   will call shuffle, sort or generate function defined in monitor. The
	   button will not held during time-consuming sorting process.
	   The button presses start the corresponding functions started asynchronously.
	   The started functions will be coordinated by monitor.
        3. Comment all "synchronized" out and run the program, clicking the buttons
	   alternatively. You will see the sorting process will be intervined by
	   other actions.
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class MultiThreadSort1 extends CloseableJFrame implements ActionListener {

   JButton btnSort[] = { new JButton("Generate"), 
			new JButton("Sort"), new JButton("Shuffle") };
   JPanel  panSouth = new JPanel();
   Canvas  drawingBoard = new Canvas();
   MultiSort1Monitor sortMtr;
   int	   data[] = new int [SortCons.ARRAY_SIZE];

   public MultiThreadSort1() {
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
	sortMtr = new MultiSort1Monitor( data, drawingBoard );
   }

   public void actionPerformed ( ActionEvent e ) {
	int k = -1;
	Object o = e.getSource();
	for ( int i = 0; i < btnSort.length; i ++ ) {
		if ( o == btnSort[i] ) { k = i; break; }
	}
	switch ( k ) {
		case 0: new MultiThreadSorter1(sortMtr, 0).start(); break;
		case 1: new MultiThreadSorter1(sortMtr, 1).start(); break;
		case 2: new MultiThreadSorter1(sortMtr, 2).start(); break;
	}
   }

   public void paint(Graphics g) {
	super.paint(g);
   }

   public static void main( String argv[] ) {
	new MultiThreadSort1();
   }
}

class MultiThreadSorter1 extends Thread {
   MultiSort1Monitor mtr;
   int action;

   public  MultiThreadSorter1 (MultiSort1Monitor m, int act) {
	action = act; mtr = m;
   }

   public void run() {
	switch ( action ) {
		case 0: mtr.generateData(); break;
		case 1: mtr.sort(); break;
		case 2: mtr.shuffle(); break;
	}
   }
}

class MultiSort1Monitor {

   int data[] ;
   Canvas cvs;
   Graphics2D g2d;
   Random rnd = new Random();
   Color bkColor = Color.black, lineColor = Color.white, rectColor = Color.yellow;
   int cvsWidth, cvsHeight, drawingWidth, drawingHeight;

   float gap;
   MultiSort1Monitor ( int [] d, Canvas v ) {
	data = d; cvs = v; g2d = (Graphics2D) v.getGraphics();
	cvs.setBackground(bkColor);
	g2d.setColor(Color.white); g2d.setFont( new Font("Aril", Font.BOLD, 14)) ;
   }

   public synchronized void generateData() {
	for ( int i = 0; i < data.length; i ++ )
		data[i] = rnd.nextInt( SortCons.MAX_INT );
	showData();
   }


   public synchronized void sort() {
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

   public synchronized void showData() {

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

   public synchronized void shuffle() {
	int k, tmp;
	for ( int i = 0; i < data.length; i ++ ) {
		k = rnd.nextInt(data.length);
		tmp = data[i]; data[i] = data[k]; data[k] = tmp;
	}
	showData();
   }
}

