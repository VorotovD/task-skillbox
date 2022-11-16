package solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("По сколько регионов записать в файл?");
        Scanner countRegions = new Scanner(System.in);
        int countRegionsByFile = countRegions.nextInt();
        System.out.println("Записать номера по " + countRegionsByFile + " регионов в файлы многопоточно?");
        System.out.println("y/n");
        Scanner taskScanner = new Scanner(System.in);
        String task = taskScanner.nextLine();
        System.out.println("Выполняю!");

        long start = System.currentTimeMillis();
        if (task.equals("n")) {
            //Запись в фалы однопоточно
            LoadToFilesWithoutMThreads writer =
                    new LoadToFilesWithoutMThreads(99, 999, countRegionsByFile);
            writer.writeFiles();
        } else if (task.equals("y")) {
            //Запись в файлы многопоточно
            int regionCodesCount = LoadToFilesWithMThreads.getRegionCodesCount();
            List<Thread> threadList = new ArrayList<>();

            int count = 1;
            for (int regionCode = 1; regionCode <= regionCodesCount; regionCode++) {

                if (regionCode - count == countRegionsByFile) {
                    LoadToFilesWithMThreads load = new LoadToFilesWithMThreads(count, regionCode, countRegionsByFile);
                    Thread thread = new Thread(load);
                    threadList.add(thread);
                    thread.start();
                    count = regionCode;
                }
            }
            if (count + countRegionsByFile != regionCodesCount) {
                LoadToFilesWithMThreads load = new LoadToFilesWithMThreads(count, regionCodesCount + 1, countRegionsByFile);
                Thread thread = new Thread(load);
                threadList.add(thread);
                thread.start();
            }

            for (Thread thread : threadList) {
                thread.join();
            }

        } else {
            System.out.println("Неизвесная команда");
        }
        System.out.println("Время записи: " + (System.currentTimeMillis() - start) + "ms");
    }
}
