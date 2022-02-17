package manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MediatorsManager {

    private final Map<String,Mediator> mediatorsMap;
    public MediatorsManager() {
        mediatorsMap = new HashMap<>();
    }
    public synchronized void addGraph(String graphName, Mediator newMed) {
        mediatorsMap.put(graphName,newMed);
    }
    public synchronized Map<String,Mediator> getMediatorsMap() {
        return Collections.unmodifiableMap(mediatorsMap);
    }
    public boolean isGraphExists(String graphName) {return mediatorsMap.containsKey(graphName);}

}
