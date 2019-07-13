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
import com.google.firebase.firestore.FirebaseFirestore
import com.jafar.Switter.R
import com.jafar.Switter.util.DATA_USERS
import com.jafar.Switter.util.User
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val firebaseDB =FirebaseFirestore.getInstance() //WE CAN STORE OBJECTS USING FIRESTORE DATABASE AND RETRIEVE THOSE OBJECTS FOR OUR APP
    private  val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user=firebaseAuth.currentUser?.uid

        user?.let {
            startActivity(HomeActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        setTextChangeListener(usernameET2,usernameTIL2)
        setTextChangeListener(emailET2,emailTIL2)
        setTextChangeListener(passwordET2,passwordTIL2)

        loginProgressLayout2.setOnTouchListener { v, event -> true }
    }

    fun setTextChangeListener(et: EditText, til: TextInputLayout)
    {
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled=false
            }

        })
    }

    companion object{

        fun newIntent(context: Context)= Intent(context, SignUpActivity::class.java)
    }

    fun OnSignup(v:View)
    {
        var proceed =true
        if (usernameET2.text.toString().isNullOrEmpty())
        {
            usernameTIL2.error="UserName is Required"
            usernameTIL2.isErrorEnabled=true
            proceed=false
        }
        if (emailET2.text.toString().isNullOrEmpty())
        {
            emailTIL2.error="Email is Required"
            emailTIL2.isErrorEnabled=true
            proceed=false
        }

        if (passwordET2.text.toString().isNullOrEmpty())
        {
            passwordTIL2.error="Password is Required"
            passwordTIL2.isErrorEnabled=true
            proceed=false
        }

        if (proceed)
        {
            loginProgressLayout2.visibility=View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(emailET2.text.toString(),passwordET2.text.toString())
                .addOnCompleteListener {
                    if (!it.isSuccessful)
                    {
                        Toast.makeText(this@SignUpActivity,"SignUp Error: ${it.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        val email = emailET2.text.toString()
                        val name =usernameET2.text.toString()
                        val user =User(email,name,"", arrayListOf(), arrayListOf())
                        firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).set(user)

                    }
                    loginProgressLayout2.visibility=View.GONE

                }

                .addOnFailureListener {
                    it.printStackTrace()
                    loginProgressLayout2.visibility=View.GONE
                }
        }

    }
    fun gotoLogin(v:View)
    {
        startActivity(MainActivity.newIntent(this))
        finish()

    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener (firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()

        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

}
