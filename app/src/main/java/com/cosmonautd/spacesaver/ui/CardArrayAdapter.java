package com.cosmonautd.spacesaver.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cosmonautd.spacesaver.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardArrayAdapter  extends ArrayAdapter<Card> {
    private static final String TAG = "CardArrayAdapter";
    private List<Card> cardList = new ArrayList<Card>();

    private int[] manyColors = {
            R.color.GreenLogo1,
            R.color.GreenLogo2,
            R.color.GreenLogo3,
            R.color.GreenLogo4,
            R.color.GreenLogo5
    };

    private int getRandomColor() {
        int random = new Random().nextInt(manyColors.length);
        return manyColors[random];
    }

    static class CardViewHolder {
        TextView row1;
        TextView row2;
        TextView row3;
        ImageView funColor;
    }

    public CardArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Card object) {
        cardList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.cardList.size();
    }

    @Override
    public Card getItem(int index) {
        return this.cardList.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CardViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.card_layout, parent, false);
            viewHolder = new CardViewHolder();
            viewHolder.row1 = (TextView) row.findViewById(R.id.row1);
            viewHolder.row2 = (TextView) row.findViewById(R.id.row2);
            viewHolder.row3 = (TextView) row.findViewById(R.id.row3);
            viewHolder.funColor = (ImageView) row.findViewById(R.id.funColor);
            row.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder)row.getTag();
        }
        Card card = getItem(position);
        viewHolder.row1.setText(card.getRow1());
        viewHolder.row2.setText(card.getRow2());
        viewHolder.row3.setText(card.getRow3());
        viewHolder.funColor.setBackgroundColor(ResourcesCompat.getColor(getContext().getResources(), getRandomColor(), null));
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
