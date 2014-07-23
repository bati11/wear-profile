package info.bati11.wearprofile;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import info.bati11.wearprofile.fragments.TwitterDialogFragment;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;


public class ProfileActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        TwitterDialogFragment.PositiveButtonListener {

    private GoogleApiClient mGoogleApiClient;

    private LinearLayout layout;
    private EditText editText;
    private Button twitterButton;
    private TextView nameTextView;
    private TextView descriptionTextView;

    private ImageView imageView;
    private Button syncButton;

    private Bitmap bitmap;

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

        layout = (LinearLayout)findViewById(R.id.layout);
        editText = (EditText)findViewById(R.id.name_edit_text);
        twitterButton = (Button)findViewById(R.id.load_button);
        nameTextView = (TextView)findViewById(R.id.nameTextView);
        descriptionTextView = (TextView)findViewById(R.id.descriptionTextView);
        syncButton = (Button)findViewById(R.id.sync_button);

        final Context context = this;
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new TwitterDialogFragment();
                dialogFragment.show(getFragmentManager(), "twtterButton");
            }
        });

        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/profile/info");
                DataMap dataMap = dataMapRequest.getDataMap();

                dataMap.putString("name", nameTextView.getText().toString());
                dataMap.putString("description", descriptionTextView.getText().toString());

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
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        Log.d("TAG", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("TAG", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("TAG", "onConnectionFailed: " + connectionResult);
    }

    @Override
    public void exec(String name) {
         ProfileImageTask profileImageTask = new ProfileImageTask(this, layout);
         TwitterProfileTask task = new TwitterProfileTask(nameTextView, descriptionTextView, profileImageTask);
         task.execute(name);
    }

    private class TwitterProfileTask extends AsyncTask<String, Integer, User> {

        private TextView nameTextView;
        private TextView descriptionTextView;
        private ProfileImageTask profileImageTask;

        public TwitterProfileTask(TextView nameTextView, TextView descriptionTextView, ProfileImageTask profileImageTask1) {
            this.nameTextView = nameTextView;
            this.descriptionTextView = descriptionTextView;
            this.profileImageTask = profileImageTask1;
        }

        @Override
        protected User doInBackground(String... userNames) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("")
                    .setOAuthConsumerSecret("")
                    .setOAuthAccessToken("")
                    .setOAuthAccessTokenSecret("");
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            User result = null;
            try {
                result = twitter.showUser(userNames[0]);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(User user) {
            nameTextView.setText("@" + user.getScreenName());
            descriptionTextView.setText(user.getDescription());

            profileImageTask.execute(user.getProfileImageURL());
        }
    }

    private class ProfileImageTask extends AsyncTask<String, Integer, Bitmap> {

        private Context context;
        private ViewGroup layout;

        public ProfileImageTask(Context context, ViewGroup layout) {
            this.context = context;
            this.layout = layout;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Bitmap result = null;
            if (url != null) {
                try {
                    InputStream inputStream = url.openStream();
                    result = BitmapFactory.decodeStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(final Bitmap bm) {
            bitmap = bm;

            if (imageView == null) {
                imageView = new ImageView(context);
                imageView.setImageBitmap(bitmap);
                layout.addView(imageView);
            } else {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }
}
