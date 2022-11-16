package solution;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для поблочной записи автомобильных номеров в n файлов.
 */
public class LoadToFilesWithoutMThreads {

    private static final List<String> regionCodes = new ArrayList<>();
    private static final List<String> numbers = new ArrayList<>();
    private static final char[] letters = {'У', 'К', 'Е', 'Н', 'Х', 'В', 'А', 'Р', 'О', 'С', 'М', 'Т'};
    private final int regionCodesCount;
    private final int numbersCount;
    private final int countRegionsByFile;

    public LoadToFilesWithoutMThreads(int regionCodesCount, int numbersCount, int countRegionsByFile) {
        this.regionCodesCount = regionCodesCount;
        this.numbersCount = numbersCount;
        this.countRegionsByFile = countRegionsByFile;
        padNumber(2, regionCodes, regionCodesCount);
        padNumber(3, numbers, numbersCount);
    }

    public void writeFiles() throws Exception {
        int count = 1;
        for (int regionCode = 1; regionCode <= regionCodesCount; regionCode++) {
            if (regionCode - count == countRegionsByFile) {

                loadNumbersByFirstLetter(count, regionCode);
                count = regionCode;
            }
        }
        if (count + countRegionsByFile != regionCodesCount) {
            loadNumbersByFirstLetter(count, regionCodesCount + 1);
        }
    }

    private void loadNumbersByFirstLetter(int startRegion, int endRegion) throws IOException {
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
}
