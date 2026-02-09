
package com.example.pawelierapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pawelierapp.screens.LoginScreen
import com.example.pawelierapp.screens.RegisterScreen
import com.example.pawelierapp.screens.HomeScreen
import com.example.pawelierapp.screens.ModernBottomBar
import com.example.pawelierapp.screens.SplashScreen
import com.example.pawelierapp.utils.PreferencesManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.mediacodec.MediaCodecAdapter
import com.example.pawelierapp.ui.theme.PawelierAppTheme
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.scale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.foundation.isSystemInDarkTheme

// Brand font (remove/change if you don't have this file)
val GreatVibes = FontFamily(Font(R.font.great_vibes_regular))

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val prefsManager = remember { PreferencesManager(context) }

            // Detect system theme
            val systemInDarkMode = isSystemInDarkTheme()

            // Load saved dark mode preference, or use system theme if not set
            val savedDarkMode = prefsManager.getDarkMode()
            val initialDarkMode = savedDarkMode ?: systemInDarkMode

            // Dark mode state
            var isDarkMode by rememberSaveable { mutableStateOf(initialDarkMode) }

            // Sync with system theme only if user hasn't manually set dark mode
            LaunchedEffect(systemInDarkMode) {
                if (!prefsManager.isDarkModeSet()) {
                    isDarkMode = systemInDarkMode
                }
            }

            var showSplash by rememberSaveable { mutableStateOf(true) }
            var showLogin by rememberSaveable { mutableStateOf(false) }
            var showRegister by rememberSaveable { mutableStateOf(false) }
            var loggedIn by rememberSaveable { mutableStateOf(false) }

            PawelierAppTheme(darkTheme = isDarkMode) {
                when {
                    showSplash -> SplashScreen(
                        onGetStarted = {
                            showSplash = false
                            showLogin = true
                        }
                    )

                    loggedIn -> HomeScreen(
                        onSignOut = { loggedIn = false },
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = {
                            isDarkMode = !isDarkMode
                            // Save dark mode preference
                            prefsManager.setDarkMode(isDarkMode)
                        }
                    )

                    showRegister -> RegisterScreen(
                        onBack = {
                            showRegister = false
                            showLogin = true
                        }
                    )

                    showLogin -> LoginScreen(
                        onLoginSuccess = { loggedIn = true },
                        onRegisterClick = { showRegister = true },
                        onForgotPasswordClick = { /* optional */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun gridColumns(): Int {
    val cfg = LocalConfiguration.current
    val width = cfg.screenWidthDp
    val isLandscape = cfg.orientation == Configuration.ORIENTATION_LANDSCAPE
    return when {
        width >= 840 -> 4              // tablets / very wide
        width >= 600 || isLandscape -> 3
        else -> 2
    }
}

/* ---------------- Splash / Welcome ---------------- */
// OLD SplashScreen - COMMENTED OUT - Now using SplashScreen from screens package
/*
@Composable
fun SplashScreen(onGetStarted: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.wel),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Pawelier",
                style = TextStyle(fontFamily = GreatVibes, fontSize = 100.sp, color = Color.Black)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Where design meets pet-approved comfort.",
                style = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Center),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
                    .shadow(6.dp, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) { Text("Get Started") }
        }
    }
}
*/

/* ---------------- Login ---------------- */
// OLD LoginScreen - COMMENTED OUT - Now using LoginScreen from screens package
/*
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,          // navigate to Register screen
    onForgotPasswordClick: () -> Unit = {}// optional: navigate to Forgot screen
) {
    val ctx = LocalContext.current
    var user by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }

    // enter animation trigger
    var show by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { show = true }

    Box(Modifier.fillMaxSize()) {
        // background image
        Image(
            painter = painterResource(R.drawable.log),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // brand title
        Text(
            text = "Pawelier",
            style = TextStyle(fontSize = 42.sp, fontFamily = GreatVibes),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        )

        // animated login card (centered; slides up from bottom)
        AnimatedVisibility(
            visible = show,
            modifier = Modifier
                .align(Alignment.Center)   // final position = center of the screen
                .offset(y = 16.dp),        // nudge a bit lower than center (optional)
            enter = fadeIn(animationSpec = tween(350)) +
                    slideInVertically(
                        // start below screen and slide UP to center
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(350)
                    ),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                )  {
                    // avatar
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) { Text("👤", fontSize = 42.sp) }

                    Spacer(Modifier.height(16.dp))
                    Text("LOG IN", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = user,
                        onValueChange = { user = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (user == "user" && pass == "password") onLoginSuccess()
                            else Toast.makeText(ctx, "Wrong username or password", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Log In") }

                    Spacer(Modifier.height(10.dp))

                    // press-scale animation for text buttons
                    val regIs = remember { MutableInteractionSource() }
                    val regPressed by regIs.collectIsPressedAsState()
                    val regScale by animateFloatAsState(if (regPressed) 0.96f else 1f, tween(120))

                    val fpIs = remember { MutableInteractionSource() }
                    val fpPressed by fpIs.collectIsPressedAsState()
                    val fpScale by animateFloatAsState(if (fpPressed) 0.96f else 1f, tween(120))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = onRegisterClick,
                            interactionSource = regIs,
                            modifier = Modifier.scale(regScale)
                        ) { Text("Register") }

                        TextButton(
                            onClick = onForgotPasswordClick,
                            interactionSource = fpIs,
                            modifier = Modifier.scale(fpScale)
                        ) { Text("Forgot Password?") }
                    }

                    TextButton(onClick = onLoginSuccess) {
                        Text("Continue to Home")
                    }
                }
            }
        }
    }
}
*/
/*----------REGISTER PAGE------------- */
// OLD RegisterScreen - COMMENTED OUT - Now using RegisterScreen from screens package
/*
@Composable
fun RegisterScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    // trigger enter animation
    var show by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { show = true }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.log),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Register",
            style = TextStyle(fontSize = 36.sp, fontFamily = GreatVibes),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        )

        // animated card (same pattern as Login)
        AnimatedVisibility(
            visible = show,
            modifier = Modifier
                .align(Alignment.Center)   // final position = center
                .offset(y = 16.dp),        // tiny nudge lower (optional)
            enter = fadeIn(animationSpec = tween(350)) +
                    slideInVertically(
                        initialOffsetY = { it / 3 }, // start below, slide UP
                        animationSpec = tween(350)
                    ),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.88f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = username, onValueChange = { username = it },
                        label = { Text("Username") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        label = { Text("Email") }, singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        label = { Text("Password") }, singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            Toast.makeText(ctx, "Account created successfully!", Toast.LENGTH_SHORT).show()
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Register") }

                    TextButton(onClick = onBack) { Text("Back to Login") }
                }
            }
        }
    }
}
*/
/* ---------- HOME (responsive, no video) ---------- */
// OLD HomeScreen - COMMENTED OUT - Now using HomeScreen from screens package
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var currentPage by rememberSaveable { mutableStateOf("HOME") }

    // shared cart across all pages
    val cart = remember { mutableStateListOf<CartLine>() }

    // keep the latest receipt for the success screen
    val lastReceipt = remember { mutableStateOf<OrderReceipt?>(null) }

    fun addToCart(p: Product) {
        val i = cart.indexOfFirst { it.product.id == p.id }
        if (i >= 0) cart[i] = cart[i].copy(qty = cart[i].qty + 1)
        else cart.add(CartLine(product = p, qty = 1))
    }

    when (currentPage) {
        "HOME" -> Scaffold(
            topBar = { HomeTopBar { selected -> currentPage = selected } },
            bottomBar = { HomeBottomBar(selected = currentPage, onSelect = { currentPage = it }) }
        ) { inner ->
            Column(Modifier.padding(inner).fillMaxSize()) {
                HeroImageSection(onShopNow = { currentPage = "WEAR" })
                Spacer(Modifier.height(8.dp))
                CategoryGrid(onCategoryClick = { currentPage = it })
            }
        }


        // ---- PRODUCT LIST PAGES (all support add-to-cart + open cart) ----
        "WEAR" -> WearScreen(
            onBackHome = { currentPage = "HOME" },
            onAddToCartAndOpen = { p -> addToCart(p); currentPage = "CART" },
            onOpenCart = { currentPage = "CART" },
            onBottomSelect = { dest -> currentPage = dest }

        )

        "WALK" -> WalkScreen(
            onBackHome = { currentPage = "HOME" },
            onAddToCartAndOpen = { p -> addToCart(p); currentPage = "CART" },
            onOpenCart = { currentPage = "CART" },
            onBottomSelect = { dest -> currentPage = dest }
        )

        "LIVING" -> LivingScreen(
            onBackHome = { currentPage = "HOME" },
            onAddToCartAndOpen = { p -> addToCart(p); currentPage = "CART" },
            onOpenCart = { currentPage = "CART" },
            onBottomSelect = { dest -> currentPage = dest }
        )

        "TRAVEL" -> TravelScreen(
            onBackHome = { currentPage = "HOME" },
            onAddToCartAndOpen = { p -> addToCart(p); currentPage = "CART" },
            onOpenCart = { currentPage = "CART" },
            onBottomSelect = { dest -> currentPage = dest }
        )

        "SEARCH" -> SearchScreen(onBackHome = { currentPage = "HOME" })

        "CART" -> CartScreen(
            cart = cart,
            onBackHome = { currentPage = "HOME" },
            onCheckout = { currentPage = "CHECKOUT" }
        )

        "CHECKOUT" -> PlaceOrderScreen(
            cart = cart,
            onBack = { currentPage = "CART" },
            onOrderPlaced = { receipt ->
                cart.clear()
                lastReceipt.value = receipt
                currentPage = "PAYMENT_SUCCESS"
            }
        )

        "PAYMENT_SUCCESS" -> PaymentSuccessScreen(
            receipt = lastReceipt.value ?: OrderReceipt(
                orderId = "N/A",
                total = 0.0,
                message = "Thanks!"
            ),
            onContinueShopping = { currentPage = "HOME" }
        )
        // ---------- NOTIFICATIONS ----------
        "ACCOUNT" -> AccountScreen(
            onBackHome = { currentPage = "HOME" },
            onEditProfile = { },
            onNotifications = { currentPage = "NOTIFICATIONS" },
            onShippingAddress = { },
            onChangePassword = { },
            onSignOut = { }
        )
        // ---------- NOTIFICATIONS ----------
        "NOTIFICATIONS" -> NotificationsScreen(
            onBackHome = { currentPage = "HOME" },
            onSelectBottom = { currentPage = it }
        )



    }
}
*/

// OLD HomeScreen helper functions - COMMENTED OUT - Now in screens/HomeScreen.kt
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(onSelect: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text("Pawelier", style = TextStyle(fontFamily = GreatVibes, fontSize = 28.sp))
        },
        navigationIcon = {
            // anchor for the dropdown
            Box {
                IconButton(onClick = { open = true }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }

                DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                    DropdownMenuItem(text = { Text("Home") },   onClick = { open = false; onSelect("HOME") })
                    DropdownMenuItem(text = { Text("Wear") },   onClick = { open = false; onSelect("WEAR") })
                    DropdownMenuItem(text = { Text("Walk") },   onClick = { open = false; onSelect("WALK") })
                    DropdownMenuItem(text = { Text("Living") }, onClick = { open = false; onSelect("LIVING") })
                    DropdownMenuItem(text = { Text("Travel") }, onClick = { open = false; onSelect("TRAVEL") })
                    // add more if you want (Favorites, Bakery, etc.)
                }
            }
        },
        actions = {
            IconButton(onClick = { onSelect("SEARCH") }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "search"
                )
            }
            IconButton(onClick = { onSelect("CART") }) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
            }
        }



    )
}

