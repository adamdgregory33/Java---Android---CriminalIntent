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
import android.widget.TimePicker;



/**
 * Created by Adam on 01/08/2017.
 */

public class TimePickerFragment extends DialogFragment {
    TimePicker mTimePicker;
    public static final String EXTRA_TIME = "com.android.adam.criminalIntent.time";
    public static final String ARGS_TIME = "time";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.crime_time_picker,null);
        mTimePicker = (TimePicker)v.findViewById(R.id.dialog_time_picker);

        mTimePicker.setHour(Integer.parseInt(this.getArguments().getString(ARGS_TIME).substring(0,2)));
        mTimePicker.setMinute(Integer.parseInt(this.getArguments().getString(ARGS_TIME).substring(3)));

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour = mTimePicker.getHour();
                        int minute = mTimePicker.getMinute();
                        String temptime = String.format("%02d",hour)+":"+String.format("%02d",minute);
                        sendResult(Activity.RESULT_OK,temptime);

                    }
                })
                .setView(v)
                .create();
        //the above is all configuring a dialog using an alert dialog builder


    }

    public static TimePickerFragment newInstance(String time){
        Bundle args=  new Bundle();
        args.putString(ARGS_TIME,time);
        TimePickerFragment tm = new TimePickerFragment();
        tm.setArguments(args);
        return tm;
    }

    private void sendResult(int resultCode, String time) {//this method is simply for simplicity sake elsewhere
        if (getTargetFragment() == null) {//checks if there is a target fragment

            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
        //the above is direct communication/knowledge that crime fragment exists
        //it is calling the method within crimefragment to send the data
    }

}
