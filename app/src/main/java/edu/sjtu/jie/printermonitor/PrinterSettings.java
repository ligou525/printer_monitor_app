package edu.sjtu.jie.printermonitor;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

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
            public boolean onPreferenceChange(Preference preference, Object o) {
                int newPeriodValue = (int) 0;
                returnToMain(newPeriodValue);
                return true;
            }
        });

    }

    public void returnToMain(int newPeriodValue) {
        Toast.makeText(this,"setting - 最新period值："+String.valueOf(newPeriodValue),Toast.LENGTH_LONG).show();
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
