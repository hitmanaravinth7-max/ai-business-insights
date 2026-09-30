package com.example.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PasswordStrength
import com.example.ui.AuthUiState
import com.example.ui.MainViewModel
import com.example.ui.theme.*

@Composable
fun AuthScreen(viewModel: MainViewModel) {
    val authState by viewModel.authUiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Navy950, Navy900, Color(0xFF0B132B))
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Brand Header
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Cyan500, Blue600)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "AI Business Consultant Logo",
                    tint = White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "BizAdvisor AI",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = White,
                    letterSpacing = 0.5.sp
                )
            )

            Text(
                text = "Decision-Support Intelligence for Small Businesses",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Slate400,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Auth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Navy700, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900.copy(alpha = 0.92f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Mode Selector Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Navy800)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TabButton(
                            text = "Log In",
                            isSelected = authState.isLoginMode,
                            onClick = { if (!authState.isLoginMode) viewModel.toggleAuthMode() },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            text = "Create Account",
                            isSelected = !authState.isLoginMode,
                            onClick = { if (authState.isLoginMode) viewModel.toggleAuthMode() },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error banner
                    if (authState.errorMessage != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            color = Rose500.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Rose500.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Rose500, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = authState.errorMessage ?: "",
                                    color = Color(0xFFFFB4AB),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Info banner
                    if (authState.infoMessage != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            color = Cyan500.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Cyan500.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Cyan400, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = authState.infoMessage ?: "",
                                    color = Cyan400,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    AnimatedContent(
                        targetState = authState.isLoginMode,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(150))
                        },
                        label = "AuthModeTransition"
                    ) { isLogin ->
                        if (isLogin) {
                            LoginForm(
                                authState = authState,
                                viewModel = viewModel,
                                onNextFocus = { focusManager.moveFocus(FocusDirection.Down) },
                                onSubmit = { viewModel.submitLogin() }
                            )
                        } else {
                            RegisterForm(
                                authState = authState,
                                viewModel = viewModel,
                                onNextFocus = { focusManager.moveFocus(FocusDirection.Down) },
                                onSubmit = { viewModel.submitRegistration() }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Demo Quick-Fill Bar
            DemoAccountBar(onSelectDemo = { email, name, biz ->
                viewModel.fillDemoCredentials(email, name, biz)
            })

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Cyan500 else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Navy950 else Slate300
            )
        )
    }
}

@Composable
private fun LoginForm(
    authState: AuthUiState,
    viewModel: MainViewModel,
    onNextFocus: () -> Unit,
    onSubmit: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = authState.emailInput,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("Business Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Slate400) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { onNextFocus() }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_email_input"),
            colors = authTextFieldColors()
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = authState.passwordInput,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Slate400) },
            trailingIcon = {
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(
                        imageVector = if (authState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility",
                        tint = Slate400
                    )
                }
            },
            visualTransformation = if (authState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_password_input"),
            colors = authTextFieldColors()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Remember Me & Forgot Password Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { viewModel.toggleRememberMe(!authState.rememberMe) }
            ) {
                Checkbox(
                    checked = authState.rememberMe,
                    onCheckedChange = { viewModel.toggleRememberMe(it) },
                    colors = CheckboxDefaults.colors(checkedColor = Cyan500, checkmarkColor = Navy950)
                )
                Text(
                    text = "Remember me",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate300
                )
            }

            TextButton(
                onClick = { viewModel.forgotPassword() },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Forgot password?",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = Cyan400
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            enabled = !authState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("login_submit_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy950)
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(color = Navy950, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
            } else {
                Text("Log In", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun RegisterForm(
    authState: AuthUiState,
    viewModel: MainViewModel,
    onNextFocus: () -> Unit,
    onSubmit: () -> Unit
) {
    val passwordStrength = viewModel.authRepo.evaluatePasswordStrength(authState.passwordInput)

    Column {
        OutlinedTextField(
            value = authState.fullNameInput,
            onValueChange = { viewModel.updateFullName(it) },
            label = { Text("Full Name") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Slate400) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { onNextFocus() }),
            modifier = Modifier.fillMaxWidth().testTag("register_fullname_input"),
            colors = authTextFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = authState.businessNameInput,
            onValueChange = { viewModel.updateBusinessName(it) },
            label = { Text("Business / Company Name") },
            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = Slate400) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { onNextFocus() }),
            modifier = Modifier.fillMaxWidth().testTag("register_bizname_input"),
            colors = authTextFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = authState.emailInput,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("Work Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Slate400) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { onNextFocus() }),
            modifier = Modifier.fillMaxWidth().testTag("register_email_input"),
            colors = authTextFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = authState.passwordInput,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Create Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Slate400) },
            trailingIcon = {
                IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                    Icon(
                        imageVector = if (authState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle visibility",
                        tint = Slate400
                    )
                }
            },
            visualTransformation = if (authState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { onNextFocus() }),
            modifier = Modifier.fillMaxWidth().testTag("register_password_input"),
            colors = authTextFieldColors()
        )

        // Password Strength Indicator
        if (authState.passwordInput.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            PasswordStrengthBar(strength = passwordStrength)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = authState.confirmPasswordInput,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = Slate400) },
            visualTransformation = if (authState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            modifier = Modifier.fillMaxWidth().testTag("register_confirm_password_input"),
            colors = authTextFieldColors()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Terms and conditions
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleTermsAccepted(!authState.termsAccepted) }
        ) {
            Checkbox(
                checked = authState.termsAccepted,
                onCheckedChange = { viewModel.toggleTermsAccepted(it) },
                colors = CheckboxDefaults.colors(checkedColor = Cyan500, checkmarkColor = Navy950)
            )
            Text(
                text = "I agree to the Terms of Service & Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = Slate300
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            enabled = !authState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("register_submit_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Navy950)
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(color = Navy950, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
            } else {
                Text("Create Account", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
private fun PasswordStrengthBar(strength: PasswordStrength) {
    val color = when (strength) {
        PasswordStrength.WEAK -> Rose500
        PasswordStrength.FAIR -> Amber500
        PasswordStrength.GOOD -> Cyan400
        PasswordStrength.STRONG -> Emerald500
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Strength: ${strength.label}",
                style = MaterialTheme.typography.labelSmall.copy(color = color, fontWeight = FontWeight.Bold)
            )
            Text(
                text = if (strength == PasswordStrength.STRONG) "Excellent" else "Use upper, numbers & symbols",
                style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 1..4) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(if (i <= strength.score) color else Navy700)
                )
            }
        }
    }
}

@Composable
private fun DemoAccountBar(onSelectDemo: (String, String, String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "OR EXPLORE INSTANT DEMO DATA",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Slate500,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onSelectDemo("elena@apexgear.com", "Elena Vance", "ApexGear Outdoor") },
                modifier = Modifier.weight(1f).testTag("demo_retail_button"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Navy600),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate200)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ApexGear", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Text("E-Commerce", style = MaterialTheme.typography.labelSmall.copy(color = Cyan400, fontSize = 10.sp))
                }
            }

            OutlinedButton(
                onClick = { onSelectDemo("marcus@cloudpulse.io", "Marcus Cole", "CloudPulse SaaS") },
                modifier = Modifier.weight(1f).testTag("demo_saas_button"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Navy600),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate200)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CloudPulse", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Text("SaaS / Tech", style = MaterialTheme.typography.labelSmall.copy(color = Emerald500, fontSize = 10.sp))
                }
            }
        }
    }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Cyan400,
    unfocusedBorderColor = Navy700,
    focusedLabelColor = Cyan400,
    unfocusedLabelColor = Slate400,
    focusedTextColor = White,
    unfocusedTextColor = Slate200,
    cursorColor = Cyan400
)
