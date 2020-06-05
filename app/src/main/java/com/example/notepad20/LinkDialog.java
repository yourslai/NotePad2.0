package com.example.notepad20;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;


public class LinkDialog extends DialogFragment {
    public static final String EXTRA_LINK="com.example.notepad20.link";
    private static final String ARGS_LINK="linkArgs";

    private EditText mLinkEditText;
    private String mLink;

    public static LinkDialog newInstance(String link) {

        Bundle args = new Bundle();
        args.putSerializable(ARGS_LINK,link);
        LinkDialog fragment = new LinkDialog();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_link,null);
        mLink=(String) getArguments().getSerializable(ARGS_LINK);

        mLinkEditText=view.findViewById(R.id.edit_link);
        mLinkEditText.setText(mLink);
        mLinkEditText.setTextColor(getResources().getColor(R.color.textColor));
        mLinkEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mLink=charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return  new AlertDialog.Builder(getActivity(),R.style.photoDialog).setView(view).setPositiveButton("保存修改", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult(Activity.RESULT_OK,mLink);
                    }
                }).create();
    }

    private void sendResult(int resultCode, String link){
        if (getTargetFragment()==null){
            return;
        }
        Intent intent=new Intent();
        intent.putExtra(EXTRA_LINK,link);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
