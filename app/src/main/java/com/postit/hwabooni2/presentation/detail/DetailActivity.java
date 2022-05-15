package com.postit.hwabooni2.presentation.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.postit.hwabooni2.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


    public static Intent getIntent(Context context,String id){
        Intent intent = new Intent(context,DetailActivity.class);
        intent.putExtra(EXTRA_ID_KEY,id);
        return intent;
    };

    static final String EXTRA_ID_KEY = "EXTRA_ID_KEY";
}
