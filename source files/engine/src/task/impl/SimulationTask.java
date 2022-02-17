package task.impl;

import data.structure.Graph;
import data.structure.Target;
import input.task.TaskInputDto;
import input.task.impl.SimulationTaskInputDto;
import output.UpdateTargetStatusDuringTaskDto;
import task.misc.UiAdapterInterface;
import task.thread.PauseManager;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;


public class SimulationTask extends AbstractTaskManager implements Serializable {
    private Long processTime;
    private Double probabilityOfSuccess, probabilityOfWarning;

    public SimulationTask(final Graph g,TaskInputDto taskInput) {
        super(g);
        updateMembers(taskInput);
    }
    public SimulationTask(final Graph g,TaskInputDto taskInput,AbstractTaskManager a,boolean copyMaps) {
        super(g,a,copyMaps);
        updateMembers(taskInput);
    }

    @Override
    public void updateMembers(TaskInputDto taskInput) {
        SimulationTaskInputDto simulationInput=(SimulationTaskInputDto) taskInput;
        processTime = simulationInput.getProcessTime();
        Boolean isRandom = simulationInput.getIsRandom();

        if (isRandom && processTime!=0) {
            processTime = (long) Math.floor(Math.random() * (processTime + 1));
        }
        probabilityOfSuccess = simulationInput.getProbabilitySuccess();
        probabilityOfWarning = simulationInput.getProbabilityWarning();
    }
}
