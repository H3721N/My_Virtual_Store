package com.gomez.herlin.mi_tiendita_virtual.cliente.Nav_Fragments_Cliente

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.gomez.herlin.mi_tiendita_virtual.Constantes
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.FragmentMiPerfilCBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentMiPerfilC : Fragment() {

    private lateinit var binding : FragmentMiPerfilCBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = FragmentMiPerfilCBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        leerInformacion()
    }

    private fun leerInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // obtener datos de usuario
                    val nombres = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    val dni = "${snapshot.child("dni").value}"
                    val imagen = "${snapshot.child("imagen").value}"
                    val telefono = "${snapshot.child("telefono").value}"
                    val fechaRegistro = "${snapshot.child("tRegistro").value}"
                    val proveedor = "${snapshot.child("proveedor").value}"

                    val fecha = Constantes().obtenerFecha(fechaRegistro.toLong())

                    binding.nombresCPerfil.setText(nombres)
                    binding.emailCPerfil.setText(email)
                    binding.dniCPerfil.setText(dni)
                    binding.telefonoCPerfil.setText(telefono)
                    binding.fechaRegistroCPerfil.setText("Se unio el: ${fecha}")

                    try {
                        Glide.with(mContext)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.imgCPerfil)
                    } catch (e: Exception) {
                    }

                    if (proveedor == "email") {
                        binding.proveedorCPerfil.setText(getString(R.string.pov_email))
                    } else if (proveedor == "google") {
                        binding.proveedorCPerfil.setText(getString(R.string.prov_google))
                    } else if (proveedor == "telefono") {
                        binding.proveedorCPerfil.setText(getString(R.string.prov_telefono))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

}