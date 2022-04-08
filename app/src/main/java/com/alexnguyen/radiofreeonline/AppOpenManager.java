package com.alexnguyen.radiofreeonline;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

public class AppOpenManager {
    private static final String LOG_TAG = "AppOpenManager";
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294";
    private static AppOpenAd appOpenAd;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;

    private final SplashActivity mySplash;

    //Hàm dựng
    @RequiresApi(api = Build.VERSION_CODES.P)
    public AppOpenManager(SplashActivity mySplash) {
        this.mySplash = mySplash;
        fetchAd();
    }

    // nạp ads
    public void fetchAd() {
        //nếu có sẵn ads có rồi
        if(isAdAvailable()){
            return;
        }
        AdRequest request = getAdRequest();
        AppOpenAd.load(
                mySplash, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAppOpenAdLoaded(AppOpenAd appOpenAd) {
                        Log.e("ThongBao", "Ad Loaded");
                        com.alexnguyen.radiofreeonline.AppOpenManager.this.appOpenAd = appOpenAd;
                    }

                    @Override
                    public void onAppOpenAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e("ThongBao", "Fail to load");
                    }
                }
        );
        Log.e("ThongBao","Fetch Ad");
    };

    // khởi tạo và trả về adRequest
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    //Kiểm tra appOpenAd đã có sẵn hay chưa
    public boolean isAdAvailable() {
        if(appOpenAd != null){
            return true;
        }else{
            return false;
        }
    }

    public void showAdIfAvailable(){
        // chỉ show ads khi ads đã được load
        if(isAdAvailable()){
            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback(){
                        // nếu app có sẵn -> hiện lên -> bỏ qua -> gọi hàm openMainActitivty của SplashActivity
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Log.e("ThongBao", "Ad Dismiss");
                            com.alexnguyen.radiofreeonline.AppOpenManager.this.appOpenAd = null;
                            fetchAd();
                            mySplash.openMainActivity();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            Log.e("ThongBao", "Ad Failed to load");
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.e("ThongBao", "Ad Showed Fullcontent");
                        }
                    };
            appOpenAd.show(mySplash, fullScreenContentCallback);
        }else{
            // nếu app chưa có sẵn -> gọi hàm openMainActitivty của SplashActivity
            Log.e("ThongBao", "Ad is unavailable");
            fetchAd();
            mySplash.openMainActivity();
        }
    }
}