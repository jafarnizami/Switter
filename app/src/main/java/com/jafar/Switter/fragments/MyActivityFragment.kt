package com.jafar.Switter.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.jafar.Switter.R
import com.jafar.Switter.adapters.TweetListAdapter
import com.jafar.Switter.listeners.TwitterListenerImpl
import com.jafar.Switter.util.DATA_TWEETS
import com.jafar.Switter.util.DATA_TWEET_USER_IDS
import com.jafar.Switter.util.Tweet
import kotlinx.android.synthetic.main.fragment_my_activity.*


class MyActivityFragment : TwitterFragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener= TwitterListenerImpl(tweetListmy,currentUser,callback)

        tweetsAdapter= TweetListAdapter(userId!!, arrayListOf())
        tweetsAdapter?.setListener(listener)

        tweetListmy?.apply {
            layoutManager= LinearLayoutManager(context)
            adapter=tweetsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        swipeRefreshmy.setOnRefreshListener {
            swipeRefreshmy.isRefreshing=false
            updateList()
        }
    }
    override fun updateList() {

        tweetListmy.visibility=View.GONE
        val tweets = arrayListOf<Tweet>()

        firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_USER_IDS,userId!!).get()
            .addOnSuccessListener {
                for (document in it.documents)
                {
                    val tweet=document.toObject(Tweet::class.java)
                    tweet?.let {
                        tweets.add(tweet)

                    }
                }
                val sortedList=tweets.sortedWith(compareByDescending { it.timestamp })
                tweetsAdapter?.updateTweets(sortedList)
                tweetListmy?.visibility=View.VISIBLE

            }
            .addOnFailureListener {
                it.printStackTrace()
                tweetListmy.visibility=View.VISIBLE
            }


    }



}
