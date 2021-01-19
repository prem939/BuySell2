package com.example.buysell2.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.buysell2.CShowProgress;
import com.example.buysell2.DatabaseHelper;
import com.example.buysell2.Do.SpinnerDo;
import com.example.buysell2.Do.UserDo;
import com.example.buysell2.Interface.GetData;
import com.example.buysell2.R;
import com.example.buysell2.common.ApiServices;
import com.example.buysell2.common.AppConstants;
import com.example.buysell2.common.CustomDialog;
import com.example.buysell2.common.Preference;
import com.example.buysell2.common.RetrofitClient;
import com.example.buysell2.common.ServiceURLs;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.hbb20.CountryCodePicker;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    private EditText edit_userId, edit_password, etFirstName, etLastName, etEmail, etPhone, etPassword, etId, edtUserId,
            edOtp, edtNewPassword, edtConformNewPassword, etConfPassword;
    private TextView txtForgotPassword, txtlogin, txtSignUp, txtBackToLogin, tvSignUp;
    private Button btn_login, btnCreateAccount, btnValidate, btnOtp, btnPasswordUpdate;
    private String strUserId = "", strPassword = "", strFname = "", strLname = "", strEmail = "", strPhoneNumber = "", strPasswd = "", strType = "", strId = "", strOtp = "",
            strNewPassword = "", strConformPassword = "", selected_country_code = "";
    private LinearLayout lllogin, llSignUpPage, llOtp, llNewPassword, llConformNewPassword, llLoginPage, llLoginActivity, llResetPasswordPage;
    private Spinner spinUserType;
    private String UserType[] = {"--select user type--", "Admin", "Supplier", "Customer"}, StringJson = "", API_LOGIN = "", emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+", stringToken = "", strResponce = "";
    private ArrayAdapter<String> userTypeAdapter;
    ApiServices apiServices = new ApiServices();
    private CShowProgress cShowProgress;
    private CheckBox pwdCheckbox;
    private ImageView imgChangePassword;
    private boolean show = false;
    GetData service;
    CountryCodePicker ccp;
    DatabaseHelper objSqliteDB = null;

    @Override
    public void initialize() {
        llLoginActivity = (LinearLayout) inflater.inflate(R.layout.login, null);
        llBody.addView(llLoginActivity, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        initialization();

        userTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UserType);
        spinUserType.setAdapter(userTypeAdapter);
        spinUserType.setOnItemSelectedListener(LoginActivity.this);

        lllogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llResetPasswordPage.setVisibility(View.GONE);
                llSignUpPage.setVisibility(View.GONE);
                llLoginPage.setVisibility(View.VISIBLE);
                clearLoginEditTest();
                hideKeyBoard(view);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard(edit_password);

                strUserId = edit_userId.getText().toString();
                strPassword = edit_password.getText().toString();

                if (strUserId.equals("") || strPassword.equals("")) {
                    validateLogin();
                } else {
                    if (isNetworkConnectionAvailable(LoginActivity.this)) {
                        syncTaskForLogin runner = new syncTaskForLogin();
                        runner.execute();
                    } else
                        showCustomDialog(LoginActivity.this, getString(R.string.warning), "Connect to Internet", getString(R.string.OK), "", "");
                }
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard(view);
                ClearResetPasswordPage();
                llResetPasswordPage.setVisibility(View.VISIBLE);
                llSignUpPage.setVisibility(View.GONE);
                llLoginPage.setVisibility(View.GONE);

                llOtp.setVisibility(view.GONE);
                llNewPassword.setVisibility(view.GONE);
                llConformNewPassword.setVisibility(view.GONE);
                btnValidate.setVisibility(view.VISIBLE);
                btnOtp.setVisibility(view.GONE);
                btnPasswordUpdate.setVisibility(view.GONE);
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyBoard(view);
                clearSignInEdittest();
                llResetPasswordPage.setVisibility(View.GONE);
                llSignUpPage.setVisibility(View.VISIBLE);
                llLoginPage.setVisibility(View.GONE);
            }
        });

        txtBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lllogin.performClick();
            }
        });

        pwdCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strEmail = edtUserId.getText().toString();
                if (strEmail.equalsIgnoreCase("")) {
                    showCustomeToast("Please enter the UserId.");
                } else {
                    syncTaskuserIdValidation syncTaskuserIdValidation = new syncTaskuserIdValidation();
                    syncTaskuserIdValidation.execute(strEmail);
                }

            }
        });
        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strOtp = edOtp.getText().toString();
                if (strOtp.equalsIgnoreCase("")) {
                    showCustomeToast("Please enter the Otp.");
                } else {
                    syncTaskOtpVerification syncTaskOtpVerification = new syncTaskOtpVerification();
                    syncTaskOtpVerification.execute(strOtp);
                }
            }
        });

        btnPasswordUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strNewPassword = edtNewPassword.getText().toString();
                strConformPassword = edtConformNewPassword.getText().toString();
                if (strNewPassword.equalsIgnoreCase("")) {
                    showCustomeToast("Please enter the New password.");
                } else if (strConformPassword.equalsIgnoreCase("")) {
                    showCustomeToast("Please enter the Conformation New password.");
                } else if (strNewPassword.equals(strConformPassword)) {
                    syncTaskResetPassword syncTaskResetPassword = new syncTaskResetPassword();
                    syncTaskResetPassword.execute(edtUserId.getText().toString(), strNewPassword);
                } else {
                    showCustomeToast("Both passwords should be equal.");
                }
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strFname = etFirstName.getText().toString();
                strLname = etLastName.getText().toString();
                strEmail = etEmail.getText().toString();
                strPhoneNumber = etPhone.getText().toString();
                strPasswd = etPassword.getText().toString();
                strConformPassword = etConfPassword.getText().toString();
                if (spinUserType.getSelectedItem().toString().equalsIgnoreCase("Sales Person")) {
                    strType = "Salesperson";
                } else {
                    strType = spinUserType.getSelectedItem().toString();
                }

                if (strFname.equalsIgnoreCase("")) {
                    showCustomeToast("Please enter First name.");
                } else if (strLname.equalsIgnoreCase("")) {
                    showCustomeToast("Please enter Last name.");
                } else if (strType.equalsIgnoreCase("--select--")) {
                    showCustomeToast("Please select the user type.");
                } else if (strPhoneNumber.equalsIgnoreCase("")) {
                    showCustomeToast("Please enter Phone Number.");
                } else if (strPasswd.equalsIgnoreCase("")) {
                    showCustomeToast("Please enter the password.");
                } else if (strPhoneNumber.length() < 10) {
                    showCustomeToast("Please enter valid phone number.");
                } else if (!strPasswd.equals(strConformPassword)) {
                    showCustomeToast("Passwords are not matching.");
                } else if (!strEmail.equalsIgnoreCase("") && !strEmail.trim().matches(emailPattern)) {
                    showCustomeToast("Mail address is not a valid one.");
                } else if (isNetworkConnectionAvailable(LoginActivity.this)) {
//                    syncTaskPhoneNoValidation phoneNoValidation = new syncTaskPhoneNoValidation();
//                    phoneNoValidation.execute(strPhoneNumber);
                    runOnUiThread(new RunshowCustomDialogsForOtp());
                } else {
                    showCustomeToast("Connect to Internet");
                }

            }
        });
    }

    public void onCountryPickerClick(View view) {
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //Alert.showMessage(RegistrationActivity.this, ccp.getSelectedCountryCodeWithPlus());
                selected_country_code = ccp.getSelectedCountryCodeWithPlus();
            }
        });
    }

    private void initialization() {
        service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        objSqliteDB = new DatabaseHelper(getApplicationContext());
        llResetPasswordPage = llLoginActivity.findViewById(R.id.llResetPasswordPage);
        txtBackToLogin = llLoginActivity.findViewById(R.id.txtBackToLogin);
        llLoginPage = llLoginActivity.findViewById(R.id.llLoginPage);
        llSignUpPage = llLoginActivity.findViewById(R.id.llSignUpPage);
        lllogin = llLoginActivity.findViewById(R.id.lllogin);
        txtForgotPassword = llLoginActivity.findViewById(R.id.txtForgotPassword);
        tvSignUp = llLoginActivity.findViewById(R.id.tvSignUp);
        spinUserType = llLoginActivity.findViewById(R.id.spinUserType);
        btnValidate = llLoginActivity.findViewById(R.id.btnValidate);
        btnOtp = llLoginActivity.findViewById(R.id.btnOtp);
        btnPasswordUpdate = llLoginActivity.findViewById(R.id.btnPasswordUpdate);
        btn_login = llLoginActivity.findViewById(R.id.btn_login);
        edit_userId = llLoginActivity.findViewById(R.id.edit_userId);
        edit_password = llLoginActivity.findViewById(R.id.edit_password);
        etFirstName = llLoginActivity.findViewById(R.id.etFirstName);
        etLastName = llLoginActivity.findViewById(R.id.etLastName);
        etEmail = llLoginActivity.findViewById(R.id.etEmail);
        etPhone = llLoginActivity.findViewById(R.id.etPhone);
        etPassword = llLoginActivity.findViewById(R.id.etPassword);
        edtUserId = llLoginActivity.findViewById(R.id.edtUserId);
        edOtp = llLoginActivity.findViewById(R.id.edOtp);
        edtNewPassword = llLoginActivity.findViewById(R.id.edtNewPassword);
        edtConformNewPassword = llLoginActivity.findViewById(R.id.edtConformNewPassword);
        pwdCheckbox = llLoginActivity.findViewById(R.id.pwdCheckbox);
        llOtp = llLoginActivity.findViewById(R.id.llOtp);
        llNewPassword = llLoginActivity.findViewById(R.id.llNewPassword);
        llConformNewPassword = llLoginActivity.findViewById(R.id.llConformNewPassword);
        btnCreateAccount = llLoginActivity.findViewById(R.id.btnCreateAccount);
        etConfPassword = llLoginActivity.findViewById(R.id.etConfPassword);
        ccp = llLoginActivity.findViewById(R.id.ccp);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class syncTaskuserIdValidation extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            strResponce = apiServices.Validating(ServiceURLs.USERID_VALIDATION + strings[0], "");
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(LoginActivity.this);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            cShowProgress.hideProgress();
            if (strResponce.equalsIgnoreCase("valid")) {
                edtUserId.setFocusable(false);
                edtUserId.setFocusableInTouchMode(false);
                llOtp.setVisibility(View.VISIBLE);
                btnOtp.setVisibility(View.VISIBLE);
                btnValidate.setVisibility(View.GONE);
            } else if (strResponce.equalsIgnoreCase("Invalid")) {
                showCustomeToast("Invalid user id");
            }
        }
    }

    public class syncTaskPhoneNoValidation extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            strResponce = apiServices.Validating(ServiceURLs.USERID_VALIDATION + strings[0], "");
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(LoginActivity.this);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            cShowProgress.hideProgress();
            if (strResponce.equalsIgnoreCase("valid")) {
                btnCreateAccount.setVisibility(View.GONE);
            } else if (strResponce.equalsIgnoreCase("Invalid")) {
                showCustomeToast("Invalid Phone Number");
            }
        }
    }

    public class syncTaskOtpVerificationForSignUp extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            strResponce = apiServices.Validating(ServiceURLs.OTP_VALIDATION + strings[0], "");
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(LoginActivity.this);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            cShowProgress.hideProgress();
            if (strResponce.equalsIgnoreCase("valid")) {
                new syncTaskForCreateAccount().execute();
                customDialog.dismiss();
            } else if (strResponce.equalsIgnoreCase("Invalid")) {
                showCustomeToast("Invalid Otp");
            }
        }
    }

    public class syncTaskOtpVerification extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            strResponce = apiServices.Validating(ServiceURLs.OTP_VALIDATION + strings[0], "");
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(LoginActivity.this);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            cShowProgress.hideProgress();
            if (strResponce.equalsIgnoreCase("valid")) {
                edOtp.setFocusable(false);
                edOtp.setFocusableInTouchMode(false);
                llNewPassword.setVisibility(View.VISIBLE);
                llConformNewPassword.setVisibility(View.VISIBLE);
                btnPasswordUpdate.setVisibility(View.VISIBLE);
                btnOtp.setVisibility(View.GONE);
            } else if (strResponce.equalsIgnoreCase("Invalid")) {
                showCustomeToast("Invalid Otp");
            }
        }
    }

    public class syncTaskResetPassword extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            strResponce = apiServices.Validating(ServiceURLs.RESETPASSWORD + "MobileNo=" + strings[0] + "&NewPassowrd=" + strings[1], "update Password");
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(LoginActivity.this);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            cShowProgress.hideProgress();
            if (strResponce.equalsIgnoreCase("valid")) {
                ClearResetPasswordPage();
                txtBackToLogin.performClick();
                showCustomeToast("Password is reset successfully");
            } else if (strResponce.equalsIgnoreCase("Invalid")) {
                showCustomeToast("Something went wrong");
            }
        }
    }

    public void ClearResetPasswordPage() {
        edtUserId.setText("");
        edOtp.setText("");
        edtNewPassword.setText("");
        edtConformNewPassword.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void validateLogin() {
        if (strUserId.equals("") && strPassword.equals("")) {
            showCustomeToast(getString(R.string.enter_username_password));
            edit_userId.requestFocus();
        } else if (strUserId.equals("")) {
            showCustomeToast(getString(R.string.enter_username));
            edit_userId.requestFocus();
        } else if (strPassword.equals("")) {
            showCustomeToast(getString(R.string.enter_password));
            edit_password.requestFocus();
        }
    }

    public class syncTaskForLogin extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            stringToken = apiServices.getLoginToken(JsontoStringForLogin(strUserId, strPassword));
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(LoginActivity.this);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            cShowProgress.hideProgress();

            if (stringToken.equalsIgnoreCase(AppConstants.INTERNAL_ERROR) || stringToken.equalsIgnoreCase(AppConstants.INVALID_USERID_PASS)) {
                showCustomeToast("Invalid UserId or Password");
            } else if (isNetworkConnectionAvailable(LoginActivity.this)) {
                preference.saveStringInPreference(AppConstants.LOGIN_TOKE, stringToken.substring(1, stringToken.length() - 1));
                preference.commitPreference();
                syncTaskForGetUserProfile syncTaskForGetUserProfile = new syncTaskForGetUserProfile();
                syncTaskForGetUserProfile.execute();
            } else {
                showCustomeToast("Connect to Internet");
            }
        }
    }

    public class syncTaskForGetUserProfile extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            strResponce = apiServices.getDataForSingleUser(strUserId);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(LoginActivity.this);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            cShowProgress.hideProgress();
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
                    preference.saveStringInPreference(Preference.USERID, "" + userDo.UP_UserID);
                    preference.saveStringInPreference(Preference.ID, "" + userDo.UP_ID);
                    preference.saveStringInPreference(Preference.TYPE, "" + userDo.UP_User_Type);
                    preference.commitPreference();
                    objSqliteDB.insertUser(userDo);
                    InsertSupplierTypes();
                    InsertCatlogItems();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("LoginUserData", userDo);
                    startActivity(intent);
                }
            }
        }
    }

    public class syncTaskForCreateAccount extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            strResponce = apiServices.PostDataForCreateUser(generateJsonToString());
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(LoginActivity.this);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            cShowProgress.hideProgress();
            if (strResponce.equalsIgnoreCase(AppConstants.EMAIL_ALREADY_EXISTS)) {
                showCustomeToast("Email already exists");
            } else if (strResponce.equalsIgnoreCase(AppConstants.MOBILE_NO_ALREADY_EXISTS)) {
                showCustomeToast("Mobile No already exists");
            } else if (strResponce.equalsIgnoreCase(AppConstants.CREATED_SUCCESSFULLY)) {
                lllogin.performClick();
                clearSignInEdittest();
                showCustomDialog(LoginActivity.this, getString(R.string.alert), "Created Successfully. Yours UserId is : " + strPhoneNumber, getString(R.string.OK), "", "");
            } else {
                showCustomeToast("Something went wrong");
            }
        }
    }

    private void clearSignInEdittest() {
        etFirstName.setText("");
        etLastName.setText("");
        etEmail.setText("");
        etPhone.setText("");
        etPassword.setText("");
        etConfPassword.setText("");
    }

    private void clearLoginEditTest() {
        edit_userId.setText("");
        edit_password.setText("");
    }

    private String generateJsonToString() {
        String jsonString = "{" +
                "\"UP_Name\": \"" + strFname + " " + strLname + "\", " +
                "\"UP_User_Type\": \"" + strType + "\", " +
                "\"UP_Email\": \"" + strEmail + "\"," +
                "\"UP_Mobile_No\": " + strPhoneNumber + "," +
                "\"UP_UserID\": " + strPhoneNumber + "," +
                "\"UP_Password\": \"" + strPasswd + "\"," +
                "\"UP_Status\": \"A\"" + "}";
        return jsonString;
    }

    private String JsontoStringForLogin(String userId, String password) {
        String jsonString = "{" +
                "\"UserName\": \"" + userId + "\", " +
                "\"Password\": \"" + password + "\" " + "}";
        return jsonString;
    }

    class RunshowCustomDialogsForOtp implements Runnable {
        public RunshowCustomDialogsForOtp() {
        }

        @Override
        public void run() {
            if (customDialog != null && customDialog.isShowing())
                customDialog.dismiss();
            View view = inflater.inflate(R.layout.custom_common_popup_for_otp, null);

            customDialog = new CustomDialog(LoginActivity.this, view, preference
                    .getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 60,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
            customDialog.setCancelable(true);
            final EditText etOtp = view.findViewById(R.id.etOtp);
            Button btnYes = (Button) view.findViewById(R.id.btnYesPopup);
            Button btnNo = (Button) view.findViewById(R.id.btnNoPopup);
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String otp = etOtp.getText().toString();
                    if (!otp.equals(null) && !otp.equals("")) {
                        syncTaskOtpVerificationForSignUp syncTaskOtpVerification = new syncTaskOtpVerificationForSignUp();
                        syncTaskOtpVerification.execute(otp);
                    } else {
                        showCustomeToast("Please enter Otp..");
                    }
                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                }
            });

            try {
                if (!customDialog.isShowing())
                    customDialog.showCustomDialog(LoginActivity.this);
            } catch (Exception e) {
            }
        }
    }

    private void InsertSupplierTypes() {
        if (objSqliteDB.isSupplierBusinessTypeExit()) {
            Call<List<SpinnerDo>> call = service.getSupplier_Bussiness_type_Master("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            call.enqueue(new Callback<List<SpinnerDo>>() {
                @Override
                public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                    objSqliteDB.insertSupplierBusinessTypeMaster(response.body());
                }

                @Override
                public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                }
            });
        }
        if (!objSqliteDB.isSupplierTypeExit()) {
            Call<List<SpinnerDo>> call2 = service.getSupplierTypeMaster("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            call2.enqueue(new Callback<List<SpinnerDo>>() {
                @Override
                public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                    objSqliteDB.insertSupplierTypeMaster(response.body());
                }

                @Override
                public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                }
            });
        }

    }

    private void InsertCatlogItems() {
        if (!objSqliteDB.isCatlogItemExit()) {
            Call<List<SpinnerDo>> call = service.getAllProdectCategoryMaster("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            call.enqueue(new Callback<List<SpinnerDo>>() {
                @Override
                public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                    objSqliteDB.insertCatlogItem(response.body());
                }

                @Override
                public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                }
            });
        }
        if (!objSqliteDB.isSubCatlogItemExit()) {
            Call<List<SpinnerDo>> call2 = service.getAllProdectSubCategoryMaster("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            call2.enqueue(new Callback<List<SpinnerDo>>() {
                @Override
                public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                    objSqliteDB.insertSubCatlogItem(response.body());
                }

                @Override
                public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                }
            });
        }
    }
}
