package net.botwithus.xapi.script.ui;

import net.botwithus.imgui.ImGui;
import net.botwithus.ui.workspace.Workspace;
import net.botwithus.xapi.script.BwuScript;
import net.botwithus.xapi.util.StringUtils;
import net.botwithus.xapi.util.collection.Pair;
import net.botwithus.xapi.util.statistic.XPInfo;
import net.botwithus.xapi.util.time.DurationStringFormat;
import net.botwithus.xapi.util.time.Timer;

public class BwuGraphicsContext {
    private final BwuScript script;
    private final Workspace workspace;

    public BwuGraphicsContext(BwuScript script, Workspace workspace) {
        this.script = script;
        this.workspace = workspace;

        this.workspace.setName(script.getInfo().name());
    }

    public void draw() {

        if (ImGui.begin(script.getInfo().name() + " Settings | " + script.getInfo().version(), 0)) {
//            ImGui.pushStyleColor(5, 0.322f, 0.494f,0.675f, 0.400f);
//            ImGui.pushStyleColor(7, 0.322f, 0.494f,0.675f, 0.200f);
//            ImGui.pushStyleColor(18, 0.322f, 0.494f,0.720f, 0.800f);
//            ImGui.pushStyleColor(21, 0.322f, 0.494f,0.675f, 0.400f);

            if (ImGui.beginTabBar("Bot", 0)) {
                if (ImGui.beginTabItem("Config", 0)) {
                    script.onDrawConfig(workspace);
                    ImGui.endTabItem();
                }
                if (ImGui.beginTabItem("Stats", 0)) {
                    if (script.botStatInfo != null) {
                        if (script.botStatInfo.xpInfoMap != null && !script.botStatInfo.xpInfoMap.isEmpty()) {
                            for (var key : script.botStatInfo.xpInfoMap.keySet()) {
                                XPInfo model = script.botStatInfo.xpInfoMap.get(key);
                                var pairs = model.getPairList(script.STOPWATCH);
                                if (!pairs.isEmpty()) {
                                    ImGui.separatorText(StringUtils.toTitleCase(model.getSkillsType().name()));
                                    for (Pair<String, String> infoUI : pairs)
                                        ImGui.text(infoUI.getLeft() + infoUI.getRight());
                                }
                            }
                            ImGui.separator();
                        }
                        if (script.botStatInfo.displayInfoMap != null && !script.botStatInfo.displayInfoMap.isEmpty()) {
                            for (String key : script.botStatInfo.displayInfoMap.keySet()) {
                                ImGui.text(key + script.botStatInfo.displayInfoMap.get(key));
                            }
                            ImGui.separator();
                        }

                        ImGui.text("Runtime: " + Timer.secondsToFormattedString(script.STOPWATCH.elapsed() / 1000, DurationStringFormat.CLOCK));
                        ImGui.text("Current Task: " + script.getCurrentState().getStatus());
                    }
                    ImGui.endTabItem();
                }
            }
            ImGui.endTabBar();

//            ImGui.popStyleColor(4);
        }
        ImGui.end();
    }

}
