package com.sliverbit.buslocator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by tdeland on 5/17/15.
 */
public class RouteDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    private int mSelectedItem;
    NoticeDialogListener mListener;
    SharedPreferences mPrefs;
    SharedPreferences.Editor mPrefsEditor;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
        mPrefs = activity.getPreferences(Context.MODE_PRIVATE);
        mPrefsEditor = mPrefs.edit();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItem = 0;
        int savedRoute = mPrefs.getInt(getString(R.string.saved_route), 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pick_route)
                .setSingleChoiceItems(R.array.routes, savedRoute, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        mSelectedItem = which;
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mPrefsEditor.putInt(getString(R.string.saved_route), mSelectedItem);
                        mPrefsEditor.commit();

                        mListener.onDialogPositiveClick(RouteDialogFragment.this);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(RouteDialogFragment.this);
                    }
                });

        return builder.create();
    }
}
