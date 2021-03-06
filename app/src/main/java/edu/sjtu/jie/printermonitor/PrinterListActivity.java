package edu.sjtu.jie.printermonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Printer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import edu.sjtu.jie.TCPCommunication.EnumsAndStatics;

public class PrinterListActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private ListView listView;

    private String selectedPrinterName;

    //    private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        listView = findViewById(R.id.listview_main);

       ArrayList<String> printerList=getIntent().getStringArrayListExtra("printerList");
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.item_view, R.id.lv_name, printerList));
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object o = listView.getItemAtPosition(position);
                selectedPrinterName = (String) o;
                returnToMain();
            }
        });


    }

    public void returnToMain() {
        Intent statusIntent = new Intent(PrinterListActivity.this, MainActivity.class);
        statusIntent.putExtra("printerName", selectedPrinterName);
        this.setResult(EnumsAndStatics.LIST_RESULT_CODE, statusIntent);
        PrinterListActivity.this.finish();
    }

    public static void addPrinter(String printerName){

    }

}
