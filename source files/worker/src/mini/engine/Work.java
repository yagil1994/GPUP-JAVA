package mini.engine;
import com.google.gson.Gson;
import javafx.application.Platform;
import mission.CompilationForWorkerDto;
import mission.SimulationForWorkerDto;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import output.UpdateTargetStatusDuringTaskDto;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

public class Work {

    public Work(){}

    public static FileWriter StartingAndExtraInfoAboutTarget(String targetName,String extraInfo,String missionName,String missionFolder, Consumer<String> logsWriter){
        FileWriter targetFile = null;
        String fileName = missionFolder + "\\" +targetName + ".log";
        try {
            targetFile = new FileWriter(fileName);

            updateServerThatTargetIsInProcess(missionName,targetName);
            String startWorkOnTarget = "Starting the task on target: " + targetName + "\n\n";
            targetFile.write(startWorkOnTarget);
            logsWriter.accept(startWorkOnTarget);
            if (extraInfo != null) {
                String extraDataStr = "The extra data on target " + targetName + " is: " + extraInfo + "\n\n";
                targetFile.write(extraDataStr);
                logsWriter.accept(extraDataStr);
            }
        }
        catch (IOException ignore){}
        return targetFile;
    }

    public static String doCompilation(String extraInfo, String targetName, String missionName, CompilationForWorkerDto t, Consumer<String> logsWriter){
        Instant startTime = Instant.now();
        FileWriter targetFile= StartingAndExtraInfoAboutTarget(targetName,extraInfo,missionName, t.getMissionFolder(),logsWriter);
        String RunningResult=null;
        try {
            String convertedExtraInfo=convertExtraInfoToPath(extraInfo);
            String resourceName = t.getSrc() + "\\" + convertedExtraInfo+".java";
            String reportLog = "";
            String compileFileInfo="Compiling file: " + convertedExtraInfo+"\n";
            File f =new File(resourceName);
            if (!f.exists()) {
                RunningResult="FAILURE";
                targetFile.write("resource does not exists");
                logsWriter.accept("resource does not exists");
            }
            targetFile.write(compileFileInfo);
            logsWriter.accept(compileFileInfo);
            String compilerIsGoingToRunInfo="Compiler is going to run now the next line:\n" +
                    "javac -d " + t.getDest() + " -cp " + t.getDest() + " " + resourceName + "\n";
            targetFile.write(compilerIsGoingToRunInfo+"\n");
            logsWriter.accept(compilerIsGoingToRunInfo+"\n");
            Process javac = null;
            try {
                javac = new ProcessBuilder(
                        "javac", "-d", t.getDest(), "-cp", t.getDest(), resourceName)
                        .redirectErrorStream(false)
                        .start();
                BufferedReader r = new BufferedReader(new InputStreamReader(javac.getErrorStream()));
                String line;
                boolean error = false;
                while (true) {
                    line=r.readLine();
                    if (line == null) {
                        break;
                    }
                    error =true;
                    reportLog += line+ "\n";
                }
                    if (error) {
                        targetFile.write(reportLog+"\n");
                        RunningResult="FAILURE";
                    }
                    else {
                        RunningResult="SUCCESS";
                    }
            }
            catch (SecurityException | IOException javacFailed) {}

        }catch (Exception ignore){}

        Instant endTime = Instant.now();
        Duration res = Duration.between(startTime, endTime);
        String taskTime = getFullTimeFromDuration(res)+"\n";
        String fileName = t.getMissionFolder() + "\\" + targetName + ".log";
        try {
            targetFile.close();
        }catch (Exception ignore){}
        UpdateTargetStatusDuringTaskDto targetStatusDuringTaskDto =new UpdateTargetStatusDuringTaskDto(targetName, null, RunningResult,taskTime,missionName,"Compilation",fileName);
        updateServerAfterTargetDidTask(targetStatusDuringTaskDto,targetFile);
        return RunningResult;
    }

