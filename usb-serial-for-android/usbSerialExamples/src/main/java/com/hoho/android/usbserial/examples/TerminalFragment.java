package com.hoho.android.usbserial.examples;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

public class TerminalFragment extends Fragment implements SerialInputOutputManager.Listener {
    private HorizontalScrollView scrollView;
    private Button ter;
    private Button buttonC3;
    private Button buttonC3black;

    private Button buttonD3;
    private Button buttonD3black;

    private Button buttonE3;

    private Button buttonF3;
    private Button buttonF3black;

    private Button buttonG3;
    private Button buttonG3black;

    private Button buttonA3;
    private Button buttonA3black;

    private Button buttonB3;

    private Button buttonC4;
    private Button buttonC4black;

    private Button buttonD4;
    private Button buttonD4black;

    private Button buttonE4;

    private Button buttonF4;
    private Button buttonF4black;

    private Button buttonG4;
    private Button buttonG4black;

    private Button buttonA4;
    private Button buttonA4black;

    private Button buttonB4;


    private Button buttonC5;
    private Button buttonC5black;

    private Button buttonD5;
    private Button buttonD5black;

    private Button buttonE5;

    private Button buttonF5;
    private Button buttonF5black;

    private Button buttonG5;
    private Button buttonG5black;

    private Button buttonA5;
    private Button buttonA5black;

    private Button buttonB5;

    private Button buttonC6;
    private Button buttonC6black;

    private Button buttonD6;
    private Button buttonD6black;

    private Button buttonE6;

    private Button buttonF6;
    private Button buttonF6black;

    private Button buttonG6;
    private Button buttonG6black;

    private Button buttonA6;
    private Button buttonA6black;

    private Button buttonB6;

    private Button buttonC7;
    private Button buttonC7black;

    private Button buttonD7;
    private Button buttonD7black;

    private Button buttonE7;

    private Button buttonF7;
    private Button buttonF7black;

    private Button buttonG7;
    private Button buttonG7black;

    private Button buttonA7;
    private Button buttonA7black;

    private Button buttonB7;

    private enum UsbPermission { Unknown, Requested, Granted, Denied }

    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    private static final int WRITE_WAIT_MILLIS = 2000;
    private static final int READ_WAIT_MILLIS = 2000;

    private int deviceId, portNum, baudRate;
    private boolean withIoManager;

    private final BroadcastReceiver broadcastReceiver;
    private final Handler mainLooper;
    private TextView receiveText;
    private ControlLines controlLines;

    private SerialInputOutputManager usbIoManager;
    private UsbSerialPort usbSerialPort;
    private UsbPermission usbPermission = UsbPermission.Unknown;
    private boolean connected = false;

