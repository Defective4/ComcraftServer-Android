package net.comcraft.server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import net.comcraft.src.Server;

public class ActivityMain extends Activity {

    private NotificationManager notManager;
    EditText addressField;
    EditText portField;
    EditText sizeField;
    EditText flatLevelField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addressField = new EditText(this);
        this.portField = new EditText(this);
        this.sizeField = new EditText(this);
        this.flatLevelField = new EditText(this);

        this.notManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        String address = "0.0.0.0";
        String port = "9999";
        String size = "16";
        String flatLevel = "12";

        addressField.setText(address);
        portField.setText(port);
        sizeField.setText(size);
        flatLevelField.setText(flatLevel);

        final Button stateToggle = createComponent("Start", Button.class);
        final TextView stateLabel = createComponent("Status: Stopped", TextView.class);

        final Spinner worldTypeField = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        adapter.add("NORMAL");
        adapter.add("FLAT");
        worldTypeField.setAdapter(adapter);

        final Spinner treeField = new Spinner(this);
        ArrayAdapter<Boolean> boolAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        boolAdapter.add(true);
        boolAdapter.add(false);
        treeField.setAdapter(boolAdapter);

        final Spinner allowCommandsField = new Spinner(this);
        boolAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        boolAdapter.add(true);
        boolAdapter.add(false);
        allowCommandsField.setAdapter(boolAdapter);

        final Button clearWorld = createComponent("Clear data", Button.class);

        clearWorld.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                File f = getFilesDir();
                if (f.exists())
                    for (File fl : f.listFiles()) {
                        recursiveDelete(fl);
                    }
            }

            private void recursiveDelete(File f) {
                if (f.isDirectory()) {
                    for (File ff : f.listFiles())
                        if (!ff.getName().equals("server.a.properties"))
                            recursiveDelete(ff);
                    f.delete();
                } else {
                    f.delete();
                }
            }
        });

        root.addView(stateToggle);
        root.addView(stateLabel);
        root.addView(withLabel("Address", addressField));
        root.addView(withLabel("Port", portField));
        root.addView(withLabel("World size", sizeField));
        root.addView(withLabel("World type", worldTypeField));
        root.addView(withLabel("Flat level", flatLevelField));
        root.addView(withLabel("Generate trees", treeField));
        root.addView(withLabel("Allow commands", allowCommandsField));
        root.addView(clearWorld);

        ScrollView sc = new ScrollView(this);
        sc.addView(root);
        setContentView(sc);

        stateToggle.setOnClickListener(new OnClickListener() {

            Server srv;

            @Override
            public void onClick(View arg0) {
                boolean isRunning = srv != null && srv.isRunning();
                if (isRunning) {
                    srv.stop();
                    srv = null;
                    notManager.cancel(1);
                } else {
                    String address = addressField.getText().toString();
                    String port = portField.getText().toString();
                    String size = sizeField.getText().toString();
                    String worldType = (String) worldTypeField.getSelectedItem();
                    String flatLevel = sizeField.getText().toString();
                    boolean genTrees = (boolean) treeField.getSelectedItem();
                    boolean allowCommands = (boolean) allowCommandsField.getSelectedItem();

                    srv = new Server(Logger.global, ActivityMain.this, new Settings(address, Integer.parseInt(port),
                            Integer.parseInt(size), worldType, Integer.parseInt(flatLevel), genTrees, allowCommands));
                    srv.start();

                    Notification not = new Notification.Builder(ActivityMain.this).setSmallIcon(R.drawable.icon)
                            .setContentText("ComCraft server is running on " + address + ":" + port)
                            .setContentTitle("ComCraft Server").setOngoing(true).getNotification();
                    notManager.notify(1, not);
                }
                String txt = isRunning ? "Start" : "Stop";
                stateLabel.setText("Status: " + (isRunning ? "Stopped" : "Started"));
                clearWorld.setEnabled(isRunning);
                stateToggle.setText(txt);
            }
        });
    }

    private LinearLayout withLabel(String text, View view) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(createComponent(text + ": ", TextView.class));
        layout.addView(view);
        return layout;
    }

    private <T extends TextView> T createComponent(String text, Class<T> type) {
        T t = null;
        try {
            t = type.getConstructor(Context.class).newInstance(this);
            t.setText(text);
        } catch (IllegalArgumentException | InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        return t;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
    }

}
