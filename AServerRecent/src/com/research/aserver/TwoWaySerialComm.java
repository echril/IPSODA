package com.research.aserver;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.HashSet;

public class TwoWaySerialComm
{
	static CommPortIdentifier portIdentifier;
	static OutputStreamWriter os;
	static BufferedWriter bw;
	
    public TwoWaySerialComm()
    {
        super();
    }
    
    public boolean isConnected(String portName) throws NoSuchPortException
    {
    	portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
    	if ( portIdentifier.isCurrentlyOwned() )
    		return true;
    	else
    		return false;
    }
    
    void connect ( String portName ) throws Exception
    {
        portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                
                //(new Thread(new SerialReader(in))).start();
                //SerialWriter os = new SerialWriter(out);
                os = new OutputStreamWriter(out);
                bw = new BufferedWriter(os);
            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    public boolean sendMessage(final String ms) {
		(new Runnable() {
			public void run() {
/*
				try {
					os = new OutputStreamWriter(bs.getOutputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG,
							"bluetoothCom.sendMessage: Output stream creation failed.",
							e);
					e.printStackTrace();
					//return false;
				}*/
				try {
					// シリアルポートに書き込み
					bw = new BufferedWriter(os);
					bw.write(ms);
					bw.newLine();
					bw.flush();
					System.out.println("sendMessage : " + ms);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//return false;
				}
				return;
			}
		}).run();
		
		return true;
	}
    
    /** */
    public static class SerialReader implements Runnable 
    {
        InputStream in;
        
        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    System.out.print(new String(buffer,0,len));
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }

    /** */
    public static class SerialWriter implements Runnable 
    {
        OutputStream out;
        
        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        
        public void run ()
        {
            try
            {                
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }
    
    public boolean close() {
		try {
			bw.close();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
    
    /**
     * @return    A HashSet containing the CommPortIdentifier for all serial ports that are not currently being used.
     */
    public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
                try {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                    h.add(com);
                } catch (PortInUseException e) {
                    System.out.println("Port, "  + com.getName() + ", is in use.");
                } catch (Exception e) {
                    System.err.println("Failed to open port " +  com.getName());
                    e.printStackTrace();
                }
            }
        }
        return h;
    }

    
//    public static void main ( String[] args )
//    {
//        try
//        {
//        	(new TwoWaySerialComm()).connect("COM5");
//        }
//        catch ( Exception e )
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
}
