package br.com.sistemas.soscidadao.
        fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

import br.com.sistemas.soscidadao.MapsActivity;
import br.com.sistemas.soscidadao.R;
import br.com.sistemas.soscidadao.models.Denuncia;
import br.com.sistemas.soscidadao.utils.ConstantUtils;
import br.com.sistemas.soscidadao.utils.FirebaseDao;

public class NovaDenunciaFragment extends DialogFragment {
    private  Bundle bundle;
    private double latitude, longitude;
    private Spinner spinnerProblema;
    private String categoria, problema;

    private ProgressBar progressBar;
    private String[] problemas = {"Acidente de trânsito", "Alagamento", "Barulho", "Buraco", "Crime", "Esgoto", "Foco de dengue", "Lixo", "Outros"};
    private Button buttonEnviar;
    private EditText  editTextDescricao;
    private GoogleSignInAccount account;
   private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public  static NovaDenunciaFragment newInstance(double latitude, double longitude){

        NovaDenunciaFragment novaDenunciaFragment = new NovaDenunciaFragment();
        Bundle  bundle = new Bundle();
        bundle.putDouble(ConstantUtils.LATITUDE, latitude);
        bundle.putDouble(ConstantUtils.LONGITUDE, longitude);
        novaDenunciaFragment.setArguments(bundle);
        return novaDenunciaFragment;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_nova_denuncia, container , false);
//            getActivity().getActionBar().setTitle("Denúncia");
            initView(view);
            initSpinnerProblema();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null)
            return;


//        Display display = getActivity().getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int largura = (int) (size.x * 0.8);
//        int altura = (int) (size.y *0.8);
//        getDialog().getWindow().setLayout(largura,altura);
//            getDialog().setTitle("Denúncia");
           getDialog().setTitle("Denúncia");


    }



     private void initSpinnerProblema() {
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, problemas);
        spinnerProblema.setAdapter(arrayAdapter1);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProblema.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                problema = spinnerProblema.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private void initView(View view) {
        progressBar = view.findViewById(R.id.progress);
        spinnerProblema = view.findViewById(R.id.spinnerProblema);
        buttonEnviar = view.findViewById(R.id.buttonEnviar);
        editTextDescricao = view.findViewById(R.id.editDescricao);

        if(bundle!= null){
            latitude = getArguments().getDouble(ConstantUtils.LATITUDE);
            longitude = getArguments().getDouble(ConstantUtils.LONGITUDE);



        }

    buttonEnviar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            progressBar.setVisibility(View.VISIBLE);
            String descricao = editTextDescricao.getText().toString();
            problema = spinnerProblema.getSelectedItem().toString();
            if(descricao == null || descricao.isEmpty()){
                progressBar.setVisibility(View.GONE);
                editTextDescricao.requestFocus();
                editTextDescricao.setError(getResources().getString(R.string.campo_obrigatorio));
            }else{

                if(mAuth != null){
                    Denuncia denuncia = new Denuncia();
                    denuncia.setDescricao(descricao);
                    denuncia.setIdUser(mAuth.getCurrentUser().getUid());
                    denuncia.setProblema(problema);
                    denuncia.setLatitude(latitude);
                    denuncia.setLongitude(longitude);
                    FirebaseDao.novaDenuncia(denuncia);
                    getDialog().dismiss();
                    progressBar.setVisibility(View.GONE);

                }else {
                    Toast.makeText(getActivity(), "Algo deu errado!", Toast.LENGTH_SHORT).show();
                }
            }


        }
    });
    }


    public void setLocalizacao(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    
}
