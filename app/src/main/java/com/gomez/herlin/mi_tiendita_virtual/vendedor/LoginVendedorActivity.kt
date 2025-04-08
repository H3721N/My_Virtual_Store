package com.gomez.herlin.mi_tiendita_virtual.vendedor

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityLoginVendedorBinding
import com.google.firebase.auth.FirebaseAuth

class LoginVendedorActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginVendedorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var pregressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        pregressDialog = ProgressDialog(this)
        pregressDialog.setTitle("Espere un momento por favor")
        pregressDialog.setCanceledOnTouchOutside(false)

        binding.btnLoginV.setOnClickListener {
            validarInfo()
        }

        binding.tvRegistroV.setOnClickListener {
            startActivity(Intent(applicationContext, RegistroVendedorActivity::class.java))
        }
    }

    private var email = ""
    private var password = ""

    private fun validarInfo() {
        email = binding.etEmailV.text.toString().trim()
        password = binding.etPasswordV.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmailV.error = "Ingrese email"
            binding.etEmailV.requestFocus()
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailV.error = "Email no valido"
            binding.etEmailV.requestFocus()
        } else if (password.isEmpty()) {
            binding.etPasswordV.error = "Ingrese contraseña"
            binding.etPasswordV.requestFocus()
        } else {
            loginVendedor()
        }
    }

    private fun loginVendedor() {
        pregressDialog.setMessage("Iniciando sesion...")
        pregressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener{
                pregressDialog.dismiss()
                startActivity(Intent(this, MainActivityVendedor::class.java))
                finishAffinity()
                Toast.makeText(this, "Bienvenido(a)",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                e -> Toast.makeText(this, "No se ha podido iniciar sesion, debido a: ${e.message}",
                Toast.LENGTH_SHORT).show()
            }
    }
}