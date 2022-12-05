package com.heeyjinny.mapsmylocation

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.heeyjinny.mapsmylocation.databinding.ActivityMapsBinding

//1
//Goole Maps API 키 발급 및 build.gradle에 적용
//참고: https://heeyjinny.tistory.com/109

//2
//스마트폰의 위치 기능에 접근을 위한 권한 선언
//AndroidManifest.xml 권한 2가지 명세

//3
//OnMapReadyCallback 인터페이스를 상속받는 MapsActivity
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //4
    //권한 처리를 위한 런처 선언
    //한 번에 2개의 권한에 대한 승인을 요청하기 때문에
    //런처의 제네릭은 문자열 배열 <Array<Srting>> 사용
    lateinit var locationPermission: ActivityResultLauncher<Array<String>>

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //4-2
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
        //4-3
        //런처를 실행해 권한 승인 요청
        //한 번에 2개의 권한에 대한 승인을 요청하기 때문에
        //arrayOf()를 사용해 권한 2개를 같이 launch()의 파라미터로 입력
        locationPermission.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        )

    }//onCreate

    //4-1
    //권한요청 승인 후 실행할 메서드 생성
    fun startProcess(){

        //4-5
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

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }//onMapReady

}//MapsActivity