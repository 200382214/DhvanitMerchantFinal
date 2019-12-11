package com.example.dhvanitmerchantfinal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    val user = FirebaseAuth.getInstance().currentUser
    // connect to firestore
    var db = FirebaseFirestore.getInstance()

    // classes to store and pass query data
    private var adapter: PlaceAdapter? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // inflate the menu_main to add the items to the toolbar
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_signout -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        // redirect to SignInActivity
                        val intent = Intent(applicationContext, SignInActivity::class.java)
                        startActivity(intent)
                    }
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(toolbar)
        //supportActionBar?.title = "Order List"

        //Display the name and email

        user?.let {
            val email = user.email

            textViewEmail.text = email
        }


        // recycler will have linear layout
        recyclerViewPlaces.setLayoutManager(LinearLayoutManager(this))

        // query
        val query = db.collection("places").orderBy("name", Query.Direction.ASCENDING)

        // pass the query results to the recycler adapter for display
        val options = FirestoreRecyclerOptions.Builder<Place>().setQuery(query, Place::class.java).build()
        adapter = PlaceAdapter(options)

        // bind the adapter to the recyclerview (adapter means the datasource)
        recyclerViewPlaces.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        // check for authenticated user
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
        }

        super.onStart()
        adapter!!.startListening()
    }




    // Kotlin equivalent of Java ArrayList class. We decided we don't need this after all
    //private var artistList = mutableListOf<Artist>()

    // stop listening for data changes if the activity gets stopped
    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }

    // inner classes needed to read and bind the data
    private inner class PlaceViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

    }

    private inner class PlaceAdapter internal constructor(options: FirestoreRecyclerOptions<Place>) :
        FirestoreRecyclerAdapter<Place,
                PlaceViewHolder>(options) {

        override fun onBindViewHolder(p0: PlaceViewHolder, p1: Int, p2: Place) {
            // pass current Artist values to the display function above
            //p0.displayArtist(p2.artistName.toString(), p2.artistGenre.toString()) - replaced by 2 lines below
            p0.itemView.findViewById<TextView>(R.id.textViewName).text = p2.name
            p0.itemView.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabLocation).setOnClickListener {
                val intent = Intent(applicationContext, MapsActivity::class.java)
                startActivity(intent)
            }
            p0.itemView.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabUrl).setOnClickListener {
                val url = p2.url.toString()
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
            // when creating, instantiate the item_artist.xml template (only happens once)
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_places, parent, false)
            return PlaceViewHolder(view)
        }
    }

}
