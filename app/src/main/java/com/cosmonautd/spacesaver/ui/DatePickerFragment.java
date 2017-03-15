package com.cosmonautd.spacesaver.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.cosmonautd.spacesaver.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private DatePicker datePicker;

    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;

    public int getSelectedDay() { return selectedDay; }
    public int getSelectedMonth() { return selectedMonth; }
    public int getSelectedYear() { return selectedYear; }

    public interface DatePickerListener {
        void onDatePositiveClick(DialogFragment dialog);
        void onDateNegativeClick(DialogFragment dialog);
    }

    DatePickerFragment.DatePickerListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.date_picker_fragment_layout, null);

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePicker.updateDate(year, month, day);
        datePicker.setMinDate(calendar.getTimeInMillis());

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectedDay = datePicker.getDayOfMonth();
                        selectedMonth = datePicker.getMonth();
                        selectedYear = datePicker.getYear();
                        listener.onDatePositiveClick(DatePickerFragment.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        listener.onDateNegativeClick(DatePickerFragment.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        try {

            listener = (DatePickerFragment.DatePickerListener) context;

        } catch (ClassCastException E) {

            throw new ClassCastException(context.toString() + " must implement DatePickerListener");
        }
    }
}