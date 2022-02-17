package component.dashboard.refresher;

import mission.WorkerPendingMissionsTableViewDto;
import mission.WorkerPendingMissionsTableViewDtoWithOutCheckBox;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.Objects;
import java.util.TimerTask;
import java.util.function.Consumer;

public class WorkerPendingMissionsTableViewRefresher extends TimerTask {

    private final Consumer<WorkerPendingMissionsTableViewDto[]> availableTasks;
    private  String workerName;
    public WorkerPendingMissionsTableViewRefresher(Consumer<WorkerPendingMissionsTableViewDto[]> availableTasksIn, String workerNameIn) {
        availableTasks = availableTasksIn;
        workerName=workerNameIn;
    }
    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(Constants.WORKER_TASKS_ROWS)
                .newBuilder()
                .addQueryParameter("username",workerName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonListOfMediators = response.body().string();
                WorkerPendingMissionsTableViewDtoWithOutCheckBox[] availableTasksRowsWithoutSelectedCheckBox = Constants.GSON_INSTANCE.fromJson(jsonListOfMediators, WorkerPendingMissionsTableViewDtoWithOutCheckBox[].class);
                WorkerPendingMissionsTableViewDto[] availableTasksRows = new WorkerPendingMissionsTableViewDto[availableTasksRowsWithoutSelectedCheckBox.length];
                int i = 0;
                for (WorkerPendingMissionsTableViewDtoWithOutCheckBox x : availableTasksRowsWithoutSelectedCheckBox) {
                    availableTasksRows[i] = new WorkerPendingMissionsTableViewDto(x);
                    i++;
                }
                synchronized (this) {
                    availableTasks.accept(availableTasksRows);
                }
                Objects.requireNonNull(response.body()).close();
            }
        });
    }
}
