package com.cosmonautd.spacesaver.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cosmonautd.spacesaver.R;
import com.triggertrap.seekarc.SeekArc;

public class PeriodPickerFragment extends DialogFragment {

    private int period = -1;
    private SeekArc seekArc;
    private TextView seekArcProgress;

    int option_index = 4;

    private static final String[] option = {"1 Hour", "2 Hours", "6 Hours", "12 Hours",
                                            "1 Day", "2 Days", "5 Days",
                                            "1 Week", "2 Weeks", "3 Weeks"};

    private final static Integer[] option_ms = { 1000*60*60, 1000*60*60*2, 1000*60*60*6, 1000*60*60*12,
                                                    1000*60*60*24, 1000*60*60*24*2, 1000*60*60*24*5,
                                                    1000*60*60*24*7, 1000*60*60*24*14, 1000*60*60*24*21};

    public int getPeriod() { return period;}
    public static String[] getOption() { return option;}
    public static Integer[] getOptionMs() {return option_ms;}

    public interface PeriodPickerListener {
        void onPeriodPositiveClick(DialogFragment dialog);
        void onPeriodNegativeClick(DialogFragment dialog);
    }

    PeriodPickerListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.period_picker_fragment_layout, null);

        seekArc = (SeekArc) view.findViewById(R.id.seekArc);
        seekArcProgress = (TextView) view.findViewById(R.id.seekArcProgress);

        seekArc.setMax(99);
        seekArc.setProgress(option_index*option.length + 100/(2*option.length));
        seekArcProgress.setText(option[option_index]);

        seekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {

                option_index = progress/option.length;
                seekArcProgress.setText(option[option_index]);
            }
        });

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        period = option_ms[option_index];
                        listener.onPeriodPositiveClick(PeriodPickerFragment.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        listener.onPeriodNegativeClick(PeriodPickerFragment.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        try {

            listener = (PeriodPickerListener) context;

        } catch (ClassCastException E) {

            throw new ClassCastException(context.toString() + " must implement PeriodPickerListener");
        }
    }
}
