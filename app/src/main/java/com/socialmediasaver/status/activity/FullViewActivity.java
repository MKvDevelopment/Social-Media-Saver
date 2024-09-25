package com.socialmediasaver.status.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.documentfile.provider.DocumentFile;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.adapter.DownloadedPagerAdapter;
import com.socialmediasaver.status.databinding.ActivityFullViewBinding;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.socialmediasaver.status.util.SharePrefs;
import com.socialmediasaver.status.util.Utils;



import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.socialmediasaver.status.util.Utils.InAppSubscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;
import static com.socialmediasaver.status.util.Utils.shareImage;
import static com.socialmediasaver.status.util.Utils.shareImageVideoOnWhatsapp;
import static com.socialmediasaver.status.util.Utils.shareVideo;

public class FullViewActivity extends AppCompatActivity {
    private ActivityFullViewBinding binding;
    private FullViewActivity activity;
    private ArrayList<File> fileArrayList;
    private int Position = -1;
    //ShowImagesAdapter showImagesAdapter;
    DownloadedPagerAdapter showImagesAdapter;
    private NetworkChangeReceiver broadcastReceiver;
    int position_increment = -1;
    String screen;
    String UriPath;
    boolean b;
    String messsage="";
    private AdRequest adRequest;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_view);

        activity = this;

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
        screen = getIntent().getStringExtra("screen");


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileArrayList = (ArrayList<File>) getIntent().getSerializableExtra("ImageDataFile");
            Position = getIntent().getIntExtra("Position", 0);
        }
        initViews();

    }

    private void checksubscription() {
        //if (Subscription.equals("NO")) {
        if (!SharePrefs.getInstance(FullViewActivity.this).getSubscribeValueFromPref()) {
            //Banner banner = (Banner) findViewById(R.id.startAppfullView);
             //AdView adView = findViewById(R.id.video_adView);
//            banner.setVisibility(View.VISIBLE);
//            banner.showBanner();
            AdView adView = findViewById(R.id.video_adView);
            adRequest = new AdRequest.Builder().build();
            bannerInit(getApplicationContext());
            adView.setVisibility(View.VISIBLE);
            adView.loadAd(adRequest);

        } else {
            AdView adView = findViewById(R.id.video_adView);
//            Banner banner = (Banner) findViewById(R.id.startAppfullView);
//            banner.setVisibility(View.GONE);
            adView.setVisibility(View.GONE);
        }



    }

    public void initViews() {
        showImagesAdapter = new DownloadedPagerAdapter(getSupportFragmentManager(), fileArrayList);
        binding.vpView.setAdapter(showImagesAdapter);
        binding.vpView.setCurrentItem(Position);

        binding.vpView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (position_increment < arg0)
                    position_increment = arg0;
                else return;
                if (Position != arg0)
                    Position = arg0;
                System.out.println("Current position==" + Position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int num) {
            }
        });

        binding.imDelete.setOnClickListener(view -> {
            // if (Subscription.equals("NO"))
            if (!SharePrefs.getInstance(FullViewActivity.this).getSubscribeValueFromPref()) {
                loadInterstial();
            }
            AlertDialog.Builder ab = new AlertDialog.Builder(activity);

//            if (!b){
//                messsage = getResources().getString(R.string.do_u_want_to_dlt_android11);
//
//            }
            ab.setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
//                Log.d("DELETE1",fileArrayList.get(Position).getAbsolutePath());
//                File tempFile=new File(fileArrayList.get(Position).getPath());
//                long mediaID=getFilePathToMediaID(tempFile.getAbsolutePath(),  FullViewActivity.this);
//                Log.d("DELETE1",mediaID+"");
//                try {
//                    DocumentsContract.deleteDocument(FullViewActivity.this.getContentResolver(), Uri.parse(fileArrayList.get(Position).getPath()));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }

                b = fileArrayList.get(Position).delete();

                if (b) {
//if (fileArrayList.size()!=0)
                    deleteFileAA(Position);
                   // messsage = "";


                } else {
                    Toast.makeText(FullViewActivity.this, getResources().getString(R.string.do_u_want_to_dlt_android11), Toast.LENGTH_LONG).show();

                    if (fileArrayList.get(Position).getAbsolutePath().contains("mp4")) {
                        Log.d("LIST1", fileArrayList.get(Position).getAbsolutePath());
                        if (screen.equalsIgnoreCase("whatsapp")) {
                            askPermissionsAndroid30BusinessVideo("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FWhatsapp_Videos");
                        } else if (screen.equalsIgnoreCase("insta")) {
                            askPermissionsAndroid30BusinessVideo("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FInsta_Videos");

                        } else if (screen.equalsIgnoreCase("insta_stories")) {
                            askPermissionsAndroid30BusinessVideo("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FInsta_Stories");

                        }else if (screen.equalsIgnoreCase("fb")) {
                            askPermissionsAndroid30BusinessVideo("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FFacebook");

                        } else {
                            askPermissionsAndroid30BusinessVideo("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FTwitter");

                        }

                    } else {
                        Log.d("LIST1", fileArrayList.get(Position) + "");
                        if (screen.equalsIgnoreCase("whatsapp")) {
                            askPermissionsAndroid30Business("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FWhatsapp_Images");
                            //askPermissionsAndroid30Business("content://com.android.externalstorage.documents/tree/primary%3ADownload%");
                        } else if (screen.equalsIgnoreCase("insta")) {
                            askPermissionsAndroid30Business("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FInsta_Images");

                        } else if (screen.equalsIgnoreCase("insta_stories")) {
                            askPermissionsAndroid30BusinessVideo("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FInsta_Stories");

                        }else if (screen.equalsIgnoreCase("insta_profile_pic")) {
                            askPermissionsAndroid30BusinessVideo("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FInsta_Profile_Picture");

                        }else if (screen.equalsIgnoreCase("fb")) {
                            askPermissionsAndroid30Business("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FFacebook");

                        } else {
                            askPermissionsAndroid30Business("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FTwitter");

                        }
                        //askPermissionsAndroid30Business();
                    }

                    //askPermissionsAndroid30();
                }
            });
            ab.setNegativeButton(getResources().getString(R.string.no), (dialog, id) ->
                    dialog.cancel());
            AlertDialog alert = ab.create();
            // boolean b = fileArrayList.get(Position).delete();
            //alert.setTitle(getResources().getString(R.string.do_u_want_to_dlt));
            alert.setTitle(getResources().getString(R.string.do_u_want_to_dlt));
            //alert.setMessage(messsage);

