package com.inopek.duvana.sink.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.inopek.duvana.sink.BuildConfig;
import com.inopek.duvana.sink.R;

import org.apache.commons.lang3.StringUtils;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView versionCodeValue = (TextView) findViewById(R.id.versionCode);
        versionCodeValue.setText(StringUtils.SPACE + String.valueOf(BuildConfig.VERSION_CODE));
        TextView versionValue = (TextView) findViewById(R.id.versionName);
        versionValue.setText(StringUtils.SPACE + BuildConfig.VERSION_NAME);
    }
}
