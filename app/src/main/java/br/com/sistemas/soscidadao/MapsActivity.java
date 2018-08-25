package br.com.sistemas.soscidadao;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.sistemas.soscidadao.fragment.LoginFragment;
import br.com.sistemas.soscidadao.fragment.NovaDenunciaFragment;
import br.com.sistemas.soscidadao.models.Denuncia;
import br.com.sistemas.soscidadao.utils.FirebaseUtils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private Query queryDenuncias;
    private List<Denuncia> denuncias = new ArrayList<>();
    private GoogleMap mMap;
    private FirebaseAuth firebaseAuth;
    private double latitude, longitude;

    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        getCurrentLatLong();
        carregarDenuncias();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            new LoginFragment().show(getSupportFragmentManager(), "");
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NovaDenunciaFragment denunciaFragment = new NovaDenunciaFragment();
                denunciaFragment.setLocalizacao(latitude, longitude);
                denunciaFragment.show(getSupportFragmentManager(),"");

            }
        });
    }

    private void getCurrentLatLong() {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
         latitude = location.getLatitude();
         longitude = location.getLongitude();
    }

    private void carregarDenuncias() {
        queryDenuncias = FirebaseUtils.getDenuncias();
        queryDenuncias.keepSynced(true);
        queryDenuncias.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    denuncias.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Denuncia denuncia = snapshot.getValue(Denuncia.class);
                        denuncia.setId(snapshot.getKey());
                        denuncias.add(denuncia);
                        setarDenuncias();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setarDenuncias() {

        for (Denuncia denuncia: denuncias) {
                LatLng latLng = new LatLng(denuncia.getLatitude(), denuncia.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(denuncia.getProblema()));
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-21.1899737, -41.9085663);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f));

        LatLng sydney1 = new LatLng(-21.1903299, -41.9117128);
        mMap.addMarker(new MarkerOptions().position(sydney1).title("Marker in Sydney"));

        LatLng sydney2 = new LatLng(-21.1953173, -41.9093979);
        mMap.addMarker(new MarkerOptions().position(sydney2).title("Marker in Sydney"));
    }
}
