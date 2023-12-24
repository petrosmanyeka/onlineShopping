package com.example.onlineshop

class Upload(
    var name: String,
    var imageUrl: String,
    var price: Double // New price field
) {
    // Required empty constructor for Firebase
    constructor() : this("", "", 0.0)
}