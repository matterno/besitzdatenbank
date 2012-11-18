package de.hsbremen.androidkurs.besitzdatenbank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class ItemDetailActivity extends FragmentActivity {

	private int category;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(ItemListActivity.EXTRA_SELECTED_ITEM,
                    getIntent().getIntExtra(ItemListActivity.EXTRA_SELECTED_ITEM,0));
            category = getIntent().getIntExtra(ItemListActivity.EXTRA_SELECTED_CATEGORY,0);
            arguments.putInt(ItemListActivity.EXTRA_SELECTED_CATEGORY,category);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
        	Intent intent = new Intent(this, ItemListActivity.class); 
        	intent.putExtra(ItemListActivity.EXTRA_SELECTED_CATEGORY, category);
        	
            NavUtils.navigateUpTo(this, intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
