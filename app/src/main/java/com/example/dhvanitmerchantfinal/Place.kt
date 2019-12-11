package com.example.dhvanitmerchantfinal

class Place {
    var placeId: String? = null
    var name: String? = null
    var url: String? = null




    // required empty constructor - not needed to add, but needed to read
    constructor() {
    }

    constructor(
        placeId: String?,
        name: String?,
        url: String?

    ) {
        this.placeId = placeId
        this.name = name
        this.url = url
    }



}