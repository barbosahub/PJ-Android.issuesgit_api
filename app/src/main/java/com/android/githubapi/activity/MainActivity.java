package com.android.githubapi.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.githubapi.R;
import com.android.githubapi.adapter.ListAdapter;
import com.android.githubapi.interfaces.IGitApi;
import com.android.githubapi.models.GithubApi;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ProgressDialog dialog;
    private ListView listView;
    Context context;

    public final static String TITLE = "package com.android.githubapi.activity.TITLE";
    public final static String DATA = "package com.android.githubapi.activity.DATA";
    public final static String IMAGE = "package com.android.githubapi.activity.IMAGE";
    public final static String DESCRIPTION = "package com.android.githubapi.activity.DESCRIPTION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        listView = findViewById(R.id.listview);

        findAll();
    }


    private void findAll() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();

        IGitApi iGitApi = retrofit.create(IGitApi.class);

        final Call<List<GithubApi>> call = iGitApi.findList();
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        call.enqueue(new Callback<List<GithubApi>>() {
            @Override
            public void onResponse(Call<List<GithubApi>> call, Response<List<GithubApi>> response) {
                if (dialog.isShowing()) dialog.dismiss();

                List<GithubApi> list = response.body();
                if (list != null) {
                    ListAdapter adapter = new ListAdapter(context, list);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Intent intent = new Intent(MainActivity.this, Details.class);
                            intent.putExtra(TITLE, list.get(i).getTitle());
                            intent.putExtra(DATA, list.get(i).getCreatedAt());
                            intent.putExtra(DESCRIPTION, list.get(i).getBody());
                            intent.putExtra(IMAGE, list.get(i).getUser().getAvatarUrl());
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<GithubApi>> call, Throwable t) {
                if (dialog.isShowing()) dialog.dismiss();
                Toast.makeText(MainActivity.this, "error:" + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OnFailure", t.getMessage());

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}