//            if (!b){
//                alert.setMessage(getResources().getString(R.string.do_u_want_to_dlt_android11));
//
//            }
            alert.show();
        });

        binding.imShare.setOnClickListener(view -> {
            if (fileArrayList.get(Position).getName().contains(".mp4")) {
                shareVideo(activity, fileArrayList.get(Position).getPath());
            } else {
                shareImage(activity, fileArrayList.get(Position).getPath());
            }
        });
        binding.imWhatsappShare.setOnClickListener(view -> {
            if (fileArrayList.get(Position).getName().contains(".mp4")) {
                shareImageVideoOnWhatsapp(activity, fileArrayList.get(Position).getPath(), true);
            } else {
                shareImageVideoOnWhatsapp(activity, fileArrayList.get(Position).getPath(), false);
            }
        });

    }

//    private void loadInterstial() {
//        StartAppAd startAppAd = new StartAppAd(this);
//        startAppAd.loadAd(StartAppAd.AdMode.FULLPAGE);
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
                            mInterstitialAd.show(FullViewActivity.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        checksubscription();
    }

    public void deleteFileAA(int position) {
        fileArrayList.remove(position);
        if (fileArrayList.size() != 0) {
            if (fileArrayList.size() > Position) {
                Position = Position;
            } else {
                Position = 0;
            }
        }

        if (fileArrayList.size() != 0)
            initViews();

        // showImagesAdapter.notifyDataSetChanged();
        Utils.setToast(activity, getResources().getString(R.string.file_deleted));
        if (fileArrayList.size() == 0) {
            onBackPressed();
        }

    }

    public long getFilePathToMediaID(String songPath, FullViewActivity context) {
        long id = 0;
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Audio.Media.DATA;
        String[] selectionArgs = {songPath};
        String[] projection = {MediaStore.Audio.Media._ID};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
            }
        }

        return id;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public void askPermissionsAndroid30Business(String s) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FWhatsapp_Images");
        Uri uri = Uri.parse(s);
        DocumentFile file = DocumentFile.fromTreeUri(FullViewActivity.this, uri);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, file.getUri());
        // Filter to show only application/pdf, using the image MIME data type.
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent);
//        if (activity != null) {
//            activity.startActivityForResult(intent, 2297);
//        }
    }

    public void askPermissionsAndroid30BusinessVideo(String s) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FWhatsapp_Videos");
        Uri uri = Uri.parse(s);
        DocumentFile file = DocumentFile.fromTreeUri(FullViewActivity.this, uri);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, file.getUri());
        // Filter to show only application/pdf, using the image MIME data type.
        intent.setType("video/*");
        someActivityResultLauncher.launch(intent);
//        if (activity != null) {
//            activity.startActivityForResult(intent, 2297);
//        }
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        Log.d("DATA2", data.getData() + "");
                        try {
                            DocumentsContract.deleteDocument(FullViewActivity.this.getContentResolver(), data.getData());
                            deleteFileAA(Position);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        boolean b = fileArrayList.get(Position).delete();

                        if (b) {
//if (fileArrayList.size()!=0)
                            deleteFileAA(Position);


                        }
                    }
                }
            });


    public void askPermissionsAndroid30() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        DocumentFile f = DocumentFile.fromFile(getExternalFilesDir("/media"));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ADownload%2FSocial_Media_Saver%2FWhatsapp_Images");
        DocumentFile file = DocumentFile.fromTreeUri(FullViewActivity.this, uri);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, file.getUri());
        startActivityForResult(intent, 2296);


    }


    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Toast.makeText(FullViewActivity.this, requestCode+"", Toast.LENGTH_SHORT).show();
        if (requestCode == 2297) {
            Log.d("TAG1", data.getData() + "");
            Uri treeUri = data.getData();
            // Toast.makeText(FullViewActivity.this, data.getData()+"", Toast.LENGTH_SHORT).show();
            Log.d("DATA", data.getData() + "");
            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            this.getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
            Log.d("TAG2", takeFlags + "");

            Uri myUri = Uri.parse(data.getData() + "%2FSocial_Media_Saver%2FWhatsapp_Images");
            DocumentFile documentFile = DocumentFile.fromTreeUri(this, myUri);

            for (DocumentFile file : documentFile.listFiles()) {
                if (file.isDirectory()) { // if it is sub directory
                    // Do stuff with sub directory
                    Log.d("yo", file.getUri() + "\n");
                    try {
                        DocumentsContract.deleteDocument(FullViewActivity.this.getContentResolver(), data.getData());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {
                    // Do stuff with normal file
                }
                Log.d("Uri", file.getUri() + "\n");
            }
            boolean b = fileArrayList.get(Position).delete();

            if (b) {
//if (fileArrayList.size()!=0)
                deleteFileAA(Position);


            }
        }
    }
}


