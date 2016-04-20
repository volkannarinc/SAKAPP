package com.one.sakap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    AppLocationService appLocationService;
    static double latitude = 0;
    static double longitude = 0;
    Util u = new Util();
    List<BusDto> busdto;
    List<BusDto> busDtoList;

    ListView lv;
    ListView lv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv  = (ListView) findViewById(R.id.listView);
        lv2 = (ListView) findViewById(R.id.listView2);

        final TextView textViewGps = (TextView)findViewById(R.id.textView7);

        TabHost tabhost= (TabHost) findViewById(R.id.tabHost);
        tabhost.setup();

        TabHost.TabSpec tabOzellikleri;

        tabOzellikleri=tabhost.newTabSpec("tag1");
        tabOzellikleri.setContent(R.id.tab1);
        tabOzellikleri.setIndicator("En Yakın Otobüsler");
        tabhost.addTab(tabOzellikleri);

        tabOzellikleri=tabhost.newTabSpec("tag1");
        tabOzellikleri.setContent(R.id.tab2);
        tabOzellikleri.setIndicator("Favorilerim");
        tabhost.addTab(tabOzellikleri);

        Timer t = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        u.Run("http://onegamesoft-001-site1.smarterasp.net/");
                        busdto = u.GetData(u.GetText());

                        for (int i = 0; i < busdto.size(); i++) {

                            double latbus = Double.valueOf(busdto.get(i).getY());       // TODO incele null geliyor. Çözüldü=servisten kaynaklı
                            double lonbus = Double.valueOf(busdto.get(i).getX());

                            BusDto b = new BusDto();
                            b.setName(busdto.get(i).getName());
                            b.setX(busdto.get(i).getX());
                            b.setY(busdto.get(i).getY());
                            Double distance = Double.valueOf(Math.round(Util.getDistance(latitude, longitude, latbus, lonbus)));
                            b.setDistance(distance);
                            b.setNextLocation(busdto.get(i).getNextLocation());
                            b.setPrevLocation(busdto.get(i).getPrevLocation());

                            busdto.set(i, b);
                        }

                        int index = 0;
                        int top = 0;

                        try {
                            index = lv.getFirstVisiblePosition();
                            View v = lv.getChildAt(0);
                            top = (v == null) ? 0 : (v.getTop() - lv.getPaddingTop());
                        } catch (Exception e) {

                        }

                        lv.setAdapter(new CustomAdapter(MainActivity.this, busdto));
                        lv.setSelectionFromTop(index, top);

                        //---------

                        busDtoList = new ArrayList<BusDto>();

                        SharedPreferences paylasilanTercihler;
                        paylasilanTercihler = getSharedPreferences("LocalDb",MODE_PRIVATE);

                        for(BusDto bus : busdto) {

                            String data = paylasilanTercihler.getString(bus.getName(), "notfound");

                            if(!data.equals("notfound")) {

                                busDtoList.add(bus);
                            }
                        }

                        int index2 = 0;
                        int top2 = 0;

                        try {
                            index2 = lv2.getFirstVisiblePosition();
                            View v2 = lv2.getChildAt(0);
                            top2 = (v2 == null) ? 0 : (v2.getTop() - lv2.getPaddingTop());
                        } catch (Exception e) {

                        }

                        lv2.setAdapter(new CustomAdapter(MainActivity.this, busDtoList));
                        lv2.setSelectionFromTop(index2, top2);

                        LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
                        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                        if(!isGPSEnabled) {
                            textViewGps.setText("En yakın otobüslerin sıralanabilmesi için konumunuzu açınız.");
                        } else if(isGPSEnabled && latitude == 0) {
                            textViewGps.setText("Yakınlık Sıralaması:           Konum Alınıyor");
                            setLocation();
                        } else if(isGPSEnabled && latitude != 0) {
                            textViewGps.setText("Yakınlık Sıralaması:           Etkin");

                        }

                    }
                });
            }
        };

        Timer t2 = new Timer();
        TimerTask task2 = new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        setLocation();
                    }
                });
            }
        };

        t2.scheduleAtFixedRate(task2, 0, 60000);
        t.scheduleAtFixedRate(task, 0, 5000);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {

                //Toast.makeText(getApplicationContext(), busdto.get(pos).getName(), Toast.LENGTH_LONG).show();

                SystemValues.busName = busdto.get(pos).getName();
                startActivity(new Intent(MainActivity.this, PopupActivity.class));

                return true;
            }
        });
        lv2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {

                //Toast.makeText(getApplicationContext(), busdto.get(pos).getName(), Toast.LENGTH_LONG).show();

                SystemValues.busName = busDtoList.get(pos).getName();
                startActivity(new Intent(MainActivity.this, PopupActivity.class));

                return true;
            }
        });

    }

    void setLocation() {

        appLocationService = new AppLocationService(MainActivity.this);
        Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {
            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();

            //Toast.makeText(getApplicationContext(), "GPS Acik", Toast.LENGTH_LONG).show();
            //textView.setText("Sıralama: Yakından uzağa");

        } else {
            //Toast.makeText(getApplicationContext(), "Otobüs yakınlık sıralamasını görebilmek için GPS açınız.", Toast.LENGTH_LONG).show();
            //textView.setText("Sıralama: Devre Dışı(GPS Açınız)");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.hakkinda:
                Intent hakkimdaIntent=new Intent("android.intent.action.HAKKINDA");
                startActivity(hakkimdaIntent);
                break;

            case R.id.cikis:
                finish();
                break;
        }
        return false;
    }


}
