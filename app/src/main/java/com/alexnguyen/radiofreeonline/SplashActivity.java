package com.alexnguyen.radiofreeonline;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.alexnguyen.asyncTasks.LoadAbout;
import com.alexnguyen.asyncTasks.LoadLogin;
import com.alexnguyen.interfaces.AboutListener;
import com.alexnguyen.interfaces.LoginListener;
import com.alexnguyen.item.ItemUser;
import com.alexnguyen.utils.Constants;
import com.alexnguyen.utils.DBHelper;
import com.alexnguyen.utils.Methods;
import com.alexnguyen.utils.SharedPref;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Methods methods;
    DBHelper dbHelper;
    AppOpenManager appOpenManager;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appOpenManager = new AppOpenManager(this);
        hideStatusBar();
        methods = new Methods(this);
        sharedPref = new SharedPref(this);
        dbHelper = new DBHelper(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPref.getIsFirst()) {
                    loadAboutData();
                } else {
                    if(sharedPref.getIsAutoLogin()) {
                        loadLogin();
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LoadAppOpenAd();
                            }
                        }, 2000);
                    }
                }
            }
        }, 500);
    }

    public void LoadAppOpenAd(){
        appOpenManager.showAdIfAvailable();
    }

    public void loadAboutData() {
        if (methods.isConnectingToInternet()) {
            LoadAbout loadAbout = new LoadAbout(new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if(!isFinishing()) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                dbHelper.addtoAbout();
                                sharedPref.setIsFirst(false);
                                LoadAppOpenAd();
                            } else {
                                errorDialog(getString(R.string.error_unauth_access), message);
                            }
                        } else {
                            errorDialog(getString(R.string.server_error), getString(R.string.error_server));
                        }
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_ABOUT, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.internet_not_connected), getString(R.string.error_connect_net_tryagain));
        }
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.AlertDialogTheme);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (title.equals(getString(R.string.internet_not_connected)) || title.equals(getString(R.string.server_error))) {
            alertDialog.setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadAboutData();
                }
            });
        }

        alertDialog.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    private void loadLogin() {
        LoadLogin loadLogin = new LoadLogin(new LoginListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name) {
                if (!isFinishing()) {
                    if (success.equals("1")) {
                        if (loginSuccess.equals("1")) {
                            Constants.itemUser = new ItemUser(user_id, user_name, sharedPref.getEmail(), "");

                            Constants.isLogged = true;
                        }
                        LoadAppOpenAd();
                    } else {
                        LoadAppOpenAd();
                    }
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_LOGIN, 0, "", "", "", "", "", "", sharedPref.getEmail(), sharedPref.getPassword(), "", "", "", "", null));
        loadLogin.execute();
    }

    public void openMainActivity() {
        Intent intent = new Intent(SplashActivity.this, BaseActivity.class);
        intent.putExtra("from", "");
        startActivity(intent);
        finish();
    }

    void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}