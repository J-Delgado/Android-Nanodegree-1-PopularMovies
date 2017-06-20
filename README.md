# Android Nanodegree Popular Movies

This is the app that I have built for Popular Movies Stage 1. First project in the Android Developer Nanodegree. This app implements show the most popular movies information and their details. I have tried to use the newest Material Design features when it was possible.

## Features Stage 1 (branch stage1)

* Upon launch, present the user with a grid arrangement of movie posters using Recycler View.
* The user can use "Pull to refresh" to update the data (Useful when previously there was no internet connection).
* Sort movies by most popular or highest rated using Floating Action Button.
* When the user taps on a movie, there is an animation to open the movie detail.
* The movie detail includes original title, movie poster, a plot synopsis, user rating, release date and a image backdrop with animated Toolbar.

## Features Stage 2 (master)

* Different amount of movies per row depending on device width 
* Uses Content Provider to store the Favorite movies and stores most of its information to check offline.
* In the movie detail, the Trailers and Reviews are shown in a horizontal scroll view using Recycler Views.
* In the movie detail, the actions Favorite and Share are shown in Floating Action Buttons.

## External APIs

This app uses The Movie Database API to retrieve movies. You must provide your own API key in order to build the app. It must be included in strings.xml:
* [The Movie Database](https://www.themoviedb.org/documentation/api)

## External libraries

This app uses Picasso to request movie covers and cache them:
* [Picasso](http://square.github.io/picasso/)


## Screenshoots

![screen](../master/art/movieList.png)
![screen](../master/art/movieDetail.png)
![screen](../master/art/movieDetail3.png)
![screen](../master/art/favorites.png)
