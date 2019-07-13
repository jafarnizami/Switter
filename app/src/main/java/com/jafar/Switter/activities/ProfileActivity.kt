package com.jafar.Switter.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.common.io.Files.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jafar.Switter.R
import com.jafar.Switter.util.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val firebaseAuth=FirebaseAuth.getInstance()
    private val firebaseDB =FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private var imageUrl:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (userId==null)
        {
            finish()
        }
        profileProgressLayout.setOnTouchListener { v, event ->true  }

        populateInfo() //RETRIEVNG INFO FROM OUR DATABASE AND DISPLAYING

        photoIV.setOnClickListener {
            val intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK && requestCode== REQUEST_CODE_PHOTO)
        {
            storeImage(data?.data)
        }
    }
    fun storeImage(imageUri:Uri?)
    {

        imageUri?.let {
            Toast.makeText(this,"Uploading.....",Toast.LENGTH_SHORT).show()
            profileProgressLayout.visibility=View.VISIBLE
            val filepath =firebaseStorage.child(DATA_IMAGES).child(userId!!)//UPLOADING THE IMAGE IN THE FIREBASE STORAGE
            filepath.putFile(imageUri)
                .addOnSuccessListener {
                    filepath.downloadUrl  //DOWNLOADING THE UPLOADED IMAGE SO WE CAN UPDATE IN OUR FIRESTORE
                        .addOnSuccessListener {

                            val url =it.toString()  //it contains the image url that has been downloaded
                            firebaseDB.collection(DATA_USERS).document(userId).update(DATA_USER_IMAGE_URL,url)
                                .addOnSuccessListener {
                                    imageUrl=url
                                    photoIV.loadUrl(imageUrl,R.drawable.logo)
                                }
                            profileProgressLayout.visibility=View.GONE

                        }
                        .addOnFailureListener {
                            Toast.makeText(this@ProfileActivity,"Image uplaod failed please try again",Toast.LENGTH_SHORT).show()
                            profileProgressLayout.visibility=View.GONE
                        }
                }
                .addOnFailureListener{
                    Toast.makeText(this@ProfileActivity,"Image uplaod failed please try again",Toast.LENGTH_SHORT).show()
                    profileProgressLayout.visibility=View.GONE
                }
        }

    }

    fun populateInfo()
    {
        ///////////////////////////////////////////////

        profileProgressLayout.visibility=View.VISIBLE
        firebaseDB.collection(DATA_USERS).document(userId!!).get()   //fetching data from the firestore
            .addOnSuccessListener {
                val user =it.toObject(User::class.java)

                usernameET3.setText(user?.username,TextView.BufferType.EDITABLE)
                emailET3.setText(user?.email,TextView.BufferType.EDITABLE)
                imageUrl=user?.imageUrl
                imageUrl?.let {
                    photoIV.loadUrl(user?.imageUrl,R.drawable.logo)
                }

                profileProgressLayout.visibility=View.GONE
            }
            .addOnFailureListener{
                it.printStackTrace()
                finish()
            }

    }

    companion object{

        fun newIntent(context: Context)= Intent(context, ProfileActivity::class.java)
    }

    fun onSignout(v:View)
    {
        firebaseAuth.signOut()
        startActivity(MainActivity.newIntent(this))
        finish()
    }
    fun onApply(v:View)
    {
        profileProgressLayout.visibility=View.VISIBLE

        //process of updating the  data in the firestore, there is already some data we are gonna update it....
        val username= usernameET3.text.toString()
        val email =emailET3.text.toString()
        val map =HashMap<String,Any>()
        map[DATA_USER_USERNAME] = username
        map[DATA_USER_EMAIL]=email

        firebaseDB.collection(DATA_USERS).document(userId!!).update(map)
            .addOnSuccessListener {
                Toast.makeText(this@ProfileActivity,"UPDATE SUCCESSFULL",Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                it.printStackTrace()

                Toast.makeText(this@ProfileActivity,"UPDATE FAILED",Toast.LENGTH_SHORT).show()
                profileProgressLayout.visibility=View.GONE


            }
    }
}
