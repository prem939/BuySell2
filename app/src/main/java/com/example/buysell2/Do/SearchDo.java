package com.example.buysell2.Do;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchDo implements Serializable {
    ArrayList<SupplierMasterDo> supplierDetailsResponces = new ArrayList<>();
    ArrayList<SupplierItemMasterDo> supplierItemDatas = new ArrayList<>();

    public ArrayList<SupplierMasterDo> getSupplierDetailsResponces() {
        return supplierDetailsResponces;
    }

    public ArrayList<SupplierItemMasterDo> getSupplierItemDatas() {
        return supplierItemDatas;
    }
}
