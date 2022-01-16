package com.tutorials.ximexmobi.ui.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tutorials.ximexmobi.R;
import com.tutorials.ximexmobi.databinding.FragmentUserInfoBinding;


public class UserInfoFragment extends Fragment {
private FragmentUserInfoBinding binding;


    public UserInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserInfoBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        View root = binding.getRoot();
        return root;
    }
}