package com.android.githubapi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.android.githubapi.R
import com.android.githubapi.models.GithubApi
import java.net.URL

class ListAdapter(context: Context, private val elements: List<GithubApi>) : ArrayAdapter<GithubApi?>(context!!, R.layout.item_menu_layout, elements) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        //region variables
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.item_menu_layout, parent, false)
        val title = rowView.findViewById<View>(R.id.txt_title) as TextView
        val state = rowView.findViewById<View>(R.id.txt_state) as TextView
        val imageUri = Uri.parse(elements[position].user?.avatarUrl)
        DownloadImageFromInternet(rowView.findViewById(R.id.imageView)).execute(imageUri.toString())
        val color = if (elements[position].state?.contains("open")!!) Color.GREEN else Color.RED
        //endregion

        //region setComponents
        title.text = elements[position].title
        state.text = elements[position].state
        state.setTextColor(color)
        //endregion

        return rowView
    }

    //region DownloadImage
    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }
    //endregion
}


