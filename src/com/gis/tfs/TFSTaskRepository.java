package com.gis.tfs;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.project.Project;
import com.intellij.tasks.TaskRepository;
import com.intellij.tasks.TaskRepositoryType;
import com.intellij.tasks.TaskState;
import com.intellij.tasks.config.TaskRepositoryEditor;
import com.intellij.util.Consumer;
import icons.TFSIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.util.EnumSet;

/**
 *
 */
public class TFSTaskRepository extends  TaskRepositoryType<GisTaskRepository> {

    TFSTaskRepository(){
        System.setProperty("com.microsoft.tfs.jni.native.base-directory", PathManager.getLibPath() + File.separator+"native");
    }

    @NotNull
    @Override
    public String getName() {
        return "TFS.GiS";
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return TFSIcons.TFSIcon;
    }

    @NotNull
    @Override
    public TaskRepositoryEditor createEditor(GisTaskRepository gisTaskRepository, Project project, Consumer<GisTaskRepository> consumer) {
            return new TFSRepositoryEditor(project, gisTaskRepository, consumer);
    }

    @NotNull
    @Override
    public TaskRepository createRepository() {
        return new GisTaskRepository(this);
    }

    @Override
    public Class<GisTaskRepository> getRepositoryClass() {
        return GisTaskRepository.class;
    }

    @Override
    public EnumSet<TaskState> getPossibleTaskStates() {
        return EnumSet.of(TaskState.SUBMITTED, TaskState.OPEN, TaskState.RESOLVED, TaskState.OTHER);
    }


}
