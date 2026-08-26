package com.vitaltrace.app.feature.auth.domain

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountActivationSession @Inject constructor() {
    var email: String = ""
        private set
    var code: String = ""
        private set

    fun begin(email: String) {
        this.email = email.trim().lowercase()
        code = ""
    }

    fun codeEntered(code: String) {
        this.code = code
    }

    fun clear() {
        email = ""
        code = ""
    }
}