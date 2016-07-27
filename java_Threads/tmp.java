
class Merger implements Runnable {
    int a[] = null;
    ObjectInputStream objIn[] = new ObjectInputStream[2];
   
    public Merger(int []a, PipedInputStream pIn1, PipedInputStream pIn2) {
	this.a = a;		
	try {	
    	 objIn[0] = new ObjectInputStream ( pIn1 );
    	 objIn[1] = new ObjectInputStream ( pIn2 );
	} catch (IOException e ) { e.printStackTrace();}
   }

   public void run() {
       int value[] = new int[2];
       int i = 0, next = 0, errorCount = 0; 

	while ( true ) {
	  if ( i == 0 ) {
	      try { 
       	  	value[0] = objIn[0].readInt();
       	   	value[1] = objIn[1].readInt();
	        next =  value[0] <= value[1] ? 0 : 1;
	      } catch (IOException e) { e.printStackTrace(); }
	   }
	     else 
		try { 
		   value[next] = objIn[next].readInt(); 
	           if ( errorCount == 0 ) next =  value[0] <= value[1] ? 0 : 1;
		} catch( IOException e2) { 
			errorCount ++;
			if ( errorCount > 1 ) return;
			next = ( next + 1) % 2;
		}	

	      
	     a[i] = value[next];
	     i ++ ;
       }
   }
}
    
