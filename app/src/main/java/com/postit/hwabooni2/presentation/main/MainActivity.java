package com.postit.hwabooni2.presentation.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.postit.hwabooni2.databinding.ActivityMainBinding;
import com.postit.hwabooni2.model.FriendData;
import com.postit.hwabooni2.presentation.detail.DetailActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    MainAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new MainAdapter();
        adapter.listener = ((id -> {
            startActivity(DetailActivity.getIntent(this,id));
        }));
        binding.recyclerView.setAdapter(adapter);

        db.collection("dummyFriend").get().addOnCompleteListener((task -> {
            if(task.isSuccessful()){
                ArrayList<FriendData> data = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    data.add(doc.toObject(FriendData.class));
                }
                adapter.submitList(data);
            }
        }));

    }
}