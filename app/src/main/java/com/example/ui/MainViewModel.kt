package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.repository.AuthRepository
import com.example.data.repository.BusinessRepository
import com.example.domain.analytics.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination(val route: String, val title: String) {
    data object Dashboard : ScreenDestination("dashboard", "Dashboard")
    data object Predictive : ScreenDestination("predictive", "Forecast")
    data object Recommendations : ScreenDestination("recommendations", "Insights")
    data object AiChat : ScreenDestination("ai_chat", "AI Advisor")
    data object DataManager : ScreenDestination("data_manager", "Business Data")
}

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val currentUser: UserEntity? = null,
    val isLoginMode: Boolean = true,
    val emailInput: String = "",
    val passwordInput: String = "",
    val fullNameInput: String = "",
    val businessNameInput: String = "",
    val confirmPasswordInput: String = "",
    val rememberMe: Boolean = true,
    val termsAccepted: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val appDao = database.appDao()

    val authRepo = AuthRepository(appDao, application)
    val businessRepo = BusinessRepository(appDao)

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Dashboard)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    // Observables for active business profile
    val businessProfile: StateFlow<BusinessProfileEntity?> = _currentUserId
        .flatMapLatest { userId ->
            if (userId != null) businessRepo.getProfileFlow(userId) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Observables for monthly metrics
    val monthlyMetrics: StateFlow<List<MonthlyMetricEntity>> = businessProfile
        .flatMapLatest { profile ->
            if (profile != null) businessRepo.getMonthlyMetricsFlow(profile.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observables for products
    val products: StateFlow<List<ProductPerformanceEntity>> = businessProfile
        .flatMapLatest { profile ->
            if (profile != null) businessRepo.getProductsFlow(profile.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observables for customer segments
    val customerSegments: StateFlow<List<CustomerSegmentEntity>> = businessProfile
        .flatMapLatest { profile ->
            if (profile != null) businessRepo.getSegmentsFlow(profile.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observables for marketing channels
    val marketingChannels: StateFlow<List<MarketingChannelEntity>> = businessProfile
        .flatMapLatest { profile ->
            if (profile != null) businessRepo.getChannelsFlow(profile.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observables for recommendations
    val recommendations: StateFlow<List<BusinessRecommendationEntity>> = businessProfile
        .flatMapLatest { profile ->
            if (profile != null) businessRepo.getRecommendationsFlow(profile.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Observables for chat messages
    val chatMessages: StateFlow<List<ChatMessageEntity>> = businessProfile
        .flatMapLatest { profile ->
            if (profile != null) businessRepo.getChatMessagesFlow(profile.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI recommendation generating state
    private val _isGeneratingAiRecs = MutableStateFlow(false)
    val isGeneratingAiRecs: StateFlow<Boolean> = _isGeneratingAiRecs.asStateFlow()

    // AI chat sending state
    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // What-If Simulation sliders
    private val _simAdSpendDelta = MutableStateFlow(0.0) // %
    val simAdSpendDelta: StateFlow<Double> = _simAdSpendDelta.asStateFlow()

    private val _simPriceDelta = MutableStateFlow(0.0) // %
    val simPriceDelta: StateFlow<Double> = _simPriceDelta.asStateFlow()

    private val _simCostReductionDelta = MutableStateFlow(0.0) // %
    val simCostReductionDelta: StateFlow<Double> = _simCostReductionDelta.asStateFlow()

    // Profile Setup / Edit dialog state
    private val _showProfileDialog = MutableStateFlow(false)
    val showProfileDialog: StateFlow<Boolean> = _showProfileDialog.asStateFlow()

    // Executive Report Dialog state
    private val _showReportDialog = MutableStateFlow(false)
    val showReportDialog: StateFlow<Boolean> = _showReportDialog.asStateFlow()

    init {
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            val savedUserId = authRepo.getLoggedInUserId()
            if (savedUserId != null) {
                val user = authRepo.getLoggedInUser()
                if (user != null) {
                    _currentUserId.value = user.id
                    _authUiState.update {
                        it.copy(
                            isLoggedIn = true,
                            currentUser = user,
                            emailInput = user.email
                        )
                    }
                    ensureProfileAndData(user.id)
                    return@launch
                }
            }

            // If not logged in, prefill saved email if remember me was on
            val savedEmail = authRepo.getSavedEmail()
            if (savedEmail.isNotBlank()) {
                _authUiState.update { it.copy(emailInput = savedEmail, rememberMe = true) }
            }
        }
    }

    private suspend fun ensureProfileAndData(userId: Long) {
        val profile = businessRepo.getProfile(userId)
        if (profile == null) {
            // Seed initial retail data for the user
            businessRepo.seedDemoDataset(userId, "Retail")
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    fun toggleAuthMode() {
        _authUiState.update {
            it.copy(
                isLoginMode = !it.isLoginMode,
                errorMessage = null,
                infoMessage = null
            )
        }
    }

    fun updateEmail(value: String) {
        _authUiState.update { it.copy(emailInput = value, errorMessage = null) }
    }

    fun updatePassword(value: String) {
        _authUiState.update { it.copy(passwordInput = value, errorMessage = null) }
    }

    fun updateFullName(value: String) {
        _authUiState.update { it.copy(fullNameInput = value, errorMessage = null) }
    }

    fun updateBusinessName(value: String) {
        _authUiState.update { it.copy(businessNameInput = value, errorMessage = null) }
    }

    fun updateConfirmPassword(value: String) {
        _authUiState.update { it.copy(confirmPasswordInput = value, errorMessage = null) }
    }

    fun toggleRememberMe(value: Boolean) {
        _authUiState.update { it.copy(rememberMe = value) }
    }

    fun toggleTermsAccepted(value: Boolean) {
        _authUiState.update { it.copy(termsAccepted = value) }
    }

    fun togglePasswordVisibility() {
        _authUiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun dismissAuthMessages() {
        _authUiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    fun fillDemoCredentials(email: String, name: String, bizName: String) {
        _authUiState.update {
            it.copy(
                emailInput = email,
                passwordInput = "Password123!",
                confirmPasswordInput = "Password123!",
                fullNameInput = name,
                businessNameInput = bizName,
                termsAccepted = true,
                errorMessage = null,
                infoMessage = "Demo credentials loaded. Click '${if (it.isLoginMode) "Log In" else "Create Account"}' to continue."
            )
        }
    }

    fun submitLogin() {
        val state = _authUiState.value
        if (state.emailInput.isBlank() || !state.emailInput.contains("@")) {
            _authUiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return
        }
        if (state.passwordInput.isBlank()) {
            _authUiState.update { it.copy(errorMessage = "Please enter your password.") }
            return
        }

        viewModelScope.launch {
            _authUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepo.login(state.emailInput, state.passwordInput, state.rememberMe)
            result.onSuccess { user ->
                _currentUserId.value = user.id
                _authUiState.update {
                    it.copy(
                        isLoggedIn = true,
                        currentUser = user,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                ensureProfileAndData(user.id)
            }.onFailure { ex ->
                _authUiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Login failed. Please check your credentials."
                    )
                }
            }
        }
    }

    fun submitRegistration() {
        val state = _authUiState.value
        if (state.fullNameInput.isBlank()) {
            _authUiState.update { it.copy(errorMessage = "Full name is required.") }
            return
        }
        if (state.businessNameInput.isBlank()) {
            _authUiState.update { it.copy(errorMessage = "Business name is required.") }
            return
        }
        if (state.emailInput.isBlank() || !state.emailInput.contains("@")) {
            _authUiState.update { it.copy(errorMessage = "Please provide a valid business email.") }
            return
        }
        if (state.passwordInput.length < 6) {
            _authUiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
            return
        }
        if (state.passwordInput != state.confirmPasswordInput) {
            _authUiState.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }
        if (!state.termsAccepted) {
            _authUiState.update { it.copy(errorMessage = "Please accept the Terms of Service to proceed.") }
            return
        }

        viewModelScope.launch {
            _authUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepo.register(
                fullName = state.fullNameInput,
                businessName = state.businessNameInput,
                email = state.emailInput,
                password = state.passwordInput,
                rememberMe = state.rememberMe
            )
            result.onSuccess { user ->
                _currentUserId.value = user.id
                _authUiState.update {
                    it.copy(
                        isLoggedIn = true,
                        currentUser = user,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                ensureProfileAndData(user.id)
            }.onFailure { ex ->
                _authUiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Registration failed."
                    )
                }
            }
        }
    }

    fun forgotPassword() {
        val email = _authUiState.value.emailInput
        if (email.isBlank() || !email.contains("@")) {
            _authUiState.update { it.copy(errorMessage = "Enter your account email above to receive reset instructions.") }
        } else {
            _authUiState.update {
                it.copy(
                    infoMessage = "Password reset link sent to $email. Please check your inbox.",
                    errorMessage = null
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
            _currentUserId.value = null
            _authUiState.update {
                AuthUiState(
                    isLoggedIn = false,
                    currentUser = null,
                    isLoginMode = true,
                    rememberMe = authRepo.isRememberMeEnabled(),
                    emailInput = authRepo.getSavedEmail()
                )
            }
            _currentScreen.value = ScreenDestination.Dashboard
        }
    }

    fun loadDemoDataset(type: String) {
        val userId = _currentUserId.value ?: return
        viewModelScope.launch {
            businessRepo.seedDemoDataset(userId, type)
        }
    }

    fun triggerAiRecommendations() {
        val profile = businessProfile.value ?: return
        viewModelScope.launch {
            _isGeneratingAiRecs.value = true
            businessRepo.generateAiRecommendationsWithGemini(profile.id)
            _isGeneratingAiRecs.value = false
        }
    }

    fun updateRecommendationStatus(id: Long, status: String) {
        viewModelScope.launch {
            businessRepo.updateRecommendationStatus(id, status)
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val profile = businessProfile.value ?: return
        viewModelScope.launch {
            _isAiThinking.value = true
            businessRepo.askConsultantChat(profile.id, text.trim())
            _isAiThinking.value = false
        }
    }

    fun updateSimulationSliders(adSpend: Double, price: Double, costReduction: Double) {
        _simAdSpendDelta.value = adSpend
        _simPriceDelta.value = price
        _simCostReductionDelta.value = costReduction
    }

    fun openProfileDialog() {
        _showProfileDialog.value = true
    }

    fun closeProfileDialog() {
        _showProfileDialog.value = false
    }

    fun saveBusinessProfile(
        companyName: String,
        industry: String,
        monthlyBudget: Double,
        teamSize: Int,
        primaryGoal: String,
        targetAudience: String
    ) {
        val current = businessProfile.value ?: return
        val updated = current.copy(
            companyName = companyName,
            industry = industry,
            monthlyBudget = monthlyBudget,
            teamSize = teamSize,
            primaryGoal = primaryGoal,
            targetAudience = targetAudience
        )
        viewModelScope.launch {
            businessRepo.saveProfile(updated)
            _showProfileDialog.value = false
        }
    }

    fun openReportDialog() {
        _showReportDialog.value = true
    }

    fun closeReportDialog() {
        _showReportDialog.value = false
    }

    fun addManualMetric(
        monthName: String,
        revenue: Double,
        cogs: Double,
        opex: Double,
        marketing: Double,
        newCust: Int,
        churn: Double
    ) {
        val profile = businessProfile.value ?: return
        val currentList = monthlyMetrics.value
        val nextIdx = currentList.size + 1
        val metric = MonthlyMetricEntity(
            businessId = profile.id,
            monthIndex = nextIdx,
            monthName = monthName,
            revenue = revenue,
            cogs = cogs,
            operatingExpenses = opex,
            marketingSpend = marketing,
            newCustomers = newCust,
            churnRate = churn,
            avgOrderValue = if (newCust > 0) revenue / newCust else 80.0
        )
        viewModelScope.launch {
            businessRepo.addMonthlyMetric(metric)
        }
    }
}
