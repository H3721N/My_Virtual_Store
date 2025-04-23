package com.gomez.herlin.mi_tiendita_virtual.vendedor

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
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
import com.google.firebase.storage.FirebaseStorage

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarProductoBinding
    private var imagenUri : Uri?=null

    private lateinit var imageSelecArrayList: ArrayList<ModeloImagenSeleccionada>
    private lateinit var adaptadorIMagenSel: AdaptadorIMagenSeleccionada

    private lateinit var categoriaArrayList : ArrayList<ModeloCategoria>

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarCategorias()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.cargando))
        progressDialog.setCanceledOnTouchOutside(false)

        binding.etPrecioConDescuento.visibility = View.GONE
        binding.etNotaDescuento.visibility = View.GONE

        binding.descuentoSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.etPrecioConDescuento.visibility = View.VISIBLE
                binding.etNotaDescuento.visibility = View.VISIBLE
            } else {
                binding.etPrecioConDescuento.visibility = View.GONE
                binding.etNotaDescuento.visibility = View.GONE
            }
        }

        imageSelecArrayList = ArrayList()

        binding.imgAgregarProducto.setOnClickListener {
            seleccionarImg()
        }

        binding.Categoria.setOnClickListener {
            selecCategorias()
        }

        binding.btnAgregarProducto.setOnClickListener {
            validarInfo()
        }

        cargarImagenes()
    }

    private var nombreP = ""
    private var descripcionP = ""
    private var categoriaP = ""
    private var precioP = ""
    private var descuentoHab = false
    private var precioDescP = ""
    private var notaDescP = ""

    private fun validarInfo() {

        nombreP = binding.etNombresP.text.toString().trim()
        descripcionP = binding.etDescripcionP.text.toString().trim()
        categoriaP = binding.Categoria.text.toString().trim()
        precioP = binding.etPrecioP.text.toString().trim()
        descuentoHab = binding.descuentoSwitch.isChecked

        if (nombreP.isEmpty()) {
            binding.etNombresP.error = getString(R.string.name_null)
            binding.etNombresP.requestFocus()
        } else if (descripcionP.isEmpty()) {
            binding.btnAgregarProducto.error = getString(R.string.description_null)
            binding.btnAgregarProducto.requestFocus()
        } else if (categoriaP.isEmpty()) {
            binding.Categoria.error = getString(R.string.categoria_null)
            binding.Categoria.requestFocus()
        } else if (precioP.isEmpty()) {
            binding.etPrecioP.error = getString(R.string.precio_null)
            binding.etPrecioP.requestFocus()
        } else if (imagenUri == null) {
            Toast.makeText(this, getString(R.string.img_null), Toast.LENGTH_SHORT).show()
        } else {
           if (descuentoHab) {
               precioDescP = binding.etPrecioConDescuento.text.toString().trim()
               notaDescP = binding.etNotaDescuento.text.toString().trim()
               if (precioDescP.isEmpty()) {
                   binding.etPrecioConDescuento.error = getString(R.string.descuento_null)
                   binding.etPrecioConDescuento.requestFocus()
               } else if (notaDescP.isEmpty()) {
                   binding.etNotaDescuento.text.toString().trim()
                   binding.etNotaDescuento.requestFocus()
               } else {
                   agregarProducto()
               }
           } else {
               notaDescP = ""
               agregarProducto()
           }
        }

    }

    private fun agregarProducto() {
        progressDialog.setMessage(getString(R.string.agregando))
        progressDialog.show()

        var ref = FirebaseDatabase.getInstance().getReference("Productos")
        val keyId = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "${keyId}"
        hashMap["nombre"] = "${nombreP}"
        hashMap["descripcion"] = "${descripcionP}"
        hashMap["categoria"] = "${tituloCat}"
        hashMap["precio"] = "${precioP}"
        hashMap["precioDesc"] = "${precioDescP}"
        hashMap["notaDesc"] = "${notaDescP}"

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                subirImgStorage(keyId)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun subirImgStorage(keyId: String) {
        for ( i in imageSelecArrayList.indices ) {
            val modeloImagenSel = imageSelecArrayList[i]
            val nombreImagen = modeloImagenSel.id
            val rutaImagen = "Productos/$nombreImagen"

            val storageRef = FirebaseStorage.getInstance().getReference(rutaImagen)
            storageRef.putFile(modeloImagenSel.imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val urlImgCargada = uriTask.result

                    if (uriTask.isSuccessful) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["id"] = "${keyId}"
                        hashMap["imagenUrl"] = "${urlImgCargada}"

                        val ref = FirebaseDatabase.getInstance().getReference("Productos")
                        ref.child(keyId).child("Imagenes").child(nombreImagen).updateChildren(hashMap)
                        progressDialog.dismiss()
                        Toast.makeText(this, getString(R.string.producto_agregado), Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    }

                }
                .addOnFailureListener { e->
                    progressDialog.dismiss()
                    Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun limpiarCampos() {
        imageSelecArrayList.clear()
        adaptadorIMagenSel.notifyDataSetChanged()
        binding.etNombresP.setText("")
        binding.etDescripcionP.setText("")
        binding.etPrecioP.setText("")
        binding.Categoria.setText("")
        binding.descuentoSwitch.isChecked = false
        binding.etPrecioConDescuento.setText("")
        binding.etNotaDescuento.setText("")

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