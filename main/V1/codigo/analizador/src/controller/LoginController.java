package controller;

import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Label lblMensaje;

    private Stage stage;
    private Consumer<SesionUsuario> onLoginSuccess;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnLoginSuccess(Consumer<SesionUsuario> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    @FXML
    private void initialize() {
        lblMensaje.setText("");
    }

    @FXML
    private void autenticar() {
        String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();

        var sesion = LoginCredentialsValidator.autenticar(usuario, contrasena);

        if (sesion.isEmpty()) {
            lblMensaje.setText("Usuario o contraseña incorrectos.");
            txtContrasena.clear();
            txtContrasena.requestFocus();
            return;
        }

        lblMensaje.setText("");
        if (onLoginSuccess != null) {
            onLoginSuccess.accept(sesion.get());
        }
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void cancelar() {
        if (stage != null) {
            stage.close();
        }
    }
}