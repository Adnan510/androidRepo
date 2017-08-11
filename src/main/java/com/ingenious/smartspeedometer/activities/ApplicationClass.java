
package com.ingenious.smartspeedometer.activities;

import android.app.Application;

/**
 * Created by ingenious on 11/03/16.
 */
public class ApplicationClass extends Application {

    public static final String TAG = ApplicationClass.class.getSimpleName();
    private static ApplicationClass mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        /*XModeSDK.init(getApplicationContext());*/

    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        /*Intent intent = new Intent();
        intent.setAction("com.mydomain.SEND_LOG"); // see step 5.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);*/

        System.exit(1); // kill off the crashed app
    }

    public static synchronized ApplicationClass getInstance() {
        return mInstance;
    }


}
