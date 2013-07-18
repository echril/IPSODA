package com.research.aserver;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

import com.research.aserver.WorkerRunnable;


public class ThreadHandler implements Runnable {
	
	protected boolean		isStopped 		= false;
	protected Thread		runningThread 	= null;
	protected Thread		clientThread 	= null;
	private static volatile ThreadHandler instance = null;
	private Hashtable<Long, WorkerRunnable> Users = new Hashtable<Long, WorkerRunnable>();
	private final ArrayList<Thread> ClientThreads = new ArrayList<Thread>();
	private WorkerRunnable 	client 			= null;
	private int				sound_max		= 0;
	private int				sound_value		= 0;
	private int				index			= 0;
	private final Object 	lock 			= new Object();
	private final ReentrantLock entrantLock = new ReentrantLock();
	
	public ThreadHandler() {
	}
	
	public void setUp(WorkerRunnable client, Thread clientThread) {
		this.client = client;
		this.clientThread = clientThread;
		Users.put(this.clientThread.getId(), this.client);	// Place clients in a list with its thread ID as key
		ClientThreads.add(this.clientThread);			// List of client threads
	}
	
	public static ThreadHandler getInstance(){
		if (instance == null) {
            synchronized (ThreadHandler .class){
                    if (instance == null) {
                            instance = new ThreadHandler ();
                    }
          }
    }
    return instance;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		long endTime = System.currentTimeMillis() + 2000;		// Help variable to check every 2 sec
		while(true){
			while (Users.isEmpty() || ClientThreads.isEmpty()) {
				// loop until client connects
			}
			
			while (!Users.isEmpty() && !ClientThreads.isEmpty()) {
			
				for (int i = 0; i < ClientThreads.size(); i++) {	// Remove clients and threads if no longer active
					if (!ClientThreads.get(i).isAlive()) {
						Users.remove(ClientThreads.get(i).getId());
						ClientThreads.get(i).interrupt();
						ClientThreads.remove(i);
						if (i == index) {
							index = 0;
						}
					}
				}
				
				if(System.currentTimeMillis() >= endTime) {				// Do work every 2 sec
					for (int i = 0; i < ClientThreads.size(); i++) {	// Get the client with the loudest sound
						System.out.println(ClientThreads.get(i).toString());
						synchronized(lock){
							sound_value = Users.get(ClientThreads.get(i).getId()).getSoundAverage();
							if (sound_max < sound_value) {
								sound_max = sound_value;
								index = i;
							}
						}
					}

					for (int i = 0; i < ClientThreads.size(); i++) {	// Get the client with the loudest sound
						Users.get(ClientThreads.get(i).getId()).unlock();		// Add sound threshold
					}
					
					if(Users.size() > 0 && ClientThreads.size() > 0) {
						synchronized(lock) {
							Users.get(ClientThreads.get(index).getId()).lock_in();
						}
					}

					//index = 0;
					sound_max = 0;
					sound_value = 0;
					endTime = System.currentTimeMillis() + 2000;					// update time
					System.out.println("End time: " + endTime);
				}
				
				if(Users.isEmpty()) {
					System.out.println("Users are empty");
				}
				
				if(ClientThreads.isEmpty()) {
					System.out.println("Clientthreads are empty");
				}
			}
		}
	}
}
