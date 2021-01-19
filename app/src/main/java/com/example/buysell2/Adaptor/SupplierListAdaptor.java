package com.example.buysell2.Adaptor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.buysell2.Activity.MainActivity;
import com.example.buysell2.CShowProgress;
import com.example.buysell2.Do.SpinnerDo;
import com.example.buysell2.Do.SupplierMasterDo;
import com.example.buysell2.Fragment.SupplierListFragment;
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

public class SupplierListAdaptor extends RecyclerView.Adapter<SupplierListAdaptor.ViewHolder> {
    private Context mcontext;
    private List<SupplierMasterDo> SuppliersList;
    private ApiServices apiServices = new ApiServices();
    public CustomDialog customDialog;
    public LayoutInflater inflater;
    public Preference preference;
    private String CountryNames[] = {"--select country--", "INDIA", "PAKISTAN", "CHINA", "SRILANKA", "BANGLADESH", "USA", "UK"}, supplierType[], businessType[];
    private ArrayAdapter<String> CAdapter, supplierTypeAdaptor, businessTypeAdaptor;
    private Spinner spinCountry, spinSupplierType, spinBusinessType;
    private EditText edtSupplierName, edtTemplate, edtMin, edtMax, edtPin_AadharNo,
            edtRegisterAddress, edtState, edtCity, edtZipCode, edtGstNo, edtMobileNo, edtDispatchAddress;
    private Button btnCreateSupplier, btnUpdateSupplier, btnCancel;
    private LinearLayout llBtns;
    private String strSupplierName = "", strTemplate = "", strMin = "", strMax = "", strPan_AadharNo = "",
            strRegisterAddress = "", strCity = "", strZipCode = "", strCountry = "", strResponce = "", strGstNo = "", strDispatchAddress = "",
            strMobileNo = "", strState = "";
    private int intbusinessType = 0, intSupplierType = 0;
    private CShowProgress cShowProgress;
    private ImageView imgCancel;
    private GetData service;
    private List<SpinnerDo> SupplierTypeMasterList = new ArrayList();
    private List<SpinnerDo> Supplier_Bussiness_type_MasterList = new ArrayList();
    private TextView txtName, txtBsnType, txtType, txtRegAdd, txtDisAdd, txtMobileNo;

