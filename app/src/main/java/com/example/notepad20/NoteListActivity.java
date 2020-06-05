package com.example.notepad20;



import android.content.Context;
import android.content.Intent;

import android.view.KeyEvent;

import androidx.fragment.app.Fragment;


public class NoteListActivity extends SingleFragmentActivity {
    NoteListFragment mFragment=new NoteListFragment();
    @Override
    protected Fragment createFragment() {
        return mFragment;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode== KeyEvent.KEYCODE_BACK&&event.getAction()== KeyEvent.ACTION_DOWN){
             mFragment.onKeyDown();
             return true;
        }
        return super.onKeyDown(keyCode,event);

    }
    public static Intent newIntent(Context packageContext){
        Intent intent=new Intent(packageContext, NoteListActivity.class);
        return intent;
    }
}
