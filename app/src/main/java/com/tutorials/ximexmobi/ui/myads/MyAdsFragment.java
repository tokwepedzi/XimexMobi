package com.tutorials.ximexmobi.ui.myads;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.databinding.FragmentMyadsBinding;

public class MyAdsFragment extends Fragment {

    //private MyAdsViewModel myAdsViewModel;
    private FragmentMyadsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       /* myAdsViewModel =
                new ViewModelProvider(this).get(MyAdsViewModel.class);*/

        binding = FragmentMyadsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final FloatingActionButton floatingActionButton = binding.addAdvertBtn;

       /* myAdsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Adding .....", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(),PostAdActivity.class));
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}