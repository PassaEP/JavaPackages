public class Simplest {

	public static void main( String args[] ) {
		if ( args.length < 1 ) {
			System.out.printf("\nRunn as java Simplest # !\n");
			return;
	    }
		int n =  Integer.parseInt( args[0] );

		for ( int i = 0; i < n; i ++ )
			new Thread( new SimpleThread( i + 1)).start();

		
	}

	public class SimpleThread implements Runnable {
		int ID ;
		public SimpleThread ( int id ) {
			ID = id;
		}

		// Entry for a SimpleThread class object, and should be
		// call minMain( String args[]) ).
		public void run() {
			while ( true ) {
				System.out.printf("I am a SimpleThread object with ID = %d\n", ID );
			}
		}
	}
}
