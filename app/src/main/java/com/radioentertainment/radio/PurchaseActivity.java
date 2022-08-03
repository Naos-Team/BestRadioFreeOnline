package com.radioentertainment.radio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.radioentertainment.utils.SharedPref;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PurchaseActivity extends AppCompatActivity {
    BillingClient billingClient;
    SharedPref sharedPref;
    ImageView ic_back, item1, item2, item3, iv_check1 ,iv_check2, iv_check3;
    TextView tv_item1_title, tv_item1_price, tv_title;
    TextView tv_item2_title, tv_item2_price;
    TextView tv_item3_title, tv_item3_price;

    private final String ONE_MONTH_SUBS = "subs_monthly";
    private final String THREE_MONTHS_SUBS = "subs_3month";
    private final String ONE_YEAR_SUBS = "subs_yearly";

    protected void onResume() {
        super.onResume();
        checkUserCurrentPurchase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        ic_back = findViewById(R.id.ic_back);
        item1 = findViewById(R.id.item1);
        item2 = findViewById(R.id.item2);
        item3 = findViewById(R.id.item3);
        iv_check1 = findViewById(R.id.iv_check1);
        iv_check2 = findViewById(R.id.iv_check2);
        iv_check3 = findViewById(R.id.iv_check3);
        tv_item1_title = findViewById(R.id.tv_item1_title);
        tv_item2_title = findViewById(R.id.tv_item2_title);
        tv_item3_title = findViewById(R.id.tv_item3_title);
        tv_item1_price = findViewById(R.id.tv_item1_price);
        tv_item2_price = findViewById(R.id.tv_item2_price);
        tv_item3_price = findViewById(R.id.tv_item3_price);
        tv_title = findViewById(R.id.tv_title);


        sharedPref = new SharedPref(this);

        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (Purchase purchase : purchases) {
                        verifySubPurchase(purchase);
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                    Toast.makeText(PurchaseActivity.this, "You own this subscription!", Toast.LENGTH_SHORT).show();
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                    Toast.makeText(PurchaseActivity.this, "Something went wrong while process the purchase! Please try again!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(purchasesUpdatedListener)
                .build();

        establishConnection();

    }

    void establishConnection() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    checkUserCurrentPurchase();

                    showProducts();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection();
            }
        });
    }

    private void checkUserCurrentPurchase() {

        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                        boolean isPremium = false;

                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {

                                verifySubPurchase(purchase);
                                isPremium = true;

                            } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged()) {
                                try {
                                    JSONObject json = new JSONObject(purchase.getOriginalJson());
                                    String product_id = json.getString("productId");

                                    this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (product_id.equals(ONE_MONTH_SUBS)) {
                                                iv_check1.setVisibility(View.VISIBLE);
                                            } else if (product_id.equals(THREE_MONTHS_SUBS)) {
                                                iv_check2.setVisibility(View.VISIBLE);
                                            } else if (product_id.equals(ONE_YEAR_SUBS)) {
                                                iv_check3.setVisibility(View.VISIBLE);
                                            }

                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                isPremium = true;
                            }
                        }

                        setPremiumUI(isPremium);
                        sharedPref.setIsPremium(isPremium);

                    }


                }
        );
    }

    private void setPremiumUI(boolean isPremium) {

        if (isPremium) {
            tv_title.setText("You are PREMIUM now \n Upgrade more?");
//            binding.topImg.setImageDrawable(getResources().getDrawable(R.drawable.bg_art_premium));
        } else {
            tv_title.setText("Get Premium With No Ads?");
//            binding.topImg.setImageDrawable(getResources().getDrawable(R.drawable.in_app_purchase_img));

            iv_check1.setVisibility(View.GONE);
            iv_check2.setVisibility(View.GONE);
            iv_check3.setVisibility(View.GONE);
        }


    }

    void showProducts() {

        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(ONE_MONTH_SUBS)
                .setProductType(BillingClient.ProductType.SUBS)
                .build());

        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(THREE_MONTHS_SUBS)
                .setProductType(BillingClient.ProductType.SUBS)
                .build());

        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(ONE_YEAR_SUBS)
                .setProductType(BillingClient.ProductType.SUBS)
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, productDetailsList) -> {
                    // Process the result
                    for (ProductDetails productDetails : productDetailsList) {

                        String product_id = productDetails.getProductId();

                        if (product_id.equals(ONE_MONTH_SUBS)) {
                            List<ProductDetails.SubscriptionOfferDetails> subDetails = productDetails.getSubscriptionOfferDetails();

                            if (subDetails != null) {
                                tv_item1_title.setText(productDetails.getDescription());
                                tv_item1_price.setText(subDetails.get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice() + " /Month");
                                item2.setOnClickListener(v -> {
                                    launchPurchaseFlow(productDetails);
                                });
                            }
                        } else if (product_id.equals(THREE_MONTHS_SUBS)) {
                            List<ProductDetails.SubscriptionOfferDetails> subDetails1 = productDetails.getSubscriptionOfferDetails();

                            if (subDetails1 != null) {
                                tv_item2_title.setText(productDetails.getDescription());
                                tv_item2_price.setText(subDetails1.get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice() + " /3 Months");
                                item2.setOnClickListener(v -> {
                                    launchPurchaseFlow(productDetails);
                                });
                            }
                        } else if (product_id.equals(ONE_YEAR_SUBS)) {
                            List<ProductDetails.SubscriptionOfferDetails> subDetails2 = productDetails.getSubscriptionOfferDetails();

                            if (subDetails2 != null) {
                                tv_item3_title.setText(productDetails.getDescription());
                                tv_item3_price.setText(subDetails2.get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice() + " /Year");
                                item3.setOnClickListener(v -> {
                                    launchPurchaseFlow(productDetails);
                                });
                            }
                        }
                    }
                }
        );

    }

    private void launchPurchaseFlow(ProductDetails productDetails) {

        if (productDetails.getSubscriptionOfferDetails() != null) {
            List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
            productDetailsParamsList.add(BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                    .build());
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();
            billingClient.launchBillingFlow(this, billingFlowParams);
        }
    }

    void verifySubPurchase(Purchase purchases) {

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {


                sharedPref.setIsPremium(true);

                try{
                    JSONObject json = new JSONObject(purchases.getOriginalJson());
                    String product_id = json.getString("productId");

                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (product_id.equals(ONE_MONTH_SUBS)) {
                                iv_check1.setVisibility(View.VISIBLE);
                            } else if (product_id.equals(THREE_MONTHS_SUBS)) {
                                iv_check2.setVisibility(View.VISIBLE);
                            } else if (product_id.equals(ONE_YEAR_SUBS)) {
                                iv_check3.setVisibility(View.VISIBLE);
                            }

                            tv_title.setText("You are PREMIUM now \n Upgrade more?");
//                            binding.topImg.setImageDrawable(getResources().getDrawable(R.drawable.bg_art_premium));
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }


}