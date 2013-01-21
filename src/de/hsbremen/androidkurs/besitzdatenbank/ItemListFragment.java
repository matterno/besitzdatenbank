package de.hsbremen.androidkurs.besitzdatenbank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Item;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ItemListFragment extends ListFragment {

	private Callbacks mCallbacks = sDummyCallbacks;

	private Set<Integer> mSelectedItemIDs;

	public interface Callbacks {

		public void onItemSelected(int position, long itemId);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int position, long itemId) {
		}
	};

	long categoryID;

	List<Item> items;

	private ArrayAdapter<Item> mListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d("ItemListFragment", "onCreate");

		if (getArguments()
				.containsKey(ItemListActivity.EXTRA_SELECTED_CATEGORY)) {
			categoryID = getArguments().getLong(
					ItemListActivity.EXTRA_SELECTED_CATEGORY);
		}

		mSelectedItemIDs = new HashSet<Integer>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		items = BesitzApplication.getItemDataSource().findByCategoryId(
				categoryID);

		if (items.isEmpty()) {
			Log.d("ItemListFragment", "Items for category " + categoryID
					+ " are empty");
			Item item = new Item();
			item.setCategoryId(categoryID);
			item.setName("default item");
			BesitzApplication.getItemDataSource().insertItem(item);

			getItemsFromDB();
		}

		mListAdapter = new ArrayAdapter<Item>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, items);

		setListAdapter(mListAdapter);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		getListView().setPadding(16, 16, 16, 16);

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		ListView listview = getListView();
		listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listview.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				mSelectedItemIDs.clear();
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// Inflate the menu for the CAB
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.fragment_item_list_cab, menu);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// Respond to clicks on the actions in the CAB
				switch (item.getItemId()) {
				case R.id.menu_itemlist_cab_delete:
					deleteSelectedItems();
					mode.finish(); // Action picked, so close the CAB
					return true;
				default:
					return false;
				}
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				if (checked) {
					mSelectedItemIDs.add(position);
				} else {
					mSelectedItemIDs.remove(position);
				}
			}
		});

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.d("ItemListFragment", "onAttach");

		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		Log.d("ItemListFragment", "onDetach");

		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		mCallbacks.onItemSelected(position, items.get(position).getId());
	}

	private void deleteSelectedItems() {
		for (int position : mSelectedItemIDs) {
			BesitzApplication.getItemDataSource().deleteItem(
					items.get(position).getId());
		}
		getItemsFromDB();

		setListAdapter(new ArrayAdapter<Item>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, items));
	}

	private void getItemsFromDB() {
		items = BesitzApplication.getItemDataSource().findByCategoryId(
				categoryID);
	}
}
