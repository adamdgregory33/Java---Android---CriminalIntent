package com.android.adam.criminalintent;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Adam on 06/06/2017.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String CRIME_TIME = "crime_time";//these two are just the tags for it within the fragment manager
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mTimeButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;
    private File mPhotoFile;


    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args= new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);
        //make a bundle, add the crime ID to the bundle

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
        //attach bundle to fragment and return the fragment

    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);



    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        View v  = inflater.inflate(R.layout.fragment_crime, container, false);

        /*1st passing in the layout resource ID. The second parameter is your
        view’s parent, which is usually needed to configure the widgets properly. The third parameter tells the
        layout inflater whether to add the inflated view to the view’s parent. You pass in false because you
        will add the view in the activity’s code.
        */
        mTimeButton = (Button)v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm  = getFragmentManager();
                TimePickerFragment tp = TimePickerFragment.newInstance(mCrime.getTime());

                tp.setTargetFragment(CrimeFragment.this, 1);//request code 1
                tp.show(fm,CRIME_TIME);
                updateCrime();

            }
        });

        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setChooserTitle(getString(R.string.send_report))
                .setType("text/plain").setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .createChooserIntent();


                startActivity(i);
            }
        });

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher(){
           @Override
                   public void beforeTextChanged(CharSequence s, int start, int count, int after){

                    //BLANK
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
                mCrime.setTitle(s.toString());
                /*In onTextChanged(…), you call toString() on the CharSequence that is the user’s input. This method
                returns a string, which you then use to set the Crime’s title.
                */
                updateCrime();
            }
            @Override
            public void afterTextChanged(Editable s){
                //BLANK
            }



        });
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*FragmentManager fm = getFragmentManager();//set up a fragment manager
                DatePickerFragment dm = DatePickerFragment.newInstance(mCrime.getDate());//create the dialog fragment

                dm.setTargetFragment(CrimeFragment.this, REQUEST_DATE);

                dm.show(fm,DIALOG_DATE);//show said fragment*/

                Intent i = DatePickerActivity.newIntent(getContext(), mCrime.getDate());

                startActivityForResult(i,REQUEST_DATE);
                updateCrime();

            }
        });


        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set the crime's solved property
                mCrime.setSolved(isChecked);
                CrimeLab.get(getContext()).updateCrime(mCrime);
                updateCrime();
            }
        });
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //pickContact.addCategory(Intent.CATEGORY_HOME);

        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if(mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = (mPhotoFile != null) && (captureImage.resolveActivity(packageManager) != null);
        mPhotoButton.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".my.package.name.provider", mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mPhotoFile != null && mPhotoFile.exists()) {
                        FragmentManager fm = getFragmentManager();
                        ImageZoomFragment im = ImageZoomFragment.newInstance(mPhotoFile.getPath());
                        im.show(fm, null);
                    }

                }
        });

        return v;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_DATE) {
            mCrime.setDate((Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));
            updateCrime();
            updateDate();
        }else if (requestCode == REQUEST_TIME){
            mCrime.setTime(data.getStringExtra(TimePickerFragment.EXTRA_TIME));
            updateCrime();
            updateTime();

        }else if(requestCode == REQUEST_CONTACT && data!= null){
            Uri contactUri = data.getData();
            //specify which fields you want your query to return values for
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            //perform your query-  the contacturi is like a where clause here

            Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            try{
                //double-check that you get the results
                if(c.getCount() == 0){
                    return;
                }
                //pull out the first column of the first row of data, that is your suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            }finally{
                c.close();
            }

        }else if (requestCode == REQUEST_PHOTO){
            updateCrime();
            updatePhotoView();
        }
    }

    public void updateDate(){

        mDateButton.setText(mCrime.getDate().toString());
    }
    public void updateTime(){
        mTimeButton.setText(mCrime.getTime());

    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_fragment_menu, menu);



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getContext()).deleteCrime(mCrime.getId());
                getActivity().finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause(){
        super .onPause();
        CrimeLab.get(getContext()).updateCrime(mCrime);

    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEEE, MMMM dd, yyyy h:mmaa";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private Callbacks mCallbacks;
    /*INTERFACE REQUIRED BY HOSTING ACTIVITY*/
    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
    }

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

    private void updateCrime(){
        CrimeLab.get(getActivity()).updateCrime(mCrime);//saves crime
        mCallbacks.onCrimeUpdated(mCrime);//updates its view
    }

}
