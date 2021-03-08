package com.example.test_lab_ad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.test_lab_ad.database.LocalDatabase;

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
        
    }
}