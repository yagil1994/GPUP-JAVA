package mission;

public class TargetNameAndExtraInfoDto {
   private String targetName,extraInfo;

    public TargetNameAndExtraInfoDto(String targetNameIn,String extraInfoIn){
        targetName=targetNameIn;
        extraInfo=extraInfoIn;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getExtraInfo() {
        return extraInfo;
    }
}
