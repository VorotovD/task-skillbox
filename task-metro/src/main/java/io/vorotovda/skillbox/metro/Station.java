package io.vorotovda.skillbox.metro;

/**
 * Класс описывает станцию на линии метрополитена
 */
public class Station {
    /**
     * Название станции
     */
    public String name;
    /**
     * Название линии
     */
    public String line;
    /**
     * Дата открытия
     */
    public String date;
    /**
     * Глубина залегания
     */
    public String depth;
    /**
     * Наличие перехода на другие станции
     */
    public Boolean hasConnection;

    public Station(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLine() {
        return line;
    }

    public String getDate() {
        return date;
    }

    public String getDepth() {
        return depth;
    }

    public Boolean getHasConnection() {
        return hasConnection;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Метод исправляет приходящие данные для дальнейшей корректной работы с ними
     *
     * @param depth Глубина залегания
     */
    public void setDepth(String depth) {
        if (depth.equals("?")) {
            this.depth = null;
        } else {
            this.depth = depth.replace('−', '-').replace(",",".");
        }
    }

    public void setHasConnection(Boolean hasConnection) {
        this.hasConnection = hasConnection;
    }


}


