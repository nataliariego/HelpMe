package com.example.helpme;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.helpme.model.Alumno;
import com.example.helpme.model.Duda;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import adapter.AlumnoAdapter;
import adapter.DudaAdapter;
import dto.AlumnoDto;
import dto.DudaDto;
import viewmodel.AlumnoViewModel;
import viewmodel.DudaViewModel;

public class FriendsActivity extends AppCompatActivity {


    public static final String TAG = "FRIENDS_ACTIVITY";
    private static final String ALUMNO_SELECCIONADO ="ALUMNO_SELECCIONADO" ;

    private RecyclerView listadoAmigos;

    //Base de datos
    private AlumnoAdapter alumnoAdapter;
    private AlumnoViewModel alumnoViewModel = new AlumnoViewModel();

    private List<AlumnoDto> amigos = new ArrayList<>();

    private BottomNavigationView navegacion;

    //Hardcodeado
    private List<Alumno> listaAlumnos = new ArrayList<Alumno>();

    private FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        listadoAmigos = (RecyclerView) findViewById(R.id.reciclerViewAmigos);


        listadoAmigos.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listadoAmigos.setLayoutManager(layoutManager);

        cargarAmigos();


        //Navegación
        navegacion = findViewById(R.id.bottomNavigationView);
        navegacion.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        redirectPantallaHome();
                        return true;
                    case R.id.nav_cuenta:
                        redirectPantallaCuenta();
                        return true;
                    case R.id.nav_dudas:
                        redirectPantallaDudas();
                        return true;
                }
                return false;
            }
        });


    }

    //click del item del adapter
    private void clikonIntem(Alumno alumno) {
        Log.i("Click adapter", "Item Clicked " + alumno.getNombre());


        //Le paso la duda al MainActivity para que la muestre al picnchar en la duda
        Intent intent = new Intent(FriendsActivity.this, ActivityShowDuda.class);
        intent.putExtra(ALUMNO_SELECCIONADO, alumno);

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    /*
    private void cargarAmigos() {
       Alumno a1 = new Alumno("1","Jose Rodriguez","uo276922","https://res.cloudinary.com/dyoejqhka/image/upload/v1666868641/natalia_im9atl.png",new ArrayList<>());
        Alumno a2 = new Alumno("2","Natalia Fernandez","uo277516","https://res.cloudinary.com/dyoejqhka/image/upload/v1666868641/natalia_im9atl.png",new ArrayList<>());

        listaAlumnos.add(a1);
        listaAlumnos.add(a2);
    }

     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        cargarAmigos();
        alumnoAdapter = new AlumnoAdapter(amigos);
        listadoAmigos.setAdapter(alumnoAdapter);
        alumnoAdapter.notifyDataSetChanged();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cargarAmigos() {
        amigos.clear();
        String email = userInSession.getEmail();
        alumnoViewModel.getAllAlumnosFriendsActivity().observe(this, alumnosResult -> {
            if (alumnosResult != null) {
                alumnosResult.forEach(d -> {
                    if (!d.getEmail().equalsIgnoreCase(email)) {
                        Log.i(TAG, d.getNombre() + " " + d.getUo() + " " + d.getUrl_foto());
                        AlumnoDto newDuda = new AlumnoDto();
                        newDuda.nombre = d.getNombre();
                        newDuda.uo = d.getUo();
                        newDuda.urlFoto = d.getUrl_foto();
                        newDuda.asignaturasDominadas = d.getAsignaturasDominadas();
                        amigos.add(newDuda);
                    }
                });
            }
            alumnoAdapter = new AlumnoAdapter(amigos);
            listadoAmigos.setAdapter(alumnoAdapter);
            alumnoAdapter.notifyDataSetChanged();
        });
    }

    private void redirectPantallaHome() {
        Intent listadoDudasIntent = new Intent(FriendsActivity.this, HomeActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }

    private void redirectPantallaCuenta() {
        Intent listadoDudasIntent = new Intent(FriendsActivity.this, ProfileActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }

    private void redirectPantallaDudas() {
        Intent listadoDudasIntent = new Intent(FriendsActivity.this, ListarDudasActivity.class);
        // Para transiciones
        startActivity(listadoDudasIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        //startActivity(listadoDudasIntent);
    }
}