package info.bati11.wearprofile.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import info.bati11.wearprofile.R;

public class TwitterDialogFragment extends DialogFragment {

    public static interface PositiveButtonListener {
        void exec(String name);
    }

    private PositiveButtonListener listener;
    private EditText nameEditText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (PositiveButtonListener)activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater =
                (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_twitter_dialog, null);
        nameEditText = (EditText)view.findViewById(R.id.twitter_name);
        builder.setView(view);
        builder.setPositiveButton("load", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       listener.exec(nameEditText.getText().toString());
                   }
               })
               .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                   }
               });
        return builder.create();
    }

}
