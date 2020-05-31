package com.android.adam.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Adam on 06/08/2017.
 */

public class ImageZoomFragment extends DialogFragment {
    private static final String REQUEST_PATH = "path";
    private ImageView mImageZoom;
    private String mPath;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.picture_zoom_view,null);
        mImageZoom = (ImageView)v.findViewById(R.id.zoom_view);
        mPath = getArguments().getString(REQUEST_PATH);
        Bitmap bm  = BitmapFactory.decodeFile(mPath);
        mImageZoom.setImageBitmap(bm);

        return new AlertDialog.Builder(getActivity())
                .setTitle("Image of Crime")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setView(v)
                .create();

    }

    public static ImageZoomFragment newInstance(String path){
        Bundle args=  new Bundle();
        args.putString(REQUEST_PATH, path);
        ImageZoomFragment im = new ImageZoomFragment();
        im.setArguments(args);
        return im;


    }

}
