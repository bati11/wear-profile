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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import info.bati11.wearprofile.R;

public class LoadTwitterFragment extends android.support.v4.app.Fragment {

    private ProfileFragmentListener profileFragmentListener;

    private EditText twitterNameEditText;
    private Button twitterLoadButton;
    private EditText twitterDescriptionTextView;

    private ImageButton imageButton;
    private ImageView imageView;
    private Bitmap bitmap;

    private Button syncButton;

    private LoadDialogFragment loadDialogFragment;

    public static LoadTwitterFragment newInstance() {
        LoadTwitterFragment fragment = new LoadTwitterFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.profileFragmentListener = (ProfileFragmentListener)activity;
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
        imageButton = (ImageButton)view.findViewById(R.id.twitter_image_button);
        imageView = (ImageView)view.findViewById(R.id.twitter_profile_image);
        syncButton = (Button)view.findViewById(R.id.twitter_sync_button);

        final Context context = getActivity();
        twitterLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadDialogFragment == null) {
                    loadDialogFragment = LoadDialogFragment.newInstance();
                }
                if (loadDialogFragment.getDialog() == null || !loadDialogFragment.getDialog().isShowing()) {
                    loadDialogFragment.show(getActivity().getFragmentManager(), "loadDialog");
                }
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
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFragmentListener.onClickImage(view);
            }
        });
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFragmentListener.onClickSync(
                        view,
                        twitterNameEditText.getText().toString(),
                        twitterDescriptionTextView.getText().toString(),
                        bitmap);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bitmap bitmap = profileFragmentListener.getProfileImage();
        if (bitmap != null) imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        profileFragmentListener = null;
    }

    private class TwitterProfileTask extends AsyncTask<String, Integer, Map<String, String>> {

        private Context context;
        private ProfileImageTask profileImageTask;

        public TwitterProfileTask(Context context, ProfileImageTask profileImageTask1) {
            this.context = context;
            this.profileImageTask = profileImageTask1;
        }

        @Override
        protected Map<String, String> doInBackground(String... userNames) {
            HttpGet httpGet = new HttpGet("http://bati11-twitter-api.herokuapp.com/wearprofile/users/" + userNames[0]);
            DefaultHttpClient client = new DefaultHttpClient();
            Map<String, String> result = null;
            try {
                HttpResponse response = client.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String res = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = new JSONObject(res);
                    result = new HashMap<String, String>();
                    result.put("screenName", jsonObject.getString("screenName"));
                    result.put("description", jsonObject.getString("description"));
                    result.put("biggerProfileImageURL", jsonObject.getString("biggerProfileImageURL"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Map<String, String> map) {
            if (loadDialogFragment.getDialog() != null) loadDialogFragment.getDialog().dismiss();
            if (map == null) {
                Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
            } else {
                twitterNameEditText.setText("@" + map.get("screenName"));
                twitterDescriptionTextView.setText(map.get("description"));
                profileImageTask.execute(map.get("biggerProfileImageURL"));
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
