package com.socialmediasaver.status.fragment;

import static androidx.databinding.DataBindingUtil.inflate;
import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaVideo;
import static com.socialmediasaver.status.util.Utils.bannerInit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.activity.FullViewActivity;
import com.socialmediasaver.status.activity.InstagramFolderActivity;
import com.socialmediasaver.status.adapter.FileListAdapter;
import com.socialmediasaver.status.databinding.FragmentHistoryBinding;
import com.socialmediasaver.status.interfaces.FileListClickInterface;
import com.socialmediasaver.status.util.SharePrefs;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class InstaVideoDownloadedFragment extends Fragment implements FileListClickInterface {
    private FragmentHistoryBinding binding;
    private FileListAdapter fileListAdapter;
    private ArrayList<File> fileArrayList;
    private InstagramFolderActivity activity;
    private AdView adView;
    private AdRequest adRequest;
    public static InstaVideoDownloadedFragment newInstance(String param1) {
        InstaVideoDownloadedFragment fragment = new InstaVideoDownloadedFragment();
        Bundle args = new Bundle();
        args.putString("m", param1);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NotNull Context _context) {
        super.onAttach(_context);
        activity = (InstagramFolderActivity) _context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("m");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        activity = (InstagramFolderActivity) getActivity();
        getAllFiles();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = inflate(inflater, R.layout.fragment_history, container, false);
        initViews();
        checksubscription();
        return binding.getRoot();
    }
    private void initViews(){
        binding.swiperefresh.setOnRefreshListener(() -> {
            getAllFiles();
            binding.swiperefresh.setRefreshing(false);
        });
    }

    private void checksubscription() {
        // if (Subscription.equals("NO")) {
        if (!SharePrefs.getInstance(getActivity()).getSubscribeValueFromPref()) {
//            Banner banner = (Banner) findViewById(R.id.startAppInstasearch);
//            banner.setVisibility(View.VISIBLE);
//            banner.showBanner();

            adRequest = new AdRequest.Builder().build();
            bannerInit(getActivity());
            binding.fullImageAdView.setVisibility(View.VISIBLE);
            binding.fullImageAdView.loadAd(adRequest);
        }else {
//            Banner banner = (Banner) findViewById(R.id.startAppInstasearch);
//            banner.setVisibility(View.GONE);

            binding.fullImageAdView.setVisibility(View.GONE);
        }

    }

    private void getAllFiles(){
        fileArrayList = new ArrayList<>();
       // File[] files = RootDirectoryInstaShow.listFiles();
        File[] files = RootDirectoryInstaVideo.listFiles();
        if (files!=null) {
            for (File file : files) {
                fileArrayList.add(file);
            }
            fileListAdapter = new FileListAdapter(activity, fileArrayList, InstaVideoDownloadedFragment.this);
            binding.rvFileList.setAdapter(fileListAdapter);
        }
    }
    @Override
    public void getPosition(int position, File file) {
        Intent inNext = new Intent(activity, FullViewActivity.class);
        inNext.putExtra("ImageDataFile", fileArrayList);
        inNext.putExtra("Position", position);
        inNext.putExtra("screen", "insta");
        activity.startActivity(inNext);
    }
}