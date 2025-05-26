package com.gomez.herlin.mi_tiendita_virtual.Mapas

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.gomez.herlin.mi_tiendita_virtual.R
import com.gomez.herlin.mi_tiendita_virtual.R.string.permiso_denegado
import com.gomez.herlin.mi_tiendita_virtual.databinding.ActivitySeleccionarUbicacionBinding
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class SeleccionarUbicacionActivity : AppCompatActivity() , OnMapReadyCallback {

    private lateinit var binding: ActivitySeleccionarUbicacionBinding
    private companion object {
        private const val DEFAUL_ZOOM = 15
    }

    private var mMap : GoogleMap?=null

    private var mPlaceClient : PlacesClient?=null
    private var mFusedLocationProviderClient : FusedLocationProviderClient ?= null

    private var mLastKnownLocation : Location?=null
    private var selectedLatitude : Double? = null
    private var selectedLongitude : Double? = null
    private var address = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeleccionarUbicacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listoLl.visibility = View.GONE

        val mapFragment = supportFragmentManager.findFragmentById(R.id.MapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Places.initialize(this, getString(R.string.mi_google_maps_api_key))
        mPlaceClient = Places.createClient(this)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val autoCompleteSupportMapFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        val placeList = arrayOf (
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        autoCompleteSupportMapFragment.setPlaceFields(listOf(*placeList))

        autoCompleteSupportMapFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {

            override fun onPlaceSelected(place: Place) {
                val id = place.id
                val name = place.name
                val latlng = place.latLng

                if (latlng != null) {
                    selectedLatitude = latlng.latitude
                    selectedLongitude = latlng.longitude
                    address = place.address ?: ""
                    addMarker(latlng, name, address)
                } else {
                    Toast.makeText(
                        this@SeleccionarUbicacionActivity,
                        "No se pudo obtener la ubicación seleccionada. Place: ${place.name}, ID: ${place.id}, Address: ${place.address}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onError(p0: Status) {
            }


        })

        binding.IbGps.setOnClickListener {
            if ( isGpsActivated() ) {
                solicitarPermisoLocalizacion.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                Toast.makeText(this, (R.string.null_position), Toast.LENGTH_SHORT).show()
            }
        }

        binding.BtnListo.setOnClickListener {
            val intent = Intent()
            intent.putExtra("latitud", selectedLatitude)
            intent.putExtra("longitud", selectedLongitude)
            intent.putExtra("direccion", address)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

    private fun elegirLugarActual() {
        if (mMap == null) {
            return
        }
        detectAndShowDeviceLoccation()
    }

    @SuppressLint("MissingPermission")
    private fun detectAndShowDeviceLoccation () {
        try {
            val locationResult = mFusedLocationProviderClient!!.lastLocation
            locationResult.addOnSuccessListener { location ->
                if (location != null) {
                    mLastKnownLocation = location

                    selectedLatitude = location.latitude
                    selectedLongitude = location.longitude
                    val latlng = LatLng(selectedLatitude!!, selectedLongitude!!)
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, DEFAUL_ZOOM.toFloat()))
                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(DEFAUL_ZOOM.toFloat()))
                    direccionLatLng(latlng)
                }
            }
                .addOnFailureListener { e->
                    Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
        }
    }

    private fun isGpsActivated(): Boolean {

        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnable = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            networkEnable = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        return !(!gpsEnabled && !networkEnable)

    }

    @SuppressLint("MissingPermission")
    private val solicitarPermisoLocalizacion : ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { seConcede ->
            if (seConcede) {
                mMap!!.isMyLocationEnabled = true
                elegirLugarActual()
            } else {
                Toast.makeText(this, (permiso_denegado), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        solicitarPermisoLocalizacion.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        mMap!!.setOnMapClickListener { latlng ->
            selectedLatitude = latlng.latitude
            selectedLongitude = latlng.longitude

            direccionLatLng(latlng)


        }

    }

    private fun direccionLatLng(latlng: LatLng) {
        val geoCoder = Geocoder(this)
        try {
            val addressLst = geoCoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
            val mAdress = addressLst!![0]

            val addressLine = mAdress.getAddressLine(0)
            val subLocality = mAdress.subLocality
            val locality = mAdress.locality

            address = "${addressLine}"
            addMarker(latlng, "${subLocality}", "${locality}")

        } catch ( e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addMarker(latlng: LatLng, titulo : String, direccion : String ) {
        mMap!!.clear()
        try {
            val markerOptions = MarkerOptions()
            markerOptions.position(latlng)
            markerOptions.title("${titulo}")
            markerOptions.snippet("${direccion}")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

            mMap!!.addMarker(markerOptions)
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, DEFAUL_ZOOM.toFloat()))
            binding.listoLl.visibility = View.VISIBLE
            binding.lugarSelecTv.text  = direccion
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}