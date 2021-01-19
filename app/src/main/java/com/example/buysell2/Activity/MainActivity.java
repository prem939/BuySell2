package com.example.buysell2.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buysell2.DatabaseHelper;
import com.example.buysell2.Do.SpinnerDo;
import com.example.buysell2.Do.UserDo;
import com.example.buysell2.Fragment.CartFragment;
import com.example.buysell2.Fragment.CrPromoFragment;
import com.example.buysell2.Fragment.CrSitesFragment;
import com.example.buysell2.Fragment.CrUpdSupplierItemsFragment;
import com.example.buysell2.Fragment.CrUpdSuppliersFragment;
import com.example.buysell2.Fragment.PurchaseOrdersFragment;
import com.example.buysell2.Fragment.HomeFragment;
import com.example.buysell2.Fragment.ItemListFragment;
import com.example.buysell2.Fragment.OrdersFragment;
import com.example.buysell2.Fragment.PoFragment;
import com.example.buysell2.Fragment.SearchFragment;
import com.example.buysell2.Fragment.SoFragment;
import com.example.buysell2.Fragment.SupplierFavListFragment;
import com.example.buysell2.Fragment.SupplierListFragment;
import com.example.buysell2.Fragment.WhisListFragment;
import com.example.buysell2.Interface.GetData;
import com.example.buysell2.R;
import com.example.buysell2.common.AppConstants;
import com.example.buysell2.common.CustomDialog;
import com.example.buysell2.common.Preference;
import com.example.buysell2.common.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavView;
    private ImageView imgNavigationBack, profileImg;
    private TextView txtName, txtPhoneNumber;
    FragmentManager fragmentManager;
    Toolbar toolbar;
    public CustomDialog customDialog;
    public Preference preference;
    public LayoutInflater inflater;
    private String UserType = "Admin";
    private UserDo user = new UserDo();
    private Fragment fragment = null;
    private DatabaseHelper objSqliteDB = null;
    private GetData service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = this.getLayoutInflater();
        setContentView(R.layout.activity_main);
        preference = new Preference(getApplicationContext());
        objSqliteDB = new DatabaseHelper(getApplicationContext());
        service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        fragmentManager = getSupportFragmentManager();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavView = findViewById(R.id.bottom_nav_view);
        initToolbar();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            user = (UserDo) bundle.get("LoginUserData");
            UserType = user.UP_User_Type;
            txtName.setText(user.UP_Name);
            txtPhoneNumber.setText("" + user.UP_Mobile_No);
        }
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavView.getLayoutParams();
//        layoutParams.setBehavior(new ScrollHandler());

        Menu nav_Menu = navigationView.getMenu();
        Menu btm_Menu = bottomNavView.getMenu();

        if (UserType.equalsIgnoreCase("Supplier")) {
            btm_Menu.findItem(R.id.btm_order).setTitle("Sales Order");
        } else if (UserType.equalsIgnoreCase("Admin")) {
            nav_Menu.findItem(R.id.nav_so).setVisible(false);
            nav_Menu.findItem(R.id.nav_po).setVisible(false);
            btm_Menu.findItem(R.id.btm_purchaseOrders).setVisible(false);
        } else if (UserType.equalsIgnoreCase("Customer")) {
            nav_Menu.findItem(R.id.nav_so).setVisible(false);
            nav_Menu.findItem(R.id.nav_po).setVisible(false);
            btm_Menu.findItem(R.id.btm_purchaseOrders).setVisible(false);
        }

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
        bottomNavView.setSelectedItemId(R.id.btm_home);

        imgNavigationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopBarMenuClick();
            }
        });

    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.btm_home:
                fragment = new HomeFragment(user);
                break;
            case R.id.nav_crSupplier:
                fragment = new CrUpdSuppliersFragment(MainActivity.this, preference,objSqliteDB.getSupplierTypeMaster(),objSqliteDB.getSupplierBusinessTypeMaster());
                break;
            case R.id.nav_crSupplierItem:
                fragment = new CrUpdSupplierItemsFragment(MainActivity.this, preference,objSqliteDB.getCatlogItemList(),objSqliteDB.getSubCatlogItemList());
                break;
            case R.id.nav_promo:
                fragment = new CrPromoFragment();
                break;
            case R.id.nav_sites:
                fragment = new CrSitesFragment();
                break;
            case R.id.btm_supplier:
                fragment = new SupplierListFragment(MainActivity.this, preference, inflater,objSqliteDB);
                break;
            case R.id.nav_items:
                fragment = new ItemListFragment(MainActivity.this, preference, inflater,objSqliteDB);
                break;
            case R.id.btm_order:
                fragment = new OrdersFragment();
                break;
            case R.id.nav_logOut:
                fragment = new HomeFragment(user);
                showCustomDialog(this, "Log out?", getResources().getString(R.string.do_you_want_to_logout) + " " + user.UP_Name + "?", "Log out", getString(R.string.Cancel), "logout");
                break;
            case R.id.btm_purchaseOrders:
                fragment = new PurchaseOrdersFragment();
                break;
            case R.id.btm_favItems:
                fragment = new SupplierFavListFragment(MainActivity.this, preference, inflater,objSqliteDB);
                break;
            case R.id.nav_so:
                fragment = new SoFragment();
                break;
            case R.id.nav_po:
                fragment = new PoFragment();
                break;
            case R.id.action_search:
                fragment = new SearchFragment(MainActivity.this, preference, inflater, objSqliteDB);
                break;
            case R.id.action_cart:
                fragment = new CartFragment();
                break;
            case R.id.action_whislist:
                fragment = new WhisListFragment(MainActivity.this, preference, inflater);
                break;
            default:
                fragment = new HomeFragment(user);
        }
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        menuItem.setChecked(true);
        if (menuItem.getTitle().equals("Log out"))
            setTitle("Home");
        else
            setTitle(menuItem.getTitle());

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        View headerview = navigationView.getHeaderView(0);

        imgNavigationBack = (ImageView) headerview.findViewById(R.id.imgNavigationBack);
        profileImg = (ImageView) headerview.findViewById(R.id.imageView);
        txtName = (TextView) headerview.findViewById(R.id.txtName);
        txtPhoneNumber = (TextView) headerview.findViewById(R.id.txtPhoneNumber);

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TopBarMenuClick();
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                intent.putExtra("LoginUserData", user);
                startActivity(intent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        selectDrawerItem(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showCustomDialog(this, "Logout?", getResources().getString(R.string.do_you_want_to_logout) + " " + user.UP_Name + "?", "Log out", getString(R.string.Cancel), "logout");
        }
//        if(fragment instanceof SearchFragment){
//            bottomNavView.setSelectedItemId(R.id.btm_home);
//        }
    }

    @SuppressLint("WrongConstant")
    public void TopBarMenuClick() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(Gravity.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectDrawerItem(item);
        return true;
    }

    public void showCustomDialog(Context context, String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from) {
        runOnUiThread(new RunshowCustomDialogs(context, strTitle, strMessage, firstBtnName, secondBtnName, from, true));
    }

    class RunshowCustomDialogs implements Runnable {
        private String strTitle;// Title of the dialog
        private String strMessage;// Message to be shown in dialog
        private String firstBtnName;
        private String secondBtnName;
        private String from;
        private String params;
        private Object paramateres;
        private boolean isCancelable = false;


        public RunshowCustomDialogs(Context context, String strTitle, String strMessage, String firstBtnName, String secondBtnName, String from, boolean isCancelable) {
            this.strTitle = strTitle;
            this.strMessage = strMessage;
            this.firstBtnName = firstBtnName;
            this.secondBtnName = secondBtnName;
            this.isCancelable = isCancelable;
            if (from != null)
                this.from = from;
            else
                this.from = "";
        }

        @Override
        public void run() {
            if (customDialog != null && customDialog.isShowing())
                customDialog.dismiss();
            View view;

            view = inflater.inflate(R.layout.custom_common_popup, null);

            customDialog = new CustomDialog(MainActivity.this, view, preference
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
                    if (from.equalsIgnoreCase("logout")) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        objSqliteDB.deleteUser(preference.getStringFromPreference(Preference.USERID, ""));
//                        objSqliteDB.deleteAllSpinners();
                        preference.removeFromPreference(AppConstants.LOGIN_TOKE);
                        preference.commitPreference();
                    }
                    if (from.equalsIgnoreCase("finish")) {
                        bottomNavView.setSelectedItemId(R.id.btm_home);
                    }
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog.dismiss();
                }
            });
            try {
                if (!customDialog.isShowing())
                    customDialog.showCustomDialog(MainActivity.this);
            } catch (Exception e) {
            }
        }
    }

    public void showCustomeTopToast(String msg) {
        View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView txtToastMsg = layout.findViewById(R.id.txtToastMsg);
        txtToastMsg.setText(msg);

        final Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP, 0, 250);
        toast.show();
    }

    public void showCustomeBottomToast(String msg) {
        View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView txtToastMsg = layout.findViewById(R.id.txtToastMsg);
        txtToastMsg.setText(msg);
        final Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.BOTTOM, 0, 250);
        toast.show();
    }

    public boolean isNetworkConnectionAvailable(Context context) {
        boolean isNetworkConnectionAvailable = false;
        @SuppressLint("WrongConstant") ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null)
            isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;

        return isNetworkConnectionAvailable;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void finish() {
        bottomNavView.setSelectedItemId(R.id.btm_home);
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}