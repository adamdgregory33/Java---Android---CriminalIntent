package com.android.adam.criminalintent;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Adam on 08/06/2017.
 */

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int mCrimeNum;
    private Button mEmptyButton;
    private TextView mEmptyTextview;


    private boolean mSubtitleVisible;

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final int REQUEST_CRIMENUM = 1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedStateInstance){


        View empty = inflater.inflate(R.layout.empty_crime,container,false);
        mEmptyTextview = (TextView)empty.findViewById(R.id.empty_textview);
        mEmptyButton = (Button) empty.findViewById(R.id.empty_add_button);
        mEmptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);//start the crime pager to edit the newly made crime


            }
        });
        mCrimeRecyclerView = (RecyclerView) empty.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));




        if (savedStateInstance != null) {
            mSubtitleVisible = savedStateInstance.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();




        //set the recycler view to that using its ID
        return empty;
    }
    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    public void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(CrimeLab.get(getContext()).getCrimes().isEmpty()){
            mCrimeRecyclerView.setVisibility(View.GONE);
            mEmptyButton.setVisibility(View.VISIBLE);
            mEmptyTextview.setVisibility(View.VISIBLE);
        }else{
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mEmptyButton.setVisibility(View.GONE);
            mEmptyTextview.setVisibility(View.GONE);
        }


        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super .onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);//start the crime pager to edit the newly made crime
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
    private void updateSubtitle() {

        CrimeLab cl = CrimeLab.get(getActivity());
        int crimeCount = cl.getCrimes().size();

        int crimeSize = cl.getCrimes().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeSize, crimeSize);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CRIMENUM) {
            mCrimeNum = CrimePagerActivity.getCrimeToUpdate(data);
        }

    }

    private Callbacks mCallbacks;
    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {

        void onCrimeSelected(Crime crime);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity){
            a=(Activity) context;
        }
        mCallbacks = (Callbacks)context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //holder extendds recycler view holder
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private Crime mCrime;

        public CrimeHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_checkbox);
            mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCrime.setSolved(isChecked);
                    CrimeLab.get(getContext()).updateCrime(mCrime);
                }
            });



        }
        public void bindCrime(Crime crime){

            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText((mCrime.getDate().toString()));
            mSolvedCheckBox.setChecked(mCrime.isSolved());


        }
        @Override
        public void onClick(View v){

            mCallbacks.onCrimeSelected(mCrime);

        }


    }
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        //Make an adadpter that extends the recycler view adapter, of type of the holder you make
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);

        }
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);

        }
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }
    }

}
