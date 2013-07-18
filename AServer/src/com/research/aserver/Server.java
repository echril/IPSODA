package com.research.aserver;

import gnu.io.NoSuchPortException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;



public class Server {

	private ServerSocket server;
	private int port = 8888;
//	private boolean exists = false;
//	private int threadPos;
	private ArrayList<ConnectionHandler> users = new ArrayList<ConnectionHandler>();

	public Server() throws Exception {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Main function of the server class. Creates a new server
	 * 
	 * @param args
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
//		Server midMan = new Server();
//		midMan.handleConnection();
		MultiThreadedServer server = new MultiThreadedServer(8888);
		new Thread(server).start();
//		
//		try {
//			Thread.sleep(20 * 1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println("Stopping Server");
//		server.stop();
	}

	/**
	 * Handle the attempted connections to the server
	 * 
	 * @throws IOException
	 * @throws NoSuchPortException
	 */
	public void handleConnection() throws IOException, NoSuchPortException {
		System.out.println("Waiting for client message...");
		System.out.println(InetAddress.getLocalHost());
		//
		// The server do a loop here to accept all connection initiated by the
		// client application.
		//
		while (true) {
			Socket socket = null;
			try {
				socket = server.accept();
			}
			catch(IOException e){
				
			}
			
			for (int i = 0; i < users.size(); i++) {
				// Check connection, remove on dead
				if (!users.get(i).isConnected()) {
					users.get(i).close();
					users.remove(i);
				}
			}
			
			System.out.println(socket.getRemoteSocketAddress().toString());
			ConnectionHandler ch = new ConnectionHandler(socket);
			users.add(ch);
			
//			for (int i = 0; i < users.size(); i++) {
//				for (int j = i + 1; j < users.size(); j++) {
//					if(users.get(i).getSoundAverage() > users.get(j).getSoundAverage()) {
//						threadPos = i;
//					}
//					else {
//						threadPos = j;
//					}
//				}
//			}
//			users.get(threadPos).run();
		}
		
	}
}
/**
 * Handles the connections between the clients and server
 * 
 * @author chril
 * 
 */
class ConnectionHandler implements Runnable {
	private Socket socket;
	private BufferedReader inFromClient;
	private DataOutputStream outToClient;
	private int[] currentPos = new int[6];
	private boolean connected = false;
	static TwoWaySerialComm serialCom = null;
	static MultiServoState mState;
	static int sound_average;
	int[] degrees = new int[7];
	Thread thread = null;

	public ConnectionHandler(Socket socket) throws NoSuchPortException {
		this.socket = socket;
		connected = true;
		initCurrentPos();
		if (serialCom == null) {
			serialCom = new TwoWaySerialComm();
		}
		if (!serialCom.isConnected("COM3")) {
			try {
				serialCom.connect("COM3");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mState = new MultiServoState(serialCom);
		}
		thread = new Thread(this);
		thread.start();
		System.out.println(thread.toString());
	}

	/**
	 * Get the client address
	 * 
	 * @return the client address
	 */
	public SocketAddress getAddress() {
		return socket.getRemoteSocketAddress();
	}

	/**
	 * Checks if the socket is connected
	 * 
	 * @return true if the socket is connected, otherwise false
	 */
	public boolean isConnected() {
		if (socket.isConnected())
			return true;
		else
			return false;
	}

	public void run() {
		try {
			//
			// Read a message sent by client application
			//
			inFromClient = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			outToClient = new DataOutputStream(socket.getOutputStream());
			updateData();
			String message = null;
			while ((message = (String) inFromClient.readLine()) != null) {
				System.out.println("Message Received: " + message);

				if (message != null) {
					char c;
					StringBuilder sb = new StringBuilder(4);
					int j = 0;
					boolean help = false;

					for (int i = 0; i < message.length(); i++) {
						c = message.charAt(i);
						if (Character.isDigit(c)) {
							sb.append(c);
							help = true;
						}
						if (!Character.isDigit(c) && help == true) {
							degrees[j] = Integer.parseInt(sb.toString());
							j++;
							help = false;
							sb.delete(0, sb.length());
						}
					}
					sound_average = degrees[6];
					//
					// Send the positional data to the robot
					//
					mState.runServo(degrees[0], degrees[1], degrees[2],
							degrees[3], degrees[4], degrees[5]);
					//
					// Send a response information to the client application
					//
					currentPos[0] = mState.getCurrentPos(0);
					currentPos[1] = mState.getCurrentPos(1);
					currentPos[2] = mState.getCurrentPos(2);
					currentPos[3] = mState.getCurrentPos(3);
					currentPos[4] = mState.getCurrentPos(4);
					updateData();
				}
				System.out.println("Waiting for client message...");
			}

		} catch (IOException e) {
			e.printStackTrace();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
     * Initiate the robot's starting position.
     */
    public void initCurrentPos()
    {
    	currentPos[0] = 100;
		currentPos[1] = 100;
		currentPos[2] = 100;
		currentPos[3] = 100;
		currentPos[4] = 100;
		currentPos[5] = 0;
    }

	/**
	 * Send the data to the client
	 * 
	 * @throws IOException
	 */
	public void updateData() throws IOException {
		String sentence = Integer.toString(currentPos[0]) + ", " + 
						  Integer.toString(currentPos[1]) + ", " +
						  Integer.toString(currentPos[2]) + ", " + 
						  Integer.toString(currentPos[3]) + ", " + 
						  Integer.toString(currentPos[4]) + "." + "\n";
		outToClient.flush();
		outToClient.writeBytes(sentence);
	}
	
	/**
	 *  Get sound average
	 */
	public int getSoundAverage()
	{
		return sound_average;
	}
	
	public long getThreadID()
	{
		return thread.getId();
	}

	/**
	 * Close all connections
	 */
	public void close() {
		if (connected) {
			synchronized (this) {
				connected = false;
			}
			if (outToClient != null) {
				try {
					outToClient.close();
					synchronized (this) {
						outToClient = null;
					}
				} catch (IOException e) {
					// there is nothing we can do: ignore it
				}
			}

			if (inFromClient != null) {
				try {
					inFromClient.close();
					synchronized (this) {
						inFromClient = null;
					}
				} catch (IOException e) {
					// there is nothing we can do: ignore it
				}
			}

			if (socket != null) {
				try {
					socket.close();
					synchronized (this) {
						socket = null;
					}
				} catch (IOException e) {
					// there is nothing we can do: ignore it
				}
			}
		}
	}
}
