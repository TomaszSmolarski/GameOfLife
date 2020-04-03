package sample;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ChangeGenerationCallable implements Callable<List<GridPoint>> {
    private int row;
    private boolean[][] bacteriaArray;
    private boolean periodBC;

    ChangeGenerationCallable(int row, boolean[][] bacteriaArray, boolean periodBC) {
        this.row = row;
        this.bacteriaArray = bacteriaArray;
        this.periodBC = periodBC;
    }

    private List<GridPoint> compute() {

        List<GridPoint> bacteriaToUpdate = new ArrayList<>();

        int rowIndex = row;
        for (int colIndex = 0; colIndex < bacteriaArray[0].length; colIndex++) {
            int neighborsNumber = 0;
            for (int row = -1; row < 2; row++) {
                for (int col = -1; col < 2; col++) {
                    int checkRow, checkCol;
                    checkRow = rowIndex + row;
                    checkCol = colIndex + col;
                    if (periodBC) {
                        checkRow = checkRow < 0 ? bacteriaArray.length - 1 : checkRow % bacteriaArray.length;
                        checkCol = checkCol < 0 ? bacteriaArray[0].length - 1 : checkCol % bacteriaArray[0].length;
                        if (bacteriaArray[checkRow][checkCol]) neighborsNumber++;

                    } else {
                        if (checkRow < 0 || (checkRow == bacteriaArray.length)) {
                            continue;
                        }
                        if (checkCol < 0 || (checkCol == bacteriaArray[0].length)) {
                            continue;
                        }
                        if (bacteriaArray[checkRow][checkCol]) neighborsNumber++;
                    }


                }
            }
            if (bacteriaArray[rowIndex][colIndex]) {
                neighborsNumber--;
            }
            if (!bacteriaArray[rowIndex][colIndex] && neighborsNumber == 3) {
                bacteriaToUpdate.add(new GridPoint(rowIndex, colIndex));
            }
            if (bacteriaArray[rowIndex][colIndex] && (neighborsNumber < 2 || neighborsNumber > 3)) {
                bacteriaToUpdate.add(new GridPoint(rowIndex, colIndex));
            }
        }

        return bacteriaToUpdate;
    }

    @Override
    public List<GridPoint> call() throws Exception {
        return compute();
    }
}
