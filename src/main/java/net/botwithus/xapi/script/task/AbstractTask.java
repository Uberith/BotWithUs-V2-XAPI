package net.botwithus.xapi.script.task;

import com.google.gson.JsonObject;
import lombok.Getter;

public abstract class AbstractTask<T extends SerializableTarget> implements Task {

    @Getter
    private final T target; // Generic target, like Hotspot, Tree, Rock, etc.

    private int currentCount;

    private final int completeCount;

    private boolean isComplete;


    /***
     * Constructor to initialize a task with specific parameters.
     * @param target
     * @param currentCount
     * @param completeCount
     */
    public AbstractTask(T target, int currentCount, int completeCount) {
        this.target = target;
        this.currentCount = currentCount;
        this.completeCount = completeCount;
        this.isComplete = false;
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    @Override
    public int getCurrentCount() {
        return currentCount;
    }

    @Override
    public void setCurrentCount(int count) {
        currentCount = count;
    }

    @Override
    public int getCompleteCount() {
        return completeCount;
    }

    /***
     * Checks if the task is complete.
     * @return true if the task is complete, otherwise false
     */
    @Override
    public boolean incrementProgress() {
        return incrementProgress(1);
    }

    /***
     * Increment the current progress count by the specified amount.
     * @param count
     * @return true if the task is complete, otherwise false
     */
    @Override
    public boolean incrementProgress(int count) {
        if (!isComplete) {
            currentCount += count;
            if (currentCount >= completeCount) {
                isComplete = true;
            }
            return true;
        }
        return false;
    }

    /***
     * Serialize the task to a JSON object.
     * Handle target serialization in concrete classes if necessary - Will use toString() by default.
     */
    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.addProperty("currentCount", currentCount);
        json.addProperty("completeCount", completeCount);
        json.addProperty("isComplete", isComplete);
        json.add("target", target.serialize());
        return json;
    }

    /***
     * Deserialize the task from a JSON object.
     * Handle target deserialization in concrete classes.
     * @param json
     */
    @Override
    public void deserialize(JsonObject json) {
        this.currentCount = json.get("currentCount").getAsInt();
        this.isComplete = json.get("isComplete").getAsBoolean();

        // Deserialize the target using its deserialization method
        JsonObject targetJson = json.getAsJsonObject("target");
        this.target.deserialize(targetJson);
    }
}