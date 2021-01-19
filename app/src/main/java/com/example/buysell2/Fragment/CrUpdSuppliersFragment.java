package com.example.buysell2.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.buysell2.Activity.MainActivity;
import com.example.buysell2.CShowProgress;
import com.example.buysell2.Do.SpinnerDo;
import com.example.buysell2.Do.SupplierMasterDo;
import com.example.buysell2.Interface.GetData;
import com.example.buysell2.R;
import com.example.buysell2.common.ApiServices;
import com.example.buysell2.common.AppConstants;
import com.example.buysell2.common.Preference;
import com.example.buysell2.common.RetrofitClient;
import com.example.buysell2.common.ServiceURLs;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CrUpdSuppliersFragment extends Fragment {

    private String CountryNames[] = {"--select country--", "INDIA", "PAKISTAN", "CHINA", "SRILANKA", "BANGLADESH", "USA", "UK"},supplierType[], businessType[];
    private ArrayAdapter<String> CAdapter, supplierTypeAdaptor, businessTypeAdaptor;
    private LinearLayout llcreate_supplier;
    private EditText edtSupplierName, edtTemplate, edtMin, edtMax, edtPin_AadharNo,
            edtRegisterAddress, edtState, edtCity, edtZipCode, edtGstNo, edtMobileNo, edtDispatchAddress;
    private Button btnCreateSupplier;
    private CShowProgress cShowProgress;
    private String strSupplierName = "", strTemplate = "", strMin = "", strMax = "", strPan_AadharNo = "",
            strRegisterAddress = "", strCity = "", strZipCode = "", strCountry = "", strResponce = "", strGstNo = "", strDispatchAddress = "",
            strMobileNo = "", strState = "", from = "Create Supplier";
    private ApiServices apiServices;
    private Spinner spinCountry, spinSupplierType, spinBusinessType;
    private int intbusinessType = 0, intSupplierType = 0;
    private SupplierMasterDo supplierMaster, supplierMasterDo;
    private Context mContext;
    private Preference preference;
    private GetData service;
    private List<SpinnerDo> SupplierTypeMasterList = new ArrayList();
    private List<SpinnerDo> Supplier_Bussiness_type_MasterList = new ArrayList();

    public CrUpdSuppliersFragment(Context mContext, Preference preference,List<SpinnerDo> SupplierTypeMasterList,List<SpinnerDo> Supplier_Bussiness_type_MasterList) {
        this.preference = preference;
        this.mContext = mContext;
        this.SupplierTypeMasterList = SupplierTypeMasterList;
        this.Supplier_Bussiness_type_MasterList = Supplier_Bussiness_type_MasterList;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_crsupplier, container, false);
        initialization(root);
        loadData();

        CAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, CountryNames);
        spinCountry.setAdapter(CAdapter);


        btnCreateSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strSupplierName = edtSupplierName.getText().toString();
                strTemplate = edtTemplate.getText().toString();
                strMin = edtMin.getText().toString();
                strMax = edtMax.getText().toString();
                strPan_AadharNo = edtPin_AadharNo.getText().toString();
                strDispatchAddress = edtDispatchAddress.getText().toString();
                strRegisterAddress = edtRegisterAddress.getText().toString();
                strCity = edtCity.getText().toString();
                strZipCode = edtZipCode.getText().toString();
                strCountry = spinCountry.getSelectedItem().toString();
                strGstNo = edtGstNo.getText().toString();
                strMobileNo = edtMobileNo.getText().toString();
                strState = edtState.getText().toString();
                intSupplierType = supplierTypeAdaptor.getPosition(spinSupplierType.getSelectedItem().toString());
                intbusinessType = businessTypeAdaptor.getPosition(spinBusinessType.getSelectedItem().toString());

                if (strSupplierName.equalsIgnoreCase("") || strTemplate.equalsIgnoreCase("") ||
                        strMin.equalsIgnoreCase("") || strMax.equalsIgnoreCase("") || strPan_AadharNo.equalsIgnoreCase("") ||
                        strDispatchAddress.equalsIgnoreCase("") || strCity.equalsIgnoreCase("") ||
                        strZipCode.equalsIgnoreCase("") || strGstNo.equalsIgnoreCase("") ||
                        strRegisterAddress.equalsIgnoreCase("")) {
                    ((MainActivity) mContext).showCustomeTopToast("Please enter all details fully.");
                } else if (strMobileNo.length() < 10) {
                    ((MainActivity) mContext).showCustomeTopToast("Please enter valid mobile no.");
                } else if (((MainActivity) mContext).isNetworkConnectionAvailable(mContext)) {
                    syncTaskForCreatingSupplier syncTaskForCreatingSupplier = new syncTaskForCreatingSupplier();
                    syncTaskForCreatingSupplier.execute();
                } else {
                    ((MainActivity) mContext).showCustomeTopToast("Please connect to Internet.");
                }
            }
        });
        return root;
    }
    private void loadData() {
        loadSupplierTypeMaster();
        loadSupplier_Bussiness_type_Master();
    }

    private void loadSupplier_Bussiness_type_Master() {
        businessType = new String[Supplier_Bussiness_type_MasterList.size()];
        for (int i = 0; i < Supplier_Bussiness_type_MasterList.size(); i++) {
            businessType[i] = Supplier_Bussiness_type_MasterList.get(i).getName();
        }
        businessTypeAdaptor = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, businessType);
        spinBusinessType.setAdapter(businessTypeAdaptor);
    }

    private void loadSupplierTypeMaster() {
        supplierType = new String[SupplierTypeMasterList.size()];
        for (int i = 0; i < SupplierTypeMasterList.size(); i++) {
            supplierType[i] = SupplierTypeMasterList.get(i).getName();
        }
        supplierTypeAdaptor = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, supplierType);
        spinSupplierType.setAdapter(supplierTypeAdaptor);
    }

    private void initialization(View root) {
        apiServices = new ApiServices();
        service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        spinCountry = root.findViewById(R.id.spinCountry);
        btnCreateSupplier = root.findViewById(R.id.btnCreateSupplier);
        edtSupplierName = root.findViewById(R.id.edtSupplierName);
        edtTemplate = root.findViewById(R.id.edtTemplate);
//        edtMin = root.findViewById(R.id.edtMin);
//        edtMax = root.findViewById(R.id.edtMax);
        edtPin_AadharNo = root.findViewById(R.id.edtPin_AadharNo);
//        edtDispatchAddress = root.findViewById(R.id.edtDispatchAddress);
//        edtRegisterAddress = root.findViewById(R.id.edtRegisterAddress);
        edtCity = root.findViewById(R.id.edtCity);
        edtZipCode = root.findViewById(R.id.edtZipCode);
        edtMobileNo = root.findViewById(R.id.edtMobileNo);
        edtGstNo = root.findViewById(R.id.edtGstNo);
        edtState = root.findViewById(R.id.edtState);
        spinSupplierType = root.findViewById(R.id.spinSupplierType);
        spinBusinessType = root.findViewById(R.id.spinBusinessType);
    }

    public class syncTaskForCreatingSupplier extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
                strResponce = apiServices.PostDatausingToken(generateJsonToString(), ServiceURLs.SUPPLIER_MASTER_CREATE, preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(mContext);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            cShowProgress.hideProgress();
            Gson gson = new Gson();
            if (strResponce.equalsIgnoreCase(AppConstants.MOBILE_NO_ALREADY_EXISTS)) {
                ((MainActivity) mContext).showCustomeTopToast(AppConstants.MOBILE_NO_ALREADY_EXISTS);
            } else if (strResponce.equalsIgnoreCase(AppConstants.SUPPLIER_NAME_ALREADY_EXISTS)) {
                ((MainActivity) mContext).showCustomeTopToast(AppConstants.SUPPLIER_NAME_ALREADY_EXISTS);
            } else if (strResponce.equalsIgnoreCase(AppConstants.PAN_NO_ALREADY_EXISTS)) {
                ((MainActivity) mContext).showCustomeTopToast(AppConstants.PAN_NO_ALREADY_EXISTS);
            } else if (strResponce.equalsIgnoreCase(AppConstants.GST_NO_ALREADY_EXISTS)) {
                ((MainActivity) mContext).showCustomeTopToast(AppConstants.GST_NO_ALREADY_EXISTS);
            } else if (strResponce.equalsIgnoreCase("Updated")) {
                ((MainActivity) mContext).showCustomDialog(mContext, getString(R.string.alert), "Changes are done", getString(R.string.OK), "", "finish");
            } else if (strResponce.equalsIgnoreCase(AppConstants.USERID_NOT_FOUND)) {
                ((MainActivity) mContext).showCustomeTopToast(AppConstants.USERID_NOT_FOUND);
            } else {
                try {
                    supplierMasterDo = gson.fromJson(strResponce, SupplierMasterDo.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }
                if (supplierMasterDo != null) {
                    ((MainActivity) mContext).showCustomeTopToast("A new supplier is added");
                    ((MainActivity) mContext).finish();
                } else {
                    ((MainActivity) mContext).showCustomeTopToast(strResponce);
                }
            }
        }
    }

    private String generateJsonToString() {
        String jsonString = "{" +
                "\"SM_Name\": \"" + strSupplierName + "\", " +
                "\"SM_Logo\": \"" + "Logo" + "\", " +
                "\"SM_Template_ID\": \"" + strTemplate + "\"," +
                "\"SM_BussinessType\": " + intbusinessType + "," +
                "\"SM_Type\": " + intSupplierType + "," +
                "\"SM_Status\": \"" + "A" + " \"," +
                "\"SM_Rating\": " + 0 + "," +
                "\"SM_Min_Delivery_Charge\": \"" + strMin + "\"," +
                "\"SM_Max_Delivery_Charge\": \"" + strMax + "\"," +
                "\"SM_UserID\": \"" + preference.getStringFromPreference(Preference.USERID, "") + "\"," +
                "\"SD_PAN\": \"" + strPan_AadharNo + "\"," +
                "\"SD_Register_Address\": \"" + strRegisterAddress + "\"," +
                "\"SD_Dispatch_Address\": \"" + strDispatchAddress + "\"," +
                "\"SD_City\": \"" + strCity + "\"," +
                "\"SD_State\": \"" + strState + "\"," +
                "\"SD_Country\": \"" + strCountry + "\"," +
                "\"SD_PINCode\": \"" + strZipCode + "\"," +
                "\"SD_GST_No\": \"" + strGstNo + "\"," +
                "\"SD_LandLine_No\": \"" + "0000000000" + "\"," +
                "\"SD_Mobile_No\": \"" + strMobileNo + "\"" + "}";
        return jsonString;
    }

}
