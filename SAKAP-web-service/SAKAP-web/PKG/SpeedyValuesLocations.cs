using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Web;

namespace SAKAP_web.PKG
{
    public class SpeedyValuesLocations
    {
        public delegate void Method();
        public static Method method;
        public static Thread thr;

        public static List<LocationDto> locationDtoList;

        public static void DGetAllLocations()
        {

            method = delegate
            {
                locationDtoList = Dao.GetLocations("Select LocationName,Lat,Lon,Bus From Locations");

            };
            (thr = new Thread(new ParameterizedThreadStart(delegate
            {
                while (true)
                {
                    method();
                    Thread.Sleep(60000);
                }
            }))).Start();
        }


    }
}