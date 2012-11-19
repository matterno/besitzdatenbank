package de.hsbremen.androidkurs.besitzdatenbank;

import java.util.ArrayList;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.dummy.DummyContent;

import android.app.Activity;
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

		public void onItemSelected(int itemId);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int itemId) {
		}
	};

	public ItemListFragment() {
	}

	int categoryID;
	
	List<String> items;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("ItemListFragment", "onCreate");
		
		if(getArguments().containsKey(ItemListActivity.EXTRA_SELECTED_CATEGORY)) {
			categoryID = getArguments().getInt(ItemListActivity.EXTRA_SELECTED_CATEGORY);
		}
		Log.d("ItemListFragment", "categoryID = " + categoryID);
		

		// TODO Get Items

		items = new ArrayList<String>();

		// TODO Harcoded shit
		switch (categoryID) {
		case 0:
			items = DummyContent.ELEKTRONIK;
			break;
		case 1:
			items = DummyContent.LEBENSMITTEL;
			break;
		case 2:
			items = DummyContent.FILME;
			break;
		}

		setListAdapter(new ArrayAdapter<String>(getActivity(),
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
		
		mCallbacks.onItemSelected(position);
	}
}
