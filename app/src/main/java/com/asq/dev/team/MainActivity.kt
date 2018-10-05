package com.asq.dev.team

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val SIGN_IN_ACTIVITY_RESULT_CODE = 10056
    private lateinit var sharedPref: SharedPreferences
    private lateinit var auth: FirebaseAuth

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = this.getSharedPreferences(getString(R.string.theme_file_key), Context.MODE_PRIVATE)
        val darkTheme = sharedPref.getBoolean(getString(R.string.theme_key), true)

        if (darkTheme)
            setTheme(R.style.AppTheme_Dark)
        else
            setTheme(R.style.AppTheme_Light)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bar)


        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null)
            startSignInActivity()

        viewManager = LinearLayoutManager(this)
        val myDataset = arrayOf("Hello!", "Hello!", "World!", "World!", "Earth!", "Quantum!", "Hello!", "Hello!", "World!", "World!", "Earth!", "Quantum!")

        viewAdapter = MyAdapter(myDataset)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            my_recycler_view.setOnScrollChangeListener { _, _, _, _, _ ->
                header.isSelected = my_recycler_view.canScrollVertically(-1)
            }
        }

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        bar.setNavigationOnClickListener {
            sharedPref.edit()
                    .putBoolean(getString(R.string.theme_key), !darkTheme)
                    .apply()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {

            }
        }
    }

    private fun startSignInActivity() {
        val signInActivity = Intent(this, SignInActivity::class.java)
        startActivityForResult(signInActivity, SIGN_IN_ACTIVITY_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_ACTIVITY_RESULT_CODE) {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                Snackbar.make(main_holder, "Signed in as ${currentUser.displayName}", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }
}

class MyAdapter(private val myDataset: Array<String>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<View>(R.id.textView) as TextView
        val imageView = view.findViewById<View>(R.id.imageView) as ImageView
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_view, parent, false)
        return MyViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = myDataset[position]
        Glide.with(holder.imageView).load("https://cdn4.iconfinder.com/data/icons/user-avatar-flat-icons/512/User_Avatar-04-512.png")
                .apply(RequestOptions().circleCrop())
                .into(holder.imageView)
    }

    override fun getItemCount() = myDataset.size
}
