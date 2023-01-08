package auth;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.helpme.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import controller.AlumnoController;
import controller.callback.GenericCallback;
import dto.AlumnoDto;

/**
 * Autenticación de usuarios mediante Firebase.
 *
 * @author UO257239
 * @version v1.0.1
 */
public class Authentication {

    public static final String TAG = "AUTHENTICATION";
    private static Authentication instance;

    FirebaseUser userInSession = FirebaseAuth.getInstance().getCurrentUser();

    private final AlumnoController alumnoController = new AlumnoController();

    /**
     * Crea una cuenta con el email y password indicados.
     *
     */
    public void signUp(AlumnoDto alumno, GenericCallback callback) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(alumno.email, alumno.password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userInSession = FirebaseAuth.getInstance().getCurrentUser();
                        assert userInSession != null;
                        alumnoController.update(alumno, userInSession.getUid());

                        callback.callback(GenericCallback.SUCCESS_CODE);

                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                })
                .addOnFailureListener(e -> Log.i(TAG, "Error al crear la cuenta de usuario."))
        ;
    }

    /**
     * Inicio de sesión de la aplicación mediante correo electrónico y contraseña.
     * Inicio de Firebase básico.
     *
     * @param email    Correo electrónico del usuario.
     * @param password Contraseña
     */
    public void signIn(final String email, final String password,
                       LoginActivity.LoginCallback callback) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                        Log.d(TAG, "login:success");
                    } else {
                        Log.w(TAG, "login:failure. ", task.getException());
                    }
                })
                .addOnFailureListener(e -> callback.onFailure());
    }

    /**
     * Cierra la sesión actual.
     */
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Envía un correo para restablecer la contraseña de acceso a la aplicación.
     *
     * @param email Email registrado en la aplicación para acceder.
     */
    public void sendResetPasswordEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("es");

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email sent.");
                    }
                });
    }

    public void sendEmailVerification() {
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email sent.");
                        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                        current.reload();
                    }
                });
    }

    /**
     * Comprueba si hay un usuario logeado.
     *
     */
    public boolean isSigned() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static Authentication getInstance() {
        if (instance == null) {
            instance = new Authentication();
        }
        return instance;
    }
}
