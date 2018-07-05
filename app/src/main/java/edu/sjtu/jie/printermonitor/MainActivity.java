package edu.sjtu.jie.printermonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private Socket appSocket;
    private String s_addr = "52.53.52.20";
    private int s_port = 8001;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String[] printerList;
    private ListView listView;
//    private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        printerList=new String[]{"printer1","printer2","printer3","printer4"};
//        listView.setAdapter(new ArrayAdapter<>(this, R.layout.item_view, R.id.lv_name, objects));
        listView.setAdapter(new ArrayAdapter<>(this,R.layout.item_view,R.id.lv_name,printerList));
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Object o = listView.getItemAtPosition(position);
                String printerName=(String)o;//As you are using Default String Adapter
                Intent statusIntent=new Intent(MainActivity.this,Printer_Status.class);
                statusIntent.putExtra("printerName",printerName);
                startActivity(statusIntent);
            }
        });
    }
}
