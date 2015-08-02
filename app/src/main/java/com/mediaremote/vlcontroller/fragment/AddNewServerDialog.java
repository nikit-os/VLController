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
import android.widget.Button;
import android.widget.EditText;

import com.mediaremote.vlcontroller.R;
import com.mediaremote.vlcontroller.model.VlcServer;

import java.util.regex.Pattern;

/**
 * Created by nikita on 12/07/15.
 */
public class AddNewServerDialog extends DialogFragment {
    public static final String TAG = AddNewServerDialog.class.toString();
    public static final String IP_AND_PORT = "ipAndPort";

    private static final String IPADDRESS_PATTERN =
                    "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    static AddNewServerDialog newInstance(String ipAndPort) {
        AddNewServerDialog dialog = new AddNewServerDialog();
        Bundle args = new Bundle();
        args.putString(IP_AND_PORT, ipAndPort);
        dialog.setArguments(args);
        return dialog;
    }

    public interface AddNewServerDialogListener {
        void onAddingServer(VlcServer newServer);
    }

    AddNewServerDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_add_new_server, null);

        final EditText name = (EditText) view.findViewById(R.id.nameEditText);
        final EditText ip = (EditText) view.findViewById(R.id.ipEditText);
        final EditText port = (EditText) view.findViewById(R.id.portEditText);
        final EditText password = (EditText) view.findViewById(R.id.passwordEditText);

        if (getArguments() != null) {
            String ipAndPort = getArguments().getString(IP_AND_PORT);
            String serverIp = ipAndPort.split(":")[0];
            String serverPort = ipAndPort.split(":")[1];

            ip.setText(serverIp);
            port.setText(serverPort);
        }

        final AlertDialog createdDialog = new AlertDialog.Builder(getActivity())
            .setView(view)
            .setTitle(R.string.addNewServerDialogTitle)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            }).create();

        createdDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
                Button b = createdDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!pattern.matcher(ip.getText().toString()).matches()) {
                            ip.setError("Invalid ip address");
                            return;
                        }

                        listener.onAddingServer(new VlcServer(
                                        name.getText().toString(),
                                        password.getText().toString(),
                                        ip.getText().toString(),
                                        Integer.parseInt(port.getText().toString()))
                        );

                        createdDialog.dismiss();
                    }
                });
            }
        });

        return createdDialog;
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
