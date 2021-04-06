package com.sg.jhony30.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.jhony30.DATE_CREATED
import com.sg.jhony30.USERNAME
import com.sg.jhony30.USERS_REF
import com.sg.jhony30.databinding.ActivityCreateUserBinding

class CreateUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateUserBinding

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("aaa","inside CreatUserActivity")

        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
    }

    fun createCreateClicked(view: View) {
        val email = binding.createEmailTxt.text.toString()
        val password = binding.cratePasswordText.text.toString()
        val username = binding.createUsernameTxt.text.toString()
        Log.i("aaa","email=$email ,password=$password ,username=$username")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                Log.e("aaa", "insid1: ${result}")
                val changeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                Log.e("aaa", "insid2: ${result}")
                result.user.updateProfile(changeRequest)
                    .addOnFailureListener { exception ->
                        Log.e( "aaa","could not update display name: ${exception.localizedMessage}"
                        )
                    }
                val data = HashMap<String, Any>()
                data[USERNAME] = username
                data[DATE_CREATED] = FieldValue.serverTimestamp()

                FirebaseFirestore.getInstance().collection(USERS_REF).document(result.user.uid)
                    .set(data)
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        "could not add user document: ${exception.localizedMessage}"
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("exception", "could not create user: ${exception.localizedMessage}")
            }
    }

    fun createCancelClicked(view: View) {
        finish()
    }
}