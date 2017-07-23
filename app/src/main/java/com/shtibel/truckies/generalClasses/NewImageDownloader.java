package com.shtibel.truckies.generalClasses;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Shtibel on 28/07/2015.
 */
public class NewImageDownloader extends BaseImageDownloader {
    public NewImageDownloader(Context context) {
        super(context);
    }

    @Override
    protected InputStream getStreamFromContent(String imageUri, Object extra) throws FileNotFoundException {
        ContentResolver res = context.getContentResolver();
        Uri uri = Uri.parse(imageUri);
        if (imageUri.startsWith("content://com.android.contacts/")) {
            return ContactsContract.Contacts.openContactPhotoInputStream(res, uri);
        } else {
            return res.openInputStream(uri);
        }
    }
}
