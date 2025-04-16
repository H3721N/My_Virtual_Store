package com.gomez.herlin.mi_tiendita_virtual.vendedor.Nav_Fragment_Vendedor

import android.app.Activity
import android.app.ProgressDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.gomez.herlin.mi_tiendita_virtual.Adaptadores.AdaptadorCategoriaV
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloCategoria
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.FragmentCategoriasVBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class FragmentCategoriasV : Fragment() {

    private lateinit var binding : FragmentCategoriasVBinding
    private lateinit var mContext : Context
    private lateinit var progressDialog : ProgressDialog
    private var imageUri : Uri? = null
    private lateinit var categoriasArrayList : ArrayList<ModeloCategoria>
    private lateinit var adaptadprCategoriaV : AdaptadorCategoriaV

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriasVBinding.inflate(inflater, container, false)

        progressDialog = ProgressDialog(context)
        progressDialog.setTitle(getString(R.string.cargando))
        progressDialog.setCanceledOnTouchOutside(false)

        binding.imgCategorias.setOnClickListener {
            seleccionarImg()
        }

        binding.btnAgregarCat.setOnClickListener {
            validarInfo()
        }

        listarCategorias()

        return binding.root
    }

    private fun listarCategorias() {
        categoriasArrayList = ArrayList()
        binding.tvCategorias.layoutManager = LinearLayoutManager(context)
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriasArrayList.clear()
                for ( ds in snapshot.children) {
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriasArrayList.add(modelo!!)
                }
                adaptadprCategoriaV = AdaptadorCategoriaV(mContext, categoriasArrayList)
                binding.tvCategorias.adapter = adaptadprCategoriaV
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun seleccionarImg() {
        ImagePicker.with(requireActivity())
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent {
                intent ->
                resultadoImg.launch(intent)
            }
    }

    private val resultadoImg =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            resultado ->
            if (resultado.resultCode == Activity.RESULT_OK) {
                val data = resultado.data
                imageUri = data!!.data
                binding.imgCategorias.setImageURI(imageUri)
            } else {
                Toast.makeText(mContext, getString(R.string.cancel), Toast.LENGTH_SHORT).show()
            }
        }

    private var categoria = ""
    private fun validarInfo() {
        categoria = binding.inCategoria.text.toString().trim()
        if (categoria.isEmpty()) {
            Toast.makeText(context, getString(R.string.inCategoria), Toast.LENGTH_SHORT).show()
        } else if (imageUri == null) {
            Toast.makeText(context, getString(R.string.imgSelect), Toast.LENGTH_SHORT).show()
        } else {
            agregarCatBD()
        }
    }

    private fun agregarCatBD() {
        progressDialog.setMessage("${getString(R.string.agregando)} categoria...")
        progressDialog.show()

        val ref = FirebaseDatabase.getInstance().getReference("Categorias")
        val keyId = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "${keyId}"
        hashMap["categoria"] = "$categoria"

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                //progressDialog.dismiss()
                //Toast.makeText(mContext, getString(R.string.add_Category), Toast.LENGTH_SHORT).show()
                //binding.inCategoria.setText("")
                subirImgStorage(keyId)
            }
            .addOnFailureListener{ e ->
                progressDialog.dismiss()
                Toast.makeText(mContext, "$ ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun subirImgStorage(keyId: String){
        progressDialog.setMessage("${getString(R.string.subiendoIMG)} imagen...")
        progressDialog.show()

        val nombreImagen = keyId
        val nombreCarpeta = "Categorias/$nombreImagen"
        val storageReference = FirebaseStorage.getInstance().getReference(nombreCarpeta)
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                progressDialog.dismiss()
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlImgCargada = uriTask.result

                if (uriTask.isSuccessful) {
                    val hashMap = HashMap<String, Any>()
                    hashMap["img"] = "$urlImgCargada"

                    val ref = FirebaseDatabase.getInstance().getReference("Categorias")
                    ref.child(nombreImagen).updateChildren(hashMap)
                    Toast.makeText(mContext, getString(R.string.add_Category), Toast.LENGTH_SHORT).show()
                    binding.inCategoria.setText("")
                    imageUri = null
                    binding.imgCategorias.setImageURI(imageUri)
                    binding.imgCategorias.setImageResource(R.drawable.categorias)
                }

            }
            .addOnFailureListener{ e ->
                progressDialog.dismiss()
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}