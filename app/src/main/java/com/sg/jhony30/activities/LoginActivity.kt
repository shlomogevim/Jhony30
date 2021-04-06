package com.sg.jhony30.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.sg.jhony30.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth= FirebaseAuth.getInstance()
    }

    fun loginLoginClicked(view: View) {
        val email=binding.loginEmailTxt.text.toString()
        val password=binding.loginPasswordTxt.text.toString()
        Log.i("aaa","inside loginActivity1 email=$email,password=$password")
        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                Log.i("aaa","inside loginActivity2 email=$email,password=$password")

                finish()
            }
            .addOnFailureListener { exception->
                Log.e("aaa","Could not sign in usert:$exception")


            }


    }
    fun loginCreateUserClicked(view: View) {
        val createIntent= Intent(this, CreateUserActivity::class.java)
        startActivity(createIntent)
    }

}