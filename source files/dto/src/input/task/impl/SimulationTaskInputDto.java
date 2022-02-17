package input.task.impl;

import input.task.TaskInputDto;

public class SimulationTaskInputDto implements TaskInputDto {
    private Boolean isScratch;
    final private Long processTime;
    final private Boolean isRandom;
    final private Double probabilitySuccess, probabilityWarning;
    final String[] targetsToRun;

    public SimulationTaskInputDto(Long processTimeInput, Boolean isRandomInput,
                                  Double probabilitySuccessInput, Double probabilityWarningInput,
                                  Boolean isScratchInput,String[] targetsToRunIn) {
        processTime = processTimeInput;
        isRandom = isRandomInput;
        probabilitySuccess = probabilitySuccessInput;
        probabilityWarning = probabilityWarningInput;
        isScratch = isScratchInput;
        targetsToRun=targetsToRunIn;
    }

    public void setScratch(Boolean scratch) {isScratch = scratch;}
    final public String[] getTargetsToRun() {
       return targetsToRun;
   }
    final public Long getProcessTime() {
        return processTime;
    }

    final public Boolean getIsRandom() {
        return isRandom;
    }

    final public Double getProbabilitySuccess() {
        return probabilitySuccess;
    }

    final public Double getProbabilityWarning() {
        return probabilityWarning;
    }

    final public Boolean getIsScratch() {
        return isScratch;
    }
}
