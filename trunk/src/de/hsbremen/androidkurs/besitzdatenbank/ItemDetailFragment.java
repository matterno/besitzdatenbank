package de.hsbremen.androidkurs.besitzdatenbank;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hsbremen.androidkurs.besitzdatenbank.adapter.AttributeAdapter;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.AttributeDataSource;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Attribute;
import de.hsbremen.androidkurs.besitzdatenbank.sqlite.entity.Item;
import de.hsbremen.androidkurs.besitzdatenbank.util.AlbumStorageDirFactory;
import de.hsbremen.androidkurs.besitzdatenbank.util.BaseAlbumDirFactory;
import de.hsbremen.androidkurs.besitzdatenbank.util.FroyoAlbumDirFactory;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

	private long mItemId;

	private List<Attribute> mAttributes;

	static final int PICK_CAMERA_REQUEST = 0;

	static final int PICK_PICTURE_REQUEST = 1;

	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

	private long mCategoryId;

	private LocationManager mLocationManager;
	
	private Location mLocation;

	private AttributeAdapter mAdapter;
	
    // Keys for maintaining UI states after rotation.
    private static final int TEN_SECONDS = 10000;
    private static final int TEN_METERS = 10;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

	public ItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ItemListActivity.EXTRA_SELECTED_ITEM)) {
			mItemId = getArguments().getLong(
					ItemListActivity.EXTRA_SELECTED_ITEM);
		}

		if (getArguments()
				.containsKey(ItemListActivity.EXTRA_SELECTED_CATEGORY)) {
			mCategoryId = getArguments().getLong(
					ItemListActivity.EXTRA_SELECTED_CATEGORY);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}

		mLocationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
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

		// Get attributes from DB
		mAttributes = BesitzApplication.getAttributeDataSource().findByItemId(mItemId);
		mAdapter = new AttributeAdapter(getActivity(),
				mAttributes);
		lv_attributes.setAdapter(mAdapter);
		lv_attributes.setSelector(android.R.color.transparent);
		
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
				showNewAttributeDialog();
			}
		});

		if (getActivity() instanceof ItemDetailActivity) {
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
			showEditInputDialog();
			break;
		case R.id.menu_itemdetail_delete:
			showDeleteConfirmationDialog();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setup();
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

	private void showDeleteConfirmationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.menu_delete);

		builder.setMessage(R.string.delete_dialog_confirmation);

		builder.setPositiveButton(R.string.btn_save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						BesitzApplication.getItemDataSource().deleteItem(
								mItemId);
						Intent intent = new Intent(getActivity(),
								ItemListActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				});

		builder.setNegativeButton(R.string.btn_cancel, null);

		builder.show();
	}

	private void showEditInputDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.edit_item));

		final EditText input = new EditText(getActivity());
		input.setText(this.mItem.getName());
		builder.setView(input);

		builder.setPositiveButton(R.string.btn_save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String value = input.getText().toString();

						mItem.setName(value);
						BesitzApplication.getItemDataSource().updateItem(mItem);

						getActivity().getActionBar().setTitle(value);
					}
				});

		builder.setNegativeButton(R.string.btn_cancel, null);

		builder.show();
	}

	protected void showNewAttributeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Add attribute");
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.activity_dialog, null);
		builder.setView(view);

		final Spinner sp_attributes = (Spinner) view.findViewById(R.id.sp_type);
		final EditText edt_name = (EditText) view.findViewById(R.id.edt_name);
		final EditText edt_value = (EditText) view.findViewById(R.id.edt_value);
		final Button btn_date = (Button) view.findViewById(R.id.btn_date);
		final RatingBar rb_rating = (RatingBar) view
				.findViewById(R.id.rb_rating);

		sp_attributes.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switch (arg2) {
				case 0:
					//Text
					edt_name.setVisibility(View.VISIBLE);
					edt_value.setVisibility(View.VISIBLE);
					btn_date.setVisibility(View.GONE);
					rb_rating.setVisibility(View.GONE);
					break;
				case 1:
					//Location
					edt_name.setVisibility(View.VISIBLE);
					edt_value.setVisibility(View.GONE);
					btn_date.setVisibility(View.GONE);
					rb_rating.setVisibility(View.GONE);
					break;
				case 2:
					//Date
					edt_name.setVisibility(View.VISIBLE);
					edt_value.setVisibility(View.GONE);
					btn_date.setVisibility(View.VISIBLE);
					rb_rating.setVisibility(View.GONE);

					final Calendar c = Calendar.getInstance();
					final int year = c.get(Calendar.YEAR);
					final int month = c.get(Calendar.MONTH);
					final int day = c.get(Calendar.DAY_OF_MONTH);
					
					final java.text.DateFormat sdf = DateFormat.getDateFormat(getActivity());
					
					btn_date.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							new DatePickerDialog(getActivity(),
									new OnDateSetListener() {
										@Override
										public void onDateSet(DatePicker view,
												int year, int monthOfYear,
												int dayOfMonth) {
											
											Date d = new Date(year-1900, monthOfYear, dayOfMonth);
											
											btn_date.setText(sdf.format(d.getTime()));
										}
									}, year, month, day).show();
						}
					});
					btn_date.setText(sdf.format(c.getTime()));
					break;
				case 3:
					//Rating
					edt_name.setVisibility(View.VISIBLE);
					edt_value.setVisibility(View.GONE);
					btn_date.setVisibility(View.GONE);
					rb_rating.setVisibility(View.VISIBLE);
					break;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				sp_attributes.setSelection(0);
			}
		});

		// Set buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AttributeDataSource attrDS = BesitzApplication
						.getAttributeDataSource();
				Attribute attribute = null;
				
				switch (sp_attributes.getSelectedItemPosition()) {
				case 0:
					//Text
					attribute = new Attribute(Attribute.TYPE_TEXT, edt_name
							.getText().toString(), edt_value.getText()
							.toString(), mItemId);
					attrDS.insertAttribute(attribute);
					break;
				case 1:
					//Location
					
					if(mLocation != null) {
						String location = "geo:" + mLocation.getLatitude() + "," + mLocation.getLongitude();
						attribute = new Attribute(Attribute.TYPE_LOCATION, edt_name
								.getText().toString(),location, mItemId);
						attrDS.insertAttribute(attribute);
					} else {
						Toast.makeText(getActivity(), "No location found", Toast.LENGTH_SHORT).show();
					}
					break;
				case 2:
					//Date
					
					attribute = new Attribute(Attribute.TYPE_DATE, edt_name
							.getText().toString(), btn_date.getText()
							.toString(), mItemId);
					attrDS.insertAttribute(attribute);
					break;
				case 3:
					//Rating
					
					attribute = new Attribute(Attribute.TYPE_RATING, edt_name
							.getText().toString(), String.valueOf(rb_rating.getProgress()), mItemId);
					attrDS.insertAttribute(attribute);
					break;
				}
				
				if(attribute != null) {
					mAttributes.add(attribute);
					mAdapter.notifyDataSetChanged();
				}
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.show();
	}

	// Set up fine and/or coarse location providers depending on whether the
	// fine provider or
	// both providers button is pressed.
	private void setup() {
		Location gpsLocation = null;
		Location networkLocation = null;
		mLocationManager.removeUpdates(listener);
		// Request updates from both fine (gps) and coarse (network) providers.
		gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER);
		networkLocation = requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER);

		// If both providers return last known locations, compare the two and
		// use the better
		// one to update the UI. If only one provider returns a location, use
		// it.
		if (gpsLocation != null && networkLocation != null) {
			updateLocation(getBetterLocation(gpsLocation, networkLocation));
		} else if (gpsLocation != null) {
			updateLocation(gpsLocation);
		} else if (networkLocation != null) {
			updateLocation(networkLocation);
		}
	}
	
	/**
     * Method to register location updates with a desired location provider.  If the requested
     * provider is not available on the device, the app displays a Toast with a message referenced
     * by a resource id.
     *
     * @param provider Name of the requested provider.
     * @param errorResId Resource id for the string message to be displayed if the provider does
     *                   not exist on the device.
     * @return A previously returned {@link android.location.Location} from the requested provider,
     *         if exists.
     */
    private Location requestUpdatesFromProvider(final String provider) {
        Location location = null;
        if (mLocationManager.isProviderEnabled(provider)) {
            mLocationManager.requestLocationUpdates(provider, TEN_SECONDS, TEN_METERS, listener);
            location = mLocationManager.getLastKnownLocation(provider);
        }
        
        return location;
    }
    
    private final LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // A new location update is received.  Do something useful with it.  Update the UI with
            // the location update.
            updateLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

	protected void updateLocation(Location location) {
		mLocation = location;
	}
	
    /** Determines whether one Location reading is better than the current Location fix.
     * Code taken from
     * http://developer.android.com/guide/topics/location/obtaining-user-location.html
     *
     * @param newLocation  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new
     *        one
     * @return The better Location object based on recency and accuracy.
     */
   protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
       if (currentBestLocation == null) {
           // A new location is always better than no location
           return newLocation;
       }

       // Check whether the new location fix is newer or older
       long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
       boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
       boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
       boolean isNewer = timeDelta > 0;

       // If it's been more than two minutes since the current location, use the new location
       // because the user has likely moved.
       if (isSignificantlyNewer) {
           return newLocation;
       // If the new location is more than two minutes older, it must be worse
       } else if (isSignificantlyOlder) {
           return currentBestLocation;
       }

       // Check whether the new location fix is more or less accurate
       int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
       boolean isLessAccurate = accuracyDelta > 0;
       boolean isMoreAccurate = accuracyDelta < 0;
       boolean isSignificantlyLessAccurate = accuracyDelta > 200;

       // Check if the old and new location are from the same provider
       boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
               currentBestLocation.getProvider());

       // Determine location quality using a combination of timeliness and accuracy
       if (isMoreAccurate) {
           return newLocation;
       } else if (isNewer && !isLessAccurate) {
           return newLocation;
       } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
           return newLocation;
       }
       return currentBestLocation;
   }
   
   /** Checks whether two providers are the same */
   private boolean isSameProvider(String provider1, String provider2) {
       if (provider1 == null) {
         return provider2 == null;
       }
       return provider1.equals(provider2);
   }
}
