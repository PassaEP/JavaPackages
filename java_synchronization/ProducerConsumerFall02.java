import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ProducerConsumerFall02
	extends JFrame
{
	BurgerTrayPanel burgerTray = null;
	ProducerPanel producerPanel = null;
	ConsumerPanel consumerPanel = null;	
	JPanel buttonPanel = null;
	PCMonitor monitor = null;
	PCManager manager = null;
	
	public ProducerConsumerFall02()
	{
		setTitle("Producer &  Consumer Problem: Adaptive Burger Store");
		setSize(650, 600);
		
		addWindowListener(
			new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{
					System.exit(0);
				}
			}
		);
		
		addComponentListener(
			new ComponentAdapter()
			{
				public void componentResized(ComponentEvent e)
				{
					producerPanel.setSize(getWidth()/2-5, 300);
					consumerPanel.setSize(getWidth()/2-5, 300);
					validate();
				}
			}
		);
		
		monitor = new PCMonitor(this);
		burgerTray = new BurgerTrayPanel(monitor);
		burgerTray.setSize(getWidth(), 100);
		burgerTray.setForeground(Color.green);
		burgerTray.setBackground(Color.black);

		manager = new PCManager(this);
		producerPanel = new ProducerPanel(manager.producers);
		producerPanel.setSize(getWidth()/2-4, 300);
		producerPanel.setForeground(Color.orange);
		producerPanel.setBackground(Color.black);
		
		consumerPanel = new ConsumerPanel(manager.consumers);
		consumerPanel.setSize(getWidth()/2-4, 300);
		consumerPanel.setForeground(Color.black);
		consumerPanel.setBackground(Color.black);
		
		buttonPanel = new JPanel();
		buttonPanel.setSize(600, 40);	
		JButton add = new JButton("Add Consumer");
		add.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					manager.addConsumer();
				}
			}
		);
		
		JButton remove = new JButton("Remove Consumer");
		remove.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					manager.removeConsumer();
				}	
			}
		);

		JButton speedUp = new JButton("Speed Up");
		speedUp.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					manager.speedUp();
				}
			}
		);

		JButton speedDown = new JButton("Slow Down");
		speedDown.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					manager.speedDown();
				}
			}
		);

		buttonPanel.add(add);
		buttonPanel.add(remove);
		buttonPanel.add(speedUp);
		buttonPanel.add(speedDown);
				
		Container c = getContentPane();
		c.setBackground( Color.yellow);
		c.add(burgerTray, BorderLayout.NORTH);
		c.add(producerPanel, BorderLayout.WEST);
		c.add(consumerPanel, BorderLayout.EAST);
		c.add(buttonPanel, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.out.println("Usage java ProducerConsumerFall02 # #");
			System.exit(1);
		}		
		ProducerConsumerFall02 app = new ProducerConsumerFall02();
		
		int p = Integer.parseInt(args[0]);
		int c = Integer.parseInt(args[1]);
		for (int i = 0; i < c; i++) app.manager.addConsumer();
		for (int i = 0; i < p; i++) app.manager.addProducer();
		
	}
}

class PCLib {
	static int addedPro = 0;
	static int removedPro = 0;
	static int addedCon = 0;
	static int removedCon = 0;

	static int PRODUCERTIME = 800;
	static int CONSUMERTIME = 10000;
	
	public static void drawHeading(Graphics g, int width, String heading)
	{
		g.drawString(heading, (width - heading.length() * 5)/2, 20);
	}
	
	public static void drawSubHeading(Graphics g, int width, String[] subHead)
	{
		int unit = (width - 40) / subHead.length;
	
		for (int i = 0; i < subHead.length; i++)
			g.drawString(subHead[i], 20 + unit * i, 40);
	}

