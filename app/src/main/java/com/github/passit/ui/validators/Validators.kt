package com.github.passit.ui.validators

import android.util.Patterns

fun String.isValidEmail(): Boolean = this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

// min 12 characters, digits, lowercase, uppercase, symbols =+-^$*.[]{}()?"!@#%&/\,><':;|_~`
fun String.isValidPassword(): Boolean = this.matches(Regex("""^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[=+\-^$*.\[\]{}()?"!@#%&/\\,><':;|_~`]).{12,}$"""))