/**
*/

public class SortMerge {

   static final int SIZE = 20, RUN_SIZE = 5;
   int data[] = new int[SIZE];

   public SortMerge() {
	setValue( data );

        Array.showValue( data, 0, data.length) ;

	Thread t = new SortMergeThread(data, 0, data.length);
	t.start();
	try { t.join();  } catch (InterruptedException e) {}

        Array.showValue(data, 0, data.length);
	t.start() ;

   }

   public static void setValue( int [] data ) {
	for ( int i = 0; i < data.length; i ++ ) 
		data[i] = (int) (Math.random() * 1000 );
   }
 
   public static void main(String args[] ) {
	new SortMerge();
   }
}

class Array {
   public static void showValue( int [] data, int lb, int ub)  {
	System.out.println("\nArray contents:");
	for ( int i = lb ; i < ub; i++) {
		System.out.print( ScreenIO.expandString(4, ScreenIO.formatInt(data[i])));
		if ( (i- lb +1) % 10 == 0 ) System.out.println();
	}
   }
}

class SortMergeThread extends Thread {

   int data[], lb, ub;
   public SortMergeThread(int d[], int l, int u ) { data = d; lb = l; ub = u; }

   public void run() {
	Thread t = recSortMerge(data, lb, ub);
	try { t.join();  } catch (InterruptedException e) {}
   }
	
   Thread recSortMerge (int [] data, int lb, int ub) {
	// Base case: range is small not further division needed.
	if (( ub - lb ) <= SortMerge.RUN_SIZE ) {
		Thread t = new SortThread ( data, lb, ub ) ;
		t.start();
		return t;
        }

	int mid = ( ub + lb ) / 2 ;
	Thread t1 = recSortMerge(data, lb, mid); 
	Thread t2 = recSortMerge(data, mid, ub ); 
	Thread mt = new MergeThread(t1, t2, data, lb, mid, ub ); mt.start();
	return mt;
   }
}

class SortThread extends Thread {

   int data[], lb, ub;

   public SortThread( int d[], int l, int u ) { data = d; lb = l; ub = u; }

   public void run() {
	int t;
	for ( int i = lb; i < ub-1; i ++ ) 
		for ( int j = i + 1; j < ub ; j ++ ) 
			if (data[i] > data[j] ) {
				t = data[i]; data[i] = data[j]; data[j] = t; }

   }
}

class MergeThread extends Thread {

   int data[], lb, mid, ub;
   Thread t1, t2;

   public MergeThread( Thread x, Thread y, int[] d, int l, int m, int u) {
	t1 = x; t2 = y; data = d; lb = l; mid = m; ub = u; 
   }

   public void run() {
	try { 
		if ( t1 != null ) t1.join(); 
		if ( t2 != null ) t2.join();
	} catch (InterruptedException e) { }

	int tmp[] = new int[ub - lb];
	int i = lb, j = mid, k = 0;
	while ( i < mid && j < ub )
		if ( data[i] < data[j] ) tmp[k++] = data[i++];
		else tmp[k++] = data[j++];
	while ( i < mid ) tmp[k++] = data[i++];
	while ( j < ub ) tmp[k++] = data[j++];
	for ( k = 0, i = lb; i < ub; ) data[i++] = tmp[k++];

   }
}

