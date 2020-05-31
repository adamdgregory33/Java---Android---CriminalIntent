package com.android.adam.criminalintent;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by Adam on 07/06/2017.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity{
    @LayoutRes
    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    protected abstract Fragment createFragment();

}
