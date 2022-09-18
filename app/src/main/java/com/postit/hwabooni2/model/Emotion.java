package com.postit.hwabooni2.model;

import com.postit.hwabooni2.R;

public enum Emotion {
    HAPPY(0, "기쁨", R.drawable.ic_good,R.color.happy_pink),
    HURT(1, "상처", R.drawable.ic_hurt,R.color.wound_green),
    ANGRY(2, "화남", R.drawable.ic_angry,R.color.angry_yellow),
    NERVOUS(3, "불안", R.drawable.ic_nervous,R.color.soso_blue),
    CONFUSE(4, "당황", R.drawable.ic_confused,R.color.embarrass_gray),
    SAD(5, "슬픔", R.drawable.ic_sad,R.color.sad_mint);

    int type;
    String krEmotion;
    int icon;
    int color;

    Emotion() {
    }

    Emotion(int type) {
        this.type = type;
    }

    Emotion(String krEmotion) {
        this.krEmotion = krEmotion;
    }

    public int getType() {
        return type;
    }

    public String getKrEmotion() {
        return krEmotion;
    }

    public int getIcon() {
        return icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    Emotion(int type, String krEmotion, int icon, int color) {
        this.type = type;
        this.krEmotion = krEmotion;
        this.icon = icon;
        this.color = color;
    }
}
