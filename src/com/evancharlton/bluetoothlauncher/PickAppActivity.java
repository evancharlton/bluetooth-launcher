
package com.evancharlton.bluetoothlauncher;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class PickAppActivity extends Activity {
    public static final String EXTRA_DEVICE = "device";

    private DeviceMapping mMapping;

    private BluetoothDevice mDevice;

    private ListView mApps;

    private TextView mAppTitle;

    private ViewGroup mContainer;

    private AppsAdapter mAdapter;

    private AppsLoader mLoader;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_app);
        setResult(RESULT_CANCELED);
        mMapping = new DeviceMapping(this);

        mContainer = (ViewGroup) findViewById(R.id.mapping_container);
        mAppTitle = (TextView) findViewById(R.id.app);
        mApps = (ListView) findViewById(R.id.list);
        mApps.setEmptyView(findViewById(android.R.id.empty));

        mDevice = getIntent().getParcelableExtra(EXTRA_DEVICE);
        updateMapping();

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapping.remove(mDevice);
                setResult(RESULT_OK);
                finish();
            }
        });

        mAdapter = new AppsAdapter(this);

        mApps.setAdapter(mAdapter);
        mApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationInfo appInfo = mAdapter.getItem(position);
                mMapping.put(mDevice, appInfo);
                setResult(RESULT_OK);
                finish();
            }
        });

        Object[] saved = (Object[]) getLastNonConfigurationInstance();
        if (saved != null) {
            mLoader = (AppsLoader) saved[0];
            mAdapter.setApps((List<ApplicationInfo>) saved[1]);
        }

        if (mLoader == null) {
            mLoader = new AppsLoader(this);
        }
        if (mLoader.getStatus() == AsyncTask.Status.PENDING) {
            mLoader.execute();
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return new Object[] {
                mLoader, mAdapter.getApps()
        };
    }

    private void updateMapping() {
        String title = mMapping.getAppTitle(mDevice);
        if (title == null) {
            mContainer.setVisibility(View.GONE);
            return;
        }
        mContainer.setVisibility(View.VISIBLE);
        mAppTitle.setText(title);
        mAppTitle.setCompoundDrawables(mMapping.getAppIcon(mDevice), null, null, null);
    }

    private void appsLoaded(List<ApplicationInfo> apps) {
        mAdapter.setApps(apps);
    }

    private static class AppsLoader extends AsyncTask<Void, Void, List<ApplicationInfo>> {
        private final PackageManager mPackageManager;

        private final PickAppActivity mActivity;

        public AppsLoader(PickAppActivity activity) {
            mPackageManager = activity.getPackageManager();
            mActivity = activity;
        }

        @Override
        protected List<ApplicationInfo> doInBackground(Void... params) {
            List<ApplicationInfo> apps =
                    mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(mPackageManager));
            return apps;
        }

        @Override
        protected void onPostExecute(List<ApplicationInfo> result) {
            mActivity.appsLoaded(result);
        }
    }
}
