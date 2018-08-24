package br.com.sistemas.soscidadao.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import br.com.sistemas.soscidadao.R;
import br.com.sistemas.soscidadao.utils.ConstantUtils;

public class NovaDenunciaFragment extends Fragment {
    private  Bundle bundle;
    private double latitude, longitude;
    private Spinner spinnerProblema, spinnerCategoria;
    private String categoria, problema;
    private String[] categorias = {"", "", ""};
    private String[] problemas = {"", "", ""};
    private Button buttonEnviar;
    private EditText  editTextDescricao;

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
        initView(view);
        initSpinnerCategoria();
        initSpinnerProblema();
        return view;
    }

    private void initSpinnerCategoria() {
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categorias);
        spinnerCategoria.setAdapter(arrayAdapter1);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = spinnerCategoria.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    } private void initSpinnerProblema() {
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, problemas);
        spinnerProblema.setAdapter(arrayAdapter1);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProblema.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                problema = spinnerCategoria.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private void initView(View view) {
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria);
        spinnerProblema = view.findViewById(R.id.spinnerProblema);
        buttonEnviar = view.findViewById(R.id.buttonEnviar);

        if(bundle!= null){
            latitude = getArguments().getDouble(ConstantUtils.LATITUDE);
            longitude = getArguments().getDouble(ConstantUtils.LONGITUDE);



        }

    buttonEnviar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isEmpty()){

            }
        }
    });
    }

    private boolean isEmpty() {
        EditText campoComFoco = null;
        Spinner spinner = null;

        boolean empty = true;
        if(categoria== null){
            spinner = spinnerCategoria;
            spinnerCategoria.requestFocus();
            empty = false;

        }if(problema== null){
            spinner = spinnerProblema;
            spinnerProblema.requestFocus();
            empty = false;

        }
        if (editTextDescricao.getText().toString().length() == 0) {
            campoComFoco = editTextDescricao;

            editTextDescricao.requestFocus();
            editTextDescricao.setError(getResources().getString(R.string.campo_obrigatorio));
            empty = false;
        }

        if (campoComFoco != null) {
            campoComFoco.requestFocus();
        }
        if (spinner != null) {
            spinner.requestFocus();
        }
        return empty;
    }
}