    private static void updateServerThatTargetIsInProcess(String missionName,String targetName){
        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_TARGET_IN_PROCESS)
                .newBuilder()
                .addQueryParameter("missionName", missionName)
                .addQueryParameter("targetName", targetName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call= HttpClientUtil.getOkHttpClient().newCall(request);
        try {
            final Response response = call.execute();
        }
        catch (Exception ignore){}
    }

    public static String doSimulation(String extraInfo, String targetName, String missionName, SimulationForWorkerDto t, Consumer<String> logsWriter) {
        Instant startTime = Instant.now();//todo later the end time and update the server
        FileWriter targetFile= StartingAndExtraInfoAboutTarget(targetName,extraInfo,missionName, t.getMissionFolder(),logsWriter);
        Long processTime = t.getProcessTime();
        Double probabilityOfSuccess = t.getProbabilityOfSuccess();
        Double probabilityOfWarning = t.getProbabilityOfWarning();
        String RunningResult=null;
        try {
            String sleepingTime = "'" + targetName + "' is going to sleep for: " + processTime + " milliseconds\n\n";
            targetFile.write(sleepingTime);
            logsWriter.accept(sleepingTime);
            String beforeSleepingTime = "Before sleeping time of " + targetName + ": " + getTime() + "\n\n";
            targetFile.write(beforeSleepingTime);
            logsWriter.accept(beforeSleepingTime);
            Thread.sleep(processTime);
            String afterSleepingTime = "After sleeping time of " + targetName + ": " + getTime() + "\n\n";
            targetFile.write(afterSleepingTime);
            logsWriter.accept(afterSleepingTime);
        }
         catch (IOException | InterruptedException ignore) {}
        if (Math.random() <= probabilityOfSuccess) {
            if (Math.random() <= probabilityOfWarning) {
                RunningResult="WARNING";
            } else {
                RunningResult="SUCCESS";
            }
        } else {
            RunningResult="FAILURE";
        }
        Instant endTime = Instant.now();
        Duration res = Duration.between(startTime, endTime);
        String taskTime = getFullTimeFromDuration(res)+"\n";
        String fileName = t.getMissionFolder() + "\\" + targetName + ".log";
        try {
            targetFile.close();
        }catch (Exception ignore){}
        UpdateTargetStatusDuringTaskDto targetStatusDuringTaskDto =new UpdateTargetStatusDuringTaskDto(targetName, null, RunningResult,taskTime,missionName,"Simulation",fileName);
        updateServerAfterTargetDidTask(targetStatusDuringTaskDto,targetFile);
        return RunningResult;
    }

    private static void updateServerAfterTargetDidTask(UpdateTargetStatusDuringTaskDto targetStatusDuringTaskDto,FileWriter targetFile){

        Gson gson = new Gson();
        String jsonTargetStatusDuringTaskDto=gson.toJson(targetStatusDuringTaskDto);
        System.out.println(jsonTargetStatusDuringTaskDto);
        String finalUrl=Constants.UPDATE_AFTER_TARGET_DID_TASK;
        RequestBody body =
                new MultipartBody.Builder()
                        .addFormDataPart("jsonTargetStatusDuringTaskDto",jsonTargetStatusDuringTaskDto)
                        .build();

        HttpClientUtil.runAsyncPost(finalUrl,body, new Callback() {
            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Objects.requireNonNull(response.body()).close();
            }
        });
    }

    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSSS");
        Date date = new Date();
        return dateFormat.format(date);
    }

    static public String getFullTimeFromDuration(Duration res) {
        long hours = res.toHours();
        res = res.minusHours(hours);
        long minutes = res.toMinutes();
        res = res.minusMinutes(minutes);
        long seconds = (res.toMinutes() / 60);
        res = res.minusSeconds(seconds);
        long millis = res.toMillis();
        return hours + ":" + minutes + ":" + seconds + ":" + millis+" ";
    }

    private static String convertExtraInfoToPath(String ExtraInfo) {
        return ExtraInfo.replaceAll("\\.", "\\\\");
    }
}
