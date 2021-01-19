package com.example.buysell2.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.buysell2.Do.UserDo;
import com.example.buysell2.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class HomeFragment extends Fragment {
    private PieChartView pieChartView;
    private ImageView imgEditUserDetails;
    private TextView txtOpenPosPer,txtToReceivePer,txtToPayPer,txtOpenSosPer,txtType;
    private LinearLayout llAdmin,llSupplier,llBuyer;
    private UserDo user= null;
    public HomeFragment(UserDo user) {
        this.user=user;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initialization(root);

        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue(15, getResources().getColor(R.color.darkblue)).setLabel(getResources().getString(R.string.open_pos)));
        pieData.add(new SliceValue(25, getResources().getColor(R.color.orange)).setLabel(getResources().getString(R.string.to_receive)));
        pieData.add(new SliceValue(10, getResources().getColor(R.color.darkpurple)).setLabel(getResources().getString(R.string.to_pay)));
        pieData.add(new SliceValue(60, getResources().getColor(R.color.blue)).setLabel(getResources().getString(R.string.open_sos)));
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize((int) getResources().getDimension(R.dimen._3ssp));
        pieChartView.setPieChartData(pieChartData);
        txtOpenPosPer.setText(" : 15 %");
        txtToReceivePer.setText(" : 25 %");
        txtToPayPer.setText(" : 10 %");
        txtOpenSosPer.setText(" : 60 %");

        imgEditUserDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(HomeScreenActivity_new.this, EditUserDetailsActivity.class);
//                intent.putExtra("LoginUserData", user);
//                startActivity(intent);
            }
        });
        return root;
    }

    private void initialization(View root) {
        pieChartView = root.findViewById(R.id.chart);
        txtOpenPosPer = root.findViewById(R.id.txtOpenPosPer);
        txtToReceivePer = root.findViewById(R.id.txtToReceivePer);
        txtToPayPer = root.findViewById(R.id.txtToPayPer);
        txtOpenSosPer = root.findViewById(R.id.txtOpenSosPer);
        imgEditUserDetails = root.findViewById(R.id.imgEditUserDetails);
        llAdmin = root.findViewById(R.id.llAdmin);
        llBuyer = root.findViewById(R.id.llBuyer);
        llSupplier = root.findViewById(R.id.llSupplier);
        txtType = root.findViewById(R.id.txtType);

        txtType.setText(user.UP_User_Type);
        if (user.UP_User_Type.equalsIgnoreCase("Supplier")) {
            llAdmin.setVisibility(View.GONE);
            llBuyer.setVisibility(View.GONE);
            llSupplier.setVisibility(View.VISIBLE);
        } else if(user.UP_User_Type.equalsIgnoreCase("Admin")){
            llAdmin.setVisibility(View.VISIBLE);
            llBuyer.setVisibility(View.GONE);
            llSupplier.setVisibility(View.GONE);
        }else if(user.UP_User_Type.equalsIgnoreCase("Customer")){
            llAdmin.setVisibility(View.GONE);
            llBuyer.setVisibility(View.VISIBLE);
            llSupplier.setVisibility(View.GONE);
        }
    }
}