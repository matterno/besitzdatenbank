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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

public class ItemListActivity extends FragmentActivity implements
		ItemListFragment.Callbacks, ActionBar.OnNavigationListener, ViewPager.OnPageChangeListener {

	public static final String EXTRA_SELECTED_CATEGORY = "category";
	
	public static final String EXTRA_SELECTED_ITEM = "item";
	
	private boolean mTwoPane;
	
	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;

	private SpinnerAdapter mSpinnerAdapter;

	private List<String> categories;

	private int mSelectedCategory;
	
	private int mSelectedItem;

	private MenuItem mSearchMenuItem;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
		
		Log.d("ItemListActivity", "onCreate");

		mSelectedCategory = getIntent().getIntExtra(EXTRA_SELECTED_CATEGORY, 0);
		
		if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_SELECTED_CATEGORY)) {
			mSelectedCategory = savedInstanceState.getInt(EXTRA_SELECTED_CATEGORY);
		}
		if(savedInstanceState != null && savedInstanceState.containsKey(EXTRA_SELECTED_ITEM)) {
			mSelectedItem = savedInstanceState.getInt(EXTRA_SELECTED_ITEM);
		}
		
		Log.d("ItemListActivity", "mSelectedCategory = " + mSelectedCategory);
		Log.d("ItemListActivity", "mSelectedItem = " + mSelectedItem);
		
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
		actionBar.setSelectedNavigationItem(mSelectedCategory);
		actionBar.setDisplayShowTitleEnabled(false);
		
		// Look if its the tablet (two pane) or normal layout
		if (findViewById(R.id.item_detail_container) != null) {
			mTwoPane = true;
			
			Bundle arguments = new Bundle();
			arguments.putInt(EXTRA_SELECTED_CATEGORY, mSelectedCategory); // 0 at this point
			 
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
			mViewPager.setOnPageChangeListener(this);
			
			mViewPager.setCurrentItem(mSelectedCategory);
		}
	}
	
	@Override
	public void onItemSelected(int itemId) {
		Log.d("ItemListActivity", "onItemSelected");
		
		mSelectedItem = itemId;
		
		Log.d("ItemListActivity", "mSelectedItem = " + mSelectedItem);
		
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putInt(EXTRA_SELECTED_ITEM, mSelectedItem);
			arguments.putInt(EXTRA_SELECTED_CATEGORY, mSelectedCategory);
			ItemDetailFragment fragment = new ItemDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.item_detail_container, fragment).commit();
		} else {
			// TODO
			Intent detailIntent = new Intent(this, ItemDetailActivity.class);
			detailIntent.putExtra(EXTRA_SELECTED_ITEM, mSelectedItem);
			detailIntent.putExtra(EXTRA_SELECTED_CATEGORY, mSelectedCategory);
			startActivity(detailIntent);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Log.d("ItemListActivity", "onNavigationItemSelected");
		
		mSelectedCategory = itemPosition;
		
		Log.d("ItemListActivity", "mSelectedCategory = " + mSelectedCategory);
		
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			
			arguments.putInt(EXTRA_SELECTED_CATEGORY, mSelectedCategory);
			
			Fragment fragment = new ItemListFragment();
			fragment.setArguments(arguments);
			
			getSupportFragmentManager().beginTransaction().replace(R.id.item_list, fragment).commit();

			mSelectedItem = 0;
			this.onItemSelected(mSelectedItem);
		} else {	
			mViewPager.setCurrentItem(mSelectedCategory);
		}
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("ItemListActivity", "onSaveInstanceState");
		
		outState.putInt(EXTRA_SELECTED_CATEGORY, mSelectedCategory);
		outState.putInt(EXTRA_SELECTED_ITEM, mSelectedItem);
		
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("ItemListActivity", "onCreateOptionsMenu");
		
		getMenuInflater().inflate(R.menu.activity_item_list, menu);
		
		mSearchMenuItem = menu.findItem(R.id.menu_itemlist_search);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("ItemListActivity", "onOptionsItemSelected");
		
		// TODO
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d("ItemListActivity", "onKeyUp");
		
		// If an older devices has a search button, pressing it will expand the SearchView in the action bar.
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
			arguments.putInt(ItemListActivity.EXTRA_SELECTED_CATEGORY, position);
			
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

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		mSelectedCategory = position;
		
		Log.d("ItemListActivity", "mSelectedCategory = " + mSelectedCategory);
	}
}
