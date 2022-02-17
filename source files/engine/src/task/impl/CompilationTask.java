package task.impl;

import data.structure.Graph;
import data.structure.Target;
import input.task.TaskInputDto;
import input.task.impl.CompilationTaskInputDto;
import output.UpdateTargetStatusDuringTaskDto;
import task.misc.UiAdapterInterface;
import task.thread.PauseManager;

import java.io.*;

public class CompilationTask extends AbstractTaskManager implements Serializable {
    private String src;
    private String dest;
    public CompilationTask(final Graph g, TaskInputDto taskInput){
        super(g);
        updateMembers(taskInput);
    }
    public CompilationTask(final Graph g,TaskInputDto taskInput,AbstractTaskManager a,boolean copyMaps) {
        super(g,a,copyMaps);
        updateMembers(taskInput);
    }
private String convertExtraInfoToPath(String ExtraInfo){
    return ExtraInfo.replaceAll("\\.","\\\\");
}

    @Override
    public void updateMembers(TaskInputDto taskInput) {
        CompilationTaskInputDto compilationInput=(CompilationTaskInputDto) taskInput;
        dest=compilationInput.getCompilationDestFolderPath();
        src=compilationInput.getCompilationSourceFolderPath();
    }
}
