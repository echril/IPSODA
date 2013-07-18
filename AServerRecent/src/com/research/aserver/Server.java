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

		MultiThreadedServer server = new MultiThreadedServer(8888);
		new Thread(server).start();
	}

}