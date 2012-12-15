package de.hsbremen.androidkurs.besitzdatenbank;

import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Category;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Item;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.SpinnerAdapter;

public class ItemListActivity extends FragmentActivity implements
		ItemListFragment.Callbacks, ActionBar.OnNavigationListener,
		ViewPager.OnPageChangeListener {

	public static final String EXTRA_SELECTED_CATEGORY = "category";

	public static final String EXTRA_SELECTED_ITEM = "item";

	private boolean mTwoPane;

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;

	private SpinnerAdapter mSpinnerAdapter;

	private List<Category> categories;

	private int mSelectedCategory;

	private int mSelectedItem;

	private MenuItem mSearchMenuItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);

		Log.d("ItemListActivity", "onCreate");

		if (savedInstanceState != null
				&& savedInstanceState.containsKey(EXTRA_SELECTED_CATEGORY)) {
			mSelectedCategory = savedInstanceState
					.getInt(EXTRA_SELECTED_CATEGORY);
		}
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(EXTRA_SELECTED_ITEM)) {
			mSelectedItem = savedInstanceState.getInt(EXTRA_SELECTED_ITEM);
		}

		Log.d("ItemListActivity", "mSelectedCategory = " + mSelectedCategory);
		Log.d("ItemListActivity", "mSelectedItem = " + mSelectedItem);

		categories = BesitzApplication.getCategoryDataSource()
				.getAllCategories();

		// Handling if no categories in DB.

		ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.greenPrimary));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		addDefaultCategoryOnEmpty();
		refreshActionBarNavigationAdapter();

		actionBar.setSelectedNavigationItem(mSelectedCategory);

		// Look if its the tablet (two pane) or normal layout
		if (findViewById(R.id.item_detail_container) != null) {
			mTwoPane = true;

			refreshItemList();
		} else {
			// Create the adapter that will return a fragment for each category
			// of the app.
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
	public void onItemSelected(int position, long itemId) {
		Log.d("ItemListActivity", "onItemSelected");

		mSelectedItem = position;

		Log.d("ItemListActivity", "mSelectedItem = " + mSelectedItem);

		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putLong(EXTRA_SELECTED_ITEM, itemId);
			arguments.putLong(
					EXTRA_SELECTED_CATEGORY,
					BesitzApplication.getCategoryDataSource().findIdByName(
							categories.get(mSelectedCategory).toString()));
			ItemDetailFragment fragment = new ItemDetailFragment();
			fragment.setArguments(arguments);
			
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.item_detail_container, fragment).commit();
		} else {
			// TODO
			Intent detailIntent = new Intent(this, ItemDetailActivity.class);
			detailIntent.putExtra(EXTRA_SELECTED_ITEM, itemId);
			detailIntent.putExtra(EXTRA_SELECTED_CATEGORY,
					categories.get(mSelectedCategory).getId());
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

			arguments.putLong(EXTRA_SELECTED_CATEGORY,
					categories.get(mSelectedCategory).getId());

			ItemListFragment fragment = new ItemListFragment();
			fragment.setArguments(arguments);
			
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.item_list, fragment).commit();

			mSelectedItem = 0;
			this.onItemSelected(itemPosition, BesitzApplication
					.getItemDataSource().getAllItems().get(0).getId());
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

		switch (item.getItemId()) {
		case R.id.menu_itemlist_add:
			showAddChooseDialog();
			break;
		case R.id.menu_itemlist_delete:
			showDeleteConfirmationDialog();
			break;
		case R.id.menu_itemlist_edit:
			showEditInputDialog();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d("ItemListActivity", "onKeyUp");

		// If an older devices has a search button, pressing it will expand the
		// SearchView in the action bar.
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			mSearchMenuItem.expandActionView();
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * Adapter needed for the swipe pager.
	 */
	private class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Übergabe der Kategorie etc...

			Bundle arguments = new Bundle();
			arguments.putLong(ItemListActivity.EXTRA_SELECTED_CATEGORY,
					categories.get(position).getId());

			Fragment fragment = new ItemListFragment();
			fragment.setArguments(arguments);
			return fragment;
		}

		@Override
		public int getCount() {
			return categories.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return categories.get(position).toString();
		}
		
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		mSelectedCategory = position;

		Log.d("ItemListActivity", "mSelectedCategory = " + mSelectedCategory);
	}

	private void showAddChooseDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.menu_add));
		// TODO Switch Case menu title

		final CharSequence[] items = { getString(R.string.category),
				getString(R.string.item) }; // TODO strings xml

		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// Category
					showAddInputDialog(R.string.category);
					break;
				case 1:
					// Item
					showAddInputDialog(R.string.item);
					break;
				}
			}
		}).show();
	}

	private void showAddInputDialog(final int typeId) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.menu_add) + " " + getString(typeId));
		
		final EditText input = new EditText(this);
		builder.setView(input);

		builder.setPositiveButton(R.string.btn_save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String value = input.getText().toString();

						switch (typeId) {
						case R.string.category:
							Category cat = new Category();
							cat.setName(value);
							BesitzApplication.getCategoryDataSource()
									.insertCategory(cat);

							refreshActionBarNavigationAdapter();
							break;
						case R.string.item:
							// TODO save new item
							Item item = new Item();
							item.setName(value);
							item.setCategoryId(categories
									.get(mSelectedCategory).getId());
							BesitzApplication.getItemDataSource().insertItem(
									item);

							refreshItemList();
							break;
						default:
							break;
						}

					}
				});

		builder.setNegativeButton(R.string.btn_cancel, null);

		builder.show();
	}
	
	private void showEditInputDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.menu_edit));

		Category cat = categories.get(mSelectedCategory);
		
		final EditText input = new EditText(this);
		input.setText(cat.getName());
		builder.setView(input);

		builder.setPositiveButton(R.string.btn_save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String value = input.getText().toString();

						Category cat = categories.get(mSelectedCategory);
						cat.setName(value);
						BesitzApplication.getCategoryDataSource().updateCategory(cat);
						
						refreshActionBarNavigationAdapter();
					}
				});

		builder.setNegativeButton(R.string.btn_cancel, null);

		builder.show();
	}
	
	
	private void showDeleteConfirmationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.menu_delete);
		
		builder.setMessage(R.string.delete_dialog_confirmation);

		builder.setPositiveButton(R.string.btn_save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						BesitzApplication.getCategoryDataSource().deleteCategory(categories.get(mSelectedCategory).getId());
						categories = BesitzApplication.getCategoryDataSource().getAllCategories();
						
						addDefaultCategoryOnEmpty();
						refreshActionBarNavigationAdapter();
					}
				});

		builder.setNegativeButton(R.string.btn_cancel, null);

		builder.show();
	}

	private void refreshActionBarNavigationAdapter() {
		categories = BesitzApplication.getCategoryDataSource()
				.getAllCategories();
		mSpinnerAdapter = new ArrayAdapter<Category>(getActionBar().getThemedContext(),
				android.R.layout.simple_spinner_dropdown_item, categories);

		getActionBar().setListNavigationCallbacks(mSpinnerAdapter,
				ItemListActivity.this);
	}

	private void refreshItemList() {
		if (mTwoPane) {

			Bundle arguments = new Bundle();
			// Category is 0 at this point
			arguments.putLong(
					EXTRA_SELECTED_CATEGORY,
					BesitzApplication.getCategoryDataSource().findIdByName(
							categories.get(mSelectedCategory).toString()));

			Fragment fragment = new ItemListFragment();
			fragment.setArguments(arguments);

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.item_list, fragment).commit();

		} else {
			mSectionsPagerAdapter.notifyDataSetChanged();
			return;
		}

	}
	
	private void addDefaultCategoryOnEmpty() {
		if (categories.isEmpty()) {
			Category category = new Category();
			category.setName("default category");
			BesitzApplication.getCategoryDataSource().insertCategory(category);
		}
	}
}
