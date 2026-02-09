package com.example.pawelierapp.screens

import androidx.compose.runtime.mutableStateMapOf
import com.example.pawelierapp.R

/**
 * Data class representing a product in the store
 */
data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val description: String,
    val imageRes: Int,
    val category: String
)

/**
 * Data class representing an item in the shopping cart
 */
data class CartItem(
    val product: Product,
    val quantity: Int = 1,
    val size: String = "Medium"
)

/**
 * Singleton to manage favorites across the app
 */
object FavoritesManager {
    private val _favoriteProducts = mutableStateMapOf<Int, Product>()
    // Expose as SnapshotStateMap for Compose observation
    val favoriteProducts: androidx.compose.runtime.snapshots.SnapshotStateMap<Int, Product>
        get() = _favoriteProducts

    fun isFavorite(productId: Int): Boolean = _favoriteProducts.containsKey(productId)

    fun toggleFavorite(product: Product) {
        if (_favoriteProducts.containsKey(product.id)) {
            _favoriteProducts.remove(product.id)
        } else {
            _favoriteProducts[product.id] = product
        }
    }

    fun addFavorite(product: Product) {
        _favoriteProducts[product.id] = product
    }

    fun removeFavorite(productId: Int) {
        _favoriteProducts.remove(productId)
    }
}

/**
 * Singleton to manage cart across the app
 */
object CartManager {
    private val _cartItems = mutableStateMapOf<Int, CartItem>()
    val cartItems: Map<Int, CartItem> get() = _cartItems

    fun addToCart(product: Product, quantity: Int = 1, size: String = "Medium") {
        val existing = _cartItems[product.id]
        if (existing != null) {
            _cartItems[product.id] = CartItem(existing.product, existing.quantity + quantity, size)
        } else {
            _cartItems[product.id] = CartItem(product, quantity, size)
        }
    }

    fun removeFromCart(productId: Int) {
        _cartItems.remove(productId)
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        val item = _cartItems[productId]
        if (item != null && quantity > 0) {
            _cartItems[productId] = CartItem(item.product, quantity, item.size)
        } else if (quantity <= 0) {
            _cartItems.remove(productId)
        }
    }

    fun getTotal(): Double {
        return _cartItems.values.sumOf { item ->
            val priceStr = item.product.price
                .replace("£", "")
                .replace("Rs.", "")
                .replace(",", "")
                .trim()
            priceStr.toDoubleOrNull()?.times(item.quantity) ?: 0.0
        }
    }

    fun clearCart() {
        _cartItems.clear()
    }

    fun getItemCount(): Int = _cartItems.values.sumOf { it.quantity }
}

/**
 * Data class representing an order notification
 */
