package com.example.notepad20;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.themeUtil.ThemeActivity;
import com.themeUtil.ThemeLab;
import com.themeUtil.theme;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import xander.elasticity.ElasticityHelper;
import xander.elasticity.ORIENTATION;




public class NoteListFragment extends Fragment {

    private static final int REQUEST_CONFIRM=0;
    private static final int REQUEST_DELETE_ITEM=1;
    private static final int REQUEST_DELETE_ITEMS=2;
    private static final String CONFIRM="dialogConfirm";
    private static final String DELETE="dialogConfirmDelete";
    private static final String DELETE_ITEMS="dialogConfirmDelete";

    private int itemPosition;

    public static SwipeMenuRecyclerView mNoteRecyclerView;
    private NoteAdapter mAdapter;
    private boolean mSubtitleVisible=false;
    private ScaleInAnimation mScaleInAnimation;
    private ImageButton mDeleteButton;
    private SwipeMenuItemClickListener itemMenuClickListener;
    private SwipeMenuCreator menuCreator;
    private List<Note> deleteNotes;
    private boolean mInDeleteMode=false;
    public static boolean mInCollectionMode=false;
    private ThemeLab mThemeLab;
    private theme mTheme;
    private View mView;

    public static ImageButton mInitButton;
    private ImageButton mCollection;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mScaleInAnimation=new ScaleInAnimation();
        mThemeLab= ThemeLab.get(getActivity());
        mTheme=mThemeLab.getTheme();
        setHasOptionsMenu(true);
    }


    public void onKeyDown(){
        if (mInDeleteMode){
            mAdapter.setInDeleteMode(false);
        }else {
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        menuCreator=new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                int height= ViewGroup.LayoutParams.MATCH_PARENT;
                SwipeMenuItem delete=new SwipeMenuItem(getActivity())
                        .setBackground(R.color.transparent)
                        .setImage(R.drawable.delete)
                        .setWidth(80)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(delete);
            }
        };

        mView=inflater.inflate(R.layout.fragment_note_list,container,false);
        mNoteRecyclerView=mView.findViewById(R.id.note_recycler_view);

        mNoteRecyclerView.setSwipeMenuCreator(menuCreator);



        itemMenuClickListener=new SwipeMenuItemClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge) {
                itemPosition=menuBridge.getAdapterPosition();
                NoteLab noteLab= NoteLab.get(getActivity());
                List<Note> notes=noteLab.getNotes();
                Note note=notes.get(itemPosition);
                if (note.isLiked()){
                    menuBridge.closeMenu();
                    Toast toast= Toast.makeText(getContext(),null, Toast.LENGTH_SHORT);
                    toast.setText("删除被收藏的记录是不对的!");
                    toast.show();
                }else {
                    FragmentManager manager=getFragmentManager();
                    ConfirmDeleteFragment dialog=new ConfirmDeleteFragment();
                    dialog.setTargetFragment(NoteListFragment.this,REQUEST_DELETE_ITEM);
                    dialog.show(manager,DELETE);
                }

            }

        };
        mNoteRecyclerView.setSwipeMenuItemClickListener(itemMenuClickListener);

        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ElasticityHelper.setUpOverScroll(mNoteRecyclerView, ORIENTATION.VERTICAL);
        mNoteRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mInitButton=mView.findViewById(R.id.initiate_button);
        mInitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mInitButton.setEnabled(false);
                int cx=mInitButton.getWidth()/2;
                int cy=mInitButton.getHeight()/2;
                float radius=mInitButton.getWidth();
                final FragmentManager fragmentManager=getFragmentManager();
                final ConfirmFragment dialog=new ConfirmFragment();
                dialog.setTargetFragment(NoteListFragment.this,REQUEST_CONFIRM);
                Animator animator1= ViewAnimationUtils.createCircularReveal(mInitButton,cx,cy,0,radius);
                final Animator animator2= ViewAnimationUtils.createCircularReveal(mInitButton,cx,cy,radius,0);
                animator2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mInitButton.setVisibility(View.GONE);
                        mInitButton.setEnabled(true);
                        dialog.show(fragmentManager,CONFIRM);


                    }
                });
                animator2.setDuration(350);
                animator1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animator2.start();

                    }
                });
                animator1.setDuration(350);
                animator1.start();



            }
        });
        mDeleteButton=mView.findViewById(R.id.deleteButton);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteButton.setEnabled(false);
                int cx=mDeleteButton.getWidth()/2;
                int cy=mDeleteButton.getHeight()/2;
                float radius=mDeleteButton.getWidth();
                Animator animator= ViewAnimationUtils.createCircularReveal(mDeleteButton,cx,cy,0,radius);
                animator.addListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (deleteNotes.size()>0){
                            deleteItem();
                        }else {
                            Toast toast= Toast.makeText(getActivity(),null, Toast.LENGTH_SHORT);
                            toast.setText("什么都没选呐");
                            toast.show();
                        }
                        mDeleteButton.setEnabled(true);
                    }


                });
                animator.setDuration(200);
                animator.start();

            }

        });


        mCollection=mView.findViewById(R.id.back_to_normal_mode);
        mCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCollection.setEnabled(false);
                int cx=mCollection.getWidth()/2;
                int cy=mCollection.getHeight()/2;
                float radius=mCollection.getWidth();
                Animator animator1= ViewAnimationUtils.createCircularReveal(mCollection,cx,cy,0,radius);
                final Animator animator2= ViewAnimationUtils.createCircularReveal(mCollection,cx,cy,radius,0);
                animator2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mCollection.setVisibility(View.GONE);
                        mInCollectionMode=false;
                        updateUI();
                        getActivity().invalidateOptionsMenu();
                        mCollection.setEnabled(true);


                    }
                });
                animator2.setDuration(350);
                animator1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        animator2.start();

                    }
                });
                animator1.setDuration(350);
                animator1.start();

            }

        });
        switch (mTheme.getNumber()){
            case 0:
                mView.setBackground(getResources().getDrawable(R.drawable.background));
                break;
            case 1:
                mView.setBackground(getResources().getDrawable(R.drawable.background1));
                break;
            case 2:
                mView.setBackground(getResources().getDrawable(R.drawable.background2));
                break;
            case 3:
                mView.setBackground(getResources().getDrawable(R.drawable.background3));
                break;
            case 4:
                mView.setBackground(getResources().getDrawable(R.drawable.background4));
                break;
            case 5:
                mView.setBackground(getResources().getDrawable(R.drawable.background5));
                break;
            case 6:
                mView.setBackground(getResources().getDrawable(R.drawable.background6));
                break;
            case 7:
                mView.setBackground(getResources().getDrawable(R.drawable.background7));
                break;
            case 8:
                mView.setBackground(getResources().getDrawable(R.drawable.background8));
                break;
        }

        updateUI();

        return mView;

    }


    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Note mNote;
        private ImageView mLikedImageView;
        private RoundedImageView mIconImageView;
        private File mPhotoFile;
        private CheckBox deleteBox;



        public void bind(Note note){
            theme theme=mThemeLab.getTheme();
            if (theme.getNumber()!=mTheme.getNumber()){
                mTheme.setNumber(theme.getNumber());
               Activity activity= getActivity();
               Intent intent= NoteListActivity.newIntent(activity);
               startActivity(intent);
               getActivity().finish();
            }

            mNote =note;
            mPhotoFile= NoteLab.get(getActivity()).getPhotoFile(mNote);
            if (!mNote.getTitle().equals("")){
                mTitleTextView.setText(mNote.getTitle());
            }else {
                mTitleTextView.setText("无标题");
            }
            if (mPhotoFile.exists()){
                updatePhotoView();
            }else {
                updatePhotoView(mNote.getPicturePath());
            }
            switch (mTheme.getNumber()){
                case 0:
                    mTitleTextView.setTextColor(getResources().getColor(R.color.white));
                    mDateTextView.setTextColor(getResources().getColor(R.color.white));
                    Glide.with(getActivity()).load(R.drawable.heart).diskCacheStrategy(DiskCacheStrategy.NONE).
                            into(mLikedImageView);
                    break;
                case 1:
                    mTitleTextView.setTextColor(getResources().getColor(R.color.white));
                    mDateTextView.setTextColor(getResources().getColor(R.color.white));
                    Glide.with(getActivity()).load(R.drawable.heart).diskCacheStrategy(DiskCacheStrategy.NONE).
                            into(mLikedImageView);
                    break;
                case 2:
                    mTitleTextView.setTextColor(getResources().getColor(R.color.white));
                    mDateTextView.setTextColor(getResources().getColor(R.color.white));
                    Glide.with(getActivity()).load(R.drawable.heart).diskCacheStrategy(DiskCacheStrategy.NONE).
                            into(mLikedImageView);
                    break;
                case 3:
                    mTitleTextView.setTextColor(getResources().getColor(R.color.textColorThree));
                    mDateTextView.setTextColor(getResources().getColor(R.color.textColorThree));
                    Glide.with(getActivity()).load(R.drawable.heart).diskCacheStrategy(DiskCacheStrategy.NONE).
                            into(mLikedImageView);
                    break;
                case 4:
                    mTitleTextView.setTextColor(getResources().getColor(R.color.textColorFour));
                    mDateTextView.setTextColor(getResources().getColor(R.color.textColorFour));
                    Glide.with(getActivity()).load(R.drawable.heart).diskCacheStrategy(DiskCacheStrategy.NONE).
                            into(mLikedImageView);
                    break;
                case 5:
                    mTitleTextView.setTextColor(getResources().getColor(R.color.white));
                    mDateTextView.setTextColor(getResources().getColor(R.color.white));
                    Glide.with(getActivity()).load(R.drawable.heart).diskCacheStrategy(DiskCacheStrategy.NONE).
                            into(mLikedImageView);
                    break;
                case 6:
                    mTitleTextView.setTextColor(getResources().getColor(R.color.textColorSix));
                    mDateTextView.setTextColor(getResources().getColor(R.color.textColorSix));
                    Glide.with(getActivity()).load(R.drawable.heart).diskCacheStrategy(DiskCacheStrategy.NONE).
                            into(mLikedImageView);
                    break;
                case 7:
                    mTitleTextView.setTextColor(getResources().getColor(R.color.gray));
                    mDateTextView.setTextColor(getResources().getColor(R.color.gray));
                    Glide.with(getActivity()).load(R.drawable.heart).diskCacheStrategy(DiskCacheStrategy.NONE).
                            into(mLikedImageView);
                    break;
                case 8:
                    mTitleTextView.setTextColor(getResources().getColor(R.color.gray));
                    mDateTextView.setTextColor(getResources().getColor(R.color.gray));
                    Glide.with(getActivity()).load(R.drawable.koeni_logo).diskCacheStrategy(DiskCacheStrategy.NONE).
                            into(mLikedImageView);
                    break;
            }
            updateDate();


            mLikedImageView.setVisibility(note.isLiked()? View.VISIBLE: View.GONE);
        }

        public NoteHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_note,parent,false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mTitleTextView=itemView.findViewById(R.id.note_title);
            mDateTextView=itemView.findViewById(R.id.note_date);
            mLikedImageView =itemView.findViewById(R.id.note_liked);
            mIconImageView= itemView.findViewById(R.id.icon_image);
            deleteBox=itemView.findViewById(R.id.delete);
        }


        @Override
        public void onClick(View view) {
            if (!mInDeleteMode){
                Intent intent= NotePagerActivity.newIntent(getActivity(), mNote.getId());
                startActivity(intent);
            }

        }


        @Override
        public boolean onLongClick(View view) {
            if (!mInDeleteMode){
                if (!mDeleteButton.isShown()&&!mNote.isLiked()){
                    mDeleteButton.setVisibility(View.VISIBLE);
                    float start=view.getHeight();
                    float end=mDeleteButton.getTop();
                    ObjectAnimator animator= ObjectAnimator.ofFloat(mDeleteButton,"y"
                    ,start,end);
                    animator.setDuration(350);
                    animator.start();
                    mAdapter.setInDeleteMode(true);
                    mAdapter.setDeleteCrimes();
                }else if (!mDeleteButton.isShown()&& mNote.isLiked()){
                    Toast toast= Toast.makeText(getActivity(),null, Toast.LENGTH_SHORT);
                    toast.setText("如果你要查看的话，点一下就好了！");
                    toast.show();
                }
            }

            return true;
        }

        private void updateDate() {
            CharSequence date=android.text.format.DateFormat.format("yyyy年MMMdd日,E", mNote.getDate());
            mDateTextView.setText(date);
        }
        private void updatePhotoView(){
            if (!mPhotoFile.exists()){
                if (mIconImageView.getDrawable()==null){
                    if (mTheme.getNumber()==5){
                        Glide.with(getActivity()).load(R.drawable.xiaohei2).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
                    }else if (mTheme.getNumber()==3){
                        Glide.with(getActivity()).load(R.drawable.xiaohei2).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
                    }else if (mTheme.getNumber()==6){
                        Glide.with(getActivity()).load(R.drawable.wlop_icon).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
                    }else if (mTheme.getNumber()==8){
                        Glide.with(getActivity()).load(R.drawable.koeni_icon).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
                    }else {
                        Glide.with(getActivity()).load(R.drawable.picture).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
                    }
                }
            }else {
                Glide.with(getActivity()).load(mPhotoFile.getPath()).skipMemoryCache(true).
                        diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
            }
        }

        private void updatePhotoView(String path){
            if (mNote.getPicturePath()==null||!(fileIsExists(path))){
                if (mTheme.getNumber()==5){
                    Glide.with(getActivity()).load(R.drawable.xiaohei2).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
                }else if (mTheme.getNumber()==3){
                    Glide.with(getActivity()).load(R.drawable.xiaohei2).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
                }else if (mTheme.getNumber()==6){
                    Glide.with(getActivity()).load(R.drawable.wlop_icon).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
                }else if (mTheme.getNumber()==8){
                    Glide.with(getActivity()).load(R.drawable.koeni_icon).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
                }else {
                    Glide.with(getActivity()).load(R.drawable.picture).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
                }
            }else if (fileIsExists(mNote.getPicturePath())){
                Glide.with(getActivity()).load(path).diskCacheStrategy(DiskCacheStrategy.NONE).into(mIconImageView);
            }
        }


    }





    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {
        private List<Note> mNotes;

        public NoteAdapter(List<Note> notes){
            mNotes =notes;
        }
        public void setDeleteCrimes() {
            deleteNotes=new ArrayList<>();
        }


        public void setInDeleteMode(boolean inDeleteMode) {
            mInDeleteMode = inDeleteMode;
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater= LayoutInflater.from(getActivity());
            return new NoteHolder(layoutInflater,viewGroup);
        }

        @Override
        public void onViewAttachedToWindow(NoteHolder holder){
            super.onViewAttachedToWindow(holder);
            mNoteRecyclerView.smoothCloseMenu();
            addAnimation(holder);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteHolder noteHolder, final int i) {
            final Note note= mNotes.get(i);
            noteHolder.bind(note);
            noteHolder.deleteBox.setVisibility(mInDeleteMode&&!note.isLiked()? View.VISIBLE: View.GONE);
            noteHolder.deleteBox.setChecked(false);
            if (mInDeleteMode){
                setHasOptionsMenu(false);
                noteHolder.mLikedImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast toast= Toast.makeText(getActivity(),null, Toast.LENGTH_SHORT);
                        toast.setText("已收藏的笔记是删不了的！");
                        toast.show();
                    }
                });
                noteHolder.deleteBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            deleteNotes.add(note);
                        }else {
                            deleteNotes.remove(note);
                        }
                    }
                });
            }else {
              exitDeleteMode();
              noteHolder.mLikedImageView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      Intent intent= NotePagerActivity.newIntent(getActivity(),note.getId());
                      startActivity(intent);
                  }
              });
            }
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }


        public void setNotes(List<Note> notes){
            mNotes = notes;
        }



    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }


    private void updateUI(){
        NoteLab noteLab= NoteLab.get(getActivity());
        List<Note> notes=noteLab.getNotes();
        if (notes.size()==0&&!mInCollectionMode){
            mInitButton.setVisibility(View.VISIBLE);
            addAnimation(mInitButton);
        }else {
            mInitButton.setVisibility(View.GONE);
        }
        if (mAdapter==null){
            mAdapter=new NoteAdapter(notes);
            mNoteRecyclerView.setAdapter(mAdapter);
            updateSubtitle();
        }else {
            if (mInCollectionMode){
                List<Note> collected=noteLab.getCollectedNotes();
                mAdapter.setNotes(collected);
                if (collected.size()==0){
                   mCollection.setVisibility(View.VISIBLE);
                   addAnimation(mCollection);
                }

            }else {
                mAdapter.setNotes(notes);
                mCollection.setVisibility(View.GONE);
            }
            mAdapter.notifyDataSetChanged();
            updateSubtitle();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_note_list,menu);
        MenuItem subtitleItem=menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible){
            SpannableString hide=new SpannableString(getResources().getString(R.string.hide_subtitle));
            hide.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)),0,hide.length(),0);
            subtitleItem.setTitle(hide);
        }else {
            SpannableString display=new SpannableString(getResources().getString(R.string.show_subtitle));
            display.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)),0,display.length(),0);
            subtitleItem.setTitle(display);
        }
        MenuItem collectionItem=menu.findItem(R.id.collection_mode);
        if (mInCollectionMode){
            SpannableString exit=new SpannableString("退出收藏夹模式");
            exit.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)),0,exit.length(),0);
            collectionItem.setTitle(exit);
        }else {
            SpannableString enter=new SpannableString("收藏夹模式");
            enter.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)),0,enter.length(),0);
            collectionItem.setTitle(enter);
        }
        MenuItem themeItem=menu.findItem(R.id.change_theme);
        SpannableString themeItemText=new SpannableString("更换主题");
        themeItemText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)),0,themeItemText.length(),0);
        themeItem.setTitle(themeItemText);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_note:
                FragmentManager fragmentManager=getFragmentManager();
                ConfirmFragment dialog=new ConfirmFragment();
                dialog.setTargetFragment(NoteListFragment.this,REQUEST_CONFIRM);
                dialog.show(fragmentManager,CONFIRM);
                mInitButton.setVisibility(View.INVISIBLE);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible=!mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            case R.id.collection_mode:
                mInCollectionMode=!mInCollectionMode;
                updateUI();
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.change_theme:
                Intent intent= ThemeActivity.newIntent(getActivity());
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode!= Activity.RESULT_OK){
            return;
        }
        if (requestCode==REQUEST_CONFIRM){

            Note note=new Note();
            NoteLab.get(getActivity()).addNote(note);
            Intent intent= NotePagerActivity.newIntent(getActivity(),note.getId());
            startActivity(intent);
        }
        if (requestCode==REQUEST_DELETE_ITEM){
            NoteLab noteLab= NoteLab.get(getActivity());
            List<Note> notes=noteLab.getNotes();
            Note note=notes.get(itemPosition);
            if(mInDeleteMode){
                for (int i=0;i<deleteNotes.size();i++){
                    if (deleteNotes.get(i).getId().equals(note.getId())){
                        deleteNotes.remove(i);
                    }
                }
            }

            File photo=noteLab.getPhotoFile(note);
            noteLab.deleteNote(note.getId());
            if (photo.exists()){
                noteLab.deletePhotoFile((note));
            }
            notes=noteLab.getNotes();
            mAdapter.setNotes(notes);
            mAdapter.notifyItemRemoved(itemPosition);
            if (notes.size()==0){
                mInitButton.setVisibility(View.VISIBLE);
                addAnimation(mInitButton);
                if (mInDeleteMode){
                  exitDeleteMode();
                  mAdapter.setInDeleteMode(false);
                }
            }
            updateSubtitle();

        }
        if (requestCode==REQUEST_DELETE_ITEMS){
            NoteLab noteLab= NoteLab.get(getActivity());
            for (int i=0;i<deleteNotes.size();i++){
                Note crime=deleteNotes.get(i);
                File photo=noteLab.getPhotoFile(crime);
                noteLab.deleteNote(crime.getId());
                if (photo.exists()){
                    noteLab.deletePhotoFile((crime));
                }
            }
            List<Note> notesChanged=noteLab.getNotes();
            if (notesChanged.size()==0){
                exitDeleteMode();
                mInitButton.setVisibility(View.VISIBLE);
                addAnimation(mInitButton);
            }
            mAdapter.setNotes(notesChanged);
            mAdapter.setInDeleteMode(false);
            mAdapter.notifyDataSetChanged();
            updateSubtitle();
        }

    }
    private void updateSubtitle(){
        NoteLab noteLab= NoteLab.get(getActivity());
        int noteCount;
        if (mInCollectionMode){
            noteCount=noteLab.getCollectedNotes().size();
        }else {
            noteCount=noteLab.getNotes().size();
        }
        String subtitle=getString(R.string.subtitle_format,noteCount);
        if (mSubtitleVisible==false){
            subtitle=null;
        }
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
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
    private void addAnimation(NoteHolder holder){
        for (Animator anim:mScaleInAnimation.getAnimators(holder.itemView)){
            anim.setDuration(500).start();
            anim.setInterpolator(new LinearInterpolator());
        }
    }
    private void addAnimation(View v){
        for (Animator anim:mScaleInAnimation.getAnimators(v)){
            anim.setDuration(500).start();
            anim.setInterpolator(new LinearInterpolator());
        }
    }
    private void deleteItem(){
        FragmentManager manager=getFragmentManager();
        ConfirmDeleteItems dialog=new ConfirmDeleteItems();
        dialog.setTargetFragment(NoteListFragment.this,REQUEST_DELETE_ITEMS);
        dialog.show(manager,DELETE_ITEMS);
    }
    private void exitDeleteMode(){
        setHasOptionsMenu(true);
        int cx=mDeleteButton.getWidth()/2;
        int cy=mDeleteButton.getHeight()/2;
        float radius=mDeleteButton.getWidth();
        Animator animator= ViewAnimationUtils.createCircularReveal(mDeleteButton,cx,cy,radius,0);
        animator.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDeleteButton.setVisibility(View.GONE);
            }
        });
        animator.setDuration(400);
        animator.start();
    }
}