// ---------- HERO (image only, responsive 16:9) ----------
@Composable
fun HeroImageSection(onShopNow: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(18f / 13f)        // ✅ responsive height based on width
    ) {
        // Use hero; if you don't have it yet, temporarily use welcome
        Image(
            painter = painterResource(R.drawable.hero),
            contentDescription = "Hero",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // subtle dim so the white text is readable
        Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.20f)))

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "EXPLORE OUR LATEST COLLECTION",
                style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "TRAVEL WITH\nTIMELESS STYLE",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    letterSpacing = 1.5.sp,
                    lineHeight = 34.sp
                )
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Luxurious Essentials for Your Beloved Companions",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onShopNow,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp)
            ) { Text("SHOP NOW") }
        }
    }
}

// ---------- ADAPTIVE GRID (1–3 columns automatically) ----------
@Composable
fun CategoryGrid(onCategoryClick: (String) -> Unit) {
    Column(Modifier.padding(12.dp)) {
        Row(Modifier.fillMaxWidth()) {
            CategoryTile("WEAR", R.drawable.wear, Modifier.weight(1f).padding(4.dp)) {
                onCategoryClick("WEAR")
            }
            CategoryTile("WALK", R.drawable.walk, Modifier.weight(1f).padding(4.dp)) {
                onCategoryClick("WALK")
            }
        }
        Row(Modifier.fillMaxWidth()) {
            CategoryTile("LIVING", R.drawable.living, Modifier.weight(1f).padding(4.dp)) {
                onCategoryClick("LIVING")
            }
            CategoryTile("TRAVEL", R.drawable.travel, Modifier.weight(1f).padding(4.dp)) {
                onCategoryClick("TRAVEL")
            }
        }
    }
}

