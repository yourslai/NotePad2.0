package com.example.notepad20;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.github.chrisbanes.photoview.PhotoView;

import androidx.fragment.app.DialogFragment;

public class PictureFragment extends DialogFragment {
    private static final String ARG_PICTURE_PATH="picture_path";
    private PhotoView mPhotoView;
    private ScaleInAnimation mScaleInAnimation;
    public static PictureFragment newInstance(String path){
        Bundle args=new Bundle();
        args.putSerializable(ARG_PICTURE_PATH,path);
        PictureFragment fragment=new PictureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart(){
        super.onStart();
        Window window=getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics displayMetrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        WindowManager.LayoutParams params=window.getAttributes();
        params.gravity= Gravity.BOTTOM;
        params.width= ViewGroup.LayoutParams.MATCH_PARENT;
        params.height= ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        String path=(String) getArguments().getSerializable(ARG_PICTURE_PATH);
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo,null);
        mPhotoView=view.findViewById(R.id.look_photo);
        mScaleInAnimation=new ScaleInAnimation();
        int degree= NoteFragment.readPictureDegree(path);
        Matrix matrix=new Matrix();
        Bitmap bitmap=PictureUtils.getScaledBitmap(path,getActivity());
        matrix.setRotate(degree,bitmap.getWidth()/2,bitmap.getHeight()/2);

        Bitmap bitmap1= Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        mPhotoView.setImageBitmap(bitmap1);
        addAnimation(mPhotoView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity(),R.style.photoDialog).setView(view).create();
    }

    private void addAnimation(View v){
        for (Animator anim:mScaleInAnimation.getAnimators(v)){
            anim.setDuration(350).start();
            anim.setInterpolator(new LinearInterpolator());
        }
    }
}
