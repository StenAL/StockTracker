package stocktracker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.FileInputStream;

//TODO: Add icons

public class StockViewerGUI extends Application {
    private Stage primaryStage;
    private Label statusLabel;
    private int width;
    private int height;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setupStartScene();
        primaryStage.show();
    }

    private void makeGraphScene() {
        VBox vBox = new VBox();
        setupMenuBar(vBox);
        Region region = new Region();
        vBox.getChildren().addAll(region, statusLabel);
        VBox.setVgrow(region, Priority.ALWAYS);


        Scene scene = new Scene(vBox, 960, 540);

        primaryStage.setScene(scene);
    }

    private void setStatusLabel(String newProgress) {
        statusLabel.setText(newProgress);
    }

    private void setupStartScene()
    {
        primaryStage.setTitle("Stock tracker");
        statusLabel = new Label("");
        width = (int) Screen.getPrimary().getBounds().getWidth()/2;
        height = (int) Screen.getPrimary().getBounds().getHeight()/2;

        ImageView imageView = null;
        Background background = null;

        Button newButton = new Button("New tracker");
        newButton.setMaxSize(150, 20);
        newButton.setOnAction(event -> setupNewTrackerScene());

        // Todo: Deactivate existingButton if no save/config found
        Button existingButton = new Button("$$$ Existing tracker $$$");
        existingButton.setPrefSize(150, 20);
        existingButton.setOnAction(event -> {
                makeGraphScene();
                //StockTracker.runTest();
        });

        VBox vBox = new VBox();

        BorderPane mainPane = new BorderPane();

        Label topLabel = new Label("Stock Tracker");
        topLabel.setStyle("-fx-font-size: 3em;");
        VBox topNode = new VBox(topLabel);
        mainPane.setTop(topNode);
        topNode.setAlignment(Pos.CENTER);
        BorderPane.setMargin(topNode, new Insets(20, 0, 50, 0));

        VBox centerNode = new VBox();
        centerNode.setSpacing(15);
        centerNode.getChildren().addAll(newButton, existingButton);
        centerNode.setAlignment(Pos.CENTER);
        mainPane.setCenter(centerNode);

        setStatusLabel("Ready...");

        setupMenuBar(vBox);

        Region region = new Region();
        vBox.getChildren().addAll(mainPane, region, statusLabel);
        VBox.setVgrow(region, Priority.ALWAYS);



        //TODO: Size = quarter of screen size
        Scene scene = new Scene(vBox, width, height);
        primaryStage.setScene(scene);
    }

    private void setupNewTrackerScene() {
        VBox vBox = new VBox();
        setupMenuBar(vBox);


        Label startLabel = new Label("Start date:");
        DatePicker startDate = new DatePicker();
        VBox startDateBox = new VBox(startLabel, startDate);
        startDateBox.setAlignment(Pos.CENTER);

        Label endLabel = new Label("End date:");
        DatePicker endDate = new DatePicker();
        VBox endDateBox = new VBox(endLabel, endDate);
        endDateBox.setAlignment(Pos.CENTER);

        //TextField stock1 = new TextField();
        VBox contentPane = new VBox(startDateBox, endDateBox);
        contentPane.setSpacing(30);
        //contentPane.setAlignment(Pos.CENTER);
        vBox.getChildren().add(contentPane);
        Scene scene = new Scene(vBox, width, height);

        primaryStage.setScene(scene);

    }

    private void setupMenuBar(Pane parent) {
        MenuBar menuBar = new MenuBar();
        parent.getChildren().add(menuBar);

        Menu fileMenu = new Menu("File");
        MenuItem quitItem = new MenuItem("Quit");
        quitItem.setOnAction(event -> System.exit(0));
        fileMenu.getItems().add(quitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About StockTracker");
            alert.setHeaderText(null); // Alerts have an optional header.
            alert.setContentText("Author: Sten Laane\nVersion: " + StockTracker.VERSION);
            alert.showAndWait();});
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);

    }

    public void runTest()
    {
        writeStockData("QQQ");
        String firstDate = writeStockData("IVV");
        writeCurrencyData("USD", firstDate);
        aggregateData(new String[] {"IVV_USD", "QQQ_USD"});
        setStatusLabel("Done");

    }

    public String writeStockData(String ticker)
    {
        setStatusLabel("Fetching stock " + ticker + " data..." );
        return StockTracker.writeStockData(ticker);
    }

    public void writeCurrencyData(String currencyCode, String firstdate) {
        setStatusLabel("Fetching " + currencyCode + " data..." );
        StockTracker.writeCurrencyData(currencyCode, firstdate);
    }

    public void aggregateData(String[] ticker_currency)
    {
        setStatusLabel("Aggregating data..." );
        StockTracker.aggregateData(ticker_currency);
    }
}
