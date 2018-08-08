package edu.sjtu.jie.printermonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;

import edu.sjtu.jie.TCPCommunication.EnumsAndStatics;
import edu.sjtu.jie.TCPCommunication.TCPCommunicator;
import edu.sjtu.jie.TCPCommunication.TCPListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, TCPListener {

    private String printerName;
    private TCPCommunicator tcpClient;
    public static String S_ADDR = "106.12.17.74";
    public static int S_PORT = 8010;
    private static ArrayList<String> printerList = new ArrayList<>();
    private int updatePeriod = 30;
    private boolean isFirstList = true;

    //声明组件
    private AlertDialog.Builder builder;
    private TextView printerNameView;
    private ImageView statusImageView;
    private EditText statusEditText;
    private Handler UIHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLayout();
//        initPrinterList();
        ConnectToServer();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TCPCommunicator.writeToSocket(messageBuilder(EnumsAndStatics.MessageTypes.Online.toString(),
                "I am online", "server"), UIHandler, this);
    }

    public void initPrinterList() {
        String[] printerNameList = new String[]{"printer1", "printer2", "printer3", "printer4"};
        for (String printer : printerNameList) {
            printerList.add(printer);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TCPCommunicator.closeStreams();
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
        statusImageView = (ImageView) findViewById(R.id.print_status_img);
        statusEditText = (EditText) findViewById(R.id.iat_text);
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
                Intent periodInetent = new Intent(MainActivity.this, PrinterSettings.class);
                periodInetent.putExtra("updatePeriod", updatePeriod);
                startActivityForResult(periodInetent, EnumsAndStatics.PERIOD_REQUEST_CODE);
//                setUpdatePeriodDialog(view);
                break;
            case R.id.printer_name:
                Intent listIntent = new Intent(MainActivity.this, PrinterListActivity.class);
                listIntent.putStringArrayListExtra("printerList", printerList);
                startActivityForResult(listIntent, EnumsAndStatics.LIST_REQUEST_CODE);
                break;
            case R.id.iat_continue:
                showAlertDialog();
                TCPCommunicator.writeToSocket(messageBuilder(EnumsAndStatics.MessageTypes.Continue.toString(),
                        "Stop printing",printerName), UIHandler, this);
                break;
            case R.id.iat_stop:
                TCPCommunicator.writeToSocket(messageBuilder(EnumsAndStatics.MessageTypes.Stop.toString(),
                        "Stop printing", printerName), UIHandler, this);
                break;
            case R.id.iat_off:
                TCPCommunicator.writeToSocket(messageBuilder(EnumsAndStatics.MessageTypes.Shutdown.toString(),
                        "Shutdown the printer", printerName), UIHandler, this);
                break;
        }
    }

    public JSONObject messageBuilder(String messageType, String messageContent, String recver) {
        JSONObject jsonMesg = new JSONObject();
        try {
            jsonMesg.put(EnumsAndStatics.MESSAGE_SENDER, "app");
            jsonMesg.put(EnumsAndStatics.MESSAGE_RECEIVER, recver);
            jsonMesg.put(EnumsAndStatics.MESSAGE_TYPE_FOR_JSON, messageType);
            jsonMesg.put(EnumsAndStatics.MESSAGE_CONTENT_FOR_JSON, messageContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonMesg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EnumsAndStatics.LIST_REQUEST_CODE && resultCode == EnumsAndStatics.LIST_RESULT_CODE) {
            printerName = data.getStringExtra("printerName");
            printerNameView.setText(printerName);
        } else if (requestCode == EnumsAndStatics.PERIOD_REQUEST_CODE && resultCode == EnumsAndStatics.PERIOD_RESULT_CODE) {
            updatePeriod = data.getIntExtra("updateperiod", 30);
            Toast.makeText(this, "main - 最新period值：" + String.valueOf(updatePeriod), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTCPMessageReceived(String message) {
        JSONObject msgObj;
        try {
            msgObj = new JSONObject(message);
            String printer = msgObj.getString(EnumsAndStatics.MESSAGE_SENDER);
            EnumsAndStatics.MessageTypes msgType = EnumsAndStatics.getMessageTypeByString(msgObj.
                    getString(EnumsAndStatics.MESSAGE_TYPE_FOR_JSON));
            switch (msgType) {
                case PrinterStatus:
                    JSONObject msgContent = msgObj.getJSONObject(EnumsAndStatics.MESSAGE_CONTENT_FOR_JSON);
                    String statusImg = msgContent.getString(EnumsAndStatics.MESSAGE_STATUS_IMG_FOR_JSON);
                    String statusText = msgContent.getString(EnumsAndStatics.MESSAGE_STATUS_TEXT_FOR_JSON);
                    int statusCode = msgContent.getInt(EnumsAndStatics.MESSAGE_STATUS_CODE);
                    // 图像预处理
                    byte[] raw_data = Base64.decode(statusImg, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(raw_data, 0, raw_data.length);
//                    img.compress(Bitmap.CompressFormat.PNG, 100, byteBuffer);
                    if (!printer.equals(printerName)) {
                        showPrinterChangedAlertDialog(printer);
                        if (printer.equals(printerName)) {
                            statusImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, statusImageView.getWidth(),
                                    statusImageView.getHeight(), false));
                        } else {
                            showAlertDialog();
                        }
                    } else {
                        statusImageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, statusImageView.getWidth(),
                                statusImageView.getHeight(), false));
                    }
                    statusEditText.setText(statusText);
                    if (statusCode == 1) {
                        statusEditText.setTextColor(Color.RED);
                    } else {
                        statusEditText.setTextColor(Color.BLACK);
                    }
                    break;
                case UpdateList:
                    JSONArray printers = msgObj.getJSONArray(EnumsAndStatics.MESSAGE_CONTENT_FOR_JSON);
                    addPrinter(printers);
                    break;
                case UpdatePeriod:
                    String rcvdUpdatePeriod= msgObj.getString(EnumsAndStatics.MESSAGE_CONTENT_FOR_JSON);
                    statusEditText.append("\n\n处理结果：" + rcvdUpdatePeriod);
                    break;
                case Stop:
                    String rcvdMsgStop = msgObj.getString(EnumsAndStatics.MESSAGE_CONTENT_FOR_JSON);
                    statusEditText.append("\n\n处理结果：" + rcvdMsgStop);
                    break;
                case Shutdown:
                    String rcvdMsgOff = msgObj.getString(EnumsAndStatics.MESSAGE_CONTENT_FOR_JSON);
                    statusEditText.append("\n\n处理结果：" + rcvdMsgOff);
                    break;
                case Continue:
                    String rcvdMsgContinue= msgObj.getString(EnumsAndStatics.MESSAGE_CONTENT_FOR_JSON);
                    statusEditText.append("\n\n处理结果：" + rcvdMsgContinue);
                    break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTCPConnectionStatusChanged(boolean isConnectedNow) {

    }

    public void addPrinter(JSONArray printers) {
        try {
            if (isFirstList) {
                for (int i = 0; i < printers.length(); i++) {
                    printerList.add(printers.get(i).toString());
                }
                printerName=printerList.get(0);
                printerNameView.setText(printerName);
                isFirstList=false;
            } else {
                for (int i = 0; i < printers.length(); i++) {
                    if (!printerList.contains(printers.get(i).toString())) {
                        printerList.add(printers.get(i).toString());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog() {
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

    private void showPrinterChangedAlertDialog(final String rcvdPrinterName) {
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.alert_icon);
        builder.setTitle(R.string.attention);
        builder.setMessage(R.string.switch_printer);
        final String rcvdPrinter = rcvdPrinterName;

        //监听下方button点击事件
        builder.setPositiveButton(R.string.postive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                printerNameView.setText(rcvdPrinter);
                printerName = rcvdPrinter;
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

//    private void setUpdatePeriodDialog(View view) {
//        builder=new AlertDialog.Builder(this);
//        builder.setIcon(R.mipmap.ic_launcher);
//        builder.setTitle(R.string.periodDialogTitle);
//
//        LinearLayout loginDialog= (LinearLayout) getLayoutInflater().inflate(R.layout.set_update_period_view,null);
//        builder.setView(loginDialog);
//
//        builder.setCancelable(false);
//        AlertDialog dialog=builder.create();
//
//        final EditText periodEditText=(EditText) findViewById(R.id.period_editText);
//        Button confirmButthon=(Button) findViewById(R.id.period_comfirm);
//        Button cancelButthon=(Button) findViewById(R.id.period_cancel);
//        periodEditText.addTextChangedListener(new SettingTextWatcher(MainActivity.this, periodEditText,0,1000));
//        confirmButthon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(periodEditText.getText().equals("")){
//                    Toast.makeText(MainActivity.this,"时间间隔不能为空，请重新输入！",Toast.LENGTH_LONG);
//                }else{
//                    if(updatePeriod!=Integer.parseInt(periodEditText.getText().toString())) {
//                        updatePeriod = Integer.parseInt(periodEditText.getText().toString());
//                        TCPCommunicator.writeToSocket(messageBuilder(EnumsAndStatics.MessageTypes.MessageUpdatePeriod.toString(),
//                                String.valueOf(updatePeriod)), UIHandler, MainActivity.this);
//                    }
//                }
//            }
//        });
//
//        dialog.show();
//    }
}
