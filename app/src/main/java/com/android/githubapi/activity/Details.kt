package com.android.githubapi.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.githubapi.R

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class Details : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_GithubApi)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        //region variables
        val intent = intent
        val title = intent.getStringExtra(MainActivity.Companion.TITLE)
        val date = intent.getStringExtra(MainActivity.Companion.DATA)
        val date_after = formatDate("yyyy-MM-dd", "dd, MMM yyyy", date)
        val image = intent.getStringExtra(MainActivity.Companion.IMAGE)
        val description = intent.getStringExtra(MainActivity.Companion.DESCRIPTION)
        val author = intent.getStringExtra(MainActivity.Companion.AUTHOR)
        val url = intent.getStringExtra(MainActivity.Companion.URL)
        val mTitle = findViewById<View>(R.id.txttitle) as TextView
        val mDate = findViewById<View>(R.id.txtDate) as TextView
        val mImage = findViewById<View>(R.id.imageView) as ImageView
        val mDescription = findViewById<View>(R.id.txtDescription) as TextView
        val mAuthor = findViewById<View>(R.id.txtAuthor) as TextView
        val mImageGit = findViewById<View>(R.id.imgGit) as ImageView
        val imageUri = Uri.parse(image)
        //endregion

        //region imageView
        DownloadImageFromInternet(findViewById(R.id.imageView)).execute(imageUri.toString())
        mImageGit.setOnClickListener {
            val viewIntent = Intent(VIEW, Uri.parse(url))
            startActivity(viewIntent)
        }
        //endregion

        //region setcomponents
        mTitle.text = title
        mDate.text = date_after
        mImage.setImageURI(imageUri)
        mDescription.text = description
        mAuthor.text = author
        //endregion
    }

    //region format date
    companion object {
        const val VIEW = "android.intent.action.VIEW"
        fun formatDate(inputFormat: String?, outputFormat: String?, inputDate: String?): String {
            var outputDate = ""
            val df_input = SimpleDateFormat(inputFormat, Locale.US)
            val df_output = SimpleDateFormat(outputFormat, Locale.US)
            try {
                val parsed = df_input.parse(inputDate)
                outputDate = df_output.format(parsed)
            } catch (e: ParseException) {
                Log.e("error:", "ParseException - dateFormat")
            }
            return outputDate
        }
    }
    //endregion

    //region download image
    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
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