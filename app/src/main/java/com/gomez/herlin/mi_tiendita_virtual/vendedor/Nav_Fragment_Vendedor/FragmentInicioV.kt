package com.gomez.herlin.mi_tiendita_virtual.vendedor.Nav_Fragment_Vendedor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.databinding.FragmentBlankInicioVBinding
import com.gomez.herlin.mi_tiendita_virtual.vendedor.AgregarProductoActivity
import com.gomez.herlin.mi_tiendita_virtual.vendedor.Bottom_Nav_Fragment_V.FragmentMIsProductosV
import com.gomez.herlin.mi_tiendita_virtual.vendedor.Bottom_Nav_Fragment_V.FragmentOrdenesV


class FragmentInicioV : Fragment() {

    private lateinit var binding: FragmentBlankInicioVBinding
    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBlankInicioVBinding.inflate(inflater, container, false)

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.op_mis_productos_v -> {
                    replaceFragment(FragmentMIsProductosV())
                }
                R.id.op_mis_ordenes_v -> {
                    replaceFragment(FragmentOrdenesV())
                }
            }
            true
        }

        replaceFragment(FragmentMIsProductosV())
        binding.bottomNavigation.selectedItemId = R.id.op_mis_productos_v

        binding.addFab.setOnClickListener {
            startActivity(Intent(context, AgregarProductoActivity::class.java))
        }


        return binding.root
    //inflater.inflate(R.layout.fragment_blank_inicio_v, container, false)
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.bottomFragment, fragment).commit()
    }


}