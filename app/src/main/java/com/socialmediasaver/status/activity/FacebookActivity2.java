package com.socialmediasaver.status.activity;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static com.socialmediasaver.status.util.Utils.Fb_video_link;
import static com.socialmediasaver.status.util.Utils.InAppSubscription;
import static com.socialmediasaver.status.util.Utils.RootDirectoryFacebook;
import static com.socialmediasaver.status.util.Utils.createFileFolder;
import static com.socialmediasaver.status.util.Utils.startDownload;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.api.CommonClassForAPI;
import com.socialmediasaver.status.databinding.ActivityFacebookBinding;
import com.socialmediasaver.status.model.Edge;
import com.socialmediasaver.status.model.InstagramUrlSearchModel;
import com.socialmediasaver.status.retrofit.Api;
import com.socialmediasaver.status.retrofit.ApiConstant;
import com.socialmediasaver.status.retrofit.ApiInterface;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.socialmediasaver.status.util.SharePrefs;
import com.socialmediasaver.status.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacebookActivity2 extends AppCompatActivity {
    ActivityFacebookBinding binding;
    FacebookActivity2 activity;
    CommonClassForAPI commonClassForAPI;
    private String VideoUrl;
    private ClipboardManager clipBoard;
    private NetworkChangeReceiver broadcastReceiver;
   // private BillingClient billingClient;
    Call<InstagramUrlSearchModel> call;
    ApiInterface apiInterface;
    TemplateView my_template;

    private AdRequest adRequest;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_facebook);

        activity = this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = findViewById(R.id.toolbarfb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();
        initViews();

        if (binding.cbWhatsapp.isChecked()) {
            binding.cbWhatsappbusiness.setChecked(false);

        } else if (binding.cbWhatsappbusiness.isChecked()) {
            binding.cbWhatsapp.setChecked(false);
        }



    }


 /*   public void checkIfAlreadySubscribed() {
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(SUBS);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();

                    if (queryPurchases != null && queryPurchases.size() > 0) {
                        SharePrefs.getInstance(FacebookActivity2.this).saveSubscribeValueToPref(true);
                        InAppSubscription = SharePrefs.getInstance(FacebookActivity2.this).getSubscribeValueFromPref();
                        //handlePurchases(queryPurchases);
                        loadbanner();


                    } else {
                        SharePrefs.getInstance(FacebookActivity2.this).saveSubscribeValueToPref(false);
                        InAppSubscription = SharePrefs.getInstance(FacebookActivity2.this).getSubscribeValueFromPref();
                        loadbanner();
                        //Toast.makeText(MainActivity.this, queryPurchases.size()+"error", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(getApplicationContext(), "Service Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }
*/
    private void loadbanner() {
      //  if (Subscription.equals("NO")) {
        if (!SharePrefs.getInstance(FacebookActivity2.this).getSubscribeValueFromPref()) {
//            Mrec mrec = (Mrec) findViewById(R.id.startAppMrecfb);
//            mrec.setVisibility(View.VISIBLE);
//            mrec.showBanner();

            AdLoader adLoader = new AdLoader.Builder(this, getResources().getString(R.string.Native_Ad_ID))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            binding.myTemplate.setVisibility(View.VISIBLE);
                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder().withMainBackgroundColor(new ColorDrawable(getResources().getColor(R.color.black))).build();
                            TemplateView template = findViewById(R.id.my_template);
                            template.setStyles(styles);
                            template.setNativeAd(nativeAd);
                        }

                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }else {
//            Mrec mrec = (Mrec) findViewById(R.id.startAppMrecfb);
//            mrec.setVisibility(View.GONE);

            binding.myTemplate.setVisibility(View.GONE);
        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        assert activity != null;
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        PasteText();
      //  checkIfAlreadySubscribed();

    }

    private void initViews() {
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        binding.loginBtn1.setOnClickListener(v -> {
            String LL = binding.etText.getText().toString();
            if (LL.equals("")) {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            } else if (!Patterns.WEB_URL.matcher(LL).matches()) {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            } else {
               // if (Subscription.equals("NO")) {
                if (!SharePrefs.getInstance(FacebookActivity2.this).getSubscribeValueFromPref()) {
                    MobileAds.initialize(this, new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(InitializationStatus initializationStatus) {}
                    });
                    loadInterstial();
                }
                //GetFacebookData();

                getheURl(binding.etText.getText().toString());
                // showDialog1();
            }
        });

        binding.tvPaste.setOnClickListener(v -> {
            PasteText();
        });
    }


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
                            mInterstitialAd.show(FacebookActivity2.this);
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
    private void showDialog1() {

        LinearLayout layout, layout1, layout2;
        TextView ok, textview2, textview1;
        ImageView close;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.CustomScreenDialogStyle);
        dialogBuilder.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);
        layout = dialogView.findViewById(R.id.layout);
        layout1 = dialogView.findViewById(R.id.layout1);
        layout2 = dialogView.findViewById(R.id.layout2);
        ok = dialogView.findViewById(R.id.ok);
        textview2 = dialogView.findViewById(R.id.textview2);
        textview1 = dialogView.findViewById(R.id.textview1);
        close = dialogView.findViewById(R.id.close);

        layout1.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape2));
        layout2.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape));
        textview1.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.colorPrimary));
        textview2.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.grey));

        layout1.setOnClickListener(v -> {
            layout1.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape2));
            layout2.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape));
            textview1.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.colorPrimary));
            textview2.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.grey));

        });
        layout2.setOnClickListener(v -> {
            layout2.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape2));
            layout1.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape));
            textview1.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.grey));
            textview2.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.colorPrimary));

        });
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();


        ok.setOnClickListener(v -> {
            alertDialog.dismiss();
            //GetFacebookData();
        });
        close.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.setOnDismissListener(dialogInterface -> {

        });
        alertDialog.setOnCancelListener(dialogInterface -> {

        });

    }


    public void getheURl(String s){

        URL address= null;
        try {
            address = new URL(s);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        //Connect & check for the location field
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) address.openConnection(Proxy.NO_PROXY);
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            String expandedURL = connection.getHeaderField("Location");
            if(expandedURL != null) {
                URL expanded = new URL(expandedURL);
                address= expanded;
            }
        } catch (Throwable e) {
            System.out.println("Problem while expanding {}"+ address+ e);
        } finally {
            if(connection != null) {
                try {
                    System.out.println(connection.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Original URL"+address);
        Log.d("Original URL",address+"");
        GetFacebookData(address+"");


    }

    private void GetFacebookData(String s) {
        try {
            createFileFolder();
           // URL url = new URL(binding.etText.getText().toString());
            URL url = new URL(s);
            String host = url.getHost();

//            if (host.contains("facebook.com")) {
//                Utils.showProgressDialog(activity);
//                new FacebookActivity2.callGetFacebookData().execute(binding.etText.getText().toString());
//            } else {
//                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
//            }


           // extract(binding.etText.getText().toString());
            if (binding.etText.getText().toString().contains("https://l.facebook.com/l.php?u=https%3A%2F%2Fwww.instagram.com")) {
                extract(binding.etText.getText().toString());

            }else if (host.contains("facebook.com")){
                Utils.showProgressDialog(activity);
                new FacebookActivity2.callGetFacebookData().execute(binding.etText.getText().toString());
            }else {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void  extract(String s){
        String CurrentString = s;
        String[] separated = CurrentString.split("%2F&h");
        String CurrentString1 = separated[0];
        String[] separated1 = CurrentString1.split("reel%2F");
       // Toast.makeText(FacebookActivity2.this, separated1[0]+"", Toast.LENGTH_SHORT).show();
       // Toast.makeText(FacebookActivity2.this, separated1[1]+"", Toast.LENGTH_SHORT).show();
        Log.d("CHECK1",separated1[0]);
        Log.d("CHECK1",separated1[1]);
        apiInterface = (new Api().getClient(ApiConstant.BASE_URL).create(ApiInterface.class));
        call = apiInterface.getURLVideo(separated1[1]);

        call.enqueue(new Callback<InstagramUrlSearchModel>() {
            @Override
            public void onResponse(Call<InstagramUrlSearchModel> call, Response<InstagramUrlSearchModel> response) {
                Utils.hideProgressDialog(activity);
                if (response.body().getVideo_url()!=null) {
                    InstagramUrlSearchModel root = response.body();
                    if (response.code() == 200) {

//
                        if (root.getEdge_sidecar_to_children() != null) {
                            List<Edge> edgeArrayList = root.getEdge_sidecar_to_children().getEdges();
                            for (int i = 0; i < edgeArrayList.size(); i++) {
                                if (edgeArrayList.get(i).getNode().isIs_video()) {
                                    VideoUrl = edgeArrayList.get(i).getNode().getVideo_url();
                                    //startDownload(VideoUrl, RootDirectoryInstaVideos, activity, getVideoFilenameFromURL(VideoUrl));
                                    startDownload(VideoUrl, RootDirectoryFacebook, activity, getFilenameFromURL(VideoUrl));
                                    binding.etText.setText("");
                                    VideoUrl = "";

                                } else {
//                                    PhotoUrl = edgeArrayList.get(i).getNode().getDisplay_resources().get(edgeArrayList.get(i).getNode().getDisplay_resources().size() - 1).getSrc();
//                                    startDownload(PhotoUrl, RootDirectoryInstaImages, activity, getImageFilenameFromURL(PhotoUrl));
//                                    PhotoUrl = "";
                                    binding.etText.setText("");
                                }
                            }

                        } else {
                            boolean isVideo = root.isIs_video();
                            if (isVideo) {
                                VideoUrl = root.getVideo_url();
                               // startDownload(VideoUrl, RootDirectoryInstaVideos, activity, getVideoFilenameFromURL(VideoUrl));
                                startDownload(VideoUrl, RootDirectoryFacebook, activity, getFilenameFromURL(VideoUrl));

                                VideoUrl = "";
                                binding.etText.setText("");
                            } else {
//                                PhotoUrl = root.getDisplay_url();
//
//                                startDownload(PhotoUrl, RootDirectoryInstaImages, activity, getImageFilenameFromURL(PhotoUrl));
//                                PhotoUrl = "";
                                binding.etText.setText("");
                            }

                        }

                    } else if (response.code() != 200) {
                        Toast.makeText(FacebookActivity2.this, getResources().getString(R.string.insta_API_error), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(FacebookActivity2.this, getResources().getString(R.string.insta_API_error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<InstagramUrlSearchModel> call, Throwable t) {
                // Log error here since request failed
                Utils.hideProgressDialog(activity);
                call.cancel();
            }
        });

    }
    public String getVideoFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath().toString()).getName();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".mp4";
        }
    }

    private void PasteText() {
        try {
            binding.etText.setText("");
            String CopyIntent = getIntent().getStringExtra("CopyIntent");
            if (CopyIntent.equals("")) {
                if (!(clipBoard.hasPrimaryClip())) {

                } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("facebook.com")||clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("fb.watch")) {
                        binding.etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("facebook.com")||item.getText().toString().contains("fb.watch")) {
                        binding.etText.setText(item.getText().toString());
                    }

                }
            } else {
                if (CopyIntent.contains("facebook.com")||CopyIntent.contains("fb.watch")) {
                    binding.etText.setText(CopyIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class callGetFacebookData extends AsyncTask<String, Void, Document> {
        Document facebookDoc;
        Element element;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... urls) {
            try {
                facebookDoc = Jsoup.connect(urls[0]).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return facebookDoc;
        }

        protected void onPostExecute(Document result) {
            Utils.hideProgressDialog(activity);
            try {

                VideoUrl = result.select("meta[property=\"og:video\"]").last().attr("content");
                Log.e("onPostExecute: ", VideoUrl);
                if (!VideoUrl.equals("")) {
                    try {
                        startDownload(VideoUrl, RootDirectoryFacebook, activity, getFilenameFromURL(VideoUrl));
                        VideoUrl = "";
                        binding.etText.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    binding.etText.setText("");
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath()).getName() + ".mp4";
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".mp4";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.how_to_use) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Fb_video_link));
            startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
      binding.etText.setText("");
    }
}
