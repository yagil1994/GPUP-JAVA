package component.dashboard.refresher;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import row.AvailableGraphsTableViewRow;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.Objects;
import java.util.TimerTask;
import java.util.function.Consumer;

public class AvailableGraphTableViewRefresher extends TimerTask {
    private final Consumer<AvailableGraphsTableViewRow[]> availableGraphs;

    public AvailableGraphTableViewRefresher(Consumer<AvailableGraphsTableViewRow[]> availableGraphsIn) {
      availableGraphs = availableGraphsIn;
    }
    @Override
    public void run() {
        HttpClientUtil.runAsync(Constants.GRAPHS_LIST, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonListOfMediators = response.body().string();
                AvailableGraphsTableViewRow[] availableGraphsRows = Constants.GSON_INSTANCE.fromJson(jsonListOfMediators,AvailableGraphsTableViewRow[].class);
                synchronized (this){
                    availableGraphs.accept(availableGraphsRows);
                }
                Objects.requireNonNull(response.body()).close();
            }
        });
    }
}
