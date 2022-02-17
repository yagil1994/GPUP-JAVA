package util;

import com.google.gson.Gson;
public class Constants {

    // global constants
    public final static int REFRESH_RATE = 2000;
    public final static int FAST_REFRESH_RATE = 500;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";


    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    public final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";// http://localhost:8080
    public final static String CONTEXT_PATH = "/app";
    public final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;// http://localhost:8080/app
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/LoginServlet";// http://localhost:8080/app/LoginServlet
    public final static String USERS_LIST = FULL_SERVER_PATH + "/UserTableViewServlet";// http://localhost:8080/app/UserTableViewServlet
    public final static String GRAPHS_LIST = FULL_SERVER_PATH + "/GraphListServlet";// http://localhost:8080/app/GraphListServlet
    public final static String UPLOAD_XML_FILE=FULL_SERVER_PATH+"/UploadXmlFile";// http://localhost:8080/app/UploadXmlFile
    public final static String GRAPH_INFO_DTO=FULL_SERVER_PATH+"/GraphInfoDtoServlet";// http://localhost:8080/app/GraphInfoDtoServlet
    public final static String TARGET_INFO_FOR_TABLEVIEW_DTO=FULL_SERVER_PATH+"/TargetInfoForTableViewDtoServlet";//http://localhost:8080/app/TargetInfoForTableViewDtoServlet
    public final static String TARGET_INFO_DTO=FULL_SERVER_PATH+"/TargetInfoDtoServlet";// http://localhost:8080/app/TargetInfoDtoServlet
    public final static String TARGET_INFO_FOR_TABLEVIEW_DTO_ARRAY_SERVLET=FULL_SERVER_PATH+"/TargetInfoForTableViewDtoArrayServlet";//http://localhost:8080/app/TargetInfoForTableViewDtoArrayServlet
    public final static String CIRCLE_PATH_FOR_TARGET_DTO=FULL_SERVER_PATH+"/CirclePathForTargetDto";// http://localhost:8080/app//CirclePathForTargetDto
    public final static String ALL_DEPENDENCIES_OF_TARGET_DTO=FULL_SERVER_PATH+"/AllDependenciesOfTargetDto";//http://localhost:8080/app/AllDependenciesOfTargetDto
    public final static String ALL_PATHS_OF_TWO_TARGETS_DTO=FULL_SERVER_PATH+"/AllPathsOfTwoTargetsDto";//http://localhost:8080/app/AllPathsOfTwoTargetsDto
    public final static String GET_ALL_TARGETS_NAMES_IN_SET_FROM_SERVER=FULL_SERVER_PATH+"/GetAllTargetsNamesInStrArr";//http://localhost:8080/app/GetAllTargetsNamesInStrArr
    public final static String GET_TARGETS_NAMES_IN_GRAPH_TASK_ONLY=FULL_SERVER_PATH+"/GetTargetsNamesInGraphTaskOnly";//http://localhost:8080/app/GetTargetsNamesInGraphTaskOnly
    public final static String GET_GRAPH_PRICE_PER_TARGET_ACCORDING_TO_TASK_TYPE=FULL_SERVER_PATH+"/GetGraphPricePerTargetAccordingToTaskType";//http://localhost:8080/app/GetGraphPricePerTargetAccordingToTaskType
    public final static String SEND_NEW_MISSION_TO_SERVER=FULL_SERVER_PATH+"/SendNewMissionToServer";//http://localhost:8080/app/SendNewMissionToServer
    public final static String ADMIN_TASKS_ROWS = FULL_SERVER_PATH + "/AdminTasksRows";// http://localhost:8080/app/AdminTasksRows
    public final static String WORKER_TASKS_ROWS = FULL_SERVER_PATH + "/WorkerTasksRows";// http://localhost:8080/app/WorkerTasksRows
    public final static String REGISTER_WORKER_TO_TASKS = FULL_SERVER_PATH + "/RegisterWorkerToTasks";// http://localhost:8080/app/RegisterWorkerToTasks
    public final static String  CHECK_IF_THERE_IS_WORK= FULL_SERVER_PATH + "/CheckIfThereIsWork";// http://localhost:8080/app/CheckIfThereIsWork
    public final static String  CHANGE_MISSION_STATUS= FULL_SERVER_PATH + "/ChangeMissionStatus";// http://localhost:8080/app/ChangeMissionStatus
    public final static String  UPDATE_TARGET_IN_PROCESS= FULL_SERVER_PATH + "/UpdateTargetInProcess";// http://localhost:8080/app/UpdateTargetInProcess
    public final static String  UPDATE_AFTER_TARGET_DID_TASK= FULL_SERVER_PATH + "/UpdateAfterTargetDidTask";// http://localhost:8080/app/UpdateAfterTargetDidTask
    public final static String  UPDATE_SERVER_IN_TARGETS_THAT_DID_NOT_DID_TASK= FULL_SERVER_PATH + "/UpdateServerInTargetsThatDidNotDidTask";// http://localhost:8080/app/UpdateServerInTargetsThatDidNotDidTask
    public final static String  TELL_SERVER_THAT_WORKER_UNREGISTER_MISSION= FULL_SERVER_PATH + "/TellServerThatWorkerUnregisterMission";// http://localhost:8080/app/TellServerThatWorkerUnregisterMission
    public final static String  UPDATE_ADMIN_MISSION_DATA= FULL_SERVER_PATH + "/UpdateAdminMissionData";// http://localhost:8080/app/UpdateAdminMissionData
    public final static String  UPDATE_ADMIN_TARGETS_DATA= FULL_SERVER_PATH + "/UpdateAdminTargetsData";// http://localhost:8080/app/UpdateAdminTargetsData
    public final static String  UPDATE_LOGS_INFO= FULL_SERVER_PATH + "/UpdateLogsInfo";// http://localhost:8080/app/UpdateLogsInfo
    public final static String  UPDATE_TARGET_LIST_VIEW_DETAILS= FULL_SERVER_PATH + "/UpdateTargetListViewDetails";// http://localhost:8080/app/UpdateTargetListViewDetails
    public final static String  ASK_FOR_TASK_REPORT= FULL_SERVER_PATH + "/AskForTaskReport";// http://localhost:8080/app/AskForTaskReport
    public final static String  RUN_MISSION_AGAIN= FULL_SERVER_PATH + "/RunMissionAgain";// http://localhost:8080/app/RunMissionAgain
    public final static String SEND_MESSAGE = FULL_SERVER_PATH + "/SendMessage";//http://localhost:8080/app/SendMessage
    public final static String UPDATE_CHAT = FULL_SERVER_PATH + "/UpdateChat";//http://localhost:8080/app/UpdateChat


    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
