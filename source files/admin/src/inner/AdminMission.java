package inner;

import com.google.gson.reflect.TypeToken;
import mission.AdminMissionDataUpdateDto;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import output.UpdateTargetStatusDuringTaskDto;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

public class AdminMission {
    private final String missionName;
    private String missionStatus;
    private final String graphName;
    private String totalTargets;
    private String independentsAmount;
    private String leavesAmount;
    private String middlesAmount;
    private String rootsAmount;

    private Integer registeredWorkers;
    private Integer runningTargets;
    private Integer availableTargets;
    private Set<String> processedTargets;
    private Integer amountOfRunningTargetsOnTask;

    private LinkedList<String> taskLogs;
    private LinkedList<String>allLogs;
    private List<String> taskReport;
    private Boolean allTargetsSucceed;

    private Map<String,AdminTargetData> adminTargetDataMap;  //targetName, AdminTargetData

    private Timer askingForTaskReportTimer;
    private Consumer<List<String>> updateTaskReport;

    public String getMissionStatus() {return missionStatus;}
    public void setMissionStatus(String missionStatus) {this.missionStatus = missionStatus;}

    public AdminMission(String missionNameIn, String graphNameIn, String totalTargetsIn, String independentsAmountIn,
                        String leavesAmountIn, String middlesAmountIn, String rootsAmountIn, Consumer<List<String>> updateTaskReportIn, String missionStatusIn){
        missionName = missionNameIn;
        graphName = graphNameIn;
        totalTargets = totalTargetsIn;
        independentsAmount = independentsAmountIn;
        leavesAmount = leavesAmountIn;
        middlesAmount = middlesAmountIn;
        rootsAmount = rootsAmountIn;

        registeredWorkers=0;
        runningTargets=0;
        availableTargets=0;

        adminTargetDataMap=new HashMap<>();
        taskLogs=new LinkedList<>();
        allLogs=new LinkedList<>();
        askingForTaskReportTimer=new Timer();
        taskReport=null;
        updateTaskReport=updateTaskReportIn;
        missionStatus=missionStatusIn;
        allTargetsSucceed=false;
        processedTargets=new HashSet<>();
        amountOfRunningTargetsOnTask=0;
    }

    public List<String> getTaskReport(){
        return taskReport;
    }

