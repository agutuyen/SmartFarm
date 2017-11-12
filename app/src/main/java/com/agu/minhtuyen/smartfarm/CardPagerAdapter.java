package com.agu.minhtuyen.smartfarm;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ToggleButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    Context context;
    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;

    public CardPagerAdapter(Context context) {
        this.context = context;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public List<CardItem> getmData() {
        return mData;
    }

    public void setmData(List<CardItem> mData) {
        this.mData = mData;
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter, container, false);
        container.addView(view);
        bind(mData.get(position), view, position);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }
        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(CardItem item, View view, final int pos) {
        final ToggleButton tglOn = (ToggleButton) view.findViewById(R.id.tglOn);
        final ToggleButton tglAuto = (ToggleButton) view.findViewById(R.id.tglAuto);
        final ImageView bookmark = (ImageView) view.findViewById(R.id.imgView);
        tglAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                if (b) {
                    tglOn.setVisibility(View.GONE);
                    bookmark.setImageResource(R.drawable.ic_bookmark);
                } else {
                    tglOn.setVisibility(View.VISIBLE);
                    bookmark.setImageResource(R.drawable.ic_bookmark2);
                }
                FirebaseDatabase.getInstance().getReference("users").child("regions").child(pos + "")
                        .child("control").child("auto").setValue(b);
            }
        });
        tglOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {

                FirebaseDatabase.getInstance().getReference("users").child("regions").child(pos + "")
                        .child("control").child("pump").setValue(b);
            }
        });
        TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        titleTextView.setText(item.getTitle());
        contentTextView.setText(".: Trạng thái: " + item.getState() + "\n.: Tuổi: " + item.getAge());
        tglAuto.setChecked(item.getControl().get("auto"));
        tglOn.setChecked(item.getControl().get("pump"));
    }

}
