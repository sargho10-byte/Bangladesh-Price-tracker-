package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nameEn: String,
    val nameBn: String,
    val categoryEn: String,
    val categoryBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val imageUrl: String? = null
)

@Entity(tableName = "stores")
data class Store(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nameEn: String,
    val nameBn: String,
    val locationEn: String,
    val locationBn: String,
    val isOnline: Boolean
)

@Entity(tableName = "product_store_prices")
data class ProductStorePrice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val storeId: Int,
    val price: Double,
    val isAvailable: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "price_history")
data class PriceHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val storeId: Int,
    val price: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titleEn: String,
    val titleBn: String,
    val bodyEn: String,
    val bodyBn: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "promo_banners")
data class PromoBanner(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titleEn: String,
    val titleBn: String,
    val activeProductId: Int, // product to open when clicked
    val imageUrl: String? = null,
    val isActive: Boolean = true
)

// UI and Query helper classes
data class PriceWithStore(
    val priceId: Int,
    val productId: Int,
    val storeId: Int,
    val price: Double,
    val isAvailable: Boolean,
    val lastUpdated: Long,
    val storeNameEn: String,
    val storeNameBn: String,
    val storeLocationEn: String,
    val storeLocationBn: String,
    val isOnline: Boolean
)

data class ProductWithPrices(
    val product: Product,
    val prices: List<PriceWithStore>
) {
    fun getLowestAvailablePrice(): PriceWithStore? {
        return prices.filter { it.isAvailable }.minByOrNull { it.price }
    }

    fun getHighestAvailablePrice(): PriceWithStore? {
        return prices.filter { it.isAvailable }.maxByOrNull { it.price }
    }
}
