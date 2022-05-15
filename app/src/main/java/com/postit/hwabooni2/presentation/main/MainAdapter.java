package com.postit.hwabooni2.presentation.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.postit.hwabooni2.databinding.ItemFriendCardViewBinding;
import com.postit.hwabooni2.model.Emotion;
import com.postit.hwabooni2.model.FriendData;

public class MainAdapter extends ListAdapter<FriendData, MainAdapter.FriendViewHolder> {

    OnClickListener listener;

    protected MainAdapter() {
        super(diffCallback);
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendViewHolder(ItemFriendCardViewBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        holder.bind(getCurrentList().get(position));
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {

        private ItemFriendCardViewBinding binding;

        FriendViewHolder(@NonNull ItemFriendCardViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FriendData data){
            binding.friendName.setText(data.getName());

            Drawable drawable = AppCompatResources.getDrawable(binding.getRoot().getContext(), Emotion.values()[Integer.parseInt(data.getEmotion())].getIcon());
            binding.friendEmotionView.setImageDrawable(drawable);

            binding.plantImageView.setVisibility(View.VISIBLE);
            Glide.with(binding.getRoot())
                    .load(data.getPlantImage())
                    .transform(new CenterCrop(), new RoundedCorners(12))
                    .into(binding.plantImageView);

            String phoneNumber="tel:"+data.getPhone();

            binding.friendCallButton.setOnClickListener((v)->{
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
                binding.getRoot().getContext().startActivity(intent);
            });

            binding.getRoot().setOnClickListener((v)->{
                if(listener!=null) listener.onClick(data.getId());
            });
        }

    }

    interface OnClickListener{
        void onClick(String id);
    }

    static DiffUtil.ItemCallback<FriendData> diffCallback = new DiffUtil.ItemCallback<FriendData>() {
        @Override
        public boolean areItemsTheSame(@NonNull FriendData oldItem, @NonNull FriendData newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull FriendData oldItem, @NonNull FriendData newItem) {
            return false;
        }
    };
}
