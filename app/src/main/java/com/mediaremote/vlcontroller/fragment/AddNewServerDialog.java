package com.mediaremote.vlcontroller.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.mediaremote.vlcontroller.R;
import com.mediaremote.vlcontroller.model.VlcServer;

/**
 * Created by nikita on 12/07/15.
 */
public class AddNewServerDialog extends DialogFragment {

    public interface AddNewServerDialogListener {
        void onAddingServer(VlcServer newServer);
    }

    AddNewServerDialogListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_add_new_server, null);
        builder.setView(view);

        builder.setTitle(R.string.addNewServerDialogTitle);

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText name = (EditText) view.findViewById(R.id.nameEditText);
                EditText ip = (EditText) view.findViewById(R.id.ipEditText);
                EditText port = (EditText) view.findViewById(R.id.portEditText);
                EditText password = (EditText) view.findViewById(R.id.passwordEditText);

                listener.onAddingServer(new VlcServer(
                        name.getText().toString(),
                        password.getText().toString(),
                        ip.getText().toString(),
                        Integer.parseInt(port.getText().toString()))
                );
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (AddNewServerDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddNewServerDialogListener");
        }
    }
}
