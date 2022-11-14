package solution;

import java.util.ArrayList;
import java.util.List;

public class MainLoadFilesWithMThreads {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        //Запись в 4 файла многопоточно
        int regionCodesCount = LoadToFilesWithMThreads.getRegionCodesCount();
        int countRegionsByFile = LoadToFilesWithMThreads.getCountRegionsByFile();
        List<Thread> threadList = new ArrayList<>();

        int count = 1;
        for (int regionCode = 1; regionCode <= regionCodesCount; regionCode++) {

            if (regionCode - count == countRegionsByFile) {
                LoadToFilesWithMThreads load = new LoadToFilesWithMThreads(count,regionCode);
                Thread thread = new Thread(load);
                threadList.add(thread);
                thread.start();
                count = regionCode;
            }
        }
        if (count + countRegionsByFile != regionCodesCount) {
            LoadToFilesWithMThreads load = new LoadToFilesWithMThreads(count,regionCodesCount + 1);
            Thread thread = new Thread(load);
            threadList.add(thread);
            thread.start();
        }

        for (Thread thread :threadList) {
            thread.join();
        }

        System.out.println(System.currentTimeMillis() - start + "ms main thread");
    }
}
