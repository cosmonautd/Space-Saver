package com.cosmonautd.spacesaver.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cosmonautd.spacesaver.R;

public class ListManagerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] labels;
    private final String[] data;

    public ListManagerAdapter(Context context, String[] labels, String[] data) {
        super(context, R.layout.list_manager_row, labels);
        this.context = context;
        this.labels = labels;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_manager_row, parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.details_icon);
        TextView detailsLabel = (TextView) rowView.findViewById(R.id.details_label);
        TextView detailsData = (TextView) rowView.findViewById(R.id.details_data);
        detailsLabel.setText(labels[position]);
        detailsData.setText(data[position]);

        switch (position) {

            case 0: {
                imageView.setImageResource(R.drawable.ic_date_range_black_24dp);
                break;
            }

            case 1: {
                imageView.setImageResource(R.drawable.ic_alarm_black_24dp);
                break;
            }

            case 2: {
                imageView.setImageResource(R.drawable.ic_hourglass_empty_black_24dp);
                break;
            }

            case 3: {
                imageView.setImageResource(R.drawable.ic_rotate_right_black_24dp);
                break;
            }
        }

        return rowView;
    }
}
