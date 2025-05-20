package com.gomez.herlin.mi_tiendita_virtual.cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gomez.herlin.mi_tiendita_virtual.Constantes
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityLoginClienteBinding
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityRegistroClientesBinding
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityRegistroVendedorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroClientesBinding
    private lateinit var  firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere un momento")

        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrarC.setOnClickListener() {
            validarInformacion()
        }
    }

    private var nombres = ""
    private var email = ""
    private var password = ""
    private var cpassword = ""

    private fun validarInformacion() {
        nombres = binding.etNombreC.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        cpassword = binding.etConfirmarPassword.text.toString().trim()

        if(nombres.isEmpty()) {
            binding.etNombreC.error = "Ingrese su nombre"
            binding.etNombreC.requestFocus()
        } else if (email.isEmpty()) {
            binding.etEmail.error = "Ingrese se email"
            binding.etEmail.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Email no valido"
            binding.etEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.etPassword.error = "Ingrese se password"
            binding.etPassword.requestFocus()
        } else if (password.length < 6) {
            binding.etPassword.error = "El password precisa tener al menos 6 caracteres"
            binding.etPassword.requestFocus()
        } else if (cpassword.isEmpty()) {
            binding.etConfirmarPassword.error = "Confirme su password"
            binding.etConfirmarPassword.requestFocus()
        } else if (password != cpassword) {
            binding.etPassword.error = "La contraseña no coincide con su confirmación"
            binding.etPassword.requestFocus()
        } else {
            registrarCliente()
        }
    }

    private fun registrarCliente() {
        progressDialog.setMessage("Creando cuenta...")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                insertarInfoDB()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Fallo el registro debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun insertarInfoDB() {
        progressDialog.setMessage("Guardando información...")

        val uid = firebaseAuth.uid
        val nombresC = nombres
        val emailC = email
        val tiempoRegistro = Constantes().obtenerTiempoD()

        val datosCliente = HashMap<String, Any>()

        datosCliente["uid"] = "$uid"
        datosCliente["nombres"] = "$nombresC"
        datosCliente["email"] = "$emailC"
        datosCliente["telefono"] = ""
        datosCliente["dni"] = ""
        datosCliente["proveedor"] = "email"
        datosCliente["tRegistro"] = "$tiempoRegistro"
        datosCliente["tipoUsuario"] = "cliente"

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid!!)
            .setValue(datosCliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this@RegistroClientesActivity, MainActivityCliente::class.java))
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Fallo el registro debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}