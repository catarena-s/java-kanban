package ru.yandex.practicum.kanban.test;

public enum OperationType {
    ADD("add"), DEL("del"), UPDATE("upd"), GET("get"), MIX("mix");
    private String name;

    public static OperationType getByName(String name){
        switch (name) {
            case "add" : return ADD;
            case "del" : return DEL;
            case "upd" : return UPDATE;
            case "get" : return GET;
            default: return null;
        }
    }
    OperationType(String name) {
        this.name = name;
    }


}