@Composable
fun CategoryTile(label: String, imageRes: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.25f)))
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                letterSpacing = 2.sp
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// ---------- BOTTOM NAV ----------
@Composable
fun HomeBottomBar(
    selected: String,
    onSelect: (String) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selected == "HOME",
            onClick = { onSelect("HOME") },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selected == "NOTIFICATIONS",
            onClick = { onSelect("NOTIFICATIONS") },
            icon = { Icon(Icons.Filled.Notifications, contentDescription = "Notifications") },
            label = { Text("Notifications") }
        )
        NavigationBarItem(
            selected = selected == "ACCOUNT",
            onClick = { onSelect("ACCOUNT") },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Account") },
            label = { Text("Account") }
        )
        NavigationBarItem(
            selected = selected == "CART",
            onClick = { onSelect("CART") },
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Cart") }
        )
    }
}
*/
/* ---------- WEAR PAGE ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WearScreen(
    onBackHome: (() -> Unit)? = null,
    onAddToCartAndOpen: (Product) -> Unit = {},   // ✅ add-to-cart + open cart
    onOpenCart: () -> Unit = {} ,                  // ✅ top-right cart button
    onBottomSelect: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pawelier", style = TextStyle(fontFamily = GreatVibes, fontSize = 24.sp))
                        Text("WEAR", style = MaterialTheme.typography.headlineMedium)
                    }
                },
                navigationIcon = {
                    if (onBackHome != null) {
                        IconButton(onClick = onBackHome) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenCart) {                     // ✅ opens cart page
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        },
        bottomBar = { ModernBottomBar(selected = "HOME", onSelect = onBottomSelect) }
    ) { inner ->
        WearGrid(
            modifier = Modifier
                .padding(inner)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            onAddToCartAndOpen = onAddToCartAndOpen                     // ✅ pass down
        )
    }
}

// put/rename these images in res/drawable (all lowercase!)
private val wearItems = listOf(
    Product(id = 201, title = "Arctic Luxe Puffer",         price = "£65.00", imageRes = R.drawable.we1),
    Product(id = 202, title = "Autumn Breeze Outdoor Suit", price = "£58.00", imageRes = R.drawable.we2),
    Product(id = 203, title = "Blue Ribbon Pearl Necklace", price = "£35.00", imageRes = R.drawable.we3),
    Product(id = 204, title = "Bow & Heart Necklace",       price = "£45.00", imageRes = R.drawable.we4),
    Product(id = 205, title = "Cocoon Stripe Dralon Tee",   price = "£48.00", imageRes = R.drawable.we5),
    Product(id = 206, title = "Comfort Stretch Pet Vest",   price = "£52.00", imageRes = R.drawable.we6),
)

@Composable
private fun WearGrid(
    modifier: Modifier = Modifier,
    onAddToCartAndOpen: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumns()),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        items(wearItems) { p ->
            WearCard(p, onAddToCartAndOpen)                                // ✅ use callback
        }

    }
}

@Composable
private fun WearCard(
    p: Product,
    onAddToCartAndOpen: (Product) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(p.imageRes),
                contentDescription = p.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // ✅ cart button: add product then open Cart page
            IconButton(
                onClick = { onAddToCartAndOpen(p) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Add to cart",
                    tint = Color.White
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(p.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        Text(p.price, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
    }
}

/* ---------- BOTTOM NAV ---------- */
@Composable
fun WearBottomBar() {
    NavigationBar {
        NavigationBarItem(true,  onClick = { }, icon = { Icon(Icons.Filled.Home, null) },          label = { Text("Home") })
        NavigationBarItem(false, onClick = { }, icon = { Icon(Icons.Filled.Notifications, null) }, label = { Text("Activities") })
        NavigationBarItem(false, onClick = { }, icon = { Icon(Icons.Filled.Person, null) },        label = { Text("Account") })
        NavigationBarItem(false, onClick = { }, icon = { Icon(Icons.Filled.ShoppingCart, null) },  label = { Text("Cart") })
    }
}

