package com.gis.tfs;

import com.intellij.tasks.*;
import com.intellij.tasks.impl.BaseRepository;
import com.intellij.tasks.impl.BaseRepositoryImpl;
import com.intellij.util.xmlb.annotations.Tag;
import com.microsoft.tfs.core.TFSTeamProjectCollection;
import com.microsoft.tfs.core.clients.workitem.WorkItem;
import com.microsoft.tfs.core.clients.workitem.WorkItemClient;
import com.microsoft.tfs.core.clients.workitem.query.Query;
import com.microsoft.tfs.core.clients.workitem.query.WorkItemCollection;
import com.microsoft.tfs.core.httpclient.Credentials;
import com.microsoft.tfs.core.httpclient.UsernamePasswordCredentials;
import icons.TFSIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 */
@Tag("TFS.GiS")
public class GisTaskRepository extends BaseRepositoryImpl {

    public static final String DEFAULT_QUERY = "Select ID, Title, Description from WorkItems where [System.AssignedTo] = @me AND State <> 'Closed'";
    public static final String DEFAULT_PROJECT = "GiS.IDM";

    private String projectId = DEFAULT_PROJECT;

    private String queryAll = DEFAULT_QUERY;

    public GisTaskRepository() {
    }

    public GisTaskRepository(GisTaskRepository gisTaskRepository) {
        super(gisTaskRepository);
        setUsername(gisTaskRepository.getUsername());
        setPassword(gisTaskRepository.getPassword());
        setUseProxy(gisTaskRepository.myUseProxy);
        setUseHttpAuthentication(gisTaskRepository.myUseHttpAuthentication);
        setLoginAnonymously(gisTaskRepository.myLoginAnonymously);
        setUrl(gisTaskRepository.getUrl());
        setCommitMessageFormat(gisTaskRepository.getCommitMessageFormat());
        setRepositoryType(gisTaskRepository.getRepositoryType());
        setQueryAll(gisTaskRepository.getQueryAll());
        setProjectId(gisTaskRepository.getProjectId());
    }

    public GisTaskRepository(TFSTaskRepository taskRepo) {
        super(taskRepo);
    }

    @Nullable
    @Override
    public Task findTask(@NotNull String s) throws Exception {
        return null;
    }

    @NotNull
    @Override
    public BaseRepository clone() {
        return new GisTaskRepository(this);
    }

    @Override
    public Task[] getIssues(@Nullable String query, int max, long since) throws Exception {
        TFSTeamProjectCollection tpc;

        Credentials credentials = new UsernamePasswordCredentials(myUsername, myPassword);
        URL uri = new URL(getUrl());
        tpc = new TFSTeamProjectCollection(uri.toURI(), credentials);
        tpc.authenticate();

        try {
            WorkItemClient workItemClient = tpc.getWorkItemClient();
            Query query2 = workItemClient.createQuery(getQueryAll());
            WorkItemCollection workItemCollection = query2.runQuery();

            ArrayList<Task> tasks = new ArrayList<>();
            for (int i = 0; i < workItemCollection.size(); i++) {
                WorkItem w = workItemCollection.getWorkItem(i);
                tasks.add(createTask(Integer.toString(w.getID()), w.getTitle(), (String) w.getFields().getField("Description").getValue()));
            }
            return tasks.toArray(new Task[tasks.size()]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void setTaskState(@NotNull Task task, @NotNull CustomTaskState state) throws Exception {
        Credentials credentials = new UsernamePasswordCredentials(myUsername, myPassword);
        URL uri = new URL(getUrl());
        TFSTeamProjectCollection tpc = new TFSTeamProjectCollection(uri.toURI(), credentials);
        tpc.authenticate();

        WorkItemClient workItemClient = tpc.getWorkItemClient();
        WorkItem workItem = workItemClient.getWorkItemByID(Integer.getInteger(task.getId()));
        if (workItem != null){
            if (state.equals(CustomTaskState.fromPredefined(TaskState.IN_PROGRESS))) {
                workItem.getFields().getField("Status").setValue("Active");
                workItem.save();
            } else if (state.equals(CustomTaskState.fromPredefined(TaskState.RESOLVED))){
                workItem.getFields().getField("Status").setValue("Closed");
                workItem.save();
            }
        }


    }

    @Nullable
    private Task createTask(final String ID, final String Title, final String Description) {

        return new Task() {
            @Override
            public boolean isIssue() {
                return true;
            }

            @Nullable
            @Override
            public String getIssueUrl() {
                return getUrl()+"/"+(getProjectId().isEmpty() ? DEFAULT_PROJECT : getProjectId())+"/_workitems#_a=edit&id="+ID;
            }

            @NotNull
            @Override
            public String getId() {
                return ID;
            }

            @NotNull
            @Override
            public String getSummary() {
                return Title;
            }

            public String getDescription() {
                return Description;
            }

            @NotNull
            @Override
            public Comment[] getComments() {
                return new Comment[0];
            }

            @NotNull
            @Override
            public Icon getIcon() {
                return TFSIcons.TFSIcon;
            }

            @NotNull
            @Override
            public TaskType getType() {
                return TaskType.OTHER;
            }

            @Nullable
            @Override
            public Date getUpdated() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Nullable
            @Override
            public Date getCreated() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isClosed() {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }


            @Override
            public TaskRepository getRepository() {
                return GisTaskRepository.this;
            }

            @Override
            public String getPresentableName() {
                return getId() + ": " + getSummary();
            }
        };
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getQueryAll() {
        return queryAll;
    }

    public void setQueryAll(String queryAll) {
        this.queryAll = queryAll;
    }
}
