package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import javax.swing.Timer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;


public class Controller {
    private static final int NTHREADS = 10;
    private boolean[][] bacteriaArray;
    private List<GridPoint> bacteriaToUpdateArray;
    private Point rectangleSize;
    private int endIterations;
    private Timer delayBetweenUpdates;
    private GraphicsContext gc;
    @FXML
    Canvas canvas;
    @FXML
    Spinner<Integer> iterationSpinner;
    @FXML
    Spinner<Integer> delayBetweenIterations;
    @FXML
    Spinner<Integer> widthOfGrid;
    @FXML
    Spinner<Integer> heightOfGrid;
    @FXML
    ComboBox<String> initialStruct;
    @FXML
    CheckBox infinityLoop;
    @FXML
    CheckBox periodBC;

    @FXML
    public void initialize() {
        iterationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000));
        delayBetweenIterations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(20, 1000));
        widthOfGrid.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 300));
        heightOfGrid.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 300));
        initialStruct.getItems().addAll(
                "Random",
                "Glider",
                "Oscillator",
                "Invariable",
                "Manual");
        initialStruct.setValue("Manual");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        initValues();


        delayBetweenUpdates = new Timer(delayBetweenIterations.getValue(), new ActionListener() {

            @Override
            public synchronized void actionPerformed(ActionEvent actionEvent) {
                if (delayBetweenIterations.getValue() != delayBetweenUpdates.getDelay()) {
                    delayBetweenUpdates.setDelay(delayBetweenIterations.getValue());
                }
                changeGeneration();
                Platform.runLater(() -> {
                    if (iterationSpinner.getValue() > 0) {


                        if (bacteriaToUpdateArray.isEmpty()) {
                            delayBetweenUpdates.stop();
                            alert.setTitle("Informacja");
                            alert.setHeaderText(null);
                            alert.setContentText("Ta symulacja popadła w stagnację po " + endIterations + " iteracji.");
                            if (!alert.isShowing()) {
                                alert.showAndWait();
                            }
                            clearButtonOnAction();
                            initValues();
                        } else {
                            endIterations++;
                            for (GridPoint b : bacteriaToUpdateArray) {
                                bacteriaArray[b.getRow()][b.getCol()] = !bacteriaArray[b.getRow()][b.getCol()];
                            }
                            DrawingMethods.updateGrid(gc, bacteriaToUpdateArray, bacteriaArray, rectangleSize);
                            if (!infinityLoop.isSelected()) {
                                iterationSpinner.getValueFactory().setValue(iterationSpinner.getValue() - 1);
                            }
                        }
                    } else {
                        delayBetweenUpdates.stop();
                        initValues();
                    }
                });
            }
        });
        delayBetweenUpdates.setRepeats(true);
        defineArray();

        widthOfGrid.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!delayBetweenUpdates.isRunning()) {
                clearButtonOnAction();
            }
        });

        heightOfGrid.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!delayBetweenUpdates.isRunning()) {
                clearButtonOnAction();
            }
        });


        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                List<GridPoint> bacteriaToUpdateListVector = new ArrayList<>();
                GridPoint clickedBacteria = new GridPoint(
                        (int) (mouseEvent.getY() / rectangleSize.getY()),
                        (int) (mouseEvent.getX() / rectangleSize.getX()));
                if (clickedBacteria.getRow() < bacteriaArray.length && clickedBacteria.getCol() < bacteriaArray[0].length) {
                    switch (initialStruct.getValue()) {
                        case "Manual":
                            if (!bacteriaArray[clickedBacteria.getRow()][clickedBacteria.getCol()]) {
                                gc.setFill(Color.BLACK);
                                bacteriaArray[clickedBacteria.getRow()][clickedBacteria.getCol()] = true;

                            } else {
                                gc.setFill(Color.WHITE);
                                bacteriaArray[clickedBacteria.getRow()][clickedBacteria.getCol()] = false;
                            }
                            gc.fillRect(clickedBacteria.getCol() * rectangleSize.getX(), clickedBacteria.getRow() * rectangleSize.getY(),
                                    rectangleSize.getY(), rectangleSize.getX());
                            break;
                        case "Random": {
                            Random generator = new Random();
                            int numberOfBacterias = generator.nextInt(bacteriaArray.length * bacteriaArray[0].length);
                            for (int i = 0; i < numberOfBacterias; i++) {
                                int row = generator.nextInt(bacteriaArray.length);
                                int col = generator.nextInt(bacteriaArray[0].length);
                                bacteriaArray[row][col] = true;
                            }
                            DrawingMethods.drawCustomGrid(gc, bacteriaArray, rectangleSize);
                            break;
                        }
                        case "Glider": {
                            bacteriaToUpdateListVector.add(new GridPoint(0, 1));
                            bacteriaToUpdateListVector.add(new GridPoint(0, 2));
                            bacteriaToUpdateListVector.add(new GridPoint(1, 0));
                            bacteriaToUpdateListVector.add(new GridPoint(1, 1));
                            bacteriaToUpdateListVector.add(new GridPoint(2, 2));

                            DrawingMethods.updateGridWithStruct(gc, clickedBacteria,
                                    bacteriaToUpdateListVector, bacteriaArray, rectangleSize);
                            break;
                        }

                        case "Oscillator": {
                            bacteriaToUpdateListVector.add(new GridPoint(0, 0));
                            bacteriaToUpdateListVector.add(new GridPoint(0, 1));
                            bacteriaToUpdateListVector.add(new GridPoint(0, 2));
                            DrawingMethods.updateGridWithStruct(gc, clickedBacteria,
                                    bacteriaToUpdateListVector, bacteriaArray, rectangleSize);
                            break;
                        }
                        case "Invariable": {
                            bacteriaToUpdateListVector.add(new GridPoint(0, 1));
                            bacteriaToUpdateListVector.add(new GridPoint(0, 2));
                            bacteriaToUpdateListVector.add(new GridPoint(1, 0));
                            bacteriaToUpdateListVector.add(new GridPoint(1, 3));
                            bacteriaToUpdateListVector.add(new GridPoint(2, 1));
                            bacteriaToUpdateListVector.add(new GridPoint(2, 2));
                            DrawingMethods.updateGridWithStruct(gc, clickedBacteria,
                                    bacteriaToUpdateListVector, bacteriaArray, rectangleSize);
                        }


                    }
                }
            }

        });
    }


    @FXML
    public void startButtonOnAction() {
        delayBetweenUpdates.start();
    }

    @FXML
    public void stopButtonOnAction() {
        delayBetweenUpdates.stop();
    }

    @FXML
    public void continueButtonOnAction() {
        delayBetweenUpdates.restart();
    }

    @FXML
    public void clearButtonOnAction() {
        if (delayBetweenUpdates.isRunning()) {
            delayBetweenUpdates.stop();
        }
        defineArray();

    }

    private void initValues() {
        iterationSpinner.getValueFactory().setValue(100);
        delayBetweenIterations.getValueFactory().setValue(50);
        widthOfGrid.getValueFactory().setValue(30);
        heightOfGrid.getValueFactory().setValue(30);
        endIterations = 0;
    }

    private void defineArray() {
        gc = canvas.getGraphicsContext2D();
        System.out.println(widthOfGrid.getValue()+"+"+heightOfGrid.getValue());
        rectangleSize = DrawingMethods.setRectangleSize(canvas, widthOfGrid.getValue(), heightOfGrid.getValue());
        bacteriaToUpdateArray = new ArrayList<>();
        bacteriaArray = new boolean[heightOfGrid.getValue()][widthOfGrid.getValue()];
        for (int row = 0; row < heightOfGrid.getValue(); row++) {
            for (int column = 0; column < widthOfGrid.getValue(); column++) {
                bacteriaArray[row][column] = false;
            }
        }
        DrawingMethods.drawStartGrid(canvas);
    }


    private void changeGeneration() {
        bacteriaToUpdateArray.clear();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NTHREADS);
        List<Future<List<GridPoint>>> resultList = new ArrayList<>();
        for (int i = 0; i < bacteriaArray.length; i++) {
            Callable<List<GridPoint>> callable = new ChangeGenerationCallable(i, bacteriaArray, periodBC.isSelected());
            Future<List<GridPoint>> result = executor.submit(callable);
            resultList.add(result);
        }
        for (Future<List<GridPoint>> listFuture : resultList) {
            try {
                bacteriaToUpdateArray.addAll(listFuture.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    }


}