/* ---------- WALK SCREEN (add-to-cart) ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkScreen(
    onBackHome: (() -> Unit)? = null,
    onAddToCartAndOpen: (Product) -> Unit,
    onOpenCart: () -> Unit,
    onBottomSelect: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pawelier", style = TextStyle(fontFamily = GreatVibes, fontSize = 26.sp))
                        Text("WALK", style = MaterialTheme.typography.headlineMedium)
                    }
                },
                navigationIcon = {
                    if (onBackHome != null) {
                        IconButton(onClick = onBackHome) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        IconButton(onClick = { /* drawer if you add one */ }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenCart) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        },
        bottomBar = { ModernBottomBar(selected = "HOME", onSelect = onBottomSelect) }
    ) { inner ->
        WalkGrid(
            modifier = Modifier.padding(inner),
            onAddToCartAndOpen = onAddToCartAndOpen
        )
    }
}

/* Items (put these images in res/drawable, all lowercase) */
private val walkItems = listOf(
    Product(id = 301, title = "Blossom Recovery Collar",          price = "£32.00", imageRes = R.drawable.wa1),
    Product(id = 302, title = "Color Gemstone Harness",           price = "£38.00", imageRes = R.drawable.wa2),
    Product(id = 303, title = "Lilco Braided-Collar | Moonbeam",  price = "£46.00", imageRes = R.drawable.wa3),
    Product(id = 304, title = "Lilco Braided-Collar | Seabreeze", price = "£46.00", imageRes = R.drawable.wa4),
    Product(id = 305, title = "Luxury Soft Ribbed Pet Harness",   price = "£41.00", imageRes = R.drawable.wa5),
    Product(id = 306, title = "Martingale Collar",                price = "£41.00", imageRes = R.drawable.wa6)
)

