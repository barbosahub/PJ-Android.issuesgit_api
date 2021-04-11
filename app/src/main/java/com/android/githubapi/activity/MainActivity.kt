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
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.githubapi.R
import com.android.githubapi.adapter.ListAdapter
import com.android.githubapi.interfaces.IGitApi
import com.android.githubapi.models.GithubApi
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnItemSelectedListener,
    NavigationView.OnNavigationItemSelectedListener {
    //region variables
    private var listView: ListView? = null
    private var context = this@MainActivity
    //endregion
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_menu_drawer)
        findViewById()
        initDrawer()

        //region navigation view
        val nav_view = findViewById<View>(R.id.nav_view) as NavigationView
        nav_view.setNavigationItemSelectedListener(this)
        //endregion

        //region pulltorefresh
        val pullToRefresh = findViewById<View>(R.id.pulltorefresh) as SwipeRefreshLayout

        pullToRefresh.setOnRefreshListener {
            progressBar(true)
            pullToRefresh.isRefreshing = false
            findAll()
        }
        //endregion

        progressBar(true)
        findAll()
        onTokenRefresh()
    }

    private fun progressBar(isVisible : Boolean){
        val progressBar = findViewById<ProgressBar>(R.id.progressBar) as ProgressBar
        progressBar.isVisible = isVisible
    }

    private fun findViewById(){
        listView = findViewById(R.id.listview)
    }

    //region drawer
    private fun initDrawer(){
        val drawerLayout = findViewById<View>(R.id.drawer_Layout) as DrawerLayout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.Open_Drawer,R.string.Close_Drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle?.drawerArrowDrawable?.color = ContextCompat.getColor(context, R.color.white)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerLayout = findViewById<View>(R.id.drawer_Layout) as DrawerLayout
        when (item.itemId) {
            R.id.nav_api -> {
                val viewIntent = Intent(Details.VIEW, Uri.parse("https://docs.github.com/pt/github/managing-your-work-on-github/creating-an-issue"))
                startActivity(viewIntent)
            }
            R.id.nav_git -> {
                val viewIntent = Intent(Details.VIEW, Uri.parse("https://github.com/barbosahub/PJ-Android.issuesgit_api"))
                startActivity(viewIntent)
            }
            R.id.nav_android -> {
                val viewIntent = Intent(Details.VIEW, Uri.parse("https://developer.android.com/kotlin"))
                startActivity(viewIntent)
            }
            R.id.nav_retrofit -> {
                val viewIntent = Intent(Details.VIEW, Uri.parse("https://square.github.io/retrofit/"))
                startActivity(viewIntent)
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    //endregion

    //region firebase cloud message token
    fun onTokenRefresh() {
        val refreshedToken: String? = FirebaseInstanceId.getInstance().getToken()
        Log.d("Token",refreshedToken.toString())
    }
    //endregion

    //region listView
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

        call.enqueue(object : Callback<List<GithubApi>?> {
            override fun onResponse(
                call: Call<List<GithubApi>?>,
                response: Response<List<GithubApi>?>
            ) {
                val list = response.body()
                if (list != null) {
                    val adapter = ListAdapter(this@MainActivity, list)
                    listView!!.adapter = adapter
                    progressBar(false)
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
                showToast(t.message.toString())
            }
        })

    }
    //endregion

    //region convert image to bitmap
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

    //region toast
    fun showToast(message:String){
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
    //endregion

    //region adapter
    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {}

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    //endregion

    //region objects
    companion object {
        const val TITLE = "package com.android.githubapi.activity.TITLE"
        const val DATA = "package com.android.githubapi.activity.DATA"
        const val IMAGE = "package com.android.githubapi.activity.IMAGE"
        const val DESCRIPTION = "package com.android.githubapi.activity.DESCRIPTION"
        const val AUTHOR = "package com.android.githubapi.activity.AUTHOR"
        const val URL = "package com.android.githubapi.activity.URL"
    }

    //endregion

}


