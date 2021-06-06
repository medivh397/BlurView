package com.medivh.blurview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.medivh.blurview.core.BlurLayout;

public class DemoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BlurLayout blurLayout;
    int lastPosition;
    boolean reverse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blurLayout = findViewById(R.id.blurLayout);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new RVAdapter(R.layout.list_item));
    }


    public void demo1(View view) {
        int position = lastPosition == 0 ? 2 : 0;
        lastPosition = position;
        recyclerView.smoothScrollToPosition(position);
    }

    public void demo2(View view) {
        float dp200 = getResources().getDimension(R.dimen.dp200);
        ObjectAnimator animator = ObjectAnimator.ofFloat(blurLayout, View.TRANSLATION_Y, blurLayout.getTranslationY(), blurLayout.getTranslationY() + (reverse ? -dp200 : dp200));
        reverse = !reverse;
        animator.setDuration(500);
        animator.start();
    }

    public void demo3(View view) {
        Intent intent = new Intent();
        intent.setClass(this, TabActivity.class);
        startActivity(intent);
    }

}
