package com.example.test_lab_ad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button viewAllButton = (Button) findViewById(R.id.viewAllButton);
        final TextView allNames = (TextView) findViewById(R.id.allNames);

        final EditText nameInput = (EditText) findViewById(R.id.nameInput);
        final TextView textView = (TextView) findViewById(R.id.textView);
        final Button button = (Button) findViewById(R.id.button);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://androidapi-1326.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final HerokuService service = retrofit.create(HerokuService.class);

        viewAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<Name>> createCall = service.all();
                createCall.enqueue(new Callback<List<Name>>() {
                    @Override
                    public void onResponse(Call<List<Name>> _, Response<List<Name>> resp) {
                        allNames.setText("ALL Names:\n");
                        for (Name b : resp.body()) {
                            allNames.append(b.name + "\n");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Name>> _, Throwable t) {
                        t.printStackTrace();
                        allNames.setText(t.getMessage());
                    }
                });
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                Name name = new Name(random.nextInt(100),nameInput.getText().toString());
                Call<Name> createCall = service.create(name);
                createCall.enqueue(new Callback<Name>() {
                    @Override
                    public void onResponse(Call<Name> _, Response<Name> resp) {
                        Name newName = resp.body();
                        textView.setText("Created Name with ISBN: " + nameInput.getText().toString());
                    }

                    @Override
                    public void onFailure(Call<Name> _, Throwable t) {
                        t.printStackTrace();
                        textView.setText(t.getMessage());
                    }
                });
            }
        });
    }
}