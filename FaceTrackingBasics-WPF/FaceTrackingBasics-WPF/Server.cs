﻿using System;
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
    public class Server
    {
        public event TcpReceiveEventHandler onTcpReceive;
        private int portNo;
        // スレッド停止命令用
        private bool stop_flg = false;
        public NetworkStream ns;
        public NetworkStream stream;
        public TcpClient client;
        public int[] currentPos = new int[6];
        private System.Drawing.Bitmap _CurrentBitmap;




        public Server(int portNo)
        {
            this.portNo = portNo;
            // 接続待ちのスレッドを開始
            Thread thread = new Thread(new ThreadStart(Client));
            //Thread thread = new Thread(new ThreadStart(ListenStart));
            // スレッドをスタート
            thread.Start();
            //Console.WriteLine("thread.Start()");
        }

        public void Dispose()
        {
            stop_flg = true;
            GC.SuppressFinalize(this);
        }
        /*
         * クライアント 
         */

        public void Client()
        {
            System.Text.Encoding enc = System.Text.Encoding.UTF8;
            string ipAddress = "192.168.2.20";//192.168.2.102";//"localhost";//192.168.2.43";
            //Console.WriteLine("IP:{0}, Port:{1}のServerへ接続開始...", ipAddress, portNo);
             TcpClient client = new TcpClient(ipAddress, portNo);
            //Console.WriteLine("接続完了");
            //NetworkStreamを取得
            stream = client.GetStream();
        
            //string msg = "hello\r\n";
            //byte[] tmp = Encoding.UTF8.GetBytes(msg);
            //stream.Write(tmp, 0, tmp.Length);

            //サーバとの接続を終了
            //client.Close();
        }
        /**
         * 接続待ちスレッド本体
        */
        public void ListenStart()
        {
            
            // Listenerの生成
            TcpListener listener = new TcpListener(IPAddress.Any, this.portNo);
            // 接続要求受け入れ開始
            listener.Start();
            while (!stop_flg)
            {
                // 接続待ちがあるか？
                if (listener.Pending() == true)
                {
                    // 接続要求を受け入れる
                    TcpClient client = listener.AcceptTcpClient();
                    //NetworkStreamを取得
                    stream = client.GetStream();
                   // TcpReceiveWorker rcv = new TcpReceiveWorker(tcp, this);
                    //Thread thread = new Thread(new ThreadStart(rcv.TCPClientProc));
                    // スレッドをスタート
                    //thread.Start();
                }
                else
                {
                    Thread.Sleep(0);
                }
            }
            // 接続待ち終了
            listener.Stop();
             
        }

        //public void SendImage()
        //{
        //    Socket sListen = new Socket(AddressFamily.InterNetwork,
        //                                SocketType.Stream,
        //                                ProtocolType.Tcp);

        //    IPAddress IP = IPAddress.Parse("192.168.1.33");
        //    IPEndPoint IPE = new IPEndPoint(IP, 8080);

        //    sListen.Bind(IPE);
        //    sListen.Listen(2);

        //    while (true)
        //    {
        //        Socket clientSocket;
        //        clientSocket = sListen.Accept();

        //        var converter = new System.Drawing.ImageConverter();
        //        while (true) // find a better way to determine that the picture is still updating?
        //        {
        //            byte[] buffer = (byte[])converter.ConvertTo(_CurrentBitmap, typeof(byte[]));
        //            clientSocket.Send(buffer, buffer.Length, SocketFlags.None);
        //        }
        //    }
        //}


        //public void Recive()
        //{
        //    Socket s = new Socket(AddressFamily.InterNetwork,
        //                        SocketType.Stream,
        //                        ProtocolType.Tcp);

        //    IPAddress IP = IPAddress.Parse("127.0.0.1");
        //    IPEndPoint IPE = new IPEndPoint(IP, 4321);
        //    s.Connect(IPE);

        //    while (true)
        //    {

        //        byte[] buffer = new byte[1000000];

        //        s.Receive(buffer, buffer.Length, SocketFlags.None);

        //        MemoryStream ms = new MemoryStream(buffer);

        //        ms.Write(buffer, 0, buffer.Length);
        //        System.Drawing.Bitmap bitmap = new System.Drawing.Bitmap(ms);

        //        Dispatcher.BeginInvoke(new Action(() =>
        //        {
        //            rgbImage11.Source = bitmap.ToBitmapSource();
        //        }));

        //    }
        //}

        //public void sendVideo(byte[] videoData)
        //{
        //    SendVideo video = new SendVideo();
        //    video.StartVideoThread(videoData);

        //}

        public void SendMsg(string msg)
        {
            String EOL = "."; //end of line
                try
                {
                    //Console.WriteLine("Message: " + msg);
                    Encoding encode = Encoding.Default;
                    // 送信する文字列をバイト配列に変換
                    // この際に、エンコードも同時に行う。
                    // string s = msg.ToString() + "\r\n";
                    string s = msg.ToString() + EOL + "\r\n";
                    byte[] bytData =
                        encode.GetBytes(s);

                    // 書き出しを行う。
                    stream.Write(bytData, 0, bytData.Length);
                    // フラッシュ(強制書き出し)
                    // これを行わないと、確実にネットワークに流れない。
                    stream.Flush();
                }
                catch (SocketException eSocket)
                {
                    System.Diagnostics.Debug.Write(eSocket.Message);
                }
                catch (Exception ex)
                {
                    System.Diagnostics.Debug.Write(ex.Message);
                }
        }

        public string ReadMsg() 
        {
            byte[] m_Bytes = ReadToEnd(stream);
            var str = System.Text.Encoding.Default.GetString(m_Bytes);
            return str;
        }

        public static byte[] ReadToEnd(System.IO.Stream stream)
        {
            long originalPosition = 0;

            if (stream.CanSeek)
            {
                originalPosition = stream.Position;
                stream.Position = 0;
            }

            try
            {
                byte[] readBuffer = new byte[4096];

                int totalBytesRead = 0;
                int bytesRead;

                if ((bytesRead = stream.Read(readBuffer, totalBytesRead, readBuffer.Length - totalBytesRead)) > 0)
                {
                    totalBytesRead += bytesRead;

                    if (totalBytesRead == readBuffer.Length)
                    {
                        int nextByte = stream.ReadByte();
                        if (nextByte != -1)
                        {
                            byte[] temp = new byte[readBuffer.Length * 2];
                            Buffer.BlockCopy(readBuffer, 0, temp, 0, readBuffer.Length);
                            Buffer.SetByte(temp, totalBytesRead, (byte)nextByte);
                            readBuffer = temp;
                            totalBytesRead++;
                        }
                    }
                }

                byte[] buffer = readBuffer;
                if (readBuffer.Length != totalBytesRead)
                {
                    buffer = new byte[totalBytesRead];
                    Buffer.BlockCopy(readBuffer, 0, buffer, 0, totalBytesRead);
                }
                return buffer;
            }
            finally
            {
                if (stream.CanSeek)
                {
                    stream.Position = originalPosition;
                }
            }
        }

        public void Close()
        {
            //Console.WriteLine("Socket Closed");
            stream.Close();
            client.Close();
        }
        protected virtual void OnReceiveEvent(long temperature)
        {
            if (onTcpReceive != null)
            {
                onTcpReceive(temperature);
            }
        }
        internal void RiaseEvent(long temperature)
        {
            OnReceiveEvent(temperature);
        }
    }
    public class TcpReceiveEventArgs : EventArgs
    {
        public TcpReceiveEventArgs(long Temperature)
        {
            this.Temperature = Temperature;
        }
        private long temperature;

        public long Temperature
        {
            get { return temperature; }
            set { temperature = value; }
        }
    }
    public delegate void TcpReceiveEventHandler(long temperature);
    class TcpReceiveWorker
    {
        private TcpClient tcp = null;
        Server rcv = null;
        public TcpReceiveWorker(TcpClient tcp, Server rcv)
        {
            this.tcp = tcp;
            this.rcv = rcv;
        }
        public void TCPClientProc()
        {
            NetworkStream st = tcp.GetStream();
            ArrayList aryList = new ArrayList();
            long count = 0;
            do
            {
                int nret = st.ReadByte();
                if (nret < '0' || nret > '9')
                {
                    break;
                }
                if (nret != -1)
                {
                    aryList.Add((byte)nret);
                }
                count++;
                if (count > 8) break;
            } while (tcp.Connected == true);
            // ソケットを閉じる
            tcp.Close();
            // 受信したものをbyte配列に変換
            byte[] byt = new byte[aryList.Count];
            for (int i = 0; i < aryList.Count; i++)
            {
                byt[i] = (byte)aryList[i];
            }
            string strFromByte = Encoding.ASCII.GetString(byt);
            long ret = long.Parse(strFromByte);
            rcv.RiaseEvent(ret);
        }

    }

    public class TcpSend
    {
        public string hostName;
        public int portNo;
        public TcpSend(string hostName, int portNo)
        {
            this.hostName = hostName;
            this.portNo = portNo;
        }

        public Boolean send(long value)
        {
            try
            {
                // クライアント用のソケットを作成する
                //Console.WriteLine("Value: " + value);
                TcpClient cl = new TcpClient();
                // サーバーへ接続する
                cl.Connect(hostName, portNo);
                // 接続したソケットからNetworkStreamを取得
                NetworkStream stream = cl.GetStream();
                Encoding encode = Encoding.Default;
                // 送信する文字列をバイト配列に変換
                // この際に、エンコードも同時に行う。
                string s = value.ToString() + "\r\n";
                byte[] bytData = encode.GetBytes(s);

                // 書き出しを行う。
                stream.Write(bytData, 0, bytData.Length);
                
                // フラッシュ(強制書き出し)
                // これを行わないと、確実にネットワークに流れない。
                stream.Flush();
                // ソケットをクローズ
                cl.Close();
            }
            catch (SocketException eSocket)
            {
                System.Diagnostics.Debug.Write(eSocket.Message);
                return false;
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.Write(ex.Message);
                return false;
            }
            return true;
        }
    }
}
