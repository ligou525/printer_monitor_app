package edu.sjtu.jie.printermonitor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.util.prefs.Preferences;

import edu.sjtu.jie.TCPCommunication.EnumsAndStatics;
import edu.sjtu.jie.util.SettingTextWatcher;


/**
 * 听写设置界面
 */
public class PrinterSettings extends PreferenceActivity implements OnPreferenceChangeListener {

    public static final String PREFER_NAME = "edu.sjtu.jie.setting";
    private EditTextPreference timePeriodPreference;

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(PREFER_NAME);
        addPreferencesFromResource(R.xml.printer_setting);

        timePeriodPreference = (EditTextPreference) findPreference("printer_time_period_preference");


        int updatePeriod = getIntent().getIntExtra("updatePeriod", 30);
        timePeriodPreference.setSummary("当前更新时间间隔： " + updatePeriod + "s");

        timePeriodPreference.getEditText().addTextChangedListener(new SettingTextWatcher(PrinterSettings.this, timePeriodPreference, 0, 1000));


        timePeriodPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object o) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PrinterSettings.this);
                Dialog changeUpdatePeriodConfirmationDialog = builder
                        .setCancelable(true)
//                        .setTitle("Change Setting")
                        .setMessage("确定要更改时间间隔参数吗")
                        .setPositiveButton(R.string.postive_button, new Dialog.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //Because I return false anyway, I change the preference manually here
                                int newPeriodValue=0;
                                if(timePeriodPreference.getText() != null){
                                    newPeriodValue = Integer.parseInt(timePeriodPreference.getText());
                                }
                                timePeriodPreference.setSummary("当前更新时间间隔： " + newPeriodValue + "s");
                                returnToMain(newPeriodValue);
                            }
                        }).setNegativeButton(R.string.negative_button, null)
                        .create();
                changeUpdatePeriodConfirmationDialog.show();
                return true;

            }
        });

    }

    public void returnToMain(int newPeriodValue) {
        Intent periodIntent = new Intent(PrinterSettings.this, MainActivity.class);
        periodIntent.putExtra("updateperiod", newPeriodValue);
        this.setResult(EnumsAndStatics.PERIOD_RESULT_CODE, periodIntent);
        PrinterSettings.this.finish();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }
}
