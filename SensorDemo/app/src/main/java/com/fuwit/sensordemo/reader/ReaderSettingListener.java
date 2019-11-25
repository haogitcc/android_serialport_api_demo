package com.fuwit.sensordemo.reader;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.fuwit.sensordemo.LoggerUtil;
import com.fuwit.sensordemo.R;
import com.fuwit.sensordemo.ReaderActivity;
import com.thingmagic.FeatureNotSupportedException;
import com.thingmagic.Gen2;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.TMConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.layout.simple_spinner_item;

public class ReaderSettingListener extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ReaderSettingListener";
    private final ReaderActivity mReaderActivity;
    private final ProgressDialog pDialog;
    private String exception = "";

    TextView readerStatusTextview;
    private TextView validationField;
    Spinner baudRateSpinner;
    LinearLayout settingsLayout;
    Spinner regionSpinner;
    CheckBox ant1Checkbox;
    CheckBox ant2Checkbox;
    CheckBox ant3Checkbox;
    CheckBox ant4Checkbox;
    CheckBox ant5Checkbox;
    CheckBox ant6Checkbox;
    CheckBox ant7Checkbox;
    CheckBox ant8Checkbox;
    CheckBox ant9Checkbox;
    CheckBox ant10Checkbox;
    CheckBox ant11Checkbox;
    CheckBox ant12Checkbox;
    CheckBox ant13Checkbox;
    CheckBox ant14Checkbox;
    CheckBox ant15Checkbox;
    CheckBox ant16Checkbox;
    CheckBox ant17Checkbox;
    CheckBox ant18Checkbox;
    CheckBox ant19Checkbox;
    CheckBox ant20Checkbox;
    CheckBox ant21Checkbox;
    CheckBox ant22Checkbox;
    CheckBox ant23Checkbox;
    CheckBox ant24Checkbox;
    CheckBox ant25Checkbox;
    CheckBox ant26Checkbox;
    CheckBox ant27Checkbox;
    CheckBox ant28Checkbox;
    CheckBox ant29Checkbox;
    CheckBox ant30Checkbox;
    CheckBox ant31Checkbox;
    CheckBox ant32Checkbox;

    private CheckBox antDetectCheckBox;

//    Spinner readpowerSpinner;
//    Spinner writepowerSpinner;
    SeekBar readpowerSeekbar;
    SeekBar writepowerSeekbar;
    EditText curReadpowerEditText;
    EditText curWritepowerEditText;
    TextView readpowerMin;
    TextView writepowerMin;
    TextView readpowerMax;
    TextView writepowerMax;
    Spinner blfSpinner;
    Spinner tariSpinner;
    Spinner tagEncodingSpinner;
    Spinner targetSpinner;
    Spinner sessionSpinner;
    Spinner qTypeSpinner;
    Spinner qSpinner;

    int minPower = 0;
    int maxPower = 0;
    int curReadPower = 0;
    int curWritePower = 0;
    Reader.Region curRegion;
    private Reader.Region[] supportedRegions;
    Gen2.LinkFrequency curBlf = null;
    Gen2.Tari curTari = null;
    Gen2.TagEncoding curTagEncoding = null;
    Gen2.Target curTarget = null;
    Gen2.Session curSession = null;
    Gen2.Q curQ = null;

