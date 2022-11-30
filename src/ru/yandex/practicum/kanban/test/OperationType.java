package ru.yandex.practicum.kanban.test;

import ru.yandex.practicum.kanban.test.solid.operations.AdditionalTest;
import ru.yandex.practicum.kanban.test.solid.Test1;

public enum OperationType {
    ADD("add", new AdditionalTest()), DEL("del", new AdditionalTest()), UPDATE("upd", new AdditionalTest()), GET("get", new AdditionalTest()), MIX("mix", new AdditionalTest());
    private String name;
    private Test1 test;

    public static OperationType getByName(String name){
        switch (name) {
            case "add" : return ADD;
            case "del" : return DEL;
            case "upd" : return UPDATE;
            case "get" : return GET;
            default: return null;
        }
    }
    OperationType(String name, Test1 test) {
        this.name = name;
        this.test = test;
    }


}
