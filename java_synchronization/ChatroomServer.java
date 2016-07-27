import java.io.* ;
import java.net.* ;
import java.util.* ;

/** Chatroom: Additional features can be added to Chatroom server:
	List chat rooms/groups (names, topics, nnumber of chaters, starting
   times of groups).
	List names of a chaters in a group/room.
	List names in your group.
	Send private message individual in the same group.
	Ignore loud mouth.
	Start a new group.
	Control the group that you started (band individual).
	Locate an individual in all groups.
	Automatic notification of joining and leaving the group.
	Make chat's client software as applet.

    The server starts a thread to receive one client message. An improved
 architecture can be used in which one thread is started to receive message
 from a group of clients so that less system resources will be consumed.
    To let a single thread to receive messages from a group of users, a
 vector of InputStream and optional ObjectInputStream are passed to the
 thread. The thread loops throught each of inputstream object to see
 whether input is available on the stream. If there is no input, the
 thread tests next stream. If there are input, the thread reads the input
 and pass the message to brocater.

    For a big chatroom, more than one thread can be started to receive
 messages from client. Each thread will handle N clients.

*/
public class ChatroomServer {
   ServerSocket		svrSocket;
   Broadcaster		broadcaster = new Broadcaster();

   ChatroomServer() { }

   public static void main( String arg[] ) {
	ChatroomServer fm =  new ChatroomServer() ;

	// ----------------------------------------------------------------
	// Create a server socket listenning on port 8900 which is 
	// special port number that allows client and server programs run
	// on the same machine. However, TCP/IP must be installed on that
	// machine even if the client and server are running on the same
	// machine.
	// ----------------------------------------------------------------
	try { fm.svrSocket = new ServerSocket(8900); }
	catch (IOException e) { e.printStackTrace(); }

	try { System.out.println("Host IP Address: " + 
				InetAddress.getLocalHost().getHostAddress() +
			     " \tHost name: " + InetAddress.getLocalHost().getHostName() );
	} catch (UnknownHostException e) {}

	while (true) {
	    try {
		Socket chaterSocket = fm.svrSocket.accept() ;
		// System.out.println("A connection is made");
		new ChaterAgent(fm.broadcaster, chaterSocket).start();
	    }
	    catch (IOException e) { e.printStackTrace(); }
	}
  }
}

// -------------------------------------------------------------------------
// ChaterAgent is a agent who is reseponsible to collect message from 
// chat client and broadcast to other chaters in the group.
// -------------------------------------------------------------------------
class ChaterAgent extends Thread {
   Socket			chaterSocket;
   int				clientN;
   ObjectInputStream		in;
   ObjectOutputStream		out;
   Broadcaster			bctr;

   public ChaterAgent(Broadcaster b, Socket s) {
	bctr = b; chaterSocket = s; 
	try {
		in  = new ObjectInputStream (chaterSocket.getInputStream());
		out  = new ObjectOutputStream (chaterSocket.getOutputStream());
		bctr.add(out);
	} catch (IOException e ) {}
   }

   public void run() {
	ChatroomGeneralMessage mObj = null ;
	while ( true ) {
	   try {
		try { mObj = (ChatroomGeneralMessage) in.readObject();
		} catch (ClassNotFoundException nfd) {}
		if ( mObj == null ) { chaterSocket.close(); return ; }
		System.out.println("Message received: " + mObj);
		bctr.broadcastMessage(mObj);
	   } catch (IOException e) { bctr.remove(out); return ;}
	}
   }
}

class Broadcaster {
   Vector<ObjectOutputStream>		outs = new Vector<ObjectOutputStream>(5);

   public Broadcaster( ) { }

   public void add( ObjectOutputStream o ) { outs.add(o); }
   public void remove( ObjectOutputStream o ) { outs.remove(o); }

   public synchronized void broadcastMessage( ChatroomGeneralMessage mObj ) {
	Iterator itr = outs.iterator();
	ObjectOutputStream out ;
	if( mObj == null ) return;
	System.out.printf("Number of Output Objects: %d\n",  outs.size() );
	while ( itr.hasNext() ) {
	   try {
		out = (ObjectOutputStream) itr.next();
		out.writeObject(mObj);
		out.flush();
	   } catch (IOException e) {}
	}
   }
}