	public static void drawOneLine(JPanel pn, Color c, int ID, int cnt, String status, int bnum) {
		int width = pn.getWidth();
		int unit = (width - 60) / 4;
		Graphics g = pn.getGraphics();
		// g.setColor( new Color(220, 220, 220) );	
		g.setColor( Color.black);	
		g.fillRect(10, ID * 20 + 45 - 12 , width - 20, 18);
		g.setColor( c );
		g.setFont(new Font("roman", Font.BOLD, 12));
		g.drawString(String.valueOf(ID), 20, ID * 20 + 45);
		g.drawString(String.valueOf(cnt), 20 + unit, ID * 20 + 45);
		g.drawString(status, 20 + 2 * unit, ID * 20 + 45);
		g.drawString(String.valueOf(bnum), 20 + 3 * unit + 20, ID * 20 + 45);
	}
	
	public static void eraseOneLine(JPanel pn, int ID ) {
		int width = pn.getWidth();
		int unit = (width - 60) / 4;
		Graphics g = pn.getGraphics();
		g.setColor( pn.getBackground () );	
		g.fillRect(10, ID * 20 + 45 - 12 , width - 20, 18);
	}
}

class BurgerTrayPanel extends JPanel {

	PCMonitor monitor = null;
	
	public BurgerTrayPanel(PCMonitor m)
	{
		// Border rab = BorderFactory.createRaisedBevelBorder( );
		// TitledBorder title = new TitledBorder(rab, "Burger Tray");
		Border etb = BorderFactory.createEtchedBorder( EtchedBorder.RAISED );
		TitledBorder title = new TitledBorder(etb, "Burger Tray");
		title.setTitleFont( new Font("roman", Font.BOLD, 16));
		title.setTitleColor( Color.orange);
		title.setTitlePosition( TitledBorder.ABOVE_TOP);
		title.setTitleJustification (TitledBorder.CENTER);
		setBorder( title);
		monitor = m;	
	}
		
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.GREEN);
		int width = getWidth();
		g.drawRect ((width - 200)/2 - 1, 35 - 1, 202, 12);
		monitor.drawCurrent(g);		
	}
		
	public void drawPCTBar(Graphics g, int pct)
	{
		int width = getWidth();
		g.setFont(new Font("roman", Font.BOLD, 10));
		g.setColor( Color.green);
		g.fillRect((width - 200)/2 , 35, 200, 10);
		g.setColor(Color.orange);
		g.fillRect((width - 200)/2, 35, 200*pct/100, 10);
		g.setColor(getBackground());
		g.fillRect((width-200)/2+210, 42 - 11, 55, 30);
		g.setColor(Color.orange);
		g.drawString(pct + "%", (width-200)/2+210, 42);
	}
	
	public void drawItem(Graphics g, int[] tray, int idx)
	{
		int width = getWidth();
		int unit = (width - 100) / tray.length;
		
		g.setColor(getBackground());
		g.fillRect(50 + unit * idx, 75 - 18, unit, 30);
		g.setFont( new Font("roman", Font.BOLD, 11));
		if ( tray[idx] != 0 ) g.setColor(Color.orange);
		else g.setColor(Color.green);
		g.drawString(String.valueOf(tray[idx]), 50 + unit * idx, 75);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(getWidth(), getHeight());
	}
}

class PCMonitor {
	int[] tray = null;
	int in = 0, 
		out = 0, 
		count = 0;
	int nextItem = 0;
	ProducerConsumerFall02 app = null;
	int maxCount = 0;
	int minCount = 0;
	
	public PCMonitor(ProducerConsumerFall02 a)
	{
		app = a;
		tray = new int[10];
	}
	
	synchronized void printAddRemoveProducers (JPanel panel ) {
		Graphics g = panel.getGraphics();
		g.setFont( new Font("roman", Font.BOLD, 12));
		g.setColor( panel.getBackground() );
		g.fillRect(50, panel.getHeight() - 20 - 16, panel.getWidth() - 50 - 20, 20);
		g.setColor( Color.orange );
		g.drawString ("Producers [added: " + PCLib.addedPro + ",  removed: " + PCLib.removedPro + "]", 50, panel.getHeight() - 20);
	}

