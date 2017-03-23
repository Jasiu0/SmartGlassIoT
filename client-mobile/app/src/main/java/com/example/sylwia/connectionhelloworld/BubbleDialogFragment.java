package com.example.sylwia.connectionhelloworld;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kingfisherphuoc.quickactiondialog.QuickActionDialogFragment;

/**
 * Created by jurek on 1/21/17.
 */

public class BubbleDialogFragment extends QuickActionDialogFragment {
    private String mTitle;
    private String mDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

       TextView titleTextView = (TextView) view.findViewById(R.id.title);
       TextView dateTextView = (TextView) view.findViewById(R.id.date);

       titleTextView.setText(mTitle);
       dateTextView.setText("Ostatnia zmiana pomiaru: " + mDate);


        return view;
    }

    public void setDialogContent(String title, String date){
        mTitle = title;
        mDate = date;
    }

    @Override
    protected int getArrowImageViewId() {
        return 0;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_bubble_dialog;
    }

}
