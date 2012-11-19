package de.hsbremen.androidkurs.besitzdatenbank;

import de.hsbremen.androidkurs.besitzdatenbank.dummy.DummyContent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ItemDetailFragment extends Fragment {

	private TextView tv_name;
	
	private ImageView iv_image;
	
	private LinearLayout ll_atributes;
	
    private String mItem;

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
        
        tv_name = (TextView) rootView.findViewById(R.id.tv_item_detail_name);
        iv_image = (ImageView) rootView.findViewById(R.id.iv_item_detail_image);
        ll_atributes = (LinearLayout) rootView.findViewById(R.id.ll_attributes);
        
        //TODO Add ListAdapter to Layout
        
        if (mItem != null) {
            tv_name.setText(mItem);
        }
        
        iv_image.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(ItemDetailFragment.this.getActivity(), "Bild auswählen", Toast.LENGTH_SHORT).show();
			}
		});
        
        return rootView;
    }
}
