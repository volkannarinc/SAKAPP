using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Web;

namespace SAKAP_web.PKG
{
    public class Dao
    {
        static List<LocationDto> locationDtoList;
        static LocationDto locationDto;

        public static List<LocationDto> GetLocations(String str)
        {
            SqlConnection conn = new SqlConnection("Data Source=SQL5016.Smarterasp.net;Initial Catalog=DB_9D6034_Locations;User Id=DB_9D6034_Locations_admin;Password=awedrtgh;");//burdaki kişiler veritabanımın adıdır

            conn.Open();

            String comm = str;
            SqlCommand commandLine = new SqlCommand(comm, conn);
            SqlDataReader dr = commandLine.ExecuteReader();

            locationDtoList = new List<LocationDto>();


            while (dr.Read())
            {
                locationDto = new LocationDto();

                locationDto.LocationName    = dr["LocationName"].ToString();
                locationDto.Lat             = dr["Lat"].ToString();
                locationDto.Lon             = dr["Lon"].ToString();

                locationDtoList.Add(locationDto);
            }

            conn.Close();

            return locationDtoList;
        }


        public static void kayitIDU(string str)
        {

            //SqlConnection conn = new SqlConnection("Data Source=localhost;Initial Catalog=Cumleler;Integrated Security=True");//burdaki kişiler veritabanımın adıdır
            SqlConnection conn = new SqlConnection("Data Source=www.csogr.sakarya.edu.tr;User Id=b111210081;Password=awedrtgh ");//burdaki kişiler veritabanımın adıdır
            //SqlConnection conn = new SqlConnection("Data Source=localhost;User Id=b111210081;Password=awedrtgh ");//burdaki kişiler veritabanımın adıdır

            conn.Open();

            string comm = str;
            SqlCommand commandLine = new SqlCommand(comm, conn);
            commandLine.ExecuteNonQuery();

            conn.Close();

        }
    }
}