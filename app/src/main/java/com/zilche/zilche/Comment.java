package com.zilche.zilche;

/**
 * Created by Kjye on 8/18/2015.
 */
public class Comment {

    byte[] image;
    boolean hasImage = false;
    String name;
    String comment_text;
    String date_added;
    private int op;
    private int mod;
    String email;
    private String author_id;

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setMod(int mod) {
        this.mod = mod;
    }

    public int getMod() {
        return mod;
    }

    public Comment(String name, String comment_text, String date_added) {
        this.name = name;
        this.comment_text = comment_text;
        this.date_added = date_added;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public int getOp() {
        return op;
    }

    public boolean hasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getComment_text() {

        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {

        return image;

    }

    public Comment(String name, String comment_text, String date_added, boolean hasImage, byte[] image) {
        this.name = name;
        this.comment_text = comment_text;
        this.date_added = date_added;
        this.hasImage = hasImage;
        this.image = image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }






}
