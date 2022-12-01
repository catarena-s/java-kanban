package ru.yandex.practicum.kanban.test.commands;

import ru.yandex.practicum.kanban.managers.TaskManager;
import ru.yandex.practicum.kanban.test.TestCommand;
import ru.yandex.practicum.kanban.utils.Helper;

public class TestMixCommand extends AbstractTest {
    @Override
    public void runTest(TaskManager taskManager, boolean isPrintHistory) {
        this.isPrintHistory = isPrintHistory;
        run(taskManager, TestCommand.MIX, this::runTestOperation);
    }

    private void runTestOperation(String line) {
        String[] records = line.split(",");
        TestCommand command = TestCommand.valueOf(records[0].trim().toUpperCase());
        switch (command) {
            case ADD:
                TestAddCommand testAddCommand = new TestAddCommand(taskManager, isPrintHistory);
                testAddCommand.insert(line);
                break;
            case REMOVE:
                TestRemoveCommand testRemoveCommand = new TestRemoveCommand(taskManager, isPrintHistory);
                testRemoveCommand.remove(line);
                break;
            case UPDATE:
                TestUpdateCommand testUpdateCommand = new TestUpdateCommand(taskManager, isPrintHistory);
                testUpdateCommand.update(line);
                break;
            case GET:
                TestGetCommand testGetCommand = new TestGetCommand(taskManager, isPrintHistory);
                testGetCommand.get(line);
                break;
            case CLONE:{
                TestCloneCommand testCloneCommand = new TestCloneCommand(taskManager,isPrintHistory);
                testCloneCommand.clone(line);
                break;
            }
            default:
                Helper.printMessage(Helper.WRONG_RECORD, line);
        }
    }
}
