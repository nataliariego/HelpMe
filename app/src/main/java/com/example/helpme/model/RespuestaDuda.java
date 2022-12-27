package com.example.helpme.model;

import android.os.Parcel;
import android.os.Parcelable;


public class RespuestaDuda implements Parcelable {

    public static final String COLLECTION = "RESPONDE";

    public static final String ALUMNO_DUDA = "alumnoDuda";
    public static final String ALUMNO_RESPONDE = "alumnoResponde";
    public static final String FECHA = "fecha";
    public static final String ID_DUDA = "idDuda";
    public static final String RESPUESTA = "respuesta";

    private String id;
    private String alumnoDuda;
    private String alumnoResponde;
    private String respuesta;
    private String idDuda;
    private String fecha;


    public RespuestaDuda(){}
    public RespuestaDuda(String emailDuda, String emailResponde, String descripcion, String idDuda, String fecha) {
        this.alumnoDuda = emailDuda;
        this.alumnoResponde = emailResponde;
        this.respuesta = descripcion;
        this.idDuda = idDuda;
        this.fecha = fecha;
    }


    protected RespuestaDuda(Parcel in) {
        id = in.readString();
        alumnoDuda = in.readString();
        alumnoResponde = in.readString();
        respuesta = in.readString();
        idDuda = in.readString();
        fecha = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(alumnoDuda);
        dest.writeString(alumnoResponde);
        dest.writeString(respuesta);
        dest.writeString(idDuda);
        dest.writeString(fecha);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RespuestaDuda> CREATOR = new Creator<RespuestaDuda>() {
        @Override
        public RespuestaDuda createFromParcel(Parcel in) {
            return new RespuestaDuda(in);
        }

        @Override
        public RespuestaDuda[] newArray(int size) {
            return new RespuestaDuda[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailDuda() {
        return alumnoDuda;
    }

    public void setEmailDuda(String emailDuda) {
        this.alumnoDuda = emailDuda;
    }

    public String getEmailResponde() {
        return alumnoResponde;
    }

    public void setEmailResponde(String emailResponde) {
        this.alumnoResponde = emailResponde;
    }

    public String getDescripcion() {
        return respuesta;
    }

    public void setDescripcion(String descripcion) {
        this.respuesta = descripcion;
    }

    public String getIdDuda() {
        return idDuda;
    }

    public void setIdDuda(String idDuda) {
        this.idDuda = idDuda;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
