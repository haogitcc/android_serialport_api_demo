package com.fuwit.sensordemo;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fuwit.sensordemo.reader.ConnectionListener;
import com.fuwit.sensordemo.reader.ReaderSettingListener;
import com.thingmagic.Reader;

public class ReaderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ReaderActivity";
    public static boolean isConnected = false;
    public static Reader reader = null;
    public Handler handler = null;
    public String model = "none";
    public String uri = "None";
    private int windowWidth;
    public int rowNumberWidth = 0;
    public int epcDataWidth = 0;
    public int epcCountWidth = 0;
    private ReaderActivity readerActivity;

    TextView readerStatusTextview;
    Button connectButton;
    Button readButton;
    Button clearButton;
    Spinner baudRateSpinner;
    LinearLayout settingsLayout;
    Spinner regionSpinner;

    CheckBox antennaCheckbox;
    CheckBox powerCheckbox;
    CheckBox gen2Checkbox;
    CheckBox readCheckbox;

    RadioButton readOnceRadioButton;
    RadioButton readContinouslyRadioButton;
    private static Display display = null;
    private static TextView rowNumberLabelView = null;
    private static TextView epcDataLabelView = null;
    private static TextView epcCountLabelView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        findAllViewById();

        windowWidth = display.getWidth();
        LoggerUtil.info(TAG,"windowWidth=" + windowWidth);
        readerActivity = this;

        connectButton.setOnClickListener(new ConnectionListener(this));
        antennaCheckbox.setOnClickListener(this);
        powerCheckbox.setOnClickListener(this);
        gen2Checkbox.setOnClickListener(this);
        readCheckbox.setOnClickListener(this);
        readOnceRadioButton.setOnClickListener(this);
        readContinouslyRadioButton.setOnClickListener(this);
        readButton.setOnClickListener(new ServiceListener(this));
        clearButton.setOnClickListener(new ServiceListener(this).clearListener);




        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 2) {
                    LoggerUtil.info(TAG, "Reader is Connected");
                    settingsLayout.setVisibility(View.VISIBLE);
                    readerStatusTextview.setText(uri + ":" + model + " isConnected="+isConnected);
                    new ReaderSettingListener(readerActivity);
                }else if(msg.what == 3) {
                    LoggerUtil.info(TAG, "Reader is Disconnected");
                    settingsLayout.setVisibility(View.INVISIBLE);
                    readerStatusTextview.setText("Reader isConnected="+isConnected);
                }
            }
        };

        setPortraitTableLayout();
        System.gc();
    }

    private void setPortraitTableLayout() {
        rowNumberLabelView.setWidth((int) (windowWidth * (0.1)));
        epcDataLabelView.setWidth((int) (windowWidth * (0.73)));
        epcCountLabelView.setWidth((int) (windowWidth * (0.17)));
        rowNumberWidth = (int) (windowWidth * (0.09));
        epcDataWidth = (int) (windowWidth * (0.75));
        epcCountWidth = (int) (windowWidth * (0.16));
    }

    private void findAllViewById() {
        readerStatusTextview = findViewById(R.id.reader_status_textview);
        connectButton = findViewById(R.id.connect_button);
        readButton = findViewById(R.id.read_button);
        clearButton = findViewById(R.id.clear_button);
        baudRateSpinner = findViewById(R.id.baudrate_spinner);
        settingsLayout = findViewById(R.id.reader_settings);
        regionSpinner = findViewById(R.id.region_spinner);


        antennaCheckbox = findViewById(R.id.antenna_checkbox);
        powerCheckbox = findViewById(R.id.power_checkbox);
        gen2Checkbox = findViewById(R.id.gen2_checkbox);
        readCheckbox = findViewById(R.id.read_checkbox);

        readOnceRadioButton = findViewById(R.id.read_once_radiobutton);
        readContinouslyRadioButton = findViewById(R.id.read_continously_radiobutton);

        rowNumberLabelView = (TextView) findViewById(R.id.SNOLabel);
        epcDataLabelView = (TextView) findViewById(R.id.EPCLabel);
        epcCountLabelView = (TextView) findViewById(R.id.COUNTLabel);

        display = ((android.view.WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    @Override
    public void onClick(View view) {
        //LoggerUtil.info(TAG, "ID=" + view.getId());
        switch (view.getId())
        {
            case R.id.read_once_radiobutton:
            {
                LoggerUtil.info(TAG, "readonce@isChecked=" + readOnceRadioButton.isChecked());
                if(readOnceRadioButton.isChecked())
                {
                    findViewById(R.id.read_once_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.read_continously_layout).setVisibility(View.GONE);
                    readButton.setText("Read");
                }
                break;
            }
            case R.id.read_continously_radiobutton:
            {
                LoggerUtil.info(TAG, "readcontinous@isChecked=" + readContinouslyRadioButton.isChecked());
                if(readContinouslyRadioButton.isChecked())
                {
                    findViewById(R.id.read_once_layout).setVisibility(View.GONE);
                    findViewById(R.id.read_continously_layout).setVisibility(View.VISIBLE);
                    readButton.setText("Start Reading");
                }
                break;
            }
            case R.id.antenna_checkbox:
            {
                if(antennaCheckbox.isChecked())
                {
                    findViewById(R.id.antenna_layout).setVisibility(View.VISIBLE);
                }
                else
                {
                    findViewById(R.id.antenna_layout).setVisibility(View.GONE);
                }
                break;
            }
            case R.id.power_checkbox:
            {
                if(powerCheckbox.isChecked())
                {
                    findViewById(R.id.power_layout).setVisibility(View.VISIBLE);
                }
                else
                {
                    findViewById(R.id.power_layout).setVisibility(View.GONE);
                }
                break;
            }
            case R.id.gen2_checkbox:
            {
                if(gen2Checkbox.isChecked())
                {
                    findViewById(R.id.gen2_layout).setVisibility(View.VISIBLE);
                }
                else
                {
                    findViewById(R.id.gen2_layout).setVisibility(View.GONE);
                }
                break;
            }
            case R.id.read_checkbox:
            {
                if(readCheckbox.isChecked())
                {
                    findViewById(R.id.read_layout).setVisibility(View.VISIBLE);
                }
                else
                {
                    findViewById(R.id.read_layout).setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortraitTableLayout();
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setPortraitTableLayout();
        }
    }
}
