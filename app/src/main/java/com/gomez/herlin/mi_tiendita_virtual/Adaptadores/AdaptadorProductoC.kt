package com.gomez.herlin.mi_tiendita_virtual.Adaptadores

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gomez.herlin.mi_tiendita_virtual.Constantes
import com.gomez.herlin.mi_tiendita_virtual.DetalleProducto.DetalleProductoActivity
import com.gomez.herlin.mi_tiendita_virtual.Filtro.FiltroProducto
import com.gomez.herlin.mi_tiendita_virtual.Modelos.ModeloProducto
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemCategoriaCBinding
import com.gomez.herlin.mi_tiendita_virtual.databinding.ItemProductoCBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorProductoC : RecyclerView.Adapter<AdaptadorProductoC.HolderProducto>, Filterable {

    private lateinit var binding : ItemProductoCBinding

    private var mContext : Context
    var productosArrayList : ArrayList<ModeloProducto>
    private var filtroLista : ArrayList<ModeloProducto>
    private var filtro : FiltroProducto? = null
    private var firebaseAuth : FirebaseAuth

    constructor(mContext: Context, productosArrayList: ArrayList<ModeloProducto>) {
        this.mContext = mContext
        this.productosArrayList = productosArrayList
        this.filtroLista = productosArrayList
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderProducto {
        binding = ItemProductoCBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return HolderProducto(binding.root)
    }

    override fun getItemCount(): Int {
        return productosArrayList.size
    }

    override fun onBindViewHolder(holder: HolderProducto, position: Int) {
        val modeloProducto = productosArrayList[position]

        val nombre = modeloProducto.nombre

        cargarPrimeraImg(modeloProducto, holder)

        visualizarDescuento(modeloProducto, holder)

        comprobarFavorito(modeloProducto, holder)

        holder.item_nombre_p.text = "${nombre}"

        // evento al precionar el boton de favorito

        holder.Ib_fav.setOnClickListener {
            val favorito = modeloProducto.favorito
            if (favorito) {
                // fav = true
                Constantes().eliminarProductoFav(mContext, modeloProducto.id)
            } else {
                // fav = false
                Constantes().agregarProductoFav(mContext, modeloProducto.id)
            }

        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, DetalleProductoActivity::class.java)
            intent.putExtra("idProducto", modeloProducto.id)
            mContext.startActivity(intent)
        }

        // evento para agregar al carrito el producto seleccionado

        holder.agregar_carrito.setOnClickListener {
            verCarrito(modeloProducto)
        }


    }

    var costo : Double = 0.0
    var costoFinal : Double = 0.0
    var cantidadProd : Int = 0

    private fun verCarrito(modeloProducto: ModeloProducto) {
        // Declarar vistas
        var imagenSIV : ShapeableImageView
        var nombreTv : TextView
        var descripcionTv : TextView
        var notaDescTv : TextView
        var precioOriginalTv : TextView
        var precioDescuentoTv : TextView
        var precioFinalTv : TextView
        var btnDisminuir : ImageButton
        var cantidadTv : TextView
        var btnAumentar : ImageButton
        var btnAgregarCarrito : MaterialButton

        val dialog = Dialog(mContext)
        dialog.setContentView(R.layout.carrito_compras)

        imagenSIV = dialog.findViewById(R.id.imagenPCar)
        nombreTv = dialog.findViewById(R.id.nombrePCar)
        descripcionTv = dialog.findViewById(R.id.descripcionPCar)
        notaDescTv = dialog.findViewById(R.id.notaDescPCar)
        precioOriginalTv = dialog.findViewById(R.id.precioOriginalPCar)
        precioDescuentoTv = dialog.findViewById(R.id.precioDescPCar)
        precioFinalTv = dialog.findViewById(R.id.precioFinalPCar)
        btnDisminuir = dialog.findViewById(R.id.btnDisminuir)
        cantidadTv = dialog.findViewById(R.id.cantidadPCar)
        btnAumentar = dialog.findViewById(R.id.btnAumentar)
        btnAgregarCarrito = dialog.findViewById(R.id.btnAgregarPCar)

        // Datos o informacion del modelo

        val productoId = modeloProducto.id
        val nombre = modeloProducto.nombre
        val descripcion = modeloProducto.descripcion
        val precio = modeloProducto.precio
        val precioDesc = modeloProducto.precioDesc
        val notaDesc = modeloProducto.notaDesc

        if (!precioDesc.equals("0") && !notaDesc.equals("")) {
            // el producto tiene descuento

            notaDescTv.visibility = View.VISIBLE
            precioDescuentoTv.visibility = View.VISIBLE

            notaDescTv.setText(notaDesc)
            precioDescuentoTv.setText(precioDesc.plus(" USD"))
            precioOriginalTv.setText(precio.plus(" USD"))
            precioOriginalTv.paintFlags = precioOriginalTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG //Marca el precio con un tachas=da

            costo = precioDesc.toDouble()

        } else {
            // el producto no tiene descuento
            precioOriginalTv.setText(precio.plus(" USD"))
            precioOriginalTv.paintFlags = precioOriginalTv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            costo = precio.toDouble()
        }

        // Settear la informacion

        nombreTv.setText(nombre)
        descripcionTv.setText(descripcion)

        costoFinal = costo
        cantidadProd = 1

        btnAumentar.setOnClickListener {
            costoFinal = costoFinal+costo
            cantidadProd++
            precioFinalTv.text = costoFinal.toString()
            cantidadTv.text = cantidadProd.toString()
        }

        btnDisminuir.setOnClickListener{
            if (cantidadProd > 1) {
                costoFinal = costoFinal-costo
                cantidadProd--

                precioFinalTv.text = costoFinal.toString()
                cantidadTv.text = cantidadProd.toString()
            }
        }

        precioFinalTv.text = costo.toString().plus(" USD")

        //ver imagen del producto en el carrito

        cargarImg(productoId, imagenSIV)

        btnAgregarCarrito.setOnClickListener {
            agregarCarrito(mContext, modeloProducto, costoFinal, cantidadProd)
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(true)

    }

    private fun agregarCarrito(mContext: Context, modeloProducto: ModeloProducto, costoFinal: Double, cantidadProd: Int) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val hashMap = HashMap<String, Any>()

        hashMap["idProducto"] = modeloProducto.id
        hashMap["nombre"] = modeloProducto.nombre
        hashMap["precio"] = modeloProducto.precio
        hashMap["precioDesc"] = modeloProducto.precioDesc
        hashMap["precioFinal"] = costoFinal.toString()
        hashMap["cantidad"] = cantidadProd.toString()

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("CarritoCompras").child(modeloProducto.id)
            .setValue(hashMap)
            .addOnSuccessListener {
                // producto agregado al carrito
                Toast.makeText(mContext, mContext.getString(R.string.add_toCar), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // error al agregar al carrito
                Toast.makeText(mContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarImg(productoId: String, imagenSIV: ShapeableImageView?) {
        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(productoId).child("Imagenes")
            .limitToFirst(1)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        // Extraer la url de la primera imagen
                        val imagenUrl = "${ds.child("imagenUrl").value}"

                        try {
                            Glide.with(mContext)
                                .load(imagenUrl)
                                .placeholder(R.drawable.item_img_producto)
                                .into(imagenSIV!!)
                        } catch (e: Exception) {

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun comprobarFavorito(modeloProducto: ModeloProducto, holder: HolderProducto) {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(firebaseAuth.uid!!).child("Favoritos").child(modeloProducto.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val favorito = snapshot.exists()
                    modeloProducto.favorito = favorito

                    if (favorito) {
                        holder.Ib_fav.setImageResource(R.drawable.icon_favorite)
                        modeloProducto.favorito = true
                    } else {
                        // producto no en favoritos
                        holder.Ib_fav.setImageResource(R.drawable.icon_no_favorite)
                        modeloProducto.favorito = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun visualizarDescuento(modeloProducto: ModeloProducto,holder: AdaptadorProductoC.HolderProducto) {
        if(!modeloProducto.precioDesc.equals("0") && !modeloProducto.notaDesc.equals("")) {
            // Habilitar vista
            holder.item_nota_desc.visibility = View.VISIBLE
            holder.item_precio_p_desc.visibility = View.VISIBLE

            holder.item_nota_desc.text = "${modeloProducto.notaDesc}"
            holder.item_precio_p_desc.text = "${modeloProducto.precioDesc}${" USD"}"
            holder.item_precio_p.text = "${modeloProducto.precio}${" USD"}"
            // tacha el precio original
            holder.item_precio_p.paintFlags = holder.item_precio_p.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            // sin descuento
            holder.item_nota_desc.visibility = View.GONE
            holder.item_precio_p_desc.visibility = View.GONE

            holder.item_precio_p.text = "${modeloProducto.precio}${" USD"}"
            holder.item_precio_p.paintFlags = holder.item_precio_p.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    private fun cargarPrimeraImg(modeloProducto: ModeloProducto, holder: AdaptadorProductoC.HolderProducto) {
        val idProducto = modeloProducto.id

        val ref = FirebaseDatabase.getInstance().getReference("Productos")
        ref.child(idProducto).child("Imagenes")
            .limitToFirst(1)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val imagenUrl = "${ds.child("imagenUrl").value}"

                        try {
                            Glide.with(mContext)
                                .load(imagenUrl)
                                .placeholder(R.drawable.item_img_producto)
                                .into(holder.imagenP)
                        } catch (e:Exception) {

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    inner class HolderProducto (itemView : View) : RecyclerView.ViewHolder(itemView) {
        var imagenP = binding.imagenP
        var item_nombre_p = binding.itemNombreP
        var item_precio_p = binding.itemPrecioP
        var item_precio_p_desc = binding.itemPrecioPDesc
        var item_nota_desc = binding.itemNotaP
        var Ib_fav = binding.IbFav

        var agregar_carrito = binding.itemAgregarCarritoP
    }

    override fun getFilter(): Filter {
        if (filtro == null) {
            filtro = FiltroProducto(this, filtroLista)
        }
        return filtro as FiltroProducto
    }


}
