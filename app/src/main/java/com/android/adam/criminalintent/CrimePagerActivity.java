package com.android.adam.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.ViewGroup;

import java.util.List;
import java.util.UUID;

/**
 * Created by Adam on 31/07/2017.
 */

public class CrimePagerActivity extends AppCompatActivity implements CrimeFragment.Callbacks{
    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";
    private static final String EXTRA_CRIME_NUM = "crimenum";
    private ViewPager mViewPager;
    private int mCurrentCrime;
    private List<Crime> mCrimes;

    @Override
    public void onCrimeUpdated(Crime crime) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID)this.getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

        mCrimes = CrimeLab.get(this).getCrimes();
        //gets the list of crimes being stored in memory
        mCurrentCrime = getCrimeNum(crimeId);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);

                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        mViewPager.setCurrentItem(mCurrentCrime);



    }

    public int getCrimeNum(UUID crimeId){
        for(int i = 0 ; i < mCrimes.size(); i++){
            if(mCrimes.get(i).getId().equals(crimeId)){
                return i;
            }

        }
        return 0;
    }



    public static Intent newIntent(Context packageContext, UUID id){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,id);
        return intent;

    }

    public static int getCrimeToUpdate(Intent i){
        return i.getIntExtra(EXTRA_CRIME_NUM,0);
    }

    public void sendToUpdate(){
        Intent i  = new Intent();
        i.putExtra(EXTRA_CRIME_NUM,mCurrentCrime);
        setResult(RESULT_OK,i);
    }

}
