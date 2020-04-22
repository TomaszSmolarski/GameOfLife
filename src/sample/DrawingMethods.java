package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

class DrawingMethods {

    static Point setRectangleSize(Canvas canvas, int widthSize, int heightSize) {

        int rectangleWidth = 0;
        int rectangleHeight = 0;
        if(widthSize>=heightSize){
            rectangleWidth = (int) ((canvas.getWidth() / widthSize));
            if (heightSize * rectangleWidth <= canvas.getHeight()) {
                rectangleHeight = rectangleWidth;
            } else {
                rectangleHeight = (int) ((canvas.getHeight() / heightSize));
                if (heightSize * rectangleHeight > canvas.getHeight()) {
                    rectangleHeight--;
                }
            }
        }else {
            rectangleHeight = (int) ((canvas.getHeight() / heightSize));
            if (widthSize * rectangleHeight <= canvas.getWidth()) {
                rectangleWidth = rectangleHeight;
            } else {
                rectangleWidth = (int) ((canvas.getWidth() / widthSize));
                if (widthSize * rectangleWidth > canvas.getWidth()) {
                    rectangleWidth--;
                }
            }
        }
        return new Point(rectangleWidth, rectangleHeight);
    }

    static void drawStartGrid(Canvas canvas) {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    static void drawCustomGrid(GraphicsContext gc, boolean[][] bacteriaArray, Point rectangleSize) {
        for (int row = 0; row < bacteriaArray.length; row++) {
            for (int col = 0; col < bacteriaArray[0].length; col++) {
                Color bacteriaColor;
                bacteriaColor = bacteriaArray[row][col] ? Color.BLACK : Color.WHITE;
                gc.setFill(bacteriaColor);
                gc.fillRect(col * rectangleSize.getX(), row * rectangleSize.getY(), rectangleSize.getX(), rectangleSize.getY());
            }
        }
    }

    static void updateGrid(GraphicsContext gc, List<GridPoint> bacteriaToUpdateList,
                           boolean[][] bacteriaArray, Point rectangleSize) {
        try {
            for (GridPoint b : bacteriaToUpdateList) {
                Color bacteriaColor;
                bacteriaColor = bacteriaArray[b.getRow()][b.getCol()] ? Color.BLACK : Color.WHITE;
                gc.setFill(bacteriaColor);
                gc.fillRect(b.getCol() * rectangleSize.getX(), b.getRow() * rectangleSize.getY(), rectangleSize.getX(), rectangleSize.getY());
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }

    static void updateGridWithStruct(GraphicsContext gc, GridPoint clickedBacteria,
                                     List<GridPoint> bacteriaToUpdateListVector,
                                     boolean[][] bacteriaArray, Point rectangleSize) {
        List<GridPoint> updateBacteriaList = new ArrayList<>();
        int heightSize = bacteriaArray.length;
        int widthSize = bacteriaArray[0].length;
        for (GridPoint vector : bacteriaToUpdateListVector) {
            int checkRow = (int) (clickedBacteria.getRow() + vector.getCol());
            int checkCol = (int) (clickedBacteria.getCol() + vector.getRow());
            checkRow = checkRow < 0 ? heightSize - 1 : checkRow % heightSize;
            checkCol = checkCol < 0 ? widthSize - 1 : checkCol % widthSize;
            updateBacteriaList.add(new GridPoint(checkRow, checkCol));
            bacteriaArray[checkRow][checkCol] = true;
        }
        DrawingMethods.updateGrid(gc, updateBacteriaList, bacteriaArray, rectangleSize);
    }
}

