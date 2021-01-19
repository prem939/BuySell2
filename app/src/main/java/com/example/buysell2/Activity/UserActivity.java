package com.example.buysell2.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.buysell2.Do.UserDo;
import com.example.buysell2.R;
import com.example.buysell2.common.ApiServices;
import com.example.buysell2.common.AppConstants;
import com.example.buysell2.common.CustomDialog;
import com.example.buysell2.common.Preference;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

public class UserActivity extends BaseActivity {
    LinearLayout llUser, llOldPwd, llNewPwd, llConfPwd;
    private ImageView imgBack, imgEditUserDetails;
    private UserDo user = new UserDo();
    private TextView txtUserName, txtEmail, txtPhoneno;
    public String strResponce = "", strName = "", strEmail = "", strPhoneNo = "", strUpdateResponse = "", strOldPwd = "", strNewPwd = "", strConfPwd = "";
    private EditText edtName, etEmail, etPhoneno, etConfPwd, etNewPwd, etOldPwd;
    private Button btnYesPopup, btnNoPopup;
    private ApiServices apiServices = new ApiServices();
    private CheckBox cbChangePwd;

    @Override
    public void initialize() {
        llUser = (LinearLayout) inflater.inflate(R.layout.user_activity, null);
        llBody.addView(llUser, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initialization();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = (UserDo) bundle.get("LoginUserData");
            txtUserName.setText(user.UP_Name);
            txtEmail.setText(user.UP_Email);
            txtPhoneno.setText("" + user.UP_Mobile_No);
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imgEditUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new RunshowCustomDialogs());
            }
        });
    }

    private void setText() {
        new syncTaskForGetUserProfile().execute();
    }

    private void initialization() {
        imgBack = llUser.findViewById(R.id.imgBack);
        txtUserName = llUser.findViewById(R.id.txtUserName);
        txtEmail = llUser.findViewById(R.id.txtEmail);
        txtPhoneno = llUser.findViewById(R.id.txtPhoneno);
        imgEditUserDetails = llUser.findViewById(R.id.imgEditUserDetails);
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
            if (strResponce != null && !strResponce.equalsIgnoreCase("")) {
                try {
                    user = new Gson().fromJson(strResponce, UserDo.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }

            }
            txtUserName.setText(user.UP_Name);
            txtEmail.setText(user.UP_Email);
            txtPhoneno.setText("" + user.UP_Mobile_No);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class RunshowCustomDialogs implements Runnable {


        @Override
        public void run() {
            if (customDialog != null && customDialog.isShowing())
                customDialog.dismiss();
            View view;

            view = inflater.inflate(R.layout.custom_popup_for_edit_userdetails, null);

            customDialog = new CustomDialog(UserActivity.this, view, preference
                    .getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 60,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
            customDialog.setCancelable(true);
            initialization(view);


            edtName.setText(user.UP_Name);
            etEmail.setText(user.UP_Email);
            etPhoneno.setText("" + user.UP_Mobile_No);

            cbChangePwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cbChangePwd.isChecked()) {
                        llOldPwd.setVisibility(View.VISIBLE);
                        llNewPwd.setVisibility(View.VISIBLE);
                        llConfPwd.setVisibility(View.VISIBLE);
                    } else {
                        llOldPwd.setVisibility(View.GONE);
                        llNewPwd.setVisibility(View.GONE);
                        llConfPwd.setVisibility(View.GONE);
                    }
                }
            });

            btnNoPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                }
            });
            btnYesPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strName = edtName.getText().toString();
                    strEmail = etEmail.getText().toString();
                    strPhoneNo = etPhoneno.getText().toString();
                    strOldPwd = etOldPwd.getText().toString();
                    strNewPwd = etNewPwd.getText().toString();
                    strConfPwd = etConfPwd.getText().toString();
                    if(!cbChangePwd.isChecked() && strName.equalsIgnoreCase(user.UP_Name) && strEmail.equalsIgnoreCase(user.UP_Email) && strPhoneNo.equalsIgnoreCase("" + user.UP_Mobile_No)){
                        showCustomeToast("No change is appear..");
                    } else if (cbChangePwd.isChecked() && strOldPwd.equals("")) {
                        showCustomeToast("Please enter old password..");
                    } else if (cbChangePwd.isChecked() && strNewPwd.equals("")) {
                        showCustomeToast("Please enter new password..");
                    } else if (cbChangePwd.isChecked() && strConfPwd.equals("")) {
                        showCustomeToast("Please enter conform password..");
                    } else if (cbChangePwd.isChecked() && !strOldPwd.equals(user.UP_Password)) {
                        showCustomeToast("Your old password is wrong..");
                    } else if (cbChangePwd.isChecked() && !strNewPwd.equals(strConfPwd)) {
                        showCustomeToast("Both password should is be equal...");
                    } else {
                        new SyncforUpdateUserDetails().execute(cbChangePwd.isChecked());
                        customDialog.dismiss();
                    }
                }
            });

            try {
                if (!customDialog.isShowing())
                    customDialog.showCustomDialog(UserActivity.this);
            } catch (Exception e) {
            }
        }
    }

    public class SyncforUpdateUserDetails extends AsyncTask<Boolean, Void, Void> {

        @Override
        protected Void doInBackground(Boolean... params) {
            strUpdateResponse = ApiServices.PutDataForCreateUser(generateJsonToString(params[0]), user.UP_ID);
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            if (strUpdateResponse.equals(AppConstants.CREATED_SUCCESSFULLY)) {
                showCustomeToast("Successfully updated..");
                setText();
            } else if (strUpdateResponse.equals(AppConstants.INTERNAL_ERROR)) {
                showCustomeToast(AppConstants.INTERNAL_ERROR);
            } else if (strUpdateResponse.equals(AppConstants.EMAIL_ALREADY_EXISTS)) {
                showCustomeToast(AppConstants.EMAIL_ALREADY_EXISTS);
            } else if (strUpdateResponse.equals(AppConstants.MOBILE_NO_ALREADY_EXISTS)) {
                showCustomeToast(AppConstants.MOBILE_NO_ALREADY_EXISTS);
            } else if (strUpdateResponse.equals(AppConstants.NO_CONTENT)) {
                showCustomeToast(AppConstants.NO_CONTENT);
            } else if (strUpdateResponse.equals(AppConstants.USER_NOT_FOUND)) {
                showCustomeToast(AppConstants.USER_NOT_FOUND);
            } else {
                showCustomeToast("Some thing went wrong.");
            }
        }
    }

    public void initialization(View view) {
        edtName = view.findViewById(R.id.edtName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhoneno = view.findViewById(R.id.etPhoneno);
        btnYesPopup = view.findViewById(R.id.btnYesPopup);
        btnNoPopup = view.findViewById(R.id.btnNoPopup);
        cbChangePwd = view.findViewById(R.id.cbChangePwd);
        llOldPwd = view.findViewById(R.id.llOldPwd);
        llNewPwd = view.findViewById(R.id.llNewPwd);
        llConfPwd = view.findViewById(R.id.llConfPwd);
        etConfPwd = view.findViewById(R.id.etConfPwd);
        etNewPwd = view.findViewById(R.id.etNewPwd);
        etOldPwd = view.findViewById(R.id.etOldPwd);
    }

    private String generateJsonToString(boolean isPassword) {
        String jsonString = "";
        if (isPassword) {
            jsonString = "{" +
                    "\"UP_ID\": " + user.UP_ID + ", " +
                    "\"UP_Name\": \"" + strName + "\", " +
                    "\"UP_Email\": \"" + strEmail + "\"," +
                    "\"UP_User_Type\": \"" + user.UP_User_Type + "\"," +
                    "\"UP_Mobile_No\": \"" + strPhoneNo + "\"," +
                    "\"UP_Password\": \"" + strNewPwd + "\"," +
                    "\"UP_Status\": \"" + "A" + "\"," +
                    "\"UP_UserID\": " + strPhoneNo + "}";
        } else {
            jsonString = "{" +
                    "\"UP_ID\": " + user.UP_ID + ", " +
                    "\"UP_Name\": \"" + strName + "\", " +
                    "\"UP_Email\": \"" + strEmail + "\"," +
                    "\"UP_User_Type\": \"" + user.UP_User_Type + "\"," +
                    "\"UP_Mobile_No\": \"" + strPhoneNo + "\"," +
                    "\"UP_Password\": \"" + user.UP_Password + "\"," +
                    "\"UP_Status\": \"" + "A" + "\"," +
                    "\"UP_UserID\": " + strPhoneNo + "}";
        }

        return jsonString;
    }
}
