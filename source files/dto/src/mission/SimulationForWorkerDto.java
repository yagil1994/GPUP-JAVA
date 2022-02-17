package mission;

public class SimulationForWorkerDto {

    private String missionFolder;
    private Long processTime;
    private Double probabilityOfSuccess, probabilityOfWarning;

    public SimulationForWorkerDto(Long processTimeIn
       ,Double probabilityOfSuccessIn,Double probabilityOfWarningIn,String missionFolderIn){

        processTime=processTimeIn;
        probabilityOfSuccess=probabilityOfSuccessIn;
        probabilityOfWarning=probabilityOfWarningIn;
        missionFolder=missionFolderIn;
    }

    public String getMissionFolder() {
        return missionFolder;
    }

    public Long getProcessTime() {
        return processTime;
    }

    public Double getProbabilityOfSuccess() {
        return probabilityOfSuccess;
    }

    public Double getProbabilityOfWarning() {
        return probabilityOfWarning;
    }
}
