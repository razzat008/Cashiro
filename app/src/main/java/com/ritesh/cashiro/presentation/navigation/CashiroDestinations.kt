package com.ritesh.cashiro.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.Serializable

// Shared Element Keys for consistent shared transitions
object SharedElementKeys {
    const val FAB_ADD_TRANSACTION = "fab_add_transaction_key"
    const val TRANSACTION_ITEM_PREFIX = "transaction_item_key_"
    const val ACCOUNT_CARD_PREFIX = "account_card_key_"
    const val SUBSCRIPTION_CARD_PREFIX = "subscription_card_key_"
    const val BUDGET_CARD_PREFIX = "budget_card_key_"
    const val SEARCH_BUTTON = "search_transaction_key"
    
    fun transactionItem(id: Long) = "$TRANSACTION_ITEM_PREFIX$id"
    fun accountCard(bankName: String, last4: String) = "$ACCOUNT_CARD_PREFIX${bankName}_$last4"
    fun subscriptionCard(id: Long) = "$SUBSCRIPTION_CARD_PREFIX$id"
    fun budgetCard(id: Long) = "$BUDGET_CARD_PREFIX$id"
}

// Centralized transition definitions
object CashiroTransitions {
    private const val DURATION_NORMAL = 300
    private const val DURATION_FAST = 200
    
    // Horizontal slide transitions for sub-screens
    val horizontalSlideEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(DURATION_NORMAL, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(DURATION_NORMAL))
    }
    
    val horizontalSlideExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { -it / 4 },
            animationSpec = tween(DURATION_NORMAL, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(DURATION_NORMAL))
    }
    
    val horizontalSlidePopEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { -it / 4 },
            animationSpec = tween(DURATION_NORMAL, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(DURATION_NORMAL))
    }
    
    val horizontalSlidePopExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(DURATION_NORMAL, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(DURATION_NORMAL))
    }
    
    // Vertical slide transitions
    val verticalSlideEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(DURATION_NORMAL, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(DURATION_NORMAL))
    }
    
    val verticalSlideExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(DURATION_NORMAL, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(DURATION_NORMAL))
    }
    
    // FAB to screen scale transitions
    val fabScaleEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(animationSpec = tween(DURATION_NORMAL)) +
            scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(DURATION_NORMAL + 100, easing = FastOutSlowInEasing)
            )
    }
    
    val fabScaleExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(animationSpec = tween(DURATION_FAST)) +
            scaleOut(
                targetScale = 1.1f,
                animationSpec = tween(DURATION_FAST, easing = FastOutSlowInEasing)
            )
    }
    
    val fabScalePopEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(animationSpec = tween(DURATION_NORMAL)) +
            scaleIn(
                initialScale = 1.1f,
                animationSpec = tween(DURATION_NORMAL, easing = FastOutSlowInEasing)
            )
    }
    
    val fabScalePopExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(animationSpec = tween(DURATION_FAST)) +
            scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(DURATION_FAST, easing = FastOutSlowInEasing)
            )
    }
    
    // Scale transitions for detail screens
    val scaleEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(animationSpec = tween(DURATION_NORMAL)) +
            scaleIn(animationSpec = tween(DURATION_NORMAL, easing = FastOutSlowInEasing))
    }
    
    val scaleExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(animationSpec = tween(DURATION_FAST)) +
            scaleOut(animationSpec = tween(DURATION_FAST, easing = FastOutSlowInEasing))
    }
    
    // None transitions - for screens using shared element transitions entirely
    val noneEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        EnterTransition.None
    }
    
    val noneExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        ExitTransition.None
    }
}

// Define navigation destinations using Kotlin Serialization
@Serializable object AppLock

@Serializable object OnBoarding

@Serializable object Home

@Serializable
data class Transactions(
    val category: String? = null,
    val merchant: String? = null,
    val period: String? = null,
    val currency: String? = null,
    val type: String? = null,
    val focusSearch: Boolean = false
)


@Serializable object Settings
@Serializable object Subscriptions
@Serializable object Categories

@Serializable object Analytics

@Serializable object Chat

@Serializable data class TransactionDetail(val transactionId: Long, val sharedElementKey: String? = null)

@Serializable data class AddTransaction(val initialTab: Int = 0)

@Serializable data class AccountDetail(val bankName: String, val accountLast4: String)

@Serializable object UnrecognizedSms

@Serializable object Faq

@Serializable object Rules

@Serializable object CreateRule

@Serializable object Appearance

@Serializable object ManageAccounts

@Serializable object Profile

@Serializable object SmsSettings

@Serializable object NotificationSettings

@Serializable data class Budgets(val sharedElementPrefix: Long? = null)

@Serializable object DeveloperOptions

@Serializable object AddAccount

// Routes where bottom navigation should be visible
val BOTTOM_NAV_ROUTES = setOf(
    Home::class.qualifiedName,
    Analytics::class.qualifiedName
)
