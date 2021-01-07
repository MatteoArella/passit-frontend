package it.uniroma1.macc.project.ui.validators

import android.util.Patterns

fun String.isValidEmail(): Boolean = this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPassword(): Boolean = this.isNotEmpty()