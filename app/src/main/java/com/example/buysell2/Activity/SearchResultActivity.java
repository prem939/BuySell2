package com.example.buysell2.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buysell2.Adaptor.SupplierItemListAdaptorForSearch;
import com.example.buysell2.Adaptor.SupplierListAdaptorForSearch;
import com.example.buysell2.CShowProgress;
import com.example.buysell2.CenterZoomLayoutManager;
import com.example.buysell2.DatabaseHelper;
import com.example.buysell2.Do.SearchDo;
import com.example.buysell2.Do.SupplierItemMasterDo;
import com.example.buysell2.Do.SupplierMasterDo;
import com.example.buysell2.R;
import com.example.buysell2.common.ApiServices;
import com.example.buysell2.common.AppConstants;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public class SearchResultActivity extends BaseActivity {
    private LinearLayout llSearch, llRv;
    private String query;
    ApiServices apiServices = new ApiServices();
    private CShowProgress cShowProgress;
    private RecyclerView rv_supplier_list, rv_itemList;
    private TextView txtNoData;
    private String strResponce = "";
    private SupplierListAdaptorForSearch supplierListAdaptor;
    private SupplierItemListAdaptorForSearch supplierItemListAdaptor;
    DatabaseHelper objSqliteDB = null;
    @Override
    public void initialize() {
        llSearch = (LinearLayout) inflater.inflate(R.layout.fragment_search, null);
        llBody.addView(llSearch, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        llheader.setVisibility(View.VISIBLE);
        initilization();
        Intent i = getIntent();
        query = i.getStringExtra("query");
        txt_head.setText(query);

        syncForSearch syncForSearch = new syncForSearch();
        syncForSearch.execute(query);
    }

    private void initilization() {
        objSqliteDB = new DatabaseHelper(getApplicationContext());

        rv_supplier_list = llSearch.findViewById(R.id.rv_supplier_list);
        rv_itemList = llSearch.findViewById(R.id.rv_itemList);
        llRv = llSearch.findViewById(R.id.llRv);
        txtNoData = llSearch.findViewById(R.id.txtNoData);
    }

    public class syncForSearch extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            strResponce = apiServices.getSearchData("http://www.polarcanvas.in/api/Search?text=" + strings[0], preference.getStringFromPreference(AppConstants.LOGIN_TOKE, ""));

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cShowProgress = CShowProgress.getInstance();
            cShowProgress.showProgress(SearchResultActivity.this);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            cShowProgress.hideProgress();
            SearchDo searchDo = null;
            if (strResponce != null && !strResponce.equalsIgnoreCase("")) {
                try {
                    searchDo = new Gson().fromJson(strResponce, SearchDo.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (JsonParseException e) {
                    e.printStackTrace();
                }
                if(searchDo.getSupplierDetailsResponces().size() > 0){
                    loadSupplierList(searchDo.getSupplierDetailsResponces());
                    loadSupplierItemList(searchDo.getSupplierItemDatas());
                }else{
                    txtNoData.setVisibility(View.VISIBLE);
                    llRv.setVisibility(View.GONE);
                }
            }
        }
    }
    public void loadSupplierList(List<SupplierMasterDo> SupplierMaster) {
            supplierListAdaptor = new SupplierListAdaptorForSearch(SearchResultActivity.this, SupplierMaster, inflater, preference,objSqliteDB.getSupplierBusinessTypeMaster(),objSqliteDB.getSupplierTypeMaster());
            rv_supplier_list.setLayoutManager(new CenterZoomLayoutManager(SearchResultActivity.this,LinearLayoutManager.HORIZONTAL,false));
            rv_supplier_list.setAdapter(supplierListAdaptor);
    }
    public void loadSupplierItemList(List<SupplierItemMasterDo> SupplierListItems) {
            supplierItemListAdaptor = new SupplierItemListAdaptorForSearch(SearchResultActivity.this, SupplierListItems, inflater, preference,objSqliteDB.getCatlogItemList(),objSqliteDB.getSubCatlogItemList());
            rv_itemList.setLayoutManager(new CenterZoomLayoutManager(SearchResultActivity.this,LinearLayoutManager.HORIZONTAL,false));
            rv_itemList.setAdapter(supplierItemListAdaptor);
    }
}
