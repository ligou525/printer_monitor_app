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

public class PrinterListActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private Socket appSocket;
    private String s_addr = "52.53.52.20";
    private int s_port = 8001;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String[] printerList;
    private ListView listView;
    private int isSelected=0;
    private String selectedPrinterName;

    //    private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView=findViewById(R.id.listview_main);
        // get connection to message server, must be in background thread
//        try {
//            appSocket = new Socket(s_addr, s_port);
//            Log.i(TAG, "Server connection successful: "+s_addr);
//            bufferedWriter = new BufferedWriter(new OutputStreamWriter(appSocket.getOutputStream()));
//            bufferedReader = new BufferedReader(new InputStreamReader(appSocket.getInputStream()));
//
//            // send initial identification message
////                            bufferedWriter.write("app\n");
////                            bufferedWriter.flush();
//            // 想打印机发送获取打印机列表请求
//            bufferedWriter.write("RequestList\n");
//            int numPrinter=Integer.parseInt(bufferedReader.readLine());
//            printerList=new String[numPrinter];
//            for(int i=0;i<numPrinter;i++){
//                printerList[i]=bufferedReader.readLine();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        listView.setAdapter(new ArrayAdapter<>(this, R.layout.item_view, R.id.lv_name, objects));

        printerList=new String[]{"printer1","printer2","printer3","printer4"};
        listView.setAdapter(new ArrayAdapter<>(this,R.layout.item_view,R.id.lv_name,printerList));
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                isSelected=1;
                Object o = listView.getItemAtPosition(position);
                selectedPrinterName=(String)o;
                returnToMain();

            }
        });



    }

    public void returnToMain(){
        Intent statusIntent=new Intent(PrinterListActivity.this, MainActivity.class);
        statusIntent.putExtra("printerName",selectedPrinterName);
        this.setResult(9,statusIntent);
        PrinterListActivity.this.finish();
    }

}
