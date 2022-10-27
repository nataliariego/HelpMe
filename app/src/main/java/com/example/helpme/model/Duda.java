package com.example.helpme.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

public class Duda implements Parcelable {

    private String titulo;
    private String persona_duda;
    private String fecha;
    private String asignatura;
    private boolean resuelta;
    private String url_foto_persona;
    private String descripcion;

    public Duda(String titulo, String persona_duda, String fecha, String asignatura, boolean resuelta, String url_foto_persona,
                String descripcion) {
        this.titulo = titulo;
        this.persona_duda = persona_duda;
        this.fecha = fecha;
        this.asignatura = asignatura;
        this.resuelta = resuelta;
        this.url_foto_persona = url_foto_persona;
        this.descripcion=descripcion;
    }

    @Override
    public String toString() {
        return "Duda{" +
                "titulo='" + titulo + '\'' +
                ", persona_duda='" + persona_duda + '\'' +
                ", fecha='" + fecha + '\'' +
                ", asignatura='" + asignatura + '\'' +
                ", resuelta=" + resuelta +
                ", url_foto_persona='" + url_foto_persona + '\'' +
                '}';
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPersona_duda() {
        return persona_duda;
    }

    public void setPersona_duda(String persona_duda) {
        this.persona_duda = persona_duda;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public boolean isResuelta() {
        return resuelta;
    }

    public void setResuelta(boolean resuelta) {
        this.resuelta = resuelta;
    }

    public String getUrl_foto_persona() {
        return url_foto_persona;
    }

    public void setUrl_foto_persona(String url_foto_persona) {
        this.url_foto_persona = url_foto_persona;
    }

    //----PARCELABLE----

    protected Duda(Parcel in) {
        titulo = in.readString();
        persona_duda = in.readString();
        fecha = in.readString();
        asignatura = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resuelta = in.readBoolean();
        }
        url_foto_persona = in.readString();
    }

    public static final Parcelable.Creator<Duda> CREATOR = new Parcelable.Creator<Duda>() {
        @Override
        public Duda createFromParcel(Parcel in) {
            return new Duda(in);
        }

        @Override
        public Duda[] newArray(int size) {
            return new Duda[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(titulo);
        parcel.writeString(persona_duda);
        parcel.writeString(fecha);
        parcel.writeString(asignatura);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(resuelta);
        }
    }
}
