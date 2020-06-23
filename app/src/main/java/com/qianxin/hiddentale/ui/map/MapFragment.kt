package com.qianxin.hiddentale.ui.map

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.*
import com.amap.api.maps.AMap.*
import com.amap.api.maps.model.*
import com.amap.api.maps.offlinemap.OfflineMapManager
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.amap.api.navi.INaviInfoCallback
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.qianxin.hiddentale.R
import com.qianxin.hiddentale.utils.ToastUtil.showToast


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
    private val target: LatLng? = null


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



    fun init() {
        if (aMap == null) {
            aMap = mapView?.map
            // 设置定位监听
            aMap?.setLocationSource(this)
            aMap?.mapType ?: AMap.MAP_TYPE_NORMAL
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
        aMap!!.setOnMapClickListener { v: LatLng ->
            val markerOptions = MarkerOptions()
            markerOptions.position(LatLng(v.latitude, v.longitude)).setFlat(false).zIndex(-2f)
            markerOptions.draggable(true)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(resources, R.drawable.icon_end)))
            aMap!!.addMarker(markerOptions)
        }
        aMap!!.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                if (marker.zIndex == -2f) marker.remove()
            }
        })
        //生成标记点
        for (i in 1 until latLng.size) {
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng[i]).title(getUserList().get(i).getName()).snippet("""
    坐标${latLng[i]}
    ${BRIEFS[i]}点击查看详细信息
    """.trimIndent())
            if (Math.random() * 2 % 2 == 0.0) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.p11)) else markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.p12))
            markerOptions.zIndex(i.toFloat())
            aMap!!.addMarker(markerOptions)
        }

        // 定义 Marker 点击事件监听
        // marker 对象被点击时回调的接口
        // 返回 true 则表示接口已响应事件，否则返回false
        val markerClickListener = OnMarkerClickListener { marker: Marker ->
            if (marker.isInfoWindowShown && !marker.isFlat && marker.zIndex != -2f) {
                marker.hideInfoWindow()
                if (Math.random() * 2 % 2 == 0.0) marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p12)) else marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p11))
                caculate()
            } else if (!marker.isInfoWindowShown && !marker.isFlat && marker.zIndex != -2f) {
                marker.showInfoWindow()
                icon(marker, (Math.random() * 10 % 10).toInt())
                caculate()
            } else if (marker.zIndex == -2f) {
                AlertDialog(activity).setTitle("导航");
                naviLatLng = NaviLatLng(marker.position.latitude, marker.position.longitude)
            }
            true
        }
        val listener = OnInfoWindowClickListener { marker: Marker ->
            if (marker.zIndex < latLng.size - 1) {
                toActivity(activity, PdfViewActivity.createIntent(activity, URLS.get(marker.zIndex.toInt()), (marker.zIndex.toInt() + 1).toString()))
                caculate()
            } else if (marker.zIndex < latLng.size + 1) {
                toActivity(activity, PdfViewActivity.createIntent(activity, URLS.get(marker.zIndex.toInt()), (marker.zIndex.toInt() + 1).toString()))
                showToast(marker.title + "期待你的补充!")
                caculate()
            }
        }
        //绑定信息窗点击事件
        aMap!!.setOnInfoWindowClickListener(listener)
        // 绑定 Marker 被点击事件
        aMap!!.setOnMarkerClickListener(markerClickListener)
        isPosition()
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

    //西安市经纬度
    private val XIAN = LatLng(34.343147, 108.939621)
    private var cameraPosition = CameraPosition.Builder()
            .target(XIAN).zoom(18f).bearing(0f).tilt(30f).build()
    var temp = 0f
    private var naviLatLng: NaviLatLng? = null
    private val naviInfoCallback: INaviInfoCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    open fun getTarget(): LatLng? {
        return XIAN
    }

    open fun getCameraPosition(): CameraPosition {
        return cameraPosition
    }

    open fun setCameraPosition(cameraPosition: CameraPosition) {
        MapFragment.cameraPosition = cameraPosition
    }

    private val latLng = arrayOf(
            LatLng(34.057441, 108.312201),
            LatLng(34.235222, 108.931813),
            LatLng(34.192109, 108.896464),
            LatLng(34.235222, 108.931813),
            LatLng(34.086456, 108.519344),
            LatLng(34.235222, 108.931813),
            LatLng(34.235222, 108.931813),
            LatLng(34.234321, 108.949763),
            LatLng(34.23777, 108.766144),
            LatLng(34.4763, 109.364455),  //11
            LatLng(34.273603, 108.954741),
            LatLng(34.257313, 108.967721),
            LatLng(34.222472, 109.024829),
            LatLng(33.910533, 109.50539),
            LatLng(34.128889, 109.228452),  //16
            LatLng(34.248333, 108.927083),
            LatLng(34.246207, 108.98378))
    private val BRIEFS = arrayOf(
            "中外文明相互交融的见证\n",
            "唐王朝盛极而衰的挽歌\n",
            "中华现存最早的调兵凭证\n",
            "葡萄花鸟满庭芳，越千年，莫相忘\n",
            "四路上的“流动音团”\n",
            "道教文化的历史见证\n",
            "依色取巧，随形变化的俏色孤品\n",
            "力量与美的杰出代表\n",
            "千年战争历史的见证者\n",
            "西周第一青铜器\n",
            "陕西省西安市古城内西五路北新街七贤庄1号\n",
            "陕西省西安市碑林区建国路69号\n",
            "陕西省西安市雁塔区等驾坡街道长鸣路66号\n",
            "陕西省西安市蓝田县葛牌镇葛牌街\n",
            "陕西省西安市蓝田县孟吴路\n",
            "西安市碑林区太白北路229号\n",
            "西安市碑林区咸宁西路28号\n")

    private open fun icon(marker: Marker, len: Int) {
        when ((Math.random() * len % len).toInt()) {
            0 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p1))
            1 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p2))
            2 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p3))
            3 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p4))
            4 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p5))
            5 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p6))
            6 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p7))
            7 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p8))
            8 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p9))
            9 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p10))
            10 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p11))
            11 -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.p12))
        }
    }

    fun isPosition() {
        if (AMapUtils.calculateLineDistance(LatLng(aMap!!.myLocation.latitude, aMap!!.myLocation.longitude), XIAN) < 100000) {
            showToast("检测到当前位置不在西安市附近")
            aMap!!.myLocationStyle = MyLocationStyle().myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        }
        setCameraPosition(getCameraPosition())
    }


    open fun caculate() {
        temp = 100000f
        val o: LatLng
        if (aMap!!.myLocation != null) {
            o = LatLng(aMap!!.myLocation.latitude, aMap!!.myLocation.longitude)
            for (i in latLng.indices) {
                val distance = AMapUtils.calculateLineDistance(o, latLng[i])
                if (distance < 100000) {
                    if (distance < temp) {
                        temp = distance
                    }
                    showToast("附近宝藏出没!为" + getUserList().get(i).getTime().toString() + "的" + getUserList().get(i).getOral().toString() + "制" + getUserList().get(i).getName().toString() + "!" + "距离" + distance.toString() + "米")
                }
            }
        } else {
            temp = 0f
        }
    }

    override fun onPoiSearched(poiResult: PoiResult?, i: Int) {}

    override fun onPoiItemSearched(poiItem: PoiItem?, i: Int) {}

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
        aMap?.myLocation?.longitude.let {
            if (it != null) {
                initPoiSearch(aMap?.myLocation!!.latitude, it)
            }
        }
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
        val multiPointOverlay: MultiPointOverlay = aMap.addMultiPointOverlay(overlayOptions) ?:
        multiPointOverlay.setItems(list)
        aMap.setOnMultiPointClickListener({ pointItem -> false })
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


    companion object {
        fun getInstance() = MapFragment()
    }
}