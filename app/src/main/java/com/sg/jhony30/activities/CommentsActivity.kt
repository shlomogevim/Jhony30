package com.sg.jhony30.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sg.jhony30.*
import com.sg.jhony30.adapter.CommentsAdapter
import com.sg.jhony30.databinding.ActivityCommentsBinding
import com.sg.jhony30.model.Comment

class CommentsActivity : AppCompatActivity() {
    lateinit var commentsAdapter: CommentsAdapter
    lateinit var thoughtDocmentId: String
    private lateinit var binding: ActivityCommentsBinding
    val comments = arrayListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        thoughtDocmentId = intent.getStringExtra(DOCUMENT_KEY)
        commentsAdapter = CommentsAdapter(comments)
        binding.commentsListview.adapter = commentsAdapter
        val layoutManager = LinearLayoutManager(this)
        binding.commentsListview.layoutManager = layoutManager

        FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocmentId)
            .collection(COMMENTS_REF)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception!=null){
                    Log.i("abc","Cant retrive comments:${exception.localizedMessage}")
                }
                if (snapshot!=null){
                    comments.clear()
                    for (document in snapshot.documents){
                        val data=document.data
                        val name= data?.get(USERNAME) as String
                        val timestamp=data[TIMESTAMP] as Timestamp
                        val commentText=data[COMMENTS_TXT] as String
                        val newComment= Comment(name,timestamp,commentText)
                        comments.add(newComment)
                    }
                    commentsAdapter.notifyDataSetChanged()
                }
            }
    }

    fun addCommentClick(view: View) {

        val commentText = binding.enterCommentText.text.toString()
        Log.i("abc", "enterCommnndTxt=${binding.enterCommentText.text.toString()}")


        val thoughtRef =
            FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocmentId)

        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val thought = transaction.get(thoughtRef)
            val numCommentTxt = thought.getLong(NUM_COMMENTS)?.plus(1)
            transaction.update(thoughtRef, NUM_COMMENTS, numCommentTxt)

            val newCommentRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                .document(thoughtDocmentId).collection(COMMENTS_REF).document()
            val data = HashMap<String, Any>()
            data.put(COMMENTS_TXT, commentText)
            data.put(TIMESTAMP, FieldValue.serverTimestamp())
            data.put(USERNAME, FirebaseAuth.getInstance().currentUser.displayName.toString())
            transaction.set(newCommentRef, data)
        }.addOnSuccessListener {
            binding.enterCommentText.setText("")
            hideKeyboard()
        }
            .addOnFailureListener { exception ->
                Log.i("abc", "could not add comment:${exception.localizedMessage}")
            }
    }
    private fun hideKeyboard(){
        val inptManagar=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inptManagar.isAcceptingText){
            inptManagar.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }

}