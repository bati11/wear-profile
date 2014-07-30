package info.bati11.wearprofile.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

public class LoadDialogFragment extends DialogFragment {
    private ProgressDialog progressDialog;
    public static LoadDialogFragment newInstance() {
        LoadDialogFragment fragment = new LoadDialogFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        return progressDialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog = null;
    }
}
