/* The example shows
*/
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import javax.swing.border.* ;
import java.io.* ;
import java.net.* ;

class  ChatMouseEventHandler extends MouseAdapter implements MouseMotionListener {
	JTextArea ta = null;
	ChatMouseEventHandler ( JTextArea t ) { ta = t; }

	public void mouseDragged( MouseEvent e) {}
	public void mouseMoved ( MouseEvent e) {
		// ta.setText( "Mouse moved to: " + e.getPoint() + "\n") ;
	}
	
	public void mouseClicked (MouseEvent e) {
		ta.setText( "Mouse clicked: " + e.getPoint() + "\n") ; }
}

	
public class ChatroomClient extends JApplet implements ActionListener {

   String			hostAddress = "sleipnir.cs.csubak.edu";
   int				port = 8900;
   JLabel			lbNameMessages[] = { new JLabel("Name"), new JLabel("Message") };
   JTextField			txtNameMessages[] = { new JTextField(), new JTextField() };
   JTextArea			ta = new JTextArea() ;
   JPanel			plMessages[] = { new JPanel(), new JPanel() };
   JPanel			plSouth = new JPanel();
   JPanel			plUsers = new JPanel(/*new GridLayout(20, 1)*/ );
   // JPanel			plUsers = new JPanel( );
   Socket			chatSocket = null;
   ObjectOutputStream		out = null;  // char-based output derived from socket.
   ObjectInputStream		in  = null;  // char-based input stream derived from socket.
   boolean			connected = false;
   static JApplet		applet;

   JButton			addChatter = new JButton("Add");

   public ChatroomClient() { }
   
   public void init() {
	Container c = getContentPane();

	plUsers.setBorder( new TitledBorder("User List"));
	plUsers.setPreferredSize(new Dimension(50, 400));
	plUsers.add( new JCheckBox( "Huaqing" ) );
	plUsers.add( new JCheckBox( "John" ) );
	
	ChatMouseEventHandler hdlr =  new ChatMouseEventHandler ( ta ); 
	plUsers.addMouseListener( hdlr );
	plUsers.addMouseMotionListener( hdlr );

	c.add(plUsers, BorderLayout.EAST);

	plSouth.setLayout(new BorderLayout());
	c.setBackground(Color.black);
	ta.setBackground(Color.black);
	ta.setForeground(Color.white); 
	ta.setForeground(Color.white);
	for ( int i = 0; i < plMessages.length; i ++ ) {
			plMessages[i].setLayout(new FlowLayout());
			plMessages[i].add(lbNameMessages[i]);
			plMessages[i].add(txtNameMessages[i]);
			txtNameMessages[i].addActionListener(this);
	}
	txtNameMessages[0].setColumns(8);
	txtNameMessages[1].setColumns(30);
	txtNameMessages[0].setToolTipText("Enter name first and press ENTER");
	txtNameMessages[1].setToolTipText("Enter message to send and press ENTER");
	plSouth.add(plMessages[0], BorderLayout.WEST) ;
	plSouth.add(plMessages[1], BorderLayout.CENTER);

	addChatter.addActionListener(this);
	plSouth.add(addChatter, BorderLayout.EAST);

	ta.setFont(new Font("verdana", Font.BOLD, 12) );
	c.add(new JScrollPane(ta), BorderLayout.CENTER);
	c.add(plSouth, BorderLayout.SOUTH);

   }

   public void stop() {
     try {
	new ChatMessageSender( out, new ChatroomGeneralMessage(ChatroomCons.MSG_GENERAL,
				 txtNameMessages[0].getText().trim(), " Bye, everyone! I'm leaving.") ).start() ;
	try { Thread.sleep(100); } catch (InterruptedException e) {}
	System.out.println("stop called");
	if ( in != null ) in.close(); 
	if ( out != null ) out.close();
	if ( chatSocket != null ) chatSocket.close();
     } catch (IOException e ) {}
   }

   public void paint( Graphics g) { super.paint(g); 
   }

