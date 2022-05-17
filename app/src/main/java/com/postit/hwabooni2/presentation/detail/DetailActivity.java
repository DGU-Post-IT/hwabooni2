package com.postit.hwabooni2.presentation.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.postit.hwabooni2.R;
import com.postit.hwabooni2.databinding.ActivityDetailBinding;
import com.postit.hwabooni2.model.Emotion;
import com.postit.hwabooni2.model.FriendData;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    ActivityDetailBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String id = intent.getStringExtra(EXTRA_ID_KEY);
        if (id == null) finish();

        db.collection("dummyFriend").document(id).get().addOnCompleteListener((task -> {
            if(task.isSuccessful()){
                FriendData data = task.getResult().toObject(FriendData.class);
                binding.friendName.setText(data.getName());

                Drawable drawable = AppCompatResources.getDrawable(binding.getRoot().getContext(), Emotion.values()[Integer.parseInt(data.getEmotion())].getIcon());
                binding.friendEmotionView.setImageDrawable(drawable);

                binding.plantImageView.setVisibility(View.VISIBLE);
                Glide.with(binding.getRoot())
                        .load(data.getPlantImage())
                        .transform(new CenterCrop(), new RoundedCorners(12))
                        .into(binding.plantImageView);

            }
        }));



        LocalDateTime today = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        Date date = Date.from(firstDayOfMonth.toInstant(ZoneOffset.of("+9")));

        int[] check = new int[32];
        AtomicInteger count = new AtomicInteger();

        db.collection("dummyFriend").document(id)
                .collection("watering").whereGreaterThanOrEqualTo("datetime", date)
                .get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    LocalDateTime temp = LocalDateTime.ofInstant(((Timestamp) doc.get("datetime")).toDate().toInstant(), ZoneId.of("Asia/Seoul"));
                    check[temp.getDayOfMonth()] += 1;
                }
                if (count.incrementAndGet() == 2) updateDecorator(check);
            }
        });
        db.collection("dummyFriend").document(id)
                .collection("prettyWord").whereGreaterThanOrEqualTo("datetime", date)
                .get().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    LocalDateTime temp = LocalDateTime.ofInstant(((Timestamp) doc.get("datetime")).toDate().toInstant(), ZoneId.of("Asia/Seoul"));
                    check[temp.getDayOfMonth()] += 2;
                }
                if (count.incrementAndGet() == 2) updateDecorator(check);
            }
        });


    }

    void updateDecorator(int[] chk) {
        binding.calendarView.removeDecorators();
        binding.calendarView.addDecorators(new PrettyWordDecorator(chk), new WateringDecorator(chk), new BothDecorator(chk));
        binding.calendarView.invalidate();
    }

    class PrettyWordDecorator implements DayViewDecorator {

        private int[] chk;

        public PrettyWordDecorator(int[] chk) {
            this.chk = chk;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return chk[day.getDay()] == 2;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_heart));
        }
    }

    class WateringDecorator implements DayViewDecorator {

        private int[] chk;

        public WateringDecorator(int[] chk) {
            this.chk = chk;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return chk[day.getDay()] == 1;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_line_new));
        }
    }

    class BothDecorator implements DayViewDecorator {

        private int[] chk;

        public BothDecorator(int[] chk) {
            this.chk = chk;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return chk[day.getDay()] == 3;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_heart_line));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null && ev != null) {
            Rect rect = new Rect();
            getCurrentFocus().getGlobalVisibleRect(rect);
            if (!rect.contains((int) ev.getX(), (int) ev.getY())) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                getCurrentFocus().clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public static Intent getIntent(Context context, String id) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_ID_KEY, id);
        return intent;
    }

    static final String EXTRA_ID_KEY = "EXTRA_ID_KEY";
}
