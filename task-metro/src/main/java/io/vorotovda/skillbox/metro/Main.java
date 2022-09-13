package io.vorotovda.skillbox.metro;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {
        HtmlObject htmlFile = new HtmlObject("task-metro/src/main/resources/metro.html");
        JsonCSVParser jsonCsv = new JsonCSVParser("task-metro/src/main/resources/dataJsonCSVFiles");

        writeMapJson("task-metro/src/main/resources/resultJsonFiles/map.json", htmlFile.getLines(), htmlFile.getStations());
        writeStationsJson("task-metro/src/main/resources/resultJsonFiles/stations.json", htmlFile.getStations(), jsonCsv.getInformationFromFiles());

        JsonCSVParser mapJson = new JsonCSVParser("task-metro/src/main/resources/resultJsonFiles/map.json");
        writeCountStation("task-metro/src/main/resources/resultJsonFiles/countStations.json",mapJson.getCountStationByLines());

    }

    /**
     * Метод пишет в файл список линий и список станций по линиям
     *
     * @param path     Путь записи результирующего файла
     * @param lines    Мапа номеров линий и их названий
     * @param stations Мапа станций, ключ - линия, значение - массив станций на ней.
     */
    public static void writeMapJson(String path, Map<String, String> lines, Map<String, ArrayList<Station>> stations) {
        Map<String, Object> json = new HashMap<>();
        json.put("lines", lines);

        Map<String, ArrayList<String>> toJson = new HashMap<>();
        for (String key : stations.keySet()) {
            ArrayList<String> listStation = new ArrayList<>();
            for (Station station : stations.get(key)) {
                listStation.add(station.getName());
            }
            toJson.put(key, listStation);
        }
        json.put("stations", toJson);

        JSONObject resultObject = new JSONObject(json);
        try (PrintWriter out = new PrintWriter(path)) {
            out.write(resultObject.toString());
        } catch (FileNotFoundException e) {
            throw new MetroException("Файл не найден: " + path, e);
        }

    }

    /**
     * Метод пишет в файл станции с указанием названия, линии, даты открытия, глубины, наличия перехода,
     * в случае отсутствия данных, данного свойства в фале не будет
     *
     * @param path                Путь записи результирующего файла
     * @param stationsLineConnect Мапа станций по линиям, ключ - название линии, значения - массив станций на линии.
     *                            Объект станция содержит название линии и наличие перехода
     * @param stationDateDepth    Мапа станций, ключ - название станции, значение - объект (станция).
     *                            Содержит дату открытия и глубину.
     */
    public static void writeStationsJson(String path, Map<String, ArrayList<Station>> stationsLineConnect, TreeMap<String, Station> stationDateDepth) {
        Map<String, Object> json = new HashMap<>();
        JSONArray jsonArray = new JSONArray();

        Map<String, Station> resultStationsMap = getResultStationsMap(stationsLineConnect, stationDateDepth);


        for (Station station : resultStationsMap.values()) {
            JSONObject stationToJson = new JSONObject();
            stationToJson.put("name", station.getName());
            stationToJson.put("line", station.getLine());
            if (station.getDate() != null) {
                stationToJson.put("date", station.getDate());
            }
            if (station.getDepth() != null) {
                stationToJson.put("depth", Double.parseDouble(station.getDepth()));
            }
            stationToJson.put("hasConnection", station.getHasConnection());

            jsonArray.add(stationToJson);
        }
        json.put("stations", jsonArray);

        JSONObject resultObject = new JSONObject(json);
        try (PrintWriter out = new PrintWriter(path)) {
            out.write(resultObject.toString());
        } catch (FileNotFoundException e) {
            throw new MetroException("Файл не найден: " + path, e);
        }
    }

    public static void writeCountStation(String path, Map<String, Integer> lines) {
        JSONObject jsonObject = new JSONObject();
        for (String line: lines.keySet()) {
            jsonObject.put(line,lines.get(line));
        }
        try (PrintWriter out = new PrintWriter(path)) {
            out.write(jsonObject.toString());
        } catch (FileNotFoundException e) {
            throw new MetroException("Файл не найден: " + path, e);
        }
    }

    /**
     * Метод объединяет информацию о станции из двух источников
     *
     * @param stationsLineConnect Мапа станций по линиям, ключ - название линии, значения - массив станций на линии.
     *                            Объект станция содержит название линии и наличие перехода.
     * @param stationsDateDepth   Мапа станций, ключ - название станции, значение - объект (станция).
     *                            Содержит дату открытия и глубину.
     * @return  Результирующую мапу с полной информацией о станции. Ключ - название станции, значение - объект (станция)
     */
    public static Map<String, Station> getResultStationsMap(Map<String, ArrayList<Station>> stationsLineConnect, Map<String, Station> stationsDateDepth) {
        Map<String, Station> result = new TreeMap<>();
        Map<String, Station> stationsFromLineConnect = new HashMap<>();

        for (ArrayList<Station> stationList : stationsLineConnect.values()) {
            for (Station station : stationList) {
                stationsFromLineConnect.put(station.getName(), station);
            }
        }

        for (Station stationFromLineConnect : stationsFromLineConnect.values()) {
            if (stationsDateDepth.containsKey(stationFromLineConnect.getName())) {
                if (stationsDateDepth.get(stationFromLineConnect.getName()).getDate() != null) {
                    stationFromLineConnect.setDate(stationsDateDepth.get(stationFromLineConnect.getName()).getDate());
                }
                if (stationsDateDepth.get(stationFromLineConnect.getName()).getDepth() != null) {
                    stationFromLineConnect.setDepth(stationsDateDepth.get(stationFromLineConnect.getName()).getDepth());
                }
            }
            result.put(stationFromLineConnect.getName(), stationFromLineConnect);
        }
        return result;
    }

}


