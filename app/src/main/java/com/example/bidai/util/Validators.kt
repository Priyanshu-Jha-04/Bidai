package com.example.bidai.util

import java.text.NumberFormat
import java.util.Locale

//validators
object Validators {
    private val EMAIL = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    private val PHONE = Regex("^\\d{10}$")
    private const val MAX_PRICE = 9_999_999_999.0

    fun name(value: String): String? = when {
        value.isBlank() -> "Enter your name"
        value.trim().length < 2 -> "Name must be at least 2 characters"
        else -> null
    }

    fun email(value: String): String? = when {
        value.isBlank() -> "Enter your email"
        !EMAIL.matches(value.trim()) -> "Enter a valid email address"
        else -> null
    }

    fun phone(value: String): String? = when {
        value.isBlank() -> "Enter your mobile number"
        !PHONE.matches(value.trim()) -> "Enter a valid 10-digit mobile number"
        else -> null
    }

    //login password
    fun password(value: String): String? = if (value.isEmpty()) "Enter your password" else null

    fun newPassword(value: String): String? = when {
        value.isEmpty() -> "Create a password"
        value.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }

    fun confirmPassword(password: String, confirm: String): String? = when {
        confirm.isEmpty() -> "Confirm your password"
        confirm != password -> "Passwords do not match"
        else -> null
    }

    fun title(value: String): String? = when {
        value.isBlank() -> "Enter a title"
        value.trim().length < 3 -> "Title must be at least 3 characters"
        value.trim().length > 120 -> "Title must be 120 characters or fewer"
        else -> null
    }

    fun price(value: String): String? {
        val number = value.trim().toDoubleOrNull()
        return when {
            value.isBlank() -> "Enter a price"
            number == null -> "Enter a valid number"
            number <= 0 -> "Price must be greater than 0"
            number > MAX_PRICE -> "Price is too large"
            else -> null
        }
    }

    fun category(value: String): String? = if (value.isBlank()) "Choose a category" else null
}

//rupees
fun formatPrice(price: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
    val whole = price % 1.0 == 0.0
    format.minimumFractionDigits = if (whole) 0 else 2
    format.maximumFractionDigits = if (whole) 0 else 2
    return format.format(price)
}

//rupees spaced
fun formatRupees(price: Double): String {
    val format = NumberFormat.getNumberInstance(Locale.forLanguageTag("en-IN"))
    val whole = price % 1.0 == 0.0
    format.minimumFractionDigits = if (whole) 0 else 2
    format.maximumFractionDigits = if (whole) 0 else 2
    return "₹ " + format.format(price)
}

//listed date
fun formatListedDate(iso: String): String = runCatching {
    java.time.Instant.parse(iso).atZone(java.time.ZoneId.systemDefault())
        .format(java.time.format.DateTimeFormatter.ofPattern("d MMMM", Locale.ENGLISH))
}.getOrDefault("")
