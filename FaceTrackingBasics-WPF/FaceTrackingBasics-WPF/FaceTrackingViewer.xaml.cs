// --------------------------------------------------------------------------------------------------------------------
// <copyright file="FaceTrackingViewer.xaml.cs" company="Microsoft">
//     Copyright (c) Microsoft Corporation.  All rights reserved.
// </copyright>
// --------------------------------------------------------------------------------------------------------------------

namespace FaceTrackingBasics
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics;
    using System.Text;
    using System.Windows;
    using System.Windows.Controls;
    using System.Windows.Media;
    using Microsoft.Kinect;
    using Microsoft.Kinect.Toolkit.FaceTracking;
    using Point = System.Windows.Point;

    /// <summary>
    /// Class that uses the Face Tracking SDK to display a face mask for
    /// tracked skeletons
    /// </summary>
    public partial class FaceTrackingViewer : UserControl, IDisposable
    {
        public static readonly DependencyProperty KinectProperty = DependencyProperty.Register(
            "Kinect", 
            typeof(KinectSensor), 
            typeof(FaceTrackingViewer), 
            new PropertyMetadata(
                null, (o, args) => ((FaceTrackingViewer)o).OnSensorChanged((KinectSensor)args.OldValue, (KinectSensor)args.NewValue)));

        private const uint MaxMissedFrames = 100;

        private readonly Dictionary<int, SkeletonFaceTracker> trackedSkeletons = new Dictionary<int, SkeletonFaceTracker>();

        private byte[] colorImage;

        private ColorImageFormat colorImageFormat = ColorImageFormat.Undefined;

        private short[] depthImage;

        private DepthImageFormat depthImageFormat = DepthImageFormat.Undefined;

        private bool disposed;

        private Skeleton[] skeletonData;

        public static int FrameWidth = 640;
        public static int cnt = 0;
        public static Vector3DF rotationXyzBefore;
        public static bool before_flag = false;
        private static int num = 3;
        private static int smallnum = 3;
        public static Smoother smoothX = new Smoother(num);
        public static Smoother smoothY = new Smoother(num);
        public static Smoother smoothDepth = new Smoother(num);
        public static Smoother smoothX_motor = new Smoother(smallnum);
        public static Smoother smoothY_motor = new Smoother(smallnum);
        public static Smoother smoothDepth_motor = new Smoother(smallnum);

        
//        public static int cnt1 = 0;
//        public static int rotationAveCnt = 10;
//        public static Vector3DF[] rotationXyzSum = new Vector3DF[rotationAveCnt];
//        public static bool sum_flag = false;


        public FaceTrackingViewer()
        {
            this.InitializeComponent();
        }

        ~FaceTrackingViewer()
        {
            this.Dispose(false);
        }

        public KinectSensor Kinect
        {
            get
            {
                return (KinectSensor)this.GetValue(KinectProperty);
            }

            set
            {
                this.SetValue(KinectProperty, value);
            }
        }

        public void Dispose()
        {
            this.Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (!this.disposed)
            {
                this.ResetFaceTracking();

                this.disposed = true;
            }
        }


        protected override void OnRender(DrawingContext drawingContext)
        {
            base.OnRender(drawingContext);
            foreach (SkeletonFaceTracker faceInformation in this.trackedSkeletons.Values)
            {
                faceInformation.DrawFaceModel(drawingContext);
            }
        }

        private void OnAllFramesReady(object sender, AllFramesReadyEventArgs allFramesReadyEventArgs)
        {
            ColorImageFrame colorImageFrame = null;
            DepthImageFrame depthImageFrame = null;
            SkeletonFrame skeletonFrame = null;
            try
            {
                colorImageFrame = allFramesReadyEventArgs.OpenColorImageFrame();
                depthImageFrame = allFramesReadyEventArgs.OpenDepthImageFrame();
                skeletonFrame = allFramesReadyEventArgs.OpenSkeletonFrame();

                if (colorImageFrame == null || depthImageFrame == null || skeletonFrame == null)
                {
                    return;
                }

                // Check for image format changes.  The FaceTracker doesn't
                // deal with that so we need to reset.
                if (this.depthImageFormat != depthImageFrame.Format)
                {
                    this.ResetFaceTracking();
                    this.depthImage = null;
                    this.depthImageFormat = depthImageFrame.Format;
                }

                if (this.colorImageFormat != colorImageFrame.Format)
                {
                    this.ResetFaceTracking();
                    this.colorImage = null;
                    this.colorImageFormat = colorImageFrame.Format;
                }

                // Create any buffers to store copies of the data we work with
                if (this.depthImage == null)
                {
                    this.depthImage = new short[depthImageFrame.PixelDataLength];
                }

                if (this.colorImage == null)
                {
                    this.colorImage = new byte[colorImageFrame.PixelDataLength];
                }
                
                // Get the skeleton information
                if (this.skeletonData == null || this.skeletonData.Length != skeletonFrame.SkeletonArrayLength)
                {
                    this.skeletonData = new Skeleton[skeletonFrame.SkeletonArrayLength];
                }

                colorImageFrame.CopyPixelDataTo(this.colorImage);
                depthImageFrame.CopyPixelDataTo(this.depthImage);
                skeletonFrame.CopySkeletonDataTo(this.skeletonData);

                // Update the list of trackers and the trackers with the current frame information
                foreach (Skeleton skeleton in this.skeletonData)
                {
                    if (skeleton.TrackingState == SkeletonTrackingState.Tracked
                        || skeleton.TrackingState == SkeletonTrackingState.PositionOnly)
                    {
                        // We want keep a record of any skeleton, tracked or untracked.
                        if (!this.trackedSkeletons.ContainsKey(skeleton.TrackingId))
                        {
                            this.trackedSkeletons.Add(skeleton.TrackingId, new SkeletonFaceTracker());
                        }

                        // Give each tracker the upated frame.
                        SkeletonFaceTracker skeletonFaceTracker;
                        if (this.trackedSkeletons.TryGetValue(skeleton.TrackingId, out skeletonFaceTracker))
                        {
                            skeletonFaceTracker.OnFrameReady(this.Kinect, colorImageFormat, colorImage, depthImageFormat, depthImage, skeleton);
                            skeletonFaceTracker.LastTrackedFrame = skeletonFrame.FrameNumber;
                        }
                    }
                }

                this.RemoveOldTrackers(skeletonFrame.FrameNumber);

                this.InvalidateVisual();
            }
            finally
            {
                if (colorImageFrame != null)
                {
                    colorImageFrame.Dispose();
                }

                if (depthImageFrame != null)
                {
                    depthImageFrame.Dispose();
                }

                if (skeletonFrame != null)
                {
                    skeletonFrame.Dispose();
                }
            }
        }

        private void OnSensorChanged(KinectSensor oldSensor, KinectSensor newSensor)
        {
            if (oldSensor != null)
            {
                oldSensor.AllFramesReady -= this.OnAllFramesReady;
                this.ResetFaceTracking();
            }

            if (newSensor != null)
            {
                newSensor.AllFramesReady += this.OnAllFramesReady;
            }
        }

        /// <summary>
        /// Clear out any trackers for skeletons we haven't heard from for a while
        /// </summary>
        private void RemoveOldTrackers(int currentFrameNumber)
        {
            var trackersToRemove = new List<int>();

            foreach (var tracker in this.trackedSkeletons)
            {
                uint missedFrames = (uint)currentFrameNumber - (uint)tracker.Value.LastTrackedFrame;
                if (missedFrames > MaxMissedFrames)
                {
                    // There have been too many frames since we last saw this skeleton
                    trackersToRemove.Add(tracker.Key);
                }
            }

            foreach (int trackingId in trackersToRemove)
            {
                this.RemoveTracker(trackingId);
            }
        }

        private void RemoveTracker(int trackingId)
        {
            this.trackedSkeletons[trackingId].Dispose();
            this.trackedSkeletons.Remove(trackingId);
        }

        private void ResetFaceTracking()
        {
            foreach (int trackingId in new List<int>(this.trackedSkeletons.Keys))
            {
                this.RemoveTracker(trackingId);
            }
        }

        private class SkeletonFaceTracker : IDisposable
        {
            private static FaceTriangle[] faceTriangles;

            private EnumIndexableCollection<FeaturePoint, PointF> facePoints;

            private FaceTracker faceTracker;

            private bool lastFaceTrackSucceeded;

            private SkeletonTrackingState skeletonTrackingState;

            private int[] currentPos = new int[6];

            public int LastTrackedFrame { get; set; }

            public void Dispose()
            {
                if (this.faceTracker != null)
                {
                    this.faceTracker.Dispose();
                    this.faceTracker = null;
                }
            }

            public void DrawFaceModel(DrawingContext drawingContext)
            {
                if (!this.lastFaceTrackSucceeded || this.skeletonTrackingState != SkeletonTrackingState.Tracked)
                {
                    return;
                }
                var faceModelPts = new List<Point>();
                var faceModel = new List<FaceModelTriangle>();

                for (int i = 0; i < this.facePoints.Count; i++)
                {
                    faceModelPts.Add(new Point(this.facePoints[i].X + 0.5f, this.facePoints[i].Y + 0.5f));
                }

                foreach (var t in faceTriangles)
                {
                    var triangle = new FaceModelTriangle();
                    triangle.P1 = faceModelPts[t.First];
                    triangle.P2 = faceModelPts[t.Second];
                    triangle.P3 = faceModelPts[t.Third];
                    faceModel.Add(triangle);
                }

                var faceModelGroup = new GeometryGroup();
                for (int i = 0; i < faceModel.Count; i++)
                {
                    var faceTriangle = new GeometryGroup();
                    faceTriangle.Children.Add(new LineGeometry(faceModel[i].P1, faceModel[i].P2));
                    faceTriangle.Children.Add(new LineGeometry(faceModel[i].P2, faceModel[i].P3));
                    faceTriangle.Children.Add(new LineGeometry(faceModel[i].P3, faceModel[i].P1));
                    faceModelGroup.Children.Add(faceTriangle);
                }

                drawingContext.DrawGeometry(Brushes.LightYellow, new Pen(Brushes.LightYellow, 1.0), faceModelGroup);
            }

            public void parse(String message) {
                int[] degrees = new int[7];
                if (message != null) {
                char c;
                StringBuilder sb = new StringBuilder(4);
                int j = 0;
                bool help = false;

                for (int i = 0; i < message.Length; i++) {
                    c = message[i];
                    if (char.IsDigit(c)) {
                        sb.Append(c);
                        help = true;
                    }
                    if (!char.IsDigit(c) && help == true && j < 7) {
                        degrees[j] = int.Parse(sb.ToString());
                        j++;
                        help = false;
                        sb.Remove(0, sb.Length);
                    }
                }

                currentPos[0] = degrees[0];
                currentPos[1] = degrees[1];
                currentPos[2] = degrees[2];
                currentPos[3] = degrees[3];
                currentPos[4] = degrees[4];
                currentPos[5] = degrees[6];
            }
        }

            /// <summary>
            /// Updates the face tracking information for this skeleton
            /// </summary>
            internal void OnFrameReady(KinectSensor kinectSensor, ColorImageFormat colorImageFormat, byte[] colorImage, DepthImageFormat depthImageFormat, short[] depthImage, Skeleton skeletonOfInterest)
            {
                this.skeletonTrackingState = skeletonOfInterest.TrackingState;

                double motorXmax = 100;
                double motorYmax = 60;
                double rotationXmax = 30;
                double rotationYmax = 50;
                MainWindow.Mwindow.setMax(rotationXmax, rotationYmax);

                ////////////////////////////////////////////////////////////////////////////////
                // Send Video to Server ////////////////////////////////////////////////////////
                // MainWindow.tcpserv.sendVideo(colorImage);///////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////

                if (this.skeletonTrackingState != SkeletonTrackingState.Tracked)
                {
                    // nothing to do with an untracked skeleton.
                    return;
                }

                if (this.faceTracker == null)
                {
                    try
                    {
                        this.faceTracker = new FaceTracker(kinectSensor);
                    }
                    catch (InvalidOperationException)
                    {
                        // During some shutdown scenarios the FaceTracker
                        // is unable to be instantiated.  Catch that exception
                        // and don't track a face.
                        //Debug.WriteLine("AllFramesReady - creating a new FaceTracker threw an InvalidOperationException");
                        this.faceTracker = null;
                    }
                }

                if (this.faceTracker != null)
                {
                    FaceTrackFrame frame = this.faceTracker.Track(
                        colorImageFormat, colorImage, depthImageFormat, depthImage, skeletonOfInterest);


                    /*
                     * 頭部情報をsocketで送信
                     */
                    Vector3DF rotationXyz;
                    Vector3DF rotationXyzAve = frame.Rotation;

                    /*
                    //顔の枠の計算
                    Microsoft.Kinect.Toolkit.FaceTracking.Rect faceRect = frame.FaceRect;
                    int RectWidth = faceRect.Right - faceRect.Left;
                    int RectHeight = faceRect.Bottom - faceRect.Top;
                    int RectS = RectHeight * RectWidth;
                    */
                    double xYDist = 0;
                    rotationXyz = frame.Rotation;

                    //Console.WriteLine("rotation x:{0}, y:{1}, z:{2}", rotationXyz.X, rotationXyz.Y, rotationXyz.Z);
                    //Console.WriteLine("translat x:{0}, y:{1}, z:{2}", translationXyz.X, translationXyz.Y, translationXyz.Z);
                    //顔の各点のDepthを保持するバッファ
                    // 点の番号はUtils.cs の　public enum FeaturePoint も使える。
                    EnumIndexableCollection<FeaturePoint, Vector3DF> DepthPoints = null;
                    DepthPoints = frame.Get3DShape();
                    int pointsNumber = 108;
                    double sum = 0;
                    for (int i = 0; i < pointsNumber; i++)
                    {
                        sum += DepthPoints[i].Z;
//                        Debug.WriteLine(string.Format("({0}, {1}, {2})", DepthPoints[21].X, DepthPoints[21].Y, DepthPoints[21].Z));
                    }
                    double faceDepth = sum / pointsNumber;
                    //Console.WriteLine("depth:{0}", faceDepth);

                    /*
                    // 顔の各点の座標を保持するバッファ　(http://msdn.microsoft.com/en-us/library/jj130970.aspxFigure 2. Tracked Points)
                    // 点の番号はUtils.cs の　public enum FeaturePoint も使える。
                    EnumIndexableCollection<FeaturePoint, PointF> facePoints = null;
                    facePoints = frame.GetProjected3DShape();
                    // 顔の各点の座標を出力
                    foreach (PointF point in facePoints)
                    {
                        int index = 0;
                        Debug.WriteLine(string.Format("{0}: ({1}, {2})", index++, point.X, point.Y));
                    }
                    */

                    if (MainWindow.checkBox_face_pub.IsChecked == true)
                    {
                        if (before_flag)
                        {
                            xYDist = Math.Sqrt(Math.Pow(rotationXyz.X - rotationXyzBefore.X, 2) + Math.Pow(rotationXyz.Y - rotationXyzBefore.Y, 2));
                            //Console.WriteLine("before:{1},{2},{3}", rotationXyzBefore.X, rotationXyzBefore.Y, rotationXyzBefore.Z);
                            //Console.WriteLine("now    :{1},{2},{3}", rotationXyz.X, rotationXyz.Y, rotationXyz.Z);
                           // Console.WriteLine("Dist:{0} ", xYDist);
                            rotationXyzBefore = rotationXyz;
                        }
                        smoothX.AddQueue(rotationXyz.X);
                        smoothY.AddQueue(rotationXyz.Y);
                        smoothDepth.AddQueue(faceDepth);
                        smoothX_motor.AddQueue(rotationXyz.X);
                        smoothY_motor.AddQueue(rotationXyz.Y);
                        smoothDepth_motor.AddQueue(faceDepth);
                        if (smoothX.checkQueueCount())
                        {
//                            Console.WriteLine("ave:{0},med{1}", smoothX.GetAverage(), smoothX.GetMedian());

                            /*
                            double Xtemp = smoothX.GetAverage();
                            double Ytemp = smoothY.GetAverage();
                            double Depthtemp = smoothDepth.GetAverage();

                             */
                            double Xtemp = smoothX.GetAverage();
                            double Ytemp = smoothY.GetAverage();
                            double Depthtemp = smoothDepth.GetAverage();
                            //Xはずれ値処理
                            if (Xtemp > rotationXmax)
                                Xtemp = rotationXmax;
                            else if (Xtemp < -rotationXmax)
                                Xtemp = -rotationXmax;
                            ///Yはずれ値処理
                            if (Ytemp > rotationYmax)
                                Ytemp = rotationYmax;
                            else if (Ytemp < -rotationYmax)
                                Ytemp = -rotationYmax;
                            if (MainWindow.checkBox_movewindow_pub.IsChecked == true)
                                MainWindow.Mwindow.moveSkype(-Xtemp, -Ytemp, Depthtemp);
                            else
                                MainWindow.Mwindow.staticSkype(Depthtemp);
                           // Console.WriteLine("x:{0},y:{1},depth:{2},init:{3}", Xtemp, Ytemp, Depthtemp,MainWindow.getDepthInit());
                            MainWindow.setDepthInitTmp(Depthtemp);


                            //motor用はスムース荒く、medhianで。入れなおし
                            Xtemp = smoothX_motor.GetMedian();
                            Ytemp = smoothY_motor.GetMedian();
                            Depthtemp = smoothDepth_motor.GetMedian();
                            ///Xはずれ値処理
                            if (Xtemp > rotationXmax)
                                Xtemp = rotationXmax;
                            else if (Xtemp < -rotationXmax)
                                Xtemp = -rotationXmax;
                            ///Yはずれ値処理
                            if (Ytemp > rotationYmax)
                                Ytemp = rotationYmax;
                            else if (Ytemp < -rotationYmax)
                                Ytemp = -rotationYmax;
                              //座標送信
                            double motorX;
                            double motorY;
                            double motorZ;
                            double motorYfix; //depthに対してYを調整
                            motorX = Ytemp * (motorXmax / rotationYmax);
                            motorY = Xtemp * (motorYmax / rotationXmax);
                            motorZ = ((int)(Depthtemp*100)-80);
                            if (MainWindow.checkBox_fixYdegree_pub.IsChecked == true)
                            {
                                motorYfix = (30 / 24) * motorZ;
                                motorY = motorY - motorYfix;
                            }

                            var main = App.Current.MainWindow as MainWindow;
                            int sound_average = (int) (main.getAverage());
                            //Console.WriteLine("Sound Average: " + sound_average);
                            string message = MainWindow.tcpserv.ReadMsg();
                            parse(message);
                            MainWindow.tcpserv.SendMsg("100," + Math.Abs(100-(int)motorX) + "," + Math.Abs(100-(int)motorY) + "," + Math.Abs(100-(int)motorZ) +"," + "100" + "," + "2" + "," + sound_average);
                            //Console.WriteLine("100," + Math.Abs(100 - (int)motorX) + "," + Math.Abs(100 - (int)motorY) + "," + Math.Abs(100 - (int)motorZ) + "," + "100" + "," + "2" + "," + sound_average);
//                            Console.WriteLine("0," + -(int)motorX + "," + -(int)motorY);
//                           Console.WriteLine("R:" +  faceRect.Right + ",L:" + faceRect.Left + ",T:" + faceRect.Top + ",B:" + faceRect.Bottom + ",W:" + RectWidth + ",H:" + RectHeight + ",S:" + RectHeight*RectWidth);
//                           Console.WriteLine("Width," + (int)RectWidth + ", Height" + (int)RectHeight);
//                            MainWindow.tcpserv.SendMsg("0," + -(int)rotationXyz.Y + "," + -(int)rotationXyz.X);
//                            Console.WriteLine("0," + -(int)rotationXyz.Y + "," + -(int)rotationXyz.X);
                        }
                        rotationXyzBefore = frame.Rotation;
                        //before_flag = true;
                        //Movewindow

                        //MainWindow.Mwindow.moveSkype(-rotationXyz.X, -rotationXyz.Y, faceDepth); 
                        //MainWindow.Mwindow.moveSkype(-rotationXyz.X, -rotationXyz.Y);
                        /*
                        if (xYDist > 3)
                        {
                            if (rotationXyz.X > 40)
                            {
                                //MainWindow.tcpserv.SendMsg("0,0,-50");
                                Console.WriteLine("うんうん。Up");
                            }
                            else if (rotationXyz.X < -20)
                            {
                                // MainWindow.tcpserv.SendMsg("0,0,50");
                                Console.WriteLine("うんうん。Down");
                            }
                            else if (rotationXyz.Z > 20)
                            {
                                MainWindow.tcpserv.SendMsg("1,point_doubtfulL");
                                Console.WriteLine("え？Left");
                            }
                            else if (rotationXyz.Z < -20)
                            {
                                MainWindow.tcpserv.SendMsg("1,point_doubtfulR");
                                //MainWindow.Mwindow.Notepad();
                                Console.WriteLine("え？Right");
                            }
                            ///Xはずれ値処理
                            if (rotationXyz.X > 50)
                            {
                                rotationXyz.X = 50;
                            }
                            else if (rotationXyz.X < -50)
                            {
                                rotationXyz.X = -50;
                            }
                            ///Yはずれ値処理
                            if (rotationXyz.Y > 50)
                            {
                                rotationXyz.Y = 50;
                            }
                            else if (rotationXyz.Y < -50)
                            {
                                rotationXyz.Y = -50;
                            }
                            //座標送信
                            double motorX;
                            double motorY;
                            motorX = rotationXyz.Y * (motorXmax / rotationYmax);
                            motorY = rotationXyz.X * (motorYmax / rotationXmax);
                            //MainWindow.tcpserv.SendMsg("0," + -(int)motorX + "," + -(int)motorY);
                            MainWindow.tcpserv.SendMsg("2," + -(int)motorX + "," + -(int)motorY + "," + ((int)(faceDepth*100)-50));
//                            Console.WriteLine("0," + -(int)motorX + "," + -(int)motorY);
//                           Console.WriteLine("R:" +  faceRect.Right + ",L:" + faceRect.Left + ",T:" + faceRect.Top + ",B:" + faceRect.Bottom + ",W:" + RectWidth + ",H:" + RectHeight + ",S:" + RectHeight*RectWidth);
//                           Console.WriteLine("Width," + (int)RectWidth + ", Height" + (int)RectHeight);
//                            MainWindow.tcpserv.SendMsg("0," + -(int)rotationXyz.Y + "," + -(int)rotationXyz.X);
//                            Console.WriteLine("0," + -(int)rotationXyz.Y + "," + -(int)rotationXyz.X);


                            //Console.WriteLine("***Cnt={0}***", cnt);
                            cnt = 0;
                        }
                        else
                        {
                            cnt++;
                        }
                         * */
                    }
                    this.lastFaceTrackSucceeded = frame.TrackSuccessful;
                    if (this.lastFaceTrackSucceeded)
                    {
                        if (faceTriangles == null)
                        {
                            // only need to get this once.  It doesn't change.
                            faceTriangles = frame.GetTriangles();
                        }

                        this.facePoints = frame.GetProjected3DShape();
                    }
                }
            }

            private struct FaceModelTriangle
            {
                public Point P1;
                public Point P2;
                public Point P3;
            }
        }
    }
}