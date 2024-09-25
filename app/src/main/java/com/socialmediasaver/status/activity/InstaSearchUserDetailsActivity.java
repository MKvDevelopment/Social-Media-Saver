package com.socialmediasaver.status.activity;

import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.api.CommonClassForAPI;
import com.socialmediasaver.status.model.InstaUserSearchRaoidModel;
import com.socialmediasaver.status.model.InstagramSearch.InstaSearchUserDetailsModel;
import com.socialmediasaver.status.retrofit.Api;
import com.socialmediasaver.status.retrofit.ApiConstant;
import com.socialmediasaver.status.retrofit.ApiInterface;
import com.socialmediasaver.status.util.SharePrefs;
import com.socialmediasaver.status.util.Utils;
import com.squareup.picasso.Transformation;


import io.reactivex.observers.DisposableObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstaSearchUserDetailsActivity extends AppCompatActivity {
    ImageView story_icon, story_icon1, story_icon2, story_icon3;
    TextView Follow, name, Followers;
    Toolbar insta_toolbar;
    String userName, Name, id;
    //Call<InstaSearchUserDetailsModel> call;
    Call<InstaUserSearchRaoidModel> call;
    LinearLayout mainLayout;
    CommonClassForAPI commonClassForAPI;
    FloatingActionButton floatingActionButton;
    LinearLayout layout, download_image;

    private AdView adView;
    private AdRequest adRequest;

    private InterstitialAd mInterstitialAd;
    private RewardedAd rewardedAd;
    boolean isLoading;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insta_user_search_detail_activity);

        insta_toolbar = findViewById(R.id.insta_toolbar);
        setSupportActionBar(insta_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.name);
        Followers = findViewById(R.id.Followers);
        layout = findViewById(R.id.layout);
        download_image = findViewById(R.id.download_image);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        Follow = findViewById(R.id.Follow);
        story_icon3 = findViewById(R.id.story_icon3);
        story_icon2 = findViewById(R.id.story_icon2);
        story_icon1 = findViewById(R.id.story_icon1);
        story_icon = findViewById(R.id.story_icon);
        mainLayout = findViewById(R.id.mainLayout);
        adView = findViewById(R.id.fullImage_adView);
        userName = getIntent().getStringExtra("userName");
        Name = getIntent().getStringExtra("Name");
        id = getIntent().getStringExtra("id");
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        checksubscription();
        loadRewardedAd();
        fetchUsers(userName, id);
        //callStoriesDetailApi(userName);
    }

    private void checksubscription() {
        //if (Subscription.equals("NO")) {
        if (!SharePrefs.getInstance(InstaSearchUserDetailsActivity.this).getSubscribeValueFromPref()) {
//            Banner banner = (Banner) findViewById(R.id.startAppinstaprofileDetail);
//            banner.setVisibility(View.VISIBLE);
//            banner.showBanner();

            adRequest = new AdRequest.Builder().build();
            bannerInit(getApplicationContext());
            adView.setVisibility(View.VISIBLE);
            adView.loadAd(adRequest);
        } else {
            adView.setVisibility(View.GONE);
        }
    }

    private void callStoriesDetailApi(String UserId) {
        try {
            Utils utils = new Utils(this);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {

                    commonClassForAPI.getDeatil(storyObserver, "__a=1", UserId);


                }
            } else {
                Utils.setToast(this, this
                        .getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private DisposableObserver<InstaSearchUserDetailsModel> storyObserver = new DisposableObserver<InstaSearchUserDetailsModel>() {
        @Override
        public void onNext(InstaSearchUserDetailsModel response) {

            try {

                name.setText(response.getGraphql().getUser().getUsername());
                Glide.with(InstaSearchUserDetailsActivity.this).load(response.getGraphql().getUser().getProfile_pic_url_hd())
                        .thumbnail(0.2f).into(story_icon);
                Glide.with(InstaSearchUserDetailsActivity.this).load(response.getGraphql().getUser().getProfile_pic_url_hd())
                        .thumbnail(0.2f).into(story_icon3);
                Glide.with(InstaSearchUserDetailsActivity.this).load(response.getGraphql().getUser().getProfile_pic_url())
                        .thumbnail(0.2f).into(story_icon);
                Glide.with(InstaSearchUserDetailsActivity.this).load(response.getGraphql().getUser().getProfile_pic_url())
                        .thumbnail(0.2f).into(story_icon);
                Followers.setText(response.getGraphql().getUser().getEdge_followed_by().getCount() + "");
                Follow.setText(response.getGraphql().getUser().getEdge_follow().getCount() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {

        }

        @Override
        public void onComplete() {

        }

    };

//    private void loadInterstial() {
//        StartAppAd startAppAd = new StartAppAd(this);
//        startAppAd.loadAd(StartAppAd.AdMode.VIDEO);
//        startAppAd.showAd();
//    }


    private void loadInterstial() {
        adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getResources().getString(R.string.Interstial_Ad_Id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("InterstitialAd", "onAdLoaded");
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(InstaSearchUserDetailsActivity.this);
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("InterstitialAd", loadAdError.getMessage());

                        mInterstitialAd = null;
                    }
                });
    }

    //    private void fetchUsers(String username, String id) {
//
//        ApiInterface apiInterface;
//        //apiInterface = (new Api().getClient("https://www.instagram.com/" + username + "/", true).create(ApiInterface.class));
//        apiInterface = (new Api().getClient("https://instagram-scraper-2022.p.rapidapi.com/i", true).create(ApiInterface.class));
//
////        call = apiInterface.getusersDetails("ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
////                // commonClassForAPI.getFullDetailFeed_(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
////                + "; sessionid=" + SharePrefs.getInstance(this).getString(SharePrefs.SESSIONID), "1");
//
//        call = apiInterface.getRapidApiUserInfo(id);
//        call.enqueue(new Callback<InstaSearchUserDetailsModel>() {
//            @Override
//            public void onResponse(Call<InstaSearchUserDetailsModel> call, Response<InstaSearchUserDetailsModel> response) {
//
//
//                if (response.code() == 200) {
//
//                    mainLayout.setVisibility(View.VISIBLE);
//                    name.setText(response.body().getGraphql().getUser().getUsername());
//
//
//                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
//                            .override(15, 15)
//                            .thumbnail(0.2f).into(story_icon3);
//                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                            .override(15, 15)
//                            .thumbnail(0.2f).into(story_icon2);
//                    //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);
//
//                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                            //.override(15,15)
//                            .thumbnail(0.2f).into(story_icon1);
//                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                            .thumbnail(0.2f).into(story_icon);
//                    // Picasso.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url()).transform(new Blur(InstaSearchUserDetailsActivity.this, 60)).into(story_icon1);
//                    layout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                            intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                            startActivity(intent);
//                        }
//                    });
//                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                            intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                            startActivity(intent);
//                        }
//                    });
//
//                    Followers.setText(response.body().getGraphql().getUser().getEdge_followed_by().getCount() + "");
//                    Follow.setText(response.body().getGraphql().getUser().getEdge_follow().getCount() + "");
//                    story_icon3.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //if (Subscription.equals("NO")) {
//                            if (!SharePrefs.getInstance(InstaSearchUserDetailsActivity.this).getSubscribeValueFromPref()) {
//                                loadInterstial();
//                            }
//                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));
//                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
//                                    //.override(15,15)
//                                    .thumbnail(0.2f).into(story_icon3);
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon2);
//                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon1);
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
//                                    .thumbnail(0.2f).into(story_icon);
//                            layout.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url_hd());
//                                    startActivity(intent);
//                                }
//                            });
//                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url_hd());
//                                    startActivity(intent);
//                                }
//                            });
//
//
//                        }
//                    });
//                    story_icon2.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            //loadAd();
//
//                          //  if (Subscription.equals("NO")) {
//                            if (!SharePrefs.getInstance(InstaSearchUserDetailsActivity.this).getSubscribeValueFromPref()) {
//                                loadInterstial();
//                            }
//                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon3);
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    // .override(15,15)
//                                    .thumbnail(0.2f).into(story_icon2);
//                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon1);
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .thumbnail(0.2f).into(story_icon);
//                            layout.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                                    startActivity(intent);
//                                }
//                            });
//                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                                    startActivity(intent);
//                                }
//                            });
//
//
//                        }
//                    });
//                    story_icon1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // loadAd();
//                           // if (Subscription.equals("NO")) {
//                            if (!SharePrefs.getInstance(InstaSearchUserDetailsActivity.this).getSubscribeValueFromPref()) {
//                              loadInterstial();
//                            }
//                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));
//                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon3);
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon2);
//                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    //.override(15,15)
//                                    .thumbnail(0.2f).into(story_icon1);
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .thumbnail(0.2f).into(story_icon);
//                            layout.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                                    startActivity(intent);
//                                }
//                            });
//                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                                    startActivity(intent);
//                                }
//                            });
//
//                        }
//                    });
//
//
//                } else {
//                    mainLayout.setVisibility(View.GONE);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<InstaSearchUserDetailsModel> call, Throwable t) {
//                // Log error here since request failed
//
//                Log.d("ERROR",t.toString());
//
//                call.cancel();
//            }
//        });
//    }
    private void fetchUsers(String username, String id) {

        ApiInterface apiInterface;
        //apiInterface = (new Api().getClient("https://www.instagram.com/" + username + "/", true).create(ApiInterface.class));
        //apiInterface = (new Api().getClient("https://instagram-scraper-2022.p.rapidapi.com/", true).create(ApiInterface.class));
        apiInterface = (new Api().getClient(ApiConstant.BASE_URL).create(ApiInterface.class));


//        call = apiInterface.getusersDetails("ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
//                // commonClassForAPI.getFullDetailFeed_(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
//                + "; sessionid=" + SharePrefs.getInstance(this).getString(SharePrefs.SESSIONID), "1");

        call = apiInterface.getRapidApiUserInfo(id);
        call.enqueue(new Callback<InstaUserSearchRaoidModel>() {
            @Override
            public void onResponse(Call<InstaUserSearchRaoidModel> call, Response<InstaUserSearchRaoidModel> response) {

                // Toast.makeText(InstaSearchUserDetailsActivity.this, response.code()+"", Toast.LENGTH_SHORT).show();

                if (response.code() == 200) {


                    mainLayout.setVisibility(View.VISIBLE);
                    name.setText(response.body().getUser().getUsername());


                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_url_info().getUrl())
                            .override(15, 15)
                            .thumbnail(0.2f).into(story_icon3);
                    if (response.body().getUser().getHd_profile_pic_versions() != null) {
                        story_icon2.setVisibility(View.VISIBLE);
                        if (response.body().getUser().getHd_profile_pic_versions().size() > 1) {
                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(1).getUrl())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon2);
                        } else {
                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(0).getUrl())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon2);
                        }
                    } else {
                        story_icon2.setVisibility(View.GONE);
                    }

                    //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);
                    if (response.body().getUser().getHd_profile_pic_versions() != null) {
                        story_icon1.setVisibility(View.VISIBLE);
                        Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(0).getUrl())
                                //.override(15,15)
                                .thumbnail(0.2f).into(story_icon1);
                    } else {
                        story_icon1.setVisibility(View.VISIBLE);
                    }

                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getProfile_pic_url())
                            .thumbnail(0.2f).into(story_icon);
                    // Picasso.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url()).transform(new Blur(InstaSearchUserDetailsActivity.this, 60)).into(story_icon1);
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                            intent.putExtra("url", response.body().getUser().getProfile_pic_url());
                            startActivity(intent);
                        }
                    });
                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                            intent.putExtra("url", response.body().getUser().getProfile_pic_url());
                            startActivity(intent);
                        }
                    });

                    Followers.setText(response.body().getUser().getFollower_count() + "");
                    Follow.setText(response.body().getUser().getFollowing_count() + "");
                    story_icon3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //if (Subscription.equals("NO")) {
                            if (!SharePrefs.getInstance(InstaSearchUserDetailsActivity.this).getSubscribeValueFromPref()) {
                                // loadInterstial();
                                showRewardedVideo();


                            }
                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));
                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_url_info().getUrl())
                                    //.override(15,15)
                                    .thumbnail(0.2f).into(story_icon3);
                            if (response.body().getUser().getHd_profile_pic_versions() != null) {
                                if (response.body().getUser().getHd_profile_pic_versions().size() > 1) {
                                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(1).getUrl())
                                            .override(15, 15)
                                            .thumbnail(0.2f).into(story_icon2);
                                } else {
                                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(0).getUrl())
                                            .override(15, 15)
                                            .thumbnail(0.2f).into(story_icon2);
                                }
                            }

                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(0).getUrl())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon1);
                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getProfile_pic_url())
                                    .thumbnail(0.2f).into(story_icon);
                            layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getUser().getHd_profile_pic_url_info().getUrl());
                                    startActivity(intent);
                                }
                            });
                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getUser().getHd_profile_pic_url_info().getUrl());
                                    startActivity(intent);
                                }
                            });


                        }
                    });
                    story_icon2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //loadAd();

                            //  if (Subscription.equals("NO")) {
                            if (!SharePrefs.getInstance(InstaSearchUserDetailsActivity.this).getSubscribeValueFromPref()) {
                                loadInterstial();
                            }
                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_url_info().getUrl())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon3);
                            if (response.body().getUser().getHd_profile_pic_versions() != null) {
                                if (response.body().getUser().getHd_profile_pic_versions().size() > 1) {
                                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(1).getUrl())
                                            // .override(15,15)
                                            .thumbnail(0.2f).into(story_icon2);
                                } else {
                                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(0).getUrl())
                                            // .override(15,15)
                                            .thumbnail(0.2f).into(story_icon2);
                                }
                            }

                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(0).getUrl())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon1);
                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getProfile_pic_url())
                                    .thumbnail(0.2f).into(story_icon);
                            layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getUser().getHd_profile_pic_versions().get(1).getUrl());
                                    startActivity(intent);
                                }
                            });
                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getUser().getHd_profile_pic_versions().get(1).getUrl());
                                    startActivity(intent);
                                }
                            });


                        }
                    });
                    story_icon1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // loadAd();
                            // if (Subscription.equals("NO")) {
                            if (!SharePrefs.getInstance(InstaSearchUserDetailsActivity.this).getSubscribeValueFromPref()) {
                                loadInterstial();
                            }
                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));
                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));


                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_url_info().getUrl())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon3);
                            if (response.body().getUser().getHd_profile_pic_versions().size() > 1) {
                                Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(1).getUrl())
                                        .override(15, 15)
                                        .thumbnail(0.2f).into(story_icon2);
                            } else {
                                Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(0).getUrl())
                                        .override(15, 15)
                                        .thumbnail(0.2f).into(story_icon2);
                            }

                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getHd_profile_pic_versions().get(0).getUrl())
                                    //.override(15,15)
                                    .thumbnail(0.2f).into(story_icon1);

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getUser().getProfile_pic_url())
                                    .thumbnail(0.2f).into(story_icon);
                            layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getUser().getHd_profile_pic_versions().get(0).getUrl());
                                    startActivity(intent);
                                }
                            });
                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getUser().getHd_profile_pic_versions().get(0).getUrl());
                                    startActivity(intent);
                                }
                            });

                        }
                    });


                } else {
                    mainLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<InstaUserSearchRaoidModel> call, Throwable t) {
                // Log error here since request failed

                Log.d("ERROR", t.toString());

                call.cancel();
            }
        });
    }


