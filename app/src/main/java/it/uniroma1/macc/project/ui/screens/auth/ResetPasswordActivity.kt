package it.uniroma1.macc.project.ui.screens.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.uniroma1.macc.project.databinding.ActivityResetPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}