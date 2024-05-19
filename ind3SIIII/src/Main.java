import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        int rowsA = 500;
        int colsA = 500;
        int rowsB = 500;
        int colsB = 500;

        // Проверка на совместимость матриц для умножения
        if (colsA != rowsB) {
            throw new IllegalArgumentException("Number of columns in Matrix A must be equal to number of rows in Matrix B");
        }

        int[][] matrixA = new int[rowsA][colsA];
        int[][] matrixB = new int[rowsB][colsB];
        int[][] result = new int[rowsA][colsB];

        // Инициализация матриц случайными значениями
        fillMatrix(matrixA);
        fillMatrix(matrixB);

        int numThreads = 4;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int blockSize = rowsA / numThreads;
        Future<?>[] futures = new Future[numThreads];

        // Создаем задачи для параллельного умножения матриц
        for (int i = 0; i < numThreads; i++) {
            int startRow = i * blockSize;
            int endRow = (i == numThreads - 1) ? rowsA : startRow + blockSize;
            futures[i] = executor.submit(new MultiplyTask(matrixA, matrixB, result, startRow, endRow));
        }

        // Ожидаем завершения всех задач
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Завершаем работу пула потоков
        executor.shutdown();

        // Опционально: выводим результат
        printMatrix(result);
    }

    private static void fillMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = (int) (Math.random() * 10);
            }
        }
    }

    // Опционально: метод для вывода матрицы
    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}

// Задача для умножения части матрицы
class MultiplyTask implements Runnable {
    private final int[][] matrixA;
    private final int[][] matrixB;
    private final int[][] result;
    private final int startRow;
    private final int endRow;

    public MultiplyTask(int[][] matrixA, int[][] matrixB, int[][] result, int startRow, int endRow) {
        this.matrixA = matrixA;
        this.matrixB = matrixB;
        this.result = result;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    @Override
    public void run() {
        int colsB = matrixB[0].length;
        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < colsB; j++) {
                result[i][j] = 0;
                for (int k = 0; k < matrixA[i].length; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
    }
}
