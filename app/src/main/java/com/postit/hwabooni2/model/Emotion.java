package com.postit.hwabooni2.model;

import com.postit.hwabooni2.R;

public enum Emotion {
    HAPPY(0, "기쁨", R.drawable.ic_good),
    HURT(1, "상처", R.drawable.ic_hurt),
    ANGRY(2, "화남", R.drawable.ic_angry),
    NERVOUS(3, "불안", R.drawable.ic_nervous),
    CONFUSE(4, "당황", R.drawable.ic_confused),
    SAD(5, "슬픔", R.drawable.ic_sad);

    int type;
    String krEmotion;
    int icon;

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

    Emotion(int type, String krEmotion, int icon) {
        this.type = type;
        this.krEmotion = krEmotion;
        this.icon = icon;
    }
}
