package com.kirascript.demo;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextEditor extends Application {

    private TextArea textArea;
    private String currentFilePath = null;


    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Editor de Texto - KiraScript");

        textArea = new TextArea();
        BorderPane root = new BorderPane();
        root.setCenter(textArea);

        MenuBar menuBar = new MenuBar();
        root.setTop(menuBar);



        // Menú de Archivo
        Menu fileMenu = new Menu("Archivo");
        MenuItem newFile = new MenuItem("Nuevo");
        newFile.setOnAction(e -> newFile());
        MenuItem openFile = new MenuItem("Abrir");
        openFile.setOnAction(e -> openFile(primaryStage));
        MenuItem saveFile = new MenuItem("Guardar");
        saveFile.setOnAction(e -> saveFile());
        MenuItem saveAsFile = new MenuItem("Guardar como");
        saveAsFile.setOnAction(e -> saveAsFile(primaryStage));
        MenuItem exit = new MenuItem("Salir");
        exit.setOnAction(e -> System.exit(0));

        fileMenu.getItems().addAll(newFile, openFile, saveFile, saveAsFile, exit);
        menuBar.getMenus().add(fileMenu);

        // Menú de Edición
        Menu editMenu = new Menu("Edición");
        MenuItem search = new MenuItem("Buscar");
        search.setOnAction(e -> searchInText());
        MenuItem replace = new MenuItem("Reemplazar");
        replace.setOnAction(e -> replaceInText());
        MenuItem copy = new MenuItem("Copiar");
        copy.setOnAction(e -> textArea.copy());
        MenuItem cut = new MenuItem("Cortar");
        cut.setOnAction(e -> textArea.cut());
        MenuItem paste = new MenuItem("Pegar");
        paste.setOnAction(e -> textArea.paste());

        editMenu.getItems().addAll(search, replace, copy, cut, paste);
        menuBar.getMenus().add(editMenu);

        // Menú de Herramientas para KiraScript
        Menu toolsMenu = new Menu("Herramientas");
        MenuItem symbolTable = new MenuItem("Tabla de Símbolos");
        symbolTable.setOnAction(e -> showSymbolTable());
        MenuItem intermediateCode = new MenuItem("Código Intermedio");
        intermediateCode.setOnAction(e -> showIntermediateCode());
        MenuItem runKiraScript = new MenuItem("Ejecutar KiraScript");
        runKiraScript.setOnAction(e -> executeKiraScript(textArea.getText()));

        toolsMenu.getItems().addAll(symbolTable, intermediateCode, runKiraScript);
        menuBar.getMenus().add(toolsMenu);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void newFile() {
        textArea.clear();
        currentFilePath = null;
    }

    private void openFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                textArea.clear();
                String line;
                while ((line = br.readLine()) != null) {
                    textArea.appendText(line + "\n");
                }
                currentFilePath = file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        if (currentFilePath != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(currentFilePath))) {
                bw.write(textArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No hay archivo guardado. Usa 'Guardar como'.");
        }
    }

    private void saveAsFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            currentFilePath = file.getAbsolutePath();
            saveFile();
        }
    }

    private void searchInText() {
        String searchText = showInputDialog("Buscar", "Introduce el texto a buscar:");
        if (searchText != null && !searchText.isEmpty()) {
            String text = textArea.getText();
            if (text.contains(searchText)) {
                System.out.println("Texto encontrado.");
            } else {
                System.out.println("Texto no encontrado.");
            }
        }
    }

    private void replaceInText() {
        String searchText = showInputDialog("Reemplazar", "Texto a buscar:");
        String replaceText = showInputDialog("Reemplazar", "Texto por el que reemplazar:");
        if (searchText != null && replaceText != null) {
            String text = textArea.getText();
            textArea.setText(text.replace(searchText, replaceText));
        }
    }

    private void showSymbolTable() {
        // Crear una nueva ventana para mostrar la tabla de símbolos
        Stage stage = new Stage();
        stage.setTitle("Tabla de Símbolos");

        TableView<Symbol> tableView = new TableView<>();
        TableColumn<Symbol, String> symbolColumn = new TableColumn<>("Símbolo");
        symbolColumn.setCellValueFactory(cellData -> cellData.getValue().symbolProperty());
        TableColumn<Symbol, String> descriptionColumn = new TableColumn<>("Descripción");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        tableView.getColumns().add(symbolColumn);
        tableView.getColumns().add(descriptionColumn);

        // Lista predefinida de símbolos básicos de KiraScript
        ObservableList<Symbol> symbols = FXCollections.observableArrayList(
                new Symbol("JUSTICIA", "Inicia el programa."),
                new Symbol("PAPAFRITA", "Asignación de variable."),
                new Symbol("PLAN", "Condicional IF."),
                new Symbol("VICTORIA", "Alternativa ELSE."),
                new Symbol("BORRAR", "Eliminación de variable."),
                new Symbol("MANZANA", "Inicia un ciclo/bucle."),
                new Symbol("MORIRÁ", "Condicional IF con eliminación."),
                new Symbol("FIN", "Finaliza un bloque."),
                new Symbol("LOCO", "Imprimir mensaje."),
                new Symbol("GANE", "Termina el programa.")
        );

        // Agregar símbolos adicionales según el texto en el área, si es necesario
        String[] lines = textArea.getText().split("\n");
        for (String line : lines) {
            if (line.startsWith("PAPAFRITA")) {
                symbols.add(new Symbol("PAPAFRITA", "Asignación de variable -> " + line));
            } else if (line.startsWith("PLAN")) {
                symbols.add(new Symbol("PLAN", "Condicional IF -> " + line));
            }
            // Puedes agregar más análisis de línea aquí si es necesario
        }

        tableView.setItems(symbols);

        // Mostrar la tabla en una nueva escena
        Scene scene = new Scene(new BorderPane(tableView), 600, 400);
        stage.setScene(scene);
        stage.show();
    }


    private void showIntermediateCode() {
        String intermediateCode = "JUSTICIA\n" +
                "PAPAFRITA nombre \"Ryuk\"\n" +
                "LOCO \"El shinigami aparece.\"\n" +
                "PAPAFRITA objetivo \"L\"\n" +
                "PAPAFRITA estrategia \"Eliminar a L\"\n" +
                "\n" +
                "PLAN objetivo == \"L\"\n" +
                "VICTORIA\n" +
                "    LOCO \"Plan en marcha para eliminar a \" + objetivo\n" +
                "    PAPAFRITA nombreObjetivo \"L\"\n" +
                "    PAPAFRITA causa \"Muerte por corazón\"\n" +
                "    LOCO \"Causando la muerte de \" + nombreObjetivo + \" por \" + causa\n" +
                "    MANZANA\n" +
                "        LOCO \"Comprobando resultados...\"\n" +
                "        PAPAFRITA resultado \"Muerte confirmada\"\n" +
                "        LOCO resultado\n" +
                "    FIN\n" +
                "BORRAR objetivo\n" +
                "LOCO \"El objetivo ha sido eliminado.\"\n" +
                "GANE";

        // Mostrar el código intermedio en el textArea
        textArea.setText(intermediateCode);
    }




    private void executeKiraScript(String script) {
        System.out.println("Ejecutando KiraScript...");

        // StringBuilder para almacenar los resultados
        StringBuilder resultBuilder = new StringBuilder();

        // Dividir el script en líneas
        String[] lines = script.split("\n");

        // Procesar cada línea
        for (String line : lines) {
            line = line.trim(); // Eliminar espacios en blanco al principio y al final

            if (line.startsWith("JUSTICIA")) {
                resultBuilder.append("Iniciando el programa...\n");
            } else if (line.startsWith("PAPAFRITA")) {
                String variableDeclaration = line.substring("PAPAFRITA ".length()).trim();
                resultBuilder.append("Declarando variable: ").append(variableDeclaration).append("\n");
                // Aquí podrías almacenar la variable en un mapa o estructura de datos.
            } else if (line.startsWith("LOCO")) {
                String message = line.substring("LOCO ".length()).trim();
                resultBuilder.append("Mensaje: ").append(message).append("\n");
            } else if (line.startsWith("PLAN")) {
                String condition = line.substring("PLAN ".length()).trim();
                resultBuilder.append("Evaluando condición: ").append(condition).append("\n");
                // Aquí podrías agregar lógica para manejar las condiciones.
            } else if (line.startsWith("VICTORIA")) {
                resultBuilder.append("Condición cumplida, ejecutando bloque de victoria.\n");
            } else if (line.startsWith("MANZANA")) {
                resultBuilder.append("Iniciando bucle para comprobaciones.\n");
            } else if (line.startsWith("BORRAR")) {
                String variableToDelete = line.substring("BORRAR ".length()).trim();
                resultBuilder.append("Borrando variable: ").append(variableToDelete).append("\n");
                // Aquí podrías eliminar la variable de la estructura de datos.
            } else if (line.startsWith("GANE")) {
                resultBuilder.append("Terminando el programa.\n");
                break; // Salir del bucle al terminar el programa
            }
        }

        // Mostrar los resultados en una ventana emergente
        JOptionPane.showMessageDialog(null, resultBuilder.toString(), "Resultados de KiraScript", JOptionPane.INFORMATION_MESSAGE);
    }


    private String showInputDialog(String title, String header) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        return dialog.showAndWait().orElse(null);
    }

  //  public static void main(String[] args) {
   //     launch(args);
   // }

    public static class Symbol {
        private final SimpleStringProperty symbol;
        private final SimpleStringProperty description;

        public Symbol(String symbol, String description) {
            this.symbol = new SimpleStringProperty(symbol);
            this.description = new SimpleStringProperty(description);
        }

        public String getSymbol() {
            return symbol.get();
        }

        public String getDescription() {
            return description.get();
        }

        public SimpleStringProperty symbolProperty() {
            return symbol;
        }

        public SimpleStringProperty descriptionProperty() {
            return description;
        }
    }
}

