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
import com.example.buysell2.Activity.SearchResultActivity;
import com.example.buysell2.CShowProgress;
import com.example.buysell2.DatabaseHelper;
import com.example.buysell2.Do.SpinnerDo;
import com.example.buysell2.Do.SupplierItemMasterDo;
import com.example.buysell2.Do.SupplierMasterDo;
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

public class SupplierItemListAdaptorForSearch extends RecyclerView.Adapter<SupplierItemListAdaptorForSearch.ViewHolder> {
    private Context mcontext;
    private List<SupplierItemMasterDo> SupplierListItems;
    private ApiServices apiServices = new ApiServices();
    public CustomDialog customDialog;
    public LayoutInflater inflater;
    public Preference preference;
    private GetData service;
    private Button btnWishlist, btnAddCart;
    private ImageView imgCancel;
    private String strResponce = "";
    private TextView txtName, txtCategory, txtSubCategory, txtUOM, txtPrice, txtDeliveryCharge, txtTaxPercentage, txtTaxAmount, txtStockAvilable, txtMeasureType;
    private List<SpinnerDo> categoryList = new ArrayList();
    private List<SpinnerDo> subCategoryList = new ArrayList();

    public SupplierItemListAdaptorForSearch(Context mcontext, List<SupplierItemMasterDo> supplierListItems, LayoutInflater inflater, Preference preference, List<SpinnerDo> categoryList, List<SpinnerDo> subCategoryList) {
        this.mcontext = mcontext;
        this.SupplierListItems = supplierListItems;
        this.inflater = inflater;
        this.preference = preference;
        this.categoryList = categoryList;
        this.subCategoryList = subCategoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_list_adaptor_search, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mcontext).showCustomeTopToast("need to implement.");
                }
            });
            holder.myImageViewText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mcontext instanceof MainActivity) {
                        ((MainActivity) mcontext).runOnUiThread(new RunshowCustomDialogs("profile", supplierItemMaster, holder));
                    } else if (mcontext instanceof SearchResultActivity) {
                        ((SearchResultActivity) mcontext).runOnUiThread(new RunshowCustomDialogs("profile", supplierItemMaster, holder));
                    }
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
        ImageView imgFav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_supplier_name = itemView.findViewById(R.id.txt_supplier_name);
            txt_to_pay = itemView.findViewById(R.id.txt_to_pay);
            myImageViewText = itemView.findViewById(R.id.myImageViewText);
            imgFav = itemView.findViewById(R.id.imgFav);
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

            txtCategory.setText(categoryList.get(supplierItemMaster.SIM_Category).getName());
            txtSubCategory.setText(subCategoryList.get(supplierItemMaster.SIM_SubCategory).getName());
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
                    new syncTaskForWishList().execute(supplierItemMaster.SIM_ID, Long.parseLong(supplierItemMaster.SIM_SM_ID));
                }
            });

            btnAddCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customDialog.dismiss();
//                    new syncTaskForAddCart().execute(supplierItemMaster.SIM_ID);
                }
            });
            try {
                if (!customDialog.isShowing())
                    customDialog.showCustomDialog(mcontext);
            } catch (Exception e) {
            }
        }
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

    public class syncTaskForWishList extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... longs) {
            strResponce = apiServices.PostData(generateJsonToAddWislist(longs[0], longs[1]), ServiceURLs.ADDITEMSTOWISLIST, preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (strResponce.equalsIgnoreCase("success")) {
                ((MainActivity) mcontext).showCustomeBottomToast("Add to Wishlist...");
            } else if (strResponce.equalsIgnoreCase(AppConstants.INTERNAL_ERROR)) {
                ((MainActivity) mcontext).showCustomeBottomToast("Some thing went wrong...");
            } else {
                ((MainActivity) mcontext).showCustomeBottomToast(strResponce);
            }
        }
    }

    public class syncTaskForAddCart extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... longs) {
            strResponce = apiServices.PostData(generateJsonToAddWislist(longs[0], longs[1]), ServiceURLs.ADDITEMSTOADDTOCART, preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
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

    private String generateJsonToAddWislist(Long id, Long sm_id) {
        String jsonString = "{" +
                "\"ItemId\": " + id + "," +
                "\"Item_Quantity\": " + 0 + "," +
                "\"SM_ID\": " + sm_id + "," +
                "\"Total\": " + 0 + "," +
                "\"Status\": \"" + "A" + "\"" + "}";
        return jsonString;
    }
}