	synchronized void  printAddRemoveConsumers( JPanel panel ) {
		Graphics g = panel.getGraphics();
		g.setFont( new Font("roman", Font.BOLD, 12));
		g.setColor( panel.getBackground() );
		g.fillRect (50, panel.getHeight() - 20 - 16, panel.getWidth() - 50 - 20, 20);
		g.setColor( Color.green);	
		g.drawString ("Consumers [added: " + PCLib.addedCon + ",  removed: " + PCLib.removedCon + "]", 50, panel.getHeight() - 20);
	}

	public void drawCurrent(Graphics g)
	{
		BurgerTrayPanel panel = app.burgerTray;
		panel.drawPCTBar(g, getPCT());
		for (int i = 0; i < tray.length; i++)
			panel.drawItem(g, tray, i);
	}
	
	public int getPCT()
	{
		int pct = 0;
		for (int i = 0; i < tray.length; i++)
			if (tray[i] != 0)
				pct += 10;
		
		return pct;
	}
	
	synchronized public int putItem()
	{
		while (count >= tray.length)
		{
			try { notifyAll(); wait(); } catch (InterruptedException iex) {}
		}
		int idx = in++;
		tray[idx] = ++nextItem;
		in %= tray.length;
		++count;
		int pct = getPCT();
		if (pct >= 100) ++maxCount;
		if (maxCount >= 5) {
			app.manager.removeProducer();
			maxCount = 0;
		}
		BurgerTrayPanel panel = app.burgerTray;
		Graphics graphics = panel.getGraphics();		
		panel.drawPCTBar(graphics, pct);
		panel.drawItem(graphics, tray, idx);
		notifyAll();
		
		return nextItem;
	}
	
	synchronized public int getItem()
	{
		while (count <= 0) {
			try { wait(); notifyAll(); } catch (InterruptedException iex) {}
		} 
		int idx = out++;
		int t = tray[idx];
		tray[idx] = 0;
		out %= tray.length;
		--count;	
		int pct = getPCT();
		if (pct <= 0) ++minCount;
		if (minCount >= 5) {
			app.manager.addProducer();
			minCount = 0;
		}	
		BurgerTrayPanel panel = app.burgerTray;
		Graphics graphics = panel.getGraphics();
		panel.drawPCTBar(graphics, getPCT());
		panel.drawItem(graphics, tray, idx);
		notifyAll();	
		
		return t;
	}
}

class ProducerPanel extends JPanel {

	Vector<Thread> producers = null;

	public ProducerPanel(Vector<Thread> ps)
	{
		// Border rab = BorderFactory.createRaisedBevelBorder( );
		// TitledBorder title = new TitledBorder(rab, "Producer");
		Border etb = BorderFactory.createEtchedBorder( EtchedBorder.RAISED );
		TitledBorder title = new TitledBorder(etb);
		title.setTitleColor( Color.orange );
		title.setTitleFont( new Font( "roman", Font.BOLD, 16));
		title.setTitle("Producers");
		title.setTitlePosition( TitledBorder.ABOVE_TOP);
		title.setTitleJustification (TitledBorder.CENTER);
		setBorder( title );
		producers = ps;
	}

	synchronized public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.orange);
		g.setFont(new Font("roman", Font.BOLD, 12));
		String[] subHead = {"No.", "Total", "Status", "Last Made"};
		PCLib.drawSubHeading(g, getWidth(), subHead);
		Iterator it = producers.iterator();
		g.setFont(new Font("roman", Font.BOLD, 12));
		while (it.hasNext())
		{
			Producer p = (Producer)it.next();
			p.drawCurrent(g);
		}
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(getWidth(), getHeight());
	}

}

class ConsumerPanel extends JPanel {

	Vector<Thread> consumers = null;