//    private void fetchUsers(String username) {
//
//        ApiInterface apiInterface;
//        apiInterface = (new Api().getClient("https://www.instagram.com/" + username + "/", true).create(ApiInterface.class));
//        call = apiInterface.getusersDetails("ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
//                // commonClassForAPI.getFullDetailFeed_(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
//                + "; sessionid=" + SharePrefs.getInstance(this).getString(SharePrefs.SESSIONID), "1");
//        call.enqueue(new Callback<InstaSearchUserDetailsModel>() {
//            @Override
//            public void onResponse(Call<InstaSearchUserDetailsModel> call, Response<InstaSearchUserDetailsModel> response) {
//
//
//                if (response.code() == 200) {
//
//                    mainLayout.setVisibility(View.VISIBLE);
//                    name.setText(response.body().getGraphql().getUser().getUsername());
//
//
//                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
//                            .override(15, 15)
//                            .thumbnail(0.2f).into(story_icon3);
//                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                            .override(15, 15)
//                            .thumbnail(0.2f).into(story_icon2);
//                    //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);
//
//                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                            //.override(15,15)
//                            .thumbnail(0.2f).into(story_icon1);
//                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                            .thumbnail(0.2f).into(story_icon);
//                    // Picasso.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url()).transform(new Blur(InstaSearchUserDetailsActivity.this, 60)).into(story_icon1);
//                    layout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                            intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                            startActivity(intent);
//                        }
//                    });
//                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                            intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                            startActivity(intent);
//                        }
//                    });
//
//                    Followers.setText(response.body().getGraphql().getUser().getEdge_followed_by().getCount() + "");
//                    Follow.setText(response.body().getGraphql().getUser().getEdge_follow().getCount() + "");
//                    story_icon3.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
////                            if (Subscription.equals("YES")){
////
////                            }else {
////                                //  loadAd();
////                                loadRewardedAd();
////                                if (mRewardedAd != null) {
////                                    Activity activityContext = InstaSearchUserDetailsActivity.this;
////                                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
////                                        @Override
////                                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
////                                            // Handle the reward.
////                                            Log.d("TAG", "The user earned the reward.");
////                                            int rewardAmount = rewardItem.getAmount();
////                                            String rewardType = rewardItem.getType();
////                                        }
////                                    });
////                                } else {
////                                    Log.d("TAG", "The rewarded ad wasn't ready yet.");
////                                }
////                            }
//                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));
//                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
//                                    //.override(15,15)
//                                    .thumbnail(0.2f).into(story_icon3);
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon2);
//                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon1);
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
//                                    .thumbnail(0.2f).into(story_icon);
//                            layout.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url_hd());
//                                    startActivity(intent);
//                                }
//                            });
//                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url_hd());
//                                    startActivity(intent);
//                                }
//                            });
//
//
//                        }
//                    });
//                    story_icon2.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
////
////                            loadAd();
////                            if (interstitialAd != null) {
////                                interstitialAd.show(InstaSearchUserDetailsActivity.this);
////                            } else {
////                                Log.d("TAG", "The interstitial ad wasn't ready yet.");
////                            }
////                            if (Subscription.equals("YES")){
////
////                            }else {
////                                loadAd();
////                                if (interstitialAd != null) {
////                                    interstitialAd.show(InstaSearchUserDetailsActivity.this);
////                                } else {
////                                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
////                                }
////                            }
//                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon3);
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    // .override(15,15)
//                                    .thumbnail(0.2f).into(story_icon2);
//                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon1);
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .thumbnail(0.2f).into(story_icon);
//                            layout.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                                    startActivity(intent);
//                                }
//                            });
//                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                                    startActivity(intent);
//                                }
//                            });
//
//
//                        }
//                    });
//                    story_icon1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
////                            loadAd();
////                            if (interstitialAd != null) {
////                                interstitialAd.show(InstaSearchUserDetailsActivity.this);
////                            } else {
////                                Log.d("TAG", "The interstitial ad wasn't ready yet.");
////                            }
////                            if (Subscription.equals("YES")){
////
////                            }else {
////                                loadAd();
////                                if (interstitialAd != null) {
////                                    interstitialAd.show(InstaSearchUserDetailsActivity.this);
////                                } else {
////                                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
////                                }
////                            }
//                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));
//                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
//
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon3);
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .override(15, 15)
//                                    .thumbnail(0.2f).into(story_icon2);
//                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    //.override(15,15)
//                                    .thumbnail(0.2f).into(story_icon1);
//
//                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
//                                    .thumbnail(0.2f).into(story_icon);
//                            layout.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                                    startActivity(intent);
//                                }
//                            });
//                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
//                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
//                                    startActivity(intent);
//                                }
//                            });
//
//                        }
//                    });
//
//
//                } else {
//                    mainLayout.setVisibility(View.GONE);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<InstaSearchUserDetailsModel> call, Throwable t) {
//                // Log error here since request failed
//
//                call.cancel();
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    public class Blur implements Transformation {
        protected static final int UP_LIMIT = 25;
        protected static final int LOW_LIMIT = 1;
        protected final Context context;
        protected final int blurRadius;


        public Blur(Context context, int radius) {
            this.context = context;

            if (radius < LOW_LIMIT) {
                this.blurRadius = LOW_LIMIT;
            } else if (radius > UP_LIMIT) {
                this.blurRadius = UP_LIMIT;
            } else
                this.blurRadius = radius;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            Bitmap sourceBitmap = source;

            Bitmap blurredBitmap;
            blurredBitmap = Bitmap.createBitmap(sourceBitmap);

            RenderScript renderScript = RenderScript.create(context);

            Allocation input = Allocation.createFromBitmap(renderScript,
                    sourceBitmap,
                    Allocation.MipmapControl.MIPMAP_FULL,
                    Allocation.USAGE_SCRIPT);


            Allocation output = Allocation.createTyped(renderScript, input.getType());

            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript,
                    Element.U8_4(renderScript));

            script.setInput(input);
            script.setRadius(blurRadius);

            script.forEach(output);
            output.copyTo(blurredBitmap);

            source.recycle();
            return blurredBitmap;
        }

        @Override
        public String key() {
            return "blurred";
        }
    }


    private void loadRewardedAd() {
        if (rewardedAd == null) {
            isLoading = true;
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(
                    this,
                    getResources().getString(R.string.Rewarded_ad_id),
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            //Log.d(TAG, loadAdError.getMessage());
                            rewardedAd = null;
                            InstaSearchUserDetailsActivity.this.isLoading = false;
                            //Toast.makeText(InstaSearchUserDetailsActivity.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            InstaSearchUserDetailsActivity.this.rewardedAd = rewardedAd;
                            // Log.d(TAG, "onAdLoaded");
                            InstaSearchUserDetailsActivity.this.isLoading = false;
                            //Toast.makeText(InstaSearchUserDetailsActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void showRewardedVideo() {

        if (rewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }
        //showVideoButton.setVisibility(View.INVISIBLE);

        rewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        //Log.d(TAG, "onAdShowedFullScreenContent");
//                        Toast.makeText(InstaSearchUserDetailsActivity.this, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        //  Log.d(TAG, "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;

                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        rewardedAd = null;

                        // Preload the next rewarded ad.
                        InstaSearchUserDetailsActivity.this.loadRewardedAd();
                    }
                });
        Activity activityContext = InstaSearchUserDetailsActivity.this;
        rewardedAd.show(
                activityContext,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("TAG", "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                    }
                });
    }
}
