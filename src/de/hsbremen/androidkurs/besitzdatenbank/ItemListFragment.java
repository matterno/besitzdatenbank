package de.hsbremen.androidkurs.besitzdatenbank;

import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Item;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ItemListFragment extends ListFragment {

//	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private Callbacks mCallbacks = sDummyCallbacks;
	
	private int mSelectedItem = ListView.INVALID_POSITION;

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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("ItemListFragment", "onCreate");
		
		if(getArguments().containsKey(ItemListActivity.EXTRA_SELECTED_CATEGORY)) {
			categoryID = getArguments().getLong(ItemListActivity.EXTRA_SELECTED_CATEGORY);
		}
		Log.d("ItemListFragment", "categoryID = " + categoryID);
		
		items = BesitzApplication.getItemDataSource().findByCategoryId(categoryID);

		if(items.isEmpty()) {
			Log.d("ItemListFragment", "Items for category " + categoryID + " are empty");
			Item item = new Item();
			item.setCategoryId(categoryID);
			item.setName("default item");
			BesitzApplication.getItemDataSource().insertItem(item);
			
			items = BesitzApplication.getItemDataSource().findByCategoryId(categoryID);
		}
		
		setListAdapter(new ArrayAdapter<Item>(getActivity(),
				android.R.layout.simple_list_item_activated_1, android.R.id.text1,
				items));
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
		
		Log.d("ItemListFragment", "onListItemClick");
		
		mSelectedItem = position;
		
		Log.d("ItemListFragment", "mSelectedItem = " + mSelectedItem);
		
		mCallbacks.onItemSelected(position, items.get(position).getId());
	}
}