	public ConsumerPanel(Vector<Thread> cs)
	{
		Border etb = BorderFactory.createEtchedBorder( EtchedBorder.RAISED );
		TitledBorder title = new TitledBorder(etb);
		title.setTitleFont( new Font ("roman", Font.BOLD, 16));
		title.setTitleColor( Color.green);
		title.setTitle("Consumers");
		title.setTitlePosition( TitledBorder.ABOVE_TOP);
		title.setTitleJustification (TitledBorder.CENTER);
		setBorder( title );
		consumers = cs;
	}

	public void paintComponent(Graphics g)
	{	
		super.paintComponent(g);
		g.setColor(Color.green);
		g.setFont(new Font("roman", Font.BOLD, 12));
		String[] subHead = {"No.", "Total", "Status", "Last Eaten"};
		PCLib.drawSubHeading(g, getWidth(), subHead);
		Iterator it = consumers.iterator();
		while (it.hasNext())
		{
			Consumer c = (Consumer)it.next();
			c.drawCurrent(g);
		}
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(getWidth(), getHeight());
	}
}

class Producer extends Thread
{
	PCManager manager = null;
	int pID = 0, bnum = -1, total=0;
	String status = "Making";	
	PCMonitor monitor = null;
	ProducerPanel panel = null;
	
	public Producer(PCManager man,  PCMonitor m, ProducerPanel p, int id)
	{
		manager = man;
		pID = id;
		monitor = m;
		panel = p;
	}
	
	public void drawCurrent(Graphics g)
	{
		PCLib.drawOneLine(panel, Color.orange, pID, total, status, bnum);
	}

	public void run()
	{	PCLib.addedPro ++;
		monitor.printAddRemoveProducers(panel);
		while (manager.isProducer(this))
		{
			making();
			PCLib.drawOneLine(panel, Color.orange, pID, total, "On queue", bnum);
			try { sleep(500); } catch (Exception e) {}
			bnum = monitor.putItem();
			total++;
			made( bnum );
			relaxing();
		}
		PCLib.removedPro ++;
		monitor.printAddRemoveProducers(panel);
		PCLib.eraseOneLine(panel, pID);
		return;
	}
	
	private void making()
	{
		if (!manager.isProducer(this)) return;
		try 
		{
			status = "Making";
			PCLib.drawOneLine(panel, Color.orange, pID, total, status, bnum);
			int maketime = PCLib.PRODUCERTIME * 60 / 100;
			sleep((int)(Math.random() * 0.4 * maketime + 0.8 * maketime));
		}
		catch (InterruptedException ie)
		{}
	}

	public void made(int bnum)
	{
		if (!manager.isProducer(this)) return;
		try 
		{
			status = "made";
			PCLib.drawOneLine(panel, Color.orange, pID, total, status, bnum);
			int maketime = PCLib.PRODUCERTIME * 3 / 100;
			sleep((int)(Math.random() * 0.4 * maketime + 0.8 * maketime));
		}
		catch (InterruptedException ie)
		{}
	}
	
	private void relaxing()
	{
		if (!manager.isProducer(this)) return;
		try
		{
			status = "Relaxing";
			PCLib.drawOneLine(panel, Color.orange, pID, total,  status, bnum);
			int relaxtime = PCLib.PRODUCERTIME * 10 / 100;
			sleep((int)(Math.random() * 0.4 * relaxtime + 0.8 * relaxtime));	
		}
		catch (InterruptedException ie)
		{}
	}
}

class Consumer extends Thread {

	PCManager manager = null;
	PCMonitor monitor = null;
	ConsumerPanel panel = null;
	int cID = 0, bnum = -1;
	String status = "Working";
	int	  burgersConsumed = 0;
	
	public Consumer(PCManager man, PCMonitor m, ConsumerPanel p, int id)
	{
		manager = man;		
		monitor = m;
		cID = id;
		panel = p;
	}
	
