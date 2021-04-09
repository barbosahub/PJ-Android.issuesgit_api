package com.android.githubapi.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.githubapi.R;
import com.android.githubapi.adapter.ListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Details extends AppCompatActivity {
    public final static String VIEW = "android.intent.action.VIEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        final String title = intent.getStringExtra(MainActivity.TITLE);
        final String date = intent.getStringExtra(MainActivity.DATA);
        final String date_after = formatDate("yyyy-MM-dd", "dd, MMM yyyy", date);
        final String image = intent.getStringExtra(MainActivity.IMAGE);
        final String description = intent.getStringExtra(MainActivity.DESCRIPTION);
        final String author = intent.getStringExtra(MainActivity.AUTHOR);
        final String url = intent.getStringExtra(MainActivity.URL);

        TextView mTitle = (TextView) findViewById(R.id.txttitle);
        TextView mDate = (TextView) findViewById(R.id.txtDate);
        ImageView mImage = (ImageView) findViewById(R.id.imageView);
        TextView mDescription = (TextView) findViewById(R.id.txtDescription);
        TextView mAuthor = (TextView) findViewById(R.id.txtAuthor);
        ImageView mImageGit = (ImageView) findViewById(R.id.imgGit);

        Uri imageUri = Uri.parse(image);
        new ListAdapter.DownloadImageTask(mImage).execute(image);

        mImageGit.setOnClickListener(v -> {
            Intent viewIntent = new Intent(VIEW, Uri.parse(url));
            startActivity(viewIntent);
        });

        mTitle.setText(title);
        mDate.setText(date_after);
        mImage.setImageURI(imageUri);
        mDescription.setText(description);
        mAuthor.setText(author);
    }

    public static String formatDate(String inputFormat, String outputFormat, String inputDate) {

        String outputDate = "";
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.US);
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.US);

        try {
            Date parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e("error:", "ParseException - dateFormat");
        }
        return outputDate;
    }
}