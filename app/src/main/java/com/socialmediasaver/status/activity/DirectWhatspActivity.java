package com.socialmediasaver.status.activity;

import static com.socialmediasaver.status.util.Utils.InAppSubscription;
import static com.socialmediasaver.status.util.Utils.Subscription;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.hbb20.CountryCodePicker;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.socialmediasaver.status.util.SharePrefs;


public class DirectWhatspActivity extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    private EditText mob, msg;
    private Button btn_send;
    private CheckBox checkBox;
    private boolean checkbox = false;
    private NetworkChangeReceiver broadcastReceiver;
    private RadioGroup group;
    private RadioButton cb_whatsapp, cb_whatsappbusiness;
    String mobile;

    private AdView adView;
    TemplateView my_template;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_whatsp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        adView = findViewById(R.id.direct_adView);
        my_template = findViewById(R.id.my_template);

        checkBox = findViewById(R.id.checkBox);
        countryCodePicker = findViewById(R.id.countrycodepicker);
        group = findViewById(R.id.group);
        cb_whatsapp = findViewById(R.id.cb_whatsapp);
        cb_whatsappbusiness = findViewById(R.id.cb_whatsappbusiness);
        mob = findViewById(R.id.ed_mob);
        msg = findViewById(R.id.editTextTextPersonName);
        btn_send = findViewById(R.id.button);

        PackageManager pm = DirectWhatspActivity.this.getPackageManager();
        boolean isInstalled1 = isPackageInstalled("com.whatsapp", pm);
        boolean isInstalled2 = isPackageInstalled("com.whatsapp.w4b", pm);

//        if(isInstalled1) {
//            cb_whatsapp.setVisibility(View.VISIBLE);
//            cb_whatsapp.setChecked(true);
//            cb_whatsappbusiness.setChecked(false);
//        }
        if (isInstalled1 && isInstalled2) {
            cb_whatsapp.setVisibility(View.VISIBLE);
            cb_whatsappbusiness.setVisibility(View.VISIBLE);
            cb_whatsapp.setChecked(true);
        } else if (isInstalled1) {
            cb_whatsapp.setVisibility(View.VISIBLE);
            cb_whatsappbusiness.setVisibility(View.VISIBLE);
            cb_whatsappbusiness.setClickable(false);
            cb_whatsappbusiness.setAlpha(0.5f);
            cb_whatsapp.setChecked(true);
            cb_whatsappbusiness.setChecked(false);

        } else if (isInstalled2) {
            cb_whatsappbusiness.setVisibility(View.VISIBLE);
            cb_whatsapp.setVisibility(View.VISIBLE);
            cb_whatsapp.setClickable(false);
            cb_whatsapp.setAlpha(0.5f);
            cb_whatsappbusiness.setChecked(true);
            cb_whatsapp.setChecked(false);

        }

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                msg.setVisibility(View.VISIBLE);
                checkbox = true;
            } else {
                checkbox = false;
                msg.setVisibility(View.GONE);
            }
        });

        checksubscription();


        btn_send.setOnClickListener(v -> {


            //countryCodePicker.registerCarrierNumberEditText(mob);
           // String mobile = countryCodePicker.getFullNumber();
            //mob.setText(mobile);

            mobile = countryCodePicker.getSelectedCountryCode() + mob.getText().toString().trim();
            countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                @Override
                public void onCountrySelected() {
                    //Alert.showMessage(RegistrationActivity.this, ccp.getSelectedCountryCodeWithPlus());
                    String selected_country_code = countryCodePicker.getSelectedCountryCode();
                    mobile = selected_country_code + mob.getText().toString().trim();
                    //Toast.makeText(DirectWhatspActivity.this, selected_country_code+"", Toast.LENGTH_SHORT).show();
                }
            });

            String mssg = msg.getText().toString();

            if (mob.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter mobile no.", Toast.LENGTH_SHORT).show();
            } else if (mob.getText().toString().length() < 10) {
                Toast.makeText(this, "Enter valid mobile no.", Toast.LENGTH_SHORT).show();
            } else if (checkbox) {
                if (TextUtils.isEmpty(mssg)) {
                    Toast.makeText(this, "Enter Valid message", Toast.LENGTH_SHORT).show();
                } else {
                    if (cb_whatsapp.isChecked()) {
//                        Uri uri = Uri.parse(mobile +"&text=" + mssg);
//                        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
//                        i.setPackage("com.whatsapp");
//                        startActivity(Intent.createChooser(i, ""));

//                        Intent waIntent = new Intent(Intent.ACTION_SEND);
//                        waIntent.setType("text/plain");
//                        String text = mssg;
//
//                        waIntent.setPackage("com.whatsapp");
//
//                        waIntent.putExtra(Intent.EXTRA_TEXT, text);
//                        startActivity(Intent.createChooser(waIntent, "Share with"));


                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="
                                + mobile
                                + "&text=" + mssg));

                        intent.setPackage("com.whatsapp");

                        startActivity(intent);
                    } else if (cb_whatsappbusiness.isChecked()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="
                                + mobile
                                + "&text=" + mssg));
                        intent.setPackage("com.whatsapp.w4b");
                        startActivity(intent);

