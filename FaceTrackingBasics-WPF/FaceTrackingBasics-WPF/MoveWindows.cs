using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Diagnostics;
using System.Runtime.InteropServices;
using System.Text;

public delegate bool EnumWindowCallBack(IntPtr hwnd, IntPtr lParam);

namespace FaceTrackingBasics
{
    public class MoveWindows
    {
        private double rotationXmax = 0;
        private double rotationYmax = 0;

        public MoveWindows()
        {
        }

        //        BOOL MoveWindow(
        //            HWND hWnd,      // ウィンドウのハンドル
        //            int X,          // 横方向の位置
        //            int Y,          // 縦方向の位置
        //            int nWidth,     // 幅
        //            int nHeight,    // 高さ
        //            BOOL bRepaint   // 再描画オプション
        //            );
        [DllImport("User32.dll")]
        static extern int MoveWindow(
            IntPtr hWnd,
            int x,
            int y,
            int nWidth,
            int nHeight,
            int bRepaint
            );
        //        HWND FindWindow(
        //            LPCTSTR lpClassName,  // クラス名
        //            LPCTSTR lpWindowName  // ウィンドウ名
        //            );
        //        Unicode：Windows NT/2000 は Unicode 版と ANSI 版を実装
        [DllImport("User32.dll", CharSet = CharSet.Unicode)]
        static extern IntPtr FindWindow(
            string lpszClass,
            string lpszWindow
            );

        //        BOOL SetForegroundWindow(
        //            HWND hWnd   // ウィンドウのハンドル
        //            );
        [DllImport("User32.dll")]
        static extern int SetForegroundWindow(
            IntPtr hWnd
            );

        //        int GetClassName(
        //            HWND hWnd,           // ウィンドウのハンドル
        //            LPTSTR lpClassName,  // クラス名
        //            int nMaxCount        // クラス名バッファのサイズ
        //            );
        //        Unicode：Windows NT/2000 は Unicode 版と ANSI 版を実装
        [DllImport("User32.Dll", CharSet = CharSet.Unicode)]
        public static extern int GetClassName(
            IntPtr hWnd,
            StringBuilder s,
            int nMaxCount
            );

        //        int GetWindowText(
        //            HWND hWnd,        // ウィンドウまたはコントロールのハンドル
        //            LPTSTR lpString,  // テキストバッファ
        //            int nMaxCount     // コピーする最大文字数
        //            );
        //        Unicode：Windows NT/2000 は Unicode 版と ANSI 版を実装
        [DllImport("User32.Dll", CharSet = CharSet.Unicode)]
        static extern int GetWindowText(
            IntPtr hWnd,
            StringBuilder s,
            int nMaxCount
            );

        //        HWND GetParent(
        //            HWND hWnd   // 子ウィンドウのハンドル
        //            );
        [DllImport("User32.Dll")]
        static extern int GetParent(
            IntPtr hWnd
            );

        //        BOOL IsWindowVisible(
        //            HWND hWnd   // ウィンドウのハンドル
        //            );
        [DllImport("User32.Dll")]
        static extern int IsWindowVisible(
            IntPtr hWnd
            );

        //        BOOL IsChild(
        //            HWND hWndParent,  // 親ウィンドウのハンドル
        //            HWND hWnd         // 調査するウィンドウのハンドル
        //            );
        [DllImport("User32.Dll")]
        static extern int IsChild(
            IntPtr hWnd
            );

        //        BOOL ShowWindow(
        //            HWND hWnd,     // ウィンドウのハンドル
        //            int nCmdShow   // 表示状態
        //            );        
        const int SW_HIDE = 0;
        const int SW_SHOWNORMAL = 1;
        const int SW_NORMAL = 1;
        const int SW_SHOWMINIMIZED = 2;
        const int SW_SHOWMAXIMIZED = 3;
        const int SW_MAXIMIZE = 3;
        const int SW_SHOWNOACTIVATE = 4;
        const int SW_SHOW = 5;
        const int SW_MINIMIZE = 6;
        const int SW_SHOWMINNOACTIVE = 7;
        const int SW_SHOWNA = 8;
        const int SW_RESTORE = 9;
        const int SW_SHOWDEFAULT = 10;
        const int SW_FORCEMINIMIZE = 11;
        const int SW_MAX = 11;
        [DllImport("User32.Dll")]
        static extern int ShowWindow(
            IntPtr hWnd,
            int nCmdShow
            );


