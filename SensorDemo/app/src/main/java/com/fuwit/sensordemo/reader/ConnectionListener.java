package com.fuwit.sensordemo.reader;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fuwit.sensordemo.LoggerUtil;
import com.fuwit.sensordemo.R;
import com.fuwit.sensordemo.ReaderActivity;
import com.fuwit.sensordemo.reader.service.UsbService;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.SerialTransportAndroid;
import com.thingmagic.SerialTransportTCP;
import com.thingmagic.TMConstants;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android_serialport_api.SerialPortFinder;

import static android.R.layout.simple_spinner_item;

public class ConnectionListener extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ConnectionListener";
    private final ReaderActivity mReaderActivity;
    private static Reader reader = null;

    RadioButton serialRadioButton;
    RadioButton tcpRadioButton;
    Button connectButton;
    AutoCompleteTextView tcpUriTextView;
    Spinner serialSpinner;
    private TextView validationField;
    Spinner baudRateSpinner;

    private static ProgressDialog pDialog = null;

    List<String> serialUriList = new ArrayList<>();
    List<String> tcpUriList = new ArrayList<>();
    private static String[] uirData = new String[] {
            "tcp://192.168.8.166:8086","tcp://192.168.1.100:8086","tcp://192.168.8.101:8086","tcp://192.168.8.102:8086",
            "tmr:///dev/ttyS0", "tmr:///dev/ttyS2", "tmr:///COM19",//串口
            "tmr:///98:D3:32:30:9F:05",//蓝牙
    };
    private String query = null;

    public ConnectionListener(ReaderActivity readerActivity) {
        LoggerUtil.info(TAG, "ConnectionListener ");
        mReaderActivity = readerActivity;
        pDialog = new ProgressDialog(readerActivity);
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        findAllViewById();

        if(mReaderActivity.isConnected == true)
            connectButton.setText("Disconnect");

        serialRadioButton.setOnClickListener(this);
        tcpRadioButton.setOnClickListener(this);
    }


    private void findAllViewById() {
        serialSpinner = mReaderActivity.findViewById(R.id.serial_spinner);
        serialRadioButton = mReaderActivity.findViewById(R.id.connect_type_serial_radio_button);
        tcpRadioButton = mReaderActivity.findViewById(R.id.connect_type_tcp_radio_button);
        connectButton = mReaderActivity.findViewById(R.id.connect_button);
        tcpUriTextView = mReaderActivity.findViewById(R.id.tcp_uri_autocompeletetextview);
        validationField = mReaderActivity.findViewById(R.id.ValidationField);
        baudRateSpinner = mReaderActivity.findViewById(R.id.baudrate_spinner);
    }

    @Override
    public void onClick(View view) {
        validationField.setText("");
        validationField.setVisibility(8);
        switch (view.getId())
        {
            case R.id.connect_button:
            {
                DealConnect();
            }
            case R.id.connect_type_serial_radio_button:
            {
                LoggerUtil.info(TAG, "Serial@isChecked=" + serialRadioButton.isChecked());
                if(serialRadioButton.isChecked())
                {
                    serialSpinner.setVisibility(View.VISIBLE);
                    tcpUriTextView.setVisibility(View.GONE);
                    getSerials();
                }
                break;
            }
            case R.id.connect_type_tcp_radio_button:
            {
                LoggerUtil.info(TAG, "TCP@isChecked=" + tcpRadioButton.isChecked());
                if(tcpRadioButton.isChecked())
                {
                    serialSpinner.setVisibility(View.GONE);
                    tcpUriTextView.setVisibility(View.VISIBLE);
                    getTcps();
                }
                break;
            }
        }
    }

    private void getTcps() {
        tcpUriList.clear();
        LoggerUtil.info(TAG, "Add tcp ...");
        tcpUriList.add("tcp://192.168.1.106:8086");
        tcpUriList.add("tcp://192.168.8.166:8086");

        ArrayAdapter<String> tcpAdapter = new ArrayAdapter<String>(mReaderActivity, android.R.layout.simple_dropdown_item_1line, tcpUriList);
        tcpUriTextView.setAdapter(tcpAdapter);
    }

    private void getSerials() {
        serialUriList.clear();

        ReaderConnectionThread readerConnectionThread = new ReaderConnectionThread(
                null, "getserials");
        readerConnectionThread.execute();
    }

    private void DealConnect() {
        try {
            String uriString = null;
            if(serialRadioButton.isChecked())
            {
                uriString = serialSpinner.getSelectedItem().toString();
            }
            else if(tcpRadioButton.isChecked())
            {
                uriString = tcpUriTextView.getText().toString().trim();
                if(!uriString.startsWith("tcp://"))
                {
                    uriString = "tcp://" + uriString;
                }
            }

            LoggerUtil.info(TAG, "DealConnect uriString=" + uriString);
            if (uriString.length() == 0) {
                throw new Exception("* Field can not be empty.");
            }

            if(tcpRadioButton.isChecked()) //Customer 模式连接的时候需要设�?
            {
                LoggerUtil.info(TAG, "DealConnect tcp");
                URI uri = new URI(uriString);
                String scheme = uri.getScheme();

                if (scheme == null) {
                    throw new Exception("Blank URI scheme.");
                }
                Reader.setSerialTransport("tcp", new SerialTransportTCP.Factory());
                query = uriString;
            } else if(serialRadioButton.isChecked())
            {
                if(uriString.startsWith("/dev"))
                {
                    if(uriString.startsWith("/dev/tty")) {
                        LoggerUtil.info(TAG, "DealConnect /dev/tty");
                        Reader.setSerialTransport("fuwit", new SerialTransportAndroid.Factory());
                        query = "fuwit://" + uriString;
                    }
                    else
                    {
                        LoggerUtil.info(TAG, "DealConnect /dev");
                        UsbService usbService = new UsbService();
                        usbService.setUsbManager(uriString, mReaderActivity);
                        query = "tmr://" + uriString;
                    }
                }
                else {
                    LoggerUtil.info(TAG, "DealConnect tmr");
                    query = "tmr://" + "/" + uriString;
                }
            }

            LoggerUtil.info(TAG, "DealConnect query=" + query);
        } catch (Exception ex) {
            validationField.setText(ex.getMessage());
            validationField.setVisibility(View.VISIBLE);
            LoggerUtil.error(TAG, "ERROR DealConnect : " + ex);
            return;
        }

        ReaderConnectionThread readerConnectionThread = new ReaderConnectionThread(
                query, connectButton.getText().toString());
        readerConnectionThread.execute();
    }

    private class ReaderConnectionThread extends
            AsyncTask<Void, Void, String> {
        private static final String TAG = "ReaderConnectionThread";
        private String uriString = "";
        private String operation;
        private boolean operationStatus = true;

        public ReaderConnectionThread(String requestedQuery, String operation) {
            this.uriString = requestedQuery;
            this.operation = operation;
        }

        @Override
        protected void onPreExecute() {
            LoggerUtil.info(TAG, "** onPreExecute **");
            if (operation.equalsIgnoreCase("Connect"))
            {
                disableEdit();
                pDialog.setMessage("Connecting. Please wait...");
            }
            else if (operation.equalsIgnoreCase("Disconnect")) {
                disableEdit();
                pDialog.setMessage("Disconnecting. Please wait...");
            } else {
                pDialog.setMessage(operation + " Please wait...");
            }
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            if(operation.equalsIgnoreCase(FUConstants.CONNECT_TYPE_GET_SERIALS))
            {
                LoggerUtil.info(TAG, "Getting Serial device list");
                try {
                    SerialPortFinder spf = new SerialPortFinder();
                    String[] serials = spf.getAllDevicesPath();
                    for (String s:serials) {
                        serialUriList.add(s);
                        LoggerUtil.info(TAG, s);
                    }
                } catch (Exception e)
                {
                    LoggerUtil.error(TAG, "get serials --> " + e.getMessage());
                }

                LoggerUtil.info(TAG, "Getting USB device list");
                UsbService usbService = new UsbService();
                ArrayList<String> connectedUSBDeviceNames = usbService.getConnectedUSBdevices(mReaderActivity);
                for (String deviceName : connectedUSBDeviceNames) {
                    serialUriList.add(deviceName);
                    LoggerUtil.info(TAG, deviceName);
                }

                BluetoothService bluetoothService = new BluetoothService();
                boolean btEnabled = bluetoothService.checkBTState(ConnectionListener.this, mReaderActivity);
                if (btEnabled) {
                    LoggerUtil.info(TAG, "Getting bluetooth device list");
                    Set<BluetoothDevice> pairedDevices = bluetoothService.getPairedDevices();
                    for (BluetoothDevice bluetoothDevice : pairedDevices) {
                        LoggerUtil.info(TAG, bluetoothDevice.getName() + ":" + bluetoothDevice.getAddress());
                        serialUriList.add(/*bluetoothDevice.getName() + ":" + */bluetoothDevice.getAddress());
                    }
                }
                return null;
            }
            else // connect
            {
                String exception = "Exception :";
                try {
                    if (operation.equalsIgnoreCase("Connect")) {
                        reader = readerConnect(uriString);

                        InitReader();

                        LoggerUtil.info(TAG, "Reader Connected");

                        mReaderActivity.isConnected = true;
                    }
                    else if (operation.equalsIgnoreCase("Disconnect")){
                        readerDisconnect();
                        LoggerUtil.info(TAG, "ReaderDisconnected");
                        mReaderActivity.isConnected = false;
                    }

                } catch (Exception ex) {
                    operationStatus = false;
                    if (ex.getMessage().contains("Connection is not created")
                            || ex.getMessage().startsWith("Failed to connect")) {
                        exception += "Failed to connect to " + query;
                    } else {
                        exception += ex.getMessage();
                    }
                    LoggerUtil.error(TAG, "Exception while Connecting :", ex);
                }
                return exception;
            }
        }

        private void readerDisconnect() {
            if(reader != null) {
                reader.destroy();
                LoggerUtil.info(TAG, "readerDisconnect destroy success");
            } else
                LoggerUtil.info(TAG, "readerDisconnect no reader connect");

        }

        private void InitReader() throws Exception {
            LoggerUtil.info(TAG, "InitReader");
            if (Reader.Region.UNSPEC == (Reader.Region) reader.paramGet("/reader/region/id")) {
                Reader.Region[] supportedRegions = (Reader.Region[]) reader.paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
                if (supportedRegions.length < 1) {
                    throw new Exception("Reader doesn't support any regions");
                } else {
                    reader.paramSet("/reader/region/id", supportedRegions[0]);
                }
            }

            mReaderActivity.model = (String) reader.paramGet(TMConstants.TMR_PARAM_VERSION_MODEL);
            mReaderActivity.uri = uriString;
        }

        @Override
        protected void onPostExecute(String exception) {
            pDialog.dismiss();
            LoggerUtil.info(TAG, "** onPostExecute **");
            if(operation.equalsIgnoreCase(FUConstants.CONNECT_TYPE_GET_SERIALS))
            {
                ArrayAdapter<String> serialAdapter = new ArrayAdapter<String>(mReaderActivity, simple_spinner_item, serialUriList);
                serialSpinner.setAdapter(serialAdapter);
                return;
            }

            if (!operationStatus) {
                validationField.setText(exception);
                validationField.setVisibility(0);
                if (operation.equalsIgnoreCase("Connect")) {
                    connectButton.setText("Connect");
                    enableEdit();
                    mReaderActivity.reader = null;
                }
            } else {
                validationField.setText("");
                validationField.setVisibility(8);
                if (operation.equalsIgnoreCase("Connect")) {
                    connectButton.setText("Disconnect");
                    disableEdit();
                    connectButton.setClickable(true);
                    mReaderActivity.reader = reader;
                    Message message = new Message();
                    message.what = 2;
                    mReaderActivity.handler.sendMessage(message);
                } else {
                    connectButton.setText("Connect");
                    enableEdit();
                    mReaderActivity.reader = null;
                    System.gc();
                    Message message = new Message();
                    message.what = 3;
                    mReaderActivity.handler.sendMessage(message);
                }
            }
        }

        private void disableEdit() {
            connectButton.setClickable(false);
            serialRadioButton.setClickable(false);
            tcpRadioButton.setClickable(false);
            serialSpinner.setClickable(false);
            tcpUriTextView.setClickable(false);
        }

        private void enableEdit() {
            connectButton.setClickable(true);
            serialRadioButton.setClickable(true);
            tcpRadioButton.setClickable(true);
            serialSpinner.setClickable(true);
            tcpUriTextView.setClickable(true);
        }
    }

    public Reader readerConnect(String uriString) throws Exception {
        Reader reader = null;
        try {
            LoggerUtil.info(TAG, "readerConnect uriString=" + uriString);
            reader = Reader.create(uriString);
            LoggerUtil.info(TAG, "create success ...");

            reader.connect();

            LoggerUtil.info(TAG, "connect success ...");

        } catch (Exception ex) {
            LoggerUtil.error(TAG, "readerConnect ",ex);
            return null;
        }
        return reader;
    }
}

