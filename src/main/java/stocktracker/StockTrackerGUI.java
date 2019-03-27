package stocktracker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.styles.jmetro8.JMetro;

import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;

//TODO: Add progress bars/loading indication

public class StockTrackerGUI extends Application {
    private Stage primaryStage;
    private int width;
    private int height;
    private ArrayList<ExtendableTextField> stocksTracked;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setupStartScene();
        primaryStage.show();
    }

    private void createScene(Region root) {
        Scene scene = new Scene(root, width, height);
        new JMetro(JMetro.Style.LIGHT).applyTheme(scene);
        root.getStylesheets().add("style.css");
        root.requestFocus();
        primaryStage.setScene(scene);
    }

    private void setupStartScene()
    {
        stocksTracked = new ArrayList<>();
        primaryStage.setTitle("StockTracker");
        primaryStage.getIcons().add(new Image("icon.png"));
        width = (int) Screen.getPrimary().getBounds().getWidth()/2;
        height = (int) Screen.getPrimary().getBounds().getHeight()/2;

        Button newButton = new Button("New tracker");
        newButton.setPrefSize(150, 20);
        newButton.setOnAction(event -> setupNewTrackerScene());
        new JMetro(JMetro.Style.DARK).applyTheme(newButton);

        Button existingButton = new Button("Existing tracker");
        existingButton.setPrefSize(150, 20);
        existingButton.setDisable(!new File(StockTracker.PATH + "save_config.csv").exists());
        existingButton.setOnAction(event -> {
            updateExistingData();
            makeGraphScene(false);
        });
        new JMetro(JMetro.Style.DARK).applyTheme(existingButton);

        VBox root = new VBox();

        BorderPane mainPane = new BorderPane();

        Label topLabel = new Label("StockTracker");
        topLabel.setStyle("-fx-font-size: 4.5em; -fx-text-fill: ivory; -fx-font-family: 'Alegreya Sans SC Medium';");

        VBox topNode = new VBox(topLabel);
        mainPane.setTop(topNode);
        topNode.setAlignment(Pos.CENTER);
        BorderPane.setMargin(topNode, new Insets(20, 0, 50, 0));

        VBox centerNode = new VBox();
        centerNode.setSpacing(15);
        centerNode.getChildren().addAll(newButton, existingButton);
        centerNode.setAlignment(Pos.CENTER);
        mainPane.setCenter(centerNode);

        setupMenuBar(root);

        Region region = new Region();
        root.getChildren().addAll(mainPane, region);
        VBox.setVgrow(region, Priority.ALWAYS);

        Image image = new Image("background.jpg");
        BackgroundFill backgroundFill = new BackgroundFill(new ImagePattern(image), CornerRadii.EMPTY, Insets.EMPTY);
        root.setBackground(new Background(backgroundFill));

        createScene(root);
    }

    private void setupMenuBar(Pane parent) {
        MenuBar menuBar = new MenuBar();

        parent.getChildren().add(menuBar);

        Menu fileMenu = new Menu("File");

        fileMenu.setStyle(null);
        MenuItem newItem = new MenuItem("New");
        newItem.setOnAction(event -> setupStartScene());
        MenuItem quitItem = new MenuItem("Quit");
        quitItem.setOnAction(event -> System.exit(0));
        fileMenu.getItems().addAll(newItem, quitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            // https://stackoverflow.com/questions/27976345/how-do-you-set-the-icon-of-a-dialog-control-java-fx-java-8/27983567
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("icon.png"));

            alert.setTitle("About StockTracker");
            alert.setHeaderText(null); // Alerts have an optional header.
            alert.setContentText("Author: Sten Arthur Laane\nVersion: " + StockTracker.VERSION);
            new JMetro(JMetro.Style.LIGHT).applyTheme(stage.getScene());
            alert.showAndWait();});

        MenuItem howToUseItem = new MenuItem("Getting started");
        howToUseItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("icon.png"));

            alert.setHeaderText(null); // Alerts have an optional header.
            alert.setTitle("How to use StockTracker");
            alert.setContentText("1. Press 'New Tracker'\n2. Choose the date you wish to start" +
                    " tracking the stocks from\n3. Write down the stock you wish to track " +
                    "(e.g. AAPL) in the ticker field. Write how much of that stock " +
                    "you own in the amount field.\n4. Press 'Go!'");
            new JMetro(JMetro.Style.LIGHT).applyTheme(stage.getScene());
            alert.showAndWait();});
        helpMenu.getItems().addAll(aboutItem, howToUseItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);
    }

    private void setupNewTrackerScene() {
        VBox root = new VBox();
        setupMenuBar(root);

        Label startLabel = new Label("Start date:");

        // DatePicker acts weird with JMetro. Sometimes randomly throws errors
        // on calendar button click and breaks
        DatePicker startDate = new DatePicker();
        startDate.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) > 0 );
            }
        });

        startDate.setEditable(false);
        VBox startDateBox = new VBox(startLabel, startDate);

        startDateBox.setAlignment(Pos.CENTER);

        VBox inputDataBox = new VBox();
        Label inputLabel = new Label("Stocks: ");
        inputDataBox.getChildren().addAll(inputLabel);
        ExtendableTextField tickerCurrencyTextField = new ExtendableTextField(inputDataBox);
        tickerCurrencyTextField.setMaxWidth(200);
        tickerCurrencyTextField.setPromptText("ticker");


        inputDataBox.setAlignment(Pos.CENTER);

        Button goButton = new Button("Go!");
        goButton.setOnAction(e -> plotNewData(startDate.getValue()));

        VBox contentPane = new VBox(startDateBox, inputDataBox, goButton);
        contentPane.setSpacing(30);
        contentPane.setAlignment(Pos.CENTER);
        root.getChildren().add(contentPane);

        createScene(root);
    }

    private void plotNewData(LocalDate startDate) {
        ArrayList<String> dataList = new ArrayList<>();
        ArrayList<Number> amounts = new ArrayList<>();
        for (ExtendableTextField field: stocksTracked) {
            String data = field.getText();
            if (data.length() > 0) {
                try {
                    writeData(data, startDate);
                    amounts.add(NumberFormat.getInstance().parse(field.amountField.getText()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dataList.add(data);
            }
        }
        createConfig(dataList, amounts);
        calculateMoney(dataList, amounts);
        createSave();
        makeGraphScene(true);
    }

    private void makeGraphScene(boolean newData) {
        VBox root = new VBox();
        setupMenuBar(root);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        xAxis.setTickMarkVisible(false);
        yAxis.setLabel("Ca$h (€)");
        LineChart<String,Number> lineChart = new LineChart<>(xAxis,yAxis);
        lineChart.getStylesheets().add("style.css");

        lineChart.setTitle("€€€");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        //series.setName("My portfolio");
        double money = 0;
        String moneyFile;
        if (newData) {
            moneyFile = StockTracker.PATH + "aggregated_with_money_temp.csv";
        }
        else {
            moneyFile = StockTracker.PATH + "save_data.csv";
        }

        for (String line: FileManager.readLines(moneyFile)) {
            String[] splitLine = line.split(",");
            money = Double.parseDouble(splitLine[splitLine.length-1]);
            String date = splitLine[0];
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(date, money);
            dataPoint.setNode(new HoveredThresholdNode(date, money));
            series.getData().add(dataPoint);
        }
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.getData().add(series);
        System.out.println("asd");

        HBox hBox = new HBox();
        Label label = new Label("Total ca$h: ");
        TextField field = new TextField("" + money);
        field.setDisable(true);
        field.setStyle("-fx-background-color: white;" +
                "-fx-text-fill: " + "darkgreen");
        field.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(label, field);
        hBox.setAlignment(Pos.CENTER);

        Region region = new Region();
        root.getChildren().addAll(lineChart, hBox, region);
        VBox.setVgrow(region, Priority.ALWAYS);

        createScene(root);
    }

    @Override
    public void stop(){
        deleteTempFiles();
    }

    private void updateExistingData() {
        StockTracker.updateSave();
    }

    private void createConfig(ArrayList<String> nameList, ArrayList<Number> amountList) {
        StockTracker.createConfig(nameList, amountList);
    }

    private void writeData(String ticker, LocalDate startDate) {
        StockTracker.writeData(ticker, startDate);
    }

    private static void createSave() {
        StockTracker.createSave();
    }

    private void calculateMoney(ArrayList<String> ticker_currency, ArrayList<Number> stockAmounts) {
        StockTracker.calculateMoney(ticker_currency, stockAmounts);
    }

    private void deleteTempFiles() {
        StockTracker.deleteTempFiles();
    }


    /**
     * Extremely simplified version of https://stackoverflow.com/questions/14615590/javafx-linechart-hover-values
     * Used to show values on graph when hovering over them
     */
    private class HoveredThresholdNode extends StackPane {
        private HoveredThresholdNode(String date, Number value) {
            setPrefSize(8, 8);
            setBackground(Background.EMPTY);
            Tooltip tooltip = new Tooltip(date + ": " + value);
            Tooltip.install(this, tooltip);
        }
    }

    private class ExtendableTextField extends TextField {
        private ExtendableTextField previousField;
        private ExtendableTextField nextField;
        private HBox container;
        private Pane root;
        private TextField amountField;

        private ExtendableTextField(Pane root) {
            super();
            previousField = null;
            nextField = null;
            container = new HBox();
            amountField = new TextField();
            container.setAlignment(Pos.CENTER);
            stocksTracked.add(this);
            this.root = root;
            setUp();
        }

        private void setUp() {
            focusedProperty().addListener(e -> {
                if (nextField == null && stocksTracked.size() < StockTracker.MAX_STOCKS) {
                    if (previousField == null ) {
                        makeNextField();
                    }
                    else if (previousField.getText().length() > 0) {
                        makeNextField();
                    }
                }
            });
            textProperty().addListener(e -> {
                if (getText().length() > 0) {
                    nextField.setDisable(false);
                    nextField.amountField.setDisable(false);

                }
            });
            amountField = new TextField();
            amountField.setMaxWidth(70);
            amountField.setPromptText("amount");
            container.getChildren().addAll(this, amountField);
            root.getChildren().addAll(container);
        }

        private void setPreviousField(ExtendableTextField field) {
            previousField = field;
        }

        private void makeNextField() {
            nextField = new ExtendableTextField(root);
            nextField.setPromptText(getPromptText());
            nextField.setMaxWidth(getMaxWidth());
            nextField.setPreviousField(this);
            nextField.setDisable(true);
            nextField.amountField.setDisable(true);
        }
    }
}
