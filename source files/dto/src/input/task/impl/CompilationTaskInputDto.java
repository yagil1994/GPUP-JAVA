package input.task.impl;
import input.task.TaskInputDto;

public class CompilationTaskInputDto implements TaskInputDto {
    private Boolean isScratch;
    final String[] targetsToRun;
    final private String compilationSourceFolderPath,compilationDestFolderPath;
     public CompilationTaskInputDto(Boolean isScratchInput, String compilationSourceFolderPathInput,
                                    String compilationDestFolderPathInput,String[] targetsToRunIn){
         isScratch=isScratchInput;
         compilationSourceFolderPath=compilationSourceFolderPathInput;
         compilationDestFolderPath=compilationDestFolderPathInput;
         targetsToRun=targetsToRunIn;
     }
    @Override
    final public String[] getTargetsToRun() {
        return targetsToRun;
    }
    final public Boolean getIsScratch() {
        return isScratch;
    }
    final public String getCompilationSourceFolderPath() {
        return compilationSourceFolderPath;
    }
    final public String getCompilationDestFolderPath() {
        return compilationDestFolderPath;
    }
    public void setScratch(Boolean scratch) {isScratch = scratch;}
}
