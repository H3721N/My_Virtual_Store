package com.gomez.herlin.mi_tiendita_virtual.vendedor

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.gomez.herlin.mi_tiendita_virtual.Constantes
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityRegistroVendedorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroVendedorActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegistroVendedorBinding
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var progresDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progresDialog = ProgressDialog(this)

        progresDialog.setTitle("Espere un momento")
        progresDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarV.setOnClickListener {
            validarinformacion()
        }
    }

    private var nombres = ""
    private var email = ""
    private var password = ""
    private var confirmPassword = ""

    private fun validarinformacion() {
        nombres = binding.etNombreV.text.toString().trim()
        email = binding.etEmailV.text.toString().trim()
        password = binding.etPasswordV.text.toString().trim()
        confirmPassword = binding.etConfirmarPasswordV.text.toString().trim()

        if(nombres.isEmpty()) {
            binding.etNombreV.error = "Ingrese sus nombres"
            binding.etNombreV.requestFocus()
        } else if (email.isEmpty()) {
            binding.etEmailV.error = "Ingrese su correo"
            binding.etEmailV.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailV.error = "Ingrese un correo valido"
            binding.etEmailV.requestFocus()
        } else if (password.isEmpty()) {
            binding.etPasswordV.error = "Ingrese password"
            binding.etPasswordV.requestFocus()
        } else if (password.length < 6) {
            binding.etPasswordV.error = "La password necesita almenos 6 caracteres"
            binding.etPasswordV.requestFocus()
        } else if (confirmPassword.isEmpty()) {
            binding.etConfirmarPasswordV.error = "Confirme su contraseña"
            binding.etConfirmarPasswordV.requestFocus()
        } else if (confirmPassword != password) {
            binding.etConfirmarPasswordV.error = "Las contraseñas no coinciden"
            binding.etConfirmarPasswordV.requestFocus()
        } else {
            registrarVendedor()
        }
    }

    private fun registrarVendedor() {
        progresDialog.setMessage("Creando una cuenta")
        progresDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                insertarInfoBD()
            }
            .addOnFailureListener{
                e ->
                Toast.makeText(this, "Error al crear la cuenta: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun insertarInfoBD() {
        progresDialog.setMessage("Guardando la informacion...")

        val uidBD = firebaseAuth.uid
        val nombresBD = nombres
        val emailBD = email
        val tiempBD = Constantes().obtenerTiempoD()

        val datosBD = Constantes().obtenerTiempoD()

        val datosVendedor = HashMap<String, Any>()
        datosVendedor["uid"] = "$uidBD"
        datosVendedor["nombres"] = "$nombresBD"
        datosVendedor["email"] = "$emailBD"
        datosVendedor["tipoUsuario"] = "vendedor"
        datosVendedor["tiempo_registro"] = tiempBD

        val references = FirebaseDatabase.getInstance().getReference("Usuarios")
        references.child(uidBD!!)
            .setValue(datosVendedor)
            .addOnSuccessListener {
                progresDialog.dismiss()
                startActivity(Intent(this, MainActivityVendedor::class.java))
                finish()
            }
            .addOnFailureListener() {
                e ->
                progresDialog.dismiss()
                Toast.makeText(this, "Error al guardar la informacion: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}