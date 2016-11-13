package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import fragment.FriendsFragment;
import fragment.GroupFragment;
import fragment.TranslateFragment;

/**
 * Name: SectionsPagerAdapter
 * Description: Adapter for ViewPager in MainActivity
 * Created on 2016/10/2 0002.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments = new ArrayList<Fragment>();

    /*
    * This Adapter is only used for display the ChatFragment, FriendsFragment and TranslateFragment.
    * */
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.add(FriendsFragment.newInstance());
        fragments.add(GroupFragment.newInstance());
        fragments.add(TranslateFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    /*
    * For TabLayout of ViewPager to find the Tab's text.
    * */
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getArguments().get("type").toString();
    }
}
