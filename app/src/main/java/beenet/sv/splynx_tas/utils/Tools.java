package beenet.sv.splynx_tas.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import beenet.sv.splynx_tas.ui.Payment;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Tools {

    private SweetAlertDialog alert;//ALERTAS PERSONALIZADAS

    /**
     * Funcion para generar ceros a la izquierda
     * */
    public String GenerateLeadingZeros (String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);
        return sb.toString();
    }

    /**
     * Funcion para validar si editext esta vacio
     * */
    public  boolean ValidateFields (EditText campo){
        String dato = campo.getText().toString().trim();
        if(TextUtils.isEmpty(dato)){
            campo.setError("Campo Requerido");
            campo.requestFocus();
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Funcion para contar los caracteres menores 16
     * */
    public  boolean ValidateCharacters(EditText campo , int cantidad){
        String dato = campo.getText().toString().trim();
        if(dato.length() < cantidad ){
            campo.setError("Campo de " + cantidad + " digitos");
            campo.requestFocus();
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Funcion para contar los caracteres mayores 16
     * */
    public  boolean ValidateCharacters2(EditText campo , int cantidad){
        String dato = campo.getText().toString().trim();
        if(dato.length() > cantidad ){
            campo.setError("Campo de " + cantidad + " digitos");
            campo.requestFocus();
            return true;
        }
        else{
            return false;
        }
    }

    public void showDialog(int status , Context mContext , String title , String text) {

        switch (status) {
            case 0:
                //Elemento de carga
                alert = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
                alert.getProgressHelper().setBarColor(Color.parseColor("#F57F17"));
                alert.setTitleText("Cargando ...");
                alert.setCancelable(false);
                alert.show();
                break;
            case 1:
                alert.dismiss(); //DETENER LOADER
                break;
            case 2:
                // ELEMENTO DE ERROR
                alert= new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE);
                alert.setTitleText(title);
                alert.setContentText(text);
                alert.setCancelable(false);
                alert.show();
                break;
        }

    }


}
