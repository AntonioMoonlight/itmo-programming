package Controller;

import Model.MusicBand;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
public class CollectionManager {
    private ArrayDeque<MusicBand> deque = new ArrayDeque<>();
    private final IdGenerator idGen = new IdGenerator(0);
    private LocalDateTime initDate;

    public void clear() {
        deque.clear();
    }

    public void sort() {
        if (deque == null) return;
        deque = deque.stream()
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toCollection(ArrayDeque::new));
    }
    public List<String> init(Collection<MusicBand> bands) {
        initDate = LocalDateTime.now();
        List<String> messages = new ArrayList<>();
        int idCounter = 0;
        if (bands == null) return messages;

        for (MusicBand band : bands) {
            idCounter = Math.max(idCounter, band.getId());
            String msg = "Id " + band.getId() + ": " + band.validate();
            messages.add(msg);
            if (band.validate().isValid()) {
                deque.addLast(band);
            }
        }
        idGen.setCounter(idCounter);
        sort();
        return messages;
    }
    public String add(MusicBand band) {
        String msg = "Id " + band.getId() + ": " + band.validate();
        if (!hasUniqueId(band)) {
            msg += "\nElement does not have unique ID!";
            return msg;
        }
        if (band.validate().isValid()) {
            deque.addLast(band);
            sort();
        }
        return msg;
    }

    public boolean hasUniqueId(MusicBand band) {
        return deque.stream()
                .filter(b -> b != band)
                .noneMatch(b -> b.getId() == band.getId());
    }

    public boolean removeById(int id) {
        boolean newDeque = deque.removeIf(band -> band.getId() == id);
        sort();
        return newDeque;
    }

    public void remove(MusicBand band) {
        deque.remove(band);
        sort();
    }

    public void update(int id, MusicBand newBand) {
        MusicBand copy = new MusicBand(newBand);
        idGen.assignId(id, copy);
        for (MusicBand band : deque) {
            if (band.getId() == id) {
                deque.remove(band);
                add(copy);
            }
        }
        sort();
    }

    public ArrayDeque<MusicBand> getDeque() {
        return deque;
    }

    public IdGenerator getIdGenerator() {
        return idGen;
    }

    public LocalDateTime getInitDate() {
        return initDate;
    }}