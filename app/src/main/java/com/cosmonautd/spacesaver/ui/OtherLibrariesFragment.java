package com.cosmonautd.spacesaver.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cosmonautd.spacesaver.R;

import java.util.regex.Pattern;

public class OtherLibrariesFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.other_libraries_fragment_layout, null);

        TextView androidDirectoryChooser = (TextView) view.findViewById(R.id.AndroidDirectoryChooserLink);
        Linkify.addLinks(androidDirectoryChooser, Linkify.ALL);

        TextView seekArc = (TextView) view.findViewById(R.id.SeekArcLink);
        Linkify.addLinks(seekArc, Linkify.ALL);

        TextView circleProgressView = (TextView) view.findViewById(R.id.CircleProgressViewLink);
        Linkify.addLinks(circleProgressView, Linkify.ALL);

        builder.setView(view);
        return builder.create();
    }
}