data class OrderNotification(
    val orderId: String,
    val products: List<Product>,
    val totalAmount: Double,
    val itemCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Singleton to manage order notifications across the app
 */
object NotificationManager {
    private val _notifications = mutableStateMapOf<String, OrderNotification>()

    // Expose as SnapshotStateMap for Compose observation
    val notifications: androidx.compose.runtime.snapshots.SnapshotStateMap<String, OrderNotification>
        get() = _notifications

    /**
     * Add a new order notification
     */
    fun addNotification(notification: OrderNotification) {
        _notifications[notification.orderId] = notification
    }

    /**
     * Remove a specific notification
     */
    fun removeNotification(orderId: String) {
        _notifications.remove(orderId)
    }

    /**
     * Clear all notifications
     */
    fun clearAllNotifications() {
        _notifications.clear()
    }

    /**
     * Get notification count
     */
    fun getNotificationCount(): Int = _notifications.size
}

/**
 * Product data for the store
 */
object ProductData {
    val wearProducts = listOf(
        Product(1, "Luxury Pet Collar", "Rs.4000.00", "Premium leather collar with gold-plated hardware. Adjustable and comfortable for daily wear.", R.drawable.w1, "WEAR"),
        Product(2, "Designer Sweater", "Rs.3000.00", "Soft merino wool sweater to keep your pet warm in style.", R.drawable.w2, "WEAR"),
        Product(3, "Bow Tie Set", "Rs.2000.00", "Elegant bow tie collection for special occasions.", R.drawable.w3, "WEAR"),
        Product(4, "Rain Jacket", "Rs.5000.00", "Waterproof jacket with reflective strips for safety.", R.drawable.w4, "WEAR"),
        Product(5, "Party Dress", "Rs.4500.00", "Adorable dress perfect for celebrations and photo shoots.", R.drawable.w5, "WEAR"),
        Product(6, "Winter Coat", "Rs.6000.00", "Thick, insulated coat for cold weather protection.", R.drawable.w6, "WEAR"),
        Product(7, "Summer T-Shirt", "Rs.2500.00", "Breathable cotton t-shirt with fun prints for warm weather.", R.drawable.w7, "WEAR"),
        Product(8, "Bandana Collection", "Rs.1500.00", "Set of 3 stylish bandanas in different patterns.", R.drawable.w8, "WEAR"),
    )

    val walkProducts = listOf(
        Product(9, "Premium Leash", "£35.00", "Durable nylon leash with padded handle for comfort.", R.drawable.walk1, "WALK"),
        Product(10, "Comfort Harness", "£48.00", "No-pull harness with breathable mesh design.", R.drawable.walk2, "WALK"),
        Product(11, "Retractable Leash", "£42.00", "16-foot retractable leash with one-button control.", R.drawable.walk3, "WALK"),
        Product(12, "Reflective Collar", "£28.00", "Safety collar with LED lights for night walks.", R.drawable.walk4, "WALK"),
        Product(13, "Poop Bag Dispenser", "£12.00", "Stylish dispenser with biodegradable bags included.", R.drawable.walk5, "WALK"),
        Product(14, "Walking Belt", "£32.00", "Hands-free leash system for active pet owners.", R.drawable.walk6, "WALK"),
        Product(15, "Training Clicker", "£8.00", "Professional clicker for positive reinforcement training during walks.", R.drawable.wal7, "WALK"),
        Product(16, "Treat Pouch", "£18.00", "Convenient pouch to carry treats and accessories on walks.", R.drawable.walk8, "WALK"),
    )

    val livingProducts = listOf(
        Product(17, "Luxury Pet Bed", "£85.00", "Orthopedic memory foam bed with removable washable cover.", R.drawable.li1, "LIVING"),
        Product(18, "Feeding Bowl Set", "£35.00", "Ceramic elevated bowls for better digestion.", R.drawable.li2, "LIVING"),
        Product(19, "Interactive Toy", "£25.00", "Smart toy that keeps your pet entertained for hours.", R.drawable.li3, "LIVING"),
        Product(20, "Pet Blanket", "£42.00", "Ultra-soft fleece blanket for cozy naps.", R.drawable.li4, "LIVING"),
        Product(21, "Scratching Post", "£58.00", "Multi-level cat tree with sisal rope posts.", R.drawable.li5, "LIVING"),
        Product(22, "Water Fountain", "£65.00", "Automatic pet fountain with triple filtration system.", R.drawable.li6, "LIVING"),
        Product(23, "Grooming Kit", "£45.00", "Complete grooming set with brush, nail clipper, and comb.", R.drawable.li7, "LIVING"),
        Product(24, "Calming Diffuser", "£38.00", "Pheromone diffuser to reduce pet anxiety and stress.", R.drawable.li8, "LIVING"),
    )

    val travelProducts = listOf(
        Product(25, "Travel Carrier", "£95.00", "Airline-approved carrier with ventilation and comfort.", R.drawable.t1, "TRAVEL"),
        Product(26, "Car Seat Cover", "£45.00", "Waterproof seat protector with hammock design.", R.drawable.t2, "TRAVEL"),
        Product(27, "Portable Bowl", "£18.00", "Collapsible food and water bowl for travel.", R.drawable.t3, "TRAVEL"),
        Product(28, "Pet Backpack", "£78.00", "Comfortable backpack carrier for small pets.", R.drawable.t4, "TRAVEL"),
        Product(29, "Travel Bed", "£52.00", "Foldable bed that fits in any suitcase.", R.drawable.t5, "TRAVEL"),
        Product(30, "ID Tag Set", "£15.00", "Customizable tags with QR code for easy identification.", R.drawable.t6, "TRAVEL"),
        Product(31, "Pet Seatbelt", "£22.00", "Safety harness seatbelt for secure car travel.", R.drawable.t7, "TRAVEL"),
        Product(32, "Travel First Aid Kit", "£35.00", "Complete emergency kit for pets on the go.", R.drawable.t8, "TRAVEL"),
    )
}

