package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Products
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Int): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)


    // Stores
    @Query("SELECT * FROM stores ORDER BY nameEn ASC")
    fun getAllStores(): Flow<List<Store>>

    @Query("SELECT * FROM stores WHERE id = :storeId")
    suspend fun getStoreById(storeId: Int): Store?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: Store): Long

    @Update
    suspend fun updateStore(store: Store)

    @Delete
    suspend fun deleteStore(store: Store)


    // Product Store Prices
    @Query("""
        SELECT 
            p.id as priceId, 
            p.productId, 
            p.storeId, 
            p.price, 
            p.isAvailable, 
            p.lastUpdated,
            s.nameEn as storeNameEn, 
            s.nameBn as storeNameBn,
            s.locationEn as storeLocationEn, 
            s.locationBn as storeLocationBn,
            s.isOnline as isOnline
        FROM product_store_prices p
        INNER JOIN stores s ON p.storeId = s.id
        WHERE p.productId = :productId
    """)
    fun getPricesForProductFlow(productId: Int): Flow<List<PriceWithStore>>

    @Query("""
        SELECT 
            p.id as priceId, 
            p.productId, 
            p.storeId, 
            p.price, 
            p.isAvailable, 
            p.lastUpdated,
            s.nameEn as storeNameEn, 
            s.nameBn as storeNameBn,
            s.locationEn as storeLocationEn, 
            s.locationBn as storeLocationBn,
            s.isOnline as isOnline
        FROM product_store_prices p
        INNER JOIN stores s ON p.storeId = s.id
        WHERE p.productId = :productId
    """)
    suspend fun getPricesForProduct(productId: Int): List<PriceWithStore>

    @Query("""
         SELECT 
            p.id as priceId, 
            p.productId, 
            p.storeId, 
            p.price, 
            p.isAvailable, 
            p.lastUpdated,
            s.nameEn as storeNameEn, 
            s.nameBn as storeNameBn,
            s.locationEn as storeLocationEn, 
            s.locationBn as storeLocationBn,
            s.isOnline as isOnline
        FROM product_store_prices p
        INNER JOIN stores s ON p.storeId = s.id
    """)
    fun getAllPricesFlow(): Flow<List<PriceWithStore>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductStorePrice(price: ProductStorePrice)

    @Query("DELETE FROM product_store_prices WHERE productId = :productId")
    suspend fun deletePricesForProduct(productId: Int)

    @Query("DELETE FROM product_store_prices WHERE id = :priceId")
    suspend fun deletePriceById(priceId: Int)


    // Price History
    @Query("SELECT * FROM price_history WHERE productId = :productId ORDER BY timestamp ASC")
    fun getPriceHistoryForProductFlow(productId: Int): Flow<List<PriceHistory>>

    @Query("SELECT * FROM price_history WHERE productId = :productId ORDER BY timestamp ASC")
    suspend fun getPriceHistoryForProduct(productId: Int): List<PriceHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceHistory(history: PriceHistory)

    @Query("DELETE FROM price_history WHERE productId = :productId")
    suspend fun deletePriceHistoryForProduct(productId: Int)


    // Notifications
    @Query("SELECT * FROM app_notifications ORDER BY timestamp DESC")
    fun getAllNotificationsFlow(): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification): Long

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM app_notifications WHERE id = :notificationId")
    suspend fun deleteNotificationById(notificationId: Int)


    // Promo Banners
    @Query("SELECT * FROM promo_banners WHERE isActive = 1")
    fun getActivePromoBanners(): Flow<List<PromoBanner>>

    @Query("SELECT * FROM promo_banners ORDER BY id DESC")
    fun getAllPromoBanners(): Flow<List<PromoBanner>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPromoBanner(banner: PromoBanner): Long

    @Update
    suspend fun updatePromoBanner(banner: PromoBanner)

    @Query("DELETE FROM promo_banners WHERE id = :bannerId")
    suspend fun deletePromoBannerById(bannerId: Int)
}
