package mission;

public class CompilationForWorkerDto {

  private String src,dest,missionFolder;

    public CompilationForWorkerDto(String srcIn, String destIn, String missionFolderIn){
      src = srcIn;
      dest = destIn;
      missionFolder = missionFolderIn;
    }

  public String getSrc() {
    return src;
  }

  public String getDest() {
    return dest;
  }

  public String getMissionFolder() {
    return missionFolder;
  }
}
