package com.fuwit.sensordemo;

import android.content.Context;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.thingmagic.ReadExceptionListener;
import com.thingmagic.ReadListener;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.SimpleReadPlan;
import com.thingmagic.TagProtocol;
import com.thingmagic.TagReadData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

class ServiceListener implements View.OnClickListener {
    private static final String TAG = "chris-->ServiceListener";
    //成员变量等等
    static ReaderActivity mReaderActivity;
    private static ReadThread readThread;
    private static long queryStartTime = 0;
    private static long queryStopTime = 0;
    private static int uniqueRecordCount = 0;
    private static int totalTagCount = 0;

    private static ArrayList<String> addedEPCRecords = new ArrayList<String>();;
    private static ConcurrentHashMap<String, TagRecord> epcToReadDataMap = new ConcurrentHashMap<String, TagRecord>();

    static int redColor = 0xffff0000;
    static int textColor = 0xff000000;
    //控件
    static TextView searchResultCount = null;
    static TextView totalTagCountView = null;
    private static LayoutInflater inflater;
    private static TableLayout table;

    static Button connectButton;
    static Button readButton;
    static Button clearButton;
    static EditText rfOnTimeout;
    static EditText rfOffTimeout;
    static EditText readOnceTimeout;
    static RadioButton syncReadRadioButton;
    static RadioButton asyncReadRadioButton;
    static CheckBox ant1_checkbox;
    static CheckBox ant2_checkbox;
    static CheckBox ant4_checkbox;
    static CheckBox ant3_checkbox;
    private static Timer timer = new Timer();


    public ServiceListener(ReaderActivity readerActivity) {
        this.mReaderActivity = readerActivity;
        findAllViewsById();
        if(!syncReadRadioButton.isChecked() && !asyncReadRadioButton.isChecked())
        {
            syncReadRadioButton.setChecked(true);
        }
    }

    private void findAllViewsById() {
        searchResultCount = mReaderActivity.findViewById(R.id.search_result_view);
        totalTagCountView = mReaderActivity.findViewById(R.id.totalTagCount_view);
        table = mReaderActivity.findViewById(R.id.tablelayout);
        inflater = (LayoutInflater) mReaderActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        connectButton = mReaderActivity.findViewById(R.id.connect_button);
        readButton = mReaderActivity.findViewById(R.id.read_button);
        clearButton = mReaderActivity.findViewById(R.id.clear_button);
        rfOffTimeout = mReaderActivity.findViewById(R.id.rf_off_timeout);
        rfOnTimeout = mReaderActivity.findViewById(R.id.rf_on_timeout);
        readOnceTimeout = mReaderActivity.findViewById(R.id.read_once_timeout);
        syncReadRadioButton = mReaderActivity.findViewById(R.id.read_once_radiobutton);
        asyncReadRadioButton = mReaderActivity.findViewById(R.id.read_continously_radiobutton);
        ant1_checkbox = mReaderActivity.findViewById(R.id.ant1_checkbox);
        ant2_checkbox = mReaderActivity.findViewById(R.id.ant2_checkbox);
        ant3_checkbox = mReaderActivity.findViewById(R.id.ant3_checkbox);
        ant4_checkbox = mReaderActivity.findViewById(R.id.ant4_checkbox);
    }

    @Override
    public void onClick(View view) {
        String operation = "";
        if (syncReadRadioButton.isChecked()) {
            operation = "syncRead";

            readButton.setText("Reading");
            readButton.setClickable(false);

        } else if (asyncReadRadioButton.isChecked()) {
            operation = "asyncRead";
        }

        if (readButton.getText().equals("Stop Reading")) {
            readThread.setReading(false);
            readButton.setText("Stopping...");
            readButton.setClickable(false);
        } else if (readButton.getText().equals("Start Reading") || readButton.getText().equals("Reading")) {
            if (readButton.getText().equals("Start Reading")) {
                readButton.setText("Stop Reading");
            }
            clearTagRecords();
            readThread = new ReadThread(mReaderActivity.reader, operation);
            readThread.execute();
        }
    }

