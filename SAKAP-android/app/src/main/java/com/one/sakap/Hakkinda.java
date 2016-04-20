package com.one.sakap;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Narinc on 29.8.2015.
 */
public class Hakkinda extends Activity{

    TextView S1,S2,S4,S5,C1,C2,C4,C5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hakkinda);

        S1 = (TextView) findViewById(R.id.tvSoru1);
        S2 = (TextView) findViewById(R.id.tvSoru2);
        S4 = (TextView) findViewById(R.id.tvSoru4);
        S5 = (TextView) findViewById(R.id.tvSoru5);


        C1 = (TextView) findViewById(R.id.tvCevap1);
        C2 = (TextView) findViewById(R.id.tvCevap2);
        C4 = (TextView) findViewById(R.id.tvCevap4);
        C5 = (TextView) findViewById(R.id.tvCevap5);





    }
}
