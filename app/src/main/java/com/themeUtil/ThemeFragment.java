package com.themeUtil;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.notepad20.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import xander.elasticity.ElasticityHelper;


public class ThemeFragment extends Fragment {
    private GridView gridView;
    private List<Map<String, Object>> themeList;
    private SimpleAdapter adapter;
    private theme mTheme;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeLab themeLab= ThemeLab.get(getContext());
        mTheme=themeLab.getTheme();

    }
    @Override
    public void onPause(){
        super.onPause();
        ThemeLab.get(getActivity()).updateTheme(mTheme);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_theme,container,false);

        gridView = view.findViewById(R.id.grid_view);
        ElasticityHelper.setUpOverScroll(gridView);
        initData();

        String[] from={"img","text"};

        int[] to={R.id.img,R.id.text};

        adapter=new SimpleAdapter(getContext(), themeList, R.layout.grid_item_theme, from, to);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (mTheme.getNumber()!=arg2){
                    mTheme.setNumber(arg2);
                    getActivity().finish();
                    Intent intent= ThemeActivity.newIntent(getContext());
                    startActivity(intent);

                }else {
                    Toast toast= Toast.makeText(getActivity(),null, Toast.LENGTH_SHORT);
                    toast.setText("你正在用的就是这个主题！");
                    toast.show();
                }

            }
        });

        switch (mTheme.getNumber()){
            case 0:
                view.setBackground(getResources().getDrawable(R.drawable.background));
                break;
            case 1:
                view.setBackground(getResources().getDrawable(R.drawable.background1));
                break;
            case 2:
                view.setBackground(getResources().getDrawable(R.drawable.background2));
                break;
            case 3:
                view.setBackground(getResources().getDrawable(R.drawable.background3));
                break;
            case 4:
                view.setBackground(getResources().getDrawable(R.drawable.background4));
                break;
            case 5:
                view.setBackground(getResources().getDrawable(R.drawable.background5));
                break;
            case 6:
                view.setBackground(getResources().getDrawable(R.drawable.background6));
                break;
            case 7:
                view.setBackground(getResources().getDrawable(R.drawable.background7));
                break;
            case 8:
                view.setBackground(getResources().getDrawable(R.drawable.background8));
                break;
        }
        return view;
    }

    private void initData() {

        int icon[] = { R.drawable.background_icon, R.drawable.background1_icon,R.drawable.background2_icon,
                R.drawable.background3_icon
        ,R.drawable.background4_icon,R.drawable.background5_icon,
                R.drawable.background6_icon,
                R.drawable.background7_icon,R.drawable.background8_icon};

        String name[]={"","","","","","","","",""};
        themeList = new ArrayList<>();
        for (int i = 0; i <icon.length; i++) {
            Map<String, Object> map=new HashMap<>();
            map.put("img", icon[i]);
            map.put("text",name[i]);
            themeList.add(map);
        }
    }


}
