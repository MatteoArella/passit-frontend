package com.github.passit.ui.screens.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.passit.databinding.ActivitySignupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}