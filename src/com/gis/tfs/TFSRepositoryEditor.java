package com.gis.tfs;

import com.intellij.openapi.project.Project;
import com.intellij.tasks.config.BaseRepositoryEditor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.Consumer;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 *
 */
public class TFSRepositoryEditor extends BaseRepositoryEditor<GisTaskRepository> {

    private JTextField projectId;
    private JTextField selectAllQuery;

    public TFSRepositoryEditor(Project project, GisTaskRepository repository, Consumer<GisTaskRepository> changeListener) {
        super(project, repository, changeListener);

    }

    @Override
    protected JComponent createCustomPanel() {
        projectId = new JTextField();
        projectId.setText(myRepository == null ? GisTaskRepository.DEFAULT_PROJECT : myRepository.getProjectId());
        JBLabel myProjectLabel = new JBLabel("Project ID:", SwingConstants.RIGHT);
        myProjectLabel.setLabelFor(projectId);

        selectAllQuery = new JTextField();
        selectAllQuery.setText(myRepository == null ? GisTaskRepository.DEFAULT_QUERY : myRepository.getQueryAll());
        JBLabel myselectAllQueryLabel = new JBLabel("Issues query:", SwingConstants.RIGHT);
        myselectAllQueryLabel.setLabelFor(projectId);

        return new FormBuilder()
                .addLabeledComponent(myProjectLabel, projectId)
                .addLabeledComponent(myselectAllQueryLabel, selectAllQuery)
                .getPanel();
    }



    @Override
    public void apply() {
        super.apply();
        myRepository.setProjectId(projectId.getText().trim());
        myRepository.setQueryAll(selectAllQuery.getText().trim());
    }
}
