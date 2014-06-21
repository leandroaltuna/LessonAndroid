package example.sixthlesson;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import example.sixthlesson.utils.AlbumStorageDirFactory;
import example.sixthlesson.utils.BitmapManager;


public class MyActivity extends ActionBarActivity {

    private static final int TAKE_PICTURE = 1;
    private static final int SELECT_PICTURE = 2;
    private static final String ALBUM_NAME = "Mejorandroid";
    private static final String BITMAP_STORAGE_KEY = "viewbitmap";

    private String currentPhotoPath;

    private ImageView pictureTaken;

    private Bitmap mImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mImageBitmap = null;
        pictureTaken = (ImageView) findViewById(R.id.picture_taken);
    }

    private void addPictureToGallery()
    {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File file = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(file);

        intent.setData(contentUri);
        this.sendBroadcast(intent);
    }

    public void takePictureFromCamera(View v)
    {
        if (isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE))
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = null;

            try
            {
                file = AlbumStorageDirFactory.setUpPhotoFile(ALBUM_NAME);
                currentPhotoPath = file.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            }
            catch (IOException e)
            {
                e.printStackTrace();
                file = null;
                currentPhotoPath = null;
            }

            startActivityForResult(intent, TAKE_PICTURE);
        }
    }

    public void takePictureFromGallery(View v)
    {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case TAKE_PICTURE:
                {
                    if (currentPhotoPath != null)
                    {
                        mImageBitmap = BitmapManager.setPic(pictureTaken, currentPhotoPath);
                        pictureTaken.setImageBitmap(mImageBitmap);
                        addPictureToGallery();
                        currentPhotoPath = null;
                    }
                    break;
                }
                case SELECT_PICTURE:
                {
                    Uri selectedImageUri = data.getData();

                    currentPhotoPath = AlbumStorageDirFactory.getImageFromGalleryPath(this, selectedImageUri);
                    pictureTaken.setImageURI(selectedImageUri);

                    break;
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action The Intent action to check for availability.
     *
     * @return True if an Intent with the specified action can be sent and
     *         responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action)
    {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);

        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
