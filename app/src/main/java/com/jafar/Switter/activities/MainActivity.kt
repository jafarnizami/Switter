package com.jafar.Switter.activities


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.jafar.Switter.R
import kotlinx.android.synthetic.main.activity_main.*

//mainactivy is the login activity
class MainActivity : AppCompatActivity() {

  private  val firebaseAuth =FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user=firebaseAuth.currentUser?.uid

        user?.let {
            startActivity(HomeActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTextChangeListener(emailET,emailTIL)
        setTextChangeListener(passwordET,passwordTIL)

        loginProgressLayout.setOnTouchListener { v, event -> true }
    }

    fun setTextChangeListener(et:EditText,til:TextInputLayout)  //this function is called when the user changes the text in the edittext
    {
        et.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled=false     //when text is changed iserrorenable becomes false so that the user can add other text
            }

        })
    }

    fun onLogin(v:View)
    {
        var proceed =true
        if (emailET.text.toString().isNullOrEmpty())
        {
            emailTIL.error="Email is Required"
            emailTIL.isErrorEnabled=true
            proceed=false
        }

        if (passwordET.text.toString().isNullOrEmpty())
        {
            passwordTIL.error="Password is Required"
            passwordTIL.isErrorEnabled=true
            proceed=false
        }

        if (proceed)
        {
            loginProgressLayout.visibility=View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(emailET.text.toString(),passwordET.text.toString())
                .addOnCompleteListener {
                    if (!it.isSuccessful)
                    {
                        loginProgressLayout.visibility=View.GONE
                        Toast.makeText(this@MainActivity,"Login Error: ${it.exception?.localizedMessage}",Toast.LENGTH_SHORT).show()
                    }
                }

                .addOnFailureListener {
                    it.printStackTrace()
                    loginProgressLayout.visibility=View.GONE
                }
        }

    }

    fun gotoSignup(v:View)
    {
        startActivity(SignUpActivity.newIntent(this))

    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener (firebaseAuthListener)
        }

    override fun onStop() {
        super.onStop()

        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    companion object
    {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }


    }

