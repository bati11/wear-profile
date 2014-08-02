package info.bati11.wearprofile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;

import info.bati11.wearprofile.adapters.CardFragmentGridPagerAdapter;
import info.bati11.wearprofile.fragments.ProfileFragment;
import info.bati11.wearprofile.views.CardGridViewPager;

public class ProfileActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
                   GoogleApiClient.OnConnectionFailedListener,
                   DataApi.DataListener {

    private GoogleApiClient mGoogleApiClient;

    private Context context;

    private ViewGroup layout;
    private CardGridViewPager gridViewPager;
    private ViewGroup dotLayout;

    private ProfileFragment fragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        context = this;

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                layout = (ViewGroup) stub.findViewById(R.id.layout);
                gridViewPager = (CardGridViewPager)layout.findViewById(R.id.pager);
                gridViewPager.setVisibility(View.INVISIBLE);
                gridViewPager.setAdapter(new CardFragmentGridPagerAdapter(getFragmentManager(), "", ""));
                gridViewPager.setOnPageChangeListener(new GridViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, int i2, float v, float v2, int i3, int i4) {
                    }

                    @Override
                    public void onPageSelected(int i, int i2) {
                        for(int j = 0; j < dotLayout.getChildCount(); j++){
                            configDot((Button) dotLayout.getChildAt(j), 4, 4);
                        }
                        if(dotLayout.getChildCount() > i) {
                            configDot((Button) dotLayout.getChildAt(i2), 6, 2);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                    }
                });
                dotLayout = (ViewGroup)stub.findViewById(R.id.dotLayout);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);

        PendingResult<DataItemBuffer> dataItems = Wearable.DataApi.getDataItems(mGoogleApiClient);
        dataItems.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {
                for (int i = 0; i < dataItems.getCount(); i++) {
                    DataItem dataItem = dataItems.get(i);
                    if (dataItem.getUri().getPath().equals("/profile/info")) {
                        DataMap dataMap = DataMap.fromByteArray(dataItem.getData());
                        String name = dataMap.getString("name", "no name");
                        String description = dataMap.getString("description", "");

                        CardFragmentGridPagerAdapter adapter = (CardFragmentGridPagerAdapter) gridViewPager.getAdapter();
                        adapter.changeProfile(name, description);
                        if (adapter.getEnableCardCount() == 2) gridViewPager.setSwipable(true);
                        else                                   gridViewPager.setSwipable(false);

                        dotLayout.removeAllViews();
                        for (int j = 0; j < 2; j++) {
                            Button button = new Button(context);
                            configDot(button, 4, 4);
                            dotLayout.addView(button);
                        }
                        configDot((Button)dotLayout.getChildAt(0), 6, 2);
                        gridViewPager.setVisibility(View.VISIBLE);

                    } else if (dataItem.getUri().getPath().equals("/profile/image")) {
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                        final Asset profileImage = dataMapItem.getDataMap().getAsset("image");
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                final Bitmap bitmap = loadBitmapFromAsset(profileImage);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        layout.setBackground(new BitmapDrawable(bitmap));
                                    }
                                });
                                return null;
                            }
                        }.execute();
                    }
                }
                dataItems.close();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                if (event.getDataItem().getUri().getPath().equals("/profile/info")) {
                    DataMap dataMap = DataMap.fromByteArray(event.getDataItem().getData());
                    final String name = dataMap.getString("name");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (fragment == null) {
                                fragment = ProfileFragment.newInstance(name);
                                getFragmentManager().beginTransaction()
                                        .add(R.id.layout, fragment).commit();
                            } else {
                                fragment.changeContent(name);
                            }
                        }
                    });
                } else if (event.getDataItem().getUri().getPath().equals("/profile/image")) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset profileImage = dataMapItem.getDataMap().getAsset("image");
                    final Bitmap bitmap = loadBitmapFromAsset(profileImage);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.setBackground(new BitmapDrawable(bitmap));
                        }
                    });
                }
            }
        }
    }

    private Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        if (!mGoogleApiClient.isConnected()) {
            return null;
        }
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();

        if (assetInputStream == null) return null;
        else                          return BitmapFactory.decodeStream(assetInputStream);
    }

    private void configDot(Button b, int size, int margin){
        b.setBackground(getResources().getDrawable(R.drawable.circle));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(getPxFromDp(size), getPxFromDp(size));
        p.gravity = Gravity.CENTER;
        p.setMargins(margin, 0, margin, 0);
        b.setLayoutParams(p);
    }

    private int getPxFromDp(int dp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
