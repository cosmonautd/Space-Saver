package com.cosmonautd.spacesaver.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.cosmonautd.spacesaver.R;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    private TimePicker timePicker;

    private int selectedHour;
    private int selectedMinute;

    public int getSelectedHour() { return selectedHour; }
    public int getSelectedMinute() { return selectedMinute; }

    public interface TimePickerListener {
        void onTimePositiveClick(DialogFragment dialog);
        void onTimeNegativeClick(DialogFragment dialog);
    }

    TimePickerFragment.TimePickerListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.time_picker_fragment_layout, null);

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        timePicker = (TimePicker) view.findViewById(R.id.timePicker);

        if(Build.VERSION.SDK_INT >= 23 ) {

            timePicker.setHour(hour);
            timePicker.setMinute(minute);

        } else {

            // API VERSION DOES NOT SUPPORT setHour() and setMinute() methods
        }

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(Build.VERSION.SDK_INT >= 23 ) {

                            selectedHour = timePicker.getHour();
                            selectedMinute = timePicker.getMinute();

                        } else {

                            selectedHour = timePicker.getCurrentHour();
                            selectedMinute = timePicker.getCurrentMinute();
                        }

                        listener.onTimePositiveClick(TimePickerFragment.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        listener.onTimeNegativeClick(TimePickerFragment.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        try {

            listener = (TimePickerFragment.TimePickerListener) context;

        } catch (ClassCastException E) {

            throw new ClassCastException(context.toString() + " must implement TimePickerListener");
        }
    }
}