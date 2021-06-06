package com.medivh.blurview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class RVAdapter extends RecyclerView.Adapter<RVAdapter.Holder> {

    int itemLayoutId;
    public RVAdapter(int itemLayoutId){
        this.itemLayoutId = itemLayoutId;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, null);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        switch (position % 6) {
            case 0:
                holder.img.setImageResource(R.drawable.a1);
                break;
            case 1:
                holder.img.setImageResource(R.drawable.a2);
                break;
            case 2:
                holder.img.setImageResource(R.drawable.a3);
                break;
            case 3:
                holder.img.setImageResource(R.drawable.a4);
                break;
            case 4:
                holder.img.setImageResource(R.drawable.a5);
                break;
            case 5:
                holder.img.setImageResource(R.drawable.a6);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 999;
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView img = itemView.findViewById(R.id.img);

        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
