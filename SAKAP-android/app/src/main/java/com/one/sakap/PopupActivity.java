package com.one.sakap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by cscmehmet on 25.08.2015.
 */
public class PopupActivity extends Activity {

    SharedPreferences paylasilanTercihler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        TextView textView = (TextView)findViewById(R.id.textView6);
        textView.setText(SystemValues.busName);

        getWindow().setLayout((int)(width*.8), (int)(height*.6));

        final ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        toggleButton.setTextOn("FAVORİLERİMDEN ÇIKAR");
        toggleButton.setTextOff("FAVORİLERİME EKLE");

        final Button button = (Button)findViewById(R.id.button);

        paylasilanTercihler = getSharedPreferences("LocalDb",MODE_PRIVATE);
        String data = paylasilanTercihler.getString(SystemValues.busName, "notfound");

        if(!data.equals("notfound")) {
            toggleButton.setChecked(true);
            toggleButton.setBackgroundResource(R.drawable.custom_togglebutton_on);

        } else if (data.equals("notfound")) {
            toggleButton.setChecked(false);
            toggleButton.setBackgroundResource(R.drawable.custom_togglebutton);
        }

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paylasilanTercihler = getSharedPreferences("LocalDb",MODE_PRIVATE);
                String data = paylasilanTercihler.getString(SystemValues.busName, "notfound");

                if(!data.equals("notfound")) {

                    SharedPreferences.Editor editorum = paylasilanTercihler.edit();
                    editorum.remove(SystemValues.busName);
                    editorum.commit();
                    toggleButton.setBackgroundResource(R.drawable.custom_togglebutton);

                } else if (data.equals("notfound")) {

                    SharedPreferences.Editor editorum = paylasilanTercihler.edit();
                    editorum.putString(SystemValues.busName, SystemValues.busName);
                    editorum.commit();
                    toggleButton.setBackgroundResource(R.drawable.custom_togglebutton_on);

                }
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(PopupActivity.this, BusTime.class));
            }



        });

    }
}
