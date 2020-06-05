package com.example.notepad20;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.themeUtil.ThemeLab;
import com.themeUtil.theme;

import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import xander.elasticity.ElasticityHelper;


public class NotePagerActivity extends AppCompatActivity {
    private static final String EXTRA_NOTE_ID ="com.example.notepad20.note_id";


    private static ViewPager mViewPager;
    private static List<Note> mNotes;
    private static MyPagerAdapter sAdapter;
    private static Context mContext;
    private theme mTheme;

    public static Intent newIntent(Context packageContext, UUID noteId){
        Intent intent=new Intent(packageContext, NotePagerActivity.class);
        intent.putExtra(EXTRA_NOTE_ID,noteId);
        return intent;
    }
    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Note crime= mNotes.get(i);
            return NoteFragment.newInstance(crime.getId());
        }

        @Override
        public int getCount() {
            return mNotes.size();
        }


    }




    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ThemeLab themeLab= ThemeLab.get(this);
        mTheme=themeLab.getTheme();
        switch (mTheme.getNumber()){
            case 0:
                setTheme(R.style.AppTheme);
                break;
            case 1:
                setTheme(R.style.AppThemeOne);
                break;
            case 2:
                setTheme(R.style.AppThemeTwo);
                break;
            case 3:
                setTheme(R.style.AppThemeThree);
                break;
            case 4:
                setTheme(R.style.AppThemeFour);
                break;
            case 5:
                setTheme(R.style.AppThemeFive);
                break;
            case 6:
                setTheme(R.style.AppThemeSix);
                break;
            case 7:
                setTheme(R.style.AppThemeEight);
                break;
            case 8:
                setTheme(R.style.AppThemeSix);
                break;
        }
        setContentView(R.layout.activity_note_pager);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        UUID noteId=(UUID)getIntent().getSerializableExtra(EXTRA_NOTE_ID);
        mViewPager=findViewById(R.id.note_view_pager);
        ElasticityHelper.setUpOverScroll(mViewPager);
        mContext=getApplicationContext();
        if (NoteListFragment.mInCollectionMode){
            mNotes = NoteLab.get(mContext).getCollectedNotes();
        }else {
            mNotes = NoteLab.get(mContext).getNotes();
        }
        FragmentManager fragmentManager=getSupportFragmentManager();
        sAdapter=new MyPagerAdapter(fragmentManager);
        mViewPager.setPageTransformer(true,new AccordionTransformer());
        mViewPager.setAdapter(sAdapter);
        for (int i = 0; i< mNotes.size(); i++){
            if (mNotes.get(i).getId().equals(noteId)){
                mViewPager.setCurrentItem(i);
                break;
            }

        }

    }
    public static Intent updatePage(UUID id){
        for (int i = 0; i< mNotes.size(); i++){
            if (mNotes.get(i).getId().equals(id)&& mNotes.size()>1){
               if (i+1< mNotes.size()){
                   return newIntent(mContext, mNotes.get(i+1).getId());
               }else{
                   return newIntent(mContext, mNotes.get(i-1).getId());
               }
            }
        }
        return null;
    }

}
