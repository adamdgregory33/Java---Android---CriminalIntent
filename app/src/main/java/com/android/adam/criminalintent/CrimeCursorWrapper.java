package com.android.adam.criminalintent;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.android.adam.criminalintent.database.CrimeDbSchema;
import com.android.adam.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Adam on 03/08/2017.
 */

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Crime getCrime(){
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String time = getString(getColumnIndex(CrimeTable.Cols.TIME));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));


        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setTime(time);
        crime.setSuspect(suspect);

        return crime;
    }




}
