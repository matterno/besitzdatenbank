package de.hsbremen.androidkurs.besitzdatenbank;

import de.hsbremen.androidkurs.besitzdatenbank.dummy.DummyContent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ItemDetailFragment extends Fragment {

    String mItem;

    private int categoryId;
    
    private int itemId;
    
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ItemListActivity.EXTRA_SELECTED_CATEGORY)) {
        	categoryId = getArguments().getInt(ItemListActivity.EXTRA_SELECTED_CATEGORY);
        }
        
        Log.d("ItemDetailFragment", "categoryId = " + categoryId);
        
        if (getArguments().containsKey(ItemListActivity.EXTRA_SELECTED_ITEM)) {
        	
        	itemId = getArguments().getInt(ItemListActivity.EXTRA_SELECTED_ITEM);
        	
        	Log.d("ItemDetailFragment", "itemId = " + itemId);
        	
        	// TODO SQL... Eigenschaften
            mItem = DummyContent.getList(categoryId).get(itemId);
        	
            Log.d("ItemDetailFragment", "mItem: " + mItem);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem);
        }
        return rootView;
    }
}
