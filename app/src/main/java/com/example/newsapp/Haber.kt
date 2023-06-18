package com.example.newsapp

import com.google.firebase.database.DataSnapshot
import java.io.Serializable

class Haber : Serializable {
    var haber_date:String?=""
    var haber_desc:String?=""
    var haber_img:String?=""
    var haber_title:String?=""
    var haber_source:String?=""
    var haber_link:String?=""
    var haber_type:String?=""
    var haber_name:String?=""

    constructor()
    constructor(haber_img: String?, haber_title: String?, haber_source: String?) {
        this.haber_img = haber_img
        this.haber_title = haber_title
        this.haber_source = haber_source
    }

    constructor(
        haber_date: String?,
        haber_desc: String?,
        haber_img: String?,
        haber_title: String?,
        haber_source: String?,
        haber_link: String?,
        haber_type: String?,
        haber_name: String?
    ) {
        this.haber_date = haber_date
        this.haber_desc = haber_desc
        this.haber_img = haber_img
        this.haber_title = haber_title
        this.haber_source = haber_source
        this.haber_link = haber_link
        this.haber_type = haber_type
        this.haber_name = haber_name
    }

    fun DataSnapshot.toHaber(): Haber? {
        return this.getValue(Haber::class.java)
    }
}