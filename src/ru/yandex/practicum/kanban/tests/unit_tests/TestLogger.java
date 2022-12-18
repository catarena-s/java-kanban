package ru.yandex.practicum.kanban.tests.unit_tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import ru.yandex.practicum.kanban.utils.Colors;
import ru.yandex.practicum.kanban.utils.Helper;

public interface TestLogger {

    @BeforeAll
    static void beforeAllTests(TestInfo testInfo) {
        Helper.printSeparator();
        Helper.printMessage(Colors.BOLD, testInfo.getTestClass().map(Class::getSimpleName).orElse(""));
        Helper.printSeparator();
    }

    @BeforeEach
    default void beforeEachTest(TestInfo testInfo) {
        Helper.printMessage(String.format("Test : [%s]",
                testInfo.getDisplayName()));
    }
}
