package com.jafar.Switter.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jafar.Switter.adapters.TweetListAdapter
import com.jafar.Switter.listeners.HomeCallback
import com.jafar.Switter.listeners.TweetListener
import com.jafar.Switter.listeners.TwitterListenerImpl
import com.jafar.Switter.util.User

abstract class TwitterFragment :Fragment(){

    protected var tweetsAdapter : TweetListAdapter?=null //can be issue
    protected var currentUser:com.jafar.Switter.util.User?=null
    protected val firebaseDB = FirebaseFirestore.getInstance()
    protected val userId = FirebaseAuth.getInstance().currentUser?.uid
    protected var listener: TwitterListenerImpl?=null
    protected var callback:HomeCallback?=null



    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is HomeCallback) {
            callback = context
        } else {
            throw RuntimeException(context.toString() + " must implement HomeCallback")
        }
    }

    fun setUser(user: User?) {
        this.currentUser = user   //this function tells that the operarations are to be performed on the currentUser (logged in )

        listener?.user=user
    }

    abstract fun updateList()

    override fun onResume() {
        super.onResume()
        updateList()
    }
}