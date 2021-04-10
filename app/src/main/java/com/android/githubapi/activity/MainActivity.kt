package com.android.githubapi.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.githubapi.R
import com.android.githubapi.adapter.ListAdapter
import com.android.githubapi.interfaces.IGitApi
import com.android.githubapi.models.GithubApi
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnItemSelectedListener {
    //region variables
    private var dialog: ProgressDialog? = null
    private var listView: ListView? = null
    var context: Context? = null

    //endregion
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_GithubApi)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this@MainActivity
        listView = findViewById(R.id.listview)
        findAll()

        val pullToRefresh = findViewById<View>(R.id.pulltorefresh) as SwipeRefreshLayout

        pullToRefresh.setOnRefreshListener {
            findAll() // your code
            pullToRefresh.isRefreshing = false
        }

        onTokenRefresh()


    }

    fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken: String? = FirebaseInstanceId.getInstance().getToken()


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        Log.d("Token",refreshedToken.toString())
    }

    //region listView Data
    private fun findAll() {
        //region variables
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        val retrofit = Retrofit.Builder().baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
        val iGitApi = retrofit.create(IGitApi::class.java)
        val call = iGitApi.findList()
        //endregion

        //region loading...
        dialog = ProgressDialog(this@MainActivity)
        dialog!!.setMessage("Loading...")
        dialog!!.setCancelable(false)
        dialog!!.show()
        //endregion

        //region enqueue
        call.enqueue(object : Callback<List<GithubApi>?> {
            override fun onResponse(
                call: Call<List<GithubApi>?>,
                response: Response<List<GithubApi>?>
            ) {
                if (dialog!!.isShowing) dialog!!.dismiss()
                val list = response.body()
                if (list != null) {
                    val adapter = ListAdapter(this@MainActivity, list)
                    listView!!.adapter = adapter

                    listView!!.onItemClickListener =

                        AdapterView.OnItemClickListener { adapterView, view, i, l ->
                            val intent = Intent(this@MainActivity, Details::class.java)
                            intent.putExtra(TITLE, list[i].title)
                            intent.putExtra(DATA, list[i].createdAt)
                            intent.putExtra(DESCRIPTION, list[i].body)
                            intent.putExtra(IMAGE, list[i].user?.avatarUrl)
                            intent.putExtra(AUTHOR, list[i].user?.login)
                            intent.putExtra(URL, list[i].user?.htmlUrl)
                            startActivity(intent)
                        }

                    listView!!.setOnItemLongClickListener(OnItemLongClickListener { parent, view, i, id ->
                        val dialog = Dialog(this@MainActivity)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setContentView(R.layout.item_menu_image_dialog)

                        val imageUri = Uri.parse(list[i].user?.avatarUrl)

                        DownloadImageFromInternet(dialog.findViewById(R.id.imageDialog)).execute(
                            imageUri.toString()
                        )

                        dialog.show()

                        true
                    })

                }
            }

            override fun onFailure(call: Call<List<GithubApi>?>, t: Throwable) {
                if (dialog!!.isShowing) dialog!!.dismiss()
                Toast.makeText(this@MainActivity, "error:" + t.message, Toast.LENGTH_SHORT).show()
                Log.e("OnFailure", t.message!!)
            }
        })
        //endregion
    }


    //endregion


    //region DownloadImage
    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) :
        AsyncTask<String, Void, Bitmap?>() {
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {}

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        const val TITLE = "package com.android.githubapi.activity.TITLE"
        const val DATA = "package com.android.githubapi.activity.DATA"
        const val IMAGE = "package com.android.githubapi.activity.IMAGE"
        const val DESCRIPTION = "package com.android.githubapi.activity.DESCRIPTION"
        const val AUTHOR = "package com.android.githubapi.activity.AUTHOR"
        const val URL = "package com.android.githubapi.activity.URL"
    }

}

private fun <T> Call<T>.enqueue(callback: Callback<List<GithubApi>?>) {

}

