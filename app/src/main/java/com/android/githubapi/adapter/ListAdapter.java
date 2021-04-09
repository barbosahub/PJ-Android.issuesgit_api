package com.android.githubapi.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.githubapi.R;
import com.android.githubapi.models.GithubApi;
import java.io.InputStream;
import java.util.List;


public class ListAdapter extends ArrayAdapter<GithubApi> {
    private final Context context;
    private final List<GithubApi> elements;

    public ListAdapter(Context context, List<GithubApi> elements) {
        super(context, R.layout.item_menu_layout, elements);
        this.context = context;
        this.elements = elements;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_menu_layout, parent, false);

        ImageView image = (ImageView) rowView.findViewById(R.id.imageView);
        TextView title = (TextView) rowView.findViewById(R.id.txt_title);
        TextView state = (TextView) rowView.findViewById(R.id.txt_state);

        Uri imageUri = Uri.parse(elements.get(position).getUser().getAvatarUrl());
        new DownloadImageTask(image).execute(elements.get(position).getUser().getAvatarUrl());
        int color = (elements.get(position).getState().contains("open")) ?  Color.GREEN  : Color.RED;

        image.setImageURI(imageUri);
        title.setText(elements.get(position).getTitle());
        state.setText(elements.get(position).getState());
        state.setTextColor(color);

        return rowView;
    }

     public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}