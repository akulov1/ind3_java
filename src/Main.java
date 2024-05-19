import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        // Инициализация массива
        int[] array = new int[1000000];
        // Заполнение массива случайными числами
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) (Math.random() * 100);
        }

        int numThreads = 4; // Количество потоков
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        int blockSize = array.length / numThreads;
        Future<Long>[] futures = new Future[numThreads];

        // Разбиваем массив на блоки и создаем задачи для их обработки
        for (int i = 0; i < numThreads; i++) {
            int start = i * blockSize;
            int end = (i == numThreads - 1) ? array.length : start + blockSize;
            futures[i] = executor.submit(new SumTask(array, start, end));
        }

        long totalSum = 0;
        try {
            // Собираем результаты выполнения задач
            for (Future<Long> future : futures) {
                totalSum += future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Завершаем работу пула потоков
        executor.shutdown();

        // Выводим результат
        System.out.println("Total sum: " + totalSum);
    }
}

class SumTask implements Callable<Long> {
    private final int[] array;
    private final int start;
    private final int end;

    public SumTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    public Long call() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += array[i];
        }
        return sum;
    }
}

