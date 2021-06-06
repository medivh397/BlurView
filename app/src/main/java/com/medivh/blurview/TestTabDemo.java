package com.medivh.blurview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.medivh.blurview.core.BlurLayout;

public class TestTabDemo extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_demo);

        BlurLayout blurLayout = findViewById(R.id.blurLayout);
        blurLayout.setCoverColor(Color.parseColor("#ddffffff"));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new RVAdapter(R.layout.list_item2));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
    }

}