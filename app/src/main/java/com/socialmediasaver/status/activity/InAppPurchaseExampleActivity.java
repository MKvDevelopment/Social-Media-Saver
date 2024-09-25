package com.socialmediasaver.status.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.util.SharePrefs;

public class InAppPurchaseExampleActivity extends AppCompatActivity {
    public static final String PREF_FILE = "MyPref";
    public static final String SUBSCRIBE_KEY = "subscribe";
    public static final String ITEM_SKU_SUBSCRIBE = "socialmediasaversixmonth";
    public static final String ITEM_SKU_SUBSCRIBE_one = "socialmediasaveronemonth";
    public static final String ITEM_SKU_SUBSCRIBE_1YEAR = "socialmediasaveroneyear";
    public static final String ITEM_SKU_SUBSCRIBE_3Months = "socialmediasaverthreemonth";
    private TextView premiumContent, subscriptionStatus;
    private Button subscribe;
   // private BillingClient billingClient;
    private RecyclerView rv;
   // private BillingFlowParams flowParams;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient signInClient;
    private SignInButton signInButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    private String email;
    private boolean mobile;
    private GoogleSignInAccount account;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_app_purchase_example);

        Toolbar toolbar = findViewById(R.id.toolbar_remove_ads);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        signInButton = findViewById(R.id.google);
        premiumContent = (TextView) findViewById(R.id.premium_content);
        subscriptionStatus = (TextView) findViewById(R.id.subscription_status);
        subscribe = (Button) findViewById(R.id.subscribe);
        rv = (RecyclerView) findViewById(R.id.rv);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");



     /*   billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(SUBS);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    //Toast.makeText(InAppPurchaseExampleActivity.this, queryPurchase.getPurchasesList().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("PURCHASE", queryPurchase.getPurchasesList().toString());


                    if (queryPurchases != null && queryPurchases.size() > 0) {
                        //Log.d("CHECk",account.toString());
                        // if (account.getEmail()!=null)
                        for (int i = 0; i < queryPurchase.getPurchasesList().size(); i++) {
                            Log.d("PURCHASE_", queryPurchase.getPurchasesList().get(i).getSku());
                            initialize(queryPurchase.getPurchasesList().get(i).getSku());

                        }
                        handlePurchases(queryPurchases);
//                        if (!SharePrefs.getInstance(InAppPurchaseExampleActivity.this).getAccount().equalsIgnoreCase("")) {
//
//                            handlePurchases(queryPurchases);
//                        }


                        //SharePrefs.getInstance(InAppPurchaseExampleActivity.this).saveSubscribeValueToPref(true);

                        //firebaseAuthWithGoogle(account);
                    }
                    //if no item in purchase list means subscription is not subscribed
                    //Or subscription is cancelled and not renewed for next month
                    // so update pref in both cases
                    // so next time on app launch our premium content will be locked again
                    else {
                        initialize("");
                        SharePrefs.getInstance(InAppPurchaseExampleActivity.this).saveSubscribeValueToPref(false);


                        InAppSubscription = SharePrefs.getInstance(InAppPurchaseExampleActivity.this).getSubscribeValueFromPref();

                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(getApplicationContext(), "Service Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
*/
        //initialize("");
        //item subscribed
        if(SharePrefs.getInstance(this).getSubscribeValueFromPref()){
            subscribe.setVisibility(View.GONE);
            premiumContent.setVisibility(View.GONE);
            subscriptionStatus.setText("Subscription Status : Subscribed");
        }
//item not subscribed
        else{
            premiumContent.setVisibility(View.GONE);
            subscribe.setVisibility(View.VISIBLE);
            subscriptionStatus.setText("Subscription Status : Not Subscribed");
        }


    }


