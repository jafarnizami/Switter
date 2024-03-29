package com.jafar.Switter.listeners

import android.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jafar.Switter.util.*

class TwitterListenerImpl(val tweetList: RecyclerView, var user: User?, val callback: HomeCallback?) :
    TweetListener { //fetching the full data of the recycler view

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    override fun onLayoutClick(tweet: Tweet?) {

        tweet?.let {
            val owner = tweet.userIds?.get(0)

            if (owner != userId) {
                if (user?.followUsers?.contains(owner) == true) {
                    AlertDialog.Builder(tweetList.context)
                        .setTitle("Unfollow ${tweet.userName}")
                        .setPositiveButton("Yes") { dialog, which ->
                            tweetList.isClickable = false
                            var followedUsers = user?.followUsers
                            if (followedUsers==null)
                            {
                                followedUsers= arrayListOf()
                            }
                            followedUsers?.remove(owner)
                            firebaseDB.collection(DATA_USERS).document(userId!!).update(DATA_USER_FOLLOW, followedUsers)
                                .addOnSuccessListener {
                                    tweetList.isClickable = true
                                    callback?.onUserUpdate()
                                }
                                .addOnFailureListener {
                                    tweetList.isClickable = true
                                }

                        }
                        .setNegativeButton("Cancel") { dialog, which -> }
                        .show()
                } else {

                    AlertDialog.Builder(tweetList.context)
                        .setTitle("Follow ${tweet.userName}")
                        .setPositiveButton("Yes") { dialog, which ->
                            tweetList.isClickable = false
                            var followedUsers = user?.followUsers
                            if (followedUsers==null)
                            {
                                followedUsers= arrayListOf()
                            }
                            owner?.let {
                                followedUsers?.add(owner)
                                firebaseDB.collection(DATA_USERS).document(userId!!)
                                    .update(DATA_USER_FOLLOW, followedUsers)
                                    .addOnSuccessListener {
                                        tweetList.isClickable = true
                                        callback?.onUserUpdate()
                                    }
                                    .addOnFailureListener {
                                        tweetList.isClickable = true
                                    }
                            }

                        }
                        .setNegativeButton("Cancel") { dialog, which -> }
                        .show()

                }
            }
        }

    }

    override fun onLike(tweet: Tweet?) {

        tweet?.let {
            tweetList.isClickable = false
            val likes =
                tweet.likes     //Simply this will check that if the logged in user already has already that userid in the array or not
            if (tweet.likes?.contains(userId) == true) {
                tweet.likes?.remove(userId)
            } else {
                tweet.likes?.add(userId!!)
            }

            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!).update(DATA_TWEET_LIKES, likes)
                .addOnSuccessListener {
                    tweetList.isClickable = true
                    callback?.onRefresh()
                }
                .addOnFailureListener {
                    tweetList.isClickable = true
                }
        }

    }

    override fun onRetweet(tweet: Tweet?) {

        tweet?.let {
            tweetList.isClickable = false
            val retweet = tweet.userIds // list of ids which have retweeted that tweet
            if (retweet?.contains(userId) == true) {
                retweet?.remove(userId)

            } else {
                retweet?.add(userId!!)
            }
            firebaseDB.collection(DATA_TWEETS).document(tweet.tweetId!!).update(DATA_TWEET_USER_IDS, retweet)
                .addOnSuccessListener {
                    tweetList.isClickable = true
                    callback?.onRefresh()

                }
                .addOnFailureListener {

                    tweetList.isClickable = true
                }
        }
    }

}