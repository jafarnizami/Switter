package com.jafar.Switter.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

import com.jafar.Switter.R
import com.jafar.Switter.adapters.TweetListAdapter
import com.jafar.Switter.listeners.TweetListener
import com.jafar.Switter.listeners.TwitterListenerImpl
import com.jafar.Switter.util.*
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : TwitterFragment() {


    private var currentHashtag = ""
    private var hashtagFollowed = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener=TwitterListenerImpl(tweetList,currentUser,callback)

        tweetsAdapter = TweetListAdapter(userId!!, arrayListOf())
        tweetsAdapter?.setListener(listener)
        tweetList?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tweetsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            updateList()
        }

        followHashtag.setOnClickListener {
            followHashtag.isClickable = false
            val followed = currentUser?.followHashTags
            if(hashtagFollowed) {
                followed?.remove(currentHashtag)
            } else {
                followed?.add(currentHashtag)
            }
            firebaseDB.collection(DATA_USERS).document(userId).update(DATA_USER_HASHTAGS, followed)
                .addOnSuccessListener {
                    callback?.onUserUpdate()
                    followHashtag.isClickable = true
                }
                .addOnFailureListener {e ->
                    e.printStackTrace()
                    followHashtag.isClickable = true
                }
        }
    }

    fun newHashtag(term: String) {
        currentHashtag = term
        followHashtag.visibility = View.VISIBLE
        updateList()
    }

    override fun updateList() {
        tweetList?.visibility = View.GONE
        firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_HASHTAG, currentHashtag).get()
            .addOnSuccessListener { list ->
                tweetList?.visibility = View.VISIBLE
                val tweets = arrayListOf<Tweet>()
                for(document in list.documents) {
                    val tweet = document.toObject(Tweet::class.java)
                    tweet?.let { tweets.add(it) }
                }
                val sortedTweets = tweets.sortedWith(compareByDescending { it.timestamp })
                tweetsAdapter?.updateTweets(sortedTweets)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }

        updateFollowDrawable()
    }

    private fun updateFollowDrawable() {
        hashtagFollowed = currentUser?.followHashTags?.contains(currentHashtag) == true
        context?.let {
            if (hashtagFollowed) {
                followHashtag.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.follow))
            } else {
                followHashtag.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.follow_inactive))
            }
        }
    }

}