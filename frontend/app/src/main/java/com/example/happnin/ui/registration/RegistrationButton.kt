package com.example.happnin.ui.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.happnin.ui.theme.HappnInPurple
import com.example.happnin.ui.theme.HappnInTheme

// Standalone register/registered toggle button.
// Teammate hook: pass isRegistered from RegistrationViewModel.isRegistered(eventId),
// and wire onRegisterClick to RegistrationViewModel.register(eventId).
@Composable
fun RegistrationButton(
    isRegistered: Boolean,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isRegistered) {
        // Filled "Registered" state — green, intentionally non-interactive
        Box(
            modifier = modifier
                .height(30.dp)
                .background(Color(0xFF4CAF50), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Registered",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        }
    } else {
        // Outlined "Register" state — purple border, transparent fill
        Box(
            modifier = modifier
                .height(30.dp)
                .border(1.5.dp, HappnInPurple, RoundedCornerShape(8.dp))
                .clickable(onClick = onRegisterClick)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Register",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = HappnInPurple,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationButtonPreview() {
    HappnInTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RegistrationButton(isRegistered = false, onRegisterClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationButtonRegisteredPreview() {
    HappnInTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RegistrationButton(isRegistered = true, onRegisterClick = {})
        }
    }
}
