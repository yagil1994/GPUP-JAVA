package mission;

import input.task.TaskInputDto;
import input.task.impl.CompilationTaskInputDto;
import input.task.impl.SimulationTaskInputDto;

public class MissionNameAndDto {
    private String missionName,missionType;
    private SimulationForWorkerDto simulationForWorkerDto;
    private CompilationForWorkerDto compilationTaskInputDto;

    public MissionNameAndDto(SimulationForWorkerDto simulationForWorkerDtoIn,CompilationForWorkerDto compilationTaskInputDtoIn,
                             String missionNameIn,String missionTypeIn){
        missionName=missionNameIn;
        missionType=missionTypeIn;
        if(missionType.equals("Simulation")){
            simulationForWorkerDto= simulationForWorkerDtoIn;
            compilationTaskInputDto=null;
        }
        else{//Compilation
            compilationTaskInputDto= compilationTaskInputDtoIn;
            simulationForWorkerDto=null;
        }
    }

    public String getMissionName() {
        return missionName;
    }

    public String getMissionType() {
        return missionType;
    }

    public SimulationForWorkerDto getSimulationForWorkerDto() {
        return simulationForWorkerDto;
    }

    public CompilationForWorkerDto getCompilationTaskInputDto() {
        return compilationTaskInputDto;
    }
}
