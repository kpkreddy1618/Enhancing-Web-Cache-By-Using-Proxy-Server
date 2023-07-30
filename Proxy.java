import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
public class Proxy implements Runnable,ActionListener
{
	static HashMap<String, File> Cache;
	private ServerSocket ss;
	private volatile boolean running = true;
	static ArrayList<Thread> servicingThreads;
	Object ob = new Object();
	String com="",command="",s="";
	String Uname = "124003127";
	String Pword = "praveen";
	int Pwdcount=0;
	JFrame f1=new JFrame("Proxy");
	JLabel l1,l2,outputLabel,username,passwd,Header,l3;
	JRadioButton rb1,rb2;
	JButton b1,ok,Request_Received,connect,back,HTML_CONTENT;
	JPanel p1,p2,innerpanel,outputPanel,mainPanel,lastPanel,HTMLPanel,HTMLOutput;
	JTextField tf1;
	JTextArea ta1,ta2;
	final JPasswordField pf1;
	JScrollPane jp,jp1,jp2;
	JLabel l=new JLabel();
	public Proxy(int port) 
	{
		f1.setSize(400,400);
		f1.setLayout(new GridLayout(1,1));
		mainPanel=new JPanel();
		mainPanel.setLayout(null);
		Header =new JLabel("PROXY SERVER DEMO");
		Header.setBounds(120,10,200,50);
		Header.setForeground(Color.decode("#FFFFFF"));
		username=new JLabel("Username :");
		username.setForeground(Color.decode("#FFFFFF"));
		username.setBounds(70,100,80,30);
		passwd=new JLabel("Password :");
		passwd.setForeground(Color.decode("#FFFFFF"));
		passwd.setBounds(70,150,80,30);
		connect=new JButton("Connect");
		connect.setBounds(125,210,90,30);
		connect.setBackground(Color.decode("#9CC3D5"));
		connect.addActionListener(this);
		Image logo = new ImageIcon("logo.png").getImage();
		f1.setIconImage(logo);
		l3=new JLabel("Developed By Praveen Kumar Reddy.K");
		l3.setBounds(75,300,250,30);
		tf1=new JTextField();
		tf1.setBounds(170,100,120,30);
		pf1=new JPasswordField();
		pf1.setBounds(170,150,120,30);
		mainPanel.add(Header);
		mainPanel.add(username);
		mainPanel.add(tf1);
		mainPanel.add(passwd);
		mainPanel.add(pf1);
		mainPanel.add(connect);
		mainPanel.add(l3);
		p1 = new JPanel(); //cache panel
		p2=new JPanel();
		Request_Received=new JButton("RECEIVED REQUESTS");
		Request_Received.setBounds(80,230,200,30);
		back=new JButton("Back");
		innerpanel=new JPanel();
		outputPanel=new JPanel();
		outputLabel=new JLabel();
		lastPanel =new JPanel();
		lastPanel.setLayout(null);
		p1.setLayout(null);
		p1.setBackground(Color.decode("#D4ED91"));
		p2.setLayout(null);
		outputPanel.setLayout(new GridLayout(1,1));
		innerpanel.setLayout(new GridLayout(1,1));
		l1=new JLabel("Select the function");
		innerpanel.setBounds(50, 50, 300, 200);
		l1.setForeground(Color.decode("#000000"));
		ta1=new JTextArea(15,40);
		ta2=new JTextArea(15,40);
		HTML_CONTENT=new JButton("HTML RESPONSE");
		HTML_CONTENT.setBounds(80,280,200,30);
		HTML_CONTENT.setBackground(Color.decode("#9CC3D5"));
		HTML_CONTENT.addActionListener(this);
		l2=new JLabel();
		jp= new JScrollPane(l2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jp1= new JScrollPane(ta1,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jp2= new JScrollPane(ta2,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		outputPanel.add(jp1);
		outputPanel.setBounds(0,0,385,300);
		back.setBounds(150,320,100,30);
		back.setBackground(Color.decode("#000000"));
		back.setForeground(Color.decode("#FFFFFF"));
		back.addActionListener(this);
		lastPanel.add(outputPanel);
		HTMLPanel =new JPanel();
		HTMLPanel.setLayout(null);
		HTMLOutput =new JPanel();
		HTMLOutput.setLayout(new GridLayout(1,1));
		HTMLOutput.setBounds(0,0,385,300);
		HTMLOutput.add(jp2);
		HTMLPanel.add(back);
		HTMLPanel.add(HTMLOutput);
		innerpanel.add(jp);
		p2.add(innerpanel);
		ok=new JButton("OK");
		ok.setBounds(230,280,80,30);
		ok.setBackground(Color.decode("#000000"));
		ok.setForeground(Color.decode("#FFFFFF"));
		Request_Received.setBackground(Color.decode("#9CC3D5"));
		ok.addActionListener(this);
		Request_Received.addActionListener(this);
		p2.add(ok);
		p2.setBackground(Color.decode("#603F83"));
		String tasks[] = {"cached","close"};
		l1.setFont(new Font("Courier", Font.BOLD,18));
		l1.setBounds(70, 50, 250,20);
		rb1=new JRadioButton("Cache");
		rb1.setBounds(50,100,100,30);
		rb1.setBackground(Color.decode("#D4ED91"));
		rb1.setForeground(Color.decode("#990011"));
		rb2=new JRadioButton("Close Server");
		rb2.setBounds(50,130,100,30);
		rb2.setBackground(Color.decode("#D4ED91"));
		rb2.setForeground(Color.decode("#990011"));
		ButtonGroup bg=new ButtonGroup();
		bg.add(rb1);
		bg.add(rb2);
		b1=new JButton("SUBMIT");
		b1.setBounds(180,160,100,30);
		b1.setBackground(Color.decode("#9CC3D5"));
		b1.addActionListener(this);
		p1.add(l1);
		p1.add(rb1);
		p1.add(rb2);
		p1.add(b1);
		p1.add(Request_Received);
		p1.add(HTML_CONTENT);
		mainPanel.setBackground(Color.decode("#DB70DB"));
		f1.add(mainPanel);
		f1.setVisible(true);
		f1.setLocationRelativeTo(null);
		f1.setDefaultCloseOperation(f1.EXIT_ON_CLOSE);
		Cache = new HashMap<String,File>(); //Hashmap for caching sites
		servicingThreads = new ArrayList<Thread>(); // Create array list for servicing threads
		new Thread(this).start(); // Starts overriden run() method
		try{                      // Cached sites is loaded from file
			File Csites = new File("CachedSites.txt");
			if(!Csites.exists())
			{
				System.out.println("No cached sites are found- A new file is created");
				Csites.createNewFile();
			} 
			else 
			{
				FileInputStream fileInputStream = new FileInputStream(Csites);
				ObjectInputStream os = new ObjectInputStream(fileInputStream);
				Cache = (HashMap<String,File>)os.readObject();
				fileInputStream.close();
				os.close();
			}
		}catch(IOException e){} 
		catch(ClassNotFoundException e){}
		try{
			ss= new ServerSocket(port);
			ss.setSoTimeout(100000);
			System.out.println("Waiting for Client on port " + ss.getLocalPort());
			running = true;    
		}catch(SocketException se){    // Catch exceptions associated with opening socket
			System.out.println("Socket Exception when connecting to client");
			se.printStackTrace();
		}catch(SocketTimeoutException ste){
			System.out.println("Timeout occured while connecting to client");
		}catch(IOException io){
			System.out.println("IO exception when connecting to client");
		}
	}
	//Listens to port, accepting newer socket connections.
	// A new thread is created to handle request and continues to listen
	public void listen()
	{
		while(running)
		{
			try{
				Socket socket = ss.accept();
				Thread thread = new Thread(new Request(socket));
				synchronized (ob) 
				{
					servicingThreads.add(thread); // added for later references
				}
				thread.start();
			}catch(SocketException e){
				System.out.println("Server closed");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	//The cached sites states are saved and can be re loaded later
	//Joins currently running requests in Request Class
	private void closeServer()
	{
		System.out.println("\nClosing Server..");
		running = false;
		try{
			for(Thread thread : servicingThreads)
			{
				if(thread.isAlive())
				{
					System.out.print("Waiting on "+ thread.getId()+" to close..");
					thread.join();
					System.out.println(" closed");
				}
			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		try{
			System.out.println("Terminating Connection");
			ss.close();
			JOptionPane.showMessageDialog(f1, "Server closed Successfully!", "closing status",JOptionPane.PLAIN_MESSAGE);
		}catch(Exception e){
			System.out.println("Exception closing proxy's server socket");
			e.printStackTrace();
		}
	}
	//If Parameter URL is Present in Cache it is returned
	public static File getCachedPage(String url)
	{
		return Cache.get(url);
	}
	//Adds new entry to the Cache HashMap
	public static void addCache(String urlString, File fileToCache)
	{
		Cache.put(urlString, fileToCache);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==connect)
		{
			String s = new String(pf1.getPassword());
			if((Uname.equals(tf1.getText()))&& (Pword.equals(s)))
			{
				Pwdcount=0;
				f1.remove(mainPanel);
				f1.setContentPane(p1);
				f1.validate();
				f1.repaint();
			}
			else
			{
				if(Pwdcount<2)
				{
				JOptionPane.showMessageDialog(f1, "Invalid Username and Password", "",JOptionPane.ERROR_MESSAGE);
				}
				else
				{
				f1.dispose();
				System.exit(0);
				}
				Pwdcount++;
			}
		}
		if(e.getSource()==b1)
		{
			if(rb1.isSelected())
			{
				command="cached";
			}
			if(rb2.isSelected())
			{
				command="close";
			}
		}
		if(e.getSource()== ok)
		{
			f1.remove(p2);
			f1.setContentPane(p1);
			f1.validate();
			f1.repaint();
			l.setText("");
			try{
				FileOutputStream fileOutputStream = new FileOutputStream("CachedSites.txt");
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(Cache);
				objectOutputStream.close();
				fileOutputStream.close();
			}catch(Exception exp){}
		}
		if(e.getSource()== Request_Received)
		{
			lastPanel.add(back);
			f1.remove(p1);
			f1.setContentPane(lastPanel);
			f1.validate();
			f1.repaint();
		}
		if(e.getSource()== back)
		{ 
			f1.remove(HTMLPanel);
			f1.remove(lastPanel);
			f1.setContentPane(p1);
			f1.validate();
			f1.repaint();
		}
		if(e.getSource()== HTML_CONTENT)
		{
			HTMLPanel.add(back);
			f1.remove(p1);
			f1.setContentPane(HTMLPanel);
			f1.validate();
			f1.repaint();
		}
	}
	String str="";
	public void run() 
	{
		while(running)
		{
			if(command == "cached" || command == "close" )
			{
				if(command.toLowerCase().equals("cached"))
				{
					System.out.println("\nCurrently Cached Sites");
					for(String key : Cache.keySet())
					{
						str+="\n"+key;
					}
					System.out.println(str);
					l.setText("Currently cached Sites");
					l.setForeground(Color.decode("#FFFFFF"));
					l.setBounds(70,10,300,20);
					l.setFont(new Font("Baskerville Old Face", Font.BOLD,24));
					String[] list = str.split("\n");
					str="";
					String label = "<html>";
					for (int i = 0; i < list.length; i++)
					{
						System.out.println(list[i]);
						label = label + list[i] + "<br>";
					}
					label = label + "</html>";
					p2.add(l);
					l2.setText(label);
					l2.setFont(new Font("Courier", Font.PLAIN,16));
					f1.remove(p1);
					f1.setContentPane(p2);
					f1.validate();
					f1.repaint();
					System.out.println();
					command="";
				}
				else if(command.equals("close"))
				{
					running = false;
					closeServer();
					command = "";
				}
			}
		}
	}
	public static void main(String[] args) 
	{
		Proxy p= new Proxy(8085); // Create Proxy Instance
		p.listen();
	}
	class Request implements Runnable 
	{
		Socket CSocket; // Socket for client
		BufferedReader proxyread; //Reads data from client
		BufferedWriter proxywrite; //Writes data to client
		private Thread httpsClientToServer;
		public Request(Socket CSocket)
		{
			this.CSocket = CSocket;
			try{
				this.CSocket.setSoTimeout(2000);
				proxyread = new BufferedReader(new InputStreamReader(CSocket.getInputStream()));
				proxywrite = new BufferedWriter(new OutputStreamWriter(CSocket.getOutputStream()));
			}catch(IOException e){}
		}
		// Examines Request String
		public void run() 
		{
			String requestString;
			try{
			requestString =proxyread.readLine();
			}catch(IOException e){
				return;
			}
			String string=ta1.getText(); // request from client
			ta1.setText(string + "\n" + requestString);
			System.out.println("Request Received " + requestString);
			String request = requestString.substring(0,requestString.indexOf(' '));// obtains the request type like GET or CONNECT
			String urlString = requestString.substring(requestString.indexOf(' ')+1); //request type is removed and we get URL alone
			urlString = urlString.substring(0, urlString.indexOf(' ')); //HTTP version is removed
			if(!urlString.substring(0,4).equals("http"))
			{
				String temp = "http://"; //adding for CONNECT request
				urlString = temp + urlString;
			}
			if(request.equals("CONNECT"))
			{
				System.out.println("HTTPS Request for : " + urlString + "\n");
				handleHTTPSRequest(urlString);
			}
			else
			{
				// Check for a cached copy
				File file;
				if((file = Proxy.getCachedPage(urlString)) != null)
				{
					System.out.println("Cached Copy found for : " + urlString + "\n");
					sendcached(file);
				}
				else 
				{
					System.out.println("HTTP GET for : " + urlString + "\n");
					sendnoncached(urlString);
				}
			}
		}
		private void sendcached(File cachedFile)
		{
			try{
				String fileExtension = cachedFile.getName().substring(cachedFile.getName().lastIndexOf('.'));
				String response;
				if((fileExtension.contains(".png")) || fileExtension.contains(".jpg") || fileExtension.contains(".jpeg") || fileExtension.contains(".gif"))
				{
					BufferedImage image = ImageIO.read(cachedFile);
					if(image == null )
					{
						System.out.println("Image " + cachedFile.getName() + " was null");
						response = "HTTP/1.0 404 NOT FOUND \n" +"Proxy-agent: ProxyServer/1.0\n" +"\r\n";
						proxywrite.write(response);
						proxywrite.flush();
					} 
					else 
					{
						response = "HTTP/1.0 200 OK\n" +"Proxy-agent: ProxyServer/1.0\n" +"\r\n";
						proxywrite.write(response);
						proxywrite.flush();
						ImageIO.write(image, fileExtension.substring(1), CSocket.getOutputStream());
					}
				}
				//text response
				else 
				{
					BufferedReader cachedFileBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(cachedFile)));
					response = "HTTP/1.0 200 OK\n" +"Proxy-agent: ProxyServer/1.0\n" +"\r\n";
					proxywrite.write(response);
					proxywrite.flush();
					String line;
					while((line = cachedFileBufferedReader.readLine()) != null)
					{
						proxywrite.write(line);
						s="\n"+line ;
					}
					if(s.contains("HTML"))
					{
						System.out.println(s);
						ta2.setText(s);
					}
					proxywrite.flush();
					if(cachedFileBufferedReader != null)
					{
						cachedFileBufferedReader.close();
					}
				}
				if(proxywrite!= null)
				{
					proxywrite.close();
				}
			}catch(IOException e){}
		}
		private void sendnoncached(String urlString)
		{
			try{
				int fileExtensionIndex = urlString.lastIndexOf(".");
				String fileExtension;
				fileExtension = urlString.substring(fileExtensionIndex, urlString.length()); //we will get file type
				String fileName = urlString.substring(0,fileExtensionIndex); //here url WILL GET
				fileName = fileName.substring(fileName.indexOf('.')+1); // Trim off http://www. header content
				fileName = fileName.replace("/", "__");
				fileName = fileName.replace('.','_');
				if(fileExtension.contains("/"))
				{
					fileExtension = fileExtension.replace("/", "__");
					fileExtension = fileExtension.replace('.','_');
					fileExtension += ".html";
				}
				fileName = fileName + fileExtension;
				boolean caching = true;
				File fileToCache = null;
				BufferedWriter fileToCacheWrite = null;
				try{
					fileToCache = new File("cached" + fileName);
					if(!fileToCache.exists()){
					fileToCache.createNewFile();
					}
					fileToCacheWrite = new BufferedWriter(new FileWriter(fileToCache));
				}catch(IOException e){} 
				catch (NullPointerException e) {}
				if((fileExtension.contains(".png")) || fileExtension.contains(".jpg") || fileExtension.contains(".jpeg") || fileExtension.contains(".gif"))
				{
					URL remoteURL = new URL(urlString);
					BufferedImage image = ImageIO.read(remoteURL);
					if(image != null) 
					{
						ImageIO.write(image, fileExtension.substring(1), fileToCache);// The Image is cached to disk
						// Response to client
						String line = "HTTP/1.0 200 OK\n" +"Proxy-agent: ProxyServer/1.0\n" +"\r\n";
						proxywrite.write(line);
						proxywrite.flush();
						ImageIO.write(image, fileExtension.substring(1), CSocket.getOutputStream());
					} 
					else 
					{
						System.out.println("No image received from server"+ fileName);
						String error = "HTTP/1.0 404 NOT FOUND\n" +"Proxy-agent: ProxyServer/1.0\n" +"\r\n";
						proxywrite.write(error);
						proxywrite.flush();
						return;
					}
				}
				// text file
				else 
				{
					URL remoteURL = new URL(urlString);
					HttpURLConnection proxyToServer = (HttpURLConnection)remoteURL.openConnection();
					proxyToServer.setRequestProperty("Content-Type","application/x-www-form-urlencoded");//If a property with the key already exists, overwrite its value with the new value.
					proxyToServer.setRequestProperty("Content-Language", "en-US");
					proxyToServer.setUseCaches(false);//false, the protocol must always try to get a fresh copy of the object.
					proxyToServer.setDoOutput(true); // true indicates that the application intends to write data to the URL connection.
					BufferedReader proxyToServerRead = new BufferedReader(new InputStreamReader(proxyToServer.getInputStream()));
					String line = "HTTP/1.0 200 OK\n" +"Proxy-agent: ProxyServer/1.0\n" +"\r\n";
					proxywrite.write(line);
					while((line = proxyToServerRead.readLine()) != null)
					{
						proxywrite.write(line);
						s="\n"+line;
						if(caching)
						{
							fileToCacheWrite.write(line);
						}
						if(s.contains("HTML"))
						{
							System.out.println(s);
							ta2.setText(s);
						}
					}
					proxywrite.flush();
					if(proxyToServerRead != null)
					{
						proxyToServerRead.close();
					}
				}
				if(caching)
				{
					fileToCacheWrite.flush();
					Proxy.addCache(urlString, fileToCache);
				}
				if(fileToCacheWrite != null)
				{
					fileToCacheWrite.close();
				}
				if(proxywrite != null)
				{
					proxywrite.close();
				}
			}catch (Exception e){}
		}
		private void handleHTTPSRequest(String urlString)
		{
			String url = urlString.substring(7); //here we skip http://
			String pieces[] = url.split(":");    
			url = pieces[0];                   //we get URL ex: push.services.mozilla.com
			int port = Integer.valueOf(pieces[1]); //port num i.e 443
			try{  							//Read only first line of HTTPS
				for(int i=0;i<5;i++)
				{
					proxyread.readLine();      //buffered reader
				}
				InetAddress address = InetAddress.getByName(url); // Actual IP address of URL through DNS
				Socket proxyToServerSocket = new Socket(address, port);
				proxyToServerSocket.setSoTimeout(5000);
				String line = "HTTP/1.0 200 Connection established\r\n" +"Proxy-Agent: ProxyServer/1.0\r\n" +"\r\n";
				proxywrite.write(line);
				proxywrite.flush();
				BufferedWriter proxyToServerWrite = new BufferedWriter(new OutputStreamWriter(proxyToServerSocket.getOutputStream()));
				BufferedReader proxyToServerRead = new BufferedReader(new InputStreamReader(proxyToServerSocket.getInputStream()));
				//a new thread is created to listen to client and transmit to server
				ClientToServerHttpsCon clientToServerHttps =new ClientToServerHttpsCon(CSocket.getInputStream(), proxyToServerSocket.getOutputStream());
				httpsClientToServer = new Thread(clientToServerHttps);
				httpsClientToServer.start();
				try{
					byte[] buffer = new byte[32768];
					int read;
					do
					{
						read = proxyToServerSocket.getInputStream().read(buffer);
						if (read > 0) 
						{
							CSocket.getOutputStream().write(buffer, 0, read);
							ta2.setText(ta2.getText()+"\n"+new String(buffer));
							if (proxyToServerSocket.getInputStream().available() < 1) 
							{
								CSocket.getOutputStream().flush();
							}
						}
					}while (read >= 0);
				}catch(SocketTimeoutException e) {}
				catch (IOException e) {}
				if(proxyToServerSocket != null)
				{
					proxyToServerSocket.close();
				}
				if(proxyToServerRead != null)
				{
					proxyToServerRead.close();
				}
				if(proxyToServerWrite != null)
				{
					proxyToServerWrite.close();
				}
				if(proxywrite != null)
				{
					proxywrite.close();
				}
			}catch(SocketTimeoutException e){
				try{
					proxywrite.flush();
				}catch(IOException ioe){
					ioe.printStackTrace();
				}
			}
			catch(Exception e){
				System.out.println("Error on HTTPS : " + urlString );
				e.printStackTrace();
			}
		}
		// Transmits data from client to server
		class ClientToServerHttpsCon implements Runnable
		{
			InputStream proxyToClientIStream;// to receive data from client
			OutputStream proxyToServerOStream;// to transmit data to server
			public ClientToServerHttpsCon(InputStream proxyToClientIStream,OutputStream proxyToServerOStream) 
			{
				this.proxyToClientIStream = proxyToClientIStream;
				this.proxyToServerOStream = proxyToServerOStream;
			}
			public void run()
			{
				try{
					byte[] buffer = new byte[32768];
					int read;
					do 
					{
						read = proxyToClientIStream.read(buffer);
						if (read > 0) 
						{
							proxyToServerOStream.write(buffer, 0, read);
							if (proxyToClientIStream.available() < 1) 
							{
								proxyToServerOStream.flush();
							}
						}
					}while (read >= 0);
				}catch(SocketTimeoutException ste) {}
				catch(IOException e){}
			}
		}
	}
}