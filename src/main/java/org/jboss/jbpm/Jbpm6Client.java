package org.jboss.jbpm;

import java.util.Map;
import org.apache.http.HttpException;

public interface Jbpm6Client {
  public enum TasksBy{potentialOwner, taskOwner, processInstanceId, taskId, workItemId}
  
  public String getTasks(TasksBy by, String value) throws HttpException;
  public String startProcess(String deploymentId, String processId, String mapOfParams) throws HttpException;
  public String startTask(String id) throws HttpException;
  public String completeTask(String id, String commaSeparatedListOfParams) throws HttpException;
  public String completeTaskWithMap(String id, Map<String, String> params) throws HttpException;
}
