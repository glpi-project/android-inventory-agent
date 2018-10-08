package org.flyve.inventory.agent.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.ProgressBar;

import org.flyve.inventory.agent.ui.FragmentInventoryList;

import java.util.ArrayList;
import java.util.Collections;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String data;
    private ArrayList<String> load;

    public ViewPagerAdapter(FragmentManager fm, String data, ArrayList<String> load, ProgressBar progressBar) {
        super(fm);
        this.data = data;
        this.load = load;
        this.load.remove("");
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentInventoryList.newInstance(data, load.get(position));
    }

    @Override
    public int getCount() {
        return load.size() - Collections.frequency(load, "");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return load.get(position);
    }
}
