package mission;

public class GraphNameAndNewMissionNameDto {
  private final String graphName;
    private final String newMissionName;
    public GraphNameAndNewMissionNameDto(String graphNameIn,String newMissionNameIn){
        graphName=graphNameIn;
        newMissionName=newMissionNameIn;
    }

    public String getGraphName() {return graphName;}
    public String getNewMissionName() {return newMissionName;}
}
