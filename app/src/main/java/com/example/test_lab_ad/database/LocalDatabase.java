package com.example.test_lab_ad.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.test_lab_ad.dao.DataModelDAO;
import com.example.test_lab_ad.entity.DataModel;

@Database(entities = {DataModel.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

    /**
     * 이 객체가 제공하는 Data Access Object
     * AppDatabase 가 생성되고 TodoDAO 를 통해서 조작한다.
     **/
    public abstract DataModelDAO dataModelDAO();

}