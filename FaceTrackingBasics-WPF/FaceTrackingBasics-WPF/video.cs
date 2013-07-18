using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.Threading;
using System.Collections;
using System.IO;
using System.Windows.Threading;

namespace FaceTrackingBasics
{
    class Video
    {

        public void ProcessVideo(byte[] videoData)
        {
            //int recv;
            byte[] data = new byte[videoData.Length];
            data = videoData;
            Socket udpSocket = new Socket(AddressFamily.InterNetwork, SocketType.Dgram,
ProtocolType.Udp);

            IPAddress serverAddr = IPAddress.Parse("192.168.2.20");

            IPEndPoint endPoint = new IPEndPoint(serverAddr, 8080);
            //IPEndPoint sender = new IPEndPoint(IPAddress.Any, 0);
            //EndPoint Remote = (EndPoint)(sender);
            UdpClient client = new UdpClient();
            client.Connect(endPoint);
            //udpSocket.Bind(endPoint);
            //recv = udpSocket.ReceiveFrom(data, ref Remote);
            Console.WriteLine(data.Length);
            client.Send(data, data.Length);
            while (true)
            {
                client.Send(data, data.Length);
            }
        }
    }

    class SendVideo
    {

        public void StartVideoThread(byte[] data)
        {


            Video oVideo = new Video();

            // Create the thread object
            Thread oThread = new Thread(new ThreadStart( () => oVideo.ProcessVideo(data)));

            // Start the thread
            oThread.Start();

            // Spin for a while waiting for the started thread to become alive
            while (!oThread.IsAlive) ;

            // Put the Main thread to sleep for 1 millisecond to allow oThread
            // to do some work:
            Thread.Sleep(1);

            //// Request that oThread be stopped
            //oThread.Abort();

            //// Wait until oThread finishes. Join also has overloads
            //// that take a millisecond interval or a TimeSpan object.
            //oThread.Join();

            //Console.WriteLine();
            //Console.WriteLine("Video.ProcessVideo has finished");

            //try
            //{
            //    Console.WriteLine("Try to restart the Video.ProcessVideo");
            //    oThread.Start();
            //}
            //catch (ThreadStateException)
            //{
            //    Console.Write("ThreadStateException trying to restart Video.ProcessVideo. ");
            //    Console.WriteLine("Expected since aborted threads cannot be restarted.");
            //}
        }
    }
}
