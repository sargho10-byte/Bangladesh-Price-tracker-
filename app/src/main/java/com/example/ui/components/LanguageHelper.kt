package com.example.ui.components

object Trans {
    // Basic structural strings
    val searchPlaceholder = Pair("Search products...", "পণ্য অনুসন্ধান করুন...")
    val categories = Pair("Categories", "বিভাগসমূহ")
    val allCategories = Pair("All Categories", "সব ক্যাটাগরি")
    val currentPrice = Pair("Current Price", "বর্তমান মূল্য")
    val highestPrice = Pair("Highest Price", "সর্বোচ্চ দাম")
    val lowestPrice = Pair("Lowest Price", "সর্বনিম্ন দাম")
    val shopsCount = Pair("shops available", "টি শপে পাওয়া যাচ্ছে")
    val outOfStock = Pair("Out of Stock", "স্টক শেষ")
    val inStock = Pair("In Stock", "স্টকে আছে")
    val online = Pair("Online", "অনলাইন")
    val offline = Pair("Offline Store", "শারীরিক শপ Area")
    val address = Pair("Location / Platform", "অবস্থান / লিঙ্ক")
    
    // Detail keys
    val priceComparison = Pair("Price & Shop Comparison", "দামের তুলনা ও শপ তালিকা")
    val priceHistory = Pair("Price History Chart (Last 7 Days)", "মূল্য পরিবর্তনের চার্ট (শেষ ৭ দিন)")
    val selectStore = Pair("Select Shop/Store", "শপ/স্টোর নির্বাচন")
    val storePrice = Pair("Product Price", "পণ্যের মূল্য")
    val alertMe = Pair("Notify on Price Drop", "দাম কমলে নোটিফিকেশান দিন")
    val alertSuccess = Pair("Subscribed! We will alert you of price drops.", "সাবস্ক্রাইবড! দাম কমলেই জানিয়ে দেওয়া হবে।")
    
    // Bottom tabs
    val tabHome = Pair("Home", "হোম")
    val tabDetails = Pair("Details", "বিস্তারিত")
    val tabAlerts = Pair("Notifications", "নোটিফিকেশন")
    val tabAdmin = Pair("Admin Dashboard", "অ্যাডমিন প্যানেল")

    // Admin dashboard specific
    val adminTitle = Pair("BD Price Controls (Admin Only)", "বিডি প্রাইস কন্ট্রোল প্যানেল")
    val analyticsTab = Pair("Analytics", "বিশ্লেষণ")
    val productsTab = Pair("Products", "পণ্যসমূহ")
    val storesTab = Pair("Stores", "শপ ও স্টোর")
    val pricesTab = Pair("Prices", "মূল্য নির্ধারণ")
    val broadcastTab = Pair("Announcements", "বিজ্ঞাপন ও বার্তা")

    // Buttons
    val btnAddProduct = Pair("Add Product", "পণ্য যোগ করুন")
    val btnEditProduct = Pair("Edit Product", "পণ্য সংশোধন")
    val btnAddStore = Pair("Add Store/Shop", "স্টোর যোগ করুন")
    val btnEditStore = Pair("Edit Store", "স্টোর সংশোধন")
    val btnSavePrice = Pair("Update Price", "দাম আপডেট")
    val btnBroadcast = Pair("Broadcast Alert", "বিজ্ঞপ্তি পাঠান")
    val btnAddPromo = Pair("Add Banner Promo", "প্রোমো ব্যানার যোগ")

    // Analytics labels
    val totalProducts = Pair("Total Products", "সর্বমোট পণ্য")
    val totalStores = Pair("Total Stores/Shops", "সর্বমোট শপ")
    val activeBanners = Pair("Featured Products", "বিজ্ঞাপন ব্যানার")
    val avgPrice = Pair("Average Category prices", "ক্যাটাগরি ভিত্তিক গড় মূল্য")
    val cheapItem = Pair("Cheapest Item in App", "অ্যাপের সর্বনিম্ন মূল্যের পণ্য")
    val expItem = Pair("Most Expensive Item", "অ্যাপের সর্বোচ্চ মূল্যের পণ্য")
    
    // Form field placeholders
    val formNameEn = Pair("Product Name (English)", "পণ্যের নাম (ইংরেজী)")
    val formNameBn = Pair("Product Name (Bangla)", "পণ্যের নাম (বাংলা)")
    val formCategoryEn = Pair("Category (English)", "ক্যাটাগরি (ইংরেজী)")
    val formCategoryBn = Pair("Category (Bangla)", "ক্যাটাগরি (বাংলা)")
    val formDescEn = Pair("Description (English)", "বিবরণ (ইংরেজী)")
    val formDescBn = Pair("Description (Bangla)", "বিবরণ (বাংলা)")

    val storeNameEn = Pair("Store Name (English)", "স্টোরের নাম (ইংরেজী)")
    val storeNameBn = Pair("Store Name (Bangla)", "স্টোরের নাম (বাংলা)")
    val storeLocationEn = Pair("Location / URL (English)", "অবস্থান / লিঙ্ক (ইংরেজী)")
    val storeLocationBn = Pair("Location / URL (Bangla)", "অবস্থান / লিঙ্ক (বাংলা)")
    val storeIsOnline = Pair("Store is Online / E-commerce", "স্টোরটি অনলাইন / ই-কমার্স")

    val notifyTitleEn = Pair("Alert Title (English)", "বিজ্ঞপ্তির শিরোনাম (ইংরেজী)")
    val notifyTitleBn = Pair("Alert Title (Bangla)", "বিজ্ঞপ্তির শিরোনাম (বাংলা)")
    val notifyBodyEn = Pair("Alert Message (English)", "বিজ্ঞপ্তির বিবরণ (ইংরেজী)")
    val notifyBodyBn = Pair("Alert Message (Bangla)", "বিজ্ঞপ্তির বিবরণ (বাংলা)")

    // Helper functions
    fun t(key: Pair<String, String>, isBangla: Boolean): String {
        return if (isBangla) key.second else key.first
    }
}
