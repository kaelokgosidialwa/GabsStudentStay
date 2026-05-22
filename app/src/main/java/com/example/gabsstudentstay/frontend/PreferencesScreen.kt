package com.example.gabsstudentstay.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gabsstudentstay.data.TenantPreferences
import com.example.gabsstudentstay.data.UserRepository

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PreferencesScreen(
    navController: NavController,
    onSaved: () -> Unit = {}  // ← add this
) {
    var maxBudget by remember { mutableStateOf("") }
    var minCapacity by remember { mutableStateOf("") }
    var preferredArea by remember { mutableStateOf("") }
    val keywords = remember { mutableStateListOf<String>() }
    var keywordInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val preferredTags = remember { mutableStateListOf<String>() }

    // load existing preferences on launch
    LaunchedEffect(Unit) {
        UserRepository.getCurrentUser(
            onSuccess = { user ->
                user?.preferences?.let { prefs ->
                    maxBudget = if (prefs.maxBudget == 5000) "" else prefs.maxBudget.toString()
                    minCapacity = if (prefs.minCapacity == 1) "" else prefs.minCapacity.toString()
                    preferredArea = prefs.preferredArea
                    keywords.addAll(prefs.keywords)
                    preferredTags.addAll(prefs.preferredTags)  // ← load tags
                }
                isLoading = false
            },
            onError = { isLoading = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Preferences",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    SectionTitle("Preferred Tags")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select tags you prefer in a listing",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TagSelector(
                        selectedTags = preferredTags.toList(),
                        onTagToggled = { tag ->
                            if (preferredTags.contains(tag)) {
                                preferredTags.remove(tag)
                            } else {
                                preferredTags.add(tag)
                            }
                        }
                    )
                }
                // max budget
                item {
                    SectionTitle("Maximum Budget (P)")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = maxBudget,
                        onValueChange = { maxBudget = it },
                        label = { Text("Max budget per month") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        placeholder = { Text("e.g. 3000") }
                    )
                }

                // min capacity
                item {
                    SectionTitle("Minimum Room Capacity")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = minCapacity,
                        onValueChange = { minCapacity = it },
                        label = { Text("Minimum number of people") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        placeholder = { Text("e.g. 2") }
                    )
                }

                // preferred area
                item {
                    SectionTitle("Preferred Area")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = preferredArea,
                        onValueChange = { preferredArea = it },
                        label = { Text("Preferred city or area") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("e.g. Gaborone") }
                    )
                }

                // keywords
                item {
                    SectionTitle("Keywords")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add words to look for in listings e.g. WiFi, shower, study room",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // keyword input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = keywordInput,
                            onValueChange = { keywordInput = it },
                            label = { Text("Add keyword") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (keywordInput.isNotBlank() &&
                                    !keywords.contains(keywordInput.trim())
                                ) {
                                    keywords.add(keywordInput.trim())
                                    keywordInput = ""
                                }
                            }
                        ) {
                            Text("Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // keyword chips
                    if (keywords.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            keywords.forEach { keyword ->
                                InputChip(
                                    selected = false,
                                    onClick = { },
                                    label = { Text(keyword) },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { keywords.remove(keyword) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove $keyword"
                                            )
                                        }
                                    },
                                    shape = RoundedCornerShape(20.dp)
                                )
                            }
                        }
                    }
                }

                // error / success messages
                item {
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }
                    if (successMessage.isNotEmpty()) {
                        Text(
                            text = successMessage,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp
                        )
                    }
                }

                // save button
                item {
                    Button(
                        onClick = {
                            isSaving = true
                            errorMessage = ""
                            successMessage = ""

                            val preferences = TenantPreferences(
                                maxBudget = maxBudget.toIntOrNull() ?: 5000,
                                minCapacity = minCapacity.toIntOrNull() ?: 1,
                                preferredArea = preferredArea.trim(),
                                keywords = keywords.toList(),
                                preferredTags = preferredTags.toList()  // ← add this
                            )

                            UserRepository.savePreferences(
                                preferences = preferences,
                                onSuccess = {
                                    isSaving = false
                                    onSaved()  // ← closes screen and triggers refresh
                                },
                                onError = {
                                    isSaving = false
                                    errorMessage = it
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(if (isSaving) "Saving..." else "Save Preferences")
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}