package com.example.helpme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Asignatura;
import com.example.helpme.model.Curso;
import com.example.helpme.model.Duda;
import com.example.helpme.model.Materia;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import controller.AlumnoController;
import controller.AsignaturaController;
import controller.DudaController;

public class ListarDudasActivity extends AppCompatActivity {

    private static final String DUDA_SELECCIONADA = "duda_seleccionada";

    //Modelo de datos
    private List<Duda> listaDuda = new ArrayList<Duda>();;
    private Duda duda;
    private RecyclerView listaDudaView;
    private Object[] asignatura_data;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private AsignaturaController asignaturaController = new AsignaturaController();
    private AlumnoController alumnoController = new AlumnoController();
    private DudaController dudaController = new DudaController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_dudas);

        //Rellenar lista de dudas y en el adapter
        cargarDudas();

        // Recuperamos referencia y configuramos recyclerView con la lista de dudas
        listaDudaView = (RecyclerView)findViewById(R.id.reciclerView);
        listaDudaView.setHasFixedSize(true);

        /* Un RecyclerView necesita un Layout Manager para manejar el posicionamiento de los
        elementos en cada línea. Se podría definir un LayoutManager propio extendendiendo la clase
        RecyclerView.LayoutManager. Sin embargo, en la mayoría de los casos, simplemente se usa
        una de las subclases LayoutManager predefinidas: LinearLayoutManager, GridLayoutManager,
        StaggeredGridLayoutManager*/
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listaDudaView.setLayoutManager(layoutManager);

        //Pasamos la lista de dudas al RecyclerView con el ListaDudaAdapter
        // Instanciamos el adapter con los datos de la petición y lo asignamos a RecyclerView
        // Generar el adaptador, le pasamos la lista de dudas
        // y el manejador para el evento click sobre un elemento

        ListaDudasAdapter lpAdapter= new ListaDudasAdapter(listaDuda,
                new ListaDudasAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Duda duda) {
                        clikonIntem(duda);
                    }
                });
        listaDudaView.setAdapter(lpAdapter);



    }

    //click del item del adapter
    private void clikonIntem(Duda duda) {
        Log.i("Click adapter","Item Clicked "+duda.getTitulo());


        //Le paso la duda al MainActivity para que la muestre al picnchar en la duda
        Intent intent=new Intent (ListarDudasActivity.this, ActivityShowDuda.class);
        intent.putExtra(DUDA_SELECCIONADA, duda);

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void cargarDudas() {
        Duda d = new Duda("Algoritmo A*",
                "Estoy intentando hacer experimentos para el algoritmo A* y me da este error:",
                new Alumno("id", "Natalia Fernández Riego", "UO277516", "https://cdn.pixabay.com/photo/2015/10/29/08/23/girl-1011915_960_720.jpg", new ArrayList<Asignatura>()),
                    new Asignatura("id", "Sistemas Inteligentes", new Curso("id", "Primero"), new Materia("id", "Inteligencia Artificial", "IA")),
                new Materia("id", "Inteligencia Artificial", "IA"), true, "30/10/2022 12:35:24");
        listaDuda.add(d);
        Duda d2 = new Duda("Funciones Lambda",
                "Estoy intentando hacer esta expresion",
                new Alumno("id", "Juan Iglesias Pérez", "UO727027", "https://img.freepik.com/foto-gratis/retrato-hombre-caucasico-alegre_53876-13440.jpg?w=2000", new ArrayList<Asignatura>()),
                new Asignatura("id", "Tecnologias y Paradigmas de la Programación", new Curso("id", "Segundo"), new Materia("id", "TPP", "TPP")),
                new Materia("id", "TPP", "TPP"), false, "30/10/2022 12:35:24");
        listaDuda.add(d2);

        Duda d3 = new Duda("Conexión entre Activities",
                "Estoy intentando hacer esta conexion",
                new Alumno("id", "Marta Ramos Álvarez", "UO829920", "https://img.freepik.com/foto-gratis/retrato-mujer-caucasica-sonriendo_53876-24998.jpg?w=2000", new ArrayList<Asignatura>()),
                new Asignatura("id", "Software de Dispositivos Móviles", new Curso("id", "Tercero"), new Materia("id", "SDM", "SDM")),
                new Materia("id", "SDM", "SDM"), false, "30/10/2022 12:35:24");
        listaDuda.add(d3);
        Duda d4 = new Duda("Paso a casos de equivalencia",
                "Estoy intentando hacer este paso",
                new Alumno("id", "Manuel Carillo Gómez", "UO762878", "https://img.freepik.com/fotos-premium/retrato-hombre-maduro-chico-adulto-tiene-pelo-canoso-hombre-guapo-barba-canosa-moda-cabello-masculino-barberia-cara-chico-canoso-afeitar-cuidado-cabello-piel-cuidado-piel-masculino-belleza-hombres_545934-56.jpg?w=2000", new ArrayList<Asignatura>()),
                new Asignatura("id", "Calidad y Validación del Software", new Curso("id", "Cuarto"), new Materia("id", "CVS", "CVS")),
                new Materia("id", "CVS", "CVS"), true, "30/10/2022 12:35:24");
        listaDuda.add(d4);
        Duda d5 = new Duda("Hacer un Grid Layout",
                "Estoy intentando hacer este layout",
                new Alumno("id", "Estela García Antuña", "UO273928", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT6aGDVV8G4834Zj3D4WaKjO3Aypf58H3te8jC0-uoFltjW2V5AD3NMebJi6L0-i7sZZY8&usqp=CAU", new ArrayList<Asignatura>()),
                new Asignatura("id", "Comunicacion Persona-Maquina", new Curso("id", "Segundo"), new Materia("id", "CPM", "CPM")),
                new Materia("id", "CPM", "CPM"), false, "30/10/2022 12:35:24");
        listaDuda.add(d5);
    }

    private boolean getBoolean(String toString) {
        if (toString.equals("true")) return true;
        return false;
    }




}