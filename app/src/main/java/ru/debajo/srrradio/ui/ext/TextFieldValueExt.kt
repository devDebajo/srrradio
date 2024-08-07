package ru.debajo.srrradio.ui.ext

import androidx.compose.ui.text.input.TextFieldValue

fun TextFieldValue.isEmpty(): Boolean = text.isEmpty()

fun TextFieldValue.isNotEmpty(): Boolean = !isEmpty()

val EmptyTextFieldValue: TextFieldValue = TextFieldValue("")

val TextFieldValue.Companion.Empty: TextFieldValue
    inline get() = EmptyTextFieldValue
