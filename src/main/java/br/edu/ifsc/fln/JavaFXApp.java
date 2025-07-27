package br.edu.ifsc.fln;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

public class JavaFXApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Caminho seguro para carregar o FXML
            URL fxmlLocation = JavaFXApp.class.getResource("/view/FXMLVBoxMainApp.fxml");
            if (fxmlLocation == null) {
                System.err.println("FXMLVBoxMainApp.fxml não foi encontrado! Verifique o caminho.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root, 800, 600);
            primaryStage.getIcons().add(new Image(JavaFXApp.class.getResourceAsStream("/icon/IFSC_logo_vertical.png")));
            primaryStage.setTitle("Sistema de Vendas do IFSC Florianópolis");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception ex) {
            System.err.println("Erro ao carregar a aplicação:");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
