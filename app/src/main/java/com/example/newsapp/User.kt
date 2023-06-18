package com.example.newsapp


 class User{
     var user_email = ""
     var user_password = ""
     var fav_sources : List<String> = listOf()
     var fav_types : List<String> = listOf()
     var liked_news : List<String> = listOf()
     var saved_news : List<String> = listOf()
     var user_notifications : List<String> = listOf()


     constructor()

    constructor( user_email: String, user_password:String){
        this.user_email= user_email
        this.user_password = user_password


    }




}