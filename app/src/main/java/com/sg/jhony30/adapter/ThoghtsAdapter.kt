package com.sg.jhony30.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.jhony30.NUM_LIKES
import com.sg.jhony30.R
import com.sg.jhony30.THOUGHTS_REF
import com.sg.jhony30.model.Thought


class ThoghtsAdapter(val thoughts: ArrayList<Thought>, val itemClick: (Thought) -> Unit) :
    RecyclerView.Adapter<ThoghtsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.thought_list_view, parent, false)
        return ViewHolder(view, itemClick)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindThuoght(thoughts[position])
    }

    override fun getItemCount() = thoughts.count()


    inner class ViewHolder(itemView: View?, val itemClick: (Thought) -> Unit) :
        RecyclerView.ViewHolder(itemView!!) {
        val username = itemView?.findViewById<TextView>(R.id.listViewUsername)
        val timestap = itemView?.findViewById<TextView>(R.id.listViewTimestamp)
        val thuoghtsText = itemView?.findViewById<TextView>(R.id.listViewToughtTxt)
        val numLikes = itemView?.findViewById<TextView>(R.id.listViewNumLikes)
        val likesImage = itemView?.findViewById<ImageView>(R.id.listViewLikesImage)
        val numComments = itemView?.findViewById<TextView>(R.id.numCommentsLabel)



        fun bindThuoght(thought: Thought) {
            username?.text = thought.userName
            thuoghtsText?.text = thought.thoughtTxt
            numLikes?.text = thought.numLikes.toString()
            numComments?.text=thought.numComments.toString()
            timestap?.text = thought.timestamp?.toDate().toString()
            itemView.setOnClickListener { itemClick(thought) }
            likesImage?.setOnClickListener {
                FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                    .document(thought.documentId)
                    .update(NUM_LIKES, thought.numLikes + 1)
            }
        }
    }
}