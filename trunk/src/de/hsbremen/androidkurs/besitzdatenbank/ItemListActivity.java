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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class ItemListActivity extends FragmentActivity implements
		ItemListFragment.Callbacks, ActionBar.OnNavigationListener {

	private static final String EXTRA_SELECTED_CATEGORY = "category";
	
	private boolean mTwoPane;
	
	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;

	private SpinnerAdapter mSpinnerAdapter;

	private List<String> categories;

	private int mSelectedCategory;

	private MenuItem mSearchMenuItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);

		//TODO Hardcoded shit
		categories = new ArrayList<String>();
		categories.add("Elektronik");
		categories.add("Lebensmittel");
		categories.add("Filme");

		mSpinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, categories);
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
		
		if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_SELECTED_CATEGORY)) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(EXTRA_SELECTED_CATEGORY));
		}

		// Look if its the tablet (two pane) or normal layout
		if (findViewById(R.id.item_detail_container) != null) {
			mTwoPane = true;
			
			Bundle arguments = new Bundle();
			arguments.putInt(ItemListFragment.ARG_CATEGORY_ID, 0); //TODO 0 = id des ersten Elements. Wenn keins da, ...?
			 
			Fragment fragment = new ItemListFragment();
			fragment.setArguments(arguments);
			
			getSupportFragmentManager().beginTransaction().replace(R.id.item_list, fragment).commit();
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
	public void onItemSelected(int itemId, int categoryId) {
		// show details fragment
		
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putInt(ItemDetailFragment.ARG_ITEM_ID, itemId);
			arguments.putInt(ItemListFragment.ARG_CATEGORY_ID, categoryId);
			ItemDetailFragment fragment = new ItemDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.item_detail_container, fragment).commit();

		} else {
			// TODO
			Intent detailIntent = new Intent(this, ItemDetailActivity.class);
			detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, itemId);
			detailIntent.putExtra(ItemListFragment.ARG_CATEGORY_ID, categoryId);
			startActivity(detailIntent);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		mSelectedCategory = itemPosition;
		
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putInt(ItemListFragment.ARG_CATEGORY_ID, itemPosition);
			
			Fragment fragment = new ItemListFragment();
			fragment.setArguments(arguments);
			
			getSupportFragmentManager().beginTransaction().replace(R.id.item_list, fragment).commit();
		} else {	
			mViewPager.setCurrentItem(itemPosition);
		}
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(EXTRA_SELECTED_CATEGORY, mSelectedCategory);
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_item_list, menu);
		
		mSearchMenuItem = menu.findItem(R.id.menu_itemlist_search);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_SEARCH) {
			mSearchMenuItem.expandActionView();
		}
		return super.onKeyUp(keyCode, event);
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
			arguments.putInt(ItemListFragment.ARG_CATEGORY_ID, position);
			
			Fragment fragment = new ItemListFragment();
			fragment.setArguments(arguments);
			return fragment;
		}

		@Override
		public int getCount() {
			// TODO Get count of categories from SQLite-DB.
			return categories.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO From DB
			return categories.get(position);
		}
	}
}