   void makeSocketConnection() {

	if ( connected ) return;

	if ( txtNameMessages[0].getText().trim().equals("") ) {
		JOptionPane.showMessageDialog(null, "Enter yur name and press ENTER!");
		return;
	}
	try {
		chatSocket = new Socket(hostAddress, port); 
		out = new ObjectOutputStream ( chatSocket.getOutputStream());
		in  = new ObjectInputStream( chatSocket.getInputStream());
		new ChatMessageReceiver(in, ta).start();
		new ChatMessageSender( out, new ChatroomGeneralMessage(ChatroomCons.MSG_GENERAL,
			txtNameMessages[0].getText().trim(), " Howdy, everyone!") ).start() ;
		connected = true;
	} catch(IOException er) { er.toString(); }

   }

   static int k = 1;

   public void actionPerformed(ActionEvent e ) {
	Object obj = e.getSource();
	if ( obj == addChatter ) {
	    /*
	    if ( ( k )  % 5 == 0 ) { plUsers.removeAll() ;
		plUsers.revalidate(); plUsers.repaint(); } 
		*/
	    plUsers.add(new JCheckBox("CK Box" + k++, k % 2 == 0) );
	    plUsers.revalidate();
	    return;
	}

	if ( obj == txtNameMessages[1] ) {
		String s = txtNameMessages[1].getText().trim();
	
		if ( txtNameMessages[0].getText().trim().equals("") ) {
			JOptionPane.showMessageDialog(null, "Enter yur name and press ENTER!");
			return;
		}
		if ( s.equals("") ) return;
		Thread thr = new ChatMessageSender( out, new ChatroomGeneralMessage(ChatroomCons.MSG_GENERAL,
			txtNameMessages[0].getText().trim(), s));
		thr.start() ;
		try { thr.join(); } catch (InterruptedException f )  { }

		txtNameMessages[1].setText("");
	}
	else if ( obj == txtNameMessages[0] ) {
		makeSocketConnection();
	}
		
   }

   public static void main( String arg[] ) {
	JFrame fm =  new JFrame () ;
	fm.setBackground(Color.black);
	applet = new ChatroomClient();
	applet.init();
	fm.setTitle("Chatroom Client: Running as appliction.") ;
	fm.addWindowListener( new WindowAdapter () {
		public void windowClosing(WindowEvent e) {
			System.out.println("Before stop called");
			applet.stop();
			System.exit(0); } } );
	fm.getContentPane().add( applet );
	fm.setSize( 600, 400);
	fm.setVisible(true);
  }
}


class ChatMessageSender extends Thread {

   ObjectOutputStream	out;  // OutputStreamWrite derived from Socket.
   ChatroomGeneralMessage	mObj;  // Message from a chat room client to other clients

   public ChatMessageSender ( ObjectOutputStream w, ChatroomGeneralMessage s ) { out = w; mObj = s; } 

   public void run() {
	if ( out == null ) return;
	try {
		out.writeObject(mObj);
 	} catch( IOException e ) { }
   }
}


class ChatMessageReceiver extends Thread {

   ObjectInputStream		in = null;
   JTextArea			ta = null;
   
   public ChatMessageReceiver ( ObjectInputStream i, JTextArea a) { in = i;  ta = a ; } 

   public void run() {
	try {
		ChatroomGeneralMessage msg = null;
		while ( true ) {
			// Color colors[] = {Color.white, Color.yellow}; 
			// System.out.println("Receiver is waiting.");
			try { msg = (ChatroomGeneralMessage) in.readObject() ;
			} catch (ClassNotFoundException nfdEx) {}
			if ( ta != null ) {
				// ta.setForeground( colors[colorCode]); 
				ta.append(msg + "\n");
				if ( ta.getText().length() > 0 ) ta.setCaretPosition(ta.getText().length()  - 1) ;
				// colorCode = (colorCode + 1) % 2;
			}
			// System.out.println("Message received: " + s);
		}
 	} catch( IOException e ) {  }
   }
}
