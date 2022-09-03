package com.example.umslweatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide

//class WeatherAdapter (val weatherResponse: WeatherResponse) : TextView(){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
//        return WeatherViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
//        return holder.bind(weatherResponse)
//    }
//
//    override fun getItemCount(): Int {
//        return movies.size
//    }
//}
//
//class WeatherViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
//
//    private val photo: ImageView = itemView.findViewById(R.id.ivMoviePhoto)
//    private val title: TextView = itemView.findViewById(R.id.tvMovieTitle)
//    private val overview: TextView = itemView.findViewById(R.id.tvMovieOverview)
//    private val rating: TextView = itemView.findViewById(R.id.tvMovieRating)
//
//    fun bind(weatherResponse: WeatherResponse){
//        Glide.with(itemView.context).load("http://openweathermap.org/img/wn/${weatherResponse.weather[0].icon}@2x.png").into(photo)
//        title.text = "Title: " + movie.title
//        overview.text = movie.overview
//        rating.text = "Rating: " + movie.vote_average.toString()
//    }
//}
