package solution;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Класс для поблочной записи автомобильных номеров в n файлов в многопоточном режиме
 */
public class LoadToFilesWithMThreads implements Runnable {

    private static final List<String> regionCodes;
    private static final List<String> numbers;
    private static final char[] letters = {'У', 'К', 'Е', 'Н', 'Х', 'В', 'А', 'Р', 'О', 'С', 'М', 'Т'};
    private static final int regionCodesCount = 99;
    private static final int numbersCount = 999;
    private final int countRegionsByFile;
    private final int startRegion;
    private final int endRegion;

    static {
        regionCodes = new ArrayList<>();
        numbers = new ArrayList<>();
        padNumber(3, numbers, 999);
        padNumber(2, regionCodes, 99);
    }

    public LoadToFilesWithMThreads(int startRegion, int endRegion, int countRegionsByFile) {
        this.startRegion = startRegion;
        this.endRegion = endRegion;
        this.countRegionsByFile = countRegionsByFile;
    }

    @Override
    public void run() {
        try {
            FileWriter writer = new FileWriter
                    ("src/main/java/result/numbers" +
                            "From" +
                            startRegion +
                            "To" +
                            (endRegion - 1) +
                            ".txt");
            for (int regionCode = startRegion; regionCode < endRegion; regionCode++) {
                StringBuilder builder = new StringBuilder();
                for (int number = 1; number <= numbersCount; number++) {
                    for (char firstLetter : letters) {
                        for (char secondLetter : letters) {
                            for (char thirdLetter : letters) {
                                builder.append(firstLetter);
                                builder.append(numbers.get(number - 1));
                                builder.append(secondLetter);
                                builder.append(thirdLetter);
                                builder.append(regionCodes.get(regionCode - 1));
                                builder.append('\n');
                            }
                        }
                    }
                }
                writer.write(builder.toString());
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void padNumber(int numberLength, List<String> array, int count) {
        for (int i = 1; i <= count; i++) {
            StringBuilder numberStr = new StringBuilder(Integer.toString(i));
            int padSize = numberLength - numberStr.length();
            numberStr = new StringBuilder();
            numberStr.append("0".repeat(padSize));
            numberStr.append(i);
            array.add(numberStr.toString());
        }
    }

    public static int getRegionCodesCount() {

        return regionCodesCount;
    }

    public int getCountRegionsByFile() {
        return countRegionsByFile;
    }
}
