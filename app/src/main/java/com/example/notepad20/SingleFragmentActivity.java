package com.example.notepad20;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.themeUtil.ThemeLab;
import com.themeUtil.theme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();
    private theme mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ThemeLab themeLab= ThemeLab.get(this);
        if (themeLab.getTheme()==null){
            mTheme=new theme();
            mTheme.setNumber(1);
            themeLab.addTheme(mTheme);
        }else {
            mTheme=themeLab.getTheme();
        }
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
        setContentView(R.layout.activity_fragment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FragmentManager fm=getSupportFragmentManager();
        Fragment fragment=fm.findFragmentById(R.id.fragment_container);
        if (fragment==null){
            fragment=createFragment();
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }
        Intent intent =getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        //设置接收类型为文本
        if (Intent.ACTION_SEND.equals(action) && type != null){
            if ("text/plain".equals(type)) {
                handlerText(intent);
            }
        }

    }
    private void handlerText(Intent intent) {
        String link = intent.getStringExtra(Intent.EXTRA_TEXT);
        Note note=new Note();
        note.setLink(link);
        note.setTitle(link);
        NoteLab.get(this).addNote(note);
        Intent intentCrime= NotePagerActivity.newIntent(this,note.getId());
        startActivity(intentCrime);

    }
}
