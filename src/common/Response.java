package common;

import java.io.Serializable;
import java.util.Collection;

/**
 * Represents a response that can be sent from server to client
 */
public class Response implements Serializable {
    private final boolean success;
    private final String message;
    private final Collection<MusicBand> data; // For commands that return collection data

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    public Response(boolean success, String message, Collection<MusicBand> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Collection<MusicBand> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + (data != null ? data.size() + " items" : "null") +
                '}';
    }
}