package mission;


public class AdminTotalInfoForMissionWithOutParams {
    private String missionName;
    private String graphName;
    private String createdBy;
    private String leavesAmount;
    private String independentsAmount;
    private String middlesAmount;
    private String rootsAmount;
    private String totalTaskPrice;

    private String taskType;
    private String totalTargetsAmount;
    private String priceForTarget;

    public AdminTotalInfoForMissionWithOutParams(String missionNameIn, String  graphNameIn, String createdByIn,
                                                 String taskTypeIn)
    {
        missionName =missionNameIn;
        graphName = graphNameIn;
        createdBy = createdByIn;
        leavesAmount = null;
        independentsAmount = null;
        middlesAmount = null;
        rootsAmount = null;
        totalTaskPrice = null;
        //amountOfWorkersOnTask = null;
        taskType = taskTypeIn;
        totalTargetsAmount = null;
        priceForTarget =null;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLeavesAmount() {
        return leavesAmount;
    }

    public void setLeavesAmount(String leavesAmount) {
        this.leavesAmount = leavesAmount;
    }

    public String getIndependentsAmount() {
        return independentsAmount;
    }

    public void setIndependentsAmount(String independentsAmount) {
        this.independentsAmount = independentsAmount;
    }

    public String getMiddlesAmount() {
        return middlesAmount;
    }

    public void setMiddlesAmount(String middlesAmount) {
        this.middlesAmount = middlesAmount;
    }

    public String getRootsAmount() {
        return rootsAmount;
    }

    public void setRootsAmount(String rootsAmount) {
        this.rootsAmount = rootsAmount;
    }

    public String getTotalTaskPrice() {
        return totalTaskPrice;
    }

    public void setTotalTaskPrice(String totalTaskPrice) {
        this.totalTaskPrice = totalTaskPrice;
    }

//    public String getAmountOfWorkersOnTask() {
//        return amountOfWorkersOnTask;
//    }
//
//    public void setAmountOfWorkersOnTask(String amountOfWorkersOnTask) {
//        this.amountOfWorkersOnTask = amountOfWorkersOnTask;
//    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTotalTargetsAmount() {
        return totalTargetsAmount;
    }

    public void setTotalTargetsAmount(String totalTargetsAmount) {
        this.totalTargetsAmount = totalTargetsAmount;
    }

    public String getPriceForTarget() {
        return priceForTarget;
    }

    public void setPriceForTarget(String priceForTarget) {
        this.priceForTarget = priceForTarget;
    }
}