//    List<Integer> power_list;

    List<Reader.Region> regionList;

    List<Gen2.LinkFrequency> blfList;
    List<Gen2.Tari> tariList;
    List<Gen2.TagEncoding> tagEncodingList;
    List<Gen2.Target> targetList;
    List<Gen2.Session> sessionList;
    List<String> qTypeList;
    private List<Gen2.Q> qList;

    List<Integer> existingAntennas = null;
    List<Integer> detectedAntennas = null;
    List<Integer> validAntennas = null;
    boolean checkPort = false;
    private boolean isDetectable = false;
    private boolean isInited = false;


    public ReaderSettingListener(ReaderActivity readerActivity) {
        LoggerUtil.info(TAG, "ReaderSettingListener");
        mReaderActivity = readerActivity;
        LoggerUtil.info(TAG, "@isConnected="+ mReaderActivity.isConnected);
        pDialog = new ProgressDialog(readerActivity);
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        findAllViewById();

        if(mReaderActivity.model.startsWith("M5e"))
        {
            tariSpinner.setEnabled(false);
        }

        String operation = "init";
        new SettingsThread(mReaderActivity.reader, operation).execute();

        antDetectCheckBox.setOnClickListener(this);
        LoggerUtil.info(TAG, "Spinner setOnItemSelectedListener");
        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                LoggerUtil.info(TAG, "position=" + position + ", l=" + l + ", region=" + regionSpinner.getItemAtPosition(position));
                new SettingsThread(mReaderActivity.reader, "setregion",regionSpinner.getItemAtPosition(position)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//        readpowerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                new SettingsThread(mReaderActivity.reader, "setreadpower", readpowerSpinner.getItemAtPosition(position)).execute();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        writepowerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                new SettingsThread(mReaderActivity.reader, "setwritepower", writepowerSpinner.getItemAtPosition(position)).execute();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        blfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                new SettingsThread(mReaderActivity.reader, "setblf",blfSpinner.getItemAtPosition(position)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        tariSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                new SettingsThread(mReaderActivity.reader, "settari", tariSpinner.getItemAtPosition(position)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        tagEncodingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                new SettingsThread(mReaderActivity.reader, "settagencoding",tagEncodingSpinner.getItemAtPosition(position)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        targetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                new SettingsThread(mReaderActivity.reader, "settarget",targetSpinner.getItemAtPosition(position)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sessionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                new SettingsThread(mReaderActivity.reader, "setsession",sessionSpinner.getItemAtPosition(position)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        qSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                new SettingsThread(mReaderActivity.reader, "setq",qSpinner.getItemAtPosition(position)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        qTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                new SettingsThread(mReaderActivity.reader, "setqtype",qTypeSpinner.getItemAtPosition(position)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        readpowerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int power, boolean b) {
                if(!isInited)
                   return;
                if(power<minPower)
                    power=minPower;
                if(power>maxPower)
                    power=maxPower;
                if(power%50 != 0)
                    power = power/50 * 50;
                curReadpowerEditText.setText(""+power);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(!isInited)
                    return;
                new SettingsThread(mReaderActivity.reader, "setreadpower", Integer.parseInt(curReadpowerEditText.getText().toString())).execute();
            }
        });

        writepowerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int power, boolean b) {
                if(!isInited)
                    return;
                if(power<minPower)
                    power=minPower;
                if(power>maxPower)
                    power=maxPower;
                if(power%50 != 0)
                    power = power/50 * 50;
                curWritepowerEditText.setText(""+power);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(!isInited)
                    return;
                new SettingsThread(mReaderActivity.reader, "setwritepower", Integer.parseInt(curWritepowerEditText.getText().toString())).execute();
            }
        });

    }

    private void findAllViewById() {
        readerStatusTextview = mReaderActivity.findViewById(R.id.reader_status_textview);
        validationField = mReaderActivity.findViewById(R.id.ValidationField);
        baudRateSpinner = mReaderActivity.findViewById(R.id.baudrate_spinner);
        settingsLayout = mReaderActivity.findViewById(R.id.reader_settings);
        regionSpinner = mReaderActivity.findViewById(R.id.region_spinner);
        ant1Checkbox = mReaderActivity.findViewById(R.id.ant1_checkbox);
        ant2Checkbox = mReaderActivity.findViewById(R.id.ant2_checkbox);
        ant3Checkbox = mReaderActivity.findViewById(R.id.ant3_checkbox);
        ant4Checkbox = mReaderActivity.findViewById(R.id.ant4_checkbox);
        ant5Checkbox = mReaderActivity.findViewById(R.id.ant5_checkbox);
        ant6Checkbox = mReaderActivity.findViewById(R.id.ant6_checkbox);
        ant7Checkbox = mReaderActivity.findViewById(R.id.ant7_checkbox);
        ant8Checkbox = mReaderActivity.findViewById(R.id.ant8_checkbox);
        ant9Checkbox = mReaderActivity.findViewById(R.id.ant9_checkbox);
        ant10Checkbox = mReaderActivity.findViewById(R.id.ant10_checkbox);
        ant11Checkbox = mReaderActivity.findViewById(R.id.ant11_checkbox);
        ant12Checkbox = mReaderActivity.findViewById(R.id.ant12_checkbox);
        ant13Checkbox = mReaderActivity.findViewById(R.id.ant13_checkbox);
        ant14Checkbox = mReaderActivity.findViewById(R.id.ant14_checkbox);
        ant15Checkbox = mReaderActivity.findViewById(R.id.ant15_checkbox);
        ant16Checkbox = mReaderActivity.findViewById(R.id.ant16_checkbox);
        ant17Checkbox = mReaderActivity.findViewById(R.id.ant17_checkbox);
        ant18Checkbox = mReaderActivity.findViewById(R.id.ant18_checkbox);
        ant19Checkbox = mReaderActivity.findViewById(R.id.ant19_checkbox);
        ant20Checkbox = mReaderActivity.findViewById(R.id.ant20_checkbox);
        ant21Checkbox = mReaderActivity.findViewById(R.id.ant21_checkbox);
        ant22Checkbox = mReaderActivity.findViewById(R.id.ant22_checkbox);
        ant23Checkbox = mReaderActivity.findViewById(R.id.ant23_checkbox);
        ant24Checkbox = mReaderActivity.findViewById(R.id.ant24_checkbox);
        ant25Checkbox = mReaderActivity.findViewById(R.id.ant25_checkbox);
        ant26Checkbox = mReaderActivity.findViewById(R.id.ant26_checkbox);
        ant27Checkbox = mReaderActivity.findViewById(R.id.ant27_checkbox);
        ant28Checkbox = mReaderActivity.findViewById(R.id.ant28_checkbox);
        ant29Checkbox = mReaderActivity.findViewById(R.id.ant29_checkbox);
        ant30Checkbox = mReaderActivity.findViewById(R.id.ant30_checkbox);
        ant31Checkbox = mReaderActivity.findViewById(R.id.ant31_checkbox);
        ant32Checkbox = mReaderActivity.findViewById(R.id.ant32_checkbox);
        antDetectCheckBox = mReaderActivity.findViewById(R.id.ant_dectect_checkbox);
//        readpowerSpinner = mReaderActivity.findViewById(R.id.read_power_spinner);
//        writepowerSpinner = mReaderActivity.findViewById(R.id.write_power_spinner);
        readpowerSeekbar = mReaderActivity.findViewById(R.id.read_power_seekbar);
        writepowerSeekbar = mReaderActivity.findViewById(R.id.write_power_seekbar);
        curReadpowerEditText = mReaderActivity.findViewById(R.id.read_power_edittext);
        curWritepowerEditText = mReaderActivity.findViewById(R.id.write_power_edittext);
        readpowerMin = mReaderActivity.findViewById(R.id.read_min_power);
        readpowerMax = mReaderActivity.findViewById(R.id.read_max_power);
        writepowerMin = mReaderActivity.findViewById(R.id.write_min_power);
        writepowerMax = mReaderActivity.findViewById(R.id.write_max_power);
        blfSpinner = mReaderActivity.findViewById(R.id.blf_spinner);
        tariSpinner = mReaderActivity.findViewById(R.id.tari_spinner);
        tagEncodingSpinner = mReaderActivity.findViewById(R.id.tagencoding_spinner);
        targetSpinner = mReaderActivity.findViewById(R.id.target_spinner);
        sessionSpinner = mReaderActivity.findViewById(R.id.session_spinner);
        qTypeSpinner = mReaderActivity.findViewById(R.id.q_type_spinner);;
        qSpinner = mReaderActivity.findViewById(R.id.q_spinner);
    }

    @Override
    public void onClick(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
        case R.id.ant_dectect_checkbox:
            new SettingsThread(mReaderActivity.reader, "setantdetect", checked).execute();
            break;
        }
    }

    private class SettingsThread extends AsyncTask<Void, Integer, String> {
        Reader reader;
        String operation;
        Object value;

        private String model;

        ArrayAdapter<Reader.Region> regionAdapter;
//        ArrayAdapter<Integer> powerAdapter;
        private ArrayAdapter<Gen2.LinkFrequency> blfAdapter;
        private ArrayAdapter<Gen2.Tari> tariAdapter;
        private ArrayAdapter<Gen2.TagEncoding> tagEncodingAdapter;
        private ArrayAdapter<Gen2.Target> targetAdapter;
        private ArrayAdapter<Gen2.Session> sessionAdapter;
        private ArrayAdapter<Gen2.Q> qAdapter;
        private ArrayAdapter<String> qTypeAdapter;

        public SettingsThread(Reader reader, String operation) {
            this.reader = reader;
            this.operation = operation;
//            LoggerUtil.info(TAG, "operation="+this.operation);
        }

        public SettingsThread(Reader reader, String operation, Object value) {
            this.reader = reader;
            this.operation = operation;
            this.value = value;
//            LoggerUtil.info(TAG, "operation="+this.operation+", value="+value);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            validationField.setText("");
            validationField.setVisibility(View.VISIBLE);
            LoggerUtil.info(TAG,"onPreExecute reader settings " + operation);
            pDialog.setMessage("Doing " + operation + " Please wait...");
        }

        @Override
        protected String doInBackground(Void... voids) {
            LoggerUtil.info(TAG, "doInBackground--> " + operation);
            if(operation.equalsIgnoreCase(FUConstants.MODULE_INIT))
            {
                try {
                    model = (String) reader.paramGet(TMConstants.TMR_PARAM_VERSION_MODEL);
                    curRegion = (Reader.Region) reader.paramGet(TMConstants.TMR_PARAM_REGION_ID);
                    supportedRegions = (Reader.Region[]) reader.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
                    minPower = (int) reader.paramGet(TMConstants.TMR_PARAM_RADIO_POWERMIN);
                    maxPower = (int) reader.paramGet(TMConstants.TMR_PARAM_RADIO_POWERMAX);
                    curReadPower = (int) reader.paramGet(TMConstants.TMR_PARAM_RADIO_READPOWER);
                    LoggerUtil.info(TAG, "1@curReadPower=" + curReadPower);
                    curWritePower = (int) reader.paramGet(TMConstants.TMR_PARAM_RADIO_WRITEPOWER);
                    LoggerUtil.info(TAG, "1@curWritePower=" + curWritePower);
                    curBlf = (Gen2.LinkFrequency) reader.paramGet(TMConstants.TMR_PARAM_GEN2_BLF);
                    if(!mReaderActivity.model.startsWith("M5e"))
                        curTari = (Gen2.Tari) reader.paramGet(TMConstants.TMR_PARAM_GEN2_TARI);
                    curTagEncoding = (Gen2.TagEncoding) reader.paramGet(TMConstants.TMR_PARAM_GEN2_TAGENCODING);
                    curTarget = (Gen2.Target) reader.paramGet(TMConstants.TMR_PARAM_GEN2_TARGET);
                    curSession = (Gen2.Session) reader.paramGet(TMConstants.TMR_PARAM_GEN2_SESSION);
                    curQ = (Gen2.Q) reader.paramGet(TMConstants.TMR_PARAM_GEN2_Q);

                } catch (ReaderException e) {
                    e.printStackTrace();
                    if(e.getMessage().startsWith("No parameter named"))
                    {
                        //donothing
                    }
                    exception += "\ninit:" + e.getMessage();
                }


                if(supportedRegions.length > 0) {
                    regionList = new ArrayList<Reader.Region>();
                    for (Reader.Region region: supportedRegions) {
                        if(region!=null) //fix bug shantui
                        {
//                            LoggerUtil.info(TAG, "add --> " + region);
                            regionList.add(region);
                        }
                    }
                }

                LoggerUtil.info(TAG, "power filed[" + minPower + "-" + maxPower+"]");

//                power_list = new ArrayList<Integer>();
//                for(int i=minPower; i<=maxPower; i++)
//                {
//                    power_list.add(i);
//                }

                loadGen2Settings();

                try
                {


                    boolean checkPort = (boolean)reader.paramGet(TMConstants.TMR_PARAM_ANTENNA_CHECKPORT);
                    String swVersion = (String) reader.paramGet(TMConstants.TMR_PARAM_VERSION_SOFTWARE);
                    if ((model.equalsIgnoreCase("M6e Micro") || model.equalsIgnoreCase("M6e Nano") ||
                            (model.equalsIgnoreCase("Sargas") && (swVersion.startsWith("5.1")))) && (false == checkPort))
                    {
                        LoggerUtil.info(TAG, "Module doesn't has antenna detection support, please provide antenna list");
                        isDetectable = false;
                    }
                    else
                    {
                        isDetectable = true;
                    }

                    LoggerUtil.info(TAG, "isDetectable=" + isDetectable);
                    if(isDetectable)
                    {
                        configureLogicalAntennaBoxes(reader);
                    }
                } catch (ReaderException e) {
                    e.printStackTrace();
                    LoggerUtil.error(TAG, e.getMessage());
                }
                isInited = true;
            }
            else if(operation.equalsIgnoreCase(FUConstants.SET_REGION))
            {
//                myParamSet(TMConstants.TMR_PARAM_REGION_ID, value);
            }
            else if(operation.equalsIgnoreCase(FUConstants.SER_READPOWER))
            {
                myParamSet(TMConstants.TMR_PARAM_RADIO_READPOWER, (int)value);
            }
            else if(operation.equalsIgnoreCase(FUConstants.SET_WRITEPOWER))
            {
                myParamSet(TMConstants.TMR_PARAM_RADIO_WRITEPOWER, (int)value);
            }
            else if(operation.equalsIgnoreCase(FUConstants.SET_GEN2_BLF))
            {
                myParamSet(TMConstants.TMR_PARAM_GEN2_BLF, value);
            }
            else if(operation.equalsIgnoreCase(FUConstants.SET_GEN2_TARI))
            {
                myParamSet(TMConstants.TMR_PARAM_GEN2_TARI, value);
            }
            else if(operation.equalsIgnoreCase(FUConstants.SET_GEN2_TAGENCODING))
            {
                myParamSet(TMConstants.TMR_PARAM_GEN2_TAGENCODING, value);
            }
            else if(operation.equalsIgnoreCase(FUConstants.SET_GEN2_TARGET))
            {
                myParamSet(TMConstants.TMR_PARAM_GEN2_TARGET, value);
            }
            else if(operation.equalsIgnoreCase(FUConstants.SET_GEN2_SESSION))
            {
                myParamSet(TMConstants.TMR_PARAM_GEN2_SESSION, value);
            }
            else if(operation.equalsIgnoreCase(FUConstants.SET_GEN2_Q))
            {
                myParamSet(TMConstants.TMR_PARAM_GEN2_Q, value);
            }
            else if(operation.equalsIgnoreCase(FUConstants.SET_GEN2_QTYPE))
            {

            }
            else if(operation.equalsIgnoreCase(FUConstants.SET_ANTENNA_DETECT))
            {
                myParamSet("/reader/antenna/checkPort", value);
                try {
                    configureLogicalAntennaBoxes(reader);
                } catch (ReaderException e) {
                    e.printStackTrace();
                    LoggerUtil.error(TAG, e.getMessage());
                }
            }
            return null;
        }

        private void myParamSet(String param, Object value) {
            LoggerUtil.info(TAG, param + "-->" + value);
            try {
                reader.paramSet(param, value);
            } catch (ReaderException e) {
                e.printStackTrace();
                exception += "\nparamSet "+param+":"+value +"-->\n" + e.getMessage();
            }
            LoggerUtil.info(TAG, param + "-->" + value + " success");
        }

        private void addStaticQ(List<Gen2.Q> qList) {
            qList.clear();
            for(int initQ=0; initQ<16; initQ++)
            {
                qList.add(new Gen2.StaticQ(initQ));
            }
        }

        private void removeStaticQ(List<Gen2.Q> qList) {
            addDynamicQ(qList);
        }

        private void addDynamicQ(List<Gen2.Q> qList) {
            LoggerUtil.info(TAG, "addDynamicQ");
            qList.clear();
            addToList(qList, new Gen2.DynamicQ());
        }

        private void removeDynamicQ(List<Gen2.Q> qList) {
            addStaticQ(qList);
        }

        private void loadGen2Settings()
        {
            blfList = new ArrayList<Gen2.LinkFrequency>();

            tariList = new ArrayList<Gen2.Tari>();

            tagEncodingList = new ArrayList<Gen2.TagEncoding>();

            targetList = new ArrayList<Gen2.Target>();
            targetList.add(Gen2.Target.A);
            targetList.add(Gen2.Target.B);
            targetList.add(Gen2.Target.AB);
            targetList.add(Gen2.Target.BA);

            sessionList = new ArrayList<Gen2.Session>();
            sessionList.add(Gen2.Session.S0);
            sessionList.add(Gen2.Session.S1);
            sessionList.add(Gen2.Session.S2);
            sessionList.add(Gen2.Session.S3);

            qTypeList = new ArrayList<String>();
            qTypeList.add("DynamicQ");
            qTypeList.add("StaticQ");

            LoggerUtil.info(TAG, "add qList ....");
            qList = new ArrayList<Gen2.Q>();
            addToList(qList, new Gen2.DynamicQ());
            for(int initQ=0; initQ<16; initQ++)
            {
                qList.add(new Gen2.StaticQ(initQ));
            }
            LoggerUtil.info(TAG, "add qList done ....");

            //Get all the current settings and set the radio buttons accordingly
            // Astra doesn't support blf parameter
            if (!(model.equalsIgnoreCase("Astra")))
            {
                addToList(blfList, Gen2.LinkFrequency.LINK250KHZ);
            }
            else
            {
                removeFromList(blfList, Gen2.LinkFrequency.LINK250KHZ);
            }
            removeFromList(blfList, Gen2.LinkFrequency.LINK640KHZ);
            try
            {
                switch (model)
                {
                    case "Mercury6":
                    case "Astra-EX":
                    case "Sargas":
                    case "Izar":
                    case "M6e":
                    case "M6e Micro":
                    case "M6e Micro USB":
                    case "M6e Micro USBPro":
                    case "M6e PRC":
                    case "M6e JIC":
                        if(model.equalsIgnoreCase("Mercury6"))
                            addToList(blfList, Gen2.LinkFrequency.LINK320KHZ);
                        addToList(blfList, Gen2.LinkFrequency.LINK250KHZ);
                        addToList(blfList, Gen2.LinkFrequency.LINK640KHZ);
                        break;
                    case "M6e Nano":
                        addToList(blfList, Gen2.LinkFrequency.LINK250KHZ);
                        removeFromList(blfList, Gen2.LinkFrequency.LINK640KHZ);
                        break;
                }
            }
            catch (Exception ex)
            {
                LoggerUtil.error("ArgumentException : ", ex.getMessage());
            }


            try
            {
                // Astra doesn't support tari parameter
                if (!(model.equalsIgnoreCase("Astra")))
                {
                    switch (model)
                    {
                        case "Mercury6":
                        case "Astra-EX":
                        case "Sargas":
                        case "Izar":
                        case "M6e":
                        case "M6e Micro":
                        case "M6e Micro USB":
                        case "M6e Micro USBPro":
                        case "M6e PRC":
                        case "M6e JIC":
                            addToList(tariList, Gen2.Tari.TARI_6_25US);
                            addToList(tariList, Gen2.Tari.TARI_12_5US);
                            addToList(tariList, Gen2.Tari.TARI_25US);
                            break;
                        case "M6e Nano":
                            removeFromList(tariList, Gen2.Tari.TARI_6_25US);
                            removeFromList(tariList, Gen2.Tari.TARI_12_5US);
                            addToList(tariList, Gen2.Tari.TARI_25US);
                            break;
                    }
                }
            }
            catch (Exception ex)
            {
                LoggerUtil.error("ArgumentException : ", ex.getMessage());
            }

            try
            {
                // Astra doesn't support tagencoding parameter
                if (!(model.equalsIgnoreCase("Astra")))
                {
                    if ((model.equalsIgnoreCase("Mercury6")) || (model.equalsIgnoreCase("Astra-EX")) || (model.equalsIgnoreCase("Sargas")) || (model.equalsIgnoreCase("Izar"))
                            || (model.equalsIgnoreCase("M6e")) || (model.equalsIgnoreCase("M6e Micro")) || (model.equalsIgnoreCase("M6e Micro USB"))
                            || (model.equalsIgnoreCase("M6e Micro USBPro")) || (model.equalsIgnoreCase("M6e PRC")) || (model.equalsIgnoreCase("M6e JIC")))
                    {
                        addToList(tagEncodingList, Gen2.TagEncoding.FM0);
                    }
                    else
                    {
                        removeFromList(tagEncodingList, Gen2.TagEncoding.FM0);
                    }
                    addToList(tagEncodingList, Gen2.TagEncoding.M2);
                    addToList(tagEncodingList, Gen2.TagEncoding.M4);
                    addToList(tagEncodingList, Gen2.TagEncoding.M8);
                }
                else
                {
                    removeFromList(tagEncodingList, Gen2.TagEncoding.FM0);
                    removeFromList(tagEncodingList, Gen2.TagEncoding.M2);
                    removeFromList(tagEncodingList, Gen2.TagEncoding.M4);
                    removeFromList(tagEncodingList, Gen2.TagEncoding.M8);
                }
            }
            catch (Exception e)
            {
                LoggerUtil.error(TAG, e.getMessage());
            }

            try
            {
                // Astra doesn't support tagencoding parameter
                if (!(model.equalsIgnoreCase("Astra")))
                {
                    addToList(qTypeList, "DynamicQ");
                    addToList(qTypeList, "StaticQ");
//                    addToQValue();
                }
                else
                {
                    addToList(qTypeList, "DynamicQ");
                    removeFromList(qTypeList, "StaticQ");
//                    removeToQValue();
                }
            }
            catch (Exception e)
            {
                LoggerUtil.error(TAG, e.getMessage());
            }

        }

        private void removeFromList(List<?> list, Object t) {
            if(list.contains(t))
            {
                LoggerUtil.info(TAG, "removeFromList->" + t);
                list.remove(t);
            }

        }

        private void addToList(List<Gen2.LinkFrequency> list, Gen2.LinkFrequency t){
            if(!list.contains(t)) {
                LoggerUtil.info(TAG, "addToList->" + t);
                list.add(t);
            }
        }

        private void addToList(List<Gen2.Tari> list, Gen2.Tari t){
            if(!list.contains(t)){
                LoggerUtil.info(TAG, "addToList->" + t);
                list.add(t);
            }

        }

        private void addToList(List<Gen2.TagEncoding> list, Gen2.TagEncoding t){
            if(!list.contains(t)) {
                LoggerUtil.info(TAG, "addToList->" + t);
                list.add(t);
            }
        }

        private void addToList(List<Gen2.Target> list, Gen2.Target t){

            if(!list.contains(t)) {
                LoggerUtil.info(TAG, "addToList->" + t);
                list.add(t);
            }
        }

        private void addToList(List<Gen2.Session> list, Gen2.Session t){
            if(!list.contains(t)) {
                LoggerUtil.info(TAG, "addToList->" + t);
                list.add(t);
            }
        }

        private void addToList(List<Gen2.Q> list, Gen2.Q t){
            if(!list.contains(t)) {
                LoggerUtil.info(TAG, "addToList->" + t);
                list.add(t);
            }
        }

        private void addToList(List<String> list, String t){
            if(!list.contains(t)) {
                LoggerUtil.info(TAG, "addToList->" + t);
                list.add(t);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            validationField.setText(exception);
            validationField.setVisibility(View.VISIBLE);
            exception = "";
            LoggerUtil.info(TAG,"onPostExecute reader settings -> " + operation);
            if(operation.equalsIgnoreCase(FUConstants.MODULE_INIT)) {
                regionAdapter = new ArrayAdapter<>(mReaderActivity, simple_spinner_item, regionList);
                regionSpinner.setAdapter(regionAdapter);

//                powerAdapter = new ArrayAdapter<Integer>(mReaderActivity, simple_spinner_item, power_list);
//                readpowerSpinner.setAdapter(powerAdapter);
//                writepowerSpinner.setAdapter(powerAdapter);




                blfAdapter = new ArrayAdapter<Gen2.LinkFrequency>(mReaderActivity, simple_spinner_item, blfList);
                blfSpinner.setAdapter(blfAdapter);

                tariAdapter = new ArrayAdapter<>(mReaderActivity, simple_spinner_item, tariList);
                tariSpinner.setAdapter(tariAdapter);

                tagEncodingAdapter = new ArrayAdapter<>(mReaderActivity, simple_spinner_item, tagEncodingList);
                tagEncodingSpinner.setAdapter(tagEncodingAdapter);

                targetAdapter = new ArrayAdapter<>(mReaderActivity, simple_spinner_item, targetList);
                targetSpinner.setAdapter(targetAdapter);

                sessionAdapter = new ArrayAdapter<>(mReaderActivity, simple_spinner_item, sessionList);
                sessionSpinner.setAdapter(sessionAdapter);

                qTypeAdapter = new ArrayAdapter<>(mReaderActivity, simple_spinner_item, qTypeList);
                qTypeSpinner.setAdapter(qTypeAdapter);

                qAdapter = new ArrayAdapter<>(mReaderActivity, simple_spinner_item, qList);
                qSpinner.setAdapter(qAdapter);

                LoggerUtil.info(TAG, operation + " set cur gen2");
//                readpowerSpinner.setSelection(powerAdapter.getPosition(curReadPower/100));
//                writepowerSpinner.setSelection(powerAdapter.getPosition(curWritePower/100));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    readpowerSeekbar.setMin(minPower);
                    writepowerSeekbar.setMin(minPower);
                }
                readpowerSeekbar.setMax(maxPower);
                writepowerSeekbar.setMax(maxPower);


                readpowerMin.setText("" + minPower);
                writepowerMin.setText("" + minPower);
                readpowerMax.setText("" + maxPower);
                writepowerMax.setText("" + maxPower);

                LoggerUtil.info(TAG, "curReadpower=" + curReadPower);
                readpowerSeekbar.setProgress(curReadPower);
                writepowerSeekbar.setProgress(curWritePower);

                curReadpowerEditText.setText("" + curReadPower);
                curWritepowerEditText.setText("" + curWritePower);

                blfSpinner.setSelection(blfAdapter.getPosition(curBlf));
                tariSpinner.setSelection(tariAdapter.getPosition(curTari));
                tagEncodingSpinner.setSelection(tagEncodingAdapter.getPosition(curTagEncoding));
                targetSpinner.setSelection(targetAdapter.getPosition(curTarget));
                sessionSpinner.setSelection(sessionAdapter.getPosition(curSession));
                qSpinner.setSelection(qAdapter.getPosition(curQ));
                LoggerUtil.info(TAG, operation + " set cur gen2 done");

                if(isDetectable)
                {
                    configureLogicalAntennaBoxesUI();
                }

            }
            else if(operation.equalsIgnoreCase(FUConstants.SET_GEN2_QTYPE))
            {
                LoggerUtil.info(TAG, "------->is "+value);
                if(value.equals("DynamicQ"))
                {
                    LoggerUtil.info(TAG, "is DynamicQ");
                    if(qList!=null) {
                        addDynamicQ(qList);
                        qSpinner.setSelection(0);
                    } else
                        LoggerUtil.info(TAG, "DynamicQ qList is null @" + qList);
                }
                else
                {
                    LoggerUtil.info(TAG, "is StaticQ");
                    if(qList!=null) {
                        addStaticQ(qList);
                        qSpinner.setSelection(1);
                    } else
                        LoggerUtil.info(TAG, "StaticQ qList is null@" + qList);
                }
            }
            else  if(operation.equalsIgnoreCase(FUConstants.SET_ANTENNA_DETECT))
            {
                configureLogicalAntennaBoxesUI();
            }
        }
    }

    private void configureLogicalAntennaBoxesUI() {
        antDetectCheckBox.setChecked(checkPort);
        CheckBox[] antennaBoxes = { ant1Checkbox, ant2Checkbox, ant3Checkbox, ant4Checkbox, ant5Checkbox, ant6Checkbox, ant7Checkbox, ant8Checkbox,
                ant9Checkbox, ant10Checkbox, ant11Checkbox, ant12Checkbox, ant13Checkbox, ant14Checkbox, ant15Checkbox, ant16Checkbox,
                ant17Checkbox, ant18Checkbox, ant19Checkbox, ant20Checkbox, ant21Checkbox, ant22Checkbox, ant23Checkbox, ant24Checkbox,
                ant25Checkbox, ant26Checkbox, ant27Checkbox, ant28Checkbox, ant29Checkbox, ant30Checkbox, ant31Checkbox, ant32Checkbox};
        int antNum = 1;
        for (CheckBox cb : antennaBoxes)
        {
            if (existingAntennas.contains(antNum))
            {
                cb.setVisibility(View.VISIBLE);
            }
            else
            {
//                cb.setVisibility(View.INVISIBLE);
                cb.setVisibility(View.GONE);
            }
            if (validAntennas.contains(antNum))
            {
                cb.setEnabled(true);
            }
            else
            {
                cb.setEnabled(false);
            }
            if (detectedAntennas.contains(antNum))
            {
                cb.setChecked(true);
            }
            else
            {
                cb.setChecked(false);
            }
            antNum++;
        }
    }

    private void configureLogicalAntennaBoxes(Reader objReader) throws ReaderException {
        // Cast int[] return values to IList<int> instead of int[] to get Contains method
        if (null == objReader)
        {
            List<Integer> empty = new ArrayList<>();
            existingAntennas = detectedAntennas = validAntennas = empty;
        }
        else
        {

            switch (mReaderActivity.model)
            {
                case "Astra":
                    checkPort = true;
                    break;
                default:
                    checkPort = (boolean) objReader.paramGet("/reader/antenna/checkPort");
                    break;
            }
            int[] existing = (int[])objReader.paramGet("/reader/antenna/PortList");
            int[] detected = (int[]) objReader.paramGet("/reader/antenna/connectedPortList");
            existingAntennas = new ArrayList<>();
            detectedAntennas = new ArrayList<>();
            for(int i : existing)
            {
                existingAntennas.add(i);
            }
            for(int i : detected)
            {
                detectedAntennas.add(i);
            }
            validAntennas = checkPort ? detectedAntennas : existingAntennas;
        }



    }
}

