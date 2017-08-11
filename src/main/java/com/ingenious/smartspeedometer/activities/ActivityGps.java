package com.ingenious.smartspeedometer.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ingenious.smartspeedometer.R;
import com.ingenious.smartspeedometer.utils.Speedometer;
import com.ingenious.smartspeedometer.utils.gpsutils.Constants;
import com.ingenious.smartspeedometer.utils.gpsutils.GPSCallback;
import com.ingenious.smartspeedometer.utils.gpsutils.GPSManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import rebus.permissionutils.AskagainCallback;
import rebus.permissionutils.FullCallback;
import rebus.permissionutils.PermissionEnum;
import rebus.permissionutils.PermissionManager;


public class ActivityGps extends AppCompatActivity implements GPSCallback {
    private GPSManager gpsManager = null;
    private double speed = 0.0;
    private AbsoluteSizeSpan sizeSpanLarge = null;
    private AbsoluteSizeSpan sizeSpanSmall = null;
    TextView tvSensor;
    Button btnSwitch;
    Speedometer speedometer;
    Context ctx;
    private AdView mAdView;
    @Override
    protected void onResume() {
        super.onResume();
        PermissionManager.with(ActivityGps.this)
                .permission(PermissionEnum.ACCESS_COARSE_LOCATION, PermissionEnum.ACCESS_FINE_LOCATION)
                .askagain(true)
                .askagainCallback(new AskagainCallback() {
                    @Override
                    public void showRequestPermission(UserResponse response) {
                        showDialog(response);
                    }
                })
                .callback(new FullCallback() {
                    @Override
                    public void result(ArrayList<PermissionEnum> permissionsGranted, ArrayList<PermissionEnum> permissionsDenied, ArrayList<PermissionEnum> permissionsDeniedForever, ArrayList<PermissionEnum> permissionsAsked) {
                    }
                })
                .ask();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx=this;
        initialiseElements();
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        clickEvents();
    }

    @Override
    public void onGPSUpdate(Location location) {
        location.getLatitude();
        location.getLongitude();
        speed = location.getSpeed();

        Double speedKM=(roundDecimal(convertSpeed(speed), 2));

        speedometer.onSpeedChanged(speedKM.longValue());

        if (speedKM>50)
        {
            speedometer.setBackgroundColor(getResources().getColor(R.color.red));
            tvSensor.setText(ctx.getResources().getString(R.string.high));
            tvSensor.setTextColor(getResources().getColor(R.color.red));
        }
        else if (speedKM>25)
        {
            tvSensor.setText(ctx.getResources().getString(R.string.average));
            tvSensor.setTextColor(getResources().getColor(R.color.orrange));
            speedometer.setBackgroundColor(getResources().getColor(R.color.orrange));

        }
        else
        {
            tvSensor.setText(ctx.getResources().getString(R.string.low));
            tvSensor.setTextColor(getResources().getColor(R.color.green));
            speedometer.setBackgroundColor(getResources().getColor(R.color.green));

        }
    }


    private void initialiseElements()
    {
        gpsManager = new GPSManager(this);
        gpsManager.startListening(getApplicationContext());
        gpsManager.setGPSCallback(this);

        ((TextView) findViewById(R.id.sensorX))
                .setText(ctx.getResources().getString(R.string.gps_speed));
        tvSensor = (TextView) findViewById(R.id.sensorX);
        tvSensor.setText("Please wait while the GPS detect your moving speed.");
        speedometer = (Speedometer) findViewById(R.id.speedometer);
        btnSwitch= (Button) findViewById(R.id.switch_activity);
        btnSwitch.setText(ctx.getResources().getString(R.string.switch_to_sensor));


    }

    @Override
    public void onBackPressed() {
        Intent in= new Intent(ctx, ActivitySensor.class);
        startActivity(in);
        finish();
    }

    private  void clickEvents()
    {
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in= new Intent(ctx, ActivitySensor.class);
                startActivity(in);
                finish();
            }
        });
    }
    @Override
    protected void onDestroy() {
        gpsManager.stopListening();
        gpsManager.setGPSCallback(null);

        gpsManager = null;

        super.onDestroy();
    }


    private double convertSpeed(double speed) {
        return ((speed * Constants.HOUR_MULTIPLIER) * Constants.UNIT_MULTIPLIERS);
    }

    private double roundDecimal(double value, final int decimalPlace) {
        BigDecimal bd = new BigDecimal(value);

        bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
        value = bd.doubleValue();

        return value;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handleResult(requestCode, permissions, grantResults);
    }

    private void showDialog(final AskagainCallback.UserResponse response) {
        new AlertDialog.Builder(ActivityGps.this)
                .setTitle("Permission needed")
                .setMessage("This app realy need to use this permission, you want to authorize it?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        response.result(true);
                    }
                })
                .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        response.result(false);
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_privacy:
                privacyDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void privacyDialog(){
        final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.privacy_dialog, null);
        dialogBuilder.setView(dialogView);
        final WebView privacyView=(WebView) dialogView.findViewById(R.id.privacyView);
        privacyView.loadUrl("file:///android_asset/policy.html");
        dialogBuilder.setTitle("Privacy Policy");
        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.show();
    }
}