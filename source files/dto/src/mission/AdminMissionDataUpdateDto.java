package mission;


import java.util.Set;

public class AdminMissionDataUpdateDto {
    private Integer registeredWorkers;
    private Long runningTargets;
    private Long availableTargets;
    private Set<String> processedTargets;
    private Boolean allTargetsSucceed;
    private Integer amountOfRunningTargetsOnTask;

    public AdminMissionDataUpdateDto(Integer registeredWorkersIn, Long runningTargetsIn, Long availableTargetsIn, Set<String> processedTargetsIn,
                                     Boolean allTargetsSucceedIn, Integer amountOfRunningTargetsOnTaskIn){
        registeredWorkers = registeredWorkersIn;
        runningTargets = runningTargetsIn;
        availableTargets = availableTargetsIn;
        processedTargets = processedTargetsIn;
        allTargetsSucceed=allTargetsSucceedIn;
        amountOfRunningTargetsOnTask = amountOfRunningTargetsOnTaskIn;
    }

    public Integer getAmountOfRunningTargetsOnTask() {
        return amountOfRunningTargetsOnTask;
    }

    public Boolean getAllTargetsSucceed() {
        return allTargetsSucceed;
    }

    public Set<String> getProcessedTargets() {return processedTargets;}

    public Integer getRegisteredWorkers() {
        return registeredWorkers;
    }

    public Long getRunningTargets() {
        return runningTargets;
    }

    public Long getAvailableTargets() {
        return availableTargets;
    }
}
