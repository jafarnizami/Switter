package com.jafar.Switter.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.WorkSource
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jafar.Switter.R
import com.jafar.Switter.util.*
import kotlinx.android.synthetic.main.activity_tweet.*

class TweetActivity : AppCompatActivity() {

    private val firebaseDB =FirebaseFirestore.getInstance()
    private val firebaseStorage=FirebaseStorage.getInstance().reference
    private var imageUrl:String?=null
    private var userId:String?=null
    private var userName:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tweet)

        if (intent.hasExtra(PARAM_USER_ID) && intent.hasExtra(PARAM_USERNAME))
        {
            userId=intent.getStringExtra(PARAM_USER_ID) //storing the values fetched from the home activity into these variables...
            userName=intent.getStringExtra(PARAM_USERNAME)
        }
        else
        {
            Toast.makeText(this,"Error Creating Tweet",Toast.LENGTH_SHORT).show()
            finish()
        }
        tweetProgressLayout.setOnTouchListener { v, event ->true  }
    }

    companion object{

        val PARAM_USER_ID ="userId"
        val PARAM_USERNAME="userName"

        //this function is getting user name and useid from the home activity
        fun newIntent(context: Context,userId:String?,userName:String?):Intent
        {
           val intent= Intent(context, TweetActivity::class.java)
            intent.putExtra(PARAM_USER_ID,userId)
            intent.putExtra(PARAM_USERNAME,userName)
            return intent

        }

    }
    fun addImage(v:View)
    {
        val intent=Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        startActivityForResult(intent, REQUEST_CODE_PHOTO)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK && requestCode== REQUEST_CODE_PHOTO)
        {
            storeImage(data?.data)
        }
    }

    fun storeImage(imageUri: Uri?)
    {

        imageUri?.let {
            Toast.makeText(this,"Uploading.....",Toast.LENGTH_SHORT).show()
            tweetProgressLayout.visibility=View.VISIBLE
            val filepath =firebaseStorage.child(DATA_IMAGES).child(userId!!)//UPLOADING THE IMAGE IN THE FIREBASE STORAGE
            filepath.putFile(imageUri)
                .addOnSuccessListener {
                    filepath.downloadUrl  //DOWNLOADING THE UPLOADED IMAGE SO WE CAN UPDATE IN OUR FIRESTORE
                        .addOnSuccessListener {
                            imageUrl=it.toString()
                            tweetImage.loadUrl(imageUrl,R.drawable.logo)
                            tweetProgressLayout.visibility=View.GONE

                        }
                        .addOnFailureListener {
                            Toast.makeText(this@TweetActivity,"Image uplaod failed please try again",Toast.LENGTH_SHORT).show()
                            tweetProgressLayout.visibility=View.GONE
                        }
                }
                .addOnFailureListener{
                    Toast.makeText(this@TweetActivity,"Image uplaod failed please try again",Toast.LENGTH_SHORT).show()
                    tweetProgressLayout.visibility=View.GONE
                }
        }

    }
    fun postTweet(v:View)
    {
        tweetProgressLayout.visibility=View.VISIBLE
        val text = tweetText.text.toString()
        val hashtags = getHashtags(text) //this function will pull out the hashtags from the text

        val tweetId =firebaseDB.collection(DATA_TWEETS).document()  // in this document we are gonna store our tweets and all the data regarding it.
        val tweet = Tweet(tweetId.id, arrayListOf(userId!!),userName,text,imageUrl,System.currentTimeMillis(),hashtags, arrayListOf())
        tweetId.set(tweet) //storing data into our firestore
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener {
                it.printStackTrace()
                tweetProgressLayout.visibility=View.GONE
                Toast.makeText(this,"Failed to post yout tweet",Toast.LENGTH_SHORT).show()
            }

    }
    fun getHashtags(source: String): ArrayList<String> {
        val hashtags = arrayListOf<String>()
        var text = source

        while (text.contains("#")) {
            var hashtag = ""
            val hash = text.indexOf("#")
            text = text.substring(hash + 1)

            val firstSpace = text.indexOf(" ")
            val firstHash = text.indexOf("#")

            if(firstSpace == -1 && firstHash == -1) {
                hashtag = text.substring(0)
            } else if (firstSpace != -1 && firstSpace < firstHash) {
                hashtag = text.substring(0, firstSpace)
                text = text.substring(firstSpace + 1)
            } else {
                hashtag = text.substring(0, firstHash)
                text = text.substring(firstHash)
            }

            if(!hashtag.isNullOrEmpty()) {
                hashtags.add(hashtag)
            }
        }

        return hashtags
    }

}
