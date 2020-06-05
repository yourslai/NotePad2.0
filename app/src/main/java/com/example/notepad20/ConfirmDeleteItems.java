package com.example.notepad20;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.themeUtil.ThemeLab;
import com.themeUtil.theme;

import androidx.fragment.app.DialogFragment;


public class ConfirmDeleteItems extends DialogFragment {
    private TextView mTextView;
    private Button mCancelButton;
    private Button mConfirmButton;
    private theme mTheme;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View view= LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_items,null);
        ThemeLab themeLab= ThemeLab.get(getContext());
        mTheme=themeLab.getTheme();
        mTextView=view.findViewById(R.id.delete_items_tip);
        mTextView.setText("真的要删除吗？");
        mTextView.setTextSize(25);

        setCancelable(false);
        mCancelButton=view.findViewById(R.id.delete_items_cancel_button);
        mCancelButton.setText("再想想");
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mConfirmButton=view.findViewById(R.id.delete_items_confirm_button);
        mConfirmButton.setText("就是要删");
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResult(Activity.RESULT_OK);
                dismiss();
            }
        });
        switch (mTheme.getNumber()){
            case 0:
                mTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
                mCancelButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                mConfirmButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case 1:
                mTextView.setTextColor(getResources().getColor(R.color.colorPrimaryOne));
                mCancelButton.setTextColor(getResources().getColor(R.color.colorPrimaryOne));
                mConfirmButton.setTextColor(getResources().getColor(R.color.colorPrimaryOne));
                break;
            case 2:
                mTextView.setTextColor(getResources().getColor(R.color.colorPrimaryTwo));
                mCancelButton.setTextColor(getResources().getColor(R.color.colorPrimaryTwo));
                mConfirmButton.setTextColor(getResources().getColor(R.color.colorPrimaryTwo));
                break;
            case 3:
                mTextView.setTextColor(getResources().getColor(R.color.colorPrimaryThree));
                mCancelButton.setTextColor(getResources().getColor(R.color.colorPrimaryThree));
                mConfirmButton.setTextColor(getResources().getColor(R.color.colorPrimaryThree));
                break;
            case 4:
                mTextView.setTextColor(getResources().getColor(R.color.colorPrimaryFour));
                mCancelButton.setTextColor(getResources().getColor(R.color.colorPrimaryFour));
                mConfirmButton.setTextColor(getResources().getColor(R.color.colorPrimaryFour));
                break;
            case 5:
                mTextView.setTextColor(getResources().getColor(R.color.white));
                mCancelButton.setTextColor(getResources().getColor(R.color.white));
                mConfirmButton.setTextColor(getResources().getColor(R.color.white));
                break;
            case 6:
                mTextView.setTextColor(getResources().getColor(R.color.white));
                mCancelButton.setTextColor(getResources().getColor(R.color.white));
                mConfirmButton.setTextColor(getResources().getColor(R.color.white));
                break;
            case 7:
                mTextView.setTextColor(getResources().getColor(R.color.colorPrimaryFour));
                mCancelButton.setTextColor(getResources().getColor(R.color.colorPrimaryFour));
                mConfirmButton.setTextColor(getResources().getColor(R.color.colorPrimaryFour));
                break;
            case 8:
                mTextView.setTextColor(getResources().getColor(R.color.white));
                mCancelButton.setTextColor(getResources().getColor(R.color.white));
                mConfirmButton.setTextColor(getResources().getColor(R.color.white));
        }
        return new AlertDialog.Builder(getActivity(),R.style.otherDialogs).setView(view).create();
    }



    private void sendResult(int resultCode){
        if (getTargetFragment()==null){
            return;
        }
        Intent intent=new Intent();
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
