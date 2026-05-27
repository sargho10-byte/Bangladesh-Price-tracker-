package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PriceHistory
import com.example.data.ProductWithPrices
import com.example.data.PromoBanner
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Card(
        modifier = modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CategoryFilterChips(
    categories: List<Pair<String, String>>, // Pair(EnName, BnName)
    selectedCategory: String?,
    isBangla: Boolean,
    onCategorySelected: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text(Trans.t(Trans.allCategories, isBangla)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.GridView,
                        contentDescription = "All",
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
        items(categories) { cat ->
            val isSelected = selectedCategory == cat.first
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(cat.first) },
                label = { Text(if (isBangla) cat.second else cat.first) },
                leadingIcon = {
                    val icon = when (cat.first) {
                        "Electronics" -> Icons.Default.LaptopMac
                        "Grocery" -> Icons.Default.ShoppingBasket
                        "Fashion" -> Icons.Default.Checkroom
                        "Medicine" -> Icons.Default.MedicalServices
                        "Home Appliances" -> Icons.Default.Kitchen
                        else -> Icons.Default.LocalMall
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = cat.first,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun PromoBannerSlider(
    banners: List<PromoBanner>,
    isBangla: Boolean,
    onBannerClick: (Int) -> Unit
) {
    if (banners.isEmpty()) return

    var currentBannerIndex by remember { mutableStateOf(0) }

    // Auto rotate mock banner effect
    LaunchedEffect(banners) {
        while (banners.size > 1) {
            kotlinx.coroutines.delay(4000)
            currentBannerIndex = (currentBannerIndex + 1) % banners.size
        }
    }

    val banner = banners.getOrNull(currentBannerIndex) ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onBannerClick(banner.activeProductId) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background visual pattern
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                drawCircle(
                    color = Color.White.copy(alpha = 0.15f),
                    radius = height * 0.8f,
                    center = Offset(width * 0.85f, height * 0.2f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = height * 1.3f,
                    center = Offset(width * 0.9f, height * 0.5f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isBangla) "ফিচারড ডিল" else "FEATURED DEAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isBangla) banner.titleBn else banner.titleEn,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    val bannerIcon = when (banner.imageUrl) {
                        "iphone_banner" -> Icons.Outlined.Smartphone
                        "rice_banner" -> Icons.Outlined.Eco
                        else -> Icons.Outlined.LocalActivity
                    }
                    Icon(
                        imageVector = bannerIcon,
                        contentDescription = null,
                        modifier = Modifier.size(68.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                }
            }

            // Indicator dots
            if (banners.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    banners.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentBannerIndex) 12.dp else 6.dp, 6.dp)
                                .background(
                                    if (index == currentBannerIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.3f
                                    ),
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductGridCard(
    productWithPrices: ProductWithPrices,
    isBangla: Boolean,
    onClick: () -> Unit
) {
    val lowest = productWithPrices.getLowestAvailablePrice()
    val highest = productWithPrices.getHighestAvailablePrice()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Category Tag
                    Text(
                        text = if (isBangla) productWithPrices.product.categoryBn else productWithPrices.product.categoryEn,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBangla) productWithPrices.product.nameBn else productWithPrices.product.nameEn,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price Indicators row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cheapest
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9) // Light Green
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = Trans.t(Trans.lowestPrice, isBangla),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1B5E20)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lowest != null) {
                                if (isBangla) "৳${lowest.price.toInt()}" else "৳${lowest.price.toInt()}"
                            } else "N/A",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = if (lowest != null) {
                                if (isBangla) lowest.storeNameBn else lowest.storeNameEn
                            } else "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1B5E20).copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Highest
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE) // Light Red
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = Trans.t(Trans.highestPrice, isBangla),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFB71C1C)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (highest != null) {
                                "৳${highest.price.toInt()}"
                            } else "N/A",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFB71C1C)
                        )
                        Text(
                            text = if (highest != null) {
                                if (isBangla) highest.storeNameBn else highest.storeNameEn
                            } else "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB71C1C).copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Overall Availability
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Number of shops offering this product
                val shopCount = productWithPrices.prices.size
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$shopCount " + Trans.t(Trans.shopsCount, isBangla),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // In stock status chip
                val anyInStock = productWithPrices.prices.any { it.isAvailable }
                Box(
                    modifier = Modifier
                        .background(
                            if (anyInStock) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = Trans.t(if (anyInStock) Trans.inStock else Trans.outOfStock, isBangla),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (anyInStock) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

@Composable
fun PriceHistoryChart(
    historyList: List<PriceHistory>,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    if (historyList.isEmpty()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.SsidChart,
                        contentDescription = "No data",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isBangla) "মূল্য পরিবর্তনের কোনো ইতিহাস নেই" else "No price history available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
        return
    }

    // Sort to ensure sequential timestamps
    val now = System.currentTimeMillis()
    val sortedHistory = historyList.sortedBy { it.timestamp }.takeLast(10)
    val maxPrice = sortedHistory.maxOfOrNull { it.price } ?: 1.0
    val minPrice = sortedHistory.minOfOrNull { it.price } ?: 0.0
    val priceDiff = if (maxPrice == minPrice) 10.0 else maxPrice - minPrice
    
    // Add extra padding range at top and bottom of chart for aesthetics
    val topTarget = maxPrice + (priceDiff * 0.15)
    val bottomTarget = if (minPrice - (priceDiff * 0.15) < 0) 0.0 else minPrice - (priceDiff * 0.15)
    val chartRange = if (topTarget == bottomTarget) 1.0 else topTarget - bottomTarget

    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = Trans.t(Trans.priceHistory, isBangla),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    // Draw grid lines
                    val gridLines = 3
                    for (i in 0..gridLines) {
                        val y = (height / gridLines) * i
                        drawLine(
                            color = outlineColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    if (sortedHistory.size > 1) {
                        val points = sortedHistory.mapIndexed { index, hist ->
                            val xPos = (width / (sortedHistory.size - 1)) * index
                            val relativeVal = (hist.price - bottomTarget) / chartRange
                            val yPos = height - (height * relativeVal).toFloat()
                            Offset(xPos, yPos)
                        }

                        // Create clean curved path with Cubic Bezier or line segments
                        val path = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val current = points[i]
                                // Bezier spline controls for smooth visuals
                                cubicTo(
                                    x1 = (prev.x + current.x) / 2f,
                                    y1 = prev.y,
                                    x2 = (prev.x + current.x) / 2f,
                                    y2 = current.y,
                                    x3 = current.x,
                                    y3 = current.y
                                )
                            }
                        }

                        // Dynamic brush/gradient filling below the line
                        val fillPath = Path().apply {
                            addPath(path)
                            lineTo(points.last().x, height)
                            lineTo(points.first().x, height)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(primaryColor.copy(alpha = 0.35f), Color.Transparent),
                                startY = points.minOf { it.y },
                                endY = height
                            )
                        )

                        // Draw main line path
                        drawPath(
                            path = path,
                            color = primaryColor,
                            style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        // Draw node circles and text values for points
                        points.forEachIndexed { i, pt ->
                            drawCircle(
                                color = Color.White,
                                radius = 7f,
                                center = pt
                            )
                            drawCircle(
                                color = primaryColor,
                                radius = 4f,
                                center = pt
                            )
                        }
                    }
                }

                // Inline Labels for Prices
                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "৳${maxPrice.toInt()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "৳${minPrice.toInt()}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Timeline indicators at the bottom
                Row(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
                    val firstDate = sdf.format(Date(sortedHistory.firstOrNull()?.timestamp ?: now))
                    val lastDate = sdf.format(Date(sortedHistory.lastOrNull()?.timestamp ?: now))

                    Text(
                        text = firstDate,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Text(
                        text = if (isBangla) "মূল্য পরিবর্তন গতিবিধি" else "Price Shift Trend",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    Text(
                        text = lastDate,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
