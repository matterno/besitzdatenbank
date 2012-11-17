package de.hsbremen.androidkurs.besitzdatenbank;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class ItemListActivity extends FragmentActivity implements
		ItemListFragment.Callbacks, ActionBar.OnNavigationListener {

	private boolean mTwoPane;

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

	SpinnerAdapter mSpinnerAdapter;

	List<String> categories;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		//TODO Hardcoded shit
		categories = new ArrayList<String>();
		categories.add("Elektronik");
		categories.add("Lebensmittel");
		categories.add("Filme");

		mSpinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, categories);

		actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);

		// Look if its the tablet (two pane) or normal layout
		if (findViewById(R.id.item_detail_container) != null) {
			mTwoPane = true;
			((ItemListFragment) getSupportFragmentManager().findFragmentById(
					R.id.item_list)).setActivateOnItemClick(true);
		} else {
			// Create the adapter that will return a fragment for each category of the app.
			mSectionsPagerAdapter = new SectionsPagerAdapter(
					getSupportFragmentManager());

			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mSectionsPagerAdapter);
		}
	}

	@Override
	public void onItemSelected(String id) {
		// show details fragment
		
		if (mTwoPane) {
			// TODO
			Bundle arguments = new Bundle();
			arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
			ItemDetailFragment fragment = new ItemDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.item_detail_container, fragment).commit();

		} else {
			// TODO
			Intent detailIntent = new Intent(this, ItemDetailActivity.class);
			detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putInt(ItemListFragment.EXTRA_CATEGORY, itemPosition);
			
			Fragment fragment = new ItemListFragment();
			fragment.setArguments(arguments);
			
			getSupportFragmentManager().beginTransaction().replace(R.id.item_list, fragment).commit();
		} else {	
			mViewPager.setCurrentItem(itemPosition);
		}
		return true;
	}

	/**
	 * Adapter needed for the swipe pager.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			//TODO Übergabe der Kategorie etc...
			
			Bundle arguments = new Bundle();
			arguments.putInt(ItemListFragment.EXTRA_CATEGORY, position);
			
			Fragment fragment = new ItemListFragment();
			fragment.setArguments(arguments);
			return fragment;
		}

		@Override
		public int getCount() {
			// TODO Get count of categories from SQLite-DB.
			// return 3;
			return categories.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return categories.get(position);

			// switch (position) {
			// case 0:
			// //TODO Aus SQLLite-DB (Title)
			// return "1";
			// case 1:
			// return "2";
			// case 2:
			// return "3";
			// }
			// return null;

			// return categoriesList.get(position);
		}
	}
}
