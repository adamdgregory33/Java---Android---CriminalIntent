package com.android.adam.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

/**
 * Created by Adam on 02/08/2017.
 */

public class DatePickerActivity extends SingleFragmentActivity {
    public static final String DATE_EXTRA = "date";

    @Override
    protected Fragment createFragment() {
        return DatePickerFragment.newInstance((Date) getIntent().getSerializableExtra(DATE_EXTRA));
      //  return new DatePickerFragment();
    }

    public static Intent newIntent(Context context, Date date){
        Intent i = new Intent(context, DatePickerActivity.class);
        i.putExtra(DATE_EXTRA,date);
        return i;

    }


}
