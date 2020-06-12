package com.example.notepad20;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.themeUtil.ThemeLab;
import com.themeUtil.theme;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class NoteFragment extends Fragment {
    private static final String ARG_CRIME_ID="crime_id";
    private static final String DIALOG_DATE="DialogDate";
    private static final String DIALOG_TIME="DialogTime";
    private static final String DIALOG_DELETE="DialogDelete";
    private static final String DIALOG_PHOTO="DialogPhoto";
    private static final String DIALOG_TEXT="DialogText";
    private static final String DIALOG_LINK="DialogLink";
    private static final int REQUEST_DATE=0;
    private static final int REQUEST_TIME=1;
    private static final int REQUEST_FRAGMENT_DELETE=2;
    private static final int REQUEST_PHOTO=3;
    private static final int REQUEST_SELECT_PHOTO=4;
    private static final int REQUEST_SAVE_TEXT=5;
    private static final int REQUEST_SAVE_LINK=6;

    private Note mNote;
    private File mPhotoFile;
    private EditText mTitleField;
    private EditText mDetailField;
    private TextView mTitleText;
    private TextView mDetailText;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private RoundedImageView mPhotoView;
    private ImageButton mPhotoButton;
    private ImageButton mSelectPhotoButton;
    private String mPicturePath;
    private ImageButton mViewTextButton;
    private ControlClickSpanTextView mLinkView;
    private ScaleInAnimation mScaleInAnimation;
    private theme mTheme;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID crimeId=(UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mNote = NoteLab.get(getActivity()).getNote(crimeId);
        mPhotoFile= NoteLab.get(getActivity()).getPhotoFile(mNote);
        mScaleInAnimation=new ScaleInAnimation();
        ThemeLab themeLab= ThemeLab.get(getContext());
        mTheme=themeLab.getTheme();
        setHasOptionsMenu(true);
    }
    @Override
    public void onPause(){
        super.onPause();
        NoteLab.get(getActivity()).updateNote(mNote);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.fragment_note,container,false);

        mTitleText=v.findViewById(R.id.titleTextView);
        mDetailText=v.findViewById(R.id.detailTextView);
        mTitleField=v.findViewById(R.id.note_title);
        mTitleField.setText(mNote.getTitle());

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mViewTextButton=v.findViewById(R.id.viewText);
        mViewTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager=getFragmentManager();
                DialogContextFragment dialog=DialogContextFragment.newInstance(mNote.getDetail());
                dialog.setTargetFragment(NoteFragment.this,REQUEST_SAVE_TEXT);
                dialog.show(manager,DIALOG_TEXT);

            }
        });

        mDetailField=v.findViewById(R.id.note_detail);
        mDetailField.setText(mNote.getDetail());

        mDetailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mNote.setDetail(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mSolvedCheckBox=v.findViewById(R.id.note_liked);
        mSolvedCheckBox.setChecked(mNote.isLiked());

        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mNote.setLiked(isChecked);
                getActivity().invalidateOptionsMenu();
            }
        });


        mDateButton=v.findViewById(R.id.note_date);

        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDateButton.setEnabled(false);
                final FragmentManager dateFragmentManager=getFragmentManager();
                final DatePickerFragment dialog=DatePickerFragment.newInstance(mNote.getDate());
                dialog.setTargetFragment(NoteFragment.this,REQUEST_DATE);
                int cx=mDateButton.getWidth()/2;
                int cy=mDateButton.getHeight()/2;
                float radius=mDateButton.getWidth();
                Animator animator= ViewAnimationUtils.createCircularReveal(mDateButton,cx,cy,0,radius);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!mNote.isLiked()){
                            dialog.show(dateFragmentManager,DIALOG_DATE);
                        }
                        mDateButton.setEnabled(true);

                    }
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (mNote.isLiked()){
                            Toast toast= Toast.makeText(getActivity(),null, Toast.LENGTH_SHORT);
                            toast.setText("重要的时间可不要轻易更改");
                            toast.show();
                        }
                    }
                });
                animator.setDuration(350);
                animator.start();

            }
        });



        mTimeButton=v.findViewById(R.id.note_time);

        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeButton.setEnabled(false);
                final FragmentManager fragmentManager=getFragmentManager();
                final TimePickerFragment dialog=TimePickerFragment.newInstance(mNote.getDate());
                int cx=mTimeButton.getWidth()/2;
                int cy=mTimeButton.getHeight()/2;
                float radius=mTimeButton.getWidth();
                dialog.setTargetFragment(NoteFragment.this,REQUEST_TIME);
                Animator animator= ViewAnimationUtils.createCircularReveal(mTimeButton,cx,cy,0,radius);
                animator.addListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!mNote.isLiked()){
                            dialog.show(fragmentManager,DIALOG_TIME);
                        }

                        mTimeButton.setEnabled(true);
                    }
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (mNote.isLiked()){
                            Toast toast= Toast.makeText(getActivity(),null, Toast.LENGTH_SHORT);
                            toast.setText("重要的时间可不要轻易更改");
                            toast.show();
                        }
                    }
                });
                animator.setDuration(350);
                animator.start();
            }
        });


        PackageManager packageManager=getActivity().getPackageManager();

        mPhotoButton=v.findViewById(R.id.notepad_camera);
        final Intent captureImage=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto=(mPhotoFile!=null&&captureImage.resolveActivity(packageManager)!=null);
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNote.isLiked()){
                    Toast toast= Toast.makeText(getActivity(),null, Toast.LENGTH_SHORT);
                    toast.setText("既然收藏了就不要修改了");
                    toast.show();
                }else {
                    Uri uri= FileProvider.getUriForFile(getActivity(),
                            "com.example.notepad20.fileprovider",mPhotoFile);
                    captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);

                    List<ResolveInfo> cameraActivities=getActivity().getPackageManager().queryIntentActivities(
                            captureImage, PackageManager.MATCH_DEFAULT_ONLY
                    );

                    for (ResolveInfo activity:cameraActivities){
                        getActivity().grantUriPermission(activity.activityInfo.packageName,uri
                                , Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }

                    startActivityForResult(captureImage,REQUEST_PHOTO);
                }

            }
        });
        mSelectPhotoButton=v.findViewById(R.id.select_photo);
        mSelectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNote.isLiked()){
                    Toast toast= Toast.makeText(getActivity(),null, Toast.LENGTH_SHORT);
                    toast.setText("既然收藏了就不要修改了");
                    toast.show();
                }else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_SELECT_PHOTO);
                }

            }
        });
        mPhotoView= v.findViewById(R.id.note_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoView!=null&&mPhotoFile.exists()){
                    FragmentManager manager=getFragmentManager();
                    PictureFragment dialog=PictureFragment.newInstance(mPhotoFile.getPath());
                    dialog.show(manager,DIALOG_PHOTO);
                }else if (mPhotoView!=null&& mNote.getPicturePath()!=null&&fileIsExists(mNote.getPicturePath())){
                    FragmentManager manager=getFragmentManager();
                    PictureFragment dialog=PictureFragment.newInstance(mNote.getPicturePath());
                    dialog.show(manager,DIALOG_PHOTO);
                }else {
                    Toast toast= Toast.makeText(getActivity(),null, Toast.LENGTH_SHORT);
                    toast.setText("图片怎么都找不到呢!");
                    toast.show();
                }
            }
        });
        mLinkView=v.findViewById(R.id.link);
        if (mNote.getLink()==""){
            mLinkView.setText("长按保存链接，视频还是音乐?");
        }else {
            mLinkView.setText(mNote.getLink());
        }



        mLinkView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                FragmentManager manager=getFragmentManager();
                LinkDialog dialog=LinkDialog.newInstance(mLinkView.getText().toString());
                dialog.setTargetFragment(NoteFragment.this,REQUEST_SAVE_LINK);
                dialog.show(manager,DIALOG_LINK);
                return true;
            }
        });

        if (mPhotoFile.exists()){
            updatePhotoView();
        }else {
            updatePhotoView(mNote.getPicturePath());
        }

        switch (mTheme.getNumber()){
            case 0:
                v.setBackground(getResources().getDrawable(R.drawable.background));
                mTitleText.setTextColor(getResources().getColor(R.color.white));
                mDetailText.setTextColor(getResources().getColor(R.color.white));
                mTitleField.setTextColor(getResources().getColor(R.color.white));
                mTimeButton.setBackgroundColor(getResources().getColor(R.color.buttonColorDefault));
                mTimeButton.setTextColor(getResources().getColor(R.color.textColorDefault));
                mDetailField.setTextColor(getResources().getColor(R.color.white));
                mSolvedCheckBox.setTextColor(getResources().getColor(R.color.textColorDefault));
                mDateButton.setBackgroundColor(getResources().getColor(R.color.buttonColorDefault));
                mDateButton.setTextColor(getResources().getColor(R.color.textColorDefault));
                break;
            case 1:
                v.setBackground(getResources().getDrawable(R.drawable.background1));
                mTitleText.setTextColor(getResources().getColor(R.color.white));
                mDetailText.setTextColor(getResources().getColor(R.color.white));
                mLinkView.setAlpha(1);
                mTitleField.setTextColor(getResources().getColor(R.color.white));
                mTimeButton.setBackgroundColor(getResources().getColor(R.color.buttonColorOne));
                mTimeButton.setTextColor(getResources().getColor(R.color.textColorDefault));
                mDetailField.setTextColor(getResources().getColor(R.color.white));
                mSolvedCheckBox.setAlpha(1);
                mSolvedCheckBox.setTextColor(getResources().getColor(R.color.white));
                mDateButton.setBackgroundColor(getResources().getColor(R.color.buttonColorOne));
                mDateButton.setTextColor(getResources().getColor(R.color.textColorDefault));
                break;
            case 2:
                v.setBackground(getResources().getDrawable(R.drawable.background2));
                mTitleText.setTextColor(getResources().getColor(R.color.white));
                mDetailText.setTextColor(getResources().getColor(R.color.white));
                mLinkView.setAlpha(1);
                mTitleField.setTextColor(getResources().getColor(R.color.white));
                mTimeButton.setBackgroundColor(getResources().getColor(R.color.buttonColorOne));
                mTimeButton.setTextColor(getResources().getColor(R.color.textColorDefault));
                mDetailField.setTextColor(getResources().getColor(R.color.white));
                mSolvedCheckBox.setAlpha(1);
                mSolvedCheckBox.setTextColor(getResources().getColor(R.color.white));
                mDateButton.setBackgroundColor(getResources().getColor(R.color.buttonColorOne));
                mDateButton.setTextColor(getResources().getColor(R.color.textColorDefault));
                break;
            case 3:
                v.setBackground(getResources().getDrawable(R.drawable.background3));
                mTitleText.setTextColor(getResources().getColor(R.color.textColorThree));
                mDetailText.setTextColor(getResources().getColor(R.color.textColorThree));
                mLinkView.setAlpha(1);
                mTitleField.setTextColor(getResources().getColor(R.color.textColorThree));
                mTimeButton.setBackgroundColor(getResources().getColor(R.color.buttonColorThree));
                mTimeButton.setTextColor(getResources().getColor(R.color.textColorThree));
                mDetailField.setTextColor(getResources().getColor(R.color.textColorThree));
                mSolvedCheckBox.setTextColor(getResources().getColor(R.color.textColorThree));
                mSolvedCheckBox.setAlpha(1);
                mDateButton.setBackgroundColor(getResources().getColor(R.color.buttonColorThree));
                mDateButton.setTextColor(getResources().getColor(R.color.textColorThree));
                break;
            case 4:
                v.setBackground(getResources().getDrawable(R.drawable.background4));
                mTitleText.setTextColor(getResources().getColor(R.color.white));
                mDetailText.setTextColor(getResources().getColor(R.color.white));
                mLinkView.setAlpha(1);
                mTitleField.setTextColor(getResources().getColor(R.color.white));
                mTimeButton.setBackgroundColor(getResources().getColor(R.color.buttonColorOne));
                mTimeButton.setTextColor(getResources().getColor(R.color.textColorDefault));
                mDetailField.setTextColor(getResources().getColor(R.color.white));
                mSolvedCheckBox.setAlpha(1);
                mSolvedCheckBox.setTextColor(getResources().getColor(R.color.textColorFour));
                mDateButton.setBackgroundColor(getResources().getColor(R.color.buttonColorOne));
                mDateButton.setTextColor(getResources().getColor(R.color.textColorDefault));
                break;
            case 5:
                v.setBackground(getResources().getDrawable(R.drawable.background5));
                mTitleText.setTextColor(getResources().getColor(R.color.white));
                mDetailText.setTextColor(getResources().getColor(R.color.white));
                mLinkView.setAlpha(1);
                mTitleField.setTextColor(getResources().getColor(R.color.white));
                mTimeButton.setBackgroundColor(getResources().getColor(R.color.buttonColorOne));
                mTimeButton.setTextColor(getResources().getColor(R.color.buttonTextColorFive));
                mDetailField.setTextColor(getResources().getColor(R.color.white));
                mSolvedCheckBox.setAlpha(1);
                mSolvedCheckBox.setTextColor(getResources().getColor(R.color.white));
                mDateButton.setBackgroundColor(getResources().getColor(R.color.buttonColorOne));
                mDateButton.setTextColor(getResources().getColor(R.color.buttonTextColorFive));
                break;
            case 6:
                v.setBackground(getResources().getDrawable(R.drawable.background6));
                mTitleText.setTextColor(getResources().getColor(R.color.textColorSix));
                mDetailText.setTextColor(getResources().getColor(R.color.textColorSix));
                mLinkView.setAlpha(1);
                mTitleField.setTextColor(getResources().getColor(R.color.textColorSix));
                mTimeButton.setBackgroundColor(getResources().getColor(R.color.buttonColorSix));
                mTimeButton.setTextColor(getResources().getColor(R.color.buttonTextColorSix));
                mDetailField.setTextColor(getResources().getColor(R.color.textColorSix));
                mSolvedCheckBox.setAlpha(1);
                mSolvedCheckBox.setTextColor(getResources().getColor(R.color.textColorSix));
                mDateButton.setBackgroundColor(getResources().getColor(R.color.buttonColorSix));
                mDateButton.setTextColor(getResources().getColor(R.color.buttonTextColorSix));
                break;
            case 7:
                v.setBackground(getResources().getDrawable(R.drawable.background7));
                mTitleText.setTextColor(getResources().getColor(R.color.gray));
                mDetailText.setTextColor(getResources().getColor(R.color.gray));
                mLinkView.setAlpha(1);
                mTitleField.setTextColor(getResources().getColor(R.color.gray));
                mTimeButton.setBackgroundColor(getResources().getColor(R.color.buttonColorOne));
                mTimeButton.setTextColor(getResources().getColor(R.color.buttonTextColorSix));
                mDetailField.setTextColor(getResources().getColor(R.color.gray));
                mSolvedCheckBox.setAlpha(1);
                mSolvedCheckBox.setTextColor(getResources().getColor(R.color.textColorFour));
                mDateButton.setBackgroundColor(getResources().getColor(R.color.buttonColorOne));
                mDateButton.setTextColor(getResources().getColor(R.color.buttonTextColorSix));
                break;
            case 8:
                v.setBackground(getResources().getDrawable(R.drawable.background8));
                mTitleText.setTextColor(getResources().getColor(R.color.gray));
                mDetailText.setTextColor(getResources().getColor(R.color.gray));
                mLinkView.setAlpha(1);
                mTitleField.setTextColor(getResources().getColor(R.color.gray));
                mTimeButton.setBackgroundColor(getResources().getColor(R.color.buttonColorSix));
                mTimeButton.setTextColor(getResources().getColor(R.color.buttonTextColorSix));
                mDetailField.setTextColor(getResources().getColor(R.color.gray));
                mSolvedCheckBox.setAlpha(1);
                mSolvedCheckBox.setTextColor(getResources().getColor(R.color.textColorFour));
                mDateButton.setBackgroundColor(getResources().getColor(R.color.buttonColorSix));
                mDateButton.setTextColor(getResources().getColor(R.color.buttonTextColorSix));
                break;
        }


        return v;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent,REQUEST_SELECT_PHOTO);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode!= Activity.RESULT_OK){
            return;
        }
        if (requestCode==REQUEST_DATE){
            Date date=(Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mNote.setDate(date);
            updateDate();
        }
        if (requestCode==REQUEST_TIME){
            Date date=(Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mNote.setDate(date);
            updateDate();
            updateTime();
        }
        if (requestCode==REQUEST_FRAGMENT_DELETE){
            UUID noteId= mNote.getId();
            NoteLab noteLab= NoteLab.get(getActivity());
            if (mPhotoView!=null&&mPhotoFile.exists()){
                noteLab.deletePhotoFile(mNote);
            }
            noteLab.deleteNote(noteId);
            List<Note> notes=noteLab.getNotes();
            if (notes.size()==0){
                NoteListFragment.mInitButton.setVisibility(View.VISIBLE);
                addAnimation(NoteListFragment.mInitButton);
            }

            if (notes.size()>0){
                getActivity().finish();
                startActivity(NotePagerActivity.updatePage(noteId));
            }else {
                getActivity().finish();
            }

        }
        if (requestCode==REQUEST_PHOTO){
            Uri uri= FileProvider.getUriForFile(getActivity(),
                    "com.example.notepad20.fileprovider",mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
        if (requestCode==REQUEST_SELECT_PHOTO&&data!=null){

            NoteLab crimeLab= NoteLab.get(getActivity());
            if (mPhotoView!=null&&mPhotoFile.exists()){
                crimeLab.deletePhotoFile(mNote);
            }
            Uri selectImage=data.getData();
            String[]filePathColumn={MediaStore.Images.Media.DATA};
            Cursor cursor=getContext().getContentResolver().query(selectImage,filePathColumn,
                    null,null,null);
            try {
                cursor.moveToFirst();
                mPicturePath=cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                mNote.setPicturePath(mPicturePath);
                updatePhotoView(mNote.getPicturePath());
            }finally {
                cursor.close();
            }


        }
        if (requestCode==REQUEST_SAVE_TEXT){
            String text=(String)data.getSerializableExtra(DialogContextFragment.EXTRA_TEXT);
            mNote.setDetail(text);
            mDetailField.setText(text);
        }
        if (requestCode==REQUEST_SAVE_LINK){
            String link=(String)data.getSerializableExtra(LinkDialog.EXTRA_LINK);
            mNote.setLink(link);
            mLinkView.setText(link);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_note,menu);
        MenuItem DeleteItem=menu.findItem(R.id.delete_record);
        if (!mNote.isLiked()){
            DeleteItem.setEnabled(true);
            DeleteItem.setVisible(true);
        }else {
            DeleteItem.setEnabled(false);
            DeleteItem.setVisible(false);
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete_record:
                FragmentManager fragmentManager=getFragmentManager();
                ConfirmDeleteFragment dialog=new ConfirmDeleteFragment();
                dialog.setTargetFragment(NoteFragment.this,REQUEST_FRAGMENT_DELETE);
                dialog.show(fragmentManager,DIALOG_DELETE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void updateDate() {
        CharSequence date=android.text.format.DateFormat.format("yyyy年MMMdd日,E", mNote.getDate());
        mDateButton.setText(date);
    }
    private void updateTime() {
        CharSequence date=android.text.format.DateFormat.format("北京时间kk点mm分", mNote.getDate());
        mTimeButton.setText(date);
    }

    public static NoteFragment newInstance(UUID crimeId){
        Bundle args=new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);
        NoteFragment fragment=new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private void updatePhotoView(){
        if (!mPhotoFile.exists()){
            if (mPhotoView.getDrawable()==null){
                if (mTheme.getNumber()==5){
                    Glide.with(getActivity()).load(R.drawable.xiaohei2).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
                }else if (mTheme.getNumber()==3){
                    Glide.with(getActivity()).load(R.drawable.xiaohei2).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
                }else if (mTheme.getNumber()==6){
                    Glide.with(getActivity()).load(R.drawable.wlop_icon).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
                }else if (mTheme.getNumber()==8){
                    Glide.with(getActivity()).load(R.drawable.koeni_logo).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
                }
                else {
                    Glide.with(getActivity()).load(R.drawable.picture).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
                }

            }
        }else {
            Glide.with(getActivity()).load(mPhotoFile.getPath()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
        }
    }

    private void updatePhotoView(String path){
        if (mNote.getPicturePath()==null||!(fileIsExists(path))){
            if (mPhotoView.getDrawable()==null){
                if (mTheme.getNumber()==5){
                    Glide.with(getContext()).load(R.drawable.xiaohei2).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
                }else if (mTheme.getNumber()==3){
                    Glide.with(getActivity()).load(R.drawable.xiaohei2).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
                }else if (mTheme.getNumber()==6){
                    Glide.with(getActivity()).load(R.drawable.wlop_icon).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
                }else if (mTheme.getNumber()==8){
                    Glide.with(getActivity()).load(R.drawable.koeni_icon).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
                }else {
                    Glide.with(getActivity()).load(R.drawable.picture).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
                }
            }
        }else if (fileIsExists(mNote.getPicturePath())){
            Glide.with(getActivity()).load(path).diskCacheStrategy(DiskCacheStrategy.NONE).into(mPhotoView);
        }
    }
    public static int readPictureDegree(String path){
        int degree=0;
        try {
            ExifInterface exifInterface=new ExifInterface(path);
            int orientation=exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree=90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree=180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree=270;
                    break;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return degree;
    }
    private boolean fileIsExists(String path){
        try {
            File file=new File(path);
            if (!file.exists()){
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }
    private void addAnimation(View v){
        for (Animator anim:mScaleInAnimation.getAnimators(v)){
            anim.setDuration(500).start();
            anim.setInterpolator(new LinearInterpolator());
        }
    }
}
