package edu.sjtu.jie.printermonitor;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.Window;

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
		
		timePeriodPreference = (EditTextPreference)findPreference("printer_time_period_preference");
		timePeriodPreference.getEditText().addTextChangedListener(new SettingTextWatcher(PrinterSettings.this, timePeriodPreference,0,10000));
		
			}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}
}
