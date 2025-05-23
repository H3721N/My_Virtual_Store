package com.gomez.herlin.mi_tiendita_virtual.cliente.Nav_Fragments_Cliente

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.gomez.herlin.mi_tiendita_virtual.Constantes
import com.gomez.herlin.mi_tiendita_virtual.Mapas.SeleccionarUbicacionActivity
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.FragmentMiPerfilCBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class FragmentMiPerfilC : Fragment() {

    private lateinit var binding : FragmentMiPerfilCBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext: Context
    private var imagenUri: Uri? = null

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = FragmentMiPerfilCBinding.inflate(layoutInflater, container, false)

        binding.imgCPerfil.setOnClickListener() {
            seleccionarImagen()
        }

        binding.btnGuardarInfoC.setOnClickListener {
            actualizarInfo()
        }

        binding.ubicacion.setOnClickListener {
            val intent = Intent(mContext, SeleccionarUbicacionActivity::class.java)
            obtenerUbicacion_ARL.launch(intent)

        }

        return binding.root
    }

    private var nombres = ""
    private var email = ""
    private var dni = ""
    private var telefono = ""

    private fun actualizarInfo() {
        nombres = binding.nombresCPerfil.text.toString().trim()
        email = binding.emailCPerfil.text.toString().trim()
        dni = binding.dniCPerfil.text.toString().trim()
        telefono = binding.telefonoCPerfil.text.toString().trim()
        direccion = binding.ubicacion.text.toString().trim()

        val hashMap : HashMap<String, Any> = HashMap()
        hashMap["nombres"] = "${nombres}"
        hashMap["email"] = "${email}"
        hashMap["dni"] = "${dni}"
        hashMap["telefono"] = "${telefono}"
        hashMap["latitud"] = "${latitud}"
        hashMap["longitud"] = "${longitud}"

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(mContext, (R.string.update_info), Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e->
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
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
                    val direccion = "${snapshot.child("direccion").value}"

                    val fecha = Constantes().obtenerFecha(fechaRegistro.toLong())

                    binding.nombresCPerfil.setText(nombres)
                    binding.emailCPerfil.setText(email)
                    binding.dniCPerfil.setText(dni)
                    binding.telefonoCPerfil.setText(telefono)
                    binding.fechaRegistroCPerfil.setText("Se unio el: ${fecha}")
                    binding.ubicacion.setText(direccion)

                    try {
                        Glide.with(mContext)
                            .load(imagen)
                            .placeholder(R.drawable.img_perfil)
                            .into(binding.imgCPerfil)
                    } catch (e: Exception) {
                    }

                    if (proveedor == "email") {
                        binding.proveedorCPerfil.setText(getString(R.string.pov_email))
                        binding.emailCPerfil.isEnabled = false
                    } else if (proveedor == "google") {
                        binding.proveedorCPerfil.setText(getString(R.string.prov_google))
                        binding.emailCPerfil.isEnabled = false
                    } else if (proveedor == "telefono") {
                        binding.proveedorCPerfil.setText(getString(R.string.prov_telefono))
                        binding.telefonoCPerfil.isEnabled = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun seleccionarImagen() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent->
                resultadoImg.launch(intent)
            }
    }

    private val resultadoImg = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        resultado ->
        if( resultado.resultCode == Activity.RESULT_OK ) {
            val data = resultado.data
            imagenUri = data!!.data
            subirImagenStorage(imagenUri)
        } else {
            Toast.makeText(mContext, (R.string.cancel_action), Toast.LENGTH_SHORT).show()
        }
    }

    private fun subirImagenStorage(imagenUri: Uri?) {
        val rutaImagen = "imagenesPerfil/"+firebaseAuth.uid
        val ref = FirebaseStorage.getInstance().getReference(rutaImagen)
        ref.putFile(imagenUri!!)
            .addOnSuccessListener { taskSnapShot ->
                val uriTask = taskSnapShot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImagenCargada = uriTask.result.toString()
                if ( uriTask.isSuccessful ) {
                    actualizarImagenBD(urlImagenCargada)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarImagenBD(urlImagenCargada: String) {
        val hashMap : HashMap<String, Any> = HashMap()
        if ( imagenUri!=null ) {
            hashMap["imagen"] = urlImagenCargada
        }

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(mContext, (R.string.updateIMG), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private var latitud = 0.0
    private var longitud = 0.0
    private var direccion = ""

    private val obtenerUbicacion_ARL = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        if ( resultado.resultCode == Activity.RESULT_OK ) {
            val data = resultado.data
            if( data != null) {
                latitud = data.getDoubleExtra("latitud", 0.0)
                longitud = data.getDoubleExtra("longitud", 0.0)
                direccion = data.getStringExtra("direccion") ?: ""

                binding.ubicacion.setText(direccion)
            }
        }
    }

}