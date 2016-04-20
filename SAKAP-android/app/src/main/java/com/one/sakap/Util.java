package com.one.sakap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by cscmehmet on 12.08.2015.
 */
public class Util {

    private String output;
    private String url;

    public Util() {}

    public String GetText()
    {
        return output;
    }

    public void Run(String u)
    {
        url = u;
        output = "";

        Thread t =  new Thread() {

            public void run() {

                URL textUrl;
                try {

                    textUrl = new URL(url);

                    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(textUrl.openStream()));

                    String StringBuffer;
                    String stringText = "";

                    while ((StringBuffer = bufferReader.readLine()) != null) {
                        stringText += StringBuffer;
                    }
                    bufferReader.close();

                    output = stringText;

                } catch (Exception e) {

                    output= e.toString();
                }
            }
        };

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    public List<BusDto> GetData(String value)
    {
        List<BusDto> list = new ArrayList<BusDto>();
        BusDto busdto;
        int index = 0;

        value += "ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp";

        while (value.indexOf("name=",index) != -1)
        {
            busdto = new BusDto();

            index = value.indexOf("name=", index);
            busdto.setName(CutUntil(value.substring(index + 5, index + 15),','));

            index = value.indexOf("lat=", index);
            busdto.setX(CutUntil(value.substring(index + 4, index + 29),','));

            index = value.indexOf("lon=", index);
            busdto.setY(CutUntil(value.substring(index + 4, index + 29),','));

            index = value.indexOf("nextloc=", index);
            busdto.setNextLocation(CutUntil(value.substring(index + 8, index + 29),','));

            index = value.indexOf("prevloc=", index);
            busdto.setPrevLocation(CutUntil(value.substring(index + 8, index + 29),')'));

            index++;
            list.add(busdto);
        }
        return list;
    }

    public double getAngle(double terlat, double terlon, double buslat, double buslon)
    {
        double height   = getDistance(terlat, 0, buslat, 0);
        double width    = getDistance(10, terlon, 10, buslon);
        double arctan = height / width;

        double angle = Math.round(Math.toDegrees(Math.atan(arctan)));

        if(buslat > terlat && buslon > terlon)
            angle = angle + 0;
        else if(buslat > terlat && buslon < terlon)
            angle = 180 - angle;
        else if(buslat < terlat && buslon < terlon)
            angle = angle + 180;
        else if(buslat < terlat && buslon > terlon)
            angle = 360 - angle;
        else
            angle = angle + 0;

        return angle;
    }

    private String CutUntil(String value, char a)
    {
        String ret = "";

        for (int i=0; i<value.length(); i++)
        {
            if(value.charAt(i) == a)
                break;

            ret += value.charAt(i);
        }

        return  ret;
    }

    public static double getDistance(double lat1, double lon1, double lat2, double lon2)
    {
        double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563;
        double L = Math.toRadians(lon2 - lon1);
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
        double cosSqAlpha;
        double sinSigma;
        double cos2SigmaM;
        double cosSigma;
        double sigma;

        double lambda = L, lambdaP, iterLimit = 100;
        do
        {
            double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt(	(cosU2 * sinLambda)
                            * (cosU2 * sinLambda)
                            + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                            * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
            );
            if (sinSigma == 0)
            {
                return 0;
            }

            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = 	L + (1 - C) * f * sinAlpha
                    * 	(sigma + C * sinSigma
                    * 	(cos2SigmaM + C * cosSigma
                    * 	(-1 + 2 * cos2SigmaM * cos2SigmaM)
            )
            );

        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0)
        {
            return 0;
        }

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384
                * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma =
                B * sinSigma
                        * (cos2SigmaM + B / 4
                        * (cosSigma
                        * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                        * (-3 + 4 * sinSigma * sinSigma)
                        * (-3 + 4 * cos2SigmaM * cos2SigmaM)));

        double s = b * A * (sigma - deltaSigma);

        return s;
    }



}
