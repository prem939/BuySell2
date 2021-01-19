package com.example.buysell2.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buysell2.Activity.MainActivity;
import com.example.buysell2.Adaptor.SupplierFavListAdaptor;
import com.example.buysell2.Adaptor.SupplierItemListAdaptor;
import com.example.buysell2.Adaptor.SupplierItemListForWishlistAdaptor;
import com.example.buysell2.CShowProgress;
import com.example.buysell2.Do.SupplierItemMasterDo;
import com.example.buysell2.Do.SupplierMasterDo;
import com.example.buysell2.Interface.GetData;
import com.example.buysell2.R;
import com.example.buysell2.common.AppConstants;
import com.example.buysell2.common.Preference;
import com.example.buysell2.common.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WhisListFragment extends Fragment {

    private LinearLayout llSupplierList;
    private RecyclerView rv_itemList;
    private SupplierItemListForWishlistAdaptor supplierItemListForWishlistAdaptor;
    private TextView txtNoData;
    private CShowProgress cShowProgress;
    private List<SupplierItemMasterDo> SupplierListItems = new ArrayList<>();
    private Context mContext;
    private Preference preference;
    private LayoutInflater inflater;
    GetData service;

    public WhisListFragment(Context mContext, Preference preference, LayoutInflater inflater) {
        this.mContext = mContext;
        this.preference = preference;
        this.inflater = inflater;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_supplier_list, container, false);
        rv_itemList = root.findViewById(R.id.rv_supplier_list);
        txtNoData = root.findViewById(R.id.txtNoData);
        txtNoData.setText("No items are added");
        return root;
    }
    public void loadSupplierItemList() {
        service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        Call<List<SupplierItemMasterDo>> call = service.getAllWishList("Bearer "+preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));
        cShowProgress = CShowProgress.getInstance();
        cShowProgress.showProgress(mContext);
        call.enqueue(new Callback<List<SupplierItemMasterDo>>() {
            @Override
            public void onResponse(Call<List<SupplierItemMasterDo>> call, Response<List<SupplierItemMasterDo>> response) {
                loadDataList(response.body());
            }
            @Override
            public void onFailure(Call<List<SupplierItemMasterDo>> call, Throwable throwable) {
                Toast.makeText(mContext, "Unable to load", Toast.LENGTH_SHORT).show();
            }
        });
        cShowProgress.hideProgress();
    }

    public void loadDataList(List<SupplierItemMasterDo> SupplierListItems) {
        if (SupplierListItems!=null && SupplierListItems.size() > 0) {
            rv_itemList.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
            supplierItemListForWishlistAdaptor = new SupplierItemListForWishlistAdaptor(mContext, SupplierListItems, inflater, preference,service);
            rv_itemList.setLayoutManager(new GridLayoutManager(mContext, 2));
            rv_itemList.setAdapter(supplierItemListForWishlistAdaptor);
        } else {
            rv_itemList.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        loadSupplierItemList();
    }

}
