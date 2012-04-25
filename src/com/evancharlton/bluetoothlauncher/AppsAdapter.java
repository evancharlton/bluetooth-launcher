
package com.evancharlton.bluetoothlauncher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppsAdapter extends BaseAdapter {
    private final List<ApplicationInfo> mApps = new ArrayList<ApplicationInfo>();

    private final PackageManager mPackageManager;

    private final Context mContext;

    public AppsAdapter(Context context) {
        mContext = context;
        mPackageManager = context.getPackageManager();
    }

    public void setApps(List<ApplicationInfo> apps) {
        mApps.clear();
        mApps.addAll(apps);
        notifyDataSetChanged();
    }

    public List<ApplicationInfo> getApps() {
        return mApps;
    }

    @Override
    public int getCount() {
        return mApps.size();
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return mApps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mApps.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView =
                    LayoutInflater.from(mContext).inflate(R.layout.device_list_item, parent, false);
        }

        ViewHolder holder = ViewHolder.get(convertView);

        ApplicationInfo appInfo = getItem(position);

        holder.text.setText(appInfo.loadLabel(mPackageManager));
        holder.text.setCompoundDrawables(
                DeviceMapping.setBounds(mContext, appInfo.loadIcon(mPackageManager)), null, null,
                null);

        return convertView;
    }

    private static final class ViewHolder {
        public final TextView text;

        private ViewHolder(View v) {
            text = (TextView) v.findViewById(R.id.text);
            v.setTag(this);
        }

        public static ViewHolder get(View v) {
            if (v.getTag() != null) return (ViewHolder) v.getTag();
            return new ViewHolder(v);
        }
    }
}