	public void run()
	{
		PCLib.addedCon++;
		monitor.printAddRemoveConsumers( panel);
		while (manager.isConsumer(this))
		{
			working();
			PCLib.drawOneLine(panel, Color.green, cID, burgersConsumed, "On queue", bnum);
			try { sleep(500); } catch (Exception e) {}
			bnum = monitor.getItem();
			eating(bnum);
			burgersConsumed ++;
			resting();
		        monitor.printAddRemoveConsumers( panel );
		}
		PCLib.removedCon++;
		monitor.printAddRemoveConsumers( panel );
		PCLib.eraseOneLine(panel, cID);
		return;
	}

	public void drawCurrent(Graphics g)
	{
		PCLib.drawOneLine(panel, Color.green, cID, burgersConsumed, status, bnum);
	}
	
	private void working()
	{
		if (!manager.isConsumer(this)) return;
		try 
		{
			status = "Working";
			PCLib.drawOneLine(panel, Color.green, cID, burgersConsumed, status, bnum);
			int worktime = PCLib.CONSUMERTIME * 60 / 100;
			sleep((int)(Math.random() * 0.4 * worktime + 0.8 * worktime));
		}
		catch (InterruptedException ie)
		{}
	}
	
	public void eating (int bnum)
	{
		if (!manager.isConsumer(this)) return;
		try 
		{
			status = "Eating";			
			PCLib.drawOneLine(panel, Color.green, cID, burgersConsumed, status, bnum);
	
		
			int eattime = PCLib.CONSUMERTIME * 20 / 100;
			sleep((int)(Math.random() * 0.4 * eattime + 0.8 * eattime));
		}
		catch (InterruptedException ie)
		{}	
	}
	
	private void resting()
	{
		if (!manager.isConsumer(this)) return;
		try
		{
			status = "Resting";
			PCLib.drawOneLine(panel, Color.green, cID, burgersConsumed, status, bnum);
			int resttime = PCLib.CONSUMERTIME * 30 / 100;
			sleep((int)(Math.random() * 0.4 * resttime + 0.8 * resttime));
		}
		catch (InterruptedException ie)
		{}
	}
}

class PCManager {

	int nextProducer = 0;
	int nextConsumer = 0;
	ProducerConsumerFall02 app = null;
	Vector<Thread> producers = new Vector<Thread>();
	Vector<Thread> consumers = new Vector<Thread>();
	
	public PCManager(ProducerConsumerFall02 a)
	{
		app = a;
	}
	
	public void addProducer()
	{
		Thread t = new Producer(this, app.monitor, app.producerPanel, ++nextProducer);
		producers.addElement(t);
		t.start();
	}
	
	public void removeProducer()
	{
		if (producers.size() <= 1)
			return;
		--nextProducer;
		producers.removeElementAt(producers.size() - 1);
	}
	
	public void addConsumer()
	{
		Thread t = new Consumer(this, app.monitor, app.consumerPanel, ++nextConsumer);	
		consumers.addElement(t);
		t.start();
	}
	
	public void removeConsumer()
	{
		if (consumers.size() <= 1)
			return;
		--nextConsumer;
		consumers.removeElementAt(consumers.size() - 1);
	}

	public void speedDown()
	{
		PCLib.PRODUCERTIME = PCLib.PRODUCERTIME * 120 / 100;
		PCLib.CONSUMERTIME = PCLib.CONSUMERTIME * 120 / 100;
	}

	public void speedUp()
	{
		PCLib.PRODUCERTIME = PCLib.PRODUCERTIME * 80 / 100;
		PCLib.CONSUMERTIME = PCLib.CONSUMERTIME * 80 / 100;
		PCLib.PRODUCERTIME = PCLib.PRODUCERTIME <= 0 ? 1000 : PCLib.PRODUCERTIME;
		PCLib.CONSUMERTIME = PCLib.CONSUMERTIME <= 0 ? 3000 : PCLib.CONSUMERTIME; 
	}
	
	public boolean isProducer(Object t)
	{
		return producers.contains(t);	
	}
	
	public boolean isConsumer(Object t)
	{
		return consumers.contains(t);
	}
}
