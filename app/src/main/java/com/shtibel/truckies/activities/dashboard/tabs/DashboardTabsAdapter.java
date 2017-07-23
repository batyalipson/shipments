package com.shtibel.truckies.activities.dashboard.tabs;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.shtibel.truckies.generalClasses.SharedPreferenceManager;

/**
 * Created by Shtibel on 02/08/2016.
 */
public class DashboardTabsAdapter extends FragmentStatePagerAdapter {

    private Fragment mCurrentFragment;
    Context context;
    public DashboardTabsAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment =null;
        if (position==0)
            fragment=new DashboardTabJobs();
        else
            fragment=new DashboardTabOffers();
        //Bundle args = new Bundle();
        // Our object is just an integer :-P
        //args.putInt(DashboardTabJobs.ARG_OBJECT, position + 1);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        if (SharedPreferenceManager.getInstance(context).getCanSeeOffers()==1)
            return 2;
        return 1;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }


    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((Fragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

}
