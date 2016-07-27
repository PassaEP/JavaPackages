
import java.awt.event.* ;
import javax.swing.* ;

public class CloseableJFrame extends JFrame 
{  
   // An anonymous class is defined to declare a window hanndle class object.
   WindowAdapter wHandler = new WindowAdapter() {
	public void windowClosing(WindowEvent e) { dispose(); System.exit(0); }
    }; 
   
   public CloseableJFrame() { super() ; addWindowListener( wHandler) ; }
   public CloseableJFrame (String s) { super( s  ) ; addWindowListener(  wHandler ); }

    protected void finalize() throws Throwable  {
	try { super.finalize(); } finally { System.exit(-1); } }
}
