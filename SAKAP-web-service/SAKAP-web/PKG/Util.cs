using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Web;

namespace SAKAP_web.PKG
{
    public class Util
    {
        static BusDto busDto;

        private static String GetData()
        {
            WebRequest request = WebRequest.Create("http://sakus.sakarya.bel.tr/Proxy/proxy.ashx?url=http%3A%2F%2Flocalhost%3A8080%2Fgeoserver%2Fwfs");
            request.Method = "POST";

            string postData = "<wfs:GetFeature xmlns:wfs=\"http://www.opengis.net/wfs\" service=\"WFS\" version=\"1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-transaction.xsd\"><wfs:Query typeName=\"feature:BelediyeOtobus142\" xmlns:feature=\"http://localhost:8080/SBB\"><ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"><ogc:BBOX><ogc:PropertyName>geom</ogc:PropertyName><gml:Box xmlns:gml=\"http://www.opengis.net/gml\" srsName=\"EPSG:4326\"><gml:coordinates decimal=\".\" cs=\",\" ts=\" \">30.14148480249,40.738828032181 30.60462719751,40.777836537157</gml:coordinates></gml:Box></ogc:BBOX></ogc:Filter></wfs:Query></wfs:GetFeature>";
            byte[] byteArray = Encoding.UTF8.GetBytes(postData);

            request.ContentType = "application/xml";
            request.ContentLength = byteArray.Length;

            Stream dataStream = request.GetRequestStream();
            dataStream.Write(byteArray, 0, byteArray.Length);
            dataStream.Close();
            WebResponse response = request.GetResponse();

            Console.WriteLine(((HttpWebResponse)response).StatusDescription);
            dataStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(dataStream);

            string responseFromServer = reader.ReadToEnd();
            Console.WriteLine(responseFromServer);

            reader.Close();
            dataStream.Close();
            response.Close();

            return responseFromServer;
        }

        public static List<BusDto> Find()
        {
            Thread.CurrentThread.CurrentCulture = new CultureInfo("en-US");

            int index = 0;
            String value = GetData();
            List<BusDto> list = new List<BusDto>();

            while (true)
            {
                busDto = new BusDto();

                index = value.IndexOf("<SBB:nhat_no>", index);
                busDto.name = CutUntilChar(value.Substring(index + 13, 10), '<');

                index++;

                if (busDto.name.Contains("n="))
                    break;

                index = value.IndexOf("ts", index);
                busDto.x = CutUntilChar(value.Substring(index + 7, 25), ',');
                busDto.y = BeginCutCommaToEnd(value.Substring(index + 7, 25));

                index++;

                index = value.IndexOf("<SBB:naci>", index);
                String temp = CutUntilChar(value.Substring(index + 10), '<');
                Double a = Convert.ToDouble(temp);                              // culture hatasi
                a = (((a - 90) * -1) + 360) % 360;
                busDto.angle = a.ToString();

                List<LocationDto> locationDtoList = Dao.GetLocations("Select LocationName,Lat,Lon From Locations");
                busDto.nextLocation = GetLocationNext(locationDtoList, busDto);

                list.Add(busDto);
            }

            return list;
        }

        public static String GetLocationNext(List<LocationDto> locationDtoList, BusDto busDto)
        {
            Thread.CurrentThread.CurrentCulture = new CultureInfo("en-US");

            List<LocationDto> nextLocationList = new List<LocationDto>();
            Double buslat = Convert.ToDouble(busDto.y);
            Double buslon = Convert.ToDouble(busDto.x);
            Double poilat;
            Double poilon;
            Double angleTan;
            Double angleRangeMin;
            Double angleRangeMax;
            Double angleBus = Convert.ToDouble(busDto.angle);
            int a;

            foreach (LocationDto locationDto in locationDtoList)
            {
                poilat = Convert.ToDouble(locationDto.Lat);
                poilon = Convert.ToDouble(locationDto.Lon);

                angleTan = GetAngleTan(poilat, poilon, buslat, buslon);

                angleRangeMin = (angleTan - 90);
                angleRangeMax = (angleTan + 90);

                if (angleBus > angleRangeMin && angleBus < angleRangeMax)
                    a = 1;
                else
                    nextLocationList.Add(locationDto);

            }

            LocationDto nearest = new LocationDto();
            Double smallest = int.MaxValue;
            Double distance;

            foreach (LocationDto locationDto in nextLocationList)
            {
                poilat = Convert.ToDouble(locationDto.Lat);
                poilon = Convert.ToDouble(locationDto.Lon);

                distance = FindMeters(poilat, poilon, buslat, buslon);

                if (distance < smallest)
                {
                    nearest = locationDto;
                    smallest = distance;
                }
            }

            return nearest.LocationName;
        }

