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
import com.github.dhaval2404.imagepicker.ImagePicker
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.FragmentCategoriasVBinding
import com.google.firebase.database.FirebaseDatabase

class FragmentCategoriasV : Fragment() {

    private lateinit var binding : FragmentCategoriasVBinding
    private lateinit var mContext : Context
    private lateinit var progressDialog : ProgressDialog
    private var imageUri : Uri? = null

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

        return binding.root
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
                progressDialog.dismiss()
                Toast.makeText(mContext, getString(R.string.add_Category), Toast.LENGTH_SHORT).show()
                binding.inCategoria.setText("")
            }
            .addOnFailureListener{ e ->
                progressDialog.dismiss()
                Toast.makeText(mContext, "$ ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

}