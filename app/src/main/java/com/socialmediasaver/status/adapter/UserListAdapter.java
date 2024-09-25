package com.socialmediasaver.status.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.interfaces.UserListInterface;
import com.socialmediasaver.status.model.story.TrayModel;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<TrayModel> trayModelArrayList;
    private UserListInterface userListInterface;

    public UserListAdapter(Context context, ArrayList<TrayModel> list, UserListInterface listInterface) {
        this.context = context;
        this.trayModelArrayList = list;
        this.userListInterface = listInterface;
        for (int i = 0; i < trayModelArrayList.size(); i++) {
            if (trayModelArrayList.get(i).getUser() == null) {
                trayModelArrayList.remove(i);
            }
        }
    }

    @NonNull
    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        //return new ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.item_user_list, viewGroup, false));
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_list, viewGroup, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.ViewHolder viewHolder, int position) {
        if (trayModelArrayList.get(position).getUser() != null) {
            viewHolder.RLStoryLayout.setVisibility(View.VISIBLE);
            viewHolder.real_name.setVisibility(View.VISIBLE);
            viewHolder.story_icon.setVisibility(View.VISIBLE);
            viewHolder.real_name.setText(trayModelArrayList.get(position).getUser().getFull_name());
            Glide.with(context).load(trayModelArrayList.get(position).getUser().getProfile_pic_url())
                    .thumbnail(0.2f).into(viewHolder.story_icon);

            viewHolder.RLStoryLayout.setOnClickListener(view ->
                 userListInterface.userListClick(position, trayModelArrayList.get(position)));
        } else {
            viewHolder.RLStoryLayout.setVisibility(View.GONE);
            viewHolder.real_name.setVisibility(View.GONE);
            viewHolder.story_icon.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return trayModelArrayList == null ? 0 : trayModelArrayList.size();
    }

    //    public class ViewHolder extends RecyclerView.ViewHolder {
//         ItemUserListBinding binding;
//        public ViewHolder(ItemUserListBinding mbinding) {
//            super(mbinding.getRoot());
//            this.binding = mbinding;
//        }
//    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView real_name;
        ImageView story_icon;
        RelativeLayout RLStoryLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            real_name = itemView.findViewById(R.id.real_name);
            story_icon = itemView.findViewById(R.id.story_icon);
            RLStoryLayout = itemView.findViewById(R.id.RLStoryLayout);
        }
    }
}