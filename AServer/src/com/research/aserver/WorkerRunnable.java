package com.research.aserver;

import gnu.io.NoSuchPortException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;

public class WorkerRunnable implements Runnable {
	
	protected Socket clientSocket 	= null;
	protected String serverText 	= null;
	private BufferedReader inFromClient;
	private DataOutputStream outToClient;
	private int[] currentPos = new int[6]; // make final?
	private boolean connected = false;
	private String comPort = "COM5";
	static TwoWaySerialComm serialCom = null;
	static MultiServoState mState;
	static int sound_average;
	int[] degrees = new int[7];
	int count = 0;
	private final AtomicBoolean lock = new AtomicBoolean(false);
	
	public WorkerRunnable(Socket clientSocket, String serverText) {
		this.clientSocket = clientSocket;
		this.serverText = serverText;
		initCurrentPos();
		if (serialCom == null) {
			serialCom = new TwoWaySerialComm();
		}
		try {
			if (!serialCom.isConnected(comPort)) {
				try {
					serialCom.connect(comPort);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mState = new MultiServoState(serialCom);
			}
		} catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			work();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
	}
	
	public void work() throws InterruptedException {
		try {
			InputStream input = clientSocket.getInputStream();
			OutputStream output = clientSocket.getOutputStream();
			inFromClient = new BufferedReader(new InputStreamReader(input));
			outToClient = new DataOutputStream(output);
			long time = System.currentTimeMillis();
			updateData();
			String message = null;
			long endTime = System.currentTimeMillis() + 2000;
			while ((message = (String) inFromClient.readLine()) != null) {
				count += 1;
				System.out.println("Message Received: " + message);
				parse(message);
				
				sound_average = degrees[6];
				
				
				if(lock.get()) {
					//
					// Send the positional data to the robot
					//
					
					mState.runServo(degrees[0], degrees[1], degrees[2],
							degrees[3], degrees[4], degrees[5]);
						// Maybe reset degrees to 0?
					
					//
					// Send a response information to the client application
					//
					currentPos[0] = mState.getCurrentPos(0);
					currentPos[1] = mState.getCurrentPos(1);
					currentPos[2] = mState.getCurrentPos(2);
					currentPos[3] = mState.getCurrentPos(3);
					currentPos[4] = mState.getCurrentPos(4);
					try {
						updateData();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
	//				if(System.currentTimeMillis() >= endTime){
	//					Thread.currentThread().interrupt();
	//				}
	//				if(Thread.currentThread().isInterrupted()) {
	//					return;
	//				}
				} else {
					//updateData();
				}
			}
//			output.close();
//			input.close();
			System.out.println("Request processed: " + time);
		} catch (IOException e) {
			// report exception somewhere
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
	public synchronized void updateData() throws IOException {
		String sentence = Integer.toString(currentPos[0]) + ", " + 
						  Integer.toString(currentPos[1]) + ", " +
						  Integer.toString(currentPos[2]) + ", " + 
						  Integer.toString(currentPos[3]) + ", " + 
						  Integer.toString(currentPos[4]) + "." + "\n";
		outToClient.flush();
		outToClient.writeBytes(sentence);
	}
	
	/**
	 * Get the clients sound average
	 * @param message
	 */
	public int getSoundAverage() {
		return sound_average;
	}
	
	public void parse(String message) {
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
		}
		System.out.println("Waiting for client message...");
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

			if (clientSocket != null) {
				try {
					clientSocket.close();
					synchronized (this) {
						clientSocket = null;
					}
				} catch (IOException e) {
					// there is nothing we can do: ignore it
				}
			}
		}
	}
	
	public synchronized void lock_in() {
		lock.compareAndSet(false, true);
	}
	
	public synchronized void unlock() {
		lock.compareAndSet(true, false);
	}
	
	
	public void returnThread() {
		return;
	}
}