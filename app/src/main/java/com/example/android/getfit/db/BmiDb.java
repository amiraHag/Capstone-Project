package com.example.android.getfit.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.android.getfit.R;

// Room database class
@Database(entities = {Bmi.class}, version = 1, exportSchema = false)
public abstract class BmiDb extends RoomDatabase {

    private static BmiDb mInstanceBmiDatabase;
    public abstract BmiDao bmiDao();

    public static BmiDb getInstance(Context context) {

        if (mInstanceBmiDatabase== null){
            mInstanceBmiDatabase = buildDatabaseInstance(context.getApplicationContext());
        }
        return mInstanceBmiDatabase;
    }


    private static BmiDb buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,  BmiDb.class,
                String.valueOf(R.string.bmi_room_db))
                .allowMainThreadQueries()
                .build();
    }

    public static void cleanUp() {
        mInstanceBmiDatabase = null;
    }




}
