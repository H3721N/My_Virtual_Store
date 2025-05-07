package com.gomez.herlin.mi_tiendita_virtual.vendedor.Productos

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
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

    private var Edicion = false
    private var idProducto = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarCategorias()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.cargando))
        progressDialog.setCanceledOnTouchOutside(false)

        Edicion = intent.getBooleanExtra("Edicion", false)

        // Vistas ocultas
        binding.etPorcentajedescuentoP.visibility = View.GONE
        binding.btnCalcularPrecioDesc.visibility = View.GONE
        binding.etPrecioConDescuentoPTXT.visibility = View.GONE
        binding.etPrecioConDescuento.visibility = View.GONE
        binding.etNotaDescuento.visibility = View.GONE

        binding.descuentoSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.etPorcentajedescuentoP.visibility = View.VISIBLE
                binding.btnCalcularPrecioDesc.visibility = View.VISIBLE
                binding.etPrecioConDescuentoPTXT.visibility = View.VISIBLE
                binding.etPrecioConDescuento.visibility = View.VISIBLE
                binding.etNotaDescuento.visibility = View.VISIBLE
            } else {
                binding.etPorcentajedescuentoP.visibility = View.GONE
                binding.btnCalcularPrecioDesc.visibility = View.GONE
                binding.etPrecioConDescuentoPTXT.visibility = View.GONE
                binding.etPrecioConDescuento.visibility = View.GONE
                binding.etNotaDescuento.visibility = View.GONE
            }
        }

        if (Edicion) {
            idProducto = intent.getStringExtra("idProducto") ?: ""
            binding.txtAgregarProducto.text = getString(R.string.editar_producto)
            cargarInfo()
        } else {
            binding.txtAgregarProducto.text = getString(R.string.agregar_producto)
        }

        imageSelecArrayList = ArrayList()

        binding.imgAgregarProducto.setOnClickListener {
            seleccionarImg()
        }

        binding.Categoria.setOnClickListener {
            selecCategorias()
        }

        binding.btnCalcularPrecioDesc.setOnClickListener {
            calcularPrecioDesc()
        }

        binding.btnAgregarProducto.setOnClickListener {
            validarInfo()
        }

        cargarImagenes()
    }

    private fun cargarInfo() {
        var ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // se obtiene la informacion de la db
                val nombre = "${snapshot.child("nombre").value}"
                val descripcion = "${snapshot.child("descripcion").value}"
                val categoria = "${snapshot.child("categoria").value}"
                val precio = "${snapshot.child("precio").value}"
                val precioDesc = "${snapshot.child("precioDesc").value}"
                val notaDesc = "${snapshot.child("notaDesc").value}"

                // se setea la informacion en los campos
                binding.etNombresP.setText(nombre)
                binding.etDescripcionP.setText(descripcion)
                binding.Categoria.setText(categoria)
                binding.etPrecioP.setText(precio)
                binding.etPorcentajedescuentoP.setText(precioDesc)
                binding.etNotaDescuento.setText(notaDesc)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun calcularPrecioDesc() {
        val precioOriginal = binding.etPrecioP.text.toString().trim()
        val notaDescuento = binding.etNotaDescuento.text.toString().trim()
        val porcentaje = binding.etPorcentajedescuentoP.text.toString().trim()

        if (precioOriginal.isEmpty()) {
            Toast.makeText(this, getString(R.string.precio_null), Toast.LENGTH_SHORT).show()
        } else if (notaDescuento.isEmpty()) {
            Toast.makeText(this, getString(R.string.nota_desc_null), Toast.LENGTH_SHORT).show()
        } else if (porcentaje.isEmpty()) {
            Toast.makeText(this, getString(R.string.nota_desc_null), Toast.LENGTH_SHORT).show()
        } else {
            val precioOriginalDouble = precioOriginal.toDouble()
            val porcentajDouble = porcentaje.toDouble()
            val descuento = precioOriginalDouble * (porcentajDouble / 100)

            val precioConDescAplicado = precioOriginalDouble - descuento

            binding.etPrecioConDescuento.text = precioConDescAplicado.toString()
        }
    }

    private var nombreP = ""
    private var descripcionP = ""
    private var categoriaP = ""
    private var precioP = ""
    private var descuentoHab = false
    private var precioDescP = ""
    private var notaDescP = ""
    private var porcentajeDescP = ""

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
            binding.etDescripcionP.error = getString(R.string.description_null)
            binding.etDescripcionP.requestFocus()
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
               notaDescP = binding.etNotaDescuento.text.toString().trim()
               porcentajeDescP = binding.etPorcentajedescuentoP.text.toString().trim()
               precioDescP = binding.etPrecioConDescuento.text.toString().trim()

               if (notaDescP.isEmpty()) {
                   binding.etNotaDescuento.text.toString().trim()
                   binding.etNotaDescuento.requestFocus()
               } else if (porcentajeDescP.isEmpty()) {
                   binding.etPorcentajedescuentoP.error = getString(R.string.porcentaje_null)
                   binding.etPorcentajedescuentoP.requestFocus()
               }else if (precioDescP.isEmpty()) {
                   binding.etPrecioConDescuento.setText(getString(R.string.descuento_null))
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
        binding.etNotaDescuento.setText("")
        binding.etPorcentajedescuentoP.setText("")
        binding.etPrecioConDescuento.setText("")

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