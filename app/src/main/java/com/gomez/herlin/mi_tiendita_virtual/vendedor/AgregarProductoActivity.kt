package com.gomez.herlin.mi_tiendita_virtual.vendedor

import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.gomez.herlin.mi_tiendita_virtual.Adaptadores.AdaptadorIMagenSeleccionada
import com.gomez.herlin.mi_tiendita_virtual.Constantes
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloCategoria
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloImagenSeleccionada
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityAgregarProductoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarProductoBinding
    private var imagenUri : Uri?=null

    private lateinit var imageSelecArrayList: ArrayList<ModeloImagenSeleccionada>
    private lateinit var adaptadorIMagenSel: AdaptadorIMagenSeleccionada

    private lateinit var categoriaArrayList : ArrayList<ModeloCategoria>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarCategorias()

        imageSelecArrayList = ArrayList()

        binding.imgAgregarProducto.setOnClickListener {
            seleccionarImg()
        }

        binding.Categoria.setOnClickListener {
            selecCategorias()
        }

        cargarImagenes()
    }

    private fun cargarCategorias() {
        categoriaArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriaArrayList.clear()
                for (ds in snapshot.children) {
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriaArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private var idCet = ""
    private var tituloCat = ""

    private fun selecCategorias () {
        val categoriasArry = arrayOfNulls<String>(categoriaArrayList.size)
        for (i in categoriaArrayList.indices) {
            categoriasArry[i] = categoriaArrayList[i].categoria
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.seleccionar_categoria))
            .setItems(categoriasArry) { dialog, which ->
                idCet = categoriaArrayList[which].id
                tituloCat = categoriaArrayList[which].categoria
                binding.Categoria.text = tituloCat
            }.show()
    }

    private fun cargarImagenes() {
        adaptadorIMagenSel = AdaptadorIMagenSeleccionada(this, imageSelecArrayList)
        binding.RVImagenesProducto.adapter = adaptadorIMagenSel
    }

    private fun seleccionarImg () {
        ImagePicker.with(this).crop().compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                resultadoImg.launch(intent)
            }
    }

    private val resultadoImg =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if(resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data
                val tiempo = "${Constantes().obtenerTiempoD()}"

                val modeloImgSel = ModeloImagenSeleccionada(tiempo, imagenUri, null, false)
                imageSelecArrayList.add(modeloImgSel)
                cargarImagenes()
                //binding.imgAgregarProducto.setImageURI(imagenUri)
            }else {
                Toast.makeText(this, "Accion cancelada", Toast.LENGTH_SHORT).show()
            }

        }
}