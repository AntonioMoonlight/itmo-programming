package Controller;

import Controller.JsonAdapters.DateAdapter;
import Controller.JsonAdapters.LocalDateTimeAdapter;
import View.ConsoleView;
import com.google.gson.Gson;
import Model.MusicBand;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Date;

public class FileManager {
    private final String FILE_PATH;
    private final CollectionManager collectionManager;
    private final ConsoleView consoleView;

    private final TypeToken<ArrayDeque<MusicBand>> collectionType = new TypeToken<ArrayDeque<MusicBand>>(){};

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Date.class, new DateAdapter())
            .setPrettyPrinting()
            .create();
    public FileManager(String FILE_PATH, CollectionManager collectionManager, ConsoleView consoleView) {
        this.FILE_PATH = FILE_PATH;
        this.collectionManager = collectionManager;
        this.consoleView = consoleView;
    }

    public void writeCollection() throws FileNotFoundException {
        ArrayDeque<MusicBand> deque = collectionManager.getDeque();
        try (FileOutputStream outputStream = new FileOutputStream(FILE_PATH)) {
            byte[] bytes = gson.toJson(deque).getBytes();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException | NullPointerException e) {
            consoleView.println("IO error! Perhaps, the file with data does not exist or cannot be written into.");
        }
    }

    public ArrayDeque<MusicBand> readCollection() {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader inputReader = new InputStreamReader(new FileInputStream(FILE_PATH), StandardCharsets.UTF_8)) {
            char[] buffer = new char[1024];
            int numCharsRead;

            while ((numCharsRead = inputReader.read(buffer)) != -1) {
                sb.append(buffer,0,numCharsRead);
            }
        } catch (IOException | NullPointerException e) {
            consoleView.println("IO error! Perhaps, the file with data does not exist or cannot be read.");
        }

        String json = sb.toString();
        try {
            return gson.fromJson(json, collectionType);
        } catch (JsonParseException e) {
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            consoleView.println("JSON parse error: " + cause.getMessage());
        } catch (RuntimeException e) {
            consoleView.println("Unexpected JSON error: " + e.getMessage());
        }
        return new ArrayDeque<>();
    }
}
