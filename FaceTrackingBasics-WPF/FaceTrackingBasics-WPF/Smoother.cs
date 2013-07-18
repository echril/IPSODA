using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FaceTrackingBasics
{
    public class Smoother
    {
        Queue<double> queue;
        int queMax;
        public Smoother(int num)
        {
            queue = new Queue<double>(num);
            queMax = num;
        }

        public void AddQueue(double value) {
            queue.Enqueue(value);
            if (queue.Count > queMax)
            {
                queue.Dequeue();
            }
        }
        public void ShowQueue()
        {
            //Console.WriteLine("Queue[{0}]", queue.Count);
            double[] temp = new double[queue.Count];
            queue.CopyTo(temp, 0);
            for (int i = 0; i < temp.Length; ++i)
            {
                //Console.Write("[{0}]:{1}", i, temp[i]);
            }
            //Console.WriteLine("");
        }
        public double GetAverage()
        {
            double average = 0;
            double[] temp = new double[queue.Count];
            queue.CopyTo(temp, 0);
            for (int i = 0; i < temp.Length; ++i)
            {
                average += temp[i];
            }
            return average / queue.Count;
        }
        public double GetMedian()
        {
            double[] temp = new double[queue.Count];
            queue.CopyTo(temp, 0);
            Array.Sort(temp);
            return temp[(int)queue.Count/2];
        }
        public Boolean checkQueueCount()
        {
            if (queue.Count == queMax)
                return true;
            else
                return false;
        }

    }
}
