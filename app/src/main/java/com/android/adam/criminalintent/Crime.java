package com.android.adam.criminalintent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Adam on 05/06/2017.
 */

public class Crime {
    private UUID mId;
    private String mTitle;

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    private String mSuspect;

    private Date mDate;
    private boolean mSolved;

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
        updateDate();
    }

    private String mTime;

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }



    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public UUID getId() {
        return mId;
    }

    public Crime(UUID id){
        //generate unique identifier
        mId = id;
        mDate = new Date();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        mTime = sdf.format(cal.getTime());

    }

    public Crime(){
        this(UUID.randomUUID());
    }

    public void updateDate(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(mTime.substring(0,2)));
        cal.set(Calendar.MINUTE, Integer.parseInt(mTime.substring(3)));
        mDate = cal.getTime();


    }

    public String getPhotoFileName(){
        return "IMG_"+getId().toString()+".jpg";
    }




}


