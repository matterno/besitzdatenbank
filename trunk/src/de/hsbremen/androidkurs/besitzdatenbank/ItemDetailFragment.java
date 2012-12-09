package de.hsbremen.androidkurs.besitzdatenbank;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Attribute;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Item;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Images.Thumbnails;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ItemDetailFragment extends Fragment {

	private TextView tv_name;

	private ImageView iv_image;

	private ListView lv_attributes;

	private TextView tv_add;

	private RelativeLayout rl_picture_container;

	private Item mItem;

	private long categoryId;

	private long mItemId;

	private List<Attribute> attributes;

	static final int PICK_CAMERA_REQUEST = 0;

	static final int PICK_PICTURE_REQUEST = 1;

	public ItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments()
				.containsKey(ItemListActivity.EXTRA_SELECTED_CATEGORY)) {
			categoryId = getArguments().getLong(
					ItemListActivity.EXTRA_SELECTED_CATEGORY);
		}

		Log.d("ItemDetailFragment", "categoryId = " + categoryId);

		if (getArguments().containsKey(ItemListActivity.EXTRA_SELECTED_ITEM)) {

			mItemId = getArguments().getLong(
					ItemListActivity.EXTRA_SELECTED_ITEM);

			Log.d("ItemDetailFragment", "mItemId = " + mItemId);

			// TODO SQL... Eigenschaften
			mItem = BesitzApplication.getItemDataSource().getItem(mItemId);

			Log.d("ItemDetailFragment", "mItem: " + mItem);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_detail,
				container, false);

		tv_name = (TextView) rootView.findViewById(R.id.tv_item_detail_name);
		iv_image = (ImageView) rootView
				.findViewById(R.id.iv_item_detail_images);
		lv_attributes = (ListView) rootView.findViewById(R.id.lv_attribute);
		tv_add = (TextView) rootView.findViewById(R.id.tv_item_detail_add);
		rl_picture_container = (RelativeLayout) rootView
				.findViewById(R.id.rl_picture_containers);

		// TODO Add ListAdapter to Layout
		// list = getFromDB
		attributes = new ArrayList<Attribute>();
		Attribute attr = new Attribute();
		attr.setName("Test");
		attributes.add(attr);
		ArrayAdapter<Attribute> adapter = new ArrayAdapter<Attribute>(
				getActivity(), android.R.layout.simple_list_item_1, attributes);
		lv_attributes.setAdapter(adapter);

		if (mItem != null) {
			tv_name.setText(mItem.getName());

			String picture = mItem.getPicture();
			if (picture != null && !"".equals(picture)) {
				try{
					Uri pic = Uri.parse(mItem.getPicture());
					iv_image.setImageURI(pic);
				} catch (SecurityException se) {
					Log.d("afsfas", "Bin drin");
					iv_image.setImageResource(android.R.drawable.ic_input_get);
				}
			}
		}

		rl_picture_container.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPictureDialog();
			}
		});

		tv_add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				Toast.makeText(ItemDetailFragment.this.getActivity(),
						"Eigenschaft hinzufügen", Toast.LENGTH_SHORT).show();
			}
		});

		return rootView;
	}

	private void showPictureDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.dialog_choose_location));
		// TODO Switch Case menu title

		final CharSequence[] items = {
				getString(R.string.dialog_location_camera),
				getString(R.string.dialog_location_gallery) };

		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// Camera
					Intent takePicture = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					Uri fileUri = getOutputMediaFileUri();
					takePicture.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

					startActivityForResult(takePicture, PICK_CAMERA_REQUEST);
					break;
				case 1:
					// Gallery
					Intent pickPhoto = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
					startActivityForResult(pickPhoto, PICK_PICTURE_REQUEST);
					break;
				}
			}
		}).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PICK_CAMERA_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				iv_image.setImageURI(selectedImage);
			}

			break;
		case PICK_PICTURE_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				iv_image.setImageURI(selectedImage);

				mItem.setPicture(selectedImage.toString());
				BesitzApplication.getItemDataSource().updateItem(mItem);
			}
			break;
		}
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri() {
		return Uri.fromFile(getOutputMediaFile());
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"BesitzDB");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("BesitzDB", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}
}
