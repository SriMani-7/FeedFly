package srimani7.apps.feedfly.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import srimani7.apps.feedfly.viewmodel.HomeViewModal

const val URL_REGEX =
    "\\b((?:https?|ftp)://[\\w-]+(\\.[\\w-]+)+([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?)"

@Composable
fun AddUrlDialog(homeViewModal: HomeViewModal, onDismiss: () -> Unit) {
    val groupNames by homeViewModal.groupNameFlow.collectAsState(emptyList())
    val selectedGroup = remember { mutableStateOf<String?>(null) }

    var url by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    fun onSubmit() {
        if (url.matches(URL_REGEX.toRegex())) {
            homeViewModal.insertFeed(url, selectedGroup.value)
            onDismiss()
        } else {
            isError = true
            errorText = "Invalid url address"
        }
    }

    Card(
        shape = AlertDialogDefaults.shape,
//        tonalElevation = AlertDialogDefaults.TonalElevation,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Add feed",
                color = AlertDialogDefaults.textContentColor,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 14.dp)
            )
            TextField(
                value = url,
                onValueChange = { url = it },
                isError = isError,
                supportingText = { Text(errorText) },
                label = { Text(text = "Feed URL") },
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions { onSubmit() },
                maxLines = 10
            )
            GroupsDropdownMenu(groupNames, selectedGroup)
            Spacer(modifier = Modifier.weight(1f))
            Button(::onSubmit, modifier = Modifier.align(Alignment.End)) {
                Text(text = "Next")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsDropdownMenu(options: List<String?>, selectedOption: MutableState<String?>) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = selectedOption.value ?: "Others",
            onValueChange = {
                selectedOption.value = it
                expanded = true
            },
            label = { Text("Group name") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        val filteringOptions = options.filter {
            if (selectedOption.value == it || selectedOption.value.isNullOrBlank()) true
            else if (selectedOption.value != null) it?.contains(
                selectedOption.value!!,
                ignoreCase = true
            ) ?: false
            else true
        }
        if (filteringOptions.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                filteringOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption ?: "Others") },
                        onClick = {
                            selectedOption.value = selectionOption
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}