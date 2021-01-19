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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.buysell2.Activity.MainActivity;
import com.example.buysell2.CShowProgress;
import com.example.buysell2.Do.SpinnerDo;
import com.example.buysell2.Do.SupplierItemMasterDo;
import com.example.buysell2.Do.SupplierMasterDo;
import com.example.buysell2.Interface.GetData;
import com.example.buysell2.R;
import com.example.buysell2.common.ApiServices;
import com.example.buysell2.common.AppConstants;
import com.example.buysell2.common.CalendarUtils;
import com.example.buysell2.common.Preference;
import com.example.buysell2.common.RetrofitClient;
import com.example.buysell2.common.ServiceURLs;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CrUpdSupplierItemsFragment extends Fragment {

    private EditText edtItemName, edtUOM, edtPrice, edtDeliveryCharges, edtTaxPercentage, edtTaxAmount, edtStock;
    private String strItemName = "", strUOM = "", strPrice = "", strDeliveryCharges = "", strTaxPercentage = "", strTaxAmount = "",
            strResponce = "", from = "Create Supplier item", strStoc = "", strMeasureType = "",
            measureTypes[] = {"--select measure type--","nominal", "ordinal", "interval", "ratio"};
    private Button btnCreateSupplierItem;
    private String[] category, subCategory;
    private int intCategory = 0, intSubCategory = 0;
    private ApiServices apiServices;
    private CShowProgress cShowProgress;
    private SupplierItemMasterDo supplierItemMasterDo = null,supplierItemMaster = null;
    private Spinner spinCategory, spinSubCategory, spinMeasureType;
    private List<SpinnerDo> categoryList = new ArrayList();
    private List<SpinnerDo> subCategoryList = new ArrayList();
    private ArrayAdapter<String> measureAdaptor, categoryAdapter, SubcategoryAdaptor;
    FragmentManager fragmentManager;
    private Context mContext;
    private Preference preference;
    GetData service;
    public CrUpdSupplierItemsFragment(Context mContext, Preference preference,List<SpinnerDo> categoryList,List<SpinnerDo> subCategoryList) {
        this.preference = preference;
        this.mContext = mContext;
        this.categoryList = categoryList;
        this.subCategoryList = subCategoryList;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_item, container, false);
        initialization(root);
        loadData();

        measureAdaptor = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, measureTypes);
        spinMeasureType.setAdapter(measureAdaptor);

        btnCreateSupplierItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strItemName = edtItemName.getText().toString();
                strUOM = edtUOM.getText().toString();
                strPrice = edtPrice.getText().toString();
                strDeliveryCharges = edtDeliveryCharges.getText().toString();
                strTaxPercentage = edtTaxPercentage.getText().toString();
                strTaxAmount = edtTaxAmount.getText().toString();
                strStoc = edtStock.getText().toString();
                strMeasureType = spinMeasureType.getSelectedItem().toString();
                intCategory = categoryAdapter.getPosition(spinCategory.getSelectedItem().toString());

                if (strItemName.equalsIgnoreCase("") || strUOM.equalsIgnoreCase("") || strPrice.equalsIgnoreCase("")
                        || strDeliveryCharges.equalsIgnoreCase("") || strTaxPercentage.equalsIgnoreCase("") || strTaxAmount.equalsIgnoreCase("")
                        || strMeasureType.equals("--select measure type--")) {
                    ((MainActivity)mContext).showCustomeTopToast("Please enter all details fully.");
                } else if ( ((MainActivity)mContext).isNetworkConnectionAvailable(mContext)) {
                    if (from.equalsIgnoreCase(AppConstants.FORCREATESUPPLIERITEM)) {
                        syncTaskForCreatingSupplierItem syncTaskForCreatingSupplieritem = new syncTaskForCreatingSupplierItem();
                        syncTaskForCreatingSupplieritem.execute();
                    } else if (from.equalsIgnoreCase(AppConstants.FOREDITSUPPLIERITEM)) {
                        if (strItemName.equalsIgnoreCase(supplierItemMaster.SIM_IT_Name) && strUOM.equalsIgnoreCase(supplierItemMaster.SIM_IT_UOM) && strPrice.equalsIgnoreCase("" + supplierItemMaster.SIM_IT_Price)
                                && strDeliveryCharges.equalsIgnoreCase("" + supplierItemMaster.SIM_IT_Delivery_Charge) &&
                                strTaxAmount.equalsIgnoreCase("" + supplierItemMaster.SIM_Tax_Amount) && strTaxPercentage.equalsIgnoreCase("" + supplierItemMaster.SIM_Tax_Percentage)
                                && strStoc.equalsIgnoreCase("" + supplierItemMaster.SIM_Stock_Avilable) && categoryAdapter.getPosition(spinCategory.getSelectedItem().toString()) == supplierItemMaster.SIM_Category &&
                                spinMeasureType.getSelectedItem().toString().equalsIgnoreCase(supplierItemMaster.SIM_MeasureType)) {
                            ((MainActivity)mContext).showCustomeTopToast("No Changes are appear.");
                        } else {
                            syncTaskForCreatingSupplierItem syncTaskForCreatingSupplieritem = new syncTaskForCreatingSupplierItem();
                            syncTaskForCreatingSupplieritem.execute();
                        }
                    }
                } else {
                    ((MainActivity)mContext).showCustomeTopToast("Connect to Internet.");
                }
            }
        });
        return root;
    }

    private void initialization(View root) {
        apiServices = new ApiServices();
        service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        edtItemName = root.findViewById(R.id.edtItemName);
        edtUOM = root.findViewById(R.id.edtUOM);
        edtPrice = root.findViewById(R.id.edtPrice);
        edtDeliveryCharges = root.findViewById(R.id.edtDeliveryCharges);
        edtTaxPercentage = root.findViewById(R.id.edtTaxPercentage);
        edtTaxAmount = root.findViewById(R.id.edtTaxAmount);
        btnCreateSupplierItem = root.findViewById(R.id.btnCreateSupplierItem);
        edtStock = root.findViewById(R.id.edtStock);
        spinCategory = root.findViewById(R.id.spinCategory);
        spinSubCategory = root.findViewById(R.id.spinSubCategory);
        spinMeasureType = root.findViewById(R.id.spinMeasureType);
    }
    private void loadData() {
        loadCatogeryList();
        loadSubCatogeryList();
    }
    private void loadCatogeryList() {
        category = new String[categoryList.size()];
        for (int i = 0; i < categoryList.size(); i++) {
            category[i] = categoryList.get(i).getName();
        }
        categoryAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, category);
        spinCategory.setAdapter(categoryAdapter);
    }

    private void loadSubCatogeryList() {
        subCategory = new String[subCategoryList.size()];
        for (int i = 0; i < subCategoryList.size(); i++) {
            subCategory[i] = subCategoryList.get(i).getName();
        }
        SubcategoryAdaptor = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, subCategory);
        spinSubCategory.setAdapter(SubcategoryAdaptor);
    }
    public class syncTaskForCreatingSupplierItem extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
                strResponce = apiServices.PostDatausingToken(generateJsonToString(), ServiceURLs.SUPPLIER_ITEM_CREATE, preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cShowProgress.hideProgress();
            Gson gson = new Gson();
            if (strResponce.equalsIgnoreCase(AppConstants.INTERNAL_ERROR) && !strResponce.equalsIgnoreCase("")) {
                Toast.makeText(getActivity(), strResponce, Toast.LENGTH_SHORT).show();
            } else if (strResponce.equalsIgnoreCase("Updated")) {
                ((MainActivity) mContext).showCustomDialog(mContext, getString(R.string.warning), "Changes are done.", getString(R.string.OK), "", "finish");
            } else {
                try {
                    supplierItemMasterDo = gson.fromJson(strResponce, SupplierItemMasterDo.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }

                if (supplierItemMasterDo != null) {
                    ((MainActivity) mContext).showCustomDialog(mContext, getString(R.string.warning), supplierItemMasterDo.SIM_IT_Name+" item created successfully", getString(R.string.OK), "", "finish");
                } else {
                    ((MainActivity) mContext).showCustomeTopToast("Something went wrong.");
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(mContext);
        }
    }

    private String generateJsonToString() {
        String jsonString = "{" +
                "\"SIM_SM_ID\": \"" + preference.getStringFromPreference(Preference.USERID, "") + "\", " +
//                "\"SIM_IT_ID\": \"" + preference.getStringFromPreference(Preference.USERID,"") + "\", " +
                "\"SIM_IT_Name\": \"" + strItemName + "\"," +
                "\"SIM_Category\": " + intCategory + "," +
                "\"SIM_SubCategory\": " + intSubCategory + "," +
//                "\"SIM_IT_Pic\": \""+"logo"+" \"," +
                "\"SIM_IT_UOM\":  \"" + strUOM + "\"," +
                "\"SIM_IT_Price\": " + strPrice + "," +
                "\"SIM_IT_Delivery_Charge\": " + strDeliveryCharges + "," +
                "\"SIM_Tax_Percentage\":  " + strTaxPercentage + "," +
                "\"SIM_Tax_Amount\": " + strTaxAmount + "," +
                "\"SIM_MeasureType\": \"" + strMeasureType + "\"," +
                "\"SIM_Stock_Avilable\": " + Integer.parseInt(strStoc) + "," +
                "\"SIM_Status\": \"" + "A" + "\"," +
                "\"SIM_Created_By\": \"" + preference.getStringFromPreference(Preference.USERID, "") + "\"," +
                "\"SM_Created_Date\": \"" + CalendarUtils.getCurrentDateTime() + "\"" + "}";
        return jsonString;
    }
}
