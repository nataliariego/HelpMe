package com.example.helpme.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Alumno implements Parcelable {
    //    Nombre de los campos de la base de datos
    public static final String COLLECTION = "ALUMNO";
    public static final String NOMBRE = "nombre";
    public static final String UO = "uo";
    public static final String URL_FOTO = "url_foto";
    public static final String EMAIL = "email";
    public static final String USER_ID = "user_id";


    private String id;
    private String nombre;
    private String uo;
    private String url_foto;
    private String email;
    private String userId; // For Firebase Auth purposes

    private List<Asignatura> asignaturasDominadas = new ArrayList<>();

    public Alumno(){}

    public Alumno(String id, String nombre, String uo, String url_foto, List<Asignatura> asignaturasDominadas) {
        this.id = id;
        this.nombre = nombre;
        this.uo = uo;
        this.url_foto = url_foto;
        this.asignaturasDominadas = asignaturasDominadas;
    }

    public Alumno(String nombre, String uo, String url_foto, String email, String userId, List<Asignatura> asignaturasDominadas) {
        this.nombre = nombre;
        this.uo = uo;
        this.url_foto = url_foto;
        this.email = email;
        this.userId = userId;
        this.asignaturasDominadas = asignaturasDominadas;
    }

    public Alumno(String id, String nombre, String uo) {
        this(id, nombre, uo, null, null);
    }
    public Alumno(String id, String nombre, String uo, String url_foto) {
        this(id, nombre, uo, url_foto, null);
    }

    public Alumno(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        uo = in.readString();
    }

    public static final Creator<Alumno> CREATOR = new Creator<Alumno>() {
        @Override
        public Alumno createFromParcel(Parcel in) {
            return new Alumno(in);
        }

        @Override
        public Alumno[] newArray(int size) {
            return new Alumno[size];
        }
    };


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUo() {
        return uo;
    }

    public void setUo(String uo) {
        this.uo = uo;
    }

    public List<Asignatura> getAsignaturasDominadas() {
        return asignaturasDominadas;
    }

    public void setAsignaturasDominadas(List<Asignatura> asignaturasDominadas) {
        this.asignaturasDominadas = asignaturasDominadas;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(nombre);
        parcel.writeString(uo);
    }

    public String getUrl_foto() {
        return url_foto;
    }

    public void setUrl_foto(String url_foto) {
        this.url_foto = url_foto;
    }

    @Override
    public String toString() {
        return "Alumno{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", uo='" + uo + '\'' +
                ", url_foto='" + url_foto + '\'' +
                ", email='" + email + '\'' +
                ", userId='" + userId + '\'' +
                ", asignaturasDominadas=" + asignaturasDominadas +
                '}';
    }
}
