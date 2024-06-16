package com.example.dermai.ui.wishlist

import android.util.Log
import com.example.dermai.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WishlistRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId get() = auth.currentUser?.uid ?: ""

    fun addProductToWishlist(product: Product) {
        db.collection("users")
            .document(userId)
            .collection("wishlist")
            .document(product.id.toString())
            .set(product)
            .addOnSuccessListener {
                Log.d("WishlistRepository", "Product added to wishlist: ${product.name}")
            }
            .addOnFailureListener { e ->
                Log.e("WishlistRepository", "Error adding product to wishlist", e)
            }
    }

    fun removeProductFromWishlist(productId: Int) {
        db.collection("users")
            .document(userId)
            .collection("wishlist")
            .document(productId.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("WishlistRepository", "Product removed from wishlist: $productId")
            }
            .addOnFailureListener { e ->
                Log.e("WishlistRepository", "Error removing product from wishlist", e)
            }
    }

    fun getWishlist(callback: (List<Product>) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("wishlist")
            .get()
            .addOnSuccessListener { result ->
                val products = result.map { document -> document.toObject(Product::class.java) }
                callback(products)
                Log.d("WishlistRepository", "Wishlist loaded: ${products.size} products")
            }
            .addOnFailureListener { e ->
                Log.e("WishlistRepository", "Error loading wishlist", e)
            }
    }
    fun isProductInWishlist(productId: Int): Boolean {
        val wishlistCollection = db.collection("users").document(userId).collection("wishlist")
        var exists = false
        wishlistCollection.document(productId.toString()).get()
            .addOnSuccessListener { document ->
                exists = document.exists()
            }
        return exists
    }
}
