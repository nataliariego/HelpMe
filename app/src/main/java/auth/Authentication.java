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

    private AlumnoController alumnoController = new AlumnoController();

    /**
     * Crea una cuenta con el email y password indicados.
     *
     * @param alumno
     */
    public void signUp(AlumnoDto alumno, GenericCallback callback) {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(alumno.email, alumno.password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            userInSession = FirebaseAuth.getInstance().getCurrentUser();

                            alumnoController.update(alumno, userInSession.getUid());
                            Log.d(TAG, "createUserWithEmail:success");

                            callback.callback(GenericCallback.SUCCESS_CODE);

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Error al crear la cuenta de usuario.");
                    }
                })
        ;
    }

    /**
     * Inicio de sesión de la aplicación mediante correo electrónico y contraseña.
     * Inicio de Firebase básico.
     *
     * @param email    Correo electrónico del usuario.
     * @param password Contraseña
     */
    public void signIn(final String email, final String password, LoginActivity.LoginCallback callback) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            callback.callback();
                            Log.d(TAG, "login:success");
                        } else {
                            Log.w(TAG, "login:failure", task.getException());
                        }
                    }
                });
    }

    /**
     * Restablecimiento de la contraseña desde un formulario de la aplicación.
     *
     * @param newPassword Nueva contraseña.
     */
    public void resetPassword(String newPassword) {
        userInSession.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });
    }

    public void reAuthenticate(String email, String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);

        userInSession.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "User re-authenticated.");
                    }
                });
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
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    public void sendEmailVerification(){
        userInSession.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    /**
     * Elimina la cuenta del usuario en sesión.
     */
    public void deleteAccount() {
        // TODO: Al eliminar la cuenta se eliminarán las dudas publicadas
        // del usuario
        userInSession.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Cuenta de usuario eliminada.");
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
