package com.zilche.zilche;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mark on 7/16/2015.
 */
public class Survey implements Parcelable {

    private String title;
    private String[] questions;
    private int[] votes;
    private String date_added;
    private String author;
    private int questions_count;
    private int category;
    private String category_title;
    private String id;

    public Survey(String id, String title, String[] questions, int[] votes,
                String date, String author, int questions_count,
                int category) {
        this.title = title;
        this.questions = questions;
        this.questions_count = questions_count;
        this.date_added = date;
        this.author = author;
        this.votes = votes;
        this.category = category;
        this.id = id;
    }

    public Survey() {}


    public String getId() {
        return id;
    }

    public int getCount() {
        return questions_count;
    }

    public void setCount(int count) {
        questions_count = count;
    }

    public String[] getQuestions() {
        return questions;
    }

    public void setQuestions(String[] questions) {
        this.questions = questions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int[] getVotes() {
        return votes;
    }

    public void setVotes(int[] votes) {
        this.votes = votes;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date) {
        date_added = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getCategory_title() {
        return category_title;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeStringArray(questions);
        dest.writeIntArray(votes);
        dest.writeString(date_added);
        dest.writeString(author);
        dest.writeInt(questions_count);
        dest.writeInt(category);
        dest.writeString(category_title);
    }

    public static final Parcelable.Creator<Survey> CREATOR = new Creator<Survey>() {
        @Override
        public Survey createFromParcel(Parcel source) {
            Survey survey = new Survey(source.readString(), source.readString(), source.createStringArray(), source.createIntArray(),
                    source.readString(), source.readString(), source.readInt(), source.readInt());
            survey.setCategory_title(source.readString());
            return survey;
        }

        @Override
        public Survey[] newArray(int size) {
            return new Survey[size];
        }
    };

    public int totalVotes() {
        int ret = 0;
        for (int i = 0; i < votes.length; i++) {
            ret += votes[i];
        }
        return ret;
    }

}