/*
    //initiate purchase on button click
    //  public void subscribe(View view) {
    public void initialize(String id) {

//check if service is already connected
        if (billingClient.isReady()) {
            initiatePurchase(id);
        }
//else reconnect service
        else {
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase(id);
                    } else {
                        Toast.makeText(getApplicationContext(), "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Toast.makeText(getApplicationContext(), "Service Disconnected ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void initiatePurchase(String id) {
        List<String> skuList = new ArrayList<>();
        skuList.add("socialmediasaveronemonth");
        skuList.add("socialmediasaverthreemonth");
        skuList.add(ITEM_SKU_SUBSCRIBE);
        skuList.add("socialmediasaveroneyear");

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(SUBS);

        BillingResult billingResult = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            billingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult,
                                                         List<SkuDetails> skuDetailsList) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                if (skuDetailsList != null && skuDetailsList.size() > 0) {

//                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
//                                            .setSkuDetails(skuDetailsList.get(0))
//                                            .build();
                                    Log.d("SKULIST", skuDetailsList.toString());
                                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(InAppPurchaseExampleActivity.this, 1);
                                    rv.setLayoutManager(layoutManager);
                                    SubscriptionListAdapter adapter = new SubscriptionListAdapter(InAppPurchaseExampleActivity.this, skuDetailsList, id, new OnSubscriptionUpdated() {


                                        @Override
                                        public void onSubscribe(int position, List<SkuDetails> skuDetailsList_) {
                                            flowParams = BillingFlowParams.newBuilder()
                                                    .setSkuDetails(skuDetailsList_.get(position))
                                                    .build();
                                            //Toast.makeText(InAppPurchaseExampleActivity.this, skuDetailsList_.get(position).getPrice()+"", Toast.LENGTH_SHORT).show();
                                            subscribe.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Log.i("SUBSCRIPTION", skuDetailsList_.get(position).getPrice());
                                                    billingClient.launchBillingFlow(InAppPurchaseExampleActivity.this, flowParams);

                                                }
                                            });
                                        }
                                    });
                                    rv.setAdapter(adapter);
                                    // billingClient.launchBillingFlow(InAppPurchaseExampleActivity.this, flowParams);
                                } else {
//try to add subscription item "sub_example" in google play console
                                    Toast.makeText(getApplicationContext(), "Item not Found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry Subscription not Supported. Please Update Play Store", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
//if item subscribed
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        }
//if item already subscribed then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(SUBS);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if (alreadyPurchases != null) {
                handlePurchases(alreadyPurchases);
            }
        }
//if Purchase canceled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(getApplicationContext(), "Purchase Canceled", Toast.LENGTH_SHORT).show();
        }
// Handle any other error msgs
        else {
            Toast.makeText(getApplicationContext(), "Error " + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
        }
    }

    void handlePurchases(List<Purchase> purchases) {
        Log.d("INITIALPURCHASE", purchases.toString().toString());

        for (Purchase purchase : purchases) {

//if item is purchased
            // if (ITEM_SKU_SUBSCRIBE.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
// Invalid purchase
// show error to user
                    Toast.makeText(getApplicationContext(), "Error : invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }
// else purchase is valid
//if item is purchased and not acknowledged
                if (!purchase.isAcknowledged()) {

                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase);
                }
//else item is purchased and also acknowledged
                else {
// Grant entitlement to the user on item purchase
// restart activity
                    //Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();

                    if (!SharePrefs.getInstance(this).getSubscribeValueFromPref()) {
                        SharePrefs.getInstance(this).saveSubscribeValueToPref(true);
                       // firebaseAuthWithGoogle(account);
                        InAppSubscription = SharePrefs.getInstance(InAppPurchaseExampleActivity.this).getSubscribeValueFromPref();
                        Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                        this.recreate();
                    }
                }
            }
//if purchase is pending
            // else if (ITEM_SKU_SUBSCRIBE.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                Toast.makeText(getApplicationContext(),
                        "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
            }
//if purchase is unknown mark false
            //else if (ITEM_SKU_SUBSCRIBE.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            else if (purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                SharePrefs.getInstance(this).saveSubscribeValueToPref(false);
                InAppSubscription = SharePrefs.getInstance(InAppPurchaseExampleActivity.this).getSubscribeValueFromPref();

                premiumContent.setVisibility(View.GONE);
                subscribe.setVisibility(View.VISIBLE);
                subscriptionStatus.setText("Subscription Status : Not Subscribed");
                Toast.makeText(getApplicationContext(), "Purchase Status Unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }

    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//if purchase is acknowledged
// Grant entitlement to the user. and restart activity
                SharePrefs.getInstance(InAppPurchaseExampleActivity.this).saveSubscribeValueToPref(true);
                //firebaseAuthWithGoogle(account);
                InAppSubscription = SharePrefs.getInstance(InAppPurchaseExampleActivity.this).getSubscribeValueFromPref();

                InAppPurchaseExampleActivity.this.recreate();
            }
        }
    };

    private boolean verifyValidSignature(String signedData, String signature) {
        try {
// To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmen//16zj0/nODUTcucniEIqc9mjTwYqeVI0dt8dTgFjE0ltuPs/RA6SbynnuUqjYtjS7pl3Uz7O7z6SPSYE5UkiEBimCNi5GOjYL77x3wWx7WouT1Isdp+QQnc3ms2AArs2AW+PuPL1A3VNm5Hsr1Qe5YKSL622/jBmw5m+vChwgYAfWXmj9QIo9dWuQnQuYFLt8lRhYc0jkCbX1zf0Ld3EBMcCwCkSxDcC3kmXWjkIWPSZea18SSziLENVFshb1N2CjOUPuEUzKK8J/BqTwffm63RsXPCel72cvlFPBodxBFPjMbfrwpWbOfupx3t/3ZX+ZZnBj25Ci+Mc0ivgRQIDAQAB";
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }*/



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
