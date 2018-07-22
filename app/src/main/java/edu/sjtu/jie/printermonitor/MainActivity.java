package edu.sjtu.jie.printermonitor;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        String printerName = getIntent().getStringExtra("printerName");
//        TextView printerNameView = (TextView) findViewById(R.id.printer_name);
//        printerNameView.setText(printerName);

        initLayout();
    }

    /*
    初始化layout
    * */
    public void initLayout() {
        findViewById(R.id.image_iat_set).setOnClickListener(MainActivity.this);
        findViewById(R.id.printer_name).setOnClickListener(MainActivity.this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_iat_set:
                Intent intents = new Intent(MainActivity.this, PrinterSettings.class);
                startActivity(intents);
                break;
            case R.id.printer_name:
                Intent listIntent=new Intent(MainActivity.this, PrinterListActivity.class);
                startActivityForResult(listIntent,10);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10&&resultCode==9){
            String printerName=data.getStringExtra("printerName");
            TextView printerNameView = (TextView) findViewById(R.id.printer_name);
            printerNameView.setText(printerName);
        }
    }
}
