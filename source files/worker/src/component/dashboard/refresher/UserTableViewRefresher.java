package component.dashboard.refresher;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.function.Consumer;

public class UserTableViewRefresher extends TimerTask {
    private final Consumer<Map<String,String>> updateUsersTableView;

    public UserTableViewRefresher(Consumer<Map<String,String>> updateUsersTableViewIn) {
        this.updateUsersTableView = updateUsersTableViewIn;
    }

    @Override
    public void run() {
        HttpClientUtil.runAsync(Constants.USERS_LIST, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfUsersNames = response.body().string();
                Map<String,String> usersNames = Constants.GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, Map.class);
                updateUsersTableView.accept(usersNames);
                Objects.requireNonNull(response.body()).close();
            }
        });
    }
}
