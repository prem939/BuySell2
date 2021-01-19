package com.example.buysell2.Adaptor;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.buysell2.Activity.MainActivity;
import com.example.buysell2.CShowProgress;
import com.example.buysell2.Do.SpinnerDo;
import com.example.buysell2.Do.SupplierItemMasterDo;
import com.example.buysell2.Interface.GetData;
import com.example.buysell2.R;
import com.example.buysell2.common.ApiServices;
import com.example.buysell2.common.AppConstants;
import com.example.buysell2.common.CalendarUtils;
import com.example.buysell2.common.CustomDialog;
import com.example.buysell2.common.Preference;
import com.example.buysell2.common.RetrofitClient;
import com.example.buysell2.common.ServiceURLs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class SupplierItemListAdaptor extends RecyclerView.Adapter<SupplierItemListAdaptor.ViewHolder> {
    private Context mcontext;
    private List<SupplierItemMasterDo> SupplierListItems;
    private ApiServices apiServices = new ApiServices();
    public CustomDialog customDialog;
    public LayoutInflater inflater;
    public Preference preference;
    private EditText edtItemName, edtUOM, edtPrice, edtDeliveryCharges, edtTaxPercentage, edtTaxAmount, edtStock;
    private String strItemName = "", strUOM = "", strPrice = "", strDeliveryCharges = "", strTaxPercentage = "", strTaxAmount = "",
            strResponce = "", from = "Create Supplier item", strStoc = "", strMeasureType = "",
            measureTypes[] = {"nominal", "ordinal", "interval", "ratio"};
    private Button btnCreateSupplierItem, btnUpdItem, btnCancel, btnWishlist, btnAddCart;
    private String[] category, subCategory;
    private int intCategory = 0, intSubCategory = 0;
    private Spinner spinCategory, spinSubCategory, spinMeasureType;
    private List<SpinnerDo> categoryList = new ArrayList();
    private List<SpinnerDo> subCategoryList = new ArrayList();
    private ArrayAdapter<String> measureAdaptor, categoryAdapter, SubcategoryAdaptor;
    private ImageView imgCancel;
    private LinearLayout llBtns;
    private CShowProgress cShowProgress;
    private GetData service;
    private TextView txtName, txtCategory, txtSubCategory, txtUOM, txtPrice, txtDeliveryCharge, txtTaxPercentage, txtTaxAmount, txtStockAvilable, txtMeasureType;

    public SupplierItemListAdaptor(Context mcontext, List<SupplierItemMasterDo> supplierListItems, LayoutInflater inflater, Preference preference, GetData service) {
        this.mcontext = mcontext;
        this.SupplierListItems = supplierListItems;
        this.inflater = inflater;
        this.preference = preference;
        this.service = service;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_list_adaptor, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final SupplierItemMasterDo supplierItemMaster = SupplierListItems.get(position);
        if (supplierItemMaster != null) {
            holder.txt_supplier_name.setText(supplierItemMaster.SIM_IT_Name);
            holder.txt_to_pay.setText("" + supplierItemMaster.SIM_IT_Price + " ₹");
            holder.myImageViewText.setText(("" + supplierItemMaster.SIM_IT_Name.charAt(0)).toUpperCase());
            holder.imgDelete.setTag(supplierItemMaster);
            holder.imgEdit.setTag(supplierItemMaster);
            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mcontext).showCustomeTopToast("need to implement.");
                }
            });

            holder.imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView img = (ImageView) view;
                    SupplierItemMasterDo supplierItemMaster = (SupplierItemMasterDo) img.getTag();
                    ((MainActivity) mcontext).runOnUiThread(new RunshowCustomDialogs("update", supplierItemMaster, holder));
                }
            });


            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView img = (ImageView) view;
                    SupplierItemMasterDo supplierItemMaster = (SupplierItemMasterDo) img.getTag();
                    ((MainActivity) mcontext).runOnUiThread(new RunshowCustomDialogs(mcontext, "Alert!", "Are to sure to delete this " + supplierItemMaster.SIM_IT_Name + " item" + " ?", "Yes", "No", "", true, supplierItemMaster, position, holder));
                }
            });
            holder.llSupplierProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mcontext).runOnUiThread(new RunshowCustomDialogs("profile", supplierItemMaster, holder));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return SupplierListItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_supplier_name, txt_to_pay, myImageViewText;
        ImageView imgEdit, imgDelete, imgFav;
        LinearLayout llSupplierProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_supplier_name = itemView.findViewById(R.id.txt_supplier_name);
            txt_to_pay = itemView.findViewById(R.id.txt_to_pay);
            myImageViewText = itemView.findViewById(R.id.myImageViewText);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            imgFav = itemView.findViewById(R.id.imgFav);
            llSupplierProfile = itemView.findViewById(R.id.llSupplierProfile);
        }
    }

    public class syncTaskForDelete extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            apiServices.deleteItem(ServiceURLs.SUPPLIER_ITEM_DELELTE + "?id=" + params[0], preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            return null;
        }
    }

    class RunshowCustomDialogs implements Runnable {
        private String strTitle;// Title of the dialog
        private String strMessage;// Message to be shown in dialog
        private String firstBtnName;
        private String secondBtnName;
        private String from = "";
        private String params;
        private Object paramateres;
        private boolean isCancelable = false;
        SupplierItemMasterDo supplierItemMaster;
        private int position = 0;
        ViewHolder holder;

        public RunshowCustomDialogs(Context context, String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, boolean isCancelable, SupplierItemMasterDo supplierItemMaster, int position, ViewHolder holder) {
            this.strTitle = strTitle;
            this.strMessage = strMessage;
            this.firstBtnName = firstBtnName;
            this.secondBtnName = secondBtnName;
            this.isCancelable = isCancelable;
            this.supplierItemMaster = supplierItemMaster;
            this.position = position;
            this.holder = holder;
        }

        public RunshowCustomDialogs(String from, SupplierItemMasterDo supplierItemMaster, ViewHolder holder) {
            this.supplierItemMaster = supplierItemMaster;
            this.holder = holder;
            this.from = from;
        }

        @Override
        public void run() {
            if (customDialog != null && customDialog.isShowing())
                customDialog.dismiss();
            View view;
            if (from.equals("update")) {
                view = inflater.inflate(R.layout.fragment_create_item, null);
                customDialog = new CustomDialog(mcontext, view, preference
                        .getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 80, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                customDialog.setCancelable(true);
                initialization(view);
                if (supplierItemMaster != null) {
                    edtItemName.setText(supplierItemMaster.SIM_IT_Name);
                    edtDeliveryCharges.setText("" + supplierItemMaster.SIM_IT_Delivery_Charge);
                    edtUOM.setText(supplierItemMaster.SIM_IT_UOM);
                    edtPrice.setText("" + supplierItemMaster.SIM_IT_Price);
                    edtTaxAmount.setText("" + supplierItemMaster.SIM_Tax_Amount);
                    edtTaxPercentage.setText("" + supplierItemMaster.SIM_Tax_Percentage);
                    edtStock.setText(supplierItemMaster.SIM_Stock_Avilable);
                    spinCategory.setSelection(supplierItemMaster.SIM_Category);
                    spinMeasureType.setSelection(measureAdaptor.getPosition(supplierItemMaster.SIM_MeasureType));
                }

                btnUpdItem.setOnClickListener(new View.OnClickListener() {
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
                        if (strItemName.equalsIgnoreCase(supplierItemMaster.SIM_IT_Name) && strUOM.equalsIgnoreCase(supplierItemMaster.SIM_IT_UOM) && strPrice.equalsIgnoreCase("" + supplierItemMaster.SIM_IT_Price)
                                && strDeliveryCharges.equalsIgnoreCase("" + supplierItemMaster.SIM_IT_Delivery_Charge) &&
                                strTaxAmount.equalsIgnoreCase("" + supplierItemMaster.SIM_Tax_Amount) && strTaxPercentage.equalsIgnoreCase("" + supplierItemMaster.SIM_Tax_Percentage)
                                && strStoc.equalsIgnoreCase("" + supplierItemMaster.SIM_Stock_Avilable) && categoryAdapter.getPosition(spinCategory.getSelectedItem().toString()) == supplierItemMaster.SIM_Category &&
                                spinMeasureType.getSelectedItem().toString().equalsIgnoreCase(supplierItemMaster.SIM_MeasureType)) {
                            ((MainActivity) mcontext).showCustomeTopToast("No Changes are appear.");
                        } else {
                            syncTaskForUpdateItem syncTaskForUpdateItem = new syncTaskForUpdateItem();
                            syncTaskForUpdateItem.execute(supplierItemMaster.SIM_ID);
                            customDialog.dismiss();
                        }
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });
                imgCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                    }
                });
            } else if (from.equals("profile")) {
                view = inflater.inflate(R.layout.custom_popup_for_item, null);
                customDialog = new CustomDialog(mcontext, view, preference
                        .getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 60,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);
                customDialog.setCancelable(isCancelable);
                initialization2(view);
                loadSpinData(supplierItemMaster.SIM_Category, supplierItemMaster.SIM_SubCategory);
                txtName.setText(supplierItemMaster.SIM_IT_Name);
                txtUOM.setText(supplierItemMaster.SIM_IT_UOM);
                txtPrice.setText("" + supplierItemMaster.SIM_IT_Price + " ₹");
                txtDeliveryCharge.setText("" + supplierItemMaster.SIM_IT_Delivery_Charge);
                txtTaxPercentage.setText("" + supplierItemMaster.SIM_Tax_Percentage);
                txtTaxAmount.setText("" + supplierItemMaster.SIM_Tax_Amount);
                txtStockAvilable.setText("" + supplierItemMaster.SIM_Stock_Avilable);
                txtMeasureType.setText(supplierItemMaster.SIM_MeasureType);

                imgCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                    }
                });
                btnWishlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                        new syncTaskForWishList().execute(supplierItemMaster.SIM_ID,Long.parseLong(supplierItemMaster.SIM_SM_ID));
                    }
                });

                btnAddCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                        new syncTaskForAddCart().execute(supplierItemMaster.SIM_ID);
                    }
                });
            } else {
                view = inflater.inflate(R.layout.custom_common_popup, null);

                customDialog = new CustomDialog(mcontext, view, preference
                        .getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 60,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);
                customDialog.setCancelable(isCancelable);
                TextView tvTitle = (TextView) view.findViewById(R.id.tvTitlePopup);
                TextView tvMessage = (TextView) view.findViewById(R.id.tvMessagePopup);
                Button btnYes = (Button) view.findViewById(R.id.btnYesPopup);
                Button btnNo = (Button) view.findViewById(R.id.btnNoPopup);

                tvTitle.setText("" + strTitle);
                tvMessage.setText("" + strMessage);
                btnYes.setText("" + firstBtnName);
                if (secondBtnName.equalsIgnoreCase("")) {
                    btnNo.setVisibility(View.GONE);
                } else {
                    btnNo.setText("" + secondBtnName);
                }
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        SupplierListItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, SupplierListItems.size());

                        new syncTaskForDelete().execute("" + supplierItemMaster.SIM_ID);
                        ((MainActivity) mcontext).showCustomeBottomToast("Deleted " + supplierItemMaster.SIM_IT_Name);
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });

            }
            try {
                if (!customDialog.isShowing())
                    customDialog.showCustomDialog(mcontext);
            } catch (Exception e) {
            }
        }
    }

    private void loadSpinData(final int intCategory, final int intSubCategory) {
        Call<List<SpinnerDo>> call = service.getAllProdectCategoryMaster("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        call.enqueue(new Callback<List<SpinnerDo>>() {
            @Override
            public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                categoryList = response.body();
                txtCategory.setText(categoryList.get(intCategory).getName());
            }

            @Override
            public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                ((MainActivity) mcontext).showCustomeBottomToast("Unable to load");
            }
        });
        Call<List<SpinnerDo>> call2 = service.getAllProdectSubCategoryMaster("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        call2.enqueue(new Callback<List<SpinnerDo>>() {
            @Override
            public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                subCategoryList = response.body();
                txtSubCategory.setText(subCategoryList.get(intSubCategory).getName());
            }

            @Override
            public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                ((MainActivity) mcontext).showCustomeBottomToast("Unable to load");
            }
        });
    }

    private void initialization2(View view) {
        txtName = (TextView) view.findViewById(R.id.txtName);
        txtCategory = (TextView) view.findViewById(R.id.txtCategory);
        txtSubCategory = (TextView) view.findViewById(R.id.txtSubCategory);
        txtUOM = (TextView) view.findViewById(R.id.txtUOM);
        txtPrice = (TextView) view.findViewById(R.id.txtPrice);
        txtDeliveryCharge = (TextView) view.findViewById(R.id.txtDeliveryCharge);
        txtTaxPercentage = (TextView) view.findViewById(R.id.txtTaxPercentage);
        txtTaxAmount = (TextView) view.findViewById(R.id.txtTaxAmount);
        txtStockAvilable = (TextView) view.findViewById(R.id.txtStockAvilable);
        txtMeasureType = (TextView) view.findViewById(R.id.txtMeasureType);
        imgCancel = view.findViewById(R.id.imgCancel);
        btnWishlist = view.findViewById(R.id.btnWishlist);
        btnAddCart = view.findViewById(R.id.btnAddCart);
    }

    private void initialization(View root) {
        apiServices = new ApiServices();
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
        btnUpdItem = root.findViewById(R.id.btnUpdItem);
        btnCancel = root.findViewById(R.id.btnCancel);
        imgCancel = root.findViewById(R.id.imgCancel);
        llBtns = root.findViewById(R.id.llBtns);

        llBtns.setVisibility(View.VISIBLE);
        btnCreateSupplierItem.setVisibility(View.GONE);
        imgCancel.setVisibility(View.VISIBLE);

        loadData();
        measureAdaptor = new ArrayAdapter<>(mcontext, android.R.layout.simple_spinner_dropdown_item, measureTypes);
        spinMeasureType.setAdapter(measureAdaptor);
    }

    private void loadData() {
        loadCatogeryList();
        loadSubCatogeryList();
    }


    private void loadCatogeryList() {
        Call<List<SpinnerDo>> call = service.getAllProdectCategoryMaster("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        call.enqueue(new Callback<List<SpinnerDo>>() {
            @Override
            public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                categoryList = response.body();
                category = new String[categoryList.size()];
                for (int i = 0; i < categoryList.size(); i++) {
                    category[i] = categoryList.get(i).getName();
                }
                categoryAdapter = new ArrayAdapter<String>(mcontext, android.R.layout.simple_spinner_dropdown_item, category);
                spinCategory.setAdapter(categoryAdapter);
            }

            @Override
            public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                ((MainActivity) mcontext).showCustomeBottomToast("Unable to load");
            }
        });
    }

    private void loadSubCatogeryList() {
        Call<List<SpinnerDo>> call = service.getAllProdectSubCategoryMaster("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        call.enqueue(new Callback<List<SpinnerDo>>() {
            @Override
            public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                subCategoryList = response.body();
                subCategory = new String[subCategoryList.size()];
                for (int i = 0; i < subCategoryList.size(); i++) {
                    subCategory[i] = subCategoryList.get(i).getName();
                }
                SubcategoryAdaptor = new ArrayAdapter<String>(mcontext, android.R.layout.simple_spinner_dropdown_item, subCategory);
                spinSubCategory.setAdapter(SubcategoryAdaptor);
            }

            @Override
            public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                ((MainActivity) mcontext).showCustomeBottomToast("Unable to load");
            }
        });
    }

    private String generateJsonToStringForEdit(Long id) {
        String jsonString = "{" +
                "\"SIM_ID\": " + id + "," +
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
                "\"SIM_Modified_Date\": \"" + CalendarUtils.getCurrentDateTime() + "\"" + "}";
        return jsonString;
    }

    public void loadSupplierList() {
        GetData service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        Call<List<SupplierItemMasterDo>> call = service.getAllSuppliersItems("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        cShowProgress = CShowProgress.getInstance();
        cShowProgress.showProgress(mcontext);
        call.enqueue(new Callback<List<SupplierItemMasterDo>>() {
            @Override
            public void onResponse(Call<List<SupplierItemMasterDo>> call, retrofit2.Response<List<SupplierItemMasterDo>> response) {
                loadDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<SupplierItemMasterDo>> call, Throwable throwable) {
                ((MainActivity) mcontext).showCustomeBottomToast("Unable to load");
            }
        });
        cShowProgress.hideProgress();
    }

    public void loadDataList(List<SupplierItemMasterDo> supplierListItems) {
        this.SupplierListItems = supplierListItems;
        notifyDataSetChanged();
    }

    public class syncTaskForUpdateItem extends AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... params) {
            strResponce = apiServices.UpdateItemUsingToken(ServiceURLs.UPDATE_SUPPLIER_ITEM + "?id=" + params[0], generateJsonToStringForEdit(params[0]), preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (strResponce.equalsIgnoreCase("Updated")) {
                ((MainActivity) mcontext).showCustomeBottomToast("Changes are done");
                loadSupplierList();
            } else if (strResponce.equalsIgnoreCase(AppConstants.INTERNAL_ERROR)) {
                ((MainActivity) mcontext).showCustomeTopToast(AppConstants.INTERNAL_ERROR);
            }
        }
    }

    public class syncTaskForWishList extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... longs) {
            strResponce = apiServices.PostData(generateJsonToAddWislist(longs[0],longs[1]), ServiceURLs.ADDITEMSTOWISLIST, preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (strResponce.equalsIgnoreCase("success")) {
                ((MainActivity) mcontext).showCustomeBottomToast("Add to Wishlist...");
            } else if (strResponce.equalsIgnoreCase(AppConstants.INTERNAL_ERROR)) {
                ((MainActivity) mcontext).showCustomeBottomToast("Some thing went wrong...");
            }else  {
                ((MainActivity) mcontext).showCustomeBottomToast(strResponce);
            }
        }
    }

    public class syncTaskForAddCart extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... longs) {
            strResponce = apiServices.PostData(generateJsonToAddWislist(longs[0],longs[1]), ServiceURLs.ADDITEMSTOADDTOCART, preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (strResponce.equalsIgnoreCase("success")) {
                ((MainActivity) mcontext).showCustomeBottomToast("Added to cart...");
            } else if (strResponce.equalsIgnoreCase(AppConstants.INTERNAL_ERROR)) {
                ((MainActivity) mcontext).showCustomeBottomToast("Some thing went wrong...");
            }
        }
    }

    private String generateJsonToAddWislist(Long id,Long sm_id) {
        String jsonString = "{" +
                "\"ItemId\": " + id + "," +
                "\"Item_Quantity\": " + 0 + "," +
                "\"SM_ID\": " + sm_id + "," +
                "\"Total\": " + 0 + "," +
                "\"Status\": \"" + "A" + "\"" + "}";
        return jsonString;
    }
}
