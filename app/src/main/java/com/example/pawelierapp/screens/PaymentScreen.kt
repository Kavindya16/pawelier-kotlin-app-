package com.example.pawelierapp.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Professional Payment Screen
 * Displays a secure payment form with card details, billing address, and order summary
 *
 * @param onBackToCart Callback to navigate back to cart
 * @param onOrderPlaced Callback when order is successfully placed
 * @param totalAmount Total order amount to display
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onBackToCart: () -> Unit,
    onOrderPlaced: () -> Unit,
    totalAmount: Double
) {
    // Context for showing toast messages
    val context = LocalContext.current

    // Coroutine scope for async operations
    val coroutineScope = rememberCoroutineScope()

    // Form field states
    var cardNumber by remember { mutableStateOf("") }
    var cardHolderName by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var billingAddress by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }

    // Validation states
    var cardNumberError by remember { mutableStateOf(false) }
    var cardHolderError by remember { mutableStateOf(false) }
    var expiryError by remember { mutableStateOf(false) }
    var cvvError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }

    // Processing animation state
    var isProcessing by remember { mutableStateOf(false) }

    // Secure lock animation for trust indicator
    val infiniteTransition = rememberInfiniteTransition(label = "lock")
    val lockScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lockScale"
    )

    /**
     * Validates all form fields before processing payment
     * Returns true if all fields are valid
     */
    fun validateFields(): Boolean {
        var isValid = true

        // Validate card number (should be 16 digits)
        if (cardNumber.length < 16) {
            cardNumberError = true
            isValid = false
        } else {
            cardNumberError = false
        }

        // Validate cardholder name (should not be empty)
        if (cardHolderName.isBlank()) {
            cardHolderError = true
            isValid = false
        } else {
            cardHolderError = false
        }

        // Validate expiry date (should be in MM/YY format)
        if (expiryDate.length < 5) {
            expiryError = true
            isValid = false
        } else {
            expiryError = false
        }

        // Validate CVV (should be 3 digits)
        if (cvv.length < 3) {
            cvvError = true
            isValid = false
        } else {
            cvvError = false
        }

        // Validate billing address (should not be empty)
        if (billingAddress.isBlank()) {
            addressError = true
            isValid = false
        } else {
            addressError = false
        }

        return isValid
    }

    /**
     * Handles the place order button click
     * Validates fields, shows processing state, and completes the order
     */
    fun handlePlaceOrder() {
        if (validateFields()) {
            isProcessing = true
            // Simulate processing delay
            coroutineScope.launch {
                delay(2000)

                // Generate unique order ID
                val orderId = "ORD${System.currentTimeMillis().toString().takeLast(8)}"

                // Get cart items before clearing
                val cartItems = CartManager.cartItems.values.toList()
                val products = cartItems.map { it.product }
                val itemCount = CartManager.getItemCount()

                // Create notification
                val notification = OrderNotification(
                    orderId = orderId,
                    products = products,
                    totalAmount = totalAmount,
                    itemCount = itemCount
                )

                // Add notification to NotificationManager
                NotificationManager.addNotification(notification)

                // Clear cart and show success message
                CartManager.clearCart()
                isProcessing = false

                // Show success toast
                Toast.makeText(
                    context,
                    "Order placed successfully!",
                    Toast.LENGTH_LONG
                ).show()
                onOrderPlaced()
            }
        } else {
            Toast.makeText(
                context,
                "Please fill all required fields correctly",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier
                                .size(24.dp)
                                .scale(lockScale)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Secure Payment",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToCart) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Cart",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Security Badge - builds user trust
            SecurityBadge()

            Spacer(Modifier.height(24.dp))

            // Order Summary Card
            OrderSummaryCard(totalAmount = totalAmount)

            Spacer(Modifier.height(24.dp))

            // Payment Information Section
            PaymentInformationSection(
                cardNumber = cardNumber,
                onCardNumberChange = { if (it.length <= 16) cardNumber = it.filter { char -> char.isDigit() } },
                cardNumberError = cardNumberError,
                cardHolderName = cardHolderName,
                onCardHolderNameChange = { cardHolderName = it },
                cardHolderError = cardHolderError,
                expiryDate = expiryDate,
                onExpiryDateChange = {
                    if (it.length <= 5) {
                        expiryDate = formatExpiryDate(it)
                    }
                },
                expiryError = expiryError,
                cvv = cvv,
                onCvvChange = { if (it.length <= 3) cvv = it.filter { char -> char.isDigit() } },
                cvvError = cvvError
            )

            Spacer(Modifier.height(24.dp))

            // Billing Address Section
            BillingAddressSection(
                billingAddress = billingAddress,
                onAddressChange = { billingAddress = it },
                addressError = addressError,
                city = city,
                onCityChange = { city = it },
                postalCode = postalCode,
                onPostalCodeChange = { postalCode = it }
            )

            Spacer(Modifier.height(32.dp))

            // Place Order Button - Main CTA
            PlaceOrderButton(
                isProcessing = isProcessing,
                totalAmount = totalAmount,
                onClick = { handlePlaceOrder() }
            )

            Spacer(Modifier.height(16.dp))

            // Trust Indicators
            TrustIndicators()

            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * Security badge to build user trust
 * Shows encrypted payment indicator
 */
@Composable
private fun SecurityBadge() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF4CAF50).copy(alpha = 0.1f),
                        Color(0xFF2196F3).copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Security,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                "Secure Payment Gateway",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Your payment information is encrypted",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Order summary card showing total amount
 */
@Composable
private fun OrderSummaryCard(totalAmount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Order Total",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    "Rs.%.2f".format(totalAmount),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "${CartManager.getItemCount()} item(s) in your order",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Payment information section with card details
 */
@Composable
private fun PaymentInformationSection(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    cardNumberError: Boolean,
    cardHolderName: String,
    onCardHolderNameChange: (String) -> Unit,
    cardHolderError: Boolean,
    expiryDate: String,
    onExpiryDateChange: (String) -> Unit,
    expiryError: Boolean,
    cvv: String,
    onCvvChange: (String) -> Unit,
    cvvError: Boolean
) {
    Column {
        // Section Header
        SectionHeader(
            icon = Icons.Filled.CreditCard,
            title = "Payment Information"
        )

        Spacer(Modifier.height(16.dp))

        // Card Number Field
        OutlinedTextField(
            value = cardNumber,
            onValueChange = onCardNumberChange,
            label = { Text("Card Number") },
            placeholder = { Text("") },
            leadingIcon = {
                Icon(Icons.Filled.CreditCard, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = cardNumberError,
            supportingText = if (cardNumberError) {
                { Text("Please enter a valid 16-digit card number") }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        // Card Holder Name Field
        OutlinedTextField(
            value = cardHolderName,
            onValueChange = onCardHolderNameChange,
            label = { Text("Cardholder Name") },
            placeholder = { Text("") },
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = null)
            },
            isError = cardHolderError,
            supportingText = if (cardHolderError) {
                { Text("Please enter the cardholder name") }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        // Expiry and CVV Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Expiry Date Field
            OutlinedTextField(
                value = expiryDate,
                onValueChange = onExpiryDateChange,
                label = { Text("Expiry") },
                placeholder = { Text("MM/YY") },
                leadingIcon = {
                    Icon(Icons.Filled.CalendarToday, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = expiryError,
                supportingText = if (expiryError) {
                    { Text("MM/YY") }
                } else null,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // CVV Field
            OutlinedTextField(
                value = cvv,
                onValueChange = onCvvChange,
                label = { Text("CVV") },
                placeholder = { Text("") },
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                isError = cvvError,
                supportingText = if (cvvError) {
                    { Text("3 digits") }
                } else null,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }
    }
}

/**
 * Billing address section
 */
@Composable
private fun BillingAddressSection(
    billingAddress: String,
    onAddressChange: (String) -> Unit,
    addressError: Boolean,
    city: String,
    onCityChange: (String) -> Unit,
    postalCode: String,
    onPostalCodeChange: (String) -> Unit
) {
    Column {
        // Section Header
        SectionHeader(
            icon = Icons.Filled.Home,
            title = "Billing Address"
        )

        Spacer(Modifier.height(16.dp))

        // Address Field
        OutlinedTextField(
            value = billingAddress,
            onValueChange = onAddressChange,
            label = { Text("Street Address") },
            placeholder = { Text("") },
            leadingIcon = {
                Icon(Icons.Filled.LocationOn, contentDescription = null)
            },
            isError = addressError,
            supportingText = if (addressError) {
                { Text("Please enter your billing address") }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = 2,
            maxLines = 3
        )

        Spacer(Modifier.height(12.dp))

        // City and Postal Code Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // City Field
            OutlinedTextField(
                value = city,
                onValueChange = onCityChange,
                label = { Text("City") },
                placeholder = { Text("") },
                leadingIcon = {
                    Icon(Icons.Filled.LocationCity, contentDescription = null)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Postal Code Field
            OutlinedTextField(
                value = postalCode,
                onValueChange = onPostalCodeChange,
                label = { Text("Postal Code") },
                placeholder = { Text("") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }
    }
}

/**
 * Section header with icon and title
 */
@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Place Order button with processing animation
 */
@Composable
private fun PlaceOrderButton(
    isProcessing: Boolean,
    totalAmount: Double,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50)
        ),
        enabled = !isProcessing
    ) {
        if (isProcessing) {
            // Processing state with loading indicator
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "Processing...",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
        } else {
            // Normal state
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Place Order",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                Text(
                    "Rs.%.2f".format(totalAmount),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/**
 * Trust indicators at the bottom
 */
@Composable
private fun TrustIndicators() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TrustBadge(icon = Icons.Filled.Security, text = "Secure")
        TrustBadge(icon = Icons.Filled.VerifiedUser, text = "Verified")
        TrustBadge(icon = Icons.Filled.Lock, text = "Encrypted")
    }
}

/**
 * Individual trust badge
 */
@Composable
private fun TrustBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * Helper function to format expiry date as MM/YY
 */
private fun formatExpiryDate(input: String): String {
    val digitsOnly = input.filter { it.isDigit() }
    return when {
        digitsOnly.length <= 2 -> digitsOnly
        else -> "${digitsOnly.substring(0, 2)}/${digitsOnly.substring(2, minOf(4, digitsOnly.length))}"
    }
}

