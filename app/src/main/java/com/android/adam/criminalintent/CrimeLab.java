package com.android.adam.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.android.adam.criminalintent.database.CrimeBaseHelper;
import com.android.adam.criminalintent.database.CrimeDbSchema;
import com.android.adam.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Adam on 07/06/2017.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if(sCrimeLab == null){

            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

    }


    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;

    }

    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + "= ?",
                new String[] {id.toString()}
        );
        try{
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();

        }finally{
            cursor.close();
        }
    }


    public void addCrime(Crime c){
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME , null, values);
    }

    public void updateCrime(Crime crime){
        String uuidString = crime.getId().toString();
        ContentValues values =  getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + "= ?",new String[] {uuidString});

    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE,crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED,crime.isSolved() ? 1: 0 );
        values.put(CrimeTable.Cols.TIME, crime.getTime());
        values.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());
        return values;

    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // colummns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, //having
                null // orderBy
        );
        return new CrimeCursorWrapper(cursor);

    }

    public void deleteCrime(UUID id){
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[]{id.toString()});

    }

    public File getPhotoFile(Crime crime){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //get a default photo files location for the default place
        if(externalFilesDir ==null){
            return null;
        }

        //return a new file which is located in this place, with the correct name
        return new File(externalFilesDir,crime.getPhotoFileName());
    }



}
