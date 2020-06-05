package com.themeUtil;

import android.content.Context;
import android.content.Intent;

import com.example.notepad20.SingleFragmentActivity;

import androidx.fragment.app.Fragment;

public class ThemeActivity extends SingleFragmentActivity {
    private ThemeFragment mFragment=new ThemeFragment();
    public static Intent newIntent(Context packageContext){
        Intent intent=new Intent(packageContext, ThemeActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return mFragment;
    }

}
