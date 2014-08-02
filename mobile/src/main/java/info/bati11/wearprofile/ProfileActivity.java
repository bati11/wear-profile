package info.bati11.wearprofile;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import info.bati11.wearprofile.adapters.ProfilePagerAdapter;
import info.bati11.wearprofile.fragments.ProfileFragmentListener;


public class ProfileActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ProfileFragmentListener,
        ActionBar.TabListener {

    private boolean initFlag = false;

    private GoogleApiClient mGoogleApiClient;

    private ActionBar actionBar;
    private ViewPager viewPager;

    private Uri profileImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
        ProfilePagerAdapter pagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        for (int i = 0; i < pagerAdapter.getCount(); i++) {
            ActionBar.Tab tab = actionBar.newTab()
                                         .setText(pagerAdapter.getPageTitle(i))
                                         .setTabListener(this);
            actionBar.addTab(tab);
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                actionBar.setSelectedNavigationItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
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
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (initFlag) return;
        initFlag = true;
        PendingResult<DataItemBuffer> dataItems = Wearable.DataApi.getDataItems(mGoogleApiClient);
        dataItems.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {
                for (DataItem dataItem : dataItems) {
                    if (dataItem.getUri().getPath().equals("/profile/info")) {
                        DataMap dataMap = DataMap.fromByteArray(dataItem.getData());
                        String name = dataMap.getString("name", "no name");
                        String description = dataMap.getString("description", "");
                        ProfilePagerAdapter adapter = (ProfilePagerAdapter) viewPager.getAdapter();
                        adapter.setProfile(name, description);
                    } else if (dataItem.getUri().getPath().equals("/profile/image")) {
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                        final Asset profileImage = dataMapItem.getDataMap().getAsset("image");
                        if (profileImage != null) {
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    final Bitmap bitmapFromAsset = loadBitmapFromAsset(profileImage);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ProfilePagerAdapter adapter = (ProfilePagerAdapter) viewPager.getAdapter();
                                            adapter.setProfileImage(bitmapFromAsset);
                                        }
                                    });
                                    return null;
                                }
                            }.execute();
                        }
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

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        } else {
            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                profileImageUri = data.getData();
            }
        }
    }

    @Override
    public void onClickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select image"), 100);
    }

    @Override
    public void onClickSync(View view, String name, String description, final Bitmap bitmap) {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (name == null || name.equals("")) {
            Toast.makeText(this, "Please input name.", Toast.LENGTH_LONG).show();
        } else {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/profile/info");
            DataMap dataMap = dataMapRequest.getDataMap();

            dataMap.putString("name", name);
            dataMap.putString("description", description);

            PutDataRequest request = dataMapRequest.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
            pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {

                    PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/profile/image");
                    DataMap dataMap = dataMapRequest.getDataMap();

                    Asset asset = createAssetFromBitmap(bitmap);
                    dataMap.putAsset("image", asset);

                    PutDataRequest request = dataMapRequest.asPutDataRequest();
                    PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
                    pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                        }
                    });
                }
            });
        }
    }

    @Override
    public Bitmap getProfileImage() {
        if (profileImageUri == null) return null;

        ContentResolver cr = getContentResolver();
        InputStream in = null;
        Bitmap result = null;
        try {
            in = cr.openInputStream(profileImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > 200) {
                double resizedHeight = 200 * ((double)height / width);
                result = Bitmap.createScaledBitmap(bitmap, 200, (int)resizedHeight, false);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
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
}