    public void startAskingForTaskReport() {
        class Helper extends TimerTask {
            public void run()
            {
                String finalUrl = HttpUrl
                        .parse(Constants.ASK_FOR_TASK_REPORT)
                        .newBuilder()
                        .addQueryParameter("missionName",missionName)
                        .build()
                        .toString();

                HttpClientUtil.runAsync(finalUrl, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String taskReportStringListJson = response.body().string();
                        Type type = new TypeToken<List<String>>() { }.getType();
                        synchronized (this) {
                        taskReport = Constants.GSON_INSTANCE.fromJson(taskReportStringListJson,type);
                           if(taskReport!=null&&!taskReport.isEmpty()) {
                                   updateTaskReport.accept(taskReport);
                                   askingForTaskReportTimer.cancel();
                               }
                           }
                        Objects.requireNonNull(response.body()).close();
                    }
                });

            }
        }
        TimerTask p = new Helper();
        askingForTaskReportTimer.schedule(p, Constants.REFRESH_RATE, Constants.FAST_REFRESH_RATE);
    }
    public List<UpdateTargetStatusDuringTaskDto> getUpdateTargetStatusDuringTaskDtoList(){
        requestToUpdateTargetsDataFromServer();
        List<UpdateTargetStatusDuringTaskDto> res=new ArrayList<>();
        for(Map.Entry<String,AdminTargetData> t:adminTargetDataMap.entrySet()){
            res.add(t.getValue().getUpdateTargetStatusDuringTaskDto());
        }
        res.sort(Comparator.comparing(UpdateTargetStatusDuringTaskDto::getTargetName));
        return res;
    }

    public String getTargetsRunningZAvailableTargets(){
        Integer targetsThatCanRun=runningTargets+availableTargets;
        return runningTargets.toString()+"/"+targetsThatCanRun.toString();
    }

    public void requestToUpdateDataFromServer(){
        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_ADMIN_MISSION_DATA)
                .newBuilder()
                .addQueryParameter("missionName",missionName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonAdminMissionDataUpdateDto = response.body().string();
                AdminMissionDataUpdateDto dto = Constants.GSON_INSTANCE.fromJson(jsonAdminMissionDataUpdateDto, AdminMissionDataUpdateDto.class);
                synchronized (this) {
                    updateFields(dto);
                }
                Objects.requireNonNull(response.body()).close();
            }
        });
    }
     private void updateFields(AdminMissionDataUpdateDto dto){
        Integer availableTargets=Integer.parseInt(dto.getAvailableTargets().toString());
        Integer runningTargets=Integer.parseInt(dto.getRunningTargets().toString());
        setAvailableTargets(availableTargets);
        setRunningTargets(runningTargets);
        setRegisteredWorkers(dto.getRegisteredWorkers());
        setProcessedTargets(dto.getProcessedTargets());
        setAllTargetsSucceed(dto.getAllTargetsSucceed());
        setAmountOfRunningTargetsOnTask(dto.getAmountOfRunningTargetsOnTask());
    }


    public void requestToUpdateTargetsDataFromServer(){
        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_ADMIN_TARGETS_DATA)
                .newBuilder()
                .addQueryParameter("missionName",missionName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Type type = new TypeToken<List<UpdateTargetStatusDuringTaskDto>>() { }.getType();
                String jsonUpdateTargetStatusDuringTaskDto = response.body().string();
                List<UpdateTargetStatusDuringTaskDto> dtoList = Constants.GSON_INSTANCE.fromJson(jsonUpdateTargetStatusDuringTaskDto,type);
                synchronized (this) {
                    updateAdminTargets(dtoList);
                    updateLogs();
                }
                Objects.requireNonNull(response.body()).close();
            }});
    }
    private void updateAdminTargets(List<UpdateTargetStatusDuringTaskDto>dtoList){
        for(UpdateTargetStatusDuringTaskDto dto:dtoList){
            if(adminTargetDataMap.containsKey(dto.getTargetName())) {
                adminTargetDataMap.get(dto.getTargetName()).updateNewStatusOnTask(dto.getNewStatusOnTask());
            }
            else {
                AdminTargetData t=new AdminTargetData(dto.getTargetName());
                t.updateNewStatusOnTask(dto.getNewStatusOnTask());
                adminTargetDataMap.put(dto.getTargetName(),t);
            }
        }
    }

    public String getLog(){
        if(!taskLogs.isEmpty()) {
            String log = taskLogs.removeFirst();
            allLogs.add(log);
            return log;
        }
        else {
            return null;
        }
    }
    private void updateLogs(){
        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_LOGS_INFO)
                .newBuilder()
                .addQueryParameter("missionName",missionName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonUpdateTargetStatusDuringTaskDto = response.body().string();
                String log = Constants.GSON_INSTANCE.fromJson(jsonUpdateTargetStatusDuringTaskDto,String.class);
                if(log!=null) {
                    synchronized (this){
                        taskLogs.add(log);
                    }
                }
                Objects.requireNonNull(response.body()).close();
            }
        });
    }

    public LinkedList<String> getAllLogs(){return allLogs;}
    public Consumer<String> getTaskReportLogWriter(){
        return new Consumer<String>() {
            @Override
            public void accept(String s) {
                taskLogs.add(s);
            }
        };
    }

    public double getProcessCalc(){
        int processed=getProcessedTargetsAmount();
        int totalTargetsThatRunOnThisMission=amountOfRunningTargetsOnTask;
        double progress=0.0;
        if(totalTargetsThatRunOnThisMission!=0){
            progress=((double)processed)/totalTargetsThatRunOnThisMission;
        }
        else {
            progress = 0;
        }
        return progress;
    }
    public String getProcessCalcStr() {
        double progress=getProcessCalc();
        return "Progress: "+ String.format("%.2f",(progress*100))+"%";
    }

    public Integer getAmountOfRunningTargetsOnTask() {
        return amountOfRunningTargetsOnTask;
    }

    public void setAmountOfRunningTargetsOnTask(Integer amountOfRunningTargetsOnTask) {
        this.amountOfRunningTargetsOnTask = amountOfRunningTargetsOnTask;
    }

    public List<String> getLogs() {
        return taskLogs;
    }

    public void setTotalTargets(String totalTargets) {
        this.totalTargets = totalTargets;
    }

    public void setIndependentsAmount(String independentsAmount) {
        this.independentsAmount = independentsAmount;
    }

    public void setLeavesAmount(String leavesAmount) {
        this.leavesAmount = leavesAmount;
    }

    public void setMiddlesAmount(String middlesAmount) {
        this.middlesAmount = middlesAmount;
    }

    public void setRootsAmount(String rootsAmount) {
        this.rootsAmount = rootsAmount;
    }

    public void setRegisteredWorkers(Integer registeredWorkers) {this.registeredWorkers = registeredWorkers;}

    public Set<String> getProcessedTargets() {return processedTargets;}

    public Integer getProcessedTargetsAmount() {return processedTargets.size();}

    public void setAllTargetsSucceed(Boolean allTargetsSucceed) {
        this.allTargetsSucceed = allTargetsSucceed;
    }

    public Boolean getAllTargetsSucceed() {
        return allTargetsSucceed;
    }

    public void setProcessedTargets(Set<String> processedTargets) {this.processedTargets = processedTargets;}

    public void setRunningTargets(Integer runningTargets) {
        this.runningTargets = runningTargets;
    }

    public void setAvailableTargets(Integer availableTargets) {
        this.availableTargets = availableTargets;
    }

    public Integer getRegisteredWorkers() {
        return registeredWorkers;
    }

    public String getMissionName() {
        return missionName;
    }

    public String getGraphName() {
        return graphName;
    }

    public String getTotalTargets() {
        return totalTargets;
    }

    public String getIndependentsAmount() {
        return independentsAmount;
    }

    public String getLeavesAmount() {
        return leavesAmount;
    }

    public String getMiddlesAmount() {
        return middlesAmount;
    }

    public String getRootsAmount() {
        return rootsAmount;
    }
}
