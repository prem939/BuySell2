package com.example.buysell2.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.buysell2.CShowProgress;
import com.example.buysell2.DatabaseHelper;
import com.example.buysell2.Do.UserDo;
import com.example.buysell2.R;
import com.example.buysell2.common.ApiServices;
import com.example.buysell2.common.AppConstants;
import com.example.buysell2.common.Preference;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

public class SplashActivity extends BaseActivity {
    private LinearLayout llSplash;
    public Preference preference;
    public String strResponce = "";
    ApiServices apiServices = new ApiServices();
    DatabaseHelper objSqliteDB = null;

    @Override
    public void initialize() {
        llSplash = (LinearLayout) inflater.inflate(R.layout.activity_splash_screen, null);
        llBody.addView(llSplash, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initializeControlls();
        objSqliteDB = new DatabaseHelper(getApplicationContext());
        moveToLogin();
    }

    private void moveToLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preference.getStringFromPreference(AppConstants.LOGIN_TOKE, "").equalsIgnoreCase("") && !objSqliteDB.isUserExist()) {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("LoginUserData", objSqliteDB.getUser());
                    startActivity(intent);
//                    if(objSqliteDB.isUserExist(preference.getStringFromPreference(Preference.USERID, ""))){
//                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                        intent.putExtra("LoginUserData", objSqliteDB.getUser());
//                        startActivity(intent);
//                    }else{
//                        syncTaskForGetUserProfile syncTaskForGetUserProfile = new syncTaskForGe=tUserProfile();
//                        syncTaskForGetUserProfile.execute();
//                    }
                }

            }
        }, 3000);
    }

    public void initializeControlls() {
        preference = new Preference(getApplicationContext());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        preference.saveIntInPreference(Preference.DEVICE_DISPLAY_WIDTH, displaymetrics.widthPixels);
        preference.saveIntInPreference(Preference.DEVICE_DISPLAY_HEIGHT, displaymetrics.heightPixels);
        preference.commitPreference();
    }

    public class syncTaskForGetUserProfile extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            strResponce = apiServices.getDataForSingleUser(preference.getStringFromPreference(Preference.USERID, ""));
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            UserDo userDo = null;
            if (strResponce != null && !strResponce.equalsIgnoreCase("")) {
                try {
                    userDo = new Gson().fromJson(strResponce, UserDo.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }
                if (userDo != null) {
                    objSqliteDB.insertUser(userDo);
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("LoginUserData", userDo);
                    startActivity(intent);
                }
            }
        }
    }
}
