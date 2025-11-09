package com.example.labverse.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.labverse.fragments.dashboard.FavoritesFragment;
import com.example.labverse.fragments.dashboard.RecentlyAddedFragment;
import com.example.labverse.fragments.dashboard.RecentlyReadFragment;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    public DashboardPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new RecentlyAddedFragment();
            case 1:
                return new RecentlyReadFragment();
            case 2:
                return new FavoritesFragment();
            default:
                return new RecentlyAddedFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Số lượng tab
    }
}
