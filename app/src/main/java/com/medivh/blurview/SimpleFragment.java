package com.medivh.blurview;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.medivh.blurview.core.BlurLayout;

/**
 * A fragment representing a list of Items.
 */
public class SimpleFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_simple, null);
        BlurLayout blurLayout = rootView.findViewById(R.id.blurLayout);
        blurLayout.setCoverColor(Color.parseColor("#aaffffff"));
        return rootView;
    }
}