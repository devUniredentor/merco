package br.com.sistemas.soscidadao.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.sistemas.soscidadao.models.Denuncia;

public class FirebaseDao {


public static void novaDenuncia(Denuncia denuncia){
    DatabaseReference reference = FirebaseUtils.getFirebase();
    reference.child(ConstantUtils.DENUNCIAS).push().setValue(denuncia);

}



}
