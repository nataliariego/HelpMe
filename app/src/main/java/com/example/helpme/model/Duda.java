package com.example.helpme.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Map;

@IgnoreExtraProperties
public class Duda implements Parcelable {
    public static final String COLLECTION = "DUDA";

    public static final String TITULO = "titulo";
    public static final String DESCRIPCION = "descripcion";
    public static final String REF_ALUMNO = "alumno";
    public static final String FECHA = "fecha";
    public static final String ASIGNATURA_REF = "asignatura";
    public static final String REF_MATERIA = "materia";

    public static final String IS_RESUELTA = "resuelta";

    private String id;
    private String titulo;
    private String descripcion;
    private Map<String, Object> alumnoId;
    private String asignaturaId;
    private Map<String, Object> materiaId;

    private boolean isResuelta;

    private String fecha;

    public Duda() {
    }

    public Duda(String titulo, String descripcion, Map<String, Object> alumnoId, String asignaturaId,
                Map<String, Object> materiaId, boolean isResuelta, String fecha) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.alumnoId = alumnoId;
        this.asignaturaId = asignaturaId;
        this.materiaId = materiaId;
        this.isResuelta = isResuelta;
        this.fecha = fecha;
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

    @Exclude
    public String getId() {
        return id;
    }

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

    public Map<String, Object> getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Map<String, Object> alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(String asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public Map<String, Object> getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(Map<String, Object> materiaId) {
        this.materiaId = materiaId;
    }

    public boolean isResuelta() {
        return isResuelta;
    }

    public void setResuelta(boolean resuelta) {
        isResuelta = resuelta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(titulo);
        parcel.writeString(descripcion);
        parcel.writeString(asignaturaId);
        parcel.writeMap(materiaId);
        parcel.writeMap(alumnoId);
        parcel.writeByte((byte) (isResuelta ? 1 : 0));
    }

    public void setId(String id) {
        this.id = id;
    }
}
