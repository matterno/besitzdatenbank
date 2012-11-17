package de.hsbremen.androidkurs.besitzdatenbank;

import java.util.ArrayList;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.dummy.DummyContent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ItemListFragment extends ListFragment {

	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	public static final String ARG_CATEGORY_ID = "cat";

	private Callbacks mCallbacks = sDummyCallbacks;
	
	private int mActivatedPosition = ListView.INVALID_POSITION;

	public interface Callbacks {

		public void onItemSelected(int itemId, int categoryId);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int itemId, int categoryId) {
		}
	};

	public ItemListFragment() {
	}

	int categoryID;
	
	List<String> items;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(getArguments().containsKey(ARG_CATEGORY_ID)) {
			categoryID = getArguments().getInt(ARG_CATEGORY_ID);
		}
		

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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		mActivatedPosition = position;
		
		mCallbacks.onItemSelected(position, this.categoryID);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			mCallbacks.onItemSelected(savedInstanceState.getInt(STATE_ACTIVATED_POSITION), this.categoryID);
		}
	}
	
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
}