@Composable
private fun WalkGrid(
    modifier: Modifier = Modifier,
    onAddToCartAndOpen: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumns()),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        items(walkItems) { p ->
            WalkCard(p = p, onAddToCartAndOpen = onAddToCartAndOpen)
        }


    }

}


@Composable
private fun WalkCard(
    p: Product,
    onAddToCartAndOpen: (Product) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(p.imageRes),
                contentDescription = p.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // small cart button (adds item then opens Cart page)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable { onAddToCartAndOpen(p) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Add to cart",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(p.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        Text(p.price, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
    }
}
/* ---------- LIVING SCREEN (add-to-cart) ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivingScreen(
    onBackHome: (() -> Unit)? = null,
    onAddToCartAndOpen: (Product) -> Unit,
    onOpenCart: () -> Unit,
    onBottomSelect: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pawelier", style = TextStyle(fontFamily = GreatVibes, fontSize = 26.sp))
                        Text("LIVING", style = MaterialTheme.typography.headlineMedium)
                    }
                },
                navigationIcon = {
                    if (onBackHome != null) {
                        IconButton(onClick = onBackHome) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        IconButton(onClick = { /* drawer */ }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenCart) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        },
        bottomBar = { ModernBottomBar(selected = "HOME", onSelect = onBottomSelect) }
    ) { inner ->
        LivingGrid(
            modifier = Modifier.padding(inner),
            onAddToCartAndOpen = onAddToCartAndOpen
        )
    }
}

