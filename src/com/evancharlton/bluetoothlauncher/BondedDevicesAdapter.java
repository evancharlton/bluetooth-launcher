
package com.evancharlton.bluetoothlauncher;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BondedDevicesAdapter extends BaseAdapter {
    private final List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();

    private final Context mContext;

    private final DeviceMapping mMapping;

    public BondedDevicesAdapter(Context context) {
        mContext = context;
        mDevices.addAll(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
        mMapping = new DeviceMapping(mContext);
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return mDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDevices.get(position).getAddress().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView =
                    LayoutInflater.from(mContext).inflate(R.layout.device_list_item, parent, false);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) holder = new ViewHolder(convertView);

        BluetoothDevice device = mDevices.get(position);

        holder.text.setText(getText(device));
        holder.text.setCompoundDrawables(null, null, mMapping.getAppIcon(device), null);

        return convertView;
    }

    private String getText(BluetoothDevice device) {
        return TextUtils.isEmpty(device.getName()) ? device.getAddress() : device.getName();
    }

    private static final class ViewHolder {
        public final TextView text;

        public ViewHolder(View v) {
            text = (TextView) v.findViewById(R.id.text);
            v.setTag(this);
        }
    }
}
