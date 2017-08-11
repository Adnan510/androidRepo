package com.ingenious.smartspeedometer.utils.gpsutils;
import android.location.Location;

/**
 * Created by ingenious on 1/8/16.
 */

public interface GPSCallback {
    public abstract void onGPSUpdate(Location location);
}

