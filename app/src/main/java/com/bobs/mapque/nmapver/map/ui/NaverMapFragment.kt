package com.bobs.mapque.nmapver.map.ui

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.bobs.baselibrary.ext.boolean
import com.bobs.baselibrary.util.loge
import com.bobs.mapque.nmapver.R
import com.bobs.mapque.nmapver.map.data.ResultListener
import com.bobs.mapque.nmapver.map.data.searchdialog.SearchDialog
import com.bobs.mapque.nmapver.map.vm.MapViewModel
import com.bobs.mapque.nmapver.ui.MainActivity
import com.bobs.mapque.nmapver.util.ext.hideKeyboard
import com.bobs.mapque.nmapver.util.ext.hideLoading
import com.bobs.mapque.nmapver.util.ext.showLoading
import com.bobs.mapque.nmapver.util.ext.toast
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import kotlinx.android.synthetic.main.fragment_naver_map.*
import org.joda.time.DateTime
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.ArrayList

class NaverMapFragment : Fragment() {
    companion object {
        fun newInstance(args: Bundle? = null): NaverMapFragment {
            val mapFragment = NaverMapFragment()

            args?.let {
                mapFragment.arguments = it
            }

            return mapFragment
        }
    }

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap

    private lateinit var locationSource: FusedLocationSource
    private var curLocation: LatLng? = null
    private var curMarker: Marker? = null

    private var searchQuery: String? = null
    private var floatingAddress: String? = null

    private val mapViewModel: MapViewModel by viewModel { parametersOf() }

    private val prefs: SharedPreferences by inject()
    private var isFirstOpenHelpDialog by prefs.boolean("", false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_naver_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        mapView = view.findViewById(R.id.naver_mapview)!!
        mapView.run {
            onCreate(savedInstanceState)
            getMapAsync { navermap ->
                naverMap = navermap

                naverMap.run {
                    this.locationSource = locationSource

                    setOnMapClickListener { pointF, latLng ->
                        mapView.parent.requestDisallowInterceptTouchEvent(true)
                        resetSearchView()
                    }
                }

                setMyLocation()
            }
        }

        search_view.run {
            isSubmitButtonEnabled = true

            setOnQueryTextListener(object :
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    hideKeyboard()
                    showLoading()

                    query?.let {
                        mapViewModel.getSearchAddress(it, object : ResultListener<String> {
                            override fun success(result: String) {
                                hideLoading()
                                searchQuery = it
                            }

                            override fun fail(msg: String?) {
                                hideLoading()
                                toast(msg ?: getString(R.string.search_view_search_fail))
                            }
                        })
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    return true
                }
            })
        }

        helpBtn.run {
            setOnClickListener {
                showHelpDialog()
            }
        }

        myLocationBtn.run {
            setOnClickListener {
                setMyLocation()
            }
        }

        lastLocationBtn.run {
            setOnClickListener {
                curLocation?.let {
                    showLoading()
                    moveMap(curLocation, searchQuery.toString(),floatingAddress.toString())
                } ?: toast(getString(R.string.last_location_empty))
            }
        }

        mapViewModel.searchAddressData.observe(
            viewLifecycleOwner,
            Observer { geocodingResponse ->
                val searchDialogs = mutableListOf<SearchDialog>()

                geocodingResponse.addresses?.forEach {
                    it?.roadAddress?.let { title ->
                        searchDialogs.add(SearchDialog(title))
                    }
                }

                // 검색 다이얼로그 show
                SimpleSearchDialogCompat(activity,
                    getString(R.string.search_dialog_title),
                    getString(R.string.search_dialog_search_hint),
                    null,
                    searchDialogs as ArrayList<SearchDialog>,
                    SearchResultListener { dialog, item, position ->
                        val selectedItem =
                            geocodingResponse.addresses?.find { addressesItem -> addressesItem?.roadAddress == item.title }

                        curLocation =
                            LatLng(selectedItem?.Y?.toDouble()!!, selectedItem.X?.toDouble()!!)

                        showLoading()

                        // room에 검색한 주소 저장
                        mapViewModel.insertSearchAddress(
                            searchQuery.toString(),
                            item.title,
                            curLocation?.latitude!!,
                            curLocation?.longitude!!,
                            DateTime.now()
                        )

                        floatingAddress = selectedItem.roadAddress

                        moveMap(curLocation, searchQuery.toString(), floatingAddress.toString())

                        dialog.dismiss()
                    }).show()
            }
        )

        // 앱 최초 설치 시 도움말을 띄운다
        if (!isFirstOpenHelpDialog) {
            showHelpDialog()
            isFirstOpenHelpDialog = true
        }
    }

    private fun showHelpDialog() {
        val mainActivity = activity as MainActivity

        MaterialDialog(mainActivity).show {
            title(text = getString(R.string.help_title))
            message(text = getString(R.string.help_content).trimIndent())
            positiveButton(text = getString(R.string.marker_dialog_cancelbtn_title)) {
                it.dismiss()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    private fun resetSearchView() {
        search_view.setQuery("", false)
        search_view.clearFocus()

        hideKeyboard()
    }

    private fun setMyLocation() {
        showLoading()

        locationSource.activate {
            val myLocation = LatLng(it?.latitude!!, it.longitude)

            mapViewModel.getSearchAddressName("${myLocation.longitude},${myLocation.latitude}", object : ResultListener<String?>{
                override fun success(result: String?) {
                    moveMap(myLocation, "내 위치", result.toString())

                    locationSource.deactivate()
                }

                override fun fail(msg: String?) {
//                    toast(msg ?: getString(R.string.search_view_search_fail))
                }
            })
        }
    }

    fun moveMap(location: LatLng?, caption: String, address: String) {
        location?.let {
            curMarker?.map = null

            curMarker = Marker().apply {
                position = location
                map = naverMap
                iconTintColor = Color.RED
                captionText = caption

                activity?.let { activity ->
                    setOnClickListener {

                        MaterialDialog(activity).show {
                            title(text = caption)
                            message(text = address) {
                                messageTextView.gravity = Gravity.CENTER
                            }
                            negativeButton(text = getString(R.string.marker_dialog_cancelbtn_title)) {
                                it.dismiss()
                            }
                        }
                        true
                    }
                }
            }

            naverMap.moveCamera(CameraUpdate.scrollTo(location))

            loge("${location.latitude}, ${location.longitude}")

            hideLoading()
        }
    }
}