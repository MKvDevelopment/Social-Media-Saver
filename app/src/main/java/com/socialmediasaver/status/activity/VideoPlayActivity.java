package com.socialmediasaver.status.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.model.WhatsappStatusModel;
import com.socialmediasaver.status.util.NetworkChangeReceiver;

import com.socialmediasaver.status.util.SharePrefs;

import static com.socialmediasaver.status.util.Utils.Subscription;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView simpleVideoView;
    private WhatsappStatusModel fileItem;

    private MediaController mediaController;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        String url = getIntent().getStringExtra("url");



        mediaController = new MediaController(this);
        Uri uri = Uri.parse(url);
        simpleVideoView = findViewById(R.id.videoview2); // initiate a video view
        simpleVideoView.setVideoURI(uri);
        mediaController.setAnchorView(simpleVideoView);
        simpleVideoView.setMediaController(mediaController);
        simpleVideoView.start();

        //checksubscription();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}