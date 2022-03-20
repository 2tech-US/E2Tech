package com.example.e2tech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.e2tech.ViewModels.ProductViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavController navController;

    ArrayList<String> userFavoriteProducts;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    Toolbar toolbar;
//    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // attach bottom navigation in to home
        bottomNavigationView = findViewById(R.id.bottom_nav);

        // setup navigation controller
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // setup default action bar (maybe change with Toolbar)
        //NavigationUI.setupActionBarWithNavController(this, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.detailFragment || navDestination.getId() == R.id.fragment_infor
                        || navDestination.getId() == R.id.fragment_update) {
                    bottomNavigationView.setVisibility(View.GONE);
                    getSupportActionBar().hide();
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    getSupportActionBar().hide();
                }
            }
        });


        // setup toolbar
        toolbar = findViewById(R.id.topToolBar);
        NavigationUI.setupWithNavController(toolbar, navController);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.app_bar_search) {
                    navController.navigate(SearchFragmentDirections.actionGlobalSearchFragment());
                }

                return false;
            }
        });

/*        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        productViewModel.getProducts().observe(this,productModels -> {});*/


//        searchView = findViewById(R.id.searchBar);
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navController.navigate(SearchFragmentDirections.actionGlobalSearchFragment());
//            }
//        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_top_menu, menu);
        return true;

    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    public void setUserFavoriteProducts(ArrayList<String> userFavoriteProducts) {
        this.userFavoriteProducts = userFavoriteProducts;
    }

    public ArrayList<String> getUserFavoriteProducts() {
        return userFavoriteProducts;
    }

    public void addFavorite(String productId) {
        this.userFavoriteProducts.add(productId);
    }

    public void removeFavorite(String productId) {
        this.userFavoriteProducts.remove(productId);
    }

}