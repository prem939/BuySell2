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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.util.Attributes;
import com.example.buysell2.Activity.MainActivity;
import com.example.buysell2.Adaptor.SupplierListAdaptor;
import com.example.buysell2.CShowProgress;
import com.example.buysell2.DatabaseHelper;
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

public class SupplierListFragment extends Fragment {

    private RecyclerView rv_supplier_list;
    private SupplierListAdaptor supplierListAdaptor;
    private TextView txtNoData;
    public CShowProgress cShowProgress;
    private List<SupplierMasterDo> SupplierMaster = new ArrayList<>();
    private Context mContext;
    private Preference preference;
    private LayoutInflater inflater;
    private GetData service;
    private DatabaseHelper objSqliteDB = null;
    public SupplierListFragment(Context mContext, Preference preference, LayoutInflater inflater,DatabaseHelper objSqliteDB) {
        this.mContext = mContext;
        this.preference = preference;
        this.inflater = inflater;
        this.objSqliteDB = objSqliteDB;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_supplier_list, container, false);
        rv_supplier_list = root.findViewById(R.id.rv_supplier_list);
        txtNoData = root.findViewById(R.id.txtNoData);
        txtNoData.setText("No Suppliers are added");
        return root;
    }

    public void loadSupplierList() {
        service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        Call<List<SupplierMasterDo>> call = service.getAllSuppliers("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        cShowProgress = CShowProgress.getInstance();
        cShowProgress.showProgress(mContext);
        call.enqueue(new Callback<List<SupplierMasterDo>>() {
            @Override
            public void onResponse(Call<List<SupplierMasterDo>> call, Response<List<SupplierMasterDo>> response) {
                loadDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<SupplierMasterDo>> call, Throwable throwable) {
                ((MainActivity) mContext).showCustomeBottomToast("Unable to load");
            }
        });
        cShowProgress.hideProgress();
    }

    public void loadDataList(List<SupplierMasterDo> SupplierMaster) {
        if (SupplierMaster != null && SupplierMaster.size() > 0) {
            rv_supplier_list.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);

            supplierListAdaptor = new SupplierListAdaptor(mContext, SupplierMaster, inflater, preference, service);
            rv_supplier_list.setLayoutManager(new LinearLayoutManager(mContext));
            rv_supplier_list.setAdapter(supplierListAdaptor);
            if (supplierListAdaptor != null)
                supplierListAdaptor.notifyDataSetChanged();
        } else {
            rv_supplier_list.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSupplierList();
    }

}
