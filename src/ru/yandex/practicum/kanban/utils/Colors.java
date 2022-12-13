package ru.yandex.practicum.kanban.utils;

public enum Colors {
    BLACK ("30m"),
    RED ("31m"),
    GREEN ("32m"),
    YELLOW ("33m"),
    BLUE ("34m"),
    PURPLE ("35m"),
    CYAN ("36m"),
    BOLD ("3"),
    WHITE ("37m");

    public String getColorNumber() {
        return colorNumber;
    }

    private final String colorNumber;

    Colors(String colorNumber) {
        this.colorNumber = colorNumber;
    }
}
