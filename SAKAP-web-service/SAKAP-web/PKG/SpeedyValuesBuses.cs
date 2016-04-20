using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Web;

namespace SAKAP_web.PKG
{
    public class SpeedyValuesBuses
    {

        public delegate void Method();
        public static Method method;
        public static Thread thr;

        public static List<BusDto> busDtoList;

        public static void DGetAllBuses()
        {

            method = delegate
            {
                busDtoList = Util.Find();

            };
            (thr = new Thread(new ParameterizedThreadStart(delegate
            {
                while (true)
                {
                    method();
                    Thread.Sleep(5000);
                }
            }))).Start();
        }

    }
}