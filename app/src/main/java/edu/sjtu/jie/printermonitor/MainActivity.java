package edu.sjtu.jie.printermonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import edu.sjtu.jie.TCPCommunication.EnumsAndStatics;
import edu.sjtu.jie.TCPCommunication.TCPCommunicator;
import edu.sjtu.jie.TCPCommunication.TCPListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, TCPListener {

    private String printerName;
    private TCPCommunicator tcpClient;
    public static String S_ADDR = "106.12.17.74";
    public static int S_PORT = 8001;


    //声明组件
    private AlertDialog.Builder builder;
    private TextView printerNameView;
    private Handler UIHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();

        ConnectToServer();
    }

    private void ConnectToServer() {
        tcpClient = TCPCommunicator.getInstance();
        TCPCommunicator.addListener(this);
        tcpClient.init(S_ADDR, S_PORT);
    }

    /*
    初始化layout
    * */
    public void initLayout() {
        printerNameView = (TextView) findViewById(R.id.printer_name);
        printerName = printerNameView.getText().toString();
        findViewById(R.id.image_iat_set).setOnClickListener(MainActivity.this);
        findViewById(R.id.printer_name).setOnClickListener(MainActivity.this);
        findViewById(R.id.iat_continue).setOnClickListener(MainActivity.this);
        findViewById(R.id.iat_stop).setOnClickListener(MainActivity.this);
        findViewById(R.id.iat_off).setOnClickListener(MainActivity.this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_iat_set:
                Intent intents = new Intent(MainActivity.this, PrinterSettings.class);
                startActivity(intents);
                break;
            case R.id.printer_name:
                Intent listIntent = new Intent(MainActivity.this, PrinterListActivity.class);
                startActivityForResult(listIntent, 10);
                break;
            case R.id.iat_continue:
                showAlertDialog(view);
                break;
            case R.id.iat_stop:
                TCPCommunicator.writeToSocket(messageBuilder(EnumsAndStatics.MessageTypes.Message_Stop.toString(),
                        "Stop printing"), UIHandler, this);
                break;
            case R.id.iat_off:
                TCPCommunicator.writeToSocket(messageBuilder(EnumsAndStatics.MessageTypes.Message_Shutdown.toString(),
                        "Shutdown the printer"), UIHandler, this);
                break;
        }
    }

    public JSONObject messageBuilder(String messageType, String messageContent) {
        JSONObject jsonMesg = new JSONObject();
        try {
            jsonMesg.put(EnumsAndStatics.MESSAGE_PRINTER_NAME_FOR_JSON, printerName);
            jsonMesg.put(EnumsAndStatics.MESSAGE_TYPE_FOR_JSON, EnumsAndStatics.getMessageTypeByString(messageType));
            jsonMesg.put(EnumsAndStatics.MESSAGE_CONTENT_FOR_JSON, messageContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonMesg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 9) {
            printerName = data.getStringExtra("printerName");
            printerNameView.setText(printerName);
        }
    }

    @Override
    public void onTCPMessageRecieved(String message) {
        JSONObject msgObj;
        try {
            msgObj = new JSONObject(message);
        }catch (JSONException e){
            e.printStackTrace();
        }
//        String msgType
    }

    @Override
    public void onTCPConnectionStatusChanged(boolean isConnectedNow) {

    }

    private void showAlertDialog(View view) {
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.alert_icon);
        builder.setTitle(R.string.alert);
        builder.setMessage(R.string.alert_content);

        //监听下方button点击事件
        builder.setPositiveButton(R.string.postive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton(R.string.negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        //设置对话框是可取消的
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }
}
