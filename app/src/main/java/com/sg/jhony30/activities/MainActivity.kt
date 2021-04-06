package com.sg.jhony30.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.sg.jhony30.*
import com.sg.jhony30.adapter.ThoghtsAdapter
import com.sg.jhony30.databinding.ActivityMainBinding
import com.sg.jhony30.model.Thought

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var selectedCtegory = FUNNY
    lateinit var thoughtAtapter: ThoghtsAdapter
    val thoughts = arrayListOf<Thought>()
    val thoughtCollectionRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
    lateinit var thoghtListner: ListenerRegistration
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.fab.setOnClickListener { view ->
            val intent = Intent(this, AddThoughtActivity::class.java)
            startActivity(intent)
        }

        thoughtAtapter = ThoghtsAdapter(thoughts){thought ->
            var commentActivity= Intent(this,CommentsActivity::class.java)
            commentActivity.putExtra(DOCUMENT_KEY,thought.documentId)
            startActivity(commentActivity)
        }
        binding.thoughtListView.adapter = thoughtAtapter
        val layoutManager = LinearLayoutManager(this)
        binding.thoughtListView.layoutManager = layoutManager
        auth = FirebaseAuth.getInstance()


        //  setListener()
        val loginInntent= Intent(this,LoginActivity::class.java)
        startActivity(loginInntent)

    }

    override fun onRestart() {
        super.onRestart()
        updateUi()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val menuItem = menu?.getItem(0)
        if (auth.currentUser == null) {
            //logout
            menuItem.title = "Login"
        } else {
            //login
            menuItem.title = "Logout"
        }
        return super.onPrepareOptionsMenu(menu)
    }

    fun updateUi() {
        if (auth.currentUser == null) {
            binding.mainCrazyBtn.isEnabled = false
            binding.mainPopularBtn.isEnabled = false
            binding.mainFunnyBtn.isEnabled = false
            binding.mainSeriousBtn.isEnabled = false
            binding.fab.isEnabled = false
            thoughts.clear()
            thoughtAtapter.notifyDataSetChanged()
        } else {
            binding.mainCrazyBtn.isEnabled = true
            binding.mainPopularBtn.isEnabled = true
            binding.mainFunnyBtn.isEnabled = true
            binding.mainSeriousBtn.isEnabled = true
            binding.fab.isEnabled = true
            setListener()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_login) {
            if (auth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            } else {
                auth.signOut()
                updateUi()
            }
            return true
        }
        return false
    }

    fun setListener() {
        if (selectedCtegory == POPULAR) {
            thoghtListner = thoughtCollectionRef
                .orderBy(NUM_LIKES, Query.Direction.DESCENDING)
                .addSnapshotListener(this) { snapshot, exception ->
                    if (exception != null) {
                        Log.e("Exception", "*** could not retrive documents : $exception")
                    }
                    if (snapshot != null) {
                        parseData(snapshot)
                    }
                }
        } else {
            thoghtListner = thoughtCollectionRef
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .whereEqualTo(CATEGORY, selectedCtegory)
                .addSnapshotListener(this) { snapshot, exception ->

                    if (exception != null) {
                        Log.e("Exception", "*** could not retrive documents : $exception")
                    }
                    if (snapshot != null) {
                        parseData(snapshot)
                    }
                }
        }
    }

    fun parseData(snapeshot: QuerySnapshot) {
        thoughts.clear()
        for (document in snapeshot.documents) {
            val data = document.data
            if (data != null) {

                val name = data[USERNAME] as String
                val timestamp = data[TIMESTAMP] as Timestamp
                var thoghtTxt = "ff"
                if (data[THOUGHT_TXT] != null) {
                    thoghtTxt = data[THOUGHT_TXT] as String
                }
                val numLikes = data[NUM_LIKES] as Long
                Log.i("message", "numLikes=$numLikes")
                val numComments = data[NUM_COMMENTS] as Long
                val documentId = document.id
                val newThought = Thought(
                    name, timestamp, thoghtTxt, numLikes.toInt(),
                    numComments.toInt(), documentId
                )
                thoughts.add(newThought)
            }

        }
        thoughtAtapter.notifyDataSetChanged()

    }

    fun mainSeriousClick(view: View) {    //its toggle button every press toggle valuep
        if (selectedCtegory == SERIOUS) {
            binding.mainSeriousBtn.isChecked = true
            return
        }
        binding.mainFunnyBtn.isChecked = false
        binding.mainCrazyBtn.isChecked = false
        binding.mainPopularBtn.isChecked = false
        selectedCtegory = SERIOUS
        thoghtListner.remove()
        setListener()
    }

    fun mainFunnyClick(view: View) {
        if (selectedCtegory == FUNNY) {
            binding.mainFunnyBtn.isChecked = true
            return
        }
        binding.mainSeriousBtn.isChecked = false
        binding.mainCrazyBtn.isChecked = false
        binding.mainPopularBtn.isChecked = false
        selectedCtegory = FUNNY
        thoghtListner.remove()
        setListener()
    }

    fun mainCrazyClick(view: View) {
        if (selectedCtegory == CRAZY) {
            binding.mainCrazyBtn.isChecked = true
            return
        }
        binding.mainSeriousBtn.isChecked = false
        binding.mainFunnyBtn.isChecked = false
        binding.mainPopularBtn.isChecked = false
        selectedCtegory = CRAZY
        thoghtListner.remove()
        setListener()
    }

    fun mainPopularClick(view: View) {
        if (selectedCtegory == POPULAR) {
            binding.mainPopularBtn.isChecked = true
            return
        }
        binding.mainSeriousBtn.isChecked = false
        binding.mainFunnyBtn.isChecked = false
        binding.mainCrazyBtn.isChecked = false
        selectedCtegory = POPULAR
        thoghtListner.remove()
        setListener()

    }

}