package com.example.pawelierapp.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawelierapp.R

// Font definition
val GreatVibes = FontFamily(Font(R.font.great_vibes_regular))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSignOut: () -> Unit = {},
    isDarkMode: Boolean = false,
    onToggleDarkMode: () -> Unit = {}
) {
    var currentPage by rememberSaveable { mutableStateOf("HOME") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    when (currentPage) {
        "HOME" -> Scaffold(
            topBar = { ModernHomeTopBar { selected -> currentPage = selected } },
            bottomBar = { ModernBottomBar(selected = currentPage, onSelect = { currentPage = it }) },
            containerColor = MaterialTheme.colorScheme.background
        ) { inner ->
            Column(
                Modifier
                    .padding(inner)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Section with Gradient
                ModernHeroSection(onShopNow = { currentPage = "WEAR" })

                Spacer(Modifier.height(24.dp))

                // Featured Categories Section
                FeaturedCategoriesSection(onCategoryClick = { currentPage = it })

                Spacer(Modifier.height(24.dp))

                // Why Choose Us Section
                WhyChooseUsSection()

                Spacer(Modifier.height(24.dp))
            }
        }

        "WEAR" -> ProductListScreen(
            category = "Pet Wear",
            products = ProductData.wearProducts,
            onBackClick = { currentPage = "HOME" },
            onProductClick = { product ->
                selectedProduct = product
                currentPage = "PRODUCT_DETAIL"
            },
            onSelectBottom = { currentPage = it }
        )

        "WALK" -> ProductListScreen(
            category = "Walk Essentials",
            products = ProductData.walkProducts,
            onBackClick = { currentPage = "HOME" },
            onProductClick = { product ->
                selectedProduct = product
                currentPage = "PRODUCT_DETAIL"
            },
            onSelectBottom = { currentPage = it }
        )

        "LIVING" -> ProductListScreen(
            category = "Living Comfort",
            products = ProductData.livingProducts,
            onBackClick = { currentPage = "HOME" },
            onProductClick = { product ->
                selectedProduct = product
                currentPage = "PRODUCT_DETAIL"
            },
            onSelectBottom = { currentPage = it }
        )

        "TRAVEL" -> ProductListScreen(
            category = "Travel Gear",
            products = ProductData.travelProducts,
            onBackClick = { currentPage = "HOME" },
            onProductClick = { product ->
                selectedProduct = product
                currentPage = "PRODUCT_DETAIL"
            },
            onSelectBottom = { currentPage = it }
        )

        "PRODUCT_DETAIL" -> selectedProduct?.let { product ->
            ProductDetailScreen(
                product = product,
                onBackClick = { currentPage = product.category },
                onAddToCart = {
                    currentPage = "HOME"
                }
            )
        }

        "CART" -> {
            // Calculate cart total for payment screen
            val subtotal = CartManager.getTotal()
            val tax = subtotal * 0.08
            val shipping = if (subtotal > 100) 0.0 else 5.0
            val total = subtotal + tax + shipping

            ModernCartScreen(
                onBackHome = { currentPage = "HOME" },
                onSelectBottom = { currentPage = it },
                onNavigateToPayment = { currentPage = "PAYMENT" }
            )
        }

        "PAYMENT" -> {
            // Calculate total for payment screen
            val subtotal = CartManager.getTotal()
            val tax = subtotal * 0.08
            val shipping = if (subtotal > 100) 0.0 else 5.0
            val total = subtotal + tax + shipping

            PaymentScreen(
                onBackToCart = { currentPage = "CART" },
                onOrderPlaced = { currentPage = "HOME" },
                totalAmount = total
            )
        }

        "ACCOUNT" -> {
            AccountScreen(
                onBackHome = { currentPage = "HOME" },
                onSignOut = onSignOut,
                isDarkMode = isDarkMode,
                onToggleDarkMode = onToggleDarkMode,
                onNavigateToPage = { page: String -> currentPage = page }
            )
        }

        "SEARCH" -> {
            SearchScreen(
                onBackClick = { currentPage = "HOME" },
                onProductClick = { product ->
                    selectedProduct = product
                    currentPage = "PRODUCT_DETAIL"
                },
                onSelectBottom = { currentPage = it }
            )
        }

        "NOTIFICATIONS" -> {
            // Use the actual NotificationScreen component
            NotificationScreen(
                onBackHome = { currentPage = "HOME" },
                onSelectBottom = { currentPage = it }
            )
        }

        "FAVORITES" -> {
            // Use the actual FavoritesScreen component
            FavoritesScreen(
                onBackHome = { currentPage = "HOME" },
                onSelectBottom = { currentPage = it }
            )
        }
    }
}

/**
 * Badged Notification Icon
 * Shows a red dot badge on top of the notification bell icon when there are unread notifications
 */
@Composable
fun BadgedNotificationIcon() {
    // Get notification count from NotificationManager
    val notificationCount = NotificationManager.notifications.size

    // BadgedBox to show badge on notification icon
    BadgedBox(
        badge = {
            // Only show badge if there are notifications
            if (notificationCount > 0) {
                Badge(
                    containerColor = Color.Red,
                    modifier = Modifier
                        .size(8.dp) // Smaller badge size
                        .offset(x = (-2).dp, y = 2.dp)
                ) {
                    // Show count if more than 0, or just a dot
                    if (notificationCount > 9) {
                        Text(
                            "9+",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    } else {
                        // Just a red dot (empty badge - smaller)
                        Text(
                            "",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) {
        Icon(
            Icons.Filled.Notifications,
            contentDescription = "Notifications",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

// Modern Top Bar with Gradient
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHomeTopBar(onSelect: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🐾", fontSize = 24.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Pawelier",
                            style = TextStyle(
                                fontFamily = GreatVibes,
                                fontSize = 32.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                },
                navigationIcon = {
                    Box {
                        IconButton(onClick = { open = true }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                            DropdownMenuItem(
                                text = { Text("🏠 Home") },
                                onClick = { open = false; onSelect("HOME") }
                            )
                            DropdownMenuItem(
                                text = { Text("👕 Wear") },
                                onClick = { open = false; onSelect("WEAR") }
                            )
                            DropdownMenuItem(
                                text = { Text("🦮 Walk") },
                                onClick = { open = false; onSelect("WALK") }
                            )
                            DropdownMenuItem(
                                text = { Text("🏡 Living") },
                                onClick = { open = false; onSelect("LIVING") }
                            )
                            DropdownMenuItem(
                                text = { Text("✈️ Travel") },
                                onClick = { open = false; onSelect("TRAVEL") }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { onSelect("SEARCH") }) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { onSelect("NOTIFICATIONS") }) {
                        // Show notification icon with badge if there are notifications
                        BadgedNotificationIcon()
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    }
}

// Modern Hero Section with Cards
@Composable
fun ModernHeroSection(onShopNow: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.hero),
                contentDescription = "Hero",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Text(
                    "🌟 NEW ARRIVAL",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Premium Pet\nAccessories",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        lineHeight = 40.sp
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Luxury meets comfort for your furry friends",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    )
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onShopNow,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.shadow(4.dp, RoundedCornerShape(12.dp))
                ) {
                    Text("SHOP NOW", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

// Featured Categories with Horizontal Scroll
@Composable
fun FeaturedCategoriesSection(onCategoryClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Shop by Category",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            TextButton(onClick = { /* View all */ }) {
                Text("View All", color = MaterialTheme.colorScheme.primary)
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                listOf(
                    CategoryItem("WEAR", "👕", R.drawable.wear),
                    CategoryItem("WALK", "🦮", R.drawable.walk),
                    CategoryItem("LIVING", "🏡", R.drawable.living),
                    CategoryItem("TRAVEL", "✈️", R.drawable.travel)
                )
            ) { category ->
                ModernCategoryCard(category, onCategoryClick)
            }
        }
    }
}

data class CategoryItem(val name: String, val emoji: String, val imageRes: Int)

@Composable
fun ModernCategoryCard(category: CategoryItem, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onClick(category.name) },
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            Image(
                painter = painterResource(category.imageRes),
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    category.emoji,
                    fontSize = 32.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    category.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

// Quick Actions Section
@Composable
fun QuickActionsSection(onActionClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            "Quick Actions",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Filled.FavoriteBorder,
                title = "Favorites",
                subtitle = "Your wishlist",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
                onClick = { onActionClick("FAVORITES") }
            )
            QuickActionCard(
                icon = Icons.Filled.ShoppingCart,
                title = "Cart",
                subtitle = "View cart",
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f),
                onClick = { onActionClick("CART") }
            )
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

// Why Choose Us Section
@Composable
fun WhyChooseUsSection() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            "Why Choose Pawelier?",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                icon = "🎁",
                title = "Premium Quality",
                description = "Handpicked luxury items",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = "🚚",
                title = "Fast Delivery",
                description = "2-3 days shipping",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                icon = "💝",
                title = "Pet Approved",
                description = "Tested & loved",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = "🌟",
                title = "24/7 Support",
                description = "Always here for you",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FeatureCard(
    icon: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }
    }
}

// Modern Bottom Navigation Bar
@Composable
fun ModernBottomBar(
    selected: String,
    onSelect: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selected == "HOME",
            onClick = { onSelect("HOME") },
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Home", fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = selected == "FAVORITES",
            onClick = { onSelect("FAVORITES") },
            icon = {
                Icon(
                    if (selected == "FAVORITES") Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorites",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Favorites", fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Red,
                selectedTextColor = Color.Red,
                indicatorColor = Color.Red.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = selected == "CART",
            onClick = { onSelect("CART") },
            icon = {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = "Cart",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Cart", fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = selected == "ACCOUNT",
            onClick = { onSelect("ACCOUNT") },
            icon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Account",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Account", fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )
    }
}

@Composable
private fun gridColumns(): Int {
    val cfg = LocalConfiguration.current
    val width = cfg.screenWidthDp
    val isLandscape = cfg.orientation == Configuration.ORIENTATION_LANDSCAPE
    return when {
        width >= 840 -> 4
        width >= 600 || isLandscape -> 3
        else -> 2
    }
}

// Search Screen with functional search bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onSelectBottom: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Combine all products from different categories
    val allProducts = remember {
        ProductData.wearProducts +
                ProductData.walkProducts +
                ProductData.livingProducts +
                ProductData.travelProducts
    }

    // Filter products based on search query
    val filteredProducts = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            allProducts
        } else {
            allProducts.filter { product ->
                product.name.contains(searchQuery, ignoreCase = true) ||
                        product.description.contains(searchQuery, ignoreCase = true) ||
                        product.category.contains(searchQuery, ignoreCase = true) ||
                        product.price.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Search Products",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = { ModernBottomBar(selected = "HOME", onSelect = onSelectBottom) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Search for products...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Search Results Info
            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "Found ${filteredProducts.size} product(s)",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Products Grid
            if (filteredProducts.isEmpty() && searchQuery.isNotEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No products found",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Try searching with different keywords",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else if (searchQuery.isEmpty()) {
                // Show all products when no search query
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Search for Products",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Search through all our pet accessories\nincluding Wear, Walk, Living, and Travel items",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Product grid with results
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts.size) { index ->
                        val product = filteredProducts[index]
                        SearchProductCard(
                            product = product,
                            isFavorite = FavoritesManager.isFavorite(product.id),
                            onFavoriteClick = {
                                FavoritesManager.toggleFavorite(product)
                            },
                            onProductClick = { onProductClick(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchProductCard(
    product: Product,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onProductClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable { onProductClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Category badge
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        bottomEnd = 12.dp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Favorite button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "Unfavorite" else "Favorite",
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Product details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = product.price,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

