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
import com.jafar.Switter.util.DATA_USER_HASHTAGS
import com.jafar.Switter.util.Tweet
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*


class HomeFragment : TwitterFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
//HOME FRAGMENT INHERITS FROM THE TWITTER FRAGMENT
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listener= TwitterListenerImpl(tweetListhome,currentUser,callback)

    tweetsAdapter= TweetListAdapter(userId!!, arrayListOf())
    tweetsAdapter?.setListener(listener)

    tweetListhome?.apply {
        layoutManager=LinearLayoutManager(context)
        adapter=tweetsAdapter
        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    swipeRefreshhome.setOnRefreshListener {
        swipeRefreshhome.isRefreshing=false
        updateList()
    }


    }
    override fun updateList() {

        //getting the values from the search fragment here...

        tweetListhome.visibility=View.GONE
        currentUser?.let {
            val tweets = arrayListOf<Tweet>()

            for (hashtag in it.followHashTags!!)
            {
                firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_USER_HASHTAGS,hashtag).get()
                    .addOnSuccessListener {

                        for (document in it.documents)
                        {
                            val tweet =document.toObject(Tweet::class.java)
                            tweet?.let {
                                tweets.add(it)
                            }
                        }
                        updateAdapter(tweets)
                        tweetListhome.visibility=View.VISIBLE
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        tweetListhome?.visibility=View.VISIBLE
                    }
            }
            for (followedUser in it.followUsers!!)
            {
                firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_USER_IDS,followedUser).get()
                    .addOnSuccessListener {

                        for (document in it.documents)
                        {
                            val tweet =document.toObject(Tweet::class.java)
                            tweet?.let {
                                tweets.add(it)
                            }
                        }
                        updateAdapter(tweets)
                        tweetListhome.visibility=View.VISIBLE
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        tweetListhome?.visibility=View.VISIBLE
                    }


            }
        }
    }

    private  fun updateAdapter(tweets: List<Tweet>)
    {
        val sortedTweets=tweets.sortedWith(compareByDescending { it.timestamp })
        tweetsAdapter?.updateTweets(removeDuplicates(sortedTweets))
    }

    private fun removeDuplicates(originalList: List<Tweet>)=originalList.distinctBy { it.tweetId }




}