        public static double GetAngleTan(double poilat, double poilon, double buslat, double buslon)
        {
            Thread.CurrentThread.CurrentCulture = new CultureInfo("en-US");

            double height = FindMeters(Convert.ToDouble(poilat), 40.0, Convert.ToDouble(buslat), 40.0);
            double width = FindMeters(30.0, poilon, 30.0, buslon);
            double arctan = height / width;

            double angle = ((Math.Atan(arctan)* 180)) / Math.PI;            // TODO radiandan degreeye cevırme

            if (buslat > poilat && buslon > poilon)
                angle = angle + 0;
            else if (buslat > poilat && buslon < poilon)
                angle = 180 - angle;
            else if (buslat < poilat && buslon < poilon)
                angle = angle + 180;
            else if (buslat < poilat && buslon > poilon)
                angle = 360 - angle;
            else
                angle = angle + 0;

            return angle;
        }

        private static String CutUntilChar(String value, char v)
        {
            String ret = "";

            for (int i = 0; i < value.Length; i++)
            {
                if (value[i] != v)
                {
                    ret += value[i];
                }
                else
                {
                    break;
                }
            }

            return ret;
        }

        private static String BeginCutCommaToEnd(String value)
        {
            bool key = false;
            String ret = "";

            for (int i = 0; i < value.Length; i++)
            {
                if (value[i] == '<')
                {
                    break;
                }

                if (key)
                {
                    ret += value[i];
                }

                if (value[i] == ',')
                {
                    key = true;
                }
            }

            return ret;
        }

        public static Double FindMeters(Double lat1, Double lon1, Double lat2, Double lon2)
        {
            Double value = VincentyDistance(lat1, lon1, lat2, lon2);  //40.761990, 30.361592

            return value;
        }

        private static Double DotToComma(String value)
        {
            String ret = "";

            for (int i = 0; i < value.Length; i++)
            {
                if (value[i] == '.')
                {
                    ret += ',';
                }
                else
                {
                    ret += value[i];
                }
            }

            return Convert.ToDouble(ret);
        }

        public static double VincentyDistance(double lat1, double lon1, double lat2, double lon2)
        {
            double a = 6378137, b = 6356752.3142, f = 1 / 298.257223563;  // WGS-84 ellipsoid params
            double L = DegToRad(lon2 - lon1);
            double U1 = Math.Atan((1 - f) * Math.Tan(DegToRad(lat1)));
            double U2 = Math.Atan((1 - f) * Math.Tan(DegToRad(lat2)));
            double sinU1 = Math.Sin(U1), cosU1 = Math.Cos(U1);
            double sinU2 = Math.Sin(U2), cosU2 = Math.Cos(U2);

            double cosSigma;
            double sigma;
            double sinAlpha;
            double cosSqAlpha;
            double cos2SigmaM;
            double sinLambda;
            double sinSigma;
            double cosLambda;

            double lambda = L, lambdaP, iterLimit = 100;

            do
            {
                sinLambda = Math.Sin(lambda);
                cosLambda = Math.Cos(lambda);
                sinSigma = Math.Sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) +
                  (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));

                if (sinSigma == 0)
                    return 0;  // co-incident points

                cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
                sigma = Math.Atan2(sinSigma, cosSigma);
                sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
                cosSqAlpha = 1 - sinAlpha * sinAlpha;
                cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

                if (Double.IsNaN(cos2SigmaM))
                    cos2SigmaM = 0;  // equatorial line: cosSqAlpha=0 (§6)

                double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
                lambdaP = lambda;
                lambda = L + (1 - C) * f * sinAlpha *
                  (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));

            } while (Math.Abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

            if (iterLimit == 0)
                return Double.NaN;  // formula failed to converge

            double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
            double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
            double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
            double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
            double s = b * A * (sigma - deltaSigma);

            return s;
        }

        private static double DegToRad(double deg)
        {
            return (deg * Math.PI / 180.0);
        }

        private static double RadToDeg(double rad)
        {
            return (rad / Math.PI * 180.0);
        }


    }
}