package Controller;

public record CommandResponse(boolean successFlag, String message) {
    public static CommandResponse success() {
        return new CommandResponse(true, "Success.");
    }

    public static CommandResponse failure(String message) {
        return new CommandResponse(false, message);
    }
}
