package com.example.notepad20;


import java.util.Date;
import java.util.UUID;

public class Note {
    private UUID mId;
    private String mTitle;
    private String mDetail;
    private Date mDate;
    private boolean mLiked;
    private String mLink;
    private String mPicturePath;

    public Note(){
        mId= UUID.randomUUID();
        mTitle="";
        mDate=new Date();
    }
    public Note(UUID id){
        mId=id;
        mDate=new Date();
        mTitle="";
    }
    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isLiked() {
        return mLiked;
    }

    public void setLiked(boolean liked) {
        mLiked = liked;
    }

    public String getDetail() {
        return mDetail;
    }

    public void setDetail(String detail) {
        mDetail = detail;
    }

    public String getPhotoFilename(){
        return "IMG_"+getId().toString()+".jpg";
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String mLink) {
        this.mLink = mLink;
    }

    public String getPicturePath() {
        return mPicturePath;
    }

    public void setPicturePath(String picturePath) {
        mPicturePath = picturePath;
    }

}
