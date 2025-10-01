package server;

import server.jsonadapters.DateAdapter;
import server.jsonadapters.LocalDateTimeAdapter;
import client.ConsoleView;
import com.google.gson.Gson;
import common.MusicBand;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Date;

public class FileManager {
    private final Path filePath;
    private final CollectionManager collectionManager;
    private final ConsoleView consoleView;

    private final TypeToken<ArrayDeque<MusicBand>> collectionType = new TypeToken<>(){};

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Date.class, new DateAdapter())
            .setPrettyPrinting()
            .create();
    public FileManager(Path filePath, CollectionManager collectionManager, ConsoleView consoleView) {
        this.filePath = filePath;
        this.collectionManager = collectionManager;
        this.consoleView = consoleView;
    }

    public void writeCollection() throws FileNotFoundException {
        ArrayDeque<MusicBand> deque = collectionManager.getDeque();
        try (FileOutputStream outputStream = new FileOutputStream(String.valueOf(filePath))) {
            byte[] bytes = gson.toJson(deque).getBytes();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException | NullPointerException e) {
            consoleView.println("IO error! Perhaps, the file with data does not exist or cannot be written into.");
        }
    }

    public ArrayDeque<MusicBand> readCollection() {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader inputReader = new InputStreamReader(new FileInputStream(String.valueOf(filePath)), StandardCharsets.UTF_8)) {
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
