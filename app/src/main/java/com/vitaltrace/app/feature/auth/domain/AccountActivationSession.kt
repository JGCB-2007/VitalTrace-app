package com.vitaltrace.app.feature.auth.domain

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountActivationSession @Inject constructor() {
    var email: String = ""
        private set
    var token: String = ""
        private set

    fun begin(email: String) { this.email = email.trim().lowercase(); token = "" }
    fun verified(token: String) { this.token = token }
    fun clear() { email = ""; token = "" }
}
