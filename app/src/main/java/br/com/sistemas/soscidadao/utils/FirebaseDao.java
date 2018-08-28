package br.com.sistemas.soscidadao.utils;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.sistemas.soscidadao.R;
import br.com.sistemas.soscidadao.models.Denuncia;

public class FirebaseDao {
    static String problema;
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
public static void novaDenuncia(Denuncia denuncia){
    DatabaseReference reference = FirebaseUtils.getFirebase();
    reference.child(ConstantUtils.DENUNCIAS).push().setValue(denuncia);

}

public static void dialogNovaDenuncia(final FragmentActivity fragment, final LatLng latLng, ArrayAdapter<String> adapter){
    final AlertDialog dialogBuilder = new AlertDialog.Builder(fragment).setPositiveButton("Enviar", null).create();
    LayoutInflater inflater = fragment.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_nova_denuncia, null);
    dialogBuilder.setTitle("Denúncia");
    final EditText editText = dialogView.findViewById(R.id.edt_comment);
   //spinner conf
    final Spinner spinner = dialogView.findViewById(R.id.spinner);
    assert spinner != null;
    spinner.setAdapter(adapter);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//


    dialogBuilder.setOnShowListener(new DialogInterface.OnShowListener() {
        @Override
        public void onShow(DialogInterface dialogInterface) {
            Button buttonPositive = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);
            buttonPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String descricao = editText.getText().toString();
                    problema = spinner.getSelectedItem().toString();
                    if(descricao == null || descricao.isEmpty()){
//                        progressBar.setVisibility(View.GONE);
                        editText.requestFocus();
                        editText.setError(fragment.getResources().getString(R.string.campo_obrigatorio));
                    }else{

                        if(mAuth != null){

                            Denuncia denuncia = new Denuncia();
                            denuncia.setDescricao(descricao);
                            denuncia.setIdUser(mAuth.getCurrentUser().getUid());
                            denuncia.setProblema(problema);
                            denuncia.setLatitude(latLng.latitude);
                            denuncia.setLongitude(latLng.longitude);
                            denuncia.setImagem(mAuth.getCurrentUser().getPhotoUrl().toString());
                            novaDenuncia(denuncia);
                            dialogBuilder.dismiss();
//                            progressBar.setVisibility(View.GONE);

                        }else {
                            Toast.makeText(fragment, "Algo deu errado!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
        }
    });





    dialogBuilder.setView(dialogView);
    dialogBuilder.show();


}
public static void dialogDetalhesDenuncia(FragmentActivity fragment, final Denuncia  denuncia){
    final AlertDialog dialogBuilder = new AlertDialog.Builder(fragment).create();
    LayoutInflater inflater = fragment.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.dialog_detalhes_denuncia, null);
    dialogBuilder.setTitle("Detalhes");
    final TextView textViewProblema = dialogView.findViewById(R.id.textProblema);
    final TextView textViewDescricao = dialogView.findViewById(R.id.textDescricao);
    final Switch aSwitch = dialogView.findViewById(R.id.switchSolucionado);
    ImageView imageViewFoto = dialogView.findViewById(R.id.image_foto);
    TextView textViewNome = dialogView.findViewById(R.id.text_nome);

    if(denuncia.getIdUser().equals(mAuth.getCurrentUser().getUid())){
        aSwitch.setVisibility(View.VISIBLE);
        aSwitch.setChecked(denuncia.isResolvido());
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               denuncia.setResolvido(b);
               denuncia.setDataFinalizacao(Calendar.getInstance().getTimeInMillis());
               atualiza(denuncia);


            }
        });
    }

    textViewDescricao.setText(denuncia.getDescricao());
    textViewProblema.setText(denuncia.getProblema());
    Picasso.get().load(denuncia.getImagem()).resize(100, 100).into(imageViewFoto);
    textViewNome.setText(mAuth.getCurrentUser().getDisplayName());

    dialogBuilder.setView(dialogView);
    dialogBuilder.show();


}

    private static  void atualiza(Denuncia denuncia) {
        DatabaseReference reference = FirebaseUtils.getFirebase();
        reference.child(ConstantUtils.DENUNCIAS).child(denuncia.getId()).setValue(denuncia);
    }


}
