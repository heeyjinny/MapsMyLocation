package com.heeyjinny.mapsmylocation

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.heeyjinny.mapsmylocation.databinding.ActivityMapsBinding

//1
//Goole Maps API 키 발급 및 build.gradle에 적용
//참고: https://heeyjinny.tistory.com/109

//2
//GooglePlayService의존성 추가
//FusedLocationProviderClient API사용을 위해
//구글플레이 서비스의 Location라이브러리 의존성 추가
//build.gradle 작성

//3
//스마트폰의 위치 기능에 접근을 위한 권한 선언
//AndroidManifest.xml 권한 2가지 명세

//4
//OnMapReadyCallback 인터페이스를 상속받는 MapsActivity
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //5
    //권한 처리를 위한 런처 선언하고 실행하여 지도 나타내기
    //5-1
    //권한 처리를 위한 런처 변수 선언
    //한 번에 2개의 권한에 대한 승인을 요청하기 때문에
    //런처의 제네릭은 문자열 배열 <Array<Srting>> 사용
    lateinit var locationPermission: ActivityResultLauncher<Array<String>>

    //6
    //현재 위치를 검색하기 위해 FusedLocationProviderClient 생성 및 사용
    //6-1
    //위치 처리를 위한 변수2개 선언
    //FusedLocationProviderClient: 위치값 사용
    //LocationCallback: 위치값 요청에 대한 갱신 정보를 받음
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //5-3
        //런처생성코드 작성하여 미리 선언해둔 위치권한 변수 locationPermission에 저장
        //한 번에 2개의 권한에 대한 승인을 요청하기 때문에
        //Contract로 RequestMultiplePermission()사용
        locationPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            if (it.all { it.value }){
                startProcess()
            }else{
                Toast.makeText(this, "권한 승인이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
        //5-4
        //런처를 실행해 권한 승인 요청
        //한 번에 2개의 권한에 대한 승인을 요청하기 때문에
        //arrayOf()를 사용해 권한 2개를 같이 launch()의 파라미터로 입력
        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        )

    }//onCreate

    //5-2
    //권한요청 승인 후 실행할 메서드 생성
    fun startProcess(){

        //5-5
        //위치권한 승인 시 구글 지도를 준비하는 작업 진행
        //자동 생성된 코드 잘라내 붙여넣기...
        //권한이 모두 승인되고 맵이 준비되면 onMapReady() 메서드가 호출됨
        //SupportMapFragment의 findFragmentById()메서드로
        //id가 map인 SupportMapFragment를 찾은 것을 저장한 변수 생성하고
        //변수는 getMapAsync()를 호출해 안드로이드에 구글 지도를 그려달라는 요청을 함
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // =SupportMapFragment를 가져와 map의 사용 준비가 완료되면 알려줌
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }//startProcess

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
        mMap = googleMap

        //6-2
        //검색 클라이언트를 생성하는 코드 추가
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //6-3
        //위치 업데이트를 하는 메서드 호출
        updateLocation()

    }//onMapReady

    //7
    //위치 업데이트 메서드
    //7-5
    //FusedLocationClient.requestLocationUpdate()코드는 권한처리가 필요한데 현재 코드에서 확인 불가
    //그래서 메서드 상단에 해당 코드를 체크하지 않아도 된다는 의미로 @SuppressLint("MissingPermission")애너테이션 달아줌
    @SuppressLint("MissingPermission")
    fun updateLocation() {

        //7-1 *****(현재 LocationRequest.create는 삭제 되었으며... 다른 코드를 사용해야 함...)
        //locationRequest 생성: 위치 정보를 요청할 정확도와 주기 설정
        //정확도는 PRIORITY_HIGH_ACCURACY, 주기는 1초(1000밀리초)
        //이제 1초(1000밀리초)에 한 번씩 변화된 위치 정보가
        //LocationCallBack의 onLocationResult()로 전달됨
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000
        }

        //7-2
        //해당 주기마다 받환 받는 locationCallback 생성
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0?.let {
                    for(location in it.locations) {
                        Log.d("Location", "${location.latitude} , ${location.longitude}")

                        //7-3
                        //onLocationResult()는 반환받은 정보에서 위치 정보를 setLastLocation()으로 전달
                        setLastLocation(location)
                    }
                }
                super.onLocationResult(p0)
            }
        }//locationCallback

        //7-4
        //onMapReady에서 생성한 위치 검색 클라이언트의 requestLocationUpdate()에
        //앞에 생성한 2개와 함께 루퍼정보 넘김
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,
            Looper.myLooper())

    }//updateLocation

    //8
    //위치 정보를 받아 마커를 그리고 화면을 이동하는 메서드 setLastLocation() 작성
    fun setLastLocation(lastLocation: Location) {

        //8-1
        //전달받은 위치 정보로 좌표 생성
        val LATLNG = LatLng(lastLocation.latitude, lastLocation.longitude)

        //8-2
        //생성된 현재위치 좌표로 마커 생성
        val markerOptions = MarkerOptions()
            .position(LATLNG)
            .title("Here!")

        //8-3
        //카메라 위치를 현재 위치로 세팅
        val cameraPosition = CameraPosition.Builder()
            .target(LATLNG)
            .zoom(15.0f)
            .build()

        //8-4
        //마커를 지도에 반영하기 전에 이전에 그려진 마커가 있으면 삭제
        mMap.clear()
        //8-5
        //마커와 함께 카메라 위치를 지도에 반영
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

    }//setLastLocation

}//MapsActivity