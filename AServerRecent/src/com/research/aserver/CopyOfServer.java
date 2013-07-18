//package com.research.aserver;
//
//import java.util.ArrayList;
//import java.util.Enumeration;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.SocketException;
//import java.util.Iterator;
//
//
//public class CopyOfServer {
//	
//	ArrayList clientOutputStreams;
//	private static String SERVERIP;
//	static TwoWaySerialComm serialCom;
//	static MultiServoState mState;
//	
//	/*
//	 * @param args
//	 * @throws IOException
//	 */
//	public static void main(String[] args) throws IOException, InterruptedException
//	{
//		String clientSentence;
//		
//		ServerSocket welcomeSocket = new ServerSocket(8888);
//		SERVERIP = getLocalIpAddress();
//		
//		System.out.println("Connected and waiting for client input!\n");
//		System.out.println("Listening on IP: " + SERVERIP +"\n\n");
//		
//		serialCom = new TwoWaySerialComm();
//		try {
//			serialCom.connect("COM5");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		mState = new MultiServoState(serialCom);
//		
//		while(true)
//		{
//			Socket connectionSocket = welcomeSocket.accept();
//			
//			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
//			
//			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
//			
//			clientSentence = inFromClient.readLine();
//			
//			String ip = connectionSocket.getInetAddress().toString().substring(1);
//			
//			System.out.println("In from client ("+ip+"): "+clientSentence);
//			
//			if(clientSentence != null)
//			{
//				char c;
//				int[] degrees = new int[6];
//				StringBuilder sb = new StringBuilder(4);
//				int j = 0;
//				boolean help = false;
//				
//				for (int i = 0; i < clientSentence.length(); i++){
//				    c = clientSentence.charAt(i);
//				    if (Character.isDigit(c)) {
//				    	sb.append(c);
//				    	help = true;
//				    }
//				    if (!Character.isDigit(c) && help == true ) {
//				    	degrees[j] = Integer.parseInt(sb.toString());
//				    	j++;
//				    	help = false;
//				    	sb.delete(0, sb.length());
//				    }
//				}
//				mState.runServo(degrees[0], degrees[1], degrees[2], degrees[3], degrees[4], degrees[5]);
////				capitalizedSentence = clientSentence.toUpperCase() + '\n';
////				System.out.println("Out to client ("+ip+"): "+capitalizedSentence);
////				outToClient.writeBytes(capitalizedSentence +"\n");
//			}
//		}
//	}
//	
//	private static String getLocalIpAddress()
//	{
//		try {
//			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
//			{
//				NetworkInterface intf = en.nextElement();
//				
//				for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) 
//				{
//					InetAddress inetAddress = enumIpAddr.nextElement();
//					
//					if(!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
//				}
//			}
//		} catch (SocketException ex) {
//			System.err.println("Server Activity Socket Exception: "+ ex.toString());
//		}
//		return null;
//	}
//	
//	private void createSocket() {
//		// TODO Auto-generated method stub
//		clientOutputStreams=new ArrayList();
//		try {
//			ServerSocket socket=new ServerSocket(5000);
//			while(true){
//				Socket clientSocket=socket.accept();
//				PrintWriter writer=new PrintWriter(clientSocket.getOutputStream());
//				clientOutputStreams.add(writer);
//				Thread t = new Thread(new ClientHandler(clientSocket));
//				t.start();
//				System.out.println("Got a Connection to the Client");
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//public class ClientHandler implements Runnable{
//		
//		BufferedReader reader;
//		Socket sock;
//		public ClientHandler(Socket clientSocket){
//
//			try {
//				sock=clientSocket;
//				InputStreamReader isr=new InputStreamReader(sock.getInputStream());
//				reader=new BufferedReader(isr);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			String message;
//			try {
//				while((message=reader.readLine())!=null)
//				{
//					System.out.println("read :"+message);
//					tellEveryone(message);
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		private void tellEveryone(String message) {
//
//			// TODO Auto-generated method stub
//			Iterator itr=clientOutputStreams.iterator();
//			while(itr.hasNext()){
//				PrintWriter pWriter=(PrintWriter)itr.next();
//				pWriter.println(message);
//				pWriter.flush();
//			}
//		}
//
//	}
//}
