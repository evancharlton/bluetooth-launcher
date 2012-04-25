
package com.evancharlton.bluetoothlauncher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class BluetoothLauncherActivity extends Activity {
    private static final int PICK_ACTIVITY_CODE = 1;

    private BondedDevicesAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mAdapter = new BondedDevicesAdapter(this);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View row, int position, long id) {
                Intent intent = new Intent(BluetoothLauncherActivity.this, PickAppActivity.class);
                intent.putExtra(PickAppActivity.EXTRA_DEVICE, mAdapter.getItem(position));

                startActivityForResult(intent, PICK_ACTIVITY_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_ACTIVITY_CODE && resultCode == RESULT_OK) {
            // Update
            mAdapter.notifyDataSetChanged();
        }
    }
}
