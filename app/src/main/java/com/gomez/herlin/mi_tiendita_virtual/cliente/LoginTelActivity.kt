package com.gomez.herlin.mi_tiendita_virtual.cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gomez.herlin.mi_tiendita_virtual.Constantes
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityLoginTelBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.TimeUnit

class LoginTelActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginTelBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    private var forceResendingToken : ForceResendingToken? = null
    private lateinit var mCallback: OnVerificationStateChangedCallbacks
    private var mVerification : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginTelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.app_loading))
        progressDialog.setCanceledOnTouchOutside(false)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.rlTelefono.visibility = View.VISIBLE
        binding.rlCodigoVer.visibility = View.GONE

        phoneLoginCallback()

        binding.btnEnviarCodigo.setOnClickListener {
            validarData()
        }

        binding.btnVerificarCod.setOnClickListener {
            val otp = binding.etCodVer.text.toString().trim()
            if (otp.isEmpty()) {
                binding.etCodVer.error = getString(R.string.null_code)
                binding.etCodVer.requestFocus()
            } else if (otp.length < 6) {
                binding.etCodVer.error = getString(R.string.min_length_code)
                binding.etCodVer.requestFocus()
            } else {
                verificarCodeTel(otp)
            }
        }

        binding.tvReenviarCod.setOnClickListener {
            if (forceResendingToken != null ){
                reenviarCodVer()
            } else {
                Toast.makeText(this, getString(R.string.error_reenviar), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun verificarCodeTel(otp: String) {
        progressDialog.setMessage(getString(R.string.verificando_codigo))
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(mVerification!!, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog.setMessage(getString(R.string.cargando))
        progressDialog.show()

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                if (authResult.additionalUserInfo!!.isNewUser) {
                    guardarInfo()
                } else {
                    startActivity(Intent(this, MainActivityCliente::class.java))
                    finishAffinity()
                }
            }
    }

    private fun guardarInfo() {
        progressDialog.setMessage(getString(R.string.guardando))
        progressDialog.show()

        val uid = firebaseAuth.uid
        val tiempo = Constantes().obtenerTiempoD()

        val datosCliente = HashMap<String, Any>()

        datosCliente["uid"] = "${uid}"
        datosCliente["nombres"] = ""
        datosCliente["telefonos"] = ""
        datosCliente["tRegistro"] = tiempo
        datosCliente["imagen"] = ""
        datosCliente["tipoUsuario"] = "cliente"

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")

        reference.child(uid!!)
            .setValue(datosCliente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivityCliente::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this@LoginTelActivity,
                    "${getString(R.string.error)} ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun reenviarCodVer() {
        progressDialog.setMessage(getString(R.string.reenviar_codigo))
        progressDialog.show()

        val option = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(codTelnumTel)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallback)
            .setForceResendingToken(forceResendingToken!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    private var codigoTel = ""
    private var numeroTel = ""
    private var codTelnumTel = ""

    private fun validarData() {
        codigoTel = binding.telCodePicker.selectedCountryCodeWithPlus
        numeroTel = binding.etTelefonoC.text.toString().trim()
        codTelnumTel = codigoTel + numeroTel

        if (numeroTel.isEmpty()) {
            binding.etTelefonoC.error = getString(R.string.null_number_hone)
            binding.etTelefonoC.requestFocus()
        } else {
            verificarNumeroTel()
        }
    }

    private fun verificarNumeroTel() {
        progressDialog.setMessage("${getString(R.string.push_code)} a $codTelnumTel")
        progressDialog.show()

        val option = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(codTelnumTel)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    private fun phoneLoginCallback() {
        mCallback = object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(verification: String, token: ForceResendingToken) {
                mVerification = verification
                forceResendingToken = token

                progressDialog.dismiss()

                binding.rlTelefono.visibility = View.GONE
                binding.rlCodigoVer.visibility = View.VISIBLE

                Toast.makeText(this@LoginTelActivity, getString(R.string.push_code),
                    Toast.LENGTH_SHORT).show()
            }
            override fun onVerificationCompleted(phoneAuthCredencial: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredencial)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                Toast.makeText(this@LoginTelActivity,
                    "${getString(R.string.error)} ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}