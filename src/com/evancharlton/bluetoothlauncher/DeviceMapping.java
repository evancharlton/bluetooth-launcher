
package com.evancharlton.bluetoothlauncher;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class DeviceMapping {
    private static final String NAME = "mappings";

    private final SharedPreferences mPreferences;

    private final Context mContext;

    public DeviceMapping(Context context) {
        mPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        mContext = context;
    }

    public void put(BluetoothDevice device, ApplicationInfo app) {
        save(mPreferences.edit().putString(device.getAddress(), app.packageName));
    }

    public void remove(BluetoothDevice device) {
        save(mPreferences.edit().remove(device.getAddress()));
    }

    private String getPackageName(BluetoothDevice device) {
        return mPreferences.getString(device.getAddress(), null);
    }

    public void startApp(BluetoothDevice device) {
        String app = getPackageName(device);
        if (app == null) return;

        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(app);
        mContext.startActivity(intent);
    }

    public String getAppTitle(BluetoothDevice device) {
        try {
            ApplicationInfo appInfo =
                    mContext.getPackageManager().getApplicationInfo(getPackageName(device),
                            PackageManager.GET_META_DATA);
            return mContext.getPackageManager().getApplicationLabel(appInfo).toString();
        } catch (NameNotFoundException e) {
        }
        return null;
    }

    public Drawable getAppIcon(BluetoothDevice device) {
        Drawable d = null;
        try {
            d = mContext.getPackageManager().getApplicationIcon(getPackageName(device));
            setBounds(mContext, d);
        } catch (NameNotFoundException e) {
        }
        return d;
    }

    private void save(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }

    public static Drawable setBounds(Context c, Drawable d) {
        if (d == null) return null;

        int px = c.getResources().getDimensionPixelSize(R.dimen.icon_square);
        d.setBounds(0, 0, px, px);
        return d;
    }
}
