package com.gomez.herlin.mi_tiendita_virtual.vendedor

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityMainVendedorBinding
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.gomez.herlin.mi_tiendita_virtual.vendedor.Bottom_Nav_Fragment_V.FragmentMIsProductosV
import com.gomez.herlin.mi_tiendita_virtual.vendedor.Bottom_Nav_Fragment_V.FragmentOrdenesV
import com.gomez.herlin.mi_tiendita_virtual.vendedor.Nav_Fragment_Vendedor.FragmentInicioV
import com.gomez.herlin.mi_tiendita_virtual.vendedor.Nav_Fragment_Vendedor.FragmentMiTiendaV
import com.gomez.herlin.mi_tiendita_virtual.vendedor.Nav_Fragment_Vendedor.FragmentResenaV
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class MainActivityVendedor : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainVendedorBinding
    private var firebaseAuth: FirebaseAuth? = null
    @Inject
    lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainVendedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        firebaseAuth = FirebaseAuth.getInstance()

        comprobarSesion()

        binding.navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        replaceFragment(FragmentInicioV())
        binding.navigationView.setCheckedItem(R.id.op_inicio_v)
    }

    private fun cerrarSesion() {
        firebaseAuth!!.signOut()
        startActivity(Intent(applicationContext, LoginVendedorActivity::class.java))
        finish()
        Toast.makeText(applicationContext, "Has cerrado la sesion", Toast.LENGTH_SHORT).show()
    }

    private fun comprobarSesion() {
        /*EL usuario que no ha iniciado sesion sera enviado a registro*/
        if(firebaseAuth!!.currentUser==null) {
            startActivity(Intent(applicationContext, LoginVendedorActivity::class.java))
            Toast.makeText(applicationContext, "Vendedor no registrado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Vendedor en linea", Toast.LENGTH_SHORT).show()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.navFragment, fragment).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.op_inicio_v -> {
                replaceFragment(FragmentInicioV())
            }
            R.id.op_mi_tienda_v -> {
                replaceFragment(FragmentMiTiendaV())
            }
            R.id.op_resenia_v -> {
                replaceFragment(FragmentResenaV())
            }
            R.id.op_cerrar_sesion_v -> {
                cerrarSesion()
                //Toast.makeText(applicationContext, "Saliste de la sesion", Toast.LENGTH_SHORT).show()
            }
            R.id.op_mis_productos_v -> {
                replaceFragment(FragmentMIsProductosV())
            }
            R.id.op_mis_ordenes_v -> {
                replaceFragment(FragmentOrdenesV())
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}