package com.example.helpme.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Duda implements Parcelable {
    private String titulo;
    private String descripcion;
    private Alumno alumno;
    private Asignatura asignatura;
    private Materia materia;
    private boolean isResuelta;

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    private String fecha;



    public Duda(String titulo, String descripcion, Alumno alumno, Asignatura asignatura, Materia materia, boolean isResuelta, String createdAt) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.alumno = alumno;
        this.asignatura = asignatura;
        this.materia = materia;
        this.isResuelta = isResuelta;
        this.fecha = createdAt;
    }

    protected Duda(Parcel in) {
        titulo = in.readString();
        descripcion = in.readString();
        isResuelta = in.readByte() != 0;
    }

    public static final Creator<Duda> CREATOR = new Creator<Duda>() {
        @Override
        public Duda createFromParcel(Parcel in) {
            return new Duda(in);
        }

        @Override
        public Duda[] newArray(int size) {
            return new Duda[size];
        }
    };



    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public boolean isResuelta() {
        return isResuelta;
    }

    public void setResuelta(boolean resuelta) {
        isResuelta = resuelta;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(titulo);
        parcel.writeString(descripcion);
        parcel.writeByte((byte) (isResuelta ? 1 : 0));
    }
}
