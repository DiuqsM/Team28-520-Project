package com.example.happnin.ui.registration

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.happnin.data.Event
import com.example.happnin.ui.theme.HappnInPurple

@Composable
fun RegistrationConfirmDialog(
    event: Event,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val priceNote = if (event.price > 0) " (${"$"}${"%.2f".format(event.price)})" else " (Free)"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Confirm Registration", fontWeight = FontWeight.SemiBold)
        },
        text = {
            Text(text = "Register for \"${event.title}\"?$priceNote")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = HappnInPurple),
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}
