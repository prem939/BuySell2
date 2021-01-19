package com.example.buysell2.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buysell2.Activity.MainActivity;
import com.example.buysell2.Adaptor.SupplierItemListAdaptorForSearch;
import com.example.buysell2.Adaptor.SupplierListAdaptorForSearch;
import com.example.buysell2.CShowProgress;
import com.example.buysell2.CenterZoomLayoutManager;
import com.example.buysell2.DatabaseHelper;
import com.example.buysell2.Do.SupplierItemMasterDo;
import com.example.buysell2.Do.SupplierMasterDo;
import com.example.buysell2.Interface.GetData;
import com.example.buysell2.R;
import com.example.buysell2.common.ApiServices;
import com.example.buysell2.common.AppConstants;
import com.example.buysell2.common.Preference;
import com.example.buysell2.common.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.buysell2.Activity.MainActivity.hideSoftKeyboard;

public class SearchFragment extends Fragment {

    private RecyclerView rv_supplier_list, rv_itemList;
    public CShowProgress cShowProgress;
    private Context mContext;
    private Preference preference;
    private LayoutInflater inflater;
    private GetData service;
    private SupplierListAdaptorForSearch supplierListAdaptor;
    private SupplierItemListAdaptorForSearch supplierItemListAdaptor;
    private ApiServices apiServices = new ApiServices();
    private DatabaseHelper objSqliteDB;
    public SearchFragment(Context mContext, Preference preference, LayoutInflater inflater,DatabaseHelper objSqliteDB) {
        this.mContext = mContext;
        this.preference = preference;
        this.inflater = inflater;
        this.objSqliteDB = objSqliteDB;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        rv_supplier_list = root.findViewById(R.id.rv_supplier_list);
        rv_itemList = root.findViewById(R.id.rv_itemList);
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
                loadSupplierList(response.body());
            }

            @Override
            public void onFailure(Call<List<SupplierMasterDo>> call, Throwable throwable) {
                ((MainActivity) mContext).showCustomeBottomToast("Unable to load");
            }
        });
        cShowProgress.hideProgress();
    }

    public void loadSupplierList(List<SupplierMasterDo> SupplierMaster) {
        if (SupplierMaster != null && SupplierMaster.size() > 0) {
            rv_supplier_list.setVisibility(View.VISIBLE);
            supplierListAdaptor = new SupplierListAdaptorForSearch(mContext, SupplierMaster, inflater, preference,objSqliteDB.getSupplierBusinessTypeMaster(),objSqliteDB.getSupplierTypeMaster());
            rv_supplier_list.setLayoutManager(new CenterZoomLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            rv_supplier_list.setAdapter(supplierListAdaptor);
            if (supplierListAdaptor != null)
                supplierListAdaptor.notifyDataSetChanged();
        }
    }

    public void loadSupplierItemList() {
        service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        Call<List<SupplierItemMasterDo>> call = service.getAllSuppliersItems("Bearer " + preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        cShowProgress = CShowProgress.getInstance();
        cShowProgress.showProgress(mContext);
        call.enqueue(new Callback<List<SupplierItemMasterDo>>() {
            @Override
            public void onResponse(Call<List<SupplierItemMasterDo>> call, Response<List<SupplierItemMasterDo>> response) {
                loadSupplierItemList(response.body());
            }

            @Override
            public void onFailure(Call<List<SupplierItemMasterDo>> call, Throwable throwable) {
                Toast.makeText(mContext, "Unable to load", Toast.LENGTH_SHORT).show();
            }
        });
        cShowProgress.hideProgress();
    }

    public void loadSupplierItemList(List<SupplierItemMasterDo> SupplierListItems) {
        if (SupplierListItems != null && SupplierListItems.size() > 0) {
            rv_itemList.setVisibility(View.VISIBLE);
            supplierItemListAdaptor = new SupplierItemListAdaptorForSearch(mContext, SupplierListItems, inflater, preference,objSqliteDB.getCatlogItemList(),objSqliteDB.getSubCatlogItemList());
            rv_itemList.setLayoutManager(new CenterZoomLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            rv_itemList.setAdapter(supplierItemListAdaptor);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        loadSupplierList();
        loadSupplierItemList();
    }

    public int Dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
