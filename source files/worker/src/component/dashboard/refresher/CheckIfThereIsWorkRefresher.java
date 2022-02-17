package component.dashboard.refresher;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import mini.engine.innerEngine;
import misc.MissionNamesContainer;
import mission.TargetNameAndExtraInfoDto;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

public class CheckIfThereIsWorkRefresher extends TimerTask {
    private final Consumer<Map<String,List<TargetNameAndExtraInfoDto>>> sendToWorkAndUpdateServer;
    private final String workerUserName;
    private innerEngine inner;

    public CheckIfThereIsWorkRefresher(Consumer<Map<String,List<TargetNameAndExtraInfoDto>>> sendToWorkAndUpdateServerIn, String workerUserNameIn, innerEngine innerIn){
        sendToWorkAndUpdateServer = sendToWorkAndUpdateServerIn;
        workerUserName=workerUserNameIn;
        inner=innerIn;
    }

    @Override
    public void run() {
            Gson gson = new Gson();
            String jsonAllMissionsThatTheWorkerRegistered = gson.toJson(inner.getUnpauseRegisteredMissionArr());
            String amountOfAvailableThreadsInTheMoment = inner.getWorkerThreadPoolManager().getAmountOfAvailableThreads().toString();
            String finalUrl = Constants.CHECK_IF_THERE_IS_WORK;
            RequestBody body =
                    new MultipartBody.Builder()
                            .addFormDataPart("jsonAllMissionsThatTheWorkerRegistered", jsonAllMissionsThatTheWorkerRegistered)
                            .addFormDataPart("username", workerUserName)
                            .addFormDataPart("amountOfAvailableThreadsRightNow", amountOfAvailableThreadsInTheMoment)
                            .build();

            //todo async=parallel-> I dont need to get the answer right now! it's not urgent
            HttpClientUtil.runAsyncPost(finalUrl, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> {

                            }
                    );
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.code() == 200) {
                        String mapMissionsNamesToStrListOfAvailableTargetsNamesJson = response.body().string();
                        Type type = new TypeToken<Map<String, TargetNameAndExtraInfoDto[]>>() {}.getType();
                        Map<String, TargetNameAndExtraInfoDto[]> mapMissionsNamesToStrListOfAvailableTargetsNames = Constants.GSON_INSTANCE.fromJson(mapMissionsNamesToStrListOfAvailableTargetsNamesJson, type);
                        Map<String, List<TargetNameAndExtraInfoDto>> res = new HashMap<>();
                        for (Map.Entry<String, TargetNameAndExtraInfoDto[]> t : mapMissionsNamesToStrListOfAvailableTargetsNames.entrySet()) {
                            res.put(t.getKey(), new ArrayList<>(Arrays.asList(t.getValue())));
                        }
                        sendToWorkAndUpdateServer.accept(res);
                    }
                    Objects.requireNonNull(response.body()).close();
                }
            });
    }
}
