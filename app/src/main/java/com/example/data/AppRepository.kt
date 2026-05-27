package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val appDao: AppDao) {

    // Reactive streams
    val productsFlow: Flow<List<Product>> = appDao.getAllProducts()
    val storesFlow: Flow<List<Store>> = appDao.getAllStores()
    val notificationsFlow: Flow<List<AppNotification>> = appDao.getAllNotificationsFlow()
    val activeBannersFlow: Flow<List<PromoBanner>> = appDao.getActivePromoBanners()
    val allBannersFlow: Flow<List<PromoBanner>> = appDao.getAllPromoBanners()

    val productsWithPricesFlow: Flow<List<ProductWithPrices>> = combine(
        appDao.getAllProducts(),
        appDao.getAllPricesFlow()
    ) { products, prices ->
        products.map { prod ->
            ProductWithPrices(
                product = prod,
                prices = prices.filter { it.productId == prod.id }
            )
        }
    }

    // Products DAO wrappers
    suspend fun insertProduct(product: Product): Int = withContext(Dispatchers.IO) {
        appDao.insertProduct(product).toInt()
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        appDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) = withContext(Dispatchers.IO) {
        appDao.deletePricesForProduct(product.id)
        appDao.deletePriceHistoryForProduct(product.id)
        appDao.deleteProduct(product)
    }

    // Stores DAO wrappers
    suspend fun insertStore(store: Store): Int = withContext(Dispatchers.IO) {
        appDao.insertStore(store).toInt()
    }

    suspend fun updateStore(store: Store) = withContext(Dispatchers.IO) {
        appDao.updateStore(store)
    }

    suspend fun deleteStore(store: Store) = withContext(Dispatchers.IO) {
        appDao.deleteStore(store)
    }

    // Prices wrappers
    suspend fun insertPrice(price: ProductStorePrice) = withContext(Dispatchers.IO) {
        appDao.insertProductStorePrice(price)
        // Also log to history to build comparison charts
        appDao.insertPriceHistory(
            PriceHistory(
                productId = price.productId,
                storeId = price.storeId,
                price = price.price,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deletePriceById(priceId: Int) = withContext(Dispatchers.IO) {
        appDao.deletePriceById(priceId)
    }

    // Price History
    fun getPriceHistoryFlow(productId: Int): Flow<List<PriceHistory>> {
        return appDao.getPriceHistoryForProductFlow(productId)
    }

    suspend fun getPriceHistoryList(productId: Int): List<PriceHistory> = withContext(Dispatchers.IO) {
        appDao.getPriceHistoryForProduct(productId)
    }

    // Notifications
    suspend fun sendNotification(titleEn: String, titleBn: String, bodyEn: String, bodyBn: String) = withContext(Dispatchers.IO) {
        appDao.insertNotification(
            AppNotification(
                titleEn = titleEn,
                titleBn = titleBn,
                bodyEn = bodyEn,
                bodyBn = bodyBn,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        appDao.markAllNotificationsAsRead()
    }

    suspend fun deleteNotification(id: Int) = withContext(Dispatchers.IO) {
        appDao.deleteNotificationById(id)
    }

    // Promo Banners
    suspend fun insertBanner(banner: PromoBanner) = withContext(Dispatchers.IO) {
        appDao.insertPromoBanner(banner)
    }

    suspend fun deleteBanner(id: Int) = withContext(Dispatchers.IO) {
        appDao.deletePromoBannerById(id)
    }

    suspend fun updateBanner(banner: PromoBanner) = withContext(Dispatchers.IO) {
        appDao.updatePromoBanner(banner)
    }


    // Prepopulate database with realistic mock data
    suspend fun prepopulateDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val currentProducts = appDao.getAllProducts().first()
        if (currentProducts.isNotEmpty()) return@withContext

        // 1. Insert default stores
        val stores = listOf(
            Store(nameEn = "Star Tech", nameBn = "স্টার টেক", locationEn = "Multi-branch, Dhaka/Chittagong", locationBn = "মাল্টি-ব্রাঞ্চ, ঢাকা/চট্টগ্রাম", isOnline = true),
            Store(nameEn = "Ryans Computers", nameBn = "রায়ানস কম্পিউটার্স", locationEn = "IDB Bhaban, Dhaka", locationBn = "আইডিবি ভবন, ঢাকা", isOnline = true),
            Store(nameEn = "Shwapno Super Store", nameBn = "স্বপ্ন সুপার শপ", locationEn = "Dhanmondi, Dhaka", locationBn = "ধানমন্ডি, ঢাকা", isOnline = false),
            Store(nameEn = "Chaldal Grocery", nameBn = "চালডাল গ্রোসারি", locationEn = "Online Service, Dhaka", locationBn = "অনলাইন সার্ভিস, ঢাকা", isOnline = true),
            Store(nameEn = "Daraz BD", nameBn = "দারাজ বাংলাদেশ", locationEn = "Online Marketplace", locationBn = "অনলাইন মার্কেটপ্লেস", isOnline = true),
            Store(nameEn = "Lazz Pharma", nameBn = "লাজ ফার্মা", locationEn = "Kakrail, Dhaka", locationBn = "কাকরাইল, ঢাকা", isOnline = false),
            Store(nameEn = "Kawran Bazar Local Market", nameBn = "কাওরান বাজার কাঁচাবাজার", locationEn = "Tejgaon, Dhaka", locationBn = "তেজগাঁও, ঢাকা", isOnline = false)
        )

        val storeIds = stores.map { appDao.insertStore(it).toInt() }
        val starTechId = storeIds[0]
        val ryansId = storeIds[1]
        val shwapnoId = storeIds[2]
        val chaldalId = storeIds[3]
        val darazId = storeIds[4]
        val lazzPharmaId = storeIds[5]
        val kawranBazarId = storeIds[6]

        // 2. Insert default products
        val prod1 = Product(
            nameEn = "iPhone 15 Pro (128GB)",
            nameBn = "আইফোন ১৫ প্রো (১২৮জিবি)",
            categoryEn = "Electronics",
            categoryBn = "ইলেকট্রনিক্স",
            descriptionEn = "Apple iPhone 15 Pro with Aerospace-grade titanium design and A17 Pro chip.",
            descriptionBn = "অ্যারোস্পেস-গ্রেড টাইটানিয়াম ডিজাইন এবং এ১৭ প্রো চিপ সহ অ্যাপল আইফোন ১৫ প্রো।"
        )
        val prod2 = Product(
            nameEn = "Miniket Rice (5kg Bag)",
            nameBn = "মিনিকেট চাল (৫ কেজি বস্তা)",
            categoryEn = "Grocery",
            categoryBn = "নিত্যপ্রয়োজনীয় দ্রব্য",
            descriptionEn = "Premium quality polished grain Miniket rice from Dunich, Bangladesh.",
            descriptionBn = "বাংলাদেশের দুনিচ থেকে আসা প্রিমিয়াম মানের পালিশ করা মিনিকেট চাল।"
        )
        val prod3 = Product(
            nameEn = "Rupchanda Soyabean Oil (5 Litre)",
            nameBn = "রূপচাঁদা সয়াবিন তেল (৫ লিটার)",
            categoryEn = "Grocery",
            categoryBn = "নিত্যপ্রয়োজনীয় দ্রব্য",
            descriptionEn = "Fortified edible premium soyabean cooking oil for healthy meals.",
            descriptionBn = "স্বাস্থ্যকর খাবারের জন্য উন্নতমানের সয়াবিন রান্নার তেল।"
        )
        val prod4 = Product(
            nameEn = "Samsung 43 Inch 4K UHD Smart TV",
            nameBn = "স্যামসাং ৪৩ ইঞ্চি ৪কে স্মার্ট টিভি",
            categoryEn = "Home Appliances",
            categoryBn = "গৃহস্থালী সরঞ্জাম",
            descriptionEn = "Crystal UHD display smart TV with webOS and amazing speaker systems.",
            descriptionBn = "ক্রিস্টাল ইউএইচডি ডিসপ্লে স্মার্ট টিভি ওয়েবওএস এবং চমৎকার স্পিকার সিস্টেম সহ।"
        )
        val prod5 = Product(
            nameEn = "Napa Extend (10 Tablets)",
            nameBn = "নাপা এক্সটেন্ড (১০টি ট্যাবলেট)",
            categoryEn = "Medicine",
            categoryBn = "ঔষধপত্র",
            descriptionEn = "665mg Paracetamol tablets for long-lasting pain and fever relief.",
            descriptionBn = "দীর্ঘস্থায়ী ব্যথা এবং জ্বর উপশমের জন্য ৬৬৫ মিলিগ্রাম প্যারাসিটামল ট্যাবলেট।"
        )
        val prod6 = Product(
            nameEn = "Sony WH-1000XM5 Headphones",
            nameBn = "সনি WH-1000XM5 হেডফোনস",
            categoryEn = "Electronics",
            categoryBn = "ইলেকট্রনিক্স",
            descriptionEn = "Top-tier premium active noise canceling wireless over-ear headphones.",
            descriptionBn = "শীর্ষ-স্তরের প্রিমিয়াম অ্যাক্টিভ নয়েজ ক্যানসেলিং ওয়্যারলেস ওভার-ইয়ার হেডফোন।"
        )

        val id1 = appDao.insertProduct(prod1).toInt()
        val id2 = appDao.insertProduct(prod2).toInt()
        val id3 = appDao.insertProduct(prod3).toInt()
        val id4 = appDao.insertProduct(prod4).toInt()
        val id5 = appDao.insertProduct(prod5).toInt()
        val id6 = appDao.insertProduct(prod6).toInt()

        // 3. Insert Store Prices & History with weekly data (for nice comparison charts)
        val now = System.currentTimeMillis()
        val dayMs = 24 * 60 * 60 * 1000L

        // iPhone 15 Pro
        val iphonePrices = listOf(
            ProductStorePrice(productId = id1, storeId = starTechId, price = 142000.0, isAvailable = true),
            ProductStorePrice(productId = id1, storeId = ryansId, price = 143500.0, isAvailable = true),
            ProductStorePrice(productId = id1, storeId = darazId, price = 139000.0, isAvailable = true)
        )
        iphonePrices.forEach { appDao.insertProductStorePrice(it) }

        // iPhone history (e.g. tracking price over last 7 days)
        for (day in 7 downTo 0) {
            val date = now - (day * dayMs)
            appDao.insertPriceHistory(PriceHistory(productId = id1, storeId = starTechId, price = 145000.0 - (day * 400) + (Math.random() * 500), timestamp = date))
            appDao.insertPriceHistory(PriceHistory(productId = id1, storeId = ryansId, price = 146000.0 - (day * 350) + (Math.random() * 400), timestamp = date))
            appDao.insertPriceHistory(PriceHistory(productId = id1, storeId = darazId, price = 141000.0 - (day * 300) + (Math.random() * 600), timestamp = date))
        }

        // Miniket Rice
        val ricePrices = listOf(
            ProductStorePrice(productId = id2, storeId = shwapnoId, price = 390.0, isAvailable = true),
            ProductStorePrice(productId = id2, storeId = chaldalId, price = 385.0, isAvailable = true),
            ProductStorePrice(productId = id2, storeId = kawranBazarId, price = 365.0, isAvailable = true)
        )
        ricePrices.forEach { appDao.insertProductStorePrice(it) }

        // Rice history
        for (day in 7 downTo 0) {
            val date = now - (day * dayMs)
            appDao.insertPriceHistory(PriceHistory(productId = id2, storeId = shwapnoId, price = 380.0 + (day * 1.5), timestamp = date))
            appDao.insertPriceHistory(PriceHistory(productId = id2, storeId = chaldalId, price = 375.0 + (day * 1.2), timestamp = date))
            appDao.insertPriceHistory(PriceHistory(productId = id2, storeId = kawranBazarId, price = 350.0 + (day * 2.0), timestamp = date))
        }

        // Rupchanda Soyabean Oil
        val oilPrices = listOf(
            ProductStorePrice(productId = id3, storeId = shwapnoId, price = 890.0, isAvailable = true),
            ProductStorePrice(productId = id3, storeId = chaldalId, price = 880.0, isAvailable = true),
            ProductStorePrice(productId = id3, storeId = kawranBazarId, price = 855.0, isAvailable = true),
            ProductStorePrice(productId = id3, storeId = darazId, price = 910.0, isAvailable = true)
        )
        oilPrices.forEach { appDao.insertProductStorePrice(it) }

        // Soyabean oil history
        for (day in 7 downTo 0) {
            val date = now - (day * dayMs)
            appDao.insertPriceHistory(PriceHistory(productId = id3, storeId = shwapnoId, price = 920.0 - (day * 4.0), timestamp = date))
            appDao.insertPriceHistory(PriceHistory(productId = id3, storeId = chaldalId, price = 910.0 - (day * 4.2), timestamp = date))
            appDao.insertPriceHistory(PriceHistory(productId = id3, storeId = kawranBazarId, price = 880.0 - (day * 3.5), timestamp = date))
        }

        // Samsung TV
        val tvPrices = listOf(
            ProductStorePrice(productId = id4, storeId = starTechId, price = 48500.0, isAvailable = true),
            ProductStorePrice(productId = id4, storeId = ryansId, price = 49200.0, isAvailable = true),
            ProductStorePrice(productId = id4, storeId = darazId, price = 46900.0, isAvailable = true)
        )
        tvPrices.forEach { appDao.insertProductStorePrice(it) }

        // Napa Extend (Medicine)
        val napaPrices = listOf(
            ProductStorePrice(productId = id5, storeId = lazzPharmaId, price = 20.0, isAvailable = true),
            ProductStorePrice(productId = id5, storeId = shwapnoId, price = 22.0, isAvailable = true)
        )
        napaPrices.forEach { appDao.insertProductStorePrice(it) }

        // Sony Headphone
        val sonyPrices = listOf(
            ProductStorePrice(productId = id6, storeId = starTechId, price = 36800.0, isAvailable = true),
            ProductStorePrice(productId = id6, storeId = ryansId, price = 37000.0, isAvailable = true),
            ProductStorePrice(productId = id6, storeId = darazId, price = 35900.0, isAvailable = true)
        )
        sonyPrices.forEach { appDao.insertProductStorePrice(it) }

        // Insert initial notifications
        appDao.insertNotification(
            AppNotification(
                titleEn = "Welcome to BD Price Tracker!",
                titleBn = "বিডি প্রাইস ট্র্যাকার এ স্বাগতম!",
                bodyEn = "Stay updated on the best deals, lowest prices, and stock availability across Bangladesh.",
                bodyBn = "বাংলাদেশের সেরা ডিল, সর্বনিম্ন দাম এবং পণ্যের স্টক আপডেট থাকুন সহজেই।"
            )
        )
        appDao.insertNotification(
            AppNotification(
                titleEn = "Weekly Price Update: Oil Price Dropped",
                titleBn = "সাপ্তাহিক মূল্য আপডেট: তেলের দাম কমেছে",
                bodyEn = "Soyabean Oil has seen a price correction in local markets like Kawran Bazar. Check it out now!",
                bodyBn = "কাওরান বাজারের স্থানীয় বাজারে সয়াবিন তেলের আকাশচুম্বী দাম কিছুটা কমেছে। এখনই দেখুন!"
            )
        )

        // Insert default banners
        appDao.insertPromoBanner(
            PromoBanner(
                titleEn = "Latest iPhone 15 Pro Offers",
                titleBn = "আইফোন ১৫ প্রো এর সেরা অফার",
                activeProductId = id1,
                imageUrl = "iphone_banner",
                isActive = true
            )
        )
        appDao.insertPromoBanner(
            PromoBanner(
                titleEn = "Daily Essentials: Rice at Lowest Price",
                titleBn = "নিত্যপ্রয়োজনীয় সামগ্রী: সর্বনিম্ন মূল্যে চাল",
                activeProductId = id2,
                imageUrl = "rice_banner",
                isActive = true
            )
        )
    }
}
