package com.gomez.herlin.mi_tiendita_virtual.cliente

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityRecuperarPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class RecuperarPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarPasswordBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecuperarPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(R.string.cargando)
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEnviarInstrucciones.setOnClickListener {
            validarEmail()
        }
    }

    private var email = ""

    private fun validarEmail() {
        email = binding.etEmail.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, R.string.null_email, Toast.LENGTH_SHORT).show()
            binding.etEmail.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = getString(R.string.invalid_email)
            binding.etEmail.requestFocus()
        } else {
            enviarInstrucciones()
        }
    }

    private fun enviarInstrucciones() {
        progressDialog.setMessage("Enviando instrucciones al correo: ${email}")
        progressDialog.dismiss()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, R.string.instructions_sent, Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this@RecuperarPasswordActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}