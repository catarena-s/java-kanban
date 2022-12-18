package ru.yandex.practicum.kanban.utils;

public enum Colors {
    RED("31m"),
    CYAN("36m"), CYAN_BOLD_ITALIC("1;3;36m"),
    BLACK("30m"),
    GREEN("32m"),
    YELLOW("33m"),
    BLUE("34m"),
    PURPLE("35m"),
    WHITE("37m"),
    UNDERLNE("4m"),
    ITALIC("3m"),
    BOLD("1m");

    public String getColor() {
        return color;
    }

    private final String color;

    Colors(String color) {
        this.color = color;
    }
}
