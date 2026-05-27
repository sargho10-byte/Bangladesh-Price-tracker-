package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val appDao = db.appDao()
    private val repository = AppRepository(appDao)

    // Language configuration: true for Bangla (বাংলা), false for English (English)
    private val _isBangla = MutableStateFlow(false)
    val isBangla: StateFlow<Boolean> = _isBangla.asStateFlow()

    // Admin vs User Mode configuration
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null) // Category string in English
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Selected product for Details view
    private val _selectedProductId = MutableStateFlow<Int?>(null)
    val selectedProductId: StateFlow<Int?> = _selectedProductId.asStateFlow()

    // Database Flows
    val stores: StateFlow<List<Store>> = repository.storesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<AppNotification>> = repository.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val banners: StateFlow<List<PromoBanner>> = repository.activeBannersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBanners: StateFlow<List<PromoBanner>> = repository.allBannersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val productsWithPrices: StateFlow<List<ProductWithPrices>> = repository.productsWithPricesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active screen navigation holder
    private val _currentScreen = MutableStateFlow("home") // "home", "details", "notifications", "admin"
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    init {
        viewModelScope.launch {
            // First boot initialization
            repository.prepopulateDatabaseIfEmpty()
        }
    }

    // Toggle states
    fun toggleLanguage() {
        _isBangla.value = !_isBangla.value
    }

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
        if (_isAdminMode.value) {
            _currentScreen.value = "admin"
        } else {
            _currentScreen.value = "home"
        }
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun selectProduct(productId: Int) {
        _selectedProductId.value = productId
        _currentScreen.value = "details"
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    // Active product in details flow
    val selectedProductWithPrices: StateFlow<ProductWithPrices?> = combine(
        productsWithPrices,
        _selectedProductId
    ) { products, id ->
        if (id == null) null else products.find { it.product.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active product price history flow
    val selectedProductHistory: StateFlow<List<PriceHistory>> = _selectedProductId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getPriceHistoryFlow(id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // Admin Operations
    fun addProduct(nameEn: String, nameBn: String, categoryEn: String, categoryBn: String, descriptionEn: String, descriptionBn: String) {
        viewModelScope.launch {
            repository.insertProduct(
                Product(
                    nameEn = nameEn,
                    nameBn = nameBn,
                    categoryEn = categoryEn,
                    categoryBn = categoryBn,
                    descriptionEn = descriptionEn,
                    descriptionBn = descriptionBn
                )
            )
        }
    }

    fun updateProduct(productId: Int, nameEn: String, nameBn: String, categoryEn: String, categoryBn: String, descriptionEn: String, descriptionBn: String) {
        viewModelScope.launch {
            repository.updateProduct(
                Product(
                    id = productId,
                    nameEn = nameEn,
                    nameBn = nameBn,
                    categoryEn = categoryEn,
                    categoryBn = categoryBn,
                    descriptionEn = descriptionEn,
                    descriptionBn = descriptionBn
                )
            )
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            if (_selectedProductId.value == product.id) {
                _selectedProductId.value = null
                _currentScreen.value = "home"
            }
        }
    }

    fun addStore(nameEn: String, nameBn: String, locationEn: String, locationBn: String, isOnline: Boolean) {
        viewModelScope.launch {
            repository.insertStore(
                Store(
                    nameEn = nameEn,
                    nameBn = nameBn,
                    locationEn = locationEn,
                    locationBn = locationBn,
                    isOnline = isOnline
                )
            )
        }
    }

    fun updateStore(storeId: Int, nameEn: String, nameBn: String, locationEn: String, locationBn: String, isOnline: Boolean) {
        viewModelScope.launch {
            repository.updateStore(
                Store(
                    id = storeId,
                    nameEn = nameEn,
                    nameBn = nameBn,
                    locationEn = locationEn,
                    locationBn = locationBn,
                    isOnline = isOnline
                )
            )
        }
    }

    fun deleteStore(store: Store) {
        viewModelScope.launch {
            repository.deleteStore(store)
        }
    }

    fun saveStorePrice(productId: Int, storeId: Int, price: Double, isAvailable: Boolean) {
        viewModelScope.launch {
            repository.insertPrice(
                ProductStorePrice(
                    productId = productId,
                    storeId = storeId,
                    price = price,
                    isAvailable = isAvailable
                )
            )
        }
    }

    fun deletePrice(priceId: Int) {
        viewModelScope.launch {
            repository.deletePriceById(priceId)
        }
    }

    fun broadcastMockNotification(titleEn: String, titleBn: String, bodyEn: String, bodyBn: String) {
        viewModelScope.launch {
            repository.sendNotification(titleEn, titleBn, bodyEn, bodyBn)
        }
    }

    fun deleteNotification(notificationId: Int) {
        viewModelScope.launch {
            repository.deleteNotification(notificationId)
        }
    }

    fun markNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun addBanner(titleEn: String, titleBn: String, activeProductId: Int, imageUrl: String) {
        viewModelScope.launch {
            repository.insertBanner(
                PromoBanner(
                    titleEn = titleEn,
                    titleBn = titleBn,
                    activeProductId = activeProductId,
                    imageUrl = imageUrl,
                    isActive = true
                )
            )
        }
    }

    fun deletePromoBanner(id: Int) {
        viewModelScope.launch {
            repository.deleteBanner(id)
        }
    }
}
