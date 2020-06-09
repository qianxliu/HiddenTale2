package com.qianxin.hiddentale.ui.map

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.*
import com.amap.api.maps.model.*
import com.amap.api.maps.offlinemap.OfflineMapManager
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.qianxin.hiddentale.R

open class MapFragment : Fragment(), LocationSource, AMapLocationListener, PoiSearch.OnPoiSearchListener, CompoundButton.OnCheckedChangeListener, OfflineMapManager.OfflineMapDownloadListener {
    private var mapView: MapView? = null
    private var aMap: AMap? = null
    private lateinit var activity: AppCompatActivity
    private var mPoiSearch: PoiSearch? = null

    /**
     * 定位监听
     */
    private var mListener: LocationSource.OnLocationChangedListener? = null
    private var mlocationClient: AMapLocationClient? = null
    val target: LatLng? = null
    var cameraPosition: CameraPosition? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as AppCompatActivity
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //设置布局
        return inflater.inflate(R.layout.fragment_map, container, false)
        // 在activity执行onCreate时执行mapView.onCreate(savedInstanceState)，实现地图生命周期管理
        //mapView.onCreate(savedInstanceState);
    }


    @Throws(AMapException::class)
    protected open fun init() {
        if (aMap == null) {
            aMap = mapView?.map
            // 设置定位监听
            aMap?.setLocationSource(this)
            aMap?.mapType = AMap.MAP_TYPE_NORMAL
            aMap?.isMyLocationEnabled = true
            aMap?.setCustomMapStyle(
                    CustomMapStyleOptions()
                            .setEnable(true)
                            .setStyleId("0aa1d1eda90911d20d3551642fcd4980") //官网控制台-自定义样式 获取
            )
            aMap?.showIndoorMap(true)
            aMap?.showBuildings(true)
            //aMap.showMapText();
            val myLocationStyle = MyLocationStyle()
            myLocationStyle.interval(2)
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE)
            aMap?.myLocationStyle = myLocationStyle
            val uiSettings: UiSettings? = mapView?.map?.uiSettings
            // 设置默认定位按钮是否显示
            if (uiSettings != null) uiSettings.isMyLocationButtonEnabled = true
            if (uiSettings != null) uiSettings.isCompassEnabled = true
            //aMap.addOnMapLoadedListener((AMap.OnMapLoadedListener) this);
            //构造OfflineMapManager对象
            val amapManager = OfflineMapManager(activity.applicationContext, this)

            //按照cityname下载
            amapManager.downloadByCityName("西安市")
        }
    }

    //		mGPSModeGroup = (RadioGroup) findViewById(R.id.gps_radio_group);
    //		mGPSModeGroup.setOnCheckedChangeListener(this);
    //		mLocationErrText = (TextView)findViewById(R.id.location_errInfo_text);
    //		mLocationErrText.setVisibility(View.GONE);
    //followed by onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView = view.findViewById(R.id.map)
        try {
            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (mapView != null) {
            //保存状态
            mapView!!.onCreate(savedInstanceState)
            if (cameraPosition == null) {
                aMap?.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(target, 10F, 0F, 0F)))
            } else {
                aMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
    }

    /**
     * 方法必须重写
     */
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    /**
     * 方法必须重写
     */
    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        //deactivate();
    }

    /**
     * 方法必须重写
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        // TODO Auto-generated method stub
    }

    override fun onLocationChanged(amapLocation: AMapLocation?) {
//		TrickerUtils.showToast(getActivity(), amapLocation.getAddress());
        if (mListener != null && amapLocation != null) {
            try {
//				mLocationErrText.setVisibility(View.GONE);
                mListener!!.onLocationChanged(amapLocation) // 显示系统小蓝点
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        aMap?.getMyLocation()?.longitude?.let { initPoiSearch(aMap?.getMyLocation()!!.latitude, it) }
    }

    private fun initPoiSearch(lat: Double, lon: Double) {
        if (mPoiSearch == null) {
            val poiQuery: PoiSearch.Query = PoiSearch.Query("", "", "陕西省")
            val centerPoint = LatLonPoint(lat, lon)
            poiQuery.pageSize = 10 // 设置每页最多返回多少条poiitem
            poiQuery.pageNum = 1 //设置查询页码
            val searchBound: PoiSearch.SearchBound
            searchBound = PoiSearch.SearchBound(centerPoint, 5000)
            mPoiSearch = PoiSearch(activity.applicationContext, poiQuery)
            mPoiSearch!!.bound = searchBound
            mPoiSearch!!.setOnPoiSearchListener(this)
            mPoiSearch!!.searchPOIAsyn()
            mPoiSearch!!.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
                override fun onPoiSearched(poiResult: PoiResult, i: Int) {
                    val pois: ArrayList<PoiItem> = poiResult.pois
                    val list: MutableList<MultiPointItem> = ArrayList()
                    for (item in pois) {
                        val latLonPoint: LatLonPoint = item.latLonPoint
                        list.add(MultiPointItem(LatLng(latLonPoint.latitude, latLonPoint.longitude)))
                    }
                    //showResultOnMap(list);
                }

                override fun onPoiItemSearched(poiItem: PoiItem?, i: Int) {}
            })
        }
    }

    /*
    private fun showResultOnMap(list: List<MultiPointItem>) {
        val overlayOptions = MultiPointOverlayOptions()
        //overlayOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.p12));
        overlayOptions.anchor(0.5f, 0.5f)
        val multiPointOverlay: MultiPointOverlay = aMap?.addMultiPointOverlay(overlayOptions) ?:
        multiPointOverlay.setItems(list)
        aMap?.setOnMultiPointClickListener({ pointItem -> false })
    }


     */
    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        mListener = listener
        if (mlocationClient == null) {
            mlocationClient = AMapLocationClient(activity)
            val mLocationOption = AMapLocationClientOption()
            //			mLocationOption.setNeedAddress(true);
            //设置定位监听
            mlocationClient!!.setLocationListener(this)
            //设置为高精度定位模式
            mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            mLocationOption.interval = 2
            mlocationClient!!.setLocationOption(mLocationOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient!!.startLocation()
        }
    }

    override fun deactivate() {
        mListener = null
        if (mlocationClient != null) {
            mlocationClient!!.stopLocation()
            mlocationClient!!.onDestroy()
        }
        mlocationClient = null
    }

    /**
     * 方法必须重写
     */
    override fun onDestroy() {
        cameraPosition = aMap?.cameraPosition
        super.onDestroy()
        mapView?.onDestroy()
        mlocationClient?.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        try {
            val childFragmentManager = Fragment::class.java.getDeclaredField("mChildFragmentManager")
            childFragmentManager.isAccessible = true
            childFragmentManager[this] = null
        } catch (e: NoSuchFieldException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }

    override fun onDownload(i: Int, i1: Int, s: String?) {}
    override fun onCheckUpdate(b: Boolean, s: String?) {}
    override fun onRemove(b: Boolean, s: String?, s1: String?) {}
    override fun onPoiSearched(poiResult: PoiResult?, i: Int) {}
    override fun onPoiItemSearched(poiItem: PoiItem?, i: Int) {}

    companion object {
        fun getInstance() = MapFragment()
    }
}