public class SmokerCons {

   static String ingredients[] = {"Agent", "tobacco", "paper", "matches"};
   static final int    none = -1, tobacco = 1, paper = 2, matches = 3; 

   public static void printMessage (int idx, String act, int sleepLen) {
	StringBuffer buf = new StringBuffer(200);

	for ( int i = 0; i < idx; i ++ ) buf.append("\t\t\t");
	if ( idx > 0 ) buf.append("S[" );
	else buf.append("A[" );

	buf.append(SmokerCons.ingredients[idx]+ "]: " + act );
	System.out.println(buf);
        // sleepX(sleepLen);
   }

   static void sleepX(int sleepLen) {
	try { Thread.sleep((int) (Math.random() * sleepLen) + sleepLen / 3 );
	} catch ( InterruptedException e ) { }
    }
}

