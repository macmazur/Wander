package com.example.wander

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.map_content.*
import java.util.*


//https://medium.com/@irenenaya/drawing-path-between-two-points-in-google-maps-with-kotlin-in-android-app-af2f08992877
//https://github.com/irenenaya/Kotlin-Practice/blob/master/MapsRouteActivity.kt
class MapsActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private val TAG = MapsActivity::class.java.simpleName
    private val PERTH = LatLng(52.25240, 20.98783)
    private lateinit var markerPerth: Marker

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //<--Places Api
        // Initialize the SDK
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        // Create a new Places client instance.
        val placesClient: PlacesClient = Places.createClient(this)

        val autocompleteFragment = supportFragmentManager
                .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        // Set up a PlaceSelectionListener to handle the response.
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: ${place.name}, ${place.id}, ${place.latLng}")
                map.addMarker(place.latLng?.let { MarkerOptions().position(it).title(place.name) })
                map.moveCamera(CameraUpdateFactory.newLatLng(place.latLng))
            }

            override fun onError(status: Status) {
                // TODO: Get info about the status.
                Log.i(TAG, "Status: ${status.toString()}")
            }
        })
        //-->

        sheetBehavior = BottomSheetBehavior.from<LinearLayout>(bottom_sheet)

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED ->
                        btBottomSheet.text = "Close Bottom Sheet" //https://www.kotlindevelopment.com/why-should-use-anko/
                    BottomSheetBehavior.STATE_COLLAPSED ->
                        btBottomSheet.text = "Expand Bottom Sheet"
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        btBottomSheet.setOnClickListener(View.OnClickListener {
            //expandCloseSheet()
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                btBottomSheet.text = "Close Bottom Sheet"
            } else {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                btBottomSheet.text = "Expand Bottom Sheet"
            }
        })

        sheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        fab.setOnClickListener {

        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setMapStyle(map)

        val LatLongB = LatLngBounds.Builder()
        val kolska = LatLng(52.25081, 20.97806)
        val pokorna = LatLng(52.25432, 20.99344)

        /*
        1: World
        5: Landmass/continent
        10: City
        15: Streets
        20: Buildings
        */

        //val kolska = LatLng(latitude, longitude)
        //map.moveCamera(CameraUpdateFactory.newLatLng(kolska))
        map.addMarker(MarkerOptions().position(kolska).title("Kolska"))
        map.addMarker(MarkerOptions().position(pokorna).title("Pokorna"))

        markerPerth = map.addMarker(MarkerOptions().position(PERTH).title("Perth").icon(
                vectorToBitmap(R.drawable.ic_battery_marker, Color.GREEN))) //Color.parseColor("#0066ff")
        markerPerth.tag = 0

        map.setOnMarkerClickListener(this)
        setMapLongClick(map)
        //map.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(this))
        val zoomLevel = 15f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(kolska, zoomLevel))

        /*
        // Declare polyline object and set up color and width
        val options = PolylineOptions()
        options.color(Color.GREEN)
        options.width(7f)

        // build URL to call API
        val url = getURL(kolska, pokorna)
        async {
            // Connect to URL, download content and convert into string asynchronously
            val result = URL(url).readText()
            uiThread {
                // When API call is done, create parser and convert into JsonObjec
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                // get to the correct element in JsonObject
                val routes = json.array<JsonObject>("routes")
                val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                // For every element in the JsonArray, decode the polyline string and pass all points to a List
                val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!)  }
                // Add  points to polyline and bounds
                options.add(kolska)
                LatLongB.include(kolska)
                for (point in polypts)  {
                    options.add(point)
                    LatLongB.include(point)
                }
                options.add(pokorna)
                LatLongB.include(pokorna)
                // build bounds
                val bounds = LatLongB.build()
                // add polyline to the map
                map.addPolyline(options)

                //wrap data!
                txtView1.text = routes["legs"]["start_address"][0].toString()
                txtView2.text = routes["legs"]["end_address"][0].toString()
                txtView3.text = routes["legs"]["distance"]["text"][0].toString() + " / " + routes["legs"]["duration"]["text"][0].toString()

                sheet_tittle.text = marker.title
                sheet_position.text = marker.title

                // show map with route centered
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        }
        */

    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this,
                            R.raw.map_style
                    )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    /**
     * Demonstrates converting a [Drawable] to a [BitmapDescriptor],
     * for use as a marker icon.
     */
    private fun vectorToBitmap(@DrawableRes id : Int, @ColorInt color : Int): BitmapDescriptor {
        val vectorDrawable: Drawable? = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val key = "key=" + getString(R.string.google_maps_key)
        //mode = driving default
        val params = "$origin&$dest&$sensor&$key"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    /**
     * Method to decode polyline points
     */
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }


    /** Called when the user clicks a marker.  */
    override fun onMarkerClick(marker: Marker): Boolean {

        // Retrieve the data from the marker.
        val clickCount = marker.tag as? Int

        // Check if a click count was set, then display the click count.
        clickCount?.let {
            val newClickCount = it + 1
            marker.tag = newClickCount
            Toast.makeText(
                    this,
                    "${marker.title} has been clicked $newClickCount times.",
                    Toast.LENGTH_SHORT
            ).show()
        }

        sheet_tittle.text = marker.title
        sheet_position.text = marker.position.toString()
        sheet_address_from.text = ""
        sheet_address_to.text = ""
        sheet_distance.text = ""
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            // A Snippet is Additional text that's displayed below the title.
            val snippet = String.format(
                    Locale.getDefault(),
                    "Lat: %1$.5f, Long: %2$.5f",
                    latLng.latitude,
                    latLng.longitude
            )
            map.addMarker(
                    MarkerOptions()
                            .position(latLng)
                            .title(getString(R.string.dropped_pin))
                            .snippet(snippet)

            )
            Toast.makeText(applicationContext,"Marker setted!",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}