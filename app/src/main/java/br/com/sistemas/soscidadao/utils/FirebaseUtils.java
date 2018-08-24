package br.com.sistemas.soscidadao.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class FirebaseUtils {

    private static DatabaseReference referenceFirebase;




    public static DatabaseReference getFirebase(){

        if(referenceFirebase == null){
            referenceFirebase = FirebaseDatabase.getInstance().getReference();

        }
        return  referenceFirebase;


    }


    public static Query getDenuncias(){
        return FirebaseDatabase.getInstance().getReference(ConstantUtils.DENUNCIAS);
    }

}
