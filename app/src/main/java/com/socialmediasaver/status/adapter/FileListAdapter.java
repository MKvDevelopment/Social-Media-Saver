package com.socialmediasaver.status.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.databinding.ItemsFileViewBinding;
import com.socialmediasaver.status.interfaces.FileListClickInterface;

import java.io.File;
import java.util.ArrayList;


public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<File> fileArrayList;
    private LayoutInflater layoutInflater;
    private FileListClickInterface fileListClickInterface;
    private MediaController mediaController;

    public FileListAdapter(Context context, ArrayList<File> files, FileListClickInterface fileListClickInterface) {
        this.context = context;
        this.fileArrayList = files;
        this.fileListClickInterface = fileListClickInterface;
    }

    @NonNull
    @Override
    public FileListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        return new ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.items_file_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FileListAdapter.ViewHolder viewHolder, int i) {
        File fileItem = fileArrayList.get(i);

        try {
            String extension = fileItem.getName().substring(fileItem.getName().lastIndexOf("."));
            if (extension.equals(".mp4")) {
                viewHolder.mbinding.ivPlay.setVisibility(View.VISIBLE);
//                viewHolder.mbinding.pc.setVisibility(View.GONE);
//                viewHolder.mbinding.videoview2.setVisibility(View.VISIBLE);
//                mediaController = new MediaController(context);
//                Uri uri = Uri.parse(fileArrayList.get(i).getPath());
//                viewHolder.mbinding.videoview2.setVideoURI(uri);
//                mediaController.setAnchorView(viewHolder.mbinding.videoview2);
//                viewHolder.mbinding.videoview2.setMediaController(mediaController);
//                viewHolder.mbinding.videoview2.start();
            } else {
                viewHolder.mbinding.ivPlay.setVisibility(View.GONE);
                //viewHolder.mbinding.pc.setVisibility(View.VISIBLE);
               // viewHolder.mbinding.videoview2.setVisibility(View.GONE);
            }
            Glide.with(context)
                    .load(fileItem.getPath())
                    .into(viewHolder.mbinding.pc);
        } catch (Exception ex) {

        }
        viewHolder.mbinding.rlMain.setOnClickListener(v ->
                fileListClickInterface.getPosition(i, fileItem));
    }


    @Override
    public int getItemCount() {
        return fileArrayList == null ? 0 : fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemsFileViewBinding mbinding;


        public ViewHolder(ItemsFileViewBinding mbinding) {
            super(mbinding.getRoot());
            this.mbinding = mbinding;
        }
    }
}