package com.gomez.herlin.mi_tiendita_virtual.cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityActualizarPaswordBinding
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ActualizarPaswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActualizarPaswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarPaswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.app_loading))
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnActualizarPassword.setOnClickListener {
            validarInformacion()
        }

    }

    private var pass_actual = ""
    private var pass_nueva = ""
    private var pass_nuevar = ""

    private fun validarInformacion() {
        pass_actual = binding.etPasswordActual.text.toString().trim()
        pass_nueva = binding.etPasswordNueva.text.toString().trim()
        pass_nuevar = binding.etPasswordNuevaR.text.toString().trim()

        if (pass_actual.isEmpty()) {
            binding.etPasswordActual.error = getString(R.string.null_password)
            binding.etPasswordActual.requestFocus()
        } else if (pass_nueva.isEmpty()) {
            binding.etPasswordNueva.error = getString(R.string.null_new_password)
            binding.etPasswordNueva.requestFocus()
        } else if (pass_nuevar.isEmpty()) {
            binding.etPasswordNuevaR.error = getString(R.string.null_new_passwordR)
            binding.etPasswordNuevaR.requestFocus()
        } else if (pass_nueva != pass_nuevar) {
            binding.etPasswordNuevaR.error = getString(R.string.passwords_not_match)
            binding.etPasswordNuevaR.requestFocus()
        } else {
            autenticarUsuario()
        }

    }

    private fun autenticarUsuario() {
        progressDialog.setMessage(getString(R.string.app_loading))
        progressDialog.show()

        val autoCredential = EmailAuthProvider.getCredential(firebaseUser.email.toString(), pass_actual)

        firebaseUser.reauthenticate(autoCredential)
            .addOnSuccessListener {
                progressDialog.dismiss()
                actualizarPassword()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this@ActualizarPaswordActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarPassword() {

        progressDialog.setMessage(getString(R.string.updating_password))
        progressDialog.show()

        firebaseUser.updatePassword(pass_nueva)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this@ActualizarPaswordActivity, R.string.password_updated, Toast.LENGTH_SHORT).show()
                firebaseAuth.signOut()
                startActivity(Intent(this@ActualizarPaswordActivity, LoginClienteActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this@ActualizarPaswordActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            }

            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this@ActualizarPaswordActivity, "${e.message}", Toast.LENGTH_SHORT).show()

            }

    }
}