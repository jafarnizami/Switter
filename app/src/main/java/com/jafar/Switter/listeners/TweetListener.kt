package com.jafar.Switter.listeners

import com.jafar.Switter.util.Tweet

interface TweetListener {
    fun onLayoutClick(tweet:Tweet?)
    fun onLike(tweet: Tweet?)
    fun onRetweet(tweet: Tweet?)
}