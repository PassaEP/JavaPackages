public class MyThreadV2 {

   public static void main(String ags[]) {

        Thread t = null;
	int 	num = 4;
	SharedVariableV2 sv = new SharedVariableV2( num );
 	for ( int i = 0; i < num; i++ ) {

		t = new SimpleThreadV2( i, sv ) ;
		t.start();
  	}
   }
}

class SimpleThreadV2 extends Thread {
   int ID;
   SharedVariableV2 sResource;
   public SimpleThreadV2 ( int i, SharedVariableV2 s ) {
	ID = i; sResource = s;
   }

   public void run() {
	while ( true ) {
	   try {
		System.out.println( "Thread : " + ID + " tries to get its turn.\n");

		sResource.waitForMyTurn( ID ) ;

		// System.out.println("\u001b[1m");
		System.out.println("Thread " + ID + ": Haha..., it is my turn.\n");

		Thread.sleep( (int) ( Math.random() * 1000 + 2000 ) );

		System.out.println( "Thread " + ID + ": OK..., next one!.\n");

		sResource.letOtherIn();

	   } catch (Exception e) {}
	}
   }

}

class SharedVariableV2 {

   int limit = 0;
   int turn = 0;
   public SharedVariableV2( int lim ) { limit = lim; turn = lim - 1; }
   synchronized public void letOtherIn() {
	turn = (int) (Math.random() * limit ) % limit;
	System.out.println( ScreenIO.formatString(80, "\t\tNEXT LUCK GUY: " + turn + "\n"));
	notifyAll();
   }
   synchronized public void waitForMyTurn ( int ID ) {
	while ( turn != ID ) {
		System.out.println( "Thread " + ID + ": Oh Oh ..., it is NOT my turn.\n");
		// Don't add notify() or notifyAll() here!
		try { wait(); } catch (Exception e) {}
	}
   }
}
