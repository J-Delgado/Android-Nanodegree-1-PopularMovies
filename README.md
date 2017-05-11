# Android Nanodegree Popular Movies Stage 1

This is the app that I have built for Popular Movies Stage 1. First project in the Android Developer Nanodegree. This app implements show the most popular movies information and their details. I have tried to use the newest Material Design features when it was possible.

## Features

Upon launch, present the user with a grid arrangement of movie posters using Recycler View.
The user can use "Pull to refresh" to update the data (Useful when previously there was no internet connection).
Sort movies by most popular or highest rated using Floating Action Button.
When the user taps on a movie, there is an animation to open the movie detail.

The movie detail includes original title, movie poster, a plot synopsis, user rating, release date and a image backdrop with animated Toolbar.

## External APIs

This app uses The Movie Database API to retrieve movies. You must provide your own API key in order to build the app. It must be included in strings.xml
[The Movie Database](https://www.themoviedb.org/documentation/api)

## External libraries

This app uses Picasso to request movie covers and cache them:
* [Picasso](http://square.github.io/picasso/)


## Screenshoots

![screen](../master/art/moviesList.png)
![screen](../master/art/moviesDetail.png)
![screen](../master/art/moviesDetail2.png)

<img alt="Main Activity" src="http://imgur.com/ikTczyR.png" /> <img alt="Detail Activity" src="http://imgur.com/cYwB7Il.png" />
