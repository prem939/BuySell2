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

public class SupplierItemListForWishlistAdaptor extends RecyclerView.Adapter<SupplierItemListForWishlistAdaptor.ViewHolder> {
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

    public SupplierItemListForWishlistAdaptor(Context mcontext, List<SupplierItemMasterDo> supplierListItems, LayoutInflater inflater, Preference preference, GetData service) {
        this.mcontext = mcontext;
        this.SupplierListItems = supplierListItems;
        this.inflater = inflater;
        this.preference = preference;
        this.service = service;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_list_adaptor_for_wishlist, null, false);
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

            holder.llSupplierProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mcontext).runOnUiThread(new RunshowCustomDialogs("profile", supplierItemMaster, holder));
                }
            });
            holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new syncTaskForAddCart().execute(supplierItemMaster.SIM_ID);
                }
            });
            holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SupplierListItems.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, SupplierListItems.size());
                    new syncTaskForDelete().execute(supplierItemMaster.WLM_ID);
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
        LinearLayout llSupplierProfile;
        Button btnAddToCart, btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_supplier_name = itemView.findViewById(R.id.txt_supplier_name);
            txt_to_pay = itemView.findViewById(R.id.txt_to_pay);
            myImageViewText = itemView.findViewById(R.id.myImageViewText);
            llSupplierProfile = itemView.findViewById(R.id.llSupplierProfile);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnRemove = itemView.findViewById(R.id.btnRemove);
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
            View view = inflater.inflate(R.layout.custom_popup_for_item, null);
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
            btnWishlist.setVisibility(View.GONE);
            btnAddCart.setVisibility(View.GONE);
            imgCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customDialog.dismiss();
                }
            });
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


    public class syncTaskForAddCart extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... longs) {
            strResponce = apiServices.PostData(generateJsonToAddWislist(longs[0]), ServiceURLs.ADDITEMSTOADDTOCART, preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
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

    public class syncTaskForDelete extends AsyncTask<Long, String, Void> {
        @Override
        protected Void doInBackground(Long... params) {
            strResponce = apiServices.deleteItem(ServiceURLs.REMOVEWISHLISTITEM + params[0], preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (strResponce.equalsIgnoreCase("delete")) {
                ((MainActivity) mcontext).showCustomeBottomToast("Removed...");
            } else if (strResponce.equalsIgnoreCase(AppConstants.INTERNAL_ERROR)) {
                ((MainActivity) mcontext).showCustomeBottomToast("Some thing went wrong...");
            }
        }
    }

    private String generateJsonToAddWislist(Long id) {
        String jsonString = "{" +
                "\"ItemId\": " + 0 + "," +
                "\"Item_Quantity\": " + 0 + "," +
                "\"SM_ID\": " + id + "," +
                "\"Total\": " + 0 + "," +
                "\"Status\": \"" + "string" + "\"" + "}";
        return jsonString;
    }
}
