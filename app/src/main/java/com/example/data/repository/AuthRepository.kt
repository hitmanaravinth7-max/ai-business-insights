package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDao
import com.example.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

enum class PasswordStrength(val label: String, val score: Int) {
    WEAK("Weak", 1),
    FAIR("Fair", 2),
    GOOD("Good", 3),
    STRONG("Strong", 4)
}

class AuthRepository(
    private val appDao: AppDao,
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LOGGED_IN_USER_ID = "logged_in_user_id"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_SAVED_EMAIL = "saved_email"
    }

    suspend fun getLoggedInUserId(): Long? = withContext(Dispatchers.IO) {
        val id = prefs.getLong(KEY_LOGGED_IN_USER_ID, -1L)
        if (id > 0) id else null
    }

    fun isRememberMeEnabled(): Boolean = prefs.getBoolean(KEY_REMEMBER_ME, false)

    fun getSavedEmail(): String = prefs.getString(KEY_SAVED_EMAIL, "") ?: ""

    suspend fun getLoggedInUser(): UserEntity? = withContext(Dispatchers.IO) {
        val id = getLoggedInUserId() ?: return@withContext null
        appDao.getUserById(id)
    }

    suspend fun login(email: String, password: String, rememberMe: Boolean): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val user = appDao.getUserByEmail(cleanEmail) ?: return@withContext Result.failure(Exception("No account found with this email."))

        val hash = hashPassword(password, user.salt)
        if (hash != user.passwordHash) {
            return@withContext Result.failure(Exception("Incorrect password. Please verify and try again."))
        }

        // Update last login
        appDao.updateUser(user.copy(lastLoginAt = System.currentTimeMillis()))

        prefs.edit().apply {
            putLong(KEY_LOGGED_IN_USER_ID, user.id)
            putBoolean(KEY_REMEMBER_ME, rememberMe)
            if (rememberMe) {
                putString(KEY_SAVED_EMAIL, cleanEmail)
            } else {
                remove(KEY_SAVED_EMAIL)
            }
            apply()
        }

        Result.success(user)
    }

    suspend fun register(
        fullName: String,
        businessName: String,
        email: String,
        password: String,
        rememberMe: Boolean = true
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val existing = appDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return@withContext Result.failure(Exception("An account with this email already exists."))
        }

        val salt = generateSalt()
        val passwordHash = hashPassword(password, salt)

        val newUser = UserEntity(
            email = cleanEmail,
            fullName = fullName.trim(),
            businessName = businessName.trim(),
            passwordHash = passwordHash,
            salt = salt
        )

        val userId = appDao.insertUser(newUser)
        val created = newUser.copy(id = userId)

        prefs.edit().apply {
            putLong(KEY_LOGGED_IN_USER_ID, userId)
            putBoolean(KEY_REMEMBER_ME, rememberMe)
            if (rememberMe) {
                putString(KEY_SAVED_EMAIL, cleanEmail)
            }
            apply()
        }

        Result.success(created)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        val remember = prefs.getBoolean(KEY_REMEMBER_ME, false)
        prefs.edit().apply {
            remove(KEY_LOGGED_IN_USER_ID)
            if (!remember) {
                remove(KEY_SAVED_EMAIL)
            }
            apply()
        }
    }

    fun evaluatePasswordStrength(password: String): PasswordStrength {
        if (password.length < 6) return PasswordStrength.WEAK
        var score = 0
        if (password.length >= 8) score++
        if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++

        return when (score) {
            4 -> PasswordStrength.STRONG
            3 -> PasswordStrength.GOOD
            2 -> PasswordStrength.FAIR
            else -> PasswordStrength.WEAK
        }
    }

    private fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest((password + salt).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return saltBytes.joinToString("") { "%02x".format(it) }
    }
}
