package org.jboss.jbpm;

import org.jboss.jbpm.api.Jbpm6Client.TasksBy;
import org.jboss.jbpm.impl.Jbpm6ClientImpl;
import org.jboss.jbpm.impl.Jbpm6ClientObjects;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Jbpm6ClientITest {
  private static final String server="http://localhost:8080/business-central/";
  private static final String username="mat";
  private static final String password="adminmonk3y!";
  private Jbpm6ClientImpl client;
  private Jbpm6ClientObjects clientObjects;
  
  @Before
  public void init(){
    client=new Jbpm6ClientImpl(server, username, password, true);
    clientObjects=new Jbpm6ClientObjects(server,username,password);
  }
  
  @Test
  public void test() throws Exception{
    Assert.assertTrue(client.getTasks(TasksBy.potentialOwner,"mat").contains("task-summary-list"));
    Assert.assertTrue(clientObjects.getTasks(TasksBy.potentialOwner,"mat").size()==0);
  }
  
}
