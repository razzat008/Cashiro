package com.ritesh.cashiro.presentation.ui.features.budgets
 
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.ritesh.cashiro.R
import com.ritesh.cashiro.data.database.entity.AccountBalanceEntity
import com.ritesh.cashiro.data.database.entity.BudgetPeriod
import com.ritesh.cashiro.data.database.entity.BudgetTrackType
import com.ritesh.cashiro.data.database.entity.BudgetType
import com.ritesh.cashiro.data.database.entity.CategoryEntity
import com.ritesh.cashiro.data.database.entity.SubcategoryEntity
import com.ritesh.cashiro.presentation.common.icons.CategoryMapping
import com.ritesh.cashiro.presentation.effects.overScrollVertical
import com.ritesh.cashiro.presentation.ui.components.BrandIcon
import com.ritesh.cashiro.presentation.ui.components.BudgetAnimatedGradientMeshCard
import com.ritesh.cashiro.presentation.ui.components.CategorySelectionSheet
import com.ritesh.cashiro.presentation.ui.components.ColorPickerContent
import com.ritesh.cashiro.presentation.ui.features.accounts.NumberPad
import com.ritesh.cashiro.presentation.ui.theme.Spacing
import com.ritesh.cashiro.utils.CurrencyFormatter
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditBudgetSheet(
    budgetState: EditBudgetState,
    categories: List<CategoryEntity>,
    subcategoriesMap: Map<Long, List<SubcategoryEntity>> = emptyMap(),
    allAccounts: List<AccountBalanceEntity> = emptyList(),
    onAmountChange: (BigDecimal) -> Unit,
    onNameChange: (String) -> Unit,
    onStartDateChange: (LocalDateTime) -> Unit,
    onEndDateChange: (LocalDateTime) -> Unit,
    onPeriodTypeChange: (BudgetPeriod) -> Unit,
    onTrackTypeChange: (BudgetTrackType) -> Unit,
    onBudgetTypeChange: (BudgetType) -> Unit,
    onAccountIdsChange: (List<String>) -> Unit,
    onColorChange: (String) -> Unit,
    onAddCategoryLimit: (String, BigDecimal) -> Unit,
    onRemoveCategoryLimit: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    var showNumberPad by remember { mutableStateOf(false) }
    var editingCategoryLimit by remember { mutableStateOf<String?>(null) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showAccountSheet by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var pendingCategoryName by remember { mutableStateOf<String?>(null) }
    var showTypeInfoSheet by remember { mutableStateOf(false) }
    var showTrackInfoSheet by remember { mutableStateOf(false) }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    // Date Picker Dialogs
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = budgetState.startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onStartDateChange(LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()))
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = budgetState.endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onEndDateChange(LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()))
                    }
                    showEndDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete Budget?") },
            text = { Text("Are you sure you want to delete this budget? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete?.invoke()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Account Selection Sheet
    if (showAccountSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAccountSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            AccountMultiSelectionSheet(
                allAccounts = allAccounts,
                selectedAccountIds = budgetState.accountIds,
                onSelectionChange = onAccountIdsChange,
                onDismiss = { showAccountSheet = false }
            )
        }
    }

    // Color Picker Sheet
    if (showColorPicker) {
        ModalBottomSheet(
            onDismissRequest = { showColorPicker = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Box{
                ColorPickerContent(
                    initialColor = budgetState.color.toColorInt(),
                    onColorChanged = { colorInt ->
                        onColorChange(String.format("#%06X", 0xFFFFFF and colorInt))
                        showColorPicker = false
                    }
                )
            }
        }
    }
    
    // Number pad for budget amount
    if (showNumberPad && editingCategoryLimit == null && pendingCategoryName == null) {
        ModalBottomSheet(
            onDismissRequest = { showNumberPad = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            NumberPad(
                initialValue = budgetState.amount.toString(),
                onDone = { value ->
                    onAmountChange(value.toBigDecimalOrNull() ?: BigDecimal.ZERO)
                    showNumberPad = false
                },
                title = if (budgetState.isNewBudget) "Set Budget Amount" else "Update Budget Amount"
            )
        }
    }
    
    // Number pad for category limit
    if (showNumberPad && (editingCategoryLimit != null || pendingCategoryName != null)) {
        val categoryName = editingCategoryLimit ?: pendingCategoryName ?: ""
        val existingLimit = budgetState.categoryLimits.find { it.categoryName == categoryName }
        
        ModalBottomSheet(
            onDismissRequest = { 
                showNumberPad = false
                editingCategoryLimit = null
                pendingCategoryName = null
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            NumberPad(
                initialValue = existingLimit?.limitAmount?.toString() ?: "0",
                onDone = { value ->
                    val amount = value.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    if (amount > BigDecimal.ZERO) {
                        onAddCategoryLimit(categoryName, amount)
                    }
                    showNumberPad = false
                    editingCategoryLimit = null
                    pendingCategoryName = null
                },
                title = "Set Limit for $categoryName"
            )
        }
    }
    
    // Category selection sheet
    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            CategorySelectionSheet(
                categories = categories.filter { !it.isIncome },
                subcategoriesMap = subcategoriesMap,
                onSelectionComplete = { category, _ ->
                    showCategorySheet = false
                    pendingCategoryName = category.name
                    showNumberPad = true
                },
                onDismiss = { showCategorySheet = false }
            )
        }
    }

    if (showTypeInfoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTypeInfoSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            BudgetTypeSelectionSheet(
                onTypeSelected = {
                    scope.launch {
                        sheetState.hide()
                        onBudgetTypeChange(it)
                        showTypeInfoSheet = false
                    }
                },
                onDismiss = { showTypeInfoSheet = false }
            )
        }
    }

    if (showTrackInfoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTrackInfoSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            BudgetTrackTypeSelectionSheet(
                onTrackTypeSelected = {
                    scope.launch {
                        sheetState.hide()
                        onTrackTypeChange(it)
                        showTrackInfoSheet = false
                    }
                },
                onDismiss = { showTrackInfoSheet = false }
            )
        }
    }
    
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .imePadding()
                .fillMaxSize()
                .clip(RoundedCornerShape(28.dp))
                .overScrollVertical()
                .verticalScroll(
                    state = rememberScrollState()
                )
                .padding(horizontal = Spacing.md, vertical = Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = if (budgetState.isNewBudget) "Create Budget" else "Edit Budget",
                style = MaterialTheme.typography.titleMediumEmphasized,
                fontWeight = FontWeight.Bold
            )
            
            // Budget Preview Card
            BudgetPreviewCard(
                name = budgetState.name.ifBlank { budgetState.getDefaultName() },
                amount = budgetState.amount,
                startDate = budgetState.startDate,
                endDate = budgetState.endDate,
                currency = budgetState.currency,
                categoryLimitsCount = budgetState.categoryLimits.size,
                color = Color(budgetState.color.toColorInt())
            )

            // Periodic Type Selector
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Budget Period",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    BudgetPeriod.entries.forEachIndexed { index, period ->
                        SegmentedButton(
                            selected = budgetState.periodType == period,
                            onClick = { onPeriodTypeChange(period) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = BudgetPeriod.entries.size),
                            colors = SegmentedButtonDefaults.colors(
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                                inactiveBorderColor = Color.Transparent,
                                activeBorderColor = Color.Transparent
                            ),
                            icon = {},
                            label = { 
                                Text(
                                    period.name.lowercase().replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                                    },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }

            // Date Range Display/Pickers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                 Surface(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Starts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = budgetState.startDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (budgetState.periodType == BudgetPeriod.CUSTOM) {
                    Surface(
                        onClick = { showEndDatePicker = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Ends", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = budgetState.endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                     Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Ends", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = budgetState.endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Budget Type and Tracking
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tracking Mode
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Track",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = "Track Info",
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { showTrackInfoSheet = true },
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = budgetState.trackType == BudgetTrackType.ADDED_ONLY,
                            onClick = { onTrackTypeChange(BudgetTrackType.ADDED_ONLY) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            colors = SegmentedButtonDefaults.colors(
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                                inactiveBorderColor = Color.Transparent,
                                activeBorderColor = Color.Transparent
                            ),
                            label = { Text("Added", fontSize = 11.sp) }
                        )
                        SegmentedButton(
                            selected = budgetState.trackType == BudgetTrackType.ALL_TRANSACTIONS,
                            onClick = { onTrackTypeChange(BudgetTrackType.ALL_TRANSACTIONS) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            colors = SegmentedButtonDefaults.colors(
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                                inactiveBorderColor = Color.Transparent,
                                activeBorderColor = Color.Transparent
                            ),
                            label = { Text("All", fontSize = 11.sp) }
                        )
                    }
                }

                // Budget Type
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Type",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = "Type Info",
                            modifier = Modifier
                                .size(14.dp)
                                .clickable { showTypeInfoSheet = true },
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    }
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = budgetState.budgetType == BudgetType.EXPENSE,
                            onClick = { onBudgetTypeChange(BudgetType.EXPENSE) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            colors = SegmentedButtonDefaults.colors(
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                                inactiveBorderColor = Color.Transparent,
                                activeBorderColor = Color.Transparent
                            ),
                            label = { Text("Expense", fontSize = 11.sp) }
                        )
                        SegmentedButton(
                            selected = budgetState.budgetType == BudgetType.SAVINGS,
                            onClick = { onBudgetTypeChange(BudgetType.SAVINGS) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            colors = SegmentedButtonDefaults.colors(
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurface,
                                inactiveBorderColor = Color.Transparent,
                                activeBorderColor = Color.Transparent
                            ),
                            label = { Text("Savings", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Accounts Selection
            Surface(
                onClick = { showAccountSheet = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Accounts", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = if (budgetState.accountIds.isEmpty()) "All Accounts" 
                                   else "${budgetState.accountIds.size} Accounts Selected",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
            
            // Stats/Amount and Color Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Budget Amount Input
                Surface(
                    onClick = { 
                        editingCategoryLimit = null
                        pendingCategoryName = null
                        showNumberPad = true 
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.PieChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Amount", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text(
                                text = CurrencyFormatter.formatCurrency(budgetState.amount, budgetState.currency),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Color Picker Trigger
                Surface(
                    onClick = { showColorPicker = true },
                    modifier = Modifier.width(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Color", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(budgetState.color.toColorInt()))
                        )
                    }
                }
            }
            
            // Budget Name Input
            TextField(
                value = budgetState.name,
                onValueChange = onNameChange,
                label = { Text("Budget Name (optional)", fontWeight = FontWeight.SemiBold) },
                placeholder = { Text(budgetState.getDefaultName()) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = { Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null) }
            )
            
            // Category Limits Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Category Limits",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        TextButton(
                            onClick = { showCategorySheet = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Limit")
                        }
                    }
                    
                    if (budgetState.categoryLimits.isEmpty()) {
                        Text(
                            text = "No category limits set. Add limits to track spending per category.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = Spacing.sm)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        
                        budgetState.categoryLimits.forEach { limit ->
                            val category = categories.find { it.name == limit.categoryName }
                            val categoryInfo = CategoryMapping.categories[limit.categoryName]
                            
                            val iconRes = category?.iconResId ?: categoryInfo?.iconResId ?: R.drawable.type_stationary_clipboard
                            val colorHex = category?.color ?: categoryInfo?.color?.let { String.format("#%06X", 0xFFFFFF and it.toArgb()) } ?: "#91CC4D"

                            CategoryLimitItem(
                                categoryName = limit.categoryName,
                                limitAmount = limit.limitAmount,
                                currency = budgetState.currency,
                                iconResId = iconRes,
                                backgroundColor = Color(colorHex.toColorInt()),
                                onEdit = {
                                    editingCategoryLimit = limit.categoryName
                                    showNumberPad = true
                                },
                                onRemove = { onRemoveCategoryLimit(limit.categoryName) }
                            )
                            
                            if (limit != budgetState.categoryLimits.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = Spacing.xs),
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
        
        // Action Buttons at Bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Delete button (only for existing budgets)
                if (onDelete != null && !budgetState.isNewBudget) {
                    OutlinedButton(
                        onClick = { showDeleteConfirmation = true },
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.error,
                                    MaterialTheme.colorScheme.error
                                )
                            )
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete budget"
                        )
                    }
                }
                
                // Save button
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    enabled = budgetState.amount > BigDecimal.ZERO,
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(
                        text = if (budgetState.isNewBudget) "Create Budget" else "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryLimitItem(
    categoryName: String,
    limitAmount: BigDecimal,
    currency: String,
    iconResId: Int,
    backgroundColor: Color,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit)
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(backgroundColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Unspecified
                )
            }
            
            Column {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = CurrencyFormatter.formatCurrency(limitAmount, currency),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Remove limit",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun BudgetPreviewCard(
    name: String,
    amount: BigDecimal,
    startDate: LocalDateTime,
    endDate: LocalDateTime,
    currency: String,
    categoryLimitsCount: Int,
    color: Color
) {
    BudgetAnimatedGradientMeshCard(
        budgetColor = color
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PieChart,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = color
                    )
                }
                
                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${startDate.format(DateTimeFormatter.ofPattern("MMM d"))} - ${endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = CurrencyFormatter.formatCurrency(amount, currency),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress bar placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AccountMultiSelectionSheet(
    allAccounts: List<AccountBalanceEntity>,
    selectedAccountIds: List<String>,
    onSelectionChange: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.xl)
        ) {
            Text(
                text = "Link Accounts",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(Spacing.md)
            )

            LazyColumn(
                modifier = Modifier.weight(1f, fill = false).clip(RoundedCornerShape(Spacing.md)),
                contentPadding = PaddingValues(horizontal = Spacing.md)
            ) {
                item {
                    Surface(
                        onClick = { onSelectionChange(emptyList()) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(Spacing.md),
                        color = if (selectedAccountIds.isEmpty()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.5f
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                "All Accounts",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold
                            )
                            if (selectedAccountIds.isEmpty()) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                items(allAccounts) { account ->
                    val accountId = "${account.bankName}:${account.accountLast4}"
                    val isSelected = selectedAccountIds.contains(accountId)

                    Surface(
                        onClick = {
                            val newList = if (isSelected) {
                                selectedAccountIds - accountId
                            } else {
                                selectedAccountIds + accountId
                            }
                            onSelectionChange(newList)
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(Spacing.md),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = 0.3f
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BrandIcon(
                                merchantName = account.bankName,
                                size = 40.dp,
                                showBackground = true,
                                accountIconResId = account.iconResId,
                                accountColorHex = account.color
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    account.bankName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Ending in ${account.accountLast4}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    val newList = if (checked) {
                                        selectedAccountIds + accountId
                                    } else {
                                        selectedAccountIds - accountId
                                    }
                                    onSelectionChange(newList)
                                }
                            )
                        }
                    }
                }
                item{Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = onDismiss,
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.md)
                    .height(56.dp)
            ) {
                Text("Done")
            }
        }
    }
}
