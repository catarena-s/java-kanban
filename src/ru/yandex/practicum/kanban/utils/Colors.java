package ru.yandex.practicum.kanban.utils;

public enum Colors {
    //    BOLD("3", "4;31m", "", "3;31m", "1;3;31m"),
        UNDERLNE("4m", "4;31m", "", "3;31m", "1;3;31m"),
    CURENT("0m", "4m", "1m", "3m", "1;3m"),
    CYAN("36m", "4;36m", "1;36m", "3;36m", "1;3;36m"),
    RED("31m", "4;31m", "1;31m", "3;31m", "1;3;31m"),
    BLACK("30m", "4;30m", "1;30m", "3;30m", "1;3;30m"),
    GREEN("32m", "4;32m", "1;32m", "3;32m", "1;3;32m"),
    YELLOW("33m", "4;33m", "1;33m", "3;33m", "1;3;33m"),
    BLUE("34m", "4;34m", "1;34m", "3;34m", "1;3;34m"),
    PURPLE("35m", "4;35m", "1;35m", "3;35m", "1;3;35m"),
    WHITE("37m", "4;37m", "1;37m", "3;37m", "1;3;37m");

    public String getColor() {
        return color;
    }
    String underLineVal ="4";
    String italicVal ="3";
    String redVal ="31";
    String setColor(String... args){
        return String.join(";",args)+"m";
    }

    private final String color;
    private final String underLine;
    private final String bold;
    private final String italic;
    private final String boldItalic;

    public String getUnderLine() {
        return underLine;
    }

    public String getBold() {
        return bold;
    }

    public String getItalic() {
        return italic;
    }

    public String getBoldItalic() {
        return boldItalic;
    }

    Colors(String color, String underLine, String bold, String italic, String boldItalic) {
        this.color = color;
        this.underLine = underLine;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;
    }
}
