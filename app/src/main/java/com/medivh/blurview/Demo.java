package com.medivh.blurview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.util.Random;

public class Demo extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BlurLayout blurLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blurLayout = findViewById(R.id.blurLayout);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new RecyclerView.Adapter<Holder>() {
            @NonNull
            @Override
            public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(Demo.this).inflate(R.layout.list_item, null);
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


        });

    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView img = itemView.findViewById(R.id.img);

        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }

    int position;

    public void demo1(View view) {
        position = position == 0 ? 2 : 2 + new Random().nextInt(4);
        recyclerView.smoothScrollToPosition(position);
    }

    boolean reverse;

    public void demo2(View view) {
        float dp200 = getResources().getDimension(R.dimen.dp200);
        ObjectAnimator animator = ObjectAnimator.ofFloat(blurLayout, View.TRANSLATION_Y, blurLayout.getTranslationY(), blurLayout.getTranslationY() + (reverse ? -dp200 : dp200));
        reverse = !reverse;
        animator.setDuration(500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

}
