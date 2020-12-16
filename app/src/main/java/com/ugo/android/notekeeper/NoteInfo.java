package com.ugo.android.notekeeper;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jim.
 */

public final class NoteInfo implements Parcelable {
    private int mId;
    private CourseInfo mCourse;
    private String mTitle;
    private String mText;

    public NoteInfo(int id, CourseInfo course, String title, String text) {
        mId = id;
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    public NoteInfo(CourseInfo course, String title, String text) {
        mCourse = course;
        mTitle = title;
        mText = text;
    }

    protected NoteInfo(Parcel source) {
        mCourse = source.readParcelable(NoteInfo.class.getClassLoader());
        mTitle = source.readString();
        mText = source.readString();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public CourseInfo getCourse() {
        return mCourse;
    }

    public void setCourse(CourseInfo course) {
        mCourse = course;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getCourseId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo that = (NoteInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mCourse, 0);
        dest.writeString(mTitle);
        dest.writeString(mText);
    }


    public static final Creator<NoteInfo> CREATOR = new Creator<NoteInfo>() {
        @Override
        public NoteInfo createFromParcel(Parcel source) {
            return new NoteInfo(source);
        }

        @Override
        public NoteInfo[] newArray(int size) {
            return new NoteInfo[size];
        }
    };
}
