package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(),OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    private var pointOfInterest: PointOfInterest? = null
    private var randomPlace=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        Toast.makeText(requireContext(),"Select Location OR POI You Want To Remember",Toast.LENGTH_LONG).show()
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)


        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.saveLocation.setOnClickListener{view->
           if  (pointOfInterest != null) {
               onLocationSelected()
               Navigation.findNavController(view).navigate(SelectLocationFragmentDirections.actionSelectLocationFragmentToSaveReminderFragment())
               _viewModel.showToast.value="Enter Title and Description, Please"
               }
            else {
               Toast.makeText(requireContext(), "Select specific Place, Please", Toast.LENGTH_LONG)
                   .show()
           }
        }

        return binding.root
    }
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setMapClick(map)
        setPoiClick(map)
        enableMyLocation()
        setMapClick(map)
        setMapStyle(map)
    }
    private fun setMapStyle(map: GoogleMap){
        try {
            var success=map.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.map_style))
            if (!success){
                Log.e("Map Style", "Style parsing failed.")
            }
        }catch (e:Resources.NotFoundException){
            Log.e("Map Style", "Can't find style. Error: ", e)
        }

    }
    private fun setMapClick(map: GoogleMap){
        map.setOnMapClickListener{latLong->
            map.clear()
            pointOfInterest=PointOfInterest(latLong,"Random Place","Random Place")

            var snippet=String.format(Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLong.latitude,
                latLong.longitude)
            map.addMarker(MarkerOptions().position(latLong)
                .title(getString(R.string.dropped_pin))
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

        }
    }
    private fun setPoiClick(map: GoogleMap){
        map.setOnPoiClickListener{poi->
            map.clear()
            pointOfInterest=poi
            var poiMarker=map.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))
            poiMarker?.showInfoWindow()

        }
    }


    private fun onLocationSelected() {

            _viewModel.selectedPOI.value=pointOfInterest
            _viewModel.longitude.value=pointOfInterest?.latLng?.longitude
            _viewModel.latitude.value=pointOfInterest?.latLng?.latitude
            _viewModel.reminderSelectedLocationStr.value=pointOfInterest?.name
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType=GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType=GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType=GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType=GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

//    private fun isPermissionGranted() : Boolean {
//        return ContextCompat.checkSelfPermission(
//            requireActivity(),
//            Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
//    }


    private fun enableMyLocation() {
        when {
            (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) -> {

                map.isMyLocationEnabled = true

            }
            (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) ->{
                this.requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }

            else ->
                //Request permission
                this.requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {

                if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    enableMyLocation()

                } else {
                    Toast.makeText(context, "Location permission important here", Toast.LENGTH_LONG).show()
                }

            }

        }

    }


}