    public View.OnClickListener clearListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            clearTagRecords();
        }
    };

    private static void clearTagRecords() {
        LoggerUtil.info(TAG, "clearTagRecords");
        addedEPCRecords.clear();
        epcToReadDataMap.clear();
        table.removeAllViews();
        searchResultCount.setText("");
        totalTagCountView.setText("");
        uniqueRecordCount = 0;
        totalTagCount = 0;
        queryStartTime = System.currentTimeMillis();
    }

    private static class ReadThread  extends
            AsyncTask<Void, Integer, ConcurrentHashMap<String, TagRecord>> {
        private static boolean reading = false;

        private static TextView nr = null;
        private static TextView epcValue = null;
        private static TextView dataView = null;
        private static TextView countView = null;
        private static TableRow fullRow = null;

        private String operation ="";
        private Reader mReader = null;

        private static boolean isEmbeddedRead = false;
        private static int uniqueRecordCount = 0;
        private int totalTagCount = 0;
        private int timeOut = 0;

        static ReadExceptionListener exceptionListener = new TagReadExceptionReceiver();
        static ReadListener readListener = new PrintListener();

        private static boolean exceptionOccur = false;
        private static String exception = "";


        public ReadThread(Reader reader, String operation) {
            this.operation = operation;
            mReader = reader;
            LoggerUtil.info(TAG, "operation=" + operation);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearTagRecords();
            syncReadRadioButton.setClickable(false);
            asyncReadRadioButton.setClickable(false);
            connectButton.setEnabled(false);
            connectButton.setClickable(false);
            exceptionOccur = false;

            addedEPCRecords = new ArrayList<String>();
            epcToReadDataMap = new ConcurrentHashMap<String, TagRecord>();
            if (operation.equalsIgnoreCase("syncRead")) {
                timeOut = Integer.parseInt(readOnceTimeout.getText().toString());
            } else {

            }
        }



        @Override
        protected ConcurrentHashMap<String, TagRecord> doInBackground(Void... voids) {
            try {
                //mSettingsService.loadReadPlan(mReader);
                if (operation.equalsIgnoreCase("syncRead")) {
                    setReadplan(mReaderActivity);
                    TagReadData[] tagReads = mReader.read(timeOut);
                    queryStopTime = System.currentTimeMillis();
                    for (TagReadData tr : tagReads) {
                        parseTag(tr, false);
                    }
                    publishProgress(0);
                } else {
                    setReading(true);
                    mReader.addReadExceptionListener(exceptionListener);
                    mReader.addReadListener(readListener);
                    setReadplan(mReaderActivity);

                    mReader.startReading();
                    queryStartTime = System.currentTimeMillis();
                    refreshReadRate();
                    while (isReading()) {
                        /* Waiting till stop reading button is pressed */
                        Thread.sleep(5);
                    }
                    queryStopTime = System.currentTimeMillis();
                    if(!exceptionOccur)
                    {
                        mReader.stopReading();
                        mReader.removeReadListener(readListener);
                        mReader.removeReadExceptionListener(exceptionListener);
                    }
                }
            } catch (Exception ex) {
                LoggerUtil.error(TAG, "Exception while reading :", ex);
                exceptionOccur = true;
            }

            return epcToReadDataMap;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            int progressToken = progress[0];
            if (progressToken == -1) {
                searchResultCount.setTextColor(redColor);
                searchResultCount.setText("ERROR :" + exception);
                totalTagCountView.setText("");
            } else {
                LoggerUtil.info(TAG, "onProgressUpdate + populateSearchResult");
                populateSearchResult(epcToReadDataMap);
                if (!exceptionOccur && totalTagCount > 0) {
                    searchResultCount.setTextColor(textColor);
                    searchResultCount.setText(Html.fromHtml("<b>Unique Tags :</b> " + epcToReadDataMap.keySet().size()));
                    totalTagCountView.setText(Html.fromHtml("<b>Total Tags  :</b> " + totalTagCount));
                }
            }
        }


        private static void populateSearchResult(
                ConcurrentHashMap<String, TagRecord> epcToReadDataMap) {
            LoggerUtil.info(TAG, "populateSearchResult...");
            try {
                Set<String> keySet = epcToReadDataMap.keySet();
                for (String epcString : keySet) {
                    TagRecord tagRecordData = epcToReadDataMap.get(epcString);
                    if (!addedEPCRecords.contains(epcString.toString())) {
                        LoggerUtil.info("chris_debug", "1@epc " + epcString);
                        addedEPCRecords.add(epcString.toString());
                        uniqueRecordCount = addedEPCRecords.size();
                        if (inflater != null) {
                            fullRow = (TableRow) inflater.inflate(R.layout.row, null, true);
                            fullRow.setId(uniqueRecordCount);

                            if (fullRow != null) {
                                nr = (TextView) fullRow.findViewById(R.id.nr);
                                if (nr != null) {
                                    nr.setText(String.valueOf(uniqueRecordCount));
                                    nr.setWidth(mReaderActivity.rowNumberWidth);
                                    epcValue = (TextView) fullRow.findViewById(R.id.EPC);

                                    if (epcValue != null) {
                                        epcValue.setText(tagRecordData.getEpcString());
                                        epcValue.setMaxWidth(mReaderActivity.epcDataWidth);
                                        countView = (TextView) fullRow.findViewById(R.id.COUNT);
                                        if (countView != null) {
                                            countView.setText(String.valueOf(tagRecordData.getReadCount()));
                                            countView.setWidth(mReaderActivity.epcCountWidth);
                                        }
                                        if (isEmbeddedRead) {
                                            dataView = (TextView) fullRow.findViewById(R.id.DATA);
                                            if (dataView != null) {
                                                dataView.setVisibility(View.VISIBLE);
                                                dataView.setText(String.valueOf(tagRecordData.getData()));
                                            }

                                        }
                                        table.addView(fullRow);
                                    }
                                }
                            }
                        }
                    } else {
                        LoggerUtil.info("chris_debug", "2222@epc " + epcString);
                        fullRow = (TableRow) table.getChildAt(Integer.valueOf(addedEPCRecords.indexOf(epcString)));
                        if (fullRow != null) {
                            countView = (TextView) fullRow.getChildAt(3);
                            if (countView != null
                                    && Integer.valueOf(countView.getText().toString()) != tagRecordData.getReadCount()) {
                                countView.setText(String.valueOf(tagRecordData.getReadCount()));
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                LoggerUtil.error(TAG, "Exception while populating tags :", ex);
            }
        }

        public static boolean isReading() {
            return reading;
        }

        @Override
        protected void onPostExecute(ConcurrentHashMap<String, TagRecord> stringTagRecordConcurrentHashMap) {
            super.onPostExecute(stringTagRecordConcurrentHashMap);
            timer.cancel();
            if (exceptionOccur) {
                totalTagCountView.setText("");
                if (totalTagCount > 0 && !operation.equalsIgnoreCase("syncRead")) {
                    if(exception.length() > 20)
                    {
                        totalTagCountView.setText(Html.fromHtml("<br>"));
                    }
                    totalTagCountView.setText(Html.fromHtml("<b>Total Tags  :</b> " + totalTagCount));
                }
            } else {
//                System.out.println("unique_tag_count :" + unique_tag_count);
                totalTagCountView.setText(Html.fromHtml("<b>Total Tags  :</b> " + totalTagCount));
                LoggerUtil.info(TAG, "onPostExecute + populateSearchResult");
                populateSearchResult(epcToReadDataMap);
                long elapsedTime = queryStopTime - queryStartTime;
                double timeTaken = (double) ((totalTagCount) / ((double) elapsedTime / 1000));
                DecimalFormat df = new DecimalFormat("#.##");
            }
            readButton.setClickable(true);
            if (operation.equalsIgnoreCase("AsyncRead")) {
                readButton.setText("Start Reading");
            } else if (operation.equalsIgnoreCase("SyncRead")) {
                readButton.setText("Read");
            }
            readButton.setClickable(true);
            syncReadRadioButton.setClickable(true);
            asyncReadRadioButton.setClickable(true);
            connectButton.setClickable(true);
            connectButton.setEnabled(true);
            if (exceptionOccur && !exception.equalsIgnoreCase("No connected antennas found")) {
                disconnectReader();
            }
        }

        private static void disconnectReader() {
            connectButton.setText("Connect");
            mReaderActivity.reader = null;
            if(!exceptionOccur)
            {
                searchResultCount.setText("");
            }
        }

        public void setReading(boolean reading) {
            ReadThread.reading = reading;
        }

        private void setReadplan(ReaderActivity mReaderActivity) throws ReaderException {

            int ant[] = getSelectedAntenna();
            if(ant==null)
                return;
            LoggerUtil.info(TAG, "antenna " + ant.toString());
            for (int i:ant) {
                LoggerUtil.info(TAG, "antenna-" + i);
            }
            SimpleReadPlan simplePlan = new SimpleReadPlan(ant, TagProtocol.GEN2, null, 1000);
            mReaderActivity.reader.paramSet("/reader/read/plan", simplePlan);
        }
        private int[] getSelectedAntenna() {
            CheckBox[] antennaBoxes = { ant1_checkbox, ant2_checkbox, ant3_checkbox, ant4_checkbox};
            List<Integer> ant = new ArrayList<Integer>();
            for (int antIdx = 0; antIdx < antennaBoxes.length; antIdx++)
            {
                CheckBox antBox = antennaBoxes[antIdx];

                if (antBox.getVisibility() == View.VISIBLE && antBox.isEnabled() && antBox.isChecked())
                {
                    int antNum = antIdx + 1;
                    ant.add(antNum);
                }
            }
            if(ant.size()==0){
                return null;
            }


            int [] antArr = new int[ant.size()];
            int i = 0;
            for (Integer a : ant) {
                antArr[i++] = a;
            }

            return antArr;
        }

        static class PrintListener implements ReadListener {
            public void tagRead(Reader r, final TagReadData tr) {
                readThread.parseTag(tr, true);
            }
        }

        private void parseTag(TagReadData tr, boolean publishResult) {
            totalTagCount += tr.getReadCount();
            String epcString = tr.getTag().epcString();
            if (epcToReadDataMap.keySet().contains(epcString)) {
                TagRecord tempTR = epcToReadDataMap.get(epcString);
                tempTR.readCount += tr.getReadCount();
            } else {
                TagRecord tagRecord = new TagRecord();
                tagRecord.setEpcString(epcString);
                tagRecord.setReadCount(tr.getReadCount());
                epcToReadDataMap.put(epcString, tagRecord);
                LoggerUtil.info(TAG, "[ "+epcToReadDataMap.size()+", "+totalTagCount+"]  add " + epcString);
            }
        }

        // private static int connectionLostCount=0;
        static class TagReadExceptionReceiver implements ReadExceptionListener {
            public void tagReadException(Reader r, ReaderException re) {
                if (re.getMessage().contains("The module has detected high return loss")
                        || re.getMessage().contains("Tag ID buffer full")
                        || re.getMessage().contains("No tags found")) {
                    // exception = "No connected antennas found";
                    /* Continue reading */
                } else {
                    LoggerUtil.error(TAG, "Reader exception : ", re);
                    exception = re.getMessage();
                    exceptionOccur = true;
                    readThread.setReading(false);
                    readThread.publishProgress(-1);
                }
            }
        }

        private void refreshReadRate() {
            timer = new Timer();
            timer.schedule( new TimerTask() {
                @Override
                public void run() {
                    publishProgress(0);
                }
            }, 100, 300);
        }
    }
}
