package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Product
import com.example.data.ProductWithPrices
import com.example.data.PromoBanner
import com.example.data.Store
import com.example.ui.AppViewModel

@Composable
fun AdminDashboardView(
    viewModel: AppViewModel,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        Trans.t(Trans.analyticsTab, isBangla),
        Trans.t(Trans.productsTab, isBangla),
        Trans.t(Trans.storesTab, isBangla),
        Trans.t(Trans.pricesTab, isBangla),
        Trans.t(Trans.broadcastTab, isBangla)
    )

    Column(modifier = modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, style = MaterialTheme.typography.titleSmall) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> AdminAnalyticsTab(viewModel, isBangla)
            1 -> AdminProductsTab(viewModel, isBangla)
            2 -> AdminStoresTab(viewModel, isBangla)
            3 -> AdminPricesTab(viewModel, isBangla)
            4 -> AdminAnnouncementsTab(viewModel, isBangla)
        }
    }
}

@Composable
fun AdminAnalyticsTab(viewModel: AppViewModel, isBangla: Boolean) {
    val products by viewModel.productsWithPrices.collectAsState()
    val stores by viewModel.stores.collectAsState()
    val banners by viewModel.allBanners.collectAsState()

    val totalProductCount = products.size
    val totalStoreCount = stores.size
    val totalBannerCount = banners.size

    // Find cheapest and most expensive
    val allPrices = products.flatMap { it.prices }.filter { it.isAvailable }
    val cheapestPrice = allPrices.minByOrNull { it.price }
    val expensivePrice = allPrices.maxByOrNull { it.price }

    val cheapestProdName = products.find { it.product.id == cheapestPrice?.productId }?.product
    val expensiveProdName = products.find { it.product.id == expensivePrice?.productId }?.product

    val cheapValueStr = if (cheapestPrice != null && cheapestProdName != null) {
        "৳${cheapestPrice.price.toInt()} (${if (isBangla) cheapestProdName.nameBn else cheapestProdName.nameEn})"
    } else "N/A"

    val expensiveValueStr = if (expensivePrice != null && expensiveProdName != null) {
        "৳${expensivePrice.price.toInt()} (${if (isBangla) expensiveProdName.nameBn else expensiveProdName.nameEn})"
    } else "N/A"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text(
                text = if (isBangla) "বিশ্লেষণ ও পারফরম্যান্স" else "Analytics & App Usage Statistics",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = Trans.t(Trans.totalProducts, isBangla),
                    value = totalProductCount.toString(),
                    subtitle = if (isBangla) "সরাসরি ডেটাবেস থেকে" else "Database active catalog",
                    icon = { Icon(Icons.Default.Category, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = Trans.t(Trans.totalStores, isBangla),
                    value = totalStoreCount.toString(),
                    subtitle = if (isBangla) "বাংলাদেশ জুড়ে শপ" else "Stores registered",
                    icon = { Icon(Icons.Default.Store, null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            StatCard(
                title = Trans.t(Trans.cheapItem, isBangla),
                value = cheapValueStr,
                subtitle = if (isBangla) "সর্বনিম্ন লাইভ মূল্য" else "Lowest recorded index",
                icon = { Icon(Icons.Default.ArrowDownward, null, tint = Color(0xFF2E7D32)) },
                cardColor = Color(0xFFE8F5E9).copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            StatCard(
                title = Trans.t(Trans.expItem, isBangla),
                value = expensiveValueStr,
                subtitle = if (isBangla) "সর্বোচ্চ লাইভ সূচক" else "Highest recorded index",
                icon = { Icon(Icons.Default.ArrowUpward, null, tint = Color(0xFFC62828)) },
                cardColor = Color(0xFFFFEBEE).copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) "ক্যাটাগরি ডেটা বন্টন" else "Category Database Stats",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val categories = listOf("Electronics", "Grocery", "Fashion", "Home Appliances", "Medicine")
                    categories.forEach { cat ->
                        val count = products.count { it.product.categoryEn == cat }
                        val pct = if (totalProductCount > 0) count.toFloat() / totalProductCount else 0f
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isBangla) {
                                        when (cat) {
                                            "Electronics" -> "ইলেকট্রনিক্স"
                                            "Grocery" -> "গ্রোসারি প্রয়োজনীয়"
                                            "Fashion" -> "ফ্যাশন ও পোশাক"
                                            "Home Appliances" -> "গৃহস্থালী সরঞ্জাম"
                                            "Medicine" -> "ঔষধপত্র"
                                            else -> "অন্যান্য"
                                        }
                                    } else cat,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "$count " + (if (isBangla) "টি আইটেম" else "items"),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { pct },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductsTab(viewModel: AppViewModel, isBangla: Boolean) {
    val products by viewModel.productsWithPrices.collectAsState()

    // Add dialog state
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    var nameEn by remember { mutableStateOf("") }
    var nameBn by remember { mutableStateOf("") }
    var categoryEn by remember { mutableStateOf("Electronics") }
    var categoryBn by remember { mutableStateOf("ইলেকট্রনিক্স") }
    var descEn by remember { mutableStateOf("") }
    var descBn by remember { mutableStateOf("") }

    val categoriesList = listOf(
        "Electronics" to "ইলেকট্রনিক্স",
        "Grocery" to "নিত্যপ্রয়োজনীয় দ্রব্য",
        "Fashion" to "ফ্যাশন ও পোশাক",
        "Home Appliances" to "গৃহস্থালী সরঞ্জাম",
        "Medicine" to "ঔষধপত্র"
    )

    if (showAddDialog || editingProduct != null) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingProduct = null
            },
            title = {
                Text(
                    text = if (editingProduct != null) {
                        Trans.t(Trans.btnEditProduct, isBangla)
                    } else {
                        Trans.t(Trans.btnAddProduct, isBangla)
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nameEn,
                        onValueChange = { nameEn = it },
                        label = { Text(Trans.t(Trans.formNameEn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nameBn,
                        onValueChange = { nameBn = it },
                        label = { Text(Trans.t(Trans.formNameBn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Category Selector using Popups or Simple Selection
                    Text(
                        text = if (isBangla) "ক্যাটাগরি নির্বাচন করুন:" else "Choose Category:",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categoriesList.forEach { pair ->
                            val isSelected = categoryEn == pair.first
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    categoryEn = pair.first
                                    categoryBn = pair.second
                                },
                                label = { Text(if (isBangla) pair.second else pair.first) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = descEn,
                        onValueChange = { descEn = it },
                        label = { Text(Trans.t(Trans.formDescEn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = descBn,
                        onValueChange = { descBn = it },
                        label = { Text(Trans.t(Trans.formDescBn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val prod = editingProduct
                        if (prod != null) {
                            viewModel.updateProduct(
                                prod.id, nameEn, nameBn, categoryEn, categoryBn, descEn, descBn
                            )
                        } else {
                            viewModel.addProduct(
                                nameEn, nameBn, categoryEn, categoryBn, descEn, descBn
                            )
                        }
                        showAddDialog = false
                        editingProduct = null
                    }
                ) {
                    Text(if (isBangla) "সংরক্ষণ" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    editingProduct = null
                }) {
                    Text(if (isBangla) "ঘোষণা বাতিল" else "Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = {
                nameEn = ""
                nameBn = ""
                categoryEn = "Electronics"
                categoryBn = "ইলেকট্রনিক্স"
                descEn = ""
                descBn = ""
                showAddDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(Trans.t(Trans.btnAddProduct, isBangla))
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(products) { prodItem ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBangla) prodItem.product.categoryBn else prodItem.product.categoryEn,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (isBangla) prodItem.product.nameBn else prodItem.product.nameEn,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isBangla) prodItem.product.descriptionBn else prodItem.product.descriptionEn,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1
                            )
                        }

                        Row {
                            IconButton(onClick = {
                                val p = prodItem.product
                                nameEn = p.nameEn
                                nameBn = p.nameBn
                                categoryEn = p.categoryEn
                                categoryBn = p.categoryBn
                                descEn = p.descriptionEn
                                descBn = p.descriptionBn
                                editingProduct = p
                            }) {
                                Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = {
                                viewModel.deleteProduct(prodItem.product)
                            }) {
                                Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStoresTab(viewModel: AppViewModel, isBangla: Boolean) {
    val stores by viewModel.stores.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingStore by remember { mutableStateOf<Store?>(null) }

    var nameEn by remember { mutableStateOf("") }
    var nameBn by remember { mutableStateOf("") }
    var locationEn by remember { mutableStateOf("") }
    var locationBn by remember { mutableStateOf("") }
    var isOnline by remember { mutableStateOf(false) }

    if (showAddDialog || editingStore != null) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingStore = null
            },
            title = {
                Text(
                    text = if (editingStore != null) {
                        Trans.t(Trans.btnEditStore, isBangla)
                    } else {
                        Trans.t(Trans.btnAddStore, isBangla)
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nameEn,
                        onValueChange = { nameEn = it },
                        label = { Text(Trans.t(Trans.storeNameEn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nameBn,
                        onValueChange = { nameBn = it },
                        label = { Text(Trans.t(Trans.storeNameBn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = locationEn,
                        onValueChange = { locationEn = it },
                        label = { Text(Trans.t(Trans.storeLocationEn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = locationBn,
                        onValueChange = { locationBn = it },
                        label = { Text(Trans.t(Trans.storeLocationBn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isOnline, onCheckedChange = { isOnline = it })
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(Trans.t(Trans.storeIsOnline, isBangla), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val str = editingStore
                        if (str != null) {
                            viewModel.updateStore(
                                str.id, nameEn, nameBn, locationEn, locationBn, isOnline
                            )
                        } else {
                            viewModel.addStore(
                                nameEn, nameBn, locationEn, locationBn, isOnline
                            )
                        }
                        showAddDialog = false
                        editingStore = null
                    }
                ) {
                    Text(if (isBangla) "সংরক্ষণ" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    editingStore = null
                }) {
                    Text(if (isBangla) "খেলুন না" else "Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = {
                nameEn = ""
                nameBn = ""
                locationEn = ""
                locationBn = ""
                isOnline = false
                showAddDialog = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(Trans.t(Trans.btnAddStore, isBangla))
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(stores) { store ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBangla) store.nameBn else store.nameEn,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (store.isOnline) Color(0xFFE3F2FD) else Color(0xFFF1F1F1),
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (store.isOnline) "ONLINE" else "PHYSICAL",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (store.isOnline) Color(0xFF0D47A1) else Color.DarkGray
                                    )
                                }
                            }
                            Text(
                                text = if (isBangla) store.locationBn else store.locationEn,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Row {
                            IconButton(onClick = {
                                nameEn = store.nameEn
                                nameBn = store.nameBn
                                locationEn = store.locationEn
                                locationBn = store.locationBn
                                isOnline = store.isOnline
                                editingStore = store
                            }) {
                                Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = {
                                viewModel.deleteStore(store)
                            }) {
                                Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPricesTab(viewModel: AppViewModel, isBangla: Boolean) {
    val productsWithPrices by viewModel.productsWithPrices.collectAsState()
    val stores by viewModel.stores.collectAsState()

    var selectedProduct by remember { mutableStateOf<ProductWithPrices?>(null) }
    var selectedStore by remember { mutableStateOf<Store?>(null) }
    var priceStr by remember { mutableStateOf("") }
    var isAvailable by remember { mutableStateOf(true) }

    // Dropdown expanding helpers
    var prodDropdownExpanded by remember { mutableStateOf(false) }
    var storeDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text(
                text = if (isBangla) "পণ্য ও শপের মূল্য নির্ধারণ" else "Assign Pricing to Stores",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Product Selector Dropdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isBangla) "পণ্য সিলেক্ট করুন:" else "Select Product:",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Button(
                        onClick = { prodDropdownExpanded = !prodDropdownExpanded },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Text(
                            text = if (selectedProduct != null) {
                                if (isBangla) selectedProduct!!.product.nameBn else selectedProduct!!.product.nameEn
                            } else {
                                if (isBangla) "পণ্য নির্বাচন করুন" else "Choose a product..."
                            }
                        )
                    }

                    if (prodDropdownExpanded) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .verticalScroll(rememberScrollState()),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            productsWithPrices.forEach { item ->
                                val text = if (isBangla) item.product.nameBn else item.product.nameEn
                                Text(
                                    text = text,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedProduct = item
                                            prodDropdownExpanded = false
                                        }
                                        .padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }

        // Store Selector Dropdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isBangla) "শপ / স্টোর সিলেক্ট করুন:" else "Select Boutique/Store:",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Button(
                        onClick = { storeDropdownExpanded = !storeDropdownExpanded },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Text(
                            text = if (selectedStore != null) {
                                if (isBangla) selectedStore!!.nameBn else selectedStore!!.nameEn
                            } else {
                                if (isBangla) "শপ নির্বাচন করুন" else "Choose a store..."
                            }
                        )
                    }

                    if (storeDropdownExpanded) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .verticalScroll(rememberScrollState()),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            stores.forEach { item ->
                                val text = if (isBangla) item.nameBn else item.nameEn
                                Text(
                                    text = text,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedStore = item
                                            storeDropdownExpanded = false
                                        }
                                        .padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }

        // Price input and status toggles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text(Trans.t(Trans.storePrice, isBangla) + " (৳)") },
                    modifier = Modifier.weight(1f)
                )

                Row(
                    modifier = Modifier.weight(0.9f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isAvailable, onCheckedChange = { isAvailable = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBangla) "স্টকে আছে" else "In Stock",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Update Button
        item {
            Button(
                onClick = {
                    val prod = selectedProduct
                    val store = selectedStore
                    val priceVal = priceStr.toDoubleOrNull()
                    if (prod != null && store != null && priceVal != null) {
                        viewModel.saveStorePrice(
                            productId = prod.product.id,
                            storeId = store.id,
                            price = priceVal,
                            isAvailable = isAvailable
                        )
                        priceStr = ""
                    }
                },
                enabled = selectedProduct != null && selectedStore != null && priceStr.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(Trans.t(Trans.btnSavePrice, isBangla))
            }
        }

        // List assigned prices for selected product
        item {
            if (selectedProduct != null) {
                Text(
                    text = (if (isBangla) "বিদ্যমান মূল্যসমূহ: " else "Existing Pricings: ") + (if (isBangla) selectedProduct!!.product.nameBn else selectedProduct!!.product.nameEn),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 12.dp)
                )

                if (selectedProduct!!.prices.isEmpty()) {
                    Text(
                        text = if (isBangla) "কোনো মূল্য বিবরণ যুক্ত করা হয়নি" else "No shop prices added yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    selectedProduct!!.prices.forEach { prWithSt ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isBangla) prWithSt.storeNameBn else prWithSt.storeNameEn,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = "৳${prWithSt.price.toInt()} - " + (if (prWithSt.isAvailable) (if (isBangla) "স্টকে আছে" else "Available") else (if (isBangla) "স্টক শেষ" else "No stock")),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (prWithSt.isAvailable) Color(0xFF2E7D32) else Color.Red
                                    )
                                }

                                IconButton(onClick = { viewModel.deletePrice(prWithSt.priceId) }) {
                                    Icon(Icons.Default.Delete, "Delete Price", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAnnouncementsTab(viewModel: AppViewModel, isBangla: Boolean) {
    val productsWithPrices by viewModel.productsWithPrices.collectAsState()

    var notifyTitleEnStr by remember { mutableStateOf("") }
    var notifyTitleBnStr by remember { mutableStateOf("") }
    var notifyBodyEnStr by remember { mutableStateOf("") }
    var notifyBodyBnStr by remember { mutableStateOf("") }

    var selectedPromoProduct by remember { mutableStateOf<ProductWithPrices?>(null) }
    var bannerHeadlineEn by remember { mutableStateOf("") }
    var bannerHeadlineBn by remember { mutableStateOf("") }
    var bannerDropdownExpanded by remember { mutableStateOf(false) }

    val allBanners by viewModel.allBanners.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Push Notification Panel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) "পুশ বিজ্ঞপ্তি পাঠান" else "Broadcast Notification",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = notifyTitleEnStr,
                        onValueChange = { notifyTitleEnStr = it },
                        label = { Text(Trans.t(Trans.notifyTitleEn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notifyTitleBnStr,
                        onValueChange = { notifyTitleBnStr = it },
                        label = { Text(Trans.t(Trans.notifyTitleBn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notifyBodyEnStr,
                        onValueChange = { notifyBodyEnStr = it },
                        label = { Text(Trans.t(Trans.notifyBodyEn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notifyBodyBnStr,
                        onValueChange = { notifyBodyBnStr = it },
                        label = { Text(Trans.t(Trans.notifyBodyBn, isBangla)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (notifyTitleEnStr.isNotBlank() && notifyTitleBnStr.isNotBlank()) {
                                viewModel.broadcastMockNotification(
                                    titleEn = notifyTitleEnStr,
                                    titleBn = notifyTitleBnStr,
                                    bodyEn = notifyBodyEnStr,
                                    bodyBn = notifyBodyBnStr
                                )
                                notifyTitleEnStr = ""
                                notifyTitleBnStr = ""
                                notifyBodyEnStr = ""
                                notifyBodyBnStr = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(Trans.t(Trans.btnBroadcast, isBangla))
                    }
                }
            }
        }

        // Advertisement / Banners Panel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBangla) "হোম স্ক্রিন প্রোমো ব্যানার" else "Configure Home Promo Banners",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isBangla) "লিঙ্ক করা পণ্য:" else "Linked Product:",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Button(
                        onClick = { bannerDropdownExpanded = !bannerDropdownExpanded },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Text(
                            text = if (selectedPromoProduct != null) {
                                if (isBangla) selectedPromoProduct!!.product.nameBn else selectedPromoProduct!!.product.nameEn
                            } else {
                                if (isBangla) "পণ্য নির্বাচন করুন" else "Select link item..."
                            }
                        )
                    }

                    if (bannerDropdownExpanded) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 160.dp)
                                .verticalScroll(rememberScrollState()),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            productsWithPrices.forEach { item ->
                                Text(
                                    text = if (isBangla) item.product.nameBn else item.product.nameEn,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedPromoProduct = item
                                            bannerDropdownExpanded = false
                                        }
                                        .padding(12.dp)
                                )
                                HorizontalDivider()
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = bannerHeadlineEn,
                        onValueChange = { bannerHeadlineEn = it },
                        label = { Text(if (isBangla) "হেডলাইন (ইংরেজী)" else "Headline (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = bannerHeadlineBn,
                        onValueChange = { bannerHeadlineBn = it },
                        label = { Text(if (isBangla) "হেডলাইন (বাংলা)" else "Headline (Bangla)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val prod = selectedPromoProduct
                            if (prod != null && bannerHeadlineEn.isNotBlank() && bannerHeadlineBn.isNotBlank()) {
                                viewModel.addBanner(
                                    titleEn = bannerHeadlineEn,
                                    titleBn = bannerHeadlineBn,
                                    activeProductId = prod.product.id,
                                    imageUrl = "custom"
                                )
                                bannerHeadlineEn = ""
                                bannerHeadlineBn = ""
                                selectedPromoProduct = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(Trans.t(Trans.btnAddPromo, isBangla))
                    }
                }
            }
        }

        // Existing Banners List
        item {
            Text(
                text = if (isBangla) "বিদ্যমান বিজ্ঞাপন ব্যানারসমূহ" else "Active Promo Advertisements",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        items(allBanners) { banner ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBangla) banner.titleBn else banner.titleEn,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = if (isBangla) "লিঙ্ক করা পণ্য আইডি: ${banner.activeProductId}" else "Linked product ID: ${banner.activeProductId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { viewModel.deletePromoBanner(banner.id) }) {
                        Icon(Icons.Default.Delete, "Delete Banner", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
