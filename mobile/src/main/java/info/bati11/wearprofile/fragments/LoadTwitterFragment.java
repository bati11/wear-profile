package info.bati11.wearprofile.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import info.bati11.wearprofile.R;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class LoadTwitterFragment extends android.support.v4.app.Fragment {

    private SyncButtonListener syncButtonListener;

    private EditText twitterNameEditText;
    private Button twitterLoadButton;
    private EditText twitterDescriptionTextView;

    private ImageView imageView;
    private Bitmap bitmap;

    private Button syncButton;

    public static LoadTwitterFragment newInstance() {
        LoadTwitterFragment fragment = new LoadTwitterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_load_twitter, container, false);
        twitterNameEditText = (EditText)view.findViewById(R.id.twitter_name_edit_text);
        twitterLoadButton = (Button)view.findViewById(R.id.twitter_load_button);
        twitterDescriptionTextView = (EditText)view.findViewById(R.id.twitter_description_edit_text);
        imageView = (ImageView)view.findViewById(R.id.twitter_profile_image);
        syncButton = (Button)view.findViewById(R.id.twitter_sync_button);

        final Context context = getActivity();
        twitterLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                final String name;
                if (twitterNameEditText.getText().toString().startsWith("@")) {
                    name = twitterNameEditText.getText().toString().substring(1);
                } else {
                    name = twitterNameEditText.getText().toString();
                }
                TwitterProfileTask task = new TwitterProfileTask(context, new ProfileImageTask());
                task.execute(name);
            }
        });
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                syncButtonListener.exec(twitterNameEditText.getText().toString(),
                        twitterDescriptionTextView.getText().toString(), bitmap);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.syncButtonListener = (SyncButtonListener)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        syncButtonListener = null;
    }

    private class TwitterProfileTask extends AsyncTask<String, Integer, User> {

        private Context context;
        private ProfileImageTask profileImageTask;

        public TwitterProfileTask(Context context, ProfileImageTask profileImageTask1) {
            this.context = context;
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
            if (user == null) {
                Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
            } else {
                twitterNameEditText.setText("@" + user.getScreenName());
                twitterDescriptionTextView.setText(user.getDescription());
                profileImageTask.execute(user.getBiggerProfileImageURL());
            }
        }
    }

    private class ProfileImageTask extends AsyncTask<String, Integer, Bitmap> {

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
            imageView.setImageBitmap(bitmap);
        }
    }
}
