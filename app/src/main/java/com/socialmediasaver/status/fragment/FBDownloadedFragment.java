package com.socialmediasaver.status.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.activity.DownLoadMainActivity;
import com.socialmediasaver.status.activity.FullViewActivity;
import com.socialmediasaver.status.activity.WhatsappActivity;
import com.socialmediasaver.status.adapter.FileListAdapter;
import com.socialmediasaver.status.databinding.FragmentHistoryBinding;
import com.socialmediasaver.status.interfaces.FileListClickInterface;
import com.socialmediasaver.status.util.SharePrefs;


import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;
import static androidx.databinding.DataBindingUtil.inflate;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.socialmediasaver.status.util.Utils.RootDirectoryFacebookShow;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;

public class FBDownloadedFragment extends Fragment implements FileListClickInterface {
    private FragmentHistoryBinding binding;
    private FileListAdapter fileListAdapter;
    private ArrayList<File> fileArrayList;
    //private GalleryActivity activity;
    private DownLoadMainActivity activity;
    private AdRequest adRequest;
    public static FBDownloadedFragment newInstance(String param1) {
        FBDownloadedFragment fragment = new FBDownloadedFragment();
        Bundle args = new Bundle();
        args.putString("m", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NotNull Context _context) {
        super.onAttach(_context);
        activity = (DownLoadMainActivity) _context;
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
        DownLoadMainActivity.toolbar.setTitle("FB Downloads");
        activity = (DownLoadMainActivity) getActivity();
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

    private void checksubscription() {
       // if (Subscription.equals("NO")) {
        if (!SharePrefs.getInstance(getActivity()).getSubscribeValueFromPref()) {
//            Banner banner = (Banner) binding.startAppfragmentHis;
//            banner.setVisibility(View.VISIBLE);
//            banner.showBanner();

            adRequest = new AdRequest.Builder().build();
            bannerInit(getApplicationContext());
            binding.fullImageAdView.setVisibility(View.VISIBLE);
            binding.fullImageAdView.loadAd(adRequest);
        }else {
//            Banner banner = (Banner) binding.startAppfragmentHis;
//            banner.setVisibility(View.GONE);
            binding.fullImageAdView.setVisibility(View.GONE);
        }

    }
    private void initViews(){
        binding.swiperefresh.setOnRefreshListener(() -> {
            getAllFiles();
            binding.swiperefresh.setRefreshing(false);
        });
    }

    private void getAllFiles(){
        fileArrayList = new ArrayList<>();
        File[] files = RootDirectoryFacebookShow.listFiles();
        if (files!=null) {
            for (File file : files) {
                fileArrayList.add(file);
            }

            fileListAdapter = new FileListAdapter(activity, fileArrayList, FBDownloadedFragment.this);
            binding.rvFileList.setAdapter(fileListAdapter);
        }

    }

    @Override
    public void getPosition(int position, File file) {
        Intent inNext = new Intent(activity, FullViewActivity.class);
        inNext.putExtra("ImageDataFile", fileArrayList);
        inNext.putExtra("Position", position);
        inNext.putExtra("screen", "fb");

        activity.startActivity(inNext);
    }
}
