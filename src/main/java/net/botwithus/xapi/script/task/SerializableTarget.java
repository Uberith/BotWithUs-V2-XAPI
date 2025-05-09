package net.botwithus.xapi.script.task;

import com.google.gson.JsonObject;

public interface SerializableTarget<T> {
    /**
     * Serializes the target to a JSON object.
     *
     * @return a JsonObject representation of the target.
     */
    JsonObject serialize();

    /**
     * Deserializes the target from a JSON object.
     *
     * @param json the JsonObject representing the target.
     */
    default T deserialize(JsonObject json) {
        throw new UnsupportedOperationException("Must be implemented by the concrete target class");
    }
}