/* Items (place these in res/drawable, all lowercase) */
private val livingItems = listOf(
    Product(id = 401, title = "Buckingham Bed",         price = "From £125.00", imageRes = R.drawable.li1),
    Product(id = 402, title = "Cartoon Dining Mat",     price = "£35.00",       imageRes = R.drawable.li3),
    Product(id = 403, title = "Cluck Pet Nest",         price = "£49.00",       imageRes = R.drawable.li2),
    Product(id = 404, title = "ColorPop Ceramic Bowls", price = "£28.00",       imageRes = R.drawable.li4),
    Product(id = 405, title = "Leopard Phone Case",     price = "£18.00",       imageRes = R.drawable.li5),
    Product(id = 406, title = "Luxe Lounge Cushion",    price = "£42.00",       imageRes = R.drawable.li6)
)

@Composable
private fun LivingGrid(
    modifier: Modifier = Modifier,
    onAddToCartAndOpen: (Product) -> Unit
) {
    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumns()),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        items(livingItems) { p ->
            LivingCard(p = p, onAddToCartAndOpen = onAddToCartAndOpen)
        }


    }
}

@Composable
private fun LivingCard(
    p: Product,
    onAddToCartAndOpen: (Product) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(p.imageRes),
                contentDescription = p.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // cart button → add + open cart
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable { onAddToCartAndOpen(p) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Add to cart",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(p.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        Text(p.price, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
    }
}

/* ---------- TRAVEL SCREEN (add-to-cart) ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelScreen(
    onBackHome: (() -> Unit)? = null,
    onAddToCartAndOpen: (Product) -> Unit,
    onOpenCart: () -> Unit,
    onBottomSelect: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pawelier", style = TextStyle(fontFamily = GreatVibes, fontSize = 26.sp))
                        Text("TRAVEL", style = MaterialTheme.typography.headlineMedium)
                    }
                },
                navigationIcon = {
                    if (onBackHome != null) {
                        IconButton(onClick = onBackHome) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        IconButton(onClick = { /* drawer */ }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenCart) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        },
        bottomBar = { ModernBottomBar(selected = "HOME", onSelect = onBottomSelect) }
    ) { inner ->
        TravelGrid(
            modifier = Modifier.padding(inner),
            onAddToCartAndOpen = onAddToCartAndOpen
        )
    }
}

/* Items */
private val travelItems = listOf(
    Product(id = 501, title = "Adventure Carrier",        price = "£35.00", imageRes = R.drawable.ti1),
    Product(id = 502, title = "Blush Bow Duffle Carrier", price = "£55.00", imageRes = R.drawable.ti2),
    Product(id = 503, title = "City Stroll Carrier",      price = "£60.00", imageRes = R.drawable.ti3),
    Product(id = 504, title = "Cloud Comfort Carrier",    price = "£55.00", imageRes = R.drawable.ti4),
    Product(id = 505, title = "Comfy Carrier",            price = "£45.00", imageRes = R.drawable.ti5),
    Product(id = 506, title = "Cozy Ride Car Seat Bed",   price = "£55.00", imageRes = R.drawable.ti6)
)

@Composable
private fun TravelGrid(
    modifier: Modifier = Modifier,
    onAddToCartAndOpen: (Product) -> Unit
) {
    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumns()),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        items(travelItems) { p ->
            TravelCard(p = p, onAddToCartAndOpen = onAddToCartAndOpen)
        }


    }
}

@Composable
private fun TravelCard(
    p: Product,
    onAddToCartAndOpen: (Product) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(p.imageRes),
                contentDescription = p.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // mini cart button → add + open cart
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable { onAddToCartAndOpen(p) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Add to cart",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(p.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        Text(p.price, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
    }
}

data class DrawerItem(val label: String, val icon: ImageVector)

