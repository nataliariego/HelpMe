package auth;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.helpme.model.Alumno;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import controller.AlumnoController;
import dto.AlumnoDto;

/**
 * Autenticación de usuarios mediante Firebase.
 *
 * @author UO257239
 * @version v1.0.0
 */
public class Authentication {

    public static final String TAG = "AUTHENTICATION";
    private static Authentication instance;

    private AlumnoController alumnoController = new AlumnoController();

    /**
     * Crea una cuenta con el email y password indicados.
     *
     * @param alumno
     */
    public void signUp(AlumnoDto alumno) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(alumno.email, alumno.password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                            alumnoController.update(alumno);

                            Log.d(TAG, "createUserWithEmail:success");
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }

    public void signIn(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "login:success");
                        } else {
                            Log.w(TAG, "login:failure", task.getException());
                        }
                    }
                });
    }

    public static Authentication getInstance() {
        if (instance == null) {
            instance = new Authentication();
        }
        return instance;
    }
}
