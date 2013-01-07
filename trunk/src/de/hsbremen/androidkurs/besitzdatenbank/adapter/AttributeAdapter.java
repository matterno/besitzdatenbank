package de.hsbremen.androidkurs.besitzdatenbank.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.R;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Attribute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AttributeAdapter extends BaseAdapter {

	private List<Attribute> attributes;
	
	private LayoutInflater inflater;
	
	private Context context;
	
	public AttributeAdapter(Context context, List<Attribute> attributes) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.attributes = attributes;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return attributes.size();
	}

	@Override
	public Attribute getItem(int position) {
		return attributes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return attributes.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Attribute attribute = attributes.get(position);
		
		switch(attribute.getType()) {
		case Attribute.TYPE_TEXT:
			convertView = this.inflater.inflate(R.layout.list_item_text, null);
			TextView tv_text_name = (TextView) convertView.findViewById(R.id.tv_name);
			TextView tv_text_value = (TextView) convertView.findViewById(R.id.tv_value);
			ImageView iv_text_edit = (ImageView) convertView.findViewById(R.id.iv_edit);
			
			tv_text_name.setText(attribute.getName() + ":");
			tv_text_value.setText(attribute.getValue());
			
			return convertView;
		case Attribute.TYPE_LOCATION:
			convertView = this.inflater.inflate(R.layout.list_item_location, null);
			TextView tv_location_name = (TextView) convertView.findViewById(R.id.tv_location_name);
			ImageView iv_location_maps = (ImageView) convertView.findViewById(R.id.iv_location_maps);
			ImageView iv_location_edit = (ImageView) convertView.findViewById(R.id.iv_location_edit);
			
			tv_location_name.setText(attribute.getName());
			iv_location_maps.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "ES GEHT!", Toast.LENGTH_SHORT).show();
					
				}
			});
			
			return convertView;
		case Attribute.TYPE_DATE:
			convertView = this.inflater.inflate(R.layout.list_item_text, null);
			TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			TextView tv_value = (TextView) convertView.findViewById(R.id.tv_value);
			ImageView iv_edit = (ImageView) convertView.findViewById(R.id.iv_edit);
			
			DateFormat df = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
			Date d = new Date(Long.parseLong(attribute.getValue()));
			
			tv_name.setText(attribute.getName() + ":");
			tv_value.setText(df.format(d));
			
			return convertView;
			
		}
		// TODO Auto-generated method stub
		return null;
	}
}