@Composable
fun PawelierMenu(onItemClick: (String) -> Unit) {
    val items = listOf(
        DrawerItem("Home", Icons.Filled.Home),
        DrawerItem("Wear", Icons.Filled.Checkroom),
        DrawerItem("Walk", Icons.Filled.Pets),
        DrawerItem("Living", Icons.Filled.Weekend),
        DrawerItem("Travel", Icons.Filled.Luggage),
        DrawerItem("Bakery", Icons.Filled.Cake),
        DrawerItem("Favorites", Icons.Filled.FavoriteBorder),
        DrawerItem("Orders", Icons.Filled.ListAlt),
        DrawerItem("Cart", Icons.Filled.ShoppingCart),
        DrawerItem("Account", Icons.Filled.Person),
        DrawerItem("Settings", Icons.Filled.Settings)
    )

    ModalDrawerSheet {
        Text(
            text = "Pawelier",
            style = TextStyle(fontFamily = GreatVibes, fontSize = 28.sp),
            modifier = Modifier.padding(start = 24.dp, top = 20.dp, bottom = 10.dp)
        )

        Divider()

        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = false,
                onClick = { onItemClick(item.label) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        Divider(modifier = Modifier.padding(vertical = 10.dp))

        Text(
            text = "© 2025 Pawelier",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 24.dp, top = 6.dp, bottom = 12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onBackHome: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pawelier", style = TextStyle(fontFamily = GreatVibes, fontSize = 26.sp))
                        Text("SEARCH", style = MaterialTheme.typography.headlineMedium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .background(Color(0xFFF7F7F7))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search products...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // 🔍 Filtered Grid Section (paste this exactly here)
            val allItems = wearItems + walkItems + livingItems + travelItems

            val filtered = if (query.isBlank()) allItems
            else allItems.filter { it.title.contains(query, ignoreCase = true) }

            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results found", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtered.size) { i ->
                        val p = filtered[i]
                        Column(Modifier.fillMaxWidth()) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    painter = painterResource(p.imageRes),
                                    contentDescription = p.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(p.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                            Text(p.price, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                        }
                    }
                }
            }
        }
    }
}

// ---------- CART SCREEN (with price summary + checkout) ----------
// ---------- CART SCREEN (stable updates + totals + checkout) ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cart: SnapshotStateList<CartLine>,
    onBackHome: () -> Unit,
    onCheckout: () -> Unit
) {
    // totals
    val subtotal = cart.sumOf {
        (it.product.price.replace("£", "").replace("From", "").trim()
            .toDoubleOrNull() ?: 0.0) * it.qty
    }
    val tax = subtotal * 0.08          // 8% example
    val shipping = if (subtotal > 100) 0.0 else 5.0
    val total = subtotal + tax + shipping

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cart") },
                navigationIcon = {
                    IconButton(onClick = onBackHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cart.isNotEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal"); Text("£${"%.2f".format(subtotal)}")
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tax (8%)"); Text("£${"%.2f".format(tax)}")
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Shipping"); Text(if (shipping == 0.0) "Free" else "£${"%.2f".format(shipping)}")
                    }
                    Divider(Modifier.padding(vertical = 8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", style = MaterialTheme.typography.titleMedium)
                        Text("£${"%.2f".format(total)}", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Checkout") }
                }
            }
        }
    ) { inner ->
        if (cart.isEmpty()) {
            Box(Modifier.padding(inner).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(inner).fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cart, key = { it.product.id }) { line ->
                    CartItemRow(
                        line = line,
                        onIncrease = {
                            val idx = cart.indexOfFirst { it.product.id == line.product.id }
                            if (idx >= 0) cart[idx] = cart[idx].copy(qty = cart[idx].qty + 1)
                        },
                        onDecrease = {
                            val idx = cart.indexOfFirst { it.product.id == line.product.id }
                            if (idx >= 0) {
                                val cur = cart[idx]
                                if (cur.qty > 1) cart[idx] = cur.copy(qty = cur.qty - 1)
                                else cart.removeAt(idx)
                            }
                        },
                        onRemove = {
                            val idx = cart.indexOfFirst { it.product.id == line.product.id }
                            if (idx >= 0) cart.removeAt(idx)
                        }
                    )
                }
                item { Spacer(Modifier.height(96.dp)) } // space for bottom summary
            }
        }
    }
}

