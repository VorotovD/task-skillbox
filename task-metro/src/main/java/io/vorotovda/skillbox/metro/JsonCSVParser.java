package io.vorotovda.skillbox.metro;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс для работы с файлами формата Json и CSV в заданной директории
 */
public class JsonCSVParser {
    /**
     * Путь к директории
     */
    String path;

    public JsonCSVParser(String path) {
        this.path = path;
    }

    /**
     * Метод обходит заданную директорию и возвращает пути к файлам Json и CSV формата
     *
     * @return Мапу путей к файлам. Ключ - формат файла, значение - массив путей к файла
     */
    public Map<String, List<String>> getPathToFiles() {
        Map<String, List<String>> result = new TreeMap<>();
        List<String> resultJson;
        List<String> resultCSV;

        try (Stream<Path> walk = Files.walk(Paths.get(this.path))) {
            resultJson = walk
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> p.toString().toLowerCase())
                    .filter(f -> f.endsWith("json"))
                    .collect(Collectors.toList());
            result.put("json", resultJson);
        } catch (IOException e) {
            throw new MetroException("Ошибка чтения json файлов из каталога: " + this.path, e);
        }
        try (Stream<Path> walk = Files.walk(Paths.get(this.path))) {
            resultCSV = walk
                    .filter(p -> !Files.isDirectory(p))
                    .map(p -> p.toString().toLowerCase())
                    .filter(f -> f.endsWith("csv"))
                    .collect(Collectors.toList());
            result.put("csv", resultCSV);
        } catch (IOException e) {
            throw new MetroException("Ошибка чтения csv файлов из каталога: " + this.path, e);
        }
        return result;
    }

    /**
     * Метод, в зависимости от формата файла, направляет массив путей к фалам в методы для соответствующего парсинга
     *
     * @return Результирующий набор данных из файлов Json и CSV форматов
     */
    public TreeMap<String, Station> getInformationFromFiles() {
        Map<String, List<String>> pathsToFiles = getPathToFiles();
        List<String> pathsJsonFiles = pathsToFiles.get("json");
        List<String> pathsCSVFiles = pathsToFiles.get("csv");
        TreeMap<String, Station> stations = new TreeMap<>();

        getInformationFormJson(pathsJsonFiles, stations);
        getInformationFormCSV(pathsCSVFiles, stations);
        return stations;
    }
    //todo повторяющийся код вынести в отдельныйы метод

    /**
     * Метод, в зависимости от конфигурации Json файла, парсит файл
     *
     * @param pathsJsonFiles Лист путей к файлам нужного формата
     * @param stations       Результирующая мапа станций
     */
    private void getInformationFormJson(List<String> pathsJsonFiles, Map<String, Station> stations) {
        JSONParser parser = new JSONParser();

        for (String path : pathsJsonFiles) {
            try {
                if (path.contains("depths-1")) {
                    JSONArray jsonData = (JSONArray) parser.parse(parseJsonFile(path));
                    Map<String, Object> dataStation = parseJsonAsDepth1(jsonData);
                    for (String station : dataStation.keySet()) {
                        stations.putIfAbsent(station, new Station(station));
                        stations.get(station).setDepth(String.valueOf(dataStation.get(station)));
                    }
                } else if (path.contains("dates-2")) {
                    JSONArray jsonData = (JSONArray) parser.parse(parseJsonFile(path));
                    Map<String, Object> dataStation = parseJsonAsDates2(jsonData);
                    for (String station : dataStation.keySet()) {
                        stations.putIfAbsent(station, new Station(station));
                        stations.get(station).setDate(String.valueOf(dataStation.get(station)));
                    }
                } else if (path.contains("depths-3")) {
                    JSONArray jsonData = (JSONArray) parser.parse(parseJsonFile(path));
                    Map<String, Object> dataStation = parseJsonAsDepth3(jsonData);
                    for (String station : dataStation.keySet()) {
                        stations.putIfAbsent(station, new Station(station));
                        stations.get(station).setDepth(String.valueOf(dataStation.get(station)));
                    }
                }
            } catch (ParseException e) {
                throw new MetroException("Ошибка парсинга файла: " + path, e);
            }
        }
    }

    /**
     * Метод, в зависимости от содержимого, парсит файл CSV формата. Если в @stations нет станции, создает ее, если есть
     * вызывает для внесения данных
     *
     * @param pathsJsonFiles Лист путей к файлам формата CSV в директории
     * @param stations       Результирующая мапа станций
     */
    private void getInformationFormCSV(List<String> pathsJsonFiles, Map<String, Station> stations) {
        for (String path : pathsJsonFiles) {
            if (path.contains("dates")) {
                try {
                    List<String> allLines = Files.readAllLines(Paths.get(path));
                    allLines.remove(0);
                    for (String line : allLines) {
                        String[] properties = line.split(",");
                        stations.putIfAbsent(properties[0], new Station(properties[0]));
                        stations.get(properties[0]).setDate(properties[1]);
                    }
                } catch (IOException e) {
                    throw new MetroException("Ошибка чтения файла: " + path, e);
                }
            } else if (path.contains("depths")) {
                try {
                    List<String> allLines = Files.readAllLines(Paths.get(path));
                    allLines.remove(0);
                    for (String line : allLines) {
                        String[] properties = line.split(",");
                        properties[1] = properties[1].replaceAll("\"", "");
                        stations.putIfAbsent(properties[0], new Station(properties[0]));
                        stations.get(properties[0]).setDepth(properties[1]);
                    }
                } catch (IOException e) {
                    throw new MetroException("Ошибка чтения файла: " + path, e);
                }
            }
        }
    }

    /**
     * Метод для парсинга Json файла определенной конфигурации
     *
     * @param stationsArray Массив станций
     * @return Мапу станций. Ключ - название станции, значение - объект (станция)
     */
    private Map<String, Object> parseJsonAsDepth1(JSONArray stationsArray) {
        Map<String, Object> map = new HashMap<>();

        stationsArray.forEach(stationObject -> {
            JSONObject lineJsonObject = (JSONObject) stationObject;
            String name = (String) lineJsonObject.get("name");
            String depth = String.valueOf(lineJsonObject.get("depth"));
            map.put(name, depth);
        });
        return map;
    }

    /**
     * Метод для парсинга Json файла определенной конфигурации
     *
     * @param stationsArray Массив станций
     * @return Мапу станций. Ключ - название станции, значение - объект (станция)
     */
    private Map<String, Object> parseJsonAsDates2(JSONArray stationsArray) {
        Map<String, Object> map = new HashMap<>();

        stationsArray.forEach(stationObject -> {
            JSONObject lineJsonObject = (JSONObject) stationObject;
            String name = (String) lineJsonObject.get("name");
            String date = (String) lineJsonObject.get("date");
            map.put(name, date);
        });
        return map;
    }

    /**
     * Метод для парсинга Json файла определенной конфигурации
     *
     * @param stationsArray Массив станций
     * @return Мапу станций. Ключ - название станции, значение - объект (станция)
     */
    private Map<String, Object> parseJsonAsDepth3(JSONArray stationsArray) {
        Map<String, Object> map = new HashMap<>();

        stationsArray.forEach(stationObject -> {
            JSONObject lineJsonObject = (JSONObject) stationObject;
            String name = (String) lineJsonObject.get("station_name");
            String depth = String.valueOf(lineJsonObject.get("depth_meters"));
            map.put(name, depth);
        });
        return map;
    }

    /**
     * Метод читает результирующий файл из метода writeMapJson и пишет в результирующий файл количество станций на линии
     *
     * @return Мапу линий и количеством станций на них
     */
    //TODO доделать
    public Map<String, Integer> getCountStationByLines() {
        Map<String, Integer> result = new TreeMap<>();
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonData = (JSONObject) parser.parse(parseJsonFile(path));
            JSONObject stationsData = (JSONObject) jsonData.get("stations");
            for (Object line : stationsData.keySet()) {
                JSONArray stations = (JSONArray) stationsData.get(line);
                result.put(line.toString(), stations.size());
            }
        } catch (ParseException e) {
            throw new MetroException("Ошибка парсинга файла: " + path, e);
        }
        return result;
    }


    /**
     * Метод для парсинга Json файла в строку
     *
     * @param path Путь к файлу Json формата
     * @return Строка после парсинга файла
     */
    private String parseJsonFile(String path) {
        StringBuilder builder = new StringBuilder();

        try {
            List<String> stations = Files.readAllLines(Paths.get(path));
            for (String str : stations) {
                builder.append(str);
            }
        } catch (IOException e) {
            throw new MetroException("Ошибка парсинга файла: " + path, e);
        }
        return builder.toString();

    }


}
