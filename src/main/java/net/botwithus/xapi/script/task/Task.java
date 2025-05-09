package net.botwithus.xapi.script.task;

import com.google.gson.JsonObject;

public interface Task {

    boolean isComplete();

    void setComplete(boolean complete);

    int getCurrentCount();

    void setCurrentCount(int count);

    int getCompleteCount();

    boolean incrementProgress();
    boolean incrementProgress(int count);

    JsonObject serialize();

    void deserialize(JsonObject json);
}
