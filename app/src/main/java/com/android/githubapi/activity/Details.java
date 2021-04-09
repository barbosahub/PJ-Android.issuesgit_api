package com.android.githubapi.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.githubapi.R;
import com.android.githubapi.adapter.ListAdapter;

public class Details extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        final String title = intent.getStringExtra(MainActivity.TITLE);
        final String date = intent.getStringExtra(MainActivity.DATA);
        final String image = intent.getStringExtra(MainActivity.IMAGE);
        final String description = intent.getStringExtra(MainActivity.DESCRIPTION);

        TextView mTitle = (TextView) findViewById(R.id.txttitle);
        TextView mDate = (TextView) findViewById(R.id.txtDate);
        ImageView mImage = (ImageView) findViewById(R.id.imageView);
        TextView mDescription = (TextView) findViewById(R.id.txtDescription);

        Uri imageUri = Uri.parse(image);
        new ListAdapter.DownloadImageTask(mImage).execute(image);

        mTitle.setText(title);
        mDate.setText(date);
        mImage.setImageURI(imageUri);
        mDescription.setText(description);
    }
}