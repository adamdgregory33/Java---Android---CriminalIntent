package com.android.adam.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Adam on 31/07/2017.
 */

public class DatePickerFragment extends DialogFragment {
    public static final int REQUESTCODE_DATE = 1;
    public static final String ARG_DATE = "date";
    private DatePicker mDatePicker;//is a type of view
    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";
    private Button mOKButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker = initDatePicker(mDatePicker);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year,month,day).getTime();

                        sendResult(Activity.RESULT_OK,date);


                    }
                })
                .setView(v)
                .create();
        //the above is all configuring a dialog using an alert dialog builder


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.dialog_date,container,false);
        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker = initDatePicker(mDatePicker);
        mOKButton = (Button) v.findViewById(R.id.ok_button);


        mOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                Date date = new GregorianCalendar(year,month,day).getTime();

                Intent i = new Intent();
                i.putExtra(EXTRA_DATE,date);

                getActivity().setResult(Activity.RESULT_OK, i);
                getActivity().finish();
            }
        });


        return v;
    }


    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, Date date) {//this method is simply for simplicity sake elsewhere
        if (getTargetFragment() == null) {//checks if there is a target fragment

            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
        //the above is direct communication/knowledge that crime fragment exists
        //it is calling the method within crimefragment to send the data
    }
    public DatePicker initDatePicker(DatePicker dm){
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        dm.init(year, month, day, null);
        return dm;
    }
}