// ---------- ITEM ROW ----------
@Composable
private fun CartItemRow(
    line: CartLine,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        Modifier
            .fillMaxWidth()
            .animateContentSize(),                 // 👈
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    )  {
        Row(
            Modifier
                .padding(12.dp)
                .animateContentSize(),              // 👈 smooth height/width changes
            verticalAlignment = Alignment.CenterVertically
        )  {            Image(
                painter = painterResource(line.product.imageRes),
                contentDescription = line.product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(line.product.title, maxLines = 1)
                Text(line.product.price, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = onDecrease, contentPadding = PaddingValues(0.dp)) { Text("−") }
                    Text(" ${line.qty} ", style = MaterialTheme.typography.bodyMedium)
                    OutlinedButton(onClick = onIncrease, contentPadding = PaddingValues(0.dp)) { Text("+") }
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove")
            }
        }
    }
}

/// CHECKOUT PAGE///
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceOrderScreen(
    cart: SnapshotStateList<CartLine>,
    onBack: () -> Unit,
    onOrderPlaced: (OrderReceipt) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var card by rememberSaveable { mutableStateOf("") }
    var orderConfirmed by rememberSaveable { mutableStateOf(false) }


    val subtotal = cart.sumOf {
        (it.product.price.replace("£","").replace("From","").trim().toDoubleOrNull() ?: 0.0) * it.qty
    }
    val tax = subtotal * 0.08
    val shipping = if (subtotal > 100) 0.0 else 5.0
    val total = subtotal + tax + shipping

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Shipping", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(8.dp))
            Text("Payment", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = card, onValueChange = { card = it }, label = { Text("Card number") }, singleLine = true, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(8.dp))
            Divider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Subtotal"); Text("£${"%.2f".format(subtotal)}") }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Tax (8%)"); Text("£${"%.2f".format(tax)}") }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Shipping"); Text(if (shipping == 0.0) "Free" else "£${"%.2f".format(shipping)}") }
            Divider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", style = MaterialTheme.typography.titleMedium)
                Text("£${"%.2f".format(total)}", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    // very light validation
                    if (name.isBlank() || address.isBlank() || card.length < 8) return@Button

                    val receipt = OrderReceipt(
                        orderId = "#${(100000..999999).random()}",
                        total = total,
                        message = "Order placed successfully!",
                        etaDays = 3
                    )
                    onOrderPlaced(receipt)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Place Order") }
        }
    }
}

//SUCCESS PAGE//
@Composable
fun PaymentSuccessScreen(
    receipt: OrderReceipt,
    onContinueShopping: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✅ Payment Successful", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("Order: ${receipt.orderId}")
        Text("Total Paid: £${"%.2f".format(receipt.total)}")
        Text("Your items will arrive in ~${receipt.etaDays} days.")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onContinueShopping) { Text("Continue Shopping") }
    }
}

//ACCOUNT PAGE//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onBackHome: () -> Unit,
    onEditProfile: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onShippingAddress: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackHome) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // avatar + edit
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.BottomEnd
            ) {
                // replace with your drawable if you have one
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit photo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .offset(x = (-6).dp, y = (-6).dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(6.dp)
                )
            }

            Spacer(Modifier.height(12.dp))
            Text("Albert Florest", style = MaterialTheme.typography.titleLarge)
            Text("Buyer", color = Color.Gray)

            Spacer(Modifier.height(20.dp))

            // card container for rows
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(vertical = 4.dp)) {
                    AccountRow(
                        icon = Icons.Filled.Person,
                        label = "Edit Profile",
                        onClick = onEditProfile
                    )
                    Divider()
                    AccountRow(
                        icon = Icons.Filled.Notifications,
                        label = "Notification",
                        onClick = onNotifications
                    )
                    Divider()
                    AccountRow(
                        icon = Icons.Filled.LocationOn,
                        label = "Shipping Address",
                        onClick = onShippingAddress
                    )
                    Divider()
                    AccountRow(
                        icon = Icons.Filled.Lock,
                        label = "Change Password",
                        onClick = onChangePassword
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Sign Out")
            }
        }
    }
}

@Composable
private fun AccountRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.width(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

//NOTIFICATION PAGE///
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackHome: () -> Unit,
    onSelectBottom: (String) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBackHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = { ModernBottomBar(selected = "NOTIFICATIONS", onSelect = onSelectBottom) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("No new notifications ✨", style = MaterialTheme.typography.titleMedium)
            Text("We’ll notify you when something arrives.", color = Color.Gray)
        }
    }
}

