package mini.engine;

import java.util.Objects;

public class TargetMission {
    private final String targetName,missionName;
    public TargetMission(String targetNameIn ,String missionNameIn){
        targetName=targetNameIn;
        missionName=missionNameIn;
    }
    public String getTargetName() {return targetName;}
    public String getMissionName() {return missionName;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TargetMission that = (TargetMission) o;
        return Objects.equals(targetName, that.targetName) && Objects.equals(missionName, that.missionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetName, missionName);
    }
}