    public SupplierListAdaptor(Context mcontext, List<SupplierMasterDo> SuppliersList, LayoutInflater inflater, Preference preference, GetData service) {
        this.mcontext = mcontext;
        this.SuppliersList = SuppliersList;
        this.inflater = inflater;
        this.preference = preference;
        this.service = service;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.supplier_list_adaptor, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final SupplierMasterDo supplierMaster = SuppliersList.get(position);
        if (supplierMaster != null) {
            holder.txt_SM_Name.setText(supplierMaster.SM_Name);
            holder.txt_description.setText(supplierMaster.SD_Register_Address + ", " + supplierMaster.SD_City + ", " + supplierMaster.SD_State + ", " + supplierMaster.SD_Country + ", " + supplierMaster.SD_PINCode);
            holder.txt_SD_Mobile_No.setText("" + supplierMaster.SD_Mobile_No);
            holder.myImageViewText.setText(("" + supplierMaster.SM_Name.charAt(0) + "" + supplierMaster.SM_Name.charAt(1)).toUpperCase());
            holder.imgDelete.setTag(supplierMaster);
            holder.imgEdit.setTag(supplierMaster);
            holder.imgFav.setTag(supplierMaster);
            setColourtoFavImage(holder.imgFav, supplierMaster.IS_favorite);
            holder.imgFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView img = (ImageView) view;
                    SupplierMasterDo supplierMaster = (SupplierMasterDo) img.getTag();
                    if (supplierMaster.IS_favorite.equals("False")) {
                        setColourtoFavImage(holder.imgFav, "True");
                        supplierMaster.IS_favorite = "True";
                        new syncTaskForPushFavSupplier().execute("" + supplierMaster.SM_Id);
                        ((MainActivity) mcontext).showCustomeBottomToast("Added to Fav supplier");
                        syncTaskForUpdateSupplierForFavSupplier FavSupplier = new syncTaskForUpdateSupplierForFavSupplier();
                        FavSupplier.execute(supplierMaster);
                    }
                }
            });
            holder.imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView img = (ImageView) view;
                    SupplierMasterDo supplierMaster = (SupplierMasterDo) img.getTag();
                    ((MainActivity) mcontext).runOnUiThread(new RunshowCustomDialogs("update", supplierMaster, holder));
                }
            });

            holder.imgCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((MainActivity) mcontext).hasPermissions(mcontext, Manifest.permission.CALL_PHONE)) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91" + supplierMaster.SD_Mobile_No));
                        ((MainActivity) mcontext).startActivity(intent);
                    } else {
                        ((MainActivity) mcontext).showCustomeTopToast("You don't assign permission.");
                    }
                }
            });
            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView img = (ImageView) view;
                    SupplierMasterDo supplierMaster = (SupplierMasterDo) img.getTag();
                    ((MainActivity) mcontext).runOnUiThread(new RunshowCustomDialogs(mcontext, "Alert!", "Are to sure to delete this " + supplierMaster.SM_Name + " supplier" + " ?", "Yes", "No", "", true, supplierMaster, position, holder));
                }
            });
            holder.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) mcontext).runOnUiThread(new RunshowCustomDialogs("profile", supplierMaster, holder));
                }
            });
        }
    }

    private void setColourtoFavImage(ImageView imgFav, String IS_favorite) {
        if (IS_favorite.equals("True")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ColorStateList stateList = ColorStateList.valueOf(((MainActivity) mcontext).getResources().getColor(R.color.dark_red));
                imgFav.setBackgroundTintList(stateList);
            } else {
                imgFav.getBackground().getCurrent().setColorFilter(new PorterDuffColorFilter(((MainActivity) mcontext).getResources().getColor(R.color.dark_red), PorterDuff.Mode.MULTIPLY));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ColorStateList stateList = ColorStateList.valueOf(((MainActivity) mcontext).getResources().getColor(R.color.gray_lite2));
                imgFav.setBackgroundTintList(stateList);
            } else {
                imgFav.getBackground().getCurrent().setColorFilter(new PorterDuffColorFilter(((MainActivity) mcontext).getResources().getColor(R.color.gray_lite2), PorterDuff.Mode.MULTIPLY));
            }
        }
    }

    @Override
    public int getItemCount() {
        return SuppliersList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_SM_Name, txt_description, txt_SD_Mobile_No, myImageViewText;
        ImageView img_SM_Logo, imgCell, imgEdit, imgDelete, imgFav;
        LinearLayout llItem;

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
            llItem = itemView.findViewById(R.id.llItem);
        }
    }

    public class syncTaskForDelete extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            apiServices.deleteItem(ServiceURLs.SUPPLIERS_MASTERS_DELETE + params[0], preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(strResponce.equals("delete")){
                ((MainActivity)mcontext).showCustomeBottomToast("Removed...");
            }
        }
    }



    public class syncTaskForPushFavSupplier extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            apiServices.postForFavSupplier(ServiceURLs.ADD_FAV_SUPPLIER + params[0], preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
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
        private SupplierMasterDo supplierMaster;
        private int position = 0;
        SupplierListAdaptor.ViewHolder holder;

        public RunshowCustomDialogs(Context context, String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, boolean isCancelable, SupplierMasterDo supplierMaster, int position, SupplierListAdaptor.ViewHolder holder) {
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
            View view;
            if (from.equals("update")) {
                view = inflater.inflate(R.layout.fragment_crsupplier, null);
                customDialog = new CustomDialog(mcontext, view, preference
                        .getIntFromPreference(Preference.DEVICE_DISPLAY_WIDTH, 320) - 80, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                customDialog.setCancelable(true);
                initialization(view);

                if (supplierMaster != null) {
                    edtSupplierName.setText(supplierMaster.SM_Name);
                    edtTemplate.setText(supplierMaster.SM_Template_ID);
                    edtMin.setText("" + supplierMaster.SM_Min_Delivery_Charge);
                    edtMax.setText("" + supplierMaster.SM_Max_Delivery_Charge);
                    edtPin_AadharNo.setText("" + supplierMaster.SD_PAN);
                    edtRegisterAddress.setText("" + supplierMaster.SD_Register_Address);
                    edtState.setText(supplierMaster.SD_State);
                    edtCity.setText(supplierMaster.SD_City);
                    edtGstNo.setText(supplierMaster.SD_GST_No);
                    edtZipCode.setText("" + supplierMaster.SD_PINCode);
                    edtMobileNo.setText("" + supplierMaster.SD_Mobile_No);
                    edtDispatchAddress.setText("" + supplierMaster.SD_Dispatch_Address);
                    spinCountry.setSelection(CAdapter.getPosition(supplierMaster.SD_Country));
                    spinSupplierType.setSelection(supplierMaster.SM_Type);
                    spinBusinessType.setSelection(supplierMaster.SM_BussinessType);
                }
                btnUpdateSupplier.setOnClickListener(new View.OnClickListener() {
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

                        if (strSupplierName.equalsIgnoreCase(supplierMaster.SM_Name) && strTemplate.equalsIgnoreCase(supplierMaster.SM_Template_ID) &&
                                strMin.equalsIgnoreCase("" + supplierMaster.SM_Min_Delivery_Charge) && strMax.equalsIgnoreCase("" + supplierMaster.SM_Max_Delivery_Charge)
                                && strPan_AadharNo.equalsIgnoreCase(supplierMaster.SD_PAN) && strDispatchAddress.equalsIgnoreCase(supplierMaster.SD_Dispatch_Address)
                                && strCity.equalsIgnoreCase(supplierMaster.SD_City) && strZipCode.equalsIgnoreCase("" + supplierMaster.SD_PINCode)
                                && strGstNo.equalsIgnoreCase(supplierMaster.SD_GST_No) && strRegisterAddress.equalsIgnoreCase(supplierMaster.SD_Register_Address)
                                && intSupplierType == supplierMaster.SM_Type && intbusinessType == supplierMaster.SM_BussinessType && strMobileNo.equalsIgnoreCase("" + supplierMaster.SD_Mobile_No)
                                && strState.equalsIgnoreCase(supplierMaster.SD_State)) {
                            ((MainActivity) mcontext).showCustomeTopToast("No change appear.");
                        } else {
                            new syncTaskForUpdateSupplier().execute(supplierMaster.SM_Id);
                            customDialog.dismiss();
                        }
                    }
                });
                imgCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDialog.dismiss();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });

            } else if (from.equals("profile")) {
                view = inflater.inflate(R.layout.custom_popup_for_supplier_profile, null);
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

                loadDataForSpin(supplierMaster.SM_BussinessType, supplierMaster.SM_Type);

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
                        SuppliersList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, SuppliersList.size());

                        new syncTaskForDelete().execute("" + supplierMaster.SM_Id);
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

    private void initialization(View root) {
        btnCreateSupplier = root.findViewById(R.id.btnCreateSupplier);
        llBtns = root.findViewById(R.id.llBtns);
        btnUpdateSupplier = root.findViewById(R.id.btnUpdSupplier);
        imgCancel = root.findViewById(R.id.imgCancel);
        btnCancel = root.findViewById(R.id.btnCancel);
        spinCountry = root.findViewById(R.id.spinCountry);
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

        llBtns.setVisibility(View.VISIBLE);
        btnCreateSupplier.setVisibility(View.GONE);
        imgCancel.setVisibility(View.VISIBLE);

        CAdapter = new ArrayAdapter<>(mcontext, android.R.layout.simple_spinner_dropdown_item, CountryNames);
        spinCountry.setAdapter(CAdapter);
        loadData();
    }

    private void loadData() {
        loadSupplierTypeMaster();
        loadSupplier_Bussiness_type_Master();
    }

    private void loadDataForSpin(final int intbusinessType, final int intSupplierType) {
        Call<List<SpinnerDo>> call = service.getSupplier_Bussiness_type_Master("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        call.enqueue(new Callback<List<SpinnerDo>>() {
            @Override
            public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                Supplier_Bussiness_type_MasterList = response.body();
                txtBsnType.setText(Supplier_Bussiness_type_MasterList.get(intbusinessType).getName());
            }

            @Override
            public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                ((MainActivity) mcontext).showCustomeBottomToast("Unable to load");
            }
        });

        Call<List<SpinnerDo>> call2 = service.getSupplierTypeMaster("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        call2.enqueue(new Callback<List<SpinnerDo>>() {
            @Override
            public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                SupplierTypeMasterList = response.body();
                txtType.setText(SupplierTypeMasterList.get(intSupplierType).getName());
            }

            @Override
            public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                ((MainActivity) mcontext).showCustomeBottomToast("Unable to load");
            }
        });
    }

    private void loadSupplier_Bussiness_type_Master() {
        Call<List<SpinnerDo>> call = service.getSupplier_Bussiness_type_Master("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        call.enqueue(new Callback<List<SpinnerDo>>() {
            @Override
            public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                Supplier_Bussiness_type_MasterList = response.body();
                businessType = new String[Supplier_Bussiness_type_MasterList.size()];
                for (int i = 0; i < Supplier_Bussiness_type_MasterList.size(); i++) {
                    businessType[i] = Supplier_Bussiness_type_MasterList.get(i).getName();
                }
                businessTypeAdaptor = new ArrayAdapter<>(mcontext, android.R.layout.simple_spinner_dropdown_item, businessType);
                spinBusinessType.setAdapter(businessTypeAdaptor);
            }

            @Override
            public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                ((MainActivity) mcontext).showCustomeBottomToast("Unable to load");
            }
        });
    }

    private void loadSupplierTypeMaster() {
        Call<List<SpinnerDo>> call = service.getSupplierTypeMaster("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        call.enqueue(new Callback<List<SpinnerDo>>() {
            @Override
            public void onResponse(Call<List<SpinnerDo>> call, retrofit2.Response<List<SpinnerDo>> response) {
                SupplierTypeMasterList = response.body();
                supplierType = new String[SupplierTypeMasterList.size()];
                for (int i = 0; i < SupplierTypeMasterList.size(); i++) {
                    supplierType[i] = SupplierTypeMasterList.get(i).getName();
                }
                supplierTypeAdaptor = new ArrayAdapter<>(mcontext, android.R.layout.simple_spinner_dropdown_item, supplierType);
                spinSupplierType.setAdapter(supplierTypeAdaptor);
            }

            @Override
            public void onFailure(Call<List<SpinnerDo>> call, Throwable throwable) {
                ((MainActivity) mcontext).showCustomeBottomToast("Unable to load");
            }
        });
    }

    public class syncTaskForUpdateSupplier extends AsyncTask<Long, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(mcontext);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cShowProgress.hideProgress();
            if (strResponce.equalsIgnoreCase("Updated")) {
                ((MainActivity) mcontext).showCustomeBottomToast("Changes are done");
                loadSupplierList();
            } else if (strResponce.equalsIgnoreCase(AppConstants.INTERNAL_ERROR)) {
                ((MainActivity) mcontext).showCustomeTopToast(AppConstants.INTERNAL_ERROR);
            }
        }

        @Override
        protected Void doInBackground(Long... params) {
            strResponce = apiServices.UpdateItemUsingToken(ServiceURLs.SUPPLIERS_MASTERS_UPDATE, generateJsonToStringForEdit(params[0]), preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            return null;
        }
    }

    public class syncTaskForUpdateSupplierForFavSupplier extends AsyncTask<SupplierMasterDo, Void, Void> {

        @Override
        protected Void doInBackground(SupplierMasterDo... supplierMasterDos) {
            apiServices.UpdateItemUsingToken(ServiceURLs.SUPPLIERS_MASTERS_UPDATE, generateJsonToStringForEdit(supplierMasterDos[0]), preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
            return null;
        }
    }

    public String generateJsonToStringForEdit(SupplierMasterDo supplierMasterDos) {
        String jsonString = "{" +
                "\"SM_ID\": " + supplierMasterDos.SM_Id + "," +
                "\"SM_Name\": \"" + supplierMasterDos.SM_Name + "\", " +
                "\"SM_Logo\": \"" + "Logo" + "\", " +
                "\"SM_Template_ID\": \"" + supplierMasterDos.SM_Template_ID + "\"," +
                "\"SM_BussinessType\": " + supplierMasterDos.SM_BussinessType + "," +
                "\"SM_Type\": " + supplierMasterDos.SM_Type + "," +
                "\"SM_Status\": \"" + "A" + " \"," +
                "\"SM_Rating\": " + 2 + "," +
                "\"SM_Min_Delivery_Charge\": \"" + supplierMasterDos.SM_Max_Delivery_Charge + "\"," +
                "\"SM_Max_Delivery_Charge\": \"" + supplierMasterDos.SM_Max_Delivery_Charge + "\"," +
                "\"SM_UserID\": \"" + preference.getStringFromPreference(Preference.USERID, "") + "\"," +
                "\"SD_PAN\": \"" + supplierMasterDos.SD_PAN + "\"," +
                "\"SD_Register_Address\": \"" + supplierMasterDos.SD_Register_Address + "\"," +
                "\"SD_Dispatch_Address\": \"" + supplierMasterDos.SD_Dispatch_Address + "\"," +
                "\"SD_City\": \"" + supplierMasterDos.SD_City + "\"," +
                "\"SD_State\": \"" + supplierMasterDos.SD_State + "\"," +
                "\"SD_Country\": \"" + supplierMasterDos.SD_Country + "\"," +
                "\"SD_PINCode\": \"" + supplierMasterDos.SD_PINCode + "\"," +
                "\"SD_GST_No\": \"" + supplierMasterDos.SD_GST_No + "\"," +
                "\"IS_favorite\": \"" + supplierMasterDos.IS_favorite + "\"," +
                "\"SD_LandLine_No\": " + 0 + "," +
                "\"SD_Mobile_No\": \"" + supplierMasterDos.SD_Mobile_No + "\"" + "}";
        return jsonString;
    }

    public String generateJsonToStringForEdit(Long id) {
        String jsonString = "{" +
                "\"SM_ID\": " + id + "," +
                "\"SM_Name\": \"" + strSupplierName + "\", " +
                "\"SM_Logo\": \"" + "Logo" + "\", " +
                "\"SM_Template_ID\": \"" + strTemplate + "\"," +
                "\"SM_BussinessType\": " + intbusinessType + "," +
                "\"SM_Type\": " + intSupplierType + "," +
                "\"SM_Status\": \"" + "A" + " \"," +
                "\"SM_Rating\": " + 2 + "," +
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
                "\"SD_LandLine_No\": " + 0 + "," +
                "\"SD_Mobile_No\": \"" + strMobileNo + "\"" + "}";
        return jsonString;
    }

    public void loadSupplierList() {
        GetData service = RetrofitClient.getRetrofitInstance().create(GetData.class);

        Call<List<SupplierMasterDo>> call = service.getAllSuppliers("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        cShowProgress = CShowProgress.getInstance();
        cShowProgress.showProgress(mcontext);
        call.enqueue(new Callback<List<SupplierMasterDo>>() {
            @Override
            public void onResponse(Call<List<SupplierMasterDo>> call, Response<List<SupplierMasterDo>> response) {
                loadDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<SupplierMasterDo>> call, Throwable throwable) {
                ((MainActivity) mcontext).showCustomeBottomToast("Unable to load");
            }
        });
        cShowProgress.hideProgress();
    }

    public void loadDataList(List<SupplierMasterDo> SuppliersList) {
        this.SuppliersList = SuppliersList;
        notifyDataSetChanged();
    }
}