    public TerminalFragment() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
                    usbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                            ? UsbPermission.Granted : UsbPermission.Denied;
                    connect();
                }
            }
        };
        mainLooper = new Handler(Looper.getMainLooper());
    }

    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceId = getArguments().getInt("device");
        portNum = getArguments().getInt("port");
        baudRate = getArguments().getInt("baud");
        withIoManager = getArguments().getBoolean("withIoManager");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));

        if(usbPermission == UsbPermission.Unknown || usbPermission == UsbPermission.Granted)
            mainLooper.post(this::connect);
    }

    @Override
    public void onPause() {
        if(connected) {
            status("disconnected");
            disconnect();
        }
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    /*
     * UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        receiveText = view.findViewById(R.id.receive_text);
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText));
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());
        TextView sendText = view.findViewById(R.id.send_text);
        View sendBtn = view.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(v -> send(sendText.getText().toString()));
        View receiveBtn = view.findViewById(R.id.receive_btn);


            buttonC3 = (Button) view.findViewById(R.id.p1);

            buttonC3black = (Button) view.findViewById(R.id.b1);

            buttonD3 = (Button) view.findViewById(R.id.p2);

            buttonD3black = (Button) view.findViewById(R.id.b2);

            buttonE3 = (Button) view.findViewById(R.id.p3);

            buttonF3 = (Button) view.findViewById(R.id.p4);

            buttonF3black = (Button) view.findViewById(R.id.b3);

            buttonG3 = (Button) view.findViewById(R.id.p5);

            buttonG3black = (Button) view.findViewById(R.id.b4);

            buttonA3 = (Button) view.findViewById(R.id.p6);

            buttonA3black = (Button) view.findViewById(R.id.b5);

            buttonB3 = (Button) view.findViewById(R.id.p7);



            buttonC4 = (Button) view.findViewById(R.id.p8);

            buttonC4black = (Button) view.findViewById(R.id.b6);

            buttonD4 = (Button) view.findViewById(R.id.p9);

            buttonD4black = (Button) view.findViewById(R.id.b7);

            buttonE4 = (Button) view.findViewById(R.id.p10);

            buttonF4 = (Button) view.findViewById(R.id.p11);

            buttonF4black = (Button) view.findViewById(R.id.b8);

            buttonG4 = (Button) view.findViewById(R.id.p12);

            buttonG4black = (Button) view.findViewById(R.id.b9);

            buttonA4 = (Button) view.findViewById(R.id.p13);

            buttonA4black = (Button) view.findViewById(R.id.b10);

            buttonB4 = (Button) view.findViewById(R.id.p14);



            buttonC5 = (Button) view.findViewById(R.id.p15);

            buttonC5black = (Button) view.findViewById(R.id.b11);

            buttonD5 = (Button) view.findViewById(R.id.p16);

            buttonD5black = (Button) view.findViewById(R.id.b12);

            buttonE5 = (Button) view.findViewById(R.id.p17);

            buttonF5 = (Button) view.findViewById(R.id.p18);

            buttonF5black = (Button) view.findViewById(R.id.b13);

            buttonG5 = (Button) view.findViewById(R.id.p19);

            buttonG5black = (Button) view.findViewById(R.id.b14);

            buttonA5 = (Button) view.findViewById(R.id.p20);

            buttonA5black = (Button) view.findViewById(R.id.b15);

            buttonB5 = (Button) view.findViewById(R.id.p21);



            buttonC6 = (Button) view.findViewById(R.id.p22);

            buttonC6black = (Button) view.findViewById(R.id.b16);

            buttonD6 = (Button) view.findViewById(R.id.p23);

            buttonD6black = (Button) view.findViewById(R.id.b17);

            buttonE6 = (Button) view.findViewById(R.id.p24);

            buttonF6 = (Button) view.findViewById(R.id.p25);

            buttonF6black = (Button) view.findViewById(R.id.b18);

            buttonG6 = (Button) view.findViewById(R.id.p26);

            buttonG6black = (Button) view.findViewById(R.id.b19);

            buttonA6 = (Button) view.findViewById(R.id.p27);

            buttonA6black = (Button) view.findViewById(R.id.b20);

            buttonB6 = (Button) view.findViewById(R.id.p28);



            buttonC7 = (Button) view.findViewById(R.id.p29);

            buttonC7black = (Button) view.findViewById(R.id.b21);

            buttonD7 = (Button) view.findViewById(R.id.p30);

            buttonD7black = (Button) view.findViewById(R.id.b22);

            buttonE7 = (Button) view.findViewById(R.id.p31);

            buttonF7 = (Button) view.findViewById(R.id.p32);

            buttonF7black = (Button) view.findViewById(R.id.b23);

            buttonG7 = (Button) view.findViewById(R.id.p33);

            buttonG7black = (Button) view.findViewById(R.id.b24);

            buttonA7 = (Button) view.findViewById(R.id.p34);

            buttonA7black = (Button) view.findViewById(R.id.b25);

            buttonB7 = (Button) view.findViewById(R.id.p35);





        controlLines = new ControlLines(view);
        if(withIoManager) {
            receiveBtn.setVisibility(View.GONE);
        } else {
            receiveBtn.setOnClickListener(v -> read());

        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_terminal, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clear) {
            receiveText.setText("");
            return true;
        } else if( id == R.id.send_break) {
            if(!connected) {
                Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    usbSerialPort.setBreak(true);
                    Thread.sleep(100); // should show progress bar instead of blocking UI thread
                    usbSerialPort.setBreak(false);
                    SpannableStringBuilder spn = new SpannableStringBuilder();
                    spn.append("send <break>\n");
                    spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    receiveText.append(spn);
                } catch(UnsupportedOperationException ignored) {
                    Toast.makeText(getActivity(), "BREAK not supported", Toast.LENGTH_SHORT).show();
                } catch(Exception e) {
                    Toast.makeText(getActivity(), "BREAK failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Serial
     */
    @Override
    public void onNewData(byte[] data) {
        mainLooper.post(() -> {
            receive(data);
        });
    }

    @Override
    public void onRunError(Exception e) {
        mainLooper.post(() -> {
            status("connection lost: " + e.getMessage());
            disconnect();
        });
    }

    /*
     * Serial + UI
     */
    private void connect() {
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
        for(UsbDevice v : usbManager.getDeviceList().values())
            if(v.getDeviceId() == deviceId)
                device = v;
        if(device == null) {
            status("connection failed: device not found");
            return;
        }
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if(driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device);
        }
        if(driver == null) {
            status("connection failed: no driver for device");
            return;
        }
        if(driver.getPorts().size() < portNum) {
            status("connection failed: not enough ports at device");
            return;
        }
        usbSerialPort = driver.getPorts().get(portNum);
        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if(usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(driver.getDevice())) {
            usbPermission = UsbPermission.Requested;
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return;
        }
        if(usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
                status("connection failed: permission denied");
            else
                status("connection failed: open failed");
            return;
        }

        try {
            usbSerialPort.open(usbConnection);
            usbSerialPort.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE);
            if(withIoManager) {
                usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
                usbIoManager.start();
            }
            status("connected");
            connected = true;
            controlLines.start();
        } catch (Exception e) {
            status("connection failed: " + e.getMessage());
            disconnect();
        }
    }

    private void disconnect() {
        connected = false;
        controlLines.stop();
        if(usbIoManager != null) {
            usbIoManager.setListener(null);
            usbIoManager.stop();
        }
        usbIoManager = null;
        try {
            usbSerialPort.close();
        } catch (IOException ignored) {}
        usbSerialPort = null;
    }

    private void send(String str) {
        if(!connected) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
        byte[] data = (str + '\n').getBytes();
            SpannableStringBuilder spn = new SpannableStringBuilder();
            spn.append("send " + data.length + " bytes\n");
            spn.append(HexDump.dumpHexString(data)).append("\n");
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn);
            usbSerialPort.write(data, WRITE_WAIT_MILLIS);
        } catch (Exception e) {
            onRunError(e);
        }
    }

    private void read() {
        if(!connected) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            byte[] buffer = new byte[8192];
            int len = usbSerialPort.read(buffer, READ_WAIT_MILLIS);
            receive(Arrays.copyOf(buffer, len));

        } catch (IOException e) {
            // when using read with timeout, USB bulkTransfer returns -1 on timeout _and_ errors
            // like connection loss, so there is typically no exception thrown here on error
            status("connection lost: " + e.getMessage());
            disconnect();
        }
    }

    private void receive(byte[] data) {
        SpannableStringBuilder spn = new SpannableStringBuilder();
        spn.append("receive " + data.length + " bytes\n");
        if(data.length > 0)
            spn.append(HexDump.dumpHexString(data)).append("\n");
        receiveText.append(spn);
    }

    void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str+'\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    class ControlLines {
        private static final int refreshInterval = 200; // msec

        private final Runnable runnable;
        private final ToggleButton rtsBtn, ctsBtn, dtrBtn, dsrBtn, cdBtn, riBtn;

        ControlLines(View view) {
            runnable = this::run;

            rtsBtn = view.findViewById(R.id.controlLineRts);
            ctsBtn = view.findViewById(R.id.controlLineCts);
            dtrBtn = view.findViewById(R.id.controlLineDtr);
            dsrBtn = view.findViewById(R.id.controlLineDsr);
            cdBtn = view.findViewById(R.id.controlLineCd);
            riBtn = view.findViewById(R.id.controlLineRi);
            rtsBtn.setOnClickListener(this::toggle);
            dtrBtn.setOnClickListener(this::toggle);
        }

        private void toggle(View v) {
            ToggleButton btn = (ToggleButton) v;
            if (!connected) {
                btn.setChecked(!btn.isChecked());
                Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            String ctrl = "";
            try {
                if (btn.equals(rtsBtn)) { ctrl = "RTS"; usbSerialPort.setRTS(btn.isChecked()); }
                if (btn.equals(dtrBtn)) { ctrl = "DTR"; usbSerialPort.setDTR(btn.isChecked()); }
            } catch (IOException e) {
                status("set" + ctrl + "() failed: " + e.getMessage());
            }
        }

        private void run() {
            if (!connected)
                return;
            try {
                EnumSet<UsbSerialPort.ControlLine> controlLines = usbSerialPort.getControlLines();
                rtsBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.RTS));
                ctsBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.CTS));
                dtrBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.DTR));
                dsrBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.DSR));
                cdBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.CD));
                riBtn.setChecked(controlLines.contains(UsbSerialPort.ControlLine.RI));
                mainLooper.postDelayed(runnable, refreshInterval);
            } catch (IOException e) {
                status("getControlLines() failed: " + e.getMessage() + " -> stopped control line refresh");
            }
        }

        void start() {
            if (!connected)
                return;
            try {
                EnumSet<UsbSerialPort.ControlLine> controlLines = usbSerialPort.getSupportedControlLines();
                if (!controlLines.contains(UsbSerialPort.ControlLine.RTS)) rtsBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.CTS)) ctsBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.DTR)) dtrBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.DSR)) dsrBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.CD))   cdBtn.setVisibility(View.INVISIBLE);
                if (!controlLines.contains(UsbSerialPort.ControlLine.RI))   riBtn.setVisibility(View.INVISIBLE);
                run();
            } catch (IOException e) {
                Toast.makeText(getActivity(), "getSupportedControlLines() failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        void stop() {
            mainLooper.removeCallbacks(runnable);
            rtsBtn.setChecked(false);
            ctsBtn.setChecked(false);
            dtrBtn.setChecked(false);
            dsrBtn.setChecked(false);
            cdBtn.setChecked(false);
            riBtn.setChecked(false);
        }
    }
}
