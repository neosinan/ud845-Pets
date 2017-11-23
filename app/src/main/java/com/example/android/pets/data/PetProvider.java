package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.android.pets.R;

import static com.example.android.pets.data.PetDbHelper.*;

/**
 * Created by cspr on 30.10.2017.
 */

public class PetProvider extends ContentProvider {

    //info for logcat
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    //ContentProviderdan dönecek constant int leri tanımla
    private static final int PETS = 100;
    private static final int PET_ID = 101;

    //Uri matcherda olası durumları kurgulama
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS +"/#",PET_ID);
    }

    //declear Database helper object
    PetDbHelper mPetDbHelper;

    @Override
    public boolean onCreate() {
        mPetDbHelper = new PetDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //PetDbHelperin bir sadece okunabilir instanceni oluşturma
        SQLiteDatabase mDataBase= mPetDbHelper.getReadableDatabase();
        Cursor cursor=null;
        int match=sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                cursor = mDataBase.query(PetContract.PetEntry.TABLE_NAME, projection,selection, selectionArgs, null, null, null, sortOrder);
                break;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = mDataBase.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, sortOrder);
                break;
            default:
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues values) {

        SQLiteDatabase database = mPetDbHelper.getWritableDatabase();

        // Check that all the values
        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        String breed = values.getAsString(PetContract.PetEntry.COLUMN_PET_BREED);
        int weight = Integer.valueOf(values.getAsString(PetContract.PetEntry.COLUMN_PET_WEIGHT));
        if (name == null || breed== null || weight<0) {
           Toast.makeText(getContext(), "Invalid data entered",Toast.LENGTH_LONG).show();
            return null;
        }

        // Insert the new pet with the given values
        long id = database.insert(PetContract.PetEntry.TABLE_NAME, null, values);


        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, values, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);

        }
    }
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // TODO: Update the selected pets in the pets database table with the given ContentValues

        // TODO: Return the number of rows that were affected
        return 0;
    }
}
