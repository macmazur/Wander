package com.example.wander

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowForGoogleMap(context: Context) : GoogleMap.InfoWindowAdapter {

    var mContext = context
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.custom_info_window, null)

    private fun rendowWindowText(marker: Marker, view: View){

        val tvTitle = view.findViewById<TextView>(R.id.title)
        val tvSnippet = view.findViewById(R.id.snippet) as TextView
        val btnClickMe = view.findViewById(R.id.infoWindowButton) as ImageButton

        tvTitle.text = marker.title
        tvSnippet.text = marker.snippet
        btnClickMe.setOnClickListener {
            Toast.makeText(mContext, "You clicked me.", Toast.LENGTH_LONG).show()
        }

    }

    override fun getInfoContents(marker: Marker): View {
        rendowWindowText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        rendowWindowText(marker, mWindow)
        return mWindow
    }
}