package com.flr.actions;

import com.flr.logConsole.FlrLogConsole;
import com.flr.logConsole.FlrLogConsoleFactory;
import com.flr.FlrApp;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class FlrGenerateAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);

        FlrLogConsole flrLogConsole = FlrLogConsoleFactory.createLogConsole(project);
        FlrLogConsoleFactory.showCurLogConsole(project);

        FlrApp flrApp = project.getComponent(FlrApp.class);

        // 如果当前资源变化监控服务正在运行，则不清空当前日志，否则就清空
        if(flrApp.flrCommand.isMonitoringAssets == false) {
            flrLogConsole.clear();
        }

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Flr Generate", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                flrApp.flrCommand.generateAll(e, flrLogConsole);

                // 如果当前资源变化监控服务正在运行，则在执行 generate 后，打印监控服务在运行的提示
                if(flrApp.flrCommand.isMonitoringAssets) {
                    FlrLogConsole.LogType indicatorType = FlrLogConsole.LogType.normal;
                    flrLogConsole.println("", indicatorType);
                    String indicatorMessage =
                            "[*]: the monitoring service is monitoring the asset changes, and then auto scan assets, specifies assets and generates \"r.g.dart\" ...\n" +
                                    "[*]: you can click menu \"Tools-Flr-Stop Monitor\" to terminate it\n";
                    flrLogConsole.println(indicatorMessage, indicatorType);
                }
            }
        });
    }
}
