package com.example.android.pets.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

/**
 * Created by root on 1/25/17.
 */

public final class PetContract {


    public static final String CONTENT_AUTHORITY = "com.example.android.pets";
    public static final Uri BASE_CONTENT = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PETS = "pets";

    private PetContract() {}

    public static class PetEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT, PATH_PETS);

        private PetEntry() {}

        public static final String TABLE_NAME = "pets";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_BREED = "breed";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_WEIGHT = "weight";

        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_FEMALE = 1;
        public static final int GENDER_MALE = 2;


    }
}
