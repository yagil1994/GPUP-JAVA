package component.dashboard.refresher;

import com.google.gson.reflect.TypeToken;
import mission.AdminPendingMissionsTableViewDtoWithoutRadioButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.TimerTask;
import java.util.function.Consumer;

public class AdminPendingMissionsTableViewRefresher extends TimerTask {

    private final Consumer<AdminPendingMissionsTableViewDtoWithoutRadioButton[]> availableTasks;

    public AdminPendingMissionsTableViewRefresher(Consumer<AdminPendingMissionsTableViewDtoWithoutRadioButton[]> availableTasksIn) {
        availableTasks = availableTasksIn;
    }
    @Override
    public void run() {
        HttpClientUtil.runAsync(Constants.ADMIN_TASKS_ROWS, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonListOfMediators = response.body().string();
                Type type = new TypeToken<AdminPendingMissionsTableViewDtoWithoutRadioButton[]>() { }.getType();
                AdminPendingMissionsTableViewDtoWithoutRadioButton[] availableTasksRows = Constants.GSON_INSTANCE.fromJson(jsonListOfMediators, type);
                synchronized (this){
                    availableTasks.accept(availableTasksRows);
                }
                Objects.requireNonNull(response.body()).close();
            }
        });
    }

}
