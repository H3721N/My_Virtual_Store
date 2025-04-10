package com.gomez.herlin.mi_tiendita_virtual.cliente

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivityMainClienteBinding
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.gomez.herlin.mi_tiendita_virtual.cliente.Bottom_Nav_Fragment_Cliente.FragmentMisOrdenes_C
import com.gomez.herlin.mi_tiendita_virtual.cliente.Nav_Fragments_Cliente.FragmentInicioC
import com.gomez.herlin.mi_tiendita_virtual.cliente.Nav_Fragments_Cliente.FragmentMiPerfilC

class MainActivityCliente : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainClienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_cliente)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding.navigationView.setNavigationItemSelectedListener(this)

        val toogle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )

        binding.drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        replaceceFragment(FragmentInicioC())
    }

    private fun replaceceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.navFragment, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.op_inicio_v -> {
                replaceceFragment(FragmentInicioC())
            }
            R.id.op_mis_ordenes_c -> {
                replaceceFragment(FragmentMisOrdenes_C())
            }
            R.id.op_mi_perfil_c -> {
                replaceceFragment(FragmentMiPerfilC())
            }
            R.id.nav_cerrar_sesion_c -> {
                Toast.makeText(this, "Cerrar Sesion", Toast.LENGTH_SHORT).show()
            }
            R.id.op_mi_tienda_c -> {
                Toast.makeText(this, "Mi Tienda", Toast.LENGTH_SHORT).show()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}