package camp.computer.clay.sequencer;

// BEGIN_INCLUDE (fragment_pager_adapter)

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.Unit;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages. This provides the data for the {@link ViewPager}.
 */
public class UnitViewPagerAdapter extends FragmentStatePagerAdapter {
    // END_INCLUDE (fragment_pager_adapter)

    public int count = 0;

    private Clay clay;

    public UnitViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Sets a reference to the Clay object to use when creating fragments corresponding to each
     * unit's timeline.
     * @param clay The Clay object to reference.
     */
    public void setClay (Clay clay) {
        this.clay = clay;
    }

    /**
     * Returns the Clay object being used by the PagerAdapter to create fragments.
     * @return The Clay object.
     */
    public Clay getClay () {
        return this.clay;
    }

    // BEGIN_INCLUDE (fragment_pager_adapter_getitem)
    /**
     * Get fragment corresponding to a specific position. This will be used to populate the
     * contents of the {@link ViewPager}.
     *
     * @param position Position to fetch fragment for.
     * @return Fragment for specified position.
     */
    @Override
    public Fragment getItem(int position) {

        // Get the unit in the specified position
        Unit unit = null;
        if (position < getClay().getUnits().size()) {
            unit = getClay().getUnits().get(position);
            Log.v("Behavior_Count", "unit not null");
        }

        // getItem is called to instantiate the fragment for the given page.
        // Return a UnitViewFragment (defined as a static inner class
        // below) with the page number as its lone argument.
        UnitViewFragment fragment = new UnitViewFragment();
        // TODO: ^ store reference to these fragments in the ApplicationView, so they can be directly called to update the Views after data model changes.
        Bundle args = new Bundle();
        args.putInt(UnitViewFragment.ARG_SECTION_NUMBER, position + 1);
        fragment.setArguments(args);
        fragment.setUnit(unit);
        return (Fragment) fragment;
    }
    // END_INCLUDE (fragment_pager_adapter_getitem)

    // BEGIN_INCLUDE (fragment_pager_adapter_getcount)
    /**
     * Get number of pages the {@link ViewPager} should render.
     *
     * @return Number of fragments to be rendered as pages.
     */
    @Override
    public int getCount() {
        return count;
    }
    // END_INCLUDE (fragment_pager_adapter_getcount)

    // BEGIN_INCLUDE (fragment_pager_adapter_getpagetitle)
    /**
     * Get title for each of the pages. This will be displayed on each of the tabs.
     *
     * @param position Page to fetch title for.
     * @return Title for specified page.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return "UNIT " + (position + 1);
    }
    // END_INCLUDE (fragment_pager_adapter_getpagetitle)
}