        //        HWND GetDesktopWindow(VOID);        
        [DllImport("User32.Dll")]
        static extern IntPtr GetDesktopWindow();

        //        typedef struct _RECT 
        //                { 
        //                    LONG left; 
        //                    LONG top; 
        //                    LONG right; 
        //                    LONG bottom; 
        //                } RECT, *PRECT; 
        [StructLayout(LayoutKind.Sequential, Pack = 4)]
        private struct RECT
        {
            public int left;
            public int top;
            public int right;
            public int bottom;
        }

        //        BOOL GetWindowRect(
        //            HWND hWnd,      // ウィンドウのハンドル
        //            LPRECT lpRect   // ウィンドウの座標値
        //            );        
        [DllImport("User32.Dll")]
        static extern int GetWindowRect(
            IntPtr hWnd,      // ウィンドウのハンドル
            out RECT rect   // ウィンドウの座標値
            );

        [DllImport("user32.Dll")]
        static extern int EnumWindows(EnumWindowCallBack x, IntPtr y);
        public void Notepad()
        {
            System.Diagnostics.Process.Start("Notepad.exe");
        }
        public void setMax(double Xmax, double Ymax)
        {
            rotationXmax = Xmax;
            rotationYmax = Ymax;
        }
        public void moveNotepad(double X, double Y)
        {
            double W;
            double H;
            double nWidth = 500;
            double nHeight = 300;
            RECT rect;
            GetWindowRect(GetDesktopWindow(), out rect);
//            W = Y / (rotationYmax * 2) * (double)rect.right;
//            H = X + (rotationXmax * 2);
            W = Y / (rotationYmax * 2) * (double)rect.right + ((double)rect.right / 2) - (nWidth / 2);
            H = X / (rotationXmax * 2) * (double)rect.bottom + ((double)rect.bottom / 2) - (nHeight / 2);
//            W = (rect.right / 2) - (nWidth / 2) + (Y * 6);
//            H = (rect.bottom / 2) - (nHeight / 2) + (X * 6);
            //Console.WriteLine("Y:" + Y + ", X:" + X);
            //Console.WriteLine("W:" + W + ", H:" + H);
            //Console.WriteLine("TEST:" + rotationYmax * 2 + "," + Y / (rotationYmax * 2));
          
            IntPtr h = FindWindow("Notepad", null);
            MoveWindow(h, (int)W, (int)H, (int)nWidth, (int)nHeight, 1);
            SetForegroundWindow(h);
        }
        public void moveNotepad(double X, double Y, double depth)
        {
            double W;
            double H;
            double nWidth = 360;
            double nHeight = 240;
            RECT rect;
            GetWindowRect(GetDesktopWindow(), out rect);
            //            W = Y / (rotationYmax * 2) * (double)rect.right;
            //            H = X + (rotationXmax * 2);
            W = Y / (rotationYmax * 2) * (double)rect.right + ((double)rect.right / 2) - (nWidth / 2);
            H = X / (rotationXmax * 2) * (double)rect.bottom + ((double)rect.bottom / 2) - (nHeight / 2);
            //            W = (rect.right / 2) - (nWidth / 2) + (Y * 6);
            //            H = (rect.bottom / 2) - (nHeight / 2) + (X * 6);
            //Console.WriteLine("Y:" + Y + ", X:" + X);
            //Console.WriteLine("W:" + W + ", H:" + H);
            //Console.WriteLine("TEST:" + rotationYmax * 2 + "," + Y / (rotationYmax * 2));
            nWidth = nWidth * (1/depth);
            nHeight = nHeight * (1 / depth);
            IntPtr h = FindWindow("Notepad", null);
            MoveWindow(h, (int)W, (int)H, (int)nWidth, (int)nHeight, 1);
            SetForegroundWindow(h);
        }
        public void staticSkype(double depth)
        {
            double W;
            double H;
            double nWidth;
            double nHeight;
            RECT rect;
            GetWindowRect(GetDesktopWindow(), out rect);
            nHeight = (double)rect.bottom * 0.3; //ウィンドウの初期値は高さの 3/10
            nHeight = nHeight * (1 / depth); //Depthに合わせて高さ拡大縮小
            nWidth = nHeight * 1.5; //3:2になるようウィンドウ横幅決定
            W = ((double)rect.right / 2) - (nWidth / 2);
            H = ((double)rect.bottom / 2) - (nHeight / 2);

            IntPtr h = FindWindow("TConversationForm", null);
            //Console.WriteLine("W:" + W + ", H:" + H);
            MoveWindow(h, (int)W, (int)H, (int)nWidth, (int)nHeight, 1);
            SetForegroundWindow(h); 
           
        }
        public void moveSkype(double X, double Y, double depth)
        {
            double W;
            double H;
            double nWidth = 500;
            double nHeight = 300;
            RECT rect;
            GetWindowRect(GetDesktopWindow(), out rect);
            nHeight = (double)rect.bottom * 0.3; //ウィンドウの初期値は高さの 3/10
            nHeight = nHeight * (1 / depth); //Depthに合わせて高さ拡大縮小
            nWidth = nHeight * 1.5; //3:2になるようウィンドウ横幅決定
//            nWidth = nWidth * (1 / depth);
            W = Y / (rotationYmax * 2) * ((double)rect.right - nWidth /**(3 / 4)*/) + ((double)rect.right / 2) - (nWidth / 2);
            H = X / (rotationXmax * 2) * ((double)rect.bottom - nHeight /** (3 / 4)*/) + ((double)rect.bottom / 2) - (nHeight / 2);
//            Console.WriteLine("nW nH =({0},{1})", nWidth, nHeight);
            //Console.WriteLine("H = {0} / ({1} * 2) * ({2} - {3} * (3 / 4)) + ({2} / 2) - ({3} / 2);", X, rotationXmax, rect.bottom, nHeight);

//            IntPtr h = FindWindow("GomPlayer1.x", null);
//            IntPtr h = FindWindow("tSkMainForm", null);

            IntPtr h = FindWindow("Notepad", null);
//            IntPtr h = FindWindow("TConversationForm", null);
            //Console.WriteLine("W:" + W + ", H:" + H + "depth:" + depth);
            MoveWindow(h, (int)W, (int)H, (int)nWidth, (int)nHeight, 1);
            SetForegroundWindow(h);
        }
        /*
        public static bool IECallBack(IntPtr hwnd, IntPtr lParam)
        {
            StringBuilder sbClassName = new StringBuilder(256);
            GetClassName(hwnd, sbClassName, sbClassName.Capacity);
            if (sbClassName.ToString().StartsWith("IEFrame"))
            {
                Form1.al.Add(hwnd);
            }
            return true;
        }
        /*
        private void button1_Click(object sender, System.EventArgs e)
        {
            RECT rect;
            GetWindowRect(GetDesktopWindow(), out rect);
            this.textBox1.Text =
                rect.left.ToString() + ","
                + rect.top.ToString() + ","
                + rect.left.ToString() + ","
                + rect.bottom.ToString();
        }




        private void button4_Click(object sender, System.EventArgs e)
        {
            EnumWindows(new EnumWindowCallBack(Form1.ListViewCallBack), this.listView1.Handle);
        }


        private void button5_Click(object sender, System.EventArgs e)
        {
            int index = this.listView1.SelectedIndices[0];
            int h = Int32.Parse(this.listView1.Items[index].Text);
            MoveWindow((IntPtr)h, 10, 10, 300, 200, 1);
            ShowWindow((IntPtr)h, SW_RESTORE);
        }

        public static bool IECallBack(IntPtr hwnd, IntPtr lParam)
        {
            StringBuilder sbClassName = new StringBuilder(256);
            GetClassName(hwnd, sbClassName, sbClassName.Capacity);
            if (sbClassName.ToString().StartsWith("IEFrame"))
            {
                Form1.al.Add(hwnd);
            }
            return true;
        }

        static public ArrayList al = null;

        private void button6_Click(object sender, System.EventArgs e)
        {
            al = new ArrayList();

            // デスクトップウィンドウのサイズを求める。
            RECT rect;
            GetWindowRect(GetDesktopWindow(), out rect);

            // EnumWindows でIECallBack へさせる。
            EnumWindows(new EnumWindowCallBack(Form1.IECallBack), IntPtr.Zero);

            // デスクトップウィンドウの縦サイズをIEの数で割る。
            int hw = rect.bottom / al.Count;
            // IE を整列させる。
            for (int i = 0; i < al.Count; i++)
            {
                SetForegroundWindow((IntPtr)al[i]);
                ShowWindow((IntPtr)al[i], SW_SHOWNORMAL);
                MoveWindow((IntPtr)al[i], 0, i * hw, rect.right / 3, hw, 1);
            }
        }
         * */

    }
}
