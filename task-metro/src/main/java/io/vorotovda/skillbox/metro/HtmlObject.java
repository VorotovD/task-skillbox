package io.vorotovda.skillbox.metro;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Класс для работы с html документом
 */
public class HtmlObject {
    /**
     * Содержит html файл после парсинга
     */
    private final String parsedFile;

    /**
     * Сразу парсит файл по переданному пути
     *
     * @param path Путь к html файлу
     */
    public HtmlObject(String path) {
        this.parsedFile = parseFile(path);
    }

    /**
     * Метод для парсинга html файла
     *
     * @param path Путь к html файлу
     * @return Строку после парсинга html файла
     */
    private String parseFile(String path) {
        StringBuilder builder = new StringBuilder();

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            throw new MetroException("Ошибка чтения файла: " + path, e);
        }
        builder.append(lines);

        return builder.toString();
    }

    /**
     * Метод извлекает из пропарсеренного html файла названия и номера линий
     *
     * @return Названия и номера линий
     */
    public Map<String, String> getLines() {
        Document metro = Jsoup.parse(parsedFile);
        Elements lines = metro.select("div.js-toggle-depend");
        Map<String, String> result = new TreeMap<>();

        lines.forEach(element ->
                result.put(element.children().get(0).attributes().get("data-line"), element.text().replace(",", ""))
        );
        return result;
    }

    /**
     * Метод извлекает из пропарсеренного html файла линии и станции на линии,
     * формирует массив объектов (станций) и указывает:
     * название станции, линии, наличие перехода
     *
     * @return Мапу линий и станций на ней. Ключ - линия, значение - массив станций на ней.
     */
    public Map<String, ArrayList<Station>> getStations() {
        Document metro = Jsoup.parse(parsedFile);
        Elements lines = metro.select("div.js-toggle-depend");
        ArrayList<String> lineNumbers = new ArrayList<>();
        Map<String, ArrayList<Station>> result = new TreeMap<>();

        lines.forEach(element ->
                lineNumbers.add(element.children().get(0).attributes().get("data-line"))
        );

        lineNumbers.forEach(lineNumber -> {
            Elements stationOnLines = metro.select("[data-line=" + lineNumber + "]");
            ArrayList<Station> stations = new ArrayList<>();

            stationOnLines.get(1).children().forEach(station -> {
                Station stationToAdd = new Station(station.child(1).text().replace(",", ""));
                stationToAdd.setLine(stationOnLines.get(0).text());
                if (station.children().get(1).childNodeSize() > 1) {
                    stationToAdd.setHasConnection(true);
                }
                stations.add(stationToAdd);
            });
            result.put(stationOnLines.get(0).text(), stations);
        });
        return result;
    }
}
