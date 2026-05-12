package com.example.happnin.ui.auth

import com.example.happnin.data.SupabaseNetwork
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

interface AuthRepository {
    suspend fun login(email: String, password: String)
}

class SupabaseAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String) {
        SupabaseNetwork.client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }
}
