package com.zilche.zilche;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.parse.Parse;
import com.parse.ParseFile;

public class Poll implements Parcelable{

    private int anon;
    private String question;
    private String[] options;
    private int[] votes;
    private String date_added;
    private String author;
    private String authorLogin;
    private int options_count;
    private int category;
    private String category_title;
    private String id;
    private int hasImage;
    private byte[] image;
    private ParseFile file;

    public ParseFile getFile() {
        return file;
    }

    public void setFile(ParseFile pf) {
        file = pf;
    }

    public byte[] getImage() {
        return image;
    }

    public int hasImage() {
        return hasImage;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setHasImage(int i) {
        hasImage = i;
    }

    public Poll(String id, String question, String[] options, int[] votes,
                String date, String author, String authorLogin, int options_count,
                int category) {
        this.question = question;
        this.options = options;
        this.options_count = options_count;
        this.date_added = date;
        this.author = author;
        this.authorLogin = authorLogin;
        this.votes = votes;
        this.category = category;
        this.id = id;
    }

    public Poll() {}

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getCount() {
        return options_count;
    }

    public void setCount(int count) {
        options_count = count;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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

    public String getAuthorLogin() {
        return authorLogin;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
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
        dest.writeString(question);
        dest.writeStringArray(options);
        dest.writeIntArray(votes);
        dest.writeString(date_added);
        dest.writeString(author);
        dest.writeString(authorLogin);
        dest.writeInt(options_count);
        dest.writeInt(category);
        dest.writeString(category_title);
        dest.writeInt(anon);
        dest.writeInt(hasImage);
        dest.writeByteArray(image);
    }

    public static final Parcelable.Creator<Poll> CREATOR = new Creator<Poll>() {
        @Override
        public Poll createFromParcel(Parcel source) {
            Poll poll = new Poll(source.readString(), source.readString(), source.createStringArray(), source.createIntArray(),
                    source.readString(), source.readString(), source.readString(), source.readInt(), source.readInt());
            poll.setCategory_title(source.readString());
            poll.setAnon(source.readInt());
            poll.setHasImage(source.readInt());
            poll.setImage(source.createByteArray());
            return poll;
        }

        @Override
        public Poll[] newArray(int size) {
            return new Poll[size];
        }
    };

    public int totalVotes() {
        int ret = 0;
        for (int i = 0; i < votes.length; i++) {
            ret += votes[i];
        }
        return ret;
    }

    public void setAnon(int c) {
        anon = c;
    }

    public int getAnon() {
        return anon;
    }

}
