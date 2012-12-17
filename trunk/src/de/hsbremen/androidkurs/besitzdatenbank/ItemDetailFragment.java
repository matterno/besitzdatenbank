package de.hsbremen.androidkurs.besitzdatenbank;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Attribute;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Item;
import de.hsbremen.androidkurs.besitzdatenbank.util.AlbumStorageDirFactory;
import de.hsbremen.androidkurs.besitzdatenbank.util.BaseAlbumDirFactory;
import de.hsbremen.androidkurs.besitzdatenbank.util.FroyoAlbumDirFactory;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ItemDetailFragment extends Fragment {

	private static final String JPEG_FILE_PREFIX = "IMG_";

	private static final String JPEG_FILE_SUFFIX = ".jpg";

	private TextView tv_name;

	private ImageView iv_image;

	private ListView lv_attributes;

	private TextView tv_add;

	private RelativeLayout rl_picture_container;

	private Item mItem;

	private long mCategoryId;

	private long mItemId;

	private List<Attribute> attributes;

	static final int PICK_CAMERA_REQUEST = 0;

	static final int PICK_PICTURE_REQUEST = 1;

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	private boolean mTwoPane;

	public ItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments()
				.containsKey(ItemListActivity.EXTRA_SELECTED_CATEGORY)) {
			mCategoryId = getArguments().getLong(
					ItemListActivity.EXTRA_SELECTED_CATEGORY);
		}

		if (getArguments().containsKey(ItemListActivity.EXTRA_SELECTED_ITEM)) {
			mItemId = getArguments().getLong(
					ItemListActivity.EXTRA_SELECTED_ITEM);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
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

		mItem = BesitzApplication.getItemDataSource().getItem(mItemId);

		if (mItem != null) {
			tv_name.setText(mItem.getName());

			String picture = mItem.getPicture();
			if (picture != null && !"".equals(picture)) {
				setPic();
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
				//TODO
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Add attribute");
				View view = getActivity().getLayoutInflater().inflate(R.layout.activity_dialog, null);
				builder.setView(view);
				builder.setPositiveButton("OK", null);
				builder.setNegativeButton("Cancel", null);
				builder.show();
				
				// TODO
				Toast.makeText(ItemDetailFragment.this.getActivity(),
						"Eigenschaft hinzufügen", Toast.LENGTH_SHORT).show();
			}
		});

		if (getActivity() instanceof ItemDetailActivity) {
			mTwoPane = false;

			this.setHasOptionsMenu(true);

			ActionBar actionBar = getActivity().getActionBar();
			actionBar.setTitle(tv_name.getText());
			tv_name.setVisibility(TextView.INVISIBLE);
		}

		return rootView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PICK_CAMERA_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				handleBigCameraPhoto();
			}

			break;
		case PICK_PICTURE_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				mItem.setPicture(getRealPathFromURI(selectedImage));
				BesitzApplication.getItemDataSource().updateItem(mItem);
				setPic();
			}
			break;
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_item_detail, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_itemdetail_edit:
			// TODO Auf DB zugreifen und Item-Name änder
			break;
		case R.id.menu_itemdetail_delete:
			// TODO Auf DB zugreifen und Item löschen
			// Danach neuen Intent zu ItemListActivity
			// BesitzApplication.getItemDataSource().deleteItem(mItemId);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().managedQuery(contentUri, proj, null,
				null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private void showPictureDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.dialog_choose_location));

		final CharSequence[] items = {
				getString(R.string.dialog_location_camera),
				getString(R.string.dialog_location_gallery) };

		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					// Camera
					dispatchTakePictureIntent();
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

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File f = null;

		try {
			f = setUpPhotoFile();
			mItem.setPicture(f.getAbsolutePath());
			takePictureIntent
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		} catch (IOException e) {
			e.printStackTrace();
			f = null;
			mItem.setPicture(null);
		}

		startActivityForResult(takePictureIntent, PICK_CAMERA_REQUEST);
	}

	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
				albumF);
		return imageF;
	}

	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		mItem.setPicture(f.getAbsolutePath());

		return f;
	}

	/* Photo album for this application */
	private String getAlbumName() {
		return "BesitzDB";
		// return getString(R.string.album_name);
	}

	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory
					.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("Camera", "failed to create directory");
						return null;
					}
				}
			}

		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	private void handleBigCameraPhoto() {
		if (mItem.getPicture() != null) {
			setPic();
			galleryAddPic();
			BesitzApplication.getItemDataSource().updateItem(mItem);
			mItem.setPicture(null);
		}
	}

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = iv_image.getWidth();
		int targetH = iv_image.getHeight();

		try {
			Field maxWidthField = ImageView.class.getDeclaredField("mMaxWidth");
			Field maxHeightField = ImageView.class
					.getDeclaredField("mMaxHeight");
			maxWidthField.setAccessible(true);
			maxHeightField.setAccessible(true);

			targetW = (Integer) maxWidthField.get(iv_image);
			targetH = (Integer) maxHeightField.get(iv_image);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mItem.getPicture(), bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mItem.getPicture(), bmOptions);

		/* Associate the Bitmap to the ImageView */
		iv_image.setImageBitmap(bitmap);
	}

	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(
				"android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mItem.getPicture());
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		getActivity().sendBroadcast(mediaScanIntent);
	}
}
