package com.ithoughts.mynaa.tsd.gfg

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate

sealed class Validator(val message: String)
open class Email : Validator("Enter valid email address")
open class NotExpired : Validator("Not expired")
open class Required : Validator("The field is Required")
open class MinLength(val length: Int) : Validator("Minimum $length chars")

class FormFieldState(
    val name: String,
    label: String,
    placeHolder: String? = null,
    val keyboardType: KeyboardType = KeyboardType.Text,
    private val validators: List<Validator> = emptyList()
) {
    var text by mutableStateOf("")
        private set
    var label by mutableStateOf(label)
        private set
    var supportingText by mutableStateOf("")
        private set
    val placeHolderText by mutableStateOf(placeHolder)
    var isError by mutableStateOf(false)
        private set

    fun onValueChange(value: String) {
        text = value
        isError = false
        supportingText = ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validate(): Boolean {
        return validators.map {
            val isValid = when (it) {
                is Email -> Patterns.EMAIL_ADDRESS.matcher(text).matches()
                is Required -> text.isNotEmpty()
                is NotExpired -> {
                    val month = text.substring(0, 2).trimStart('0').toInt()
                    val year = text.substring(2, 4).trimStart('0').toInt()
                    val now = LocalDate.now()
                    (year < now.year) || (year == now.year && month < now.monthValue)
                }

                is MinLength -> text.length > it.length
            }
            if (!isValid) {
                supportingText = it.message
                isError = true
            }
            isValid
        }.all { it }
    }
}

@Composable
fun FormField(formFieldState: FormFieldState, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = formFieldState.text,
        onValueChange = onValueChange,
        label = { Text(formFieldState.label) },
        supportingText = { Text(formFieldState.supportingText) },
        isError = formFieldState.isError,
        keyboardOptions = KeyboardOptions(keyboardType = formFieldState.keyboardType),
        placeholder = { formFieldState.placeHolderText?.let { Text(it) } }
    )
}

class Form(val fields: List<FormFieldState>) {

    var hasEmptyFields by mutableStateOf(true)
        private set

    fun onValueChange(fieldState: FormFieldState, value: String) {
        fieldState.onValueChange(value)
        hasEmptyFields = value.isBlank()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validate() = fields.all { it.validate() }

    fun formData() = fields.associate { it.name to it.text }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun FormComposable(submitData: (Map<String, String>) -> Unit = {}) {
    val form = remember {
        Form(
            listOf(
                FormFieldState("firstName", "First name", validators = listOf(Required())),
                FormFieldState("lastName", "Last name", validators = listOf(Required())),
                FormFieldState(
                    "panNumber",
                    "Pan number",
                    validators = listOf(MinLength(8), Required()),
                    keyboardType = KeyboardType.Number
                ),
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        form.fields.forEach {
            FormField(it) { value ->
                form.onValueChange(it, value)
            }
        }

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = !form.hasEmptyFields,
            onClick = { if (form.validate()) submitData(form.formData()) }
        ) {
            Text(text = "Submit")
        }
    }
}