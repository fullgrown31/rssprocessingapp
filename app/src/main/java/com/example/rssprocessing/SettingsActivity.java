package com.example.rssprocessing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {

    private EditText etTitleFontSize, etPubDateFontSize;
    private RadioGroup rgTitleButtons, rgPubDateButtons;
    private RadioButton rbTitleBlack, rbTitleRed, rbTitleGreen,
                        rbPubDateBlack, rbPubdateRed, rbPubDateGreen;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.setTitle("Settings");
        etTitleFontSize = findViewById(R.id.etTitleFontSize);
        etPubDateFontSize = findViewById(R.id.etPubDateFontSize);

        rbTitleBlack = findViewById(R.id.rbTitleBlack);
        rbTitleRed = findViewById(R.id.rbTitleRed);
        rbTitleGreen = findViewById(R.id.rbTitleGreen);

        rbPubDateBlack = findViewById(R.id.rbPubDateBlack);
        rbPubdateRed = findViewById(R.id.rbPubDateRed);
        rbPubDateGreen = findViewById(R.id.rbPubDateGreen);

        rgTitleButtons = findViewById(R.id.rgTitleButtons);
        rgPubDateButtons = findViewById(R.id.rgPubDateButtons);

        sharedPreferences = getSharedPreferences("general prefs", MODE_PRIVATE);

        try {
            Log.d("Dwight", "Title Size: " + sharedPreferences.getInt("titleSize", 0));
            etTitleFontSize.setText(String.valueOf(sharedPreferences.getInt("titleSize", 20)));
            etPubDateFontSize.setText(String.valueOf(sharedPreferences.getInt("pubDateSize", 14)));

            int titleColor = sharedPreferences.getInt("titleColor", 0);
            int pubDateColor = sharedPreferences.getInt("pubDateColor", 0);

            if(titleColor == 0)
            {
                rgTitleButtons.check(R.id.rbTitleBlack);
            }
            else if(titleColor == 1)
            {
                rgTitleButtons.check(R.id.rbTitleRed);
            }
            else if(titleColor == 0)
            {
                rgTitleButtons.check(R.id.rbTitleGreen);
            }

            if(pubDateColor == 0)
            {
                rgPubDateButtons.check(R.id.rbPubDateBlack);
            }
            else if(pubDateColor == 1)
            {
                rgPubDateButtons.check(R.id.rbPubDateRed);
            }
            else if(pubDateColor == 2)
            {
                rgPubDateButtons.check(R.id.rbPubDateGreen);
            }
        } catch (Exception e)
        {
            Log.d("Dwight", e.toString());
        }

    }

    @Override
    public void onBackPressed() {
        int titleFontSize = Integer.valueOf(etTitleFontSize.getText().toString());
        int pubDateFontSize = Integer.valueOf(etPubDateFontSize.getText().toString());


        //Save
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("titleSize", titleFontSize);
        editor.putInt("pubDateSize", pubDateFontSize);


        Intent intent = new Intent();
        intent.putExtra("titleSize", titleFontSize);
        intent.putExtra("pubDateSize", pubDateFontSize);

        if(rbTitleBlack.isChecked())
        {
            intent.putExtra("titleColor", 0);
            editor.putInt("titleColor", 0);
        }
        else if (rbTitleRed.isChecked())
        {
            intent.putExtra("titleColor", 1);
            editor.putInt("titleColor", 1);
        }
        else if (rbTitleGreen.isChecked())
        {
            intent.putExtra("titleColor", 2);
            editor.putInt("titleColor", 2);
        }

        if(rbPubDateBlack.isChecked())
        {
            intent.putExtra("pubDateColor", 0);
            editor.putInt("pubDateColor", 0);
        }
        else if (rbPubdateRed.isChecked())
        {
            intent.putExtra("pubDateColor", 1);
            editor.putInt("pubDateColor", 1);
        }
        else if (rbPubDateGreen.isChecked())
        {
            intent.putExtra("pubDateColor", 2);
            editor.putInt("pubDateColor", 2);
        }

        editor.commit();

        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
