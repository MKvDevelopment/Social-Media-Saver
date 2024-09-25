package com.socialmediasaver.status.fragment;

import static androidx.databinding.DataBindingUtil.inflate;
import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaImage;
import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaShow;
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
import com.socialmediasaver.status.activity.InstaSearchUserActivity;
import com.socialmediasaver.status.activity.InstagramFolderActivity;
import com.socialmediasaver.status.adapter.FileListAdapter;
import com.socialmediasaver.status.databinding.FragmentHistoryBinding;
import com.socialmediasaver.status.interfaces.FileListClickInterface;
import com.socialmediasaver.status.util.SharePrefs;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class InstaImagesDownloadedFragment extends Fragment implements FileListClickInterface {
    private FragmentHistoryBinding binding;
    private FileListAdapter fileListAdapter;
    private ArrayList<File> fileArrayList;
    private InstagramFolderActivity activity;
    private AdView adView;
    private AdRequest adRequest;

    public static InstaImagesDownloadedFragment newInstance(String param1) {
        InstaImagesDownloadedFragment fragment = new InstaImagesDownloadedFragment();
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
        File[] files;
        fileArrayList = new ArrayList<>();
         files = RootDirectoryInstaShow.listFiles();

       // File[] files = RootDirectoryInstaShow.listFiles();
       // File[] files = RootDirectoryInstaImage.listFiles();
         files = RootDirectoryInstaImage.listFiles();

        if (files!=null) {
            //for (File file : files) {
                for (int i=0;i<files.length;i++){
                    String extension = files[i].getName().substring(files[i].getName().lastIndexOf("."));
                    if (!extension.equals(".mp4")) {
                        fileArrayList.add(files[i]);
                    }
                }
               // fileArrayList.add(file);
            }
            fileListAdapter = new FileListAdapter(activity, fileArrayList, InstaImagesDownloadedFragment.this);
            binding.rvFileList.setAdapter(fileListAdapter);
        }
   // }

    @Override
    public void getPosition(int position, File file) {
        Intent inNext = new Intent(activity, FullViewActivity.class);
        inNext.putExtra("ImageDataFile", fileArrayList);
        inNext.putExtra("Position", position);
        inNext.putExtra("screen", "insta");
        activity.startActivity(inNext);
    }
}