//                        Uri uri = Uri.parse(mobile +"&text=" + mssg);
//                        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
//                        i.setPackage("com.whatsapp.w4b");
//                        startActivity(Intent.createChooser(i, ""));

//                        Intent waIntent = new Intent(Intent.ACTION_SEND);
//                        waIntent.setType("text/plain");
//                        String text = mssg;
//
//                        waIntent.setPackage("com.whatsapp.w4b");
//
//                        waIntent.putExtra(Intent.EXTRA_TEXT, text);
//                        startActivity(Intent.createChooser(waIntent, "Share with"));
                    }

                }

            } else {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + mobile));
//                startActivity(intent);

                if (cb_whatsapp.isChecked()) {
//                    Uri uri = Uri.parse(mobile);
//                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
//                    i.setPackage("com.whatsapp");
//                    startActivity(Intent.createChooser(i, ""));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + mobile));

                    // Intent waIntent = new Intent(Intent.ACTION_SEND);
                    //  waIntent.setType("text/plain");
                    //  String text = mssg;

                    intent.setPackage("com.whatsapp");


                    startActivity(Intent.createChooser(intent, "Share with"));


//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="
//                                + mobile
//                                + "&text=" + mssg));
//                        startActivity(intent);
                } else if (cb_whatsappbusiness.isChecked()) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="
//                                + mobile
//                                + "&text=" + mssg));
//                        startActivity(intent);

//                    Uri uri = Uri.parse(mobile);
//                    Intent i = new Intent(Intent.ACTION_SENDTO, uri);
//                    i.setPackage("com.whatsapp.w4b");
//                    startActivity(Intent.createChooser(i, ""));

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="
                            + mobile
                    ));
                    //Intent waIntent = new Intent(Intent.ACTION_SEND);
                    // waIntent.setType("text/plain");
                    //  String text = mssg;

                    intent.setPackage("com.whatsapp.w4b");


                    startActivity(Intent.createChooser(intent, "Share with"));
                }

            }
        });
    }


    public void onCountryPickerClick(View view) {
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //Alert.showMessage(RegistrationActivity.this, ccp.getSelectedCountryCodeWithPlus());
                 String selected_country_code = countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });
    }
//
//    private void checksubscription() {
//        //if (Subscription.equals("NO")) {
//        if (!SharePrefs.getInstance(DirectWhatspActivity.this).getSubscribeValueFromPref()) {
//            Cover mrec = (Cover) findViewById(R.id.startAppMrecdirect);
//            mrec.setVisibility(View.VISIBLE);
//            mrec.showBanner();
//        }else {
//            Cover mrec = (Cover) findViewById(R.id.startAppMrecdirect);
//            mrec.setVisibility(View.GONE);
//        }
//    }



    private void checksubscription() {
        if (!SharePrefs.getInstance(DirectWhatspActivity.this).getSubscribeValueFromPref())
        // if (SharePrefs.getInstance(DirectWhatspActivity.this).getSubscribeValueFromPref())
        {
            AdRequest adRequest = new AdRequest.Builder().build();
//            adView.loadAd(adRequest);
//            adView.setVisibility(View.GONE);

            //my_template.setVisibility(View.VISIBLE);
            AdLoader adLoader = new AdLoader.Builder(this, getResources().getString(R.string.Native_Ad_ID))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            if (nativeAd!=null) {
                                my_template.setVisibility(View.VISIBLE);
                                NativeTemplateStyle styles = new
                                        NativeTemplateStyle.Builder().withMainBackgroundColor(new ColorDrawable(getResources().getColor(R.color.black))).build();
                                TemplateView template = findViewById(R.id.my_template);
                                template.setStyles(styles);
                                template.setNativeAd(nativeAd);

                            }else {
                                my_template.setVisibility(View.GONE);
                            }

                        }

                    })
                    .build();


            adLoader.loadAd(new AdRequest.Builder().build());
        } else {
            //adView.setVisibility(View.GONE);
            my_template.setVisibility(View.GONE);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, packageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}