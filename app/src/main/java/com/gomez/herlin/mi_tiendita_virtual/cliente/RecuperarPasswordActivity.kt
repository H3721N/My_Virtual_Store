package com.gomez.herlin.mi_tiendita_virtual.cliente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityRecuperarPasswordBinding

class RecuperarPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecuperarPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}