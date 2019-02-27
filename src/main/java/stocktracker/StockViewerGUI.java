package stocktracker;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.styles.jmetro8.JMetro;

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

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Ca$h");
        LineChart<String,Number> lineChart = new LineChart<>(xAxis,yAxis);

        lineChart.setTitle("$$$");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        //series.setName("My portfolio");
        double money = 0;
        for (String line: FileManager.readLines("src\\main\\resources\\money.txt")) {
            String[] splitLine = line.split(" ");
            money = Double.parseDouble(splitLine[1]);
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(splitLine[0], money);
            dataPoint.setNode(new HoveredThresholdNode(money));
            series.getData().add(dataPoint);
        }
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.getData().add(series);

        HBox hBox = new HBox();
        Label label = new Label("Total ca$h: ");
        TextField field = new TextField("" + money);
        field.setEditable(false);
        hBox.getChildren().addAll(label, field);
        hBox.setAlignment(Pos.CENTER);

        Region region = new Region();
        vBox.getChildren().addAll(lineChart, hBox, region, statusLabel);
        VBox.setVgrow(region, Priority.ALWAYS);


        Scene scene = new Scene(vBox, 960, 540);
        new JMetro(JMetro.Style.LIGHT).applyTheme(scene);
        vBox.requestFocus();
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

        Button newButton = new Button("New tracker");
        newButton.setPrefSize(150, 20);
        newButton.setOnAction(event -> setupNewTrackerScene());

        Button existingButton = new Button("Existing tracker");
        existingButton.setPrefSize(150, 20);
        existingButton.setDisable(!FileManager.fileExists("src\\main\\resources\\saved_data\\existingData.txt"));
        //existingButton.setDisable(false);
        existingButton.setOnAction(event -> {
            //runTest();
            makeGraphScene();
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

        Scene scene = new Scene(vBox, width, height);
        new JMetro(JMetro.Style.LIGHT).applyTheme(scene);
        // So the newButton isn't focused
        vBox.requestFocus();
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
        new JMetro(JMetro.Style.LIGHT).applyTheme(scene);
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

        MenuItem howToUseItem = new MenuItem("Getting started");
        howToUseItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null); // Alerts have an optional header.
            alert.setTitle("How to use StockTracker");
            alert.setContentText("***Insert tutorial here***");
            alert.showAndWait();});
        helpMenu.getItems().addAll(aboutItem, howToUseItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);

    }

    public void runTest()
    {
        writeStockData("QQQ");
        String firstDate = writeStockData("IVV");
        writeCurrencyData("USD", "2018-09-24");
        aggregateData(new String[] {"IVV_USD", "QQQ_USD"}, new String[] {"5", "10"});
        //deleteTempFiles();
        setStatusLabel("Ready...");

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

    public void aggregateData(String[] ticker_currency, String[] stockAmounts)
    {
        setStatusLabel("Aggregating data..." );
        StockTracker.calculateMoney(ticker_currency, stockAmounts);
    }

    public void deleteTempFiles() {
        StockTracker.deleteTempFiles();
    }

    /**
     * Extremely simplified version of https://stackoverflow.com/questions/14615590/javafx-linechart-hover-values
     */
    class HoveredThresholdNode extends StackPane {
        HoveredThresholdNode(Number value) {
            setPrefSize(8, 8);
            setBackground(Background.EMPTY);
            Tooltip tooltip = new Tooltip("" + value);
            Tooltip.install(this, tooltip);
        }
    }

}
