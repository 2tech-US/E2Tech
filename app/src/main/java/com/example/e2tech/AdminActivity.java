package com.example.e2tech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavController navController;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // attach bottom navigation in to home
        bottomNavigationView = findViewById(R.id.admin_bottom_nav);

        // setup navigation controller
        navController = Navigation.findNavController(this, R.id.admin_nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // setup default action bar (maybe change with Toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
//                if (navDestination.getId() == R.id.detailFragment || navDestination.getId() == R.id.fragment_infor
//                        || navDestination.getId() == R.id.fragment_update) {
//                    bottomNavigationView.setVisibility(View.GONE);
//                    getSupportActionBar().hide();
//                } else {
//                    bottomNavigationView.setVisibility(View.VISIBLE);
//                    getSupportActionBar().hide();
//                }
            }
        });

//        navController.addOnDestinationChangedListener(
//                new NavController.OnDestinationChangedListener() {
//                    @Override
//                    public void onDestinationChanged(
//                            @NonNull NavController controller,
//                            @NonNull NavDestination destination,
//                            @Nullable Bundle arguments
//                    ) {
//                        boolean showAppBar = false;
//                        if (arguments != null) {
//                            showAppBar = arguments.getBoolean("ShowAppBar", false);
//                        }
//                        if(showAppBar) {
//                            appBar.setVisibility(View.VISIBLE);
//                        } else {
//                            appBar.setVisibility(View.GONE);
//                        }
//                    }
//                }
//        );


    }

    @Override
    protected void onDestroy() {
        FirebaseAuth.getInstance().signOut();
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}