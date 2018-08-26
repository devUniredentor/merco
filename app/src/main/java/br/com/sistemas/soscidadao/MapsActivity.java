package br.com.sistemas.soscidadao;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.sistemas.soscidadao.fragment.DetalhesProblemasFragment;
import br.com.sistemas.soscidadao.fragment.NovaDenunciaFragment;
import br.com.sistemas.soscidadao.models.Denuncia;
import br.com.sistemas.soscidadao.utils.ConstantUtils;
import br.com.sistemas.soscidadao.utils.FirebaseUtils;
import br.com.sistemas.soscidadao.utils.PermissionUtils;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    private String provider;

    private Query queryDenuncias;
    private List<Denuncia> denuncias;
    private GoogleMap mMap;

    private   GoogleSignInAccount account;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        provider = getIntent().getStringExtra(ConstantUtils.PROVIDER);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkMyPermissionLocation();
        } else {
            initGoogleMapLocation();
        }

        carregarDenuncias();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);






        account = GoogleSignIn.getLastSignedInAccount(this);



    }

    private void checkMyPermissionLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission Check
            PermissionUtils.requestPermission(this);
        } else {
            //If you're authorized, start setting your location
            initGoogleMapLocation();
        }
    }

    private void initGoogleMapLocation() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        /**
         * Location Setting API to
         */
        SettingsClient mSettingsClient = LocationServices.getSettingsClient(this);
        /*
         * Callback returning location result
         */
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                super.onLocationResult(result);
                //mCurrentLocation = locationResult.getLastLocation();
                mCurrentLocation = result.getLocations().get(0);


                if(mCurrentLocation!=null)
                {
                    Log.e("Location(Lat)==",""+mCurrentLocation.getLatitude());
                    Log.e("Location(Long)==",""+mCurrentLocation.getLongitude());
                }


                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                options.icon(icon);
                try{
                    options.title(account.getDisplayName());
                }catch (Exception e){
                    options.title("Eu");
                }
                Marker marker = mMap.addMarker(options);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
                /**
                 * To get location information consistently
                 * mLocationRequest.setNumUpdates(1) Commented out
                 * Uncomment the code below
                 */
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }

            //Locatio nMeaning that all relevant information is available
            @Override
            public void onLocationAvailability(LocationAvailability availability) {
                //boolean isLocation = availability.isLocationAvailable();
            }
        };





        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        //To get location information only once here
        mLocationRequest.setNumUpdates(3);
        if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
            //Accuracy is a top priority regardless of battery consumption
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }else{
            //Acquired location information based on balance of battery and accuracy (somewhat higher accuracy)
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        /**
         * Stores the type of location service the client wants to use. Also used for positioning.
         */
        LocationSettingsRequest mLocationSettingsRequest = builder.build();

        Task<LocationSettingsResponse> locationResponse = mSettingsClient.checkLocationSettings(mLocationSettingsRequest);
        locationResponse.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.e("Response", "Successful acquisition of location information!!");
                //
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
        });


        //When the location information is not set and acquired, callback
        locationResponse.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e("onFailure", "Location environment check");
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Check location setting";
                        Log.e("onFailure", errorMessage);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //If the request code does not match
        if (requestCode != PermissionUtils.REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION}, grantResults)) {
            //If you have permission, go to the code to get the location value
            initGoogleMapLocation();
        } else {
            Toast.makeText(this, "Stop apps without permission to use location information", Toast.LENGTH_SHORT).show();
            //finish();
        }
    }

    /**
     * Remove location information
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }


    private void carregarDenuncias() {
        denuncias = new ArrayList<>();
        queryDenuncias = FirebaseUtils.getDenuncias();
        queryDenuncias.keepSynced(true);
        queryDenuncias.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    denuncias.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Denuncia denuncia = snapshot.getValue(Denuncia.class);
                        denuncia.setId(snapshot.getKey());

                        denuncias.add(denuncia);

                    }
                    setarDenuncias(denuncias);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setarDenuncias(List<Denuncia> denuncias) {

        for (Denuncia denuncia: denuncias) {
//                Log.v("DENUNCIAS", denuncia.getDateCriacao()+"");
//                LatLng latLng = new LatLng(denuncia.getLatitude(), denuncia.getLongitude());
//                mMap.addMarker(new MarkerOptions().position(latLng).title(denuncia.getProblema()).icon();
                setarNoMapa(denuncia);
        }

    }

    private void setarNoMapa(final Denuncia denuncia) {

        try{


            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(denuncia.getLatitude(), denuncia.getLongitude());
            markerOptions.position(latLng).title(denuncia.getProblema());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));


            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(denuncia);

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Denuncia denuncia = (Denuncia) marker.getTag();
                    if(denuncia != null) {
                        DetalhesProblemasFragment detalhesProblemasFragment = new DetalhesProblemasFragment();
                        detalhesProblemasFragment.setDenuncia(denuncia);
                        detalhesProblemasFragment.setLocation(denuncia.getLatitude(), denuncia.getLongitude());
                        detalhesProblemasFragment.show(getSupportFragmentManager(), "detalhesFragment");
                    }
                }
            });
        }catch (Exception e ){
            e.printStackTrace();
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(account!=null){
                    chamaNovaDenuncia(latLng);
                }else {
                 signIn();
                }

            }
        });
    }

    public void chamaNovaDenuncia(LatLng latLng) {
        NovaDenunciaFragment denunciaFragment = new NovaDenunciaFragment();
        denunciaFragment.setLocalizacao(latLng.latitude,latLng.longitude);
        denunciaFragment.show(getSupportFragmentManager(),"");


    }


    //signIn Google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            this.account =account;
            updateUI(account);
        } catch (ApiException e) {

            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            this.account =account;

        } else {
            this.account = null;
        }
    }
    @Override
    public void onStart() {
        super.onStart();


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        updateUI(null);

                    }
                });
    }
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        updateUI(null);

                    }
                });
    }
}
