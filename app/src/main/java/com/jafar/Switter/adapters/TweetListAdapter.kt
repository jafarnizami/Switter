package com.jafar.Switter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jafar.Switter.R
import com.jafar.Switter.listeners.TweetListener
import com.jafar.Switter.util.Tweet
import com.jafar.Switter.util.getDate
import com.jafar.Switter.util.loadUrl

class TweetListAdapter(val userId:String,val tweets:ArrayList<Tweet>):RecyclerView.Adapter<TweetListAdapter.TweetViewHolder>() {

   private var listener:TweetListener?=null //listener basically is used to listen for the changes in the tweet like:::"CHANGE IN THE LIKES OR RETWETS ETC"
    fun setListener(listener: TweetListener?)
    {
        this.listener=listener   //2) Listener is connected to the  TweetListener
    }

    fun updateTweets(newTweets:List<Tweet>)
    {
        tweets.clear()
        tweets.addAll(newTweets)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= TweetViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_tweet,parent,false)
    )

    override fun getItemCount()=tweets.size

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {

        holder.Bind(userId,tweets[position],listener)    //binds the different tweets of the user stored in the arraylist
    }

    class TweetViewHolder(v:View):RecyclerView.ViewHolder(v)
    {
        private val layout =v.findViewById<ViewGroup>(R.id.tweetLayout) //making objects of all the ids in the xml
        private val username =v.findViewById<TextView>(R.id.tweetUsername)
        private val text =v.findViewById<TextView>(R.id.tweetText)
        private val image =v.findViewById<ImageView>(R.id.tweetImagefrag)
        private val date =v.findViewById<TextView>(R.id.tweetDate)
        private val like =v.findViewById<ImageView>(R.id.tweetLike)
        private val likeCount=v.findViewById<TextView>(R.id.tweetLikeCount)
        private val retweet =v.findViewById<ImageView>(R.id.tweetRetweet)
        private val retweetCount=v.findViewById<TextView>(R.id.tweetRetweetCount)

        fun Bind(userId: String,tweet:Tweet,listener: TweetListener?)
        {
            username.text=tweet.userName
            text.text=tweet.text
            if (tweet.imageUrl.isNullOrEmpty())
            {
                image.visibility=View.GONE      //if user has not uploaded the image in the tweet
            }
            else{
                image.visibility=View.VISIBLE
                image.loadUrl(tweet.imageUrl)
            }
            date.text= getDate(tweet.timestamp)
            likeCount.text=tweet.likes?.size.toString()  // no of likes is the size of the arraylist "likes"
            retweetCount.text=tweet.userIds?.size?.minus(1).toString() //minus 1 because the first user in the arraylst userids is the user itself

            layout.setOnClickListener {listener?.onLayoutClick(tweet)}  //when user click on these options an listener is called to make the change
            like.setOnClickListener { listener?.onLike(tweet) }   //1)
            retweet.setOnClickListener { listener?.onRetweet(tweet) }

            if (tweet.likes?.contains(userId)==true) //whosoever likes your tweets is saved in the "likes arraylist" so here we are checking that the logged in user has liked this post or not
                //if john goes to davids profile and like his post then davids arraylist of likes will have a userid of john ,and here we are checking whether david id is present in johns arraylist or not
            {
                like.setImageDrawable(ContextCompat.getDrawable(like.context,R.drawable.like))
            }
            else{
                like.setImageDrawable(ContextCompat.getDrawable(like.context,R.drawable.like_inactive))
            }

            if (tweet.userIds?.get(0).equals(userId))  //if user is himself present in the userIds arraylist then it means user has created this tweet and cannot retweet his own tweet
            {
                retweet.setImageDrawable(ContextCompat.getDrawable(retweet.context,R.drawable.original))
                retweet.isClickable=false
            }
            else if (tweet.userIds?.contains(userId)==true)  //if userIds arraylist contains the userId of the logged in user that meand that user has already retweeted this....
            {
                retweet.setImageDrawable(ContextCompat.getDrawable(retweet.context,R.drawable.retweet))
            }
            else{
                retweet.setImageDrawable(ContextCompat.getDrawable(retweet.context,R.drawable.retweet_inactive))

            }
        }
    }
}