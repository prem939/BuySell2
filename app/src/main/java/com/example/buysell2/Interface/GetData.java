package com.example.buysell2.Interface;


import com.example.buysell2.Do.SearchDo;
import com.example.buysell2.Do.SpinnerDo;
import com.example.buysell2.Do.SupplierItemMasterDo;
import com.example.buysell2.Do.SupplierMasterDo;
import com.example.buysell2.common.AppConstants;
import com.example.buysell2.common.Preference;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface GetData {
    @GET("/api/Supplier_Master")
    Call<List<SupplierMasterDo>> getAllSuppliers(@Header("Authorization") String auth);

    @GET("/api/SupplierItems")
    Call<List<SupplierItemMasterDo>> getAllSuppliersItems(@Header("Authorization") String auth);

    @GET("/api/SupplierItems/GetProdectCategoryMaster")
    Call<List<SpinnerDo>> getAllProdectCategoryMaster(@Header("Authorization") String auth);

    @GET("/api/SupplierItems/ProdectSubCategoryMaster")
    Call<List<SpinnerDo>> getAllProdectSubCategoryMaster(@Header("Authorization") String auth);

    @GET("/api/SupplierMaster/GetSupplierTypeMaster")
    Call<List<SpinnerDo>> getSupplierTypeMaster(@Header("Authorization") String auth);

    @GET("/api/SupplierMaster/Supplier_Bussiness_type_Master")
    Call<List<SpinnerDo>> getSupplier_Bussiness_type_Master(@Header("Authorization") String auth);

    @GET("/api/Search?text=samsung")
    Call<List<SearchDo>> getSearchResult(@Header("Authorization") String auth);

    @GET("/api/CustmerSupplierMapping/SupplierList")
    Call<List<SupplierMasterDo>> getAllFavSuppliers(@Header("Authorization") String auth);

    @GET("/api/WishList")
    Call<List<SupplierItemMasterDo>> getAllWishList(@Header("Authorization") String auth);
}
