package de.hsbremen.androidkurs.besitzdatenbank;

import de.hsbremen.androidkurs.besitzdatenbank.dummy.DummyContent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ItemDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    String mItem;

    int categoryId;
    
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ItemListFragment.ARG_CATEGORY_ID)) {
        	categoryId = getArguments().getInt(ItemListFragment.ARG_CATEGORY_ID);
        }
        
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = DummyContent.getList(categoryId).get(getArguments().getInt(ARG_ITEM_ID));
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
