package de.hsbremen.androidkurs.besitzdatenbank.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.BesitzApplication;
import de.hsbremen.androidkurs.besitzdatenbank.R;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Attribute;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.sax.StartElementListener;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
		final Attribute attribute = attributes.get(position);
		
		switch(attribute.getType()) {
		case Attribute.TYPE_TEXT:
			convertView = this.inflater.inflate(R.layout.list_item_text, null);
			TextView tv_text_name = (TextView) convertView.findViewById(R.id.tv_name);
			TextView tv_text_value = (TextView) convertView.findViewById(R.id.tv_value);
			
			tv_text_name.setText(attribute.getName());
			tv_text_value.setText(attribute.getValue());
			
			return convertView;
		case Attribute.TYPE_LOCATION:
			convertView = this.inflater.inflate(R.layout.list_item_location, null);
			TextView tv_location_name = (TextView) convertView.findViewById(R.id.tv_location_name);
			ImageView iv_location_maps = (ImageView) convertView.findViewById(R.id.iv_location_maps);
			
			tv_location_name.setText(attribute.getName());
			iv_location_maps.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					context.startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(attribute.getValue())));
				}
			});
			
			return convertView;
		case Attribute.TYPE_DATE:
			convertView = this.inflater.inflate(R.layout.list_item_text, null);
			TextView tv_date_name = (TextView) convertView.findViewById(R.id.tv_name);
			final TextView tv_date_value = (TextView) convertView.findViewById(R.id.tv_value);
			
			tv_date_name.setText(attribute.getName());
			tv_date_value.setText(attribute.getValue());
			
			final java.text.DateFormat sdf = DateFormat.getDateFormat(context);
			Date date = null;
			try {
				date = sdf.parse(attribute.getValue());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			final Calendar c = Calendar.getInstance();
			c.setTime(date);
			final int year = c.get(Calendar.YEAR);
			final int month = c.get(Calendar.MONTH);
			final int day = c.get(Calendar.DAY_OF_MONTH);
			
			tv_date_value.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					new DatePickerDialog(context,
							new OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									
									Date d = new Date(year-1900, monthOfYear, dayOfMonth);
									
									tv_date_value.setText(sdf.format(d.getTime()));
									
									attribute.setValue(sdf.format(d.getTime()));
									BesitzApplication.getAttributeDataSource().updateAttribute(attribute);
								}
							}, year, month, day).show();
				}
			});
			
			return convertView;
		case Attribute.TYPE_RATING:
			convertView = this.inflater.inflate(R.layout.list_item_rating, null);
			TextView tv_rating_name = (TextView) convertView.findViewById(R.id.tv_rating_name);
			final RatingBar rb_rating_rating = (RatingBar) convertView.findViewById(R.id.rb_bar);
			LinearLayout ll_rating_rating = (LinearLayout) convertView.findViewById(R.id.ll_rating_rating);
			
			tv_rating_name.setText(attribute.getName());
			rb_rating_rating.setProgress(Integer.parseInt(attribute.getValue()));
			ll_rating_rating.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Add attribute");
					
					View view = inflater.inflate(
							R.layout.rating_dialog, null);
					builder.setView(view);
					final RatingBar rating = (RatingBar) view.findViewById(R.id.rb_dialog_rating);
					rating.setProgress(Integer.parseInt(attribute.getValue()));
					builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							attribute.setValue(String.valueOf(rating.getProgress()));
							BesitzApplication.getAttributeDataSource().updateAttribute(attribute);
							rb_rating_rating.setProgress(rating.getProgress());
						}
					});
					builder.show();
				}
			});
			
			return convertView;
			
		}
		
		return null;
	}
}
