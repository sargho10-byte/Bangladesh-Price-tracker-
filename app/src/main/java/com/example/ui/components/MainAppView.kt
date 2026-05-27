package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PriceWithStore
import com.example.ui.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppView(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBangla by viewModel.isBangla.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    // Data streams
    val productsWithPrices by viewModel.productsWithPrices.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val banners by viewModel.banners.collectAsState()

    // Unread count
    val unreadNotificationsCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "বিডি প্রাইস ট্র্যাকার" else "BD Price Tracker",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Language Toggle
                            TextButton(
                                onClick = { viewModel.toggleLanguage() },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(
                                    text = if (isBangla) "English" else "বাংলা",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            // Notification Mail Icon
                            IconButton(onClick = { viewModel.setScreen("notifications") }) {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotificationsCount > 0) {
                                            Badge { Text(unreadNotificationsCount.toString()) }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Notifications,
                                        contentDescription = "Alerts"
                                    )
                                }
                            }

                            // Secure Admin Toggle Switch
                            IconButton(
                                onClick = {
                                    viewModel.toggleAdminMode()
                                    Toast.makeText(
                                        context,
                                        if (!isAdminMode) {
                                            if (isBangla) "অ্যাডমিন প্যানেলে প্রবেশ করা হয়েছে" else "Logged into Secure Admin System!"
                                        } else {
                                            if (isBangla) "অ্যাডমিন প্যানেল বন্ধ করা হয়েছে" else "Exited Admin Panel"
                                        },
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            ) {
                                Icon(
                                    imageVector = if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Default.LockOpen,
                                    contentDescription = "Admin Mode Toggle",
                                    tint = if (isAdminMode) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
            ) {
                // Home Tab
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = { viewModel.setScreen("home") },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text(Trans.t(Trans.tabHome, isBangla)) }
                )

                // Details Tab (linked node)
                NavigationBarItem(
                    selected = currentScreen == "details",
                    onClick = {
                        viewModel.setScreen("details")
                    },
                    enabled = true,
                    icon = { Icon(Icons.Default.Info, null) },
                    label = { Text(Trans.t(Trans.tabDetails, isBangla)) }
                )

                // Notifications Inbox Tab
                NavigationBarItem(
                    selected = currentScreen == "notifications",
                    onClick = { viewModel.setScreen("notifications") },
                    icon = { Icon(Icons.Default.Notifications, null) },
                    label = { Text(Trans.t(Trans.tabAlerts, isBangla)) }
                )

                // Admin Tab (Visible only design options or if switched)
                if (isAdminMode) {
                    NavigationBarItem(
                        selected = currentScreen == "admin",
                        onClick = { viewModel.setScreen("admin") },
                        icon = { Icon(Icons.Default.AdminPanelSettings, null) },
                        label = { Text(Trans.t(Trans.tabAdmin, isBangla)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.error,
                            selectedTextColor = MaterialTheme.colorScheme.error,
                            indicatorColor = MaterialTheme.colorScheme.errorContainer
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenSwitch"
            ) { screen ->
                when (screen) {
                    "home" -> HomeScreenView(
                        viewModel = viewModel,
                        isBangla = isBangla
                    )
                    "details" -> ProductDetailsScreenView(
                        viewModel = viewModel,
                        isBangla = isBangla,
                        onBack = { viewModel.setScreen("home") }
                    )
                    "notifications" -> NotificationsScreenView(
                        viewModel = viewModel,
                        isBangla = isBangla
                    )
                    "admin" -> {
                        if (isAdminMode) {
                            AdminDashboardView(
                                viewModel = viewModel,
                                isBangla = isBangla
                            )
                        } else {
                            // Friendly fallback warning if admin is turned off
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isBangla) "অ্যাডমিন অ্যাক্সেস লক করা আছে।" else "Secure Admin Access is locked.",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenView(
    viewModel: AppViewModel,
    isBangla: Boolean
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val productsWithPrices by viewModel.productsWithPrices.collectAsState()
    val banners by viewModel.banners.collectAsState()

    // Extract dynamic category lists from products database to display as filters!
    // Pair of (EnName, BnName)
    val defaultCategories = listOf(
        "Electronics" to "ইলেকট্রনিক্স",
        "Grocery" to "নিত্যপ্রয়োজনীয় দ্রব্য",
        "Fashion" to "ফ্যাশন ও পোশাক",
        "Home Appliances" to "গৃহস্থালী সরঞ্জাম",
        "Medicine" to "ঔষধপত্র"
    )

    // Filter products
    val filteredProducts = productsWithPrices.filter { withPrice ->
        val p = withPrice.product
        val matchesSearch = p.nameEn.contains(searchQuery, ignoreCase = true) ||
                p.nameBn.contains(searchQuery, ignoreCase = true) ||
                p.categoryEn.contains(searchQuery, ignoreCase = true) ||
                p.categoryBn.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == null || p.categoryEn == selectedCategory
        matchesSearch && matchesCategory
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        // Advertisement Promo slide show
        if (banners.isNotEmpty()) {
            item {
                PromoBannerSlider(
                    banners = banners,
                    isBangla = isBangla,
                    onBannerClick = { productId ->
                        viewModel.selectProduct(productId)
                    }
                )
            }
        }

        // Search Bar container
        item {
            PaddingValues(horizontal = 16.dp).let {
                Box(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text(Trans.t(Trans.searchPlaceholder, isBangla)) },
                        leadingIcon = { Icon(Icons.Default.Search, "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, "Clear")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Category selections Horizontal Scroller
        item {
            CategoryFilterChips(
                categories = defaultCategories,
                selectedCategory = selectedCategory,
                isBangla = isBangla,
                onCategorySelected = { viewModel.selectCategory(it) }
            )
        }

        // List Header or Count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "পণ্যসমূহের তালিকা" else "Tracked Products",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${filteredProducts.size} " + (if (isBangla) "টি পাওয়া গেছে" else "found"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Render Cards
        if (filteredProducts.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "Empty list",
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isBangla) "কোনো পণ্য পাওয়া যায়নি" else "No products found matching filters.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredProducts) { item ->
                ProductGridCard(
                    productWithPrices = item,
                    isBangla = isBangla,
                    onClick = {
                        viewModel.selectProduct(item.product.id)
                    }
                )
            }
        }
    }
}

@Composable
fun ProductDetailsScreenView(
    viewModel: AppViewModel,
    isBangla: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val itemWithPrices by viewModel.selectedProductWithPrices.collectAsState()
    val historyList by viewModel.selectedProductHistory.collectAsState()

    if (itemWithPrices == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Info, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isBangla) "অনুগ্রহ করে হোম স্ক্রিন থেকে তথ্য দেখতে একটি পণ্য সিলেক্ট করুন।" else "Please select a product from Home to view details.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(24.dp)
                )
                Button(onClick = onBack) {
                    Text(if (isBangla) "হোমে ফিরে যান" else "Back to Home")
                }
            }
        }
        return
    }

    val product = itemWithPrices!!.product
    val prices = itemWithPrices!!.prices

    // Sorted prices to highlight lowest and highest with gorgeous colors
    val sortedPriceList = prices.sortedBy { it.price }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Navigation Back bar
        item {
            IconButton(onClick = onBack, modifier = Modifier.padding(top = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBangla) "ফিরুন" else "Home", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Product description detail box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) product.categoryBn else product.categoryEn,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBangla) product.nameBn else product.nameEn,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isBangla) product.descriptionBn else product.descriptionEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                    )
                }
            }
        }

        // Dynamic price alerts trigger
        item {
            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        Trans.t(Trans.alertSuccess, isBangla),
                        Toast.LENGTH_LONG
                    ).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.NotificationsActive, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(Trans.t(Trans.alertMe, isBangla))
            }
        }

        // Price comparison table header
        item {
            Text(
                text = Trans.t(Trans.priceComparison, isBangla),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Table List
        if (prices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = if (isBangla) "দুঃখিত, কোনো শপে এই পণ্যটির দাম এখনো নির্ধারণ করা হয়নি।" else "No pricing points added for this item yet.",
                        modifier = Modifier.padding(20.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(sortedPriceList) { priceSt ->
                val isCheapest = priceSt.priceId == sortedPriceList.firstOrNull { it.isAvailable }?.priceId
                val isMostExpensive = priceSt.priceId == sortedPriceList.lastOrNull { it.isAvailable }?.priceId

                val cardColor = when {
                    isCheapest -> Color(0xFFE8F5E9)      // green highlights
                    isMostExpensive -> Color(0xFFFFEBEE) // red highlights
                    else -> MaterialTheme.colorScheme.surface
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.2f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBangla) priceSt.storeNameBn else priceSt.storeNameEn,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (priceSt.isOnline) Color(0xFFBBDEFB) else Color(0xFFE0E0E0),
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = Trans.t(if (priceSt.isOnline) Trans.online else Trans.offline, isBangla),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                                        color = Color.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBangla) priceSt.storeLocationBn else priceSt.storeLocationEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
                            val dateStr = sdf.format(Date(priceSt.lastUpdated))
                            Text(
                                text = (if (isBangla) "আপডেট: $dateStr" else "Updated: $dateStr"),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column(
                            modifier = Modifier.weight(0.8f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "৳${priceSt.price.toInt()}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (isCheapest) Color(0xFF1B5E20) else if (isMostExpensive) Color(0xFFB71C1C) else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (priceSt.isAvailable) Color(0xFFC8E6C9) else Color(0xFFFFCDD2),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                Text(
                                    text = Trans.t(if (priceSt.isAvailable) Trans.inStock else Trans.outOfStock, isBangla),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = if (priceSt.isAvailable) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Custom drawn history chart
        item {
            Spacer(modifier = Modifier.height(10.dp))
            PriceHistoryChart(
                historyList = historyList,
                isBangla = isBangla
            )
        }
    }
}

@Composable
fun NotificationsScreenView(
    viewModel: AppViewModel,
    isBangla: Boolean
) {
    val list by viewModel.notifications.collectAsState()

    // Mark all as read upon opening
    LaunchedEffect(Unit) {
        viewModel.markNotificationsAsRead()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isBangla) "বার্তা ও মূল্য সতর্কতা ইনবক্স" else "Message & Alert Inbox",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "${list.size} notifications",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        if (list.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NotificationsOff,
                        contentDescription = "Inbox safe",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isBangla) "আপনার ইনবক্সটি সম্পূর্ণ খালি রয়েছে।" else "You don't have any push notifications or updates.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(list) { alert ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (alert.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Bullet dot for unread status
                                    if (!alert.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(MaterialTheme.colorScheme.error, shape = RoundedCornerShape(50))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }

                                    Text(
                                        text = if (isBangla) alert.titleBn else alert.titleEn,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteNotification(alert.id) }) {
                                    Icon(Icons.Default.Clear, "Dismiss Alert", Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBangla) alert.bodyBn else alert.bodyEn,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                            val alertDate = sdf.format(Date(alert.timestamp))
                            Text(
                                text = alertDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}
