package com.example.test_lab_ad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.test_lab_ad.dao.DataModelDAO;
import com.example.test_lab_ad.database.LocalDatabase;
import com.example.test_lab_ad.entity.DataModel;

public class MainActivity extends AppCompatActivity {

    //UI View
    private EditText mAddEdit, mUpdateIdEdit, mUpdateTitleEdit, mDeleteEdit;
    private TextView mResultTextView;
    private Button mAddButton, mUpdateButton, mDeleteButton, mClearButton;

    // Room Database
    private LocalDatabase db;

    private final String INSERT = "INSERT";
    private final String UPDATE = "UPDATE";
    private final String DELETE = "DELETE";
    private final String CLEAR = "CLEAR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddEdit = (EditText) findViewById(R.id.add_edit);
        mUpdateIdEdit = (EditText) findViewById(R.id.update_id_edit);
        mUpdateTitleEdit = (EditText) findViewById(R.id.update_title_edit);
        mDeleteEdit = (EditText) findViewById(R.id.delete_edit);

        mResultTextView = (TextView) findViewById(R.id.result_text);
        mAddButton = (Button) findViewById(R.id.add_button);
        mUpdateButton = (Button) findViewById(R.id.update_button);
        mDeleteButton = (Button) findViewById(R.id.delete_button);
        mClearButton = (Button) findViewById(R.id.clear_button);

        db = Room.databaseBuilder(this, LocalDatabase.class, "test-db")
                .build();

        db.dataModelDAO().getAll().observe(this, dataList -> {
            mResultTextView.setText(dataList.toString());
        });

        mAddButton.setOnClickListener(v -> {
            /**
             *  AsyncTask 생성자에 execute 로 DataModelDAO 객체를 던저준다.
             *  비동기 처리
             **/
            new DaoAsyncTask(db.dataModelDAO(),INSERT,0,"")
                    .execute(new DataModel(mAddEdit.getText().toString()));
        });

        mUpdateButton.setOnClickListener(v ->
                new DaoAsyncTask(
                        db.dataModelDAO(),
                        UPDATE,
                        Integer.parseInt(mUpdateIdEdit.getText().toString()),
                        mUpdateTitleEdit.getText().toString()
                ).execute()
        );

        mDeleteButton.setOnClickListener(v ->
                new DaoAsyncTask(
                        db.dataModelDAO(),
                        DELETE,
                        Integer.parseInt(mDeleteEdit.getText().toString()),
                        ""
                ).execute()
        );

        mClearButton.setOnClickListener(v ->
                new DaoAsyncTask(db.dataModelDAO(),CLEAR,0,"").execute()
        );
    }

    private static class DaoAsyncTask extends AsyncTask<DataModel,Void,Void> {
        private DataModelDAO mDataModelDAO;
        private String mType;
        private int mId;
        private String mTitle;

        private DaoAsyncTask (DataModelDAO dataModelDAO, String type, int id, String title) {
            this.mDataModelDAO = dataModelDAO;
            this.mType = type;
            this.mId = id;
            this.mTitle = title;
        }

        @Override
        protected Void doInBackground(DataModel... dataModels) {
            if(mType.equals("INSERT")){
                mDataModelDAO.insert(dataModels[0]);
            }
            else if(mType.equals("UPDATE")){
                if(mDataModelDAO.getData(mId) != null){
                    mDataModelDAO.dataUpdate(mId,mTitle);
                }
            }
            else if(mType.equals("DELETE")){
                if(mDataModelDAO.getData(mId) != null) {
                    mDataModelDAO.delete(mDataModelDAO.getData(mId));
                }
            }
            else if(mType.equals("CLEAR")){
                mDataModelDAO.clearAll();
            }
            return null;
        }
    }
}
