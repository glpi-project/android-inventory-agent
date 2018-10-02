package org.flyve.inventory.agent.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.flyve.inventory.agent.ui.FragmentInventoryList;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String data;
    private ArrayList<String> load;

    public ViewPagerAdapter(FragmentManager fm, String data, ArrayList<String> load) {
        super(fm);
        this.data = data;
        this.load = load;
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentInventoryList.newInstance(data, load.get(position));
    }

    @Override
    public int getCount() {
        return load.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return load.get(position);
    }
}
