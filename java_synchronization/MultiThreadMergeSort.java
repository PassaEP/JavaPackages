/** Multiple Thread 2-Way Merger Sort:

    Merge sort is an useful internal sorting algorithm. The algorithm sorts
small portions of data first and then merge the sorted portions into a
larger sorted portions. The sorting will be done by repeatedly merging
portion into larger one until one portion left.

   In Merge Sort, we will
	1. divide arrays into portions with initial run-size, and let one thread to
	   sort one portion.
	2. merge two adjacent sorted portions of into a doubled portion after the
	   two adjacent portions are sorted. One thread will be used to merge two
	   adjacent portions into a bigger one. No blocking between two merge threads.
	3. Repeatedly, use thread to merge two larger portions into even bigger
	   one until the merged portion is the whole array. 
   Notice that
	1. Threads for sorting portions of array need no synchronization among
	   themself.
	2. A thread for merging two portions must wait until two threads that sort
	   the two portions finish. To make a thread A wait for another thread, B,
	   to finish call B.join() A thread. 
	3. Thread for merging the merged portions by other two merging threads should
	   be synchronized.
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class MultiThreadMergeSort extends JApplet implements ActionListener {

   JButton btnSort[] = { new JButton("Generate"), 
			new JButton("Sort"), new JButton("Shuffle") };
   JPanel  panSouth = new JPanel();
   static  Canvas  drawingBoard = new Canvas();
   int	   data[] = new int [SortCons.ARRAY_SIZE];

   GenerateDataThread generateDataThread = null;
   ShuffleDataThread  shuffleDataThread = null;
   MergeSortThread    mergeSortThread = null;

   public MultiThreadMergeSort() {
   }
 
   public void init() {
	Container c = getContentPane();
	c.setSize(650, 500);

	c.setLayout(new BorderLayout() );
	for ( int i = 0; i < btnSort.length; i ++ ) {
		btnSort[i].addActionListener(this);
		if ( i > 0 )  btnSort[i].setEnabled(false);
		panSouth.add(btnSort[i]);
	}
	c.add(panSouth, BorderLayout.SOUTH);
	c.add(drawingBoard, BorderLayout.CENTER);
	drawingBoard.setBackground(Color.black);
   }

   public void start() {
   }

   public void actionPerformed ( ActionEvent e ) {
	int k = -1;
	Object o = e.getSource();
	for ( int i = 0; i < btnSort.length; i ++ ) {
		if ( o == btnSort[i] ) { k = i; break; }
	}

	switch ( k ) {
		case 0: generateDataThread = new GenerateDataThread(
				mergeSortThread, shuffleDataThread, data);
			generateDataThread.start();
			for ( int i = 1; i < 3; i++) btnSort[i].setEnabled(true) ;
			break;
		case 1: mergeSortThread = new MergeSortThread (
				generateDataThread, shuffleDataThread, data);
			mergeSortThread.start() ; break;
		case 2: shuffleDataThread = new ShuffleDataThread (
				generateDataThread, mergeSortThread, data);
			shuffleDataThread.start(); break;
	}
   }

   //public void paint(Graphics g) { super.paint(g); }

   public static void main( String argv[] ) {
	JFrame fmObj = new JFrame("Multithreaded 2-Way Merge Sort");
	fmObj.addWindowListener( new  WindowAdapter() {
			public void windowClosing( WindowEvent e ) { System.exit(0); } } );
	fmObj.setSize(600, 500);
	MultiThreadMergeSort apObj = new MultiThreadMergeSort();
	apObj.init();
	fmObj.getContentPane().add(apObj);
	fmObj.setVisible(true);
	apObj.start();
   }
}

class MergeSortThread extends Thread {
   int data[];
   Thread thrd1, thrd2 = null;
   public MergeSortThread (Thread t1, Thread t2, int d[] ) {
	thrd1 = t1; thrd2 = t2; data = d;
   }

   public void run () {
	try { if ( thrd1 != null ) thrd1.join(); if (thrd2 != null ) thrd2.join();  }
	catch (InterruptedException e) {}
	Thread t = mergeSort(0, data.length);
	new DrawLinesThread(data, 0, data.length).start();
	try { t.join(); } catch ( InterruptedException e) {}
	try { sleep(1000); } catch (InterruptedException e) {}
   }

   private Thread mergeSort(int lb, int ub ) {

	if ( (ub - lb ) <= SortCons.PORTION_SIZE ) { 
		SortThread s = new SortThread(data, lb, ub);
		s.start();
		return s;
	} else {
		int k = (ub - lb ) / 2; 
		Thread th1 = mergeSort(lb, lb + k);
		Thread th2 = mergeSort(lb + k, ub);
		MergeThread m = new MergeThread(th1, th2, data, lb, lb + k, lb + k, ub);
		m.start();
		return m;
	       }
   }
		
}

class SortThread extends Thread {
   int data[], lb, ub;

   SortThread( int [] d, int l, int u ) { data = d; lb = l; ub = u; }

   public void run () {
	
	try { sleep((int) (Math.random() * 2000) + 500); } catch (InterruptedException e) {}
	int k, tmp;
	for ( int i = lb; i < ub - 1; i ++ )  {
		k = i;
		for (int j = i + 1; j < ub; j ++ ) 
			if ( data[k] > data[j] ) k = j;
		if ( k != i ) { tmp = data[i]; data[i] = data[k]; data[k] = tmp; }
	}
	new DrawLinesThread(data, lb, ub).start();

   }
}

class MergeThread extends Thread {

   int data[], lb1, ub1, lb2, ub2;
   Thread thrd1 = null, thrd2 = null;

   public MergeThread(Thread t1, Thread t2, int [] d, int l1, int u1, int l2, int u2) {
	thrd1 = t1; thrd2 = t2;
	data = d; lb1 = l1; ub1 = u1; lb2 = l2; ub2 = u2;
   }

   // -----------------------------------------------------------
   // Merge two adjacent part of array: data[lb1 .. ub1] and 
   // data[lb2 .. ub2]. The ub1 == lb2, then two parts are adjacent.
   // -----------------------------------------------------------
   public void run () {

	// if ( ub1 != lb2  || lb1 > ub1 || lb2 > ub2 ) return ;

	try { if ( thrd1 != null ) thrd1.join(); if (thrd2 != null ) thrd2.join();  }
	catch (InterruptedException e) {}

	int d[] = new int[ ub2 - lb1]; //there are better algorithms existed for merging.

	try { sleep((int) (Math.random() * 2000) + 500); } catch (InterruptedException e) {}

	int i = lb1, j = lb2, k = 0;
	while ( i < ub1 || j < ub2 ) {
		if ( i >= ub1 ) { d[k++] = data[j++]; }
		else if ( j >= ub2 ) { d[k++] = data[i++]; }
			else if ( data[i] < data[j] ) d[k++] = data[i++];
				else  d[k++] = data[j++];
	}
	// copy merged and sorted larger portion back.
	for ( i = lb1, k = 0; i < ub2; i++, k++ ) data[i] = d[k];
	new DrawLinesThread(data, lb1, ub2).start();
   }
}

class DrawLinesThread extends Thread {
   int data[], lb, ub;
   Color rectColor = Color.yellow, lineColor = Color.white, backgroundColor = Color.black;
   Canvas cvs = MultiThreadMergeSort.drawingBoard;
   Graphics2D g2d;

   String title = "Concurrent Sorting & Concurrent Merging at Same Levels";
   FontMetrics  fm;

   DrawLinesThread( int [] d, int l, int u ) {
	data = d; lb = l; ub = u; g2d = (Graphics2D) cvs.getGraphics();
	g2d.setFont( new Font("TimesRoman", Font.BOLD, 14));
   }

   public void run() {
	int cvsWidth = cvs.getWidth(), cvsHeight = cvs.getHeight();
	int drawingWidth = cvs.getWidth() - SortCons.RIGHT_INSET - SortCons.LEFT_INSET;
 	int drawingHeight = cvs.getHeight() - SortCons.TOP_INSET - SortCons.BOTTOM_INSET;
	float gap = (float) drawingWidth / (SortCons.ARRAY_SIZE + 1);

	// g2d.clearRect(0, 0, drawingWidth, drawingHeight);
	g2d.setColor(rectColor);
	g2d.drawRect( SortCons.LEFT_INSET, SortCons.TOP_INSET, drawingWidth, drawingHeight);
	fm = g2d.getFontMetrics();
	g2d.drawString(title, (cvsWidth - fm.stringWidth(title)) / 2, SortCons.TOP_INSET-5);
	if ( lb == 0 && ub == SortCons.ARRAY_SIZE ) lineColor = Color.white;
	else lineColor = generateColor();

	for ( int i = lb; i < ub; i ++ ) {
		// Erase a line
		g2d.setColor(backgroundColor);
		g2d.drawLine((int ) ( (i + 1)  * gap + SortCons.LEFT_INSET),
			cvsHeight - SortCons.BOTTOM_INSET,
			(int ) ( (i + 1)  * gap + SortCons.LEFT_INSET), 
			cvsHeight - SortCons.BOTTOM_INSET -  drawingHeight + 1 );
		// draw a line.
		if ( (lb != 0 || ub != SortCons.ARRAY_SIZE) && ( i == ub - 1 || i == SortCons.ARRAY_SIZE - 1))
			g2d.setColor(Color.magenta);
		else g2d.setColor( lineColor );

		g2d.drawLine((int ) ( (i + 1)  * gap + SortCons.LEFT_INSET),
			cvsHeight - SortCons.BOTTOM_INSET,
			(int ) ( (i + 1)  * gap + SortCons.LEFT_INSET), 
			cvsHeight - SortCons.BOTTOM_INSET - 
			(int) ((float) data[i] / SortCons.MAX_INT * drawingHeight) );
		try { sleep( data.length * 5 / (ub - lb)); } catch (InterruptedException e) {}
	}
   }

   Color generateColor() {
	float r = 0, g = 0, b = 0;
	while ( r+g+b < 0.5 )  {
		r = (float) Math.random(); g = (float) Math.random(); b = (float) Math.random(); }
	return new Color(r, g, b);
  }
	
}

class GenerateDataThread extends Thread {
   int [] data;
   Thread thrd1 = null, thrd2 = null;

   public GenerateDataThread(Thread t1, Thread t2, int [] d ) {
   	thrd1 = t1; thrd2 = t2; data = d;
   }

   public void run() {
	Random rnd = new Random();
	try { if ( thrd1 != null ) thrd1.join(); if (thrd2 != null ) thrd2.join();  }
	catch (InterruptedException e) {}
	for ( int i = 0; i < data.length; i ++ )
		data[i] = rnd.nextInt( SortCons.MAX_INT );
	DrawLinesThread t = new DrawLinesThread( data, 0, data.length );
	t.start();
	try { t.join(); } catch (InterruptedException e) {}
   }
}

class ShuffleDataThread extends Thread {
   int [] data;
   Thread thrd1 = null, thrd2 = null;

   public ShuffleDataThread(Thread t1, Thread t2, int [] d ) {
   	thrd1 = t1; thrd2 = t2; data = d;
   }

   public void run() {
	Random rnd = new Random();
	int tmp, k;

	try { if ( thrd1 != null ) thrd1.join(); if (thrd2 != null ) thrd2.join();  }
	catch (InterruptedException e) {}

	for ( int i = 0; i < data.length; i ++ ) {
		k = rnd.nextInt( data.length );
		tmp = data[i]; data[i] = data[k]; data[k] = tmp;
	}
	DrawLinesThread t = new DrawLinesThread( data, 0, data.length );
	t.start();
	try { t.join(); } catch (InterruptedException e) {}
   }
}
