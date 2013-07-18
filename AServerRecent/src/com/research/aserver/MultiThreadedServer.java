package com.research.aserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Hashtable;

public class MultiThreadedServer implements Runnable {
	protected int			serverPort 		= 8888;
	protected ServerSocket	serverSocket 	= null;
	protected boolean		isStopped 		= false;
	protected Thread		runningThread 	= null;
	protected Thread		clientThread 	= null;
	protected Thread		threadThread	= null;
	private Hashtable<Long, WorkerRunnable> Users = new Hashtable<Long, WorkerRunnable>();
	private ArrayList<Thread> ClientThreads = new ArrayList<Thread>();
	private WorkerRunnable 	client 			= null;
	private ThreadHandler	threadHandler	= null;
	private int				sound_max		= 0;
	private boolean 		once 			= true;
	
	public MultiThreadedServer (int port) {
		this.serverPort = port;
	}
	
	public void run() {
		synchronized(this) {
			this.runningThread = Thread.currentThread();
		}
		openServerSocket();

		while( !isStopped() ) {
			Socket clientSocket = null;
			try {
				System.out.println(InetAddress.getLocalHost());
				clientSocket = this.serverSocket.accept();			// Connect to clients
			} catch (SocketTimeoutException e) {
				
			} catch (IOException e) {
				if( isStopped() ) {
					System.out.println("Server Stopped");
					return;
				}
				throw new RuntimeException("Error accepting client connection", e);
			}
			
			client = new WorkerRunnable(clientSocket, "Multithreaded Server");//Class does client work
			clientThread = new Thread(client);			// Make a thread for each client
			clientThread.start();						// start thread

			threadHandler = ThreadHandler.getInstance();
			threadHandler.setUp(client, clientThread);	// Set up the thread handler
			if ( once == true) {						// make sure the threadHandler thread is only created once
				threadThread = new Thread(threadHandler);
				threadThread.start();
				once = false;
			}	
		}
		System.out.println("Server Stopped");
	}
	
	/**
	 * Check if the socket is stopped
	 * @return true if the socket is stopped
	 */
	private synchronized boolean isStopped() {
		return this.isStopped;
	}
	
	/**
	 * Stop and close the socket
	 */
	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}
	
	/**
	 * Open server socket
	 */
	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port 8888", e);
		}
	}
}