package com.example.helpme.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Alumno implements Parcelable {
    private String id;
    private String nombre;
    private String uo;
    private String url_foto;

    private List<Asignatura> asignaturasDominadas = new ArrayList<>();

    public Alumno(String id, String nombre, String uo, String url_foto, List<Asignatura> asignaturasDominadas) {
        this.id = id;
        this.nombre = nombre;
        this.uo = uo;
        this.url_foto = url_foto;
        this.asignaturasDominadas = asignaturasDominadas;
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
}
