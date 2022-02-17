package row;

import javafx.beans.property.SimpleStringProperty;

public class AvailableGraphsTableViewRow {
    private SimpleStringProperty graphNameCol, uploadedByCol, leavesCol, independentsCol, middlesCol,
    rootsCol, totalTargetsCol, simulationCol, compilationCol;

    public AvailableGraphsTableViewRow(String graphNameColumn, String uploadedByColumn, String leavesColumn, String independentsColumn,
           String middlesColumn, String rootsColumn, String totalTargetsColumn, String simulationColumn,String compilationColumn){
        graphNameCol = new SimpleStringProperty(graphNameColumn);
        uploadedByCol = new SimpleStringProperty(uploadedByColumn);
        leavesCol =new SimpleStringProperty(leavesColumn);
        independentsCol = new SimpleStringProperty(independentsColumn);
        middlesCol =new SimpleStringProperty(middlesColumn);
        rootsCol = new SimpleStringProperty(rootsColumn);
        totalTargetsCol = new SimpleStringProperty(totalTargetsColumn);
        simulationCol = new SimpleStringProperty(simulationColumn);
        compilationCol = new SimpleStringProperty(compilationColumn);
    }

    public String getSimulationCol() {
        return simulationCol.get();
    }

    public SimpleStringProperty simulationColProperty() {
        return simulationCol;
    }

    public void setSimulationCol(String simulationCol) {
        this.simulationCol.set(simulationCol);
    }

    public String getCompilationCol() {
        return compilationCol.get();
    }

    public SimpleStringProperty compilationColProperty() {
        return compilationCol;
    }

    public void setCompilationCol(String compilationCol) {
        this.compilationCol.set(compilationCol);
    }

    public String getGraphNameCol() {
        return graphNameCol.get();
    }

    public SimpleStringProperty graphNameColProperty() {
        return graphNameCol;
    }

    public void setGraphNameCol(String graphNameCol) {
        this.graphNameCol.set(graphNameCol);
    }

    public String getUploadedByCol() {
        return uploadedByCol.get();
    }

    public SimpleStringProperty uploadedByColProperty() {
        return uploadedByCol;
    }

    public void setUploadedByCol(String uploadedByCol) {
        this.uploadedByCol.set(uploadedByCol);
    }

    public String getLeavesCol() {
        return leavesCol.get();
    }

    public SimpleStringProperty leavesColProperty() {
        return leavesCol;
    }

    public void setLeavesCol(String leavesCol) {
        this.leavesCol.set(leavesCol);
    }

    public String getIndependentsCol() {
        return independentsCol.get();
    }

    public SimpleStringProperty independentsColProperty() {
        return independentsCol;
    }

    public void setIndependentsCol(String independentsCol) {
        this.independentsCol.set(independentsCol);
    }

    public String getMiddlesCol() {
        return middlesCol.get();
    }

    public SimpleStringProperty middlesColProperty() {
        return middlesCol;
    }

    public void setMiddlesCol(String middlesCol) {
        this.middlesCol.set(middlesCol);
    }

    public String getRootsCol() {
        return rootsCol.get();
    }

    public SimpleStringProperty rootsColProperty() {
        return rootsCol;
    }

    public void setRootsCol(String rootsCol) {
        this.rootsCol.set(rootsCol);
    }

    public String getTotalTargetsCol() {
        return totalTargetsCol.get();
    }

    public SimpleStringProperty totalTargetsColProperty() {
        return totalTargetsCol;
    }

    public void setTotalTargetsCol(String totalTargetsCol) {
        this.totalTargetsCol.set(totalTargetsCol);
    }


}
