package com.example.labverse;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.labverse.activities.ImportPaperActivity;
import com.example.labverse.activities.LoginActivity;
import com.example.labverse.auth.FirebaseAuthManager;
import com.example.labverse.fragments.CollectionsFragment;
import com.example.labverse.fragments.DashboardFragment;
import com.example.labverse.fragments.DiscoverFragment;
import com.example.labverse.fragments.FilterBottomSheetDialog;
import com.example.labverse.fragments.ProfileFragment;
import com.example.labverse.fragments.SearchFragment;
import com.example.labverse.viewmodels.SearchViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddPaper;
    private FirebaseAuthManager authManager;
    private SearchViewModel searchViewModel;

    // To keep track of the current visible fragment
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authManager = new FirebaseAuthManager(this);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        initViews();
        setupBottomNavigation();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAddPaper = findViewById(R.id.fab_add_paper);

        fabAddPaper.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ImportPaperActivity.class);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search papers, authors...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewModel.updateSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Navigate to SearchFragment if there is text
                if (!newText.isEmpty() && !(currentFragment instanceof SearchFragment)) {
                    loadFragment(new SearchFragment());
                }
                // Navigate back to DashboardFragment if text is empty
                else if (newText.isEmpty() && (currentFragment instanceof SearchFragment)) {
                    loadFragment(new DashboardFragment());
                }

                searchViewModel.updateSearchQuery(newText);
                return true;
            }
        });

        // Handle closing the search view
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true; // Allow the search view to expand
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // When search is closed, go back to the DashboardFragment
                if (currentFragment instanceof SearchFragment) {
                    loadFragment(new DashboardFragment());
                }
                return true; // Allow the search view to collapse
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            FilterBottomSheetDialog dialog = new FilterBottomSheetDialog();
            dialog.show(getSupportFragmentManager(), "FilterDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.nav_dashboard) {
            fragment = new DashboardFragment();
        } else if (itemId == R.id.nav_discover) {
            fragment = new DiscoverFragment();
        } else if (itemId == R.id.nav_collections) {
            fragment = new CollectionsFragment();
        } else if (itemId == R.id.nav_profile) {
            fragment = new ProfileFragment();
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            currentFragment = fragment; // Keep track of the current fragment
            return true;
        }
        return false;
    }

    private void logoutUser() {
        authManager.logout();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
