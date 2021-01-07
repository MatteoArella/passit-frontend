package it.uniroma1.macc.project.ui.screens.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.uniroma1.macc.project.databinding.ActivitySignupBinding
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