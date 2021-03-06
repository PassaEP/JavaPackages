public class MyThread {

   public static void main(String ags[]) {

        Thread t = null;
	int 	num = 4;
	SharedVariable sv = new SharedVariable( num );
 	for ( int i = 0; i < num; i++ ) {

		t = new SimpleThread( i, sv ) ;
		t.start();
  	}
   }
}

class SimpleThread extends Thread {
   int ID;
   SharedVariable sResource;
   public SimpleThread ( int i, SharedVariable s ) {
	ID = i; sResource = s;
   }

   public void run() {
	 while ( true ) {
	   try {
		synchronized (sResource) {
	    	    while ( ! sResource.isMyTurn(ID) ) {
			System.out.println( ScreenIO.makeString(' ', ID * 10) + 
			   "Thread : " + ID + " before notify and wait.\n");
			System.out.flush();
		 	sResource.notify();
			System.out.println( ScreenIO.makeString(' ', ID * 10) + 
			   "Thread : " + ID + " before wait and after notify.\n");
			sResource.wait() ;
		    }
		// System.out.println("\u001b[1m");
		System.out.println( ScreenIO.makeString(' ', ID * 10) + 
			"Thread : " + ID + " is having CPU.\n");
		Thread.sleep( (int) ( Math.random() * 1000 + 2000 ) );
		sResource.letOtherIn();
		sResource.notify();
		}
	   } catch (Exception e) {}
	}
   }

}

class SharedVariable {

   int limit = 0;
   int turn = 0;
   public SharedVariable( int lim ) { limit = lim; turn = lim - 1; }
   synchronized public void letOtherIn() {
	turn = (int) (Math.random() * limit ) % limit;
   }
   synchronized public boolean isMyTurn ( int ID) {
	return turn == ID;
   }
}
