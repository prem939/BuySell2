package com.example.buysell2.Adaptor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.example.buysell2.Do.SupplierMasterDo;
import com.example.buysell2.Interface.GetData;
import com.example.buysell2.R;
import com.example.buysell2.common.ApiServices;
import com.example.buysell2.common.AppConstants;
import com.example.buysell2.common.CustomDialog;
import com.example.buysell2.common.Preference;
import com.example.buysell2.common.RetrofitClient;
import com.example.buysell2.common.ServiceURLs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupplierListAdaptorForSearch extends RecyclerView.Adapter<SupplierListAdaptorForSearch.ViewHolder> {
    private Context mcontext;
    private List<SupplierMasterDo> SuppliersList;
    public LayoutInflater inflater;
    public Preference preference;
    public CustomDialog customDialog;
    private TextView txtName, txtBsnType, txtType, txtRegAdd, txtDisAdd, txtMobileNo;
    private ImageView imgCancel;
    private List<SpinnerDo> SupplierTypeMasterList = new ArrayList();
    private List<SpinnerDo> Supplier_Bussiness_type_MasterList = new ArrayList();
    public SupplierListAdaptorForSearch(Context mcontext, List<SupplierMasterDo> SuppliersList, LayoutInflater inflater, Preference preference,List<SpinnerDo> Supplier_Bussiness_type_MasterList,List<SpinnerDo> SupplierTypeMasterList) {
        this.mcontext = mcontext;
        this.SuppliersList = SuppliersList;
        this.inflater = inflater;
        this.preference = preference;
        this.SupplierTypeMasterList = SupplierTypeMasterList;
        this.Supplier_Bussiness_type_MasterList = Supplier_Bussiness_type_MasterList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.supplier_list_adaptor_search, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final SupplierMasterDo supplierMaster = SuppliersList.get(position);
        if (supplierMaster != null) {
            holder.txt_SM_Name.setText(supplierMaster.SM_Name);
            holder.txt_description.setText(supplierMaster.SD_Register_Address + ", " + supplierMaster.SD_City + ", " + supplierMaster.SD_State + ", " + supplierMaster.SD_Country + ", " + supplierMaster.SD_PINCode + " ," + supplierMaster.SD_Mobile_No);
            holder.myImageViewText.setText(("" + supplierMaster.SM_Name.charAt(0) + "" + supplierMaster.SM_Name.charAt(1)).toUpperCase());

            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mcontext).showCustomeTopToast("need to implement.");
                }
            });

            holder.myImageViewText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mcontext instanceof MainActivity){
                        ((MainActivity) mcontext).runOnUiThread(new RunshowCustomDialogs("profile", supplierMaster, holder));
                    }else if(mcontext instanceof SearchResultActivity) {
                        ((SearchResultActivity) mcontext).runOnUiThread(new RunshowCustomDialogs("profile", supplierMaster, holder));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return SuppliersList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_SM_Name, txt_description, txt_SD_Mobile_No, myImageViewText;
        ImageView img_SM_Logo, imgCell, imgEdit, imgDelete, imgFav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_SM_Name = itemView.findViewById(R.id.txt_SM_Name);
            txt_description = itemView.findViewById(R.id.txt_description);
            txt_SD_Mobile_No = itemView.findViewById(R.id.txt_SD_Mobile_No);
            myImageViewText = itemView.findViewById(R.id.myImageViewText);
            img_SM_Logo = itemView.findViewById(R.id.img_SM_Logo);
            imgCell = itemView.findViewById(R.id.imgCell);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgDelete = itemView.findViewById(R.id.imgDelete);
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
        private SupplierMasterDo supplierMaster;
        private int position = 0;
        ViewHolder holder;

        public RunshowCustomDialogs(Context context, String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, boolean isCancelable, SupplierMasterDo supplierMaster, int position, ViewHolder holder) {
            this.strTitle = strTitle;
            this.strMessage = strMessage;
            this.firstBtnName = firstBtnName;
            this.secondBtnName = secondBtnName;
            this.isCancelable = isCancelable;
            this.supplierMaster = supplierMaster;
            this.position = position;
            this.holder = holder;
        }

        public RunshowCustomDialogs(String from, SupplierMasterDo supplierMaster, ViewHolder holder) {
            this.supplierMaster = supplierMaster;
            this.holder = holder;
            this.from = from;
        }

        @Override
        public void run() {
            if (customDialog != null && customDialog.isShowing())
                customDialog.dismiss();
            View view = inflater.inflate(R.layout.custom_popup_for_supplier_profile, null);
            customDialog = new CustomDialog(mcontext, view, preference
                    .getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 60,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);
            customDialog.setCancelable(isCancelable);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtBsnType = (TextView) view.findViewById(R.id.txtBsnType);
            txtType = (TextView) view.findViewById(R.id.txtType);
            txtRegAdd = (TextView) view.findViewById(R.id.txtRegAdd);
            txtDisAdd = (TextView) view.findViewById(R.id.txtDisAdd);
            txtMobileNo = (TextView) view.findViewById(R.id.txtMobileNo);
            imgCancel = view.findViewById(R.id.imgCancel);

            txtBsnType.setText(Supplier_Bussiness_type_MasterList.get(supplierMaster.SM_BussinessType).getName());
            txtType.setText(SupplierTypeMasterList.get(supplierMaster.SM_Type).getName());
            txtName.setText(supplierMaster.SM_Name);
            txtRegAdd.setText(supplierMaster.SD_Register_Address + ", " + supplierMaster.SD_City + ", "
                    + supplierMaster.SD_State + ", " + supplierMaster.SD_Country + ", " + supplierMaster.SD_PINCode);
            txtDisAdd.setText(supplierMaster.SD_Dispatch_Address);
            txtMobileNo.setText("" + supplierMaster.SD_Mobile_No);
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
}