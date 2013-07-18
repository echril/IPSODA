package com.research.aserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class VideoHandler implements Runnable{

	private ServerSocket videoServer;
	private ServerSocket ipsodaServer;
	private int ipsodaPort = 8080;
	private int videoPort = 9000;
	String ipsodaHostname = "192.168.2.19";
	private Socket videoSocket = null;
	private Socket ipsodaSocket = null;
	private DataOutputStream outToIpsoda;
	private BufferedReader inFromClient;
	private InetAddress ipsodaAddr;
	public int input = 0;
	
	public VideoHandler() throws Exception {
		try {
			videoServer = new ServerSocket(videoPort);
			ipsodaServer = new ServerSocket(ipsodaPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] getPicture(InputStream in) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int length = 0;
			while ((length = in.read(data)) != -1) {
				out.write(data, 0, length);
			}
			return out.toByteArray();
		} catch (IOException ioe) {
			// handle it
		}
		return null;
	}

	@Override
	public void run(){
		// TODO Auto-generated method stub
//		byte[] imageByteArray;
//		InputStream is = null;
//		byte[] data = new byte[1024];
//		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//		is = videoSocket.getInputStream();
		
		try {
			System.out.println(InetAddress.getLocalHost());
			videoSocket = this.videoServer.accept(); // Connect to clients
			System.out.println("Video has reached the server");
		} catch (IOException e) {
		}

		try {
			
			while (true) {
				ipsodaSocket = new Socket(ipsodaHostname, ipsodaPort);
				inFromClient = new BufferedReader(new InputStreamReader(
						videoSocket.getInputStream()));
				outToIpsoda = new DataOutputStream(ipsodaSocket.getOutputStream());
				input = inFromClient.read();
				while(input != -1) {
					outToIpsoda.writeByte(input);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
