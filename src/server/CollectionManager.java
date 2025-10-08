package server;

import common.MusicBand;
import common.MusicGenre;

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
        // If band has ID 0 (from client), assign a new ID
        if (band.getId() == 0) {
            band.setId(idGen.next());
        }
        
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
        boolean removed = deque.removeIf(band -> band.getId() == id);
        sort();
        return removed;
    }

    public void remove(MusicBand band) {
        deque.remove(band);
        sort();
    }

    public void update(int id, MusicBand newBand) {
        MusicBand copy = new MusicBand(newBand);
        idGen.assignId(id, copy);
        
        // Use Stream API to find and replace the band
        boolean found = deque.stream()
                .filter(band -> band.getId() == id)
                .findFirst()
                .map(band -> {
                    deque.remove(band);
                    add(copy);
                    return true;
                })
                .orElse(false);
        
        if (found) {
            sort();
        }
    }

    // Stream API methods for collection operations
    public long countByLabel(String labelName) {
        return deque.stream()
                .filter(band -> band.getLabel().getName().equals(labelName))
                .count();
    }

    public int sumOfNumberOfParticipants() {
        return deque.stream()
                .mapToInt(MusicBand::getNumberOfParticipants)
                .sum();
    }

    public List<MusicBand> filterLessThanGenre(MusicGenre genre) {
        return deque.stream()
                .filter(band -> band.getGenre().ordinal() < genre.ordinal())
                .sorted()
                .collect(Collectors.toList());
    }

    public Optional<MusicBand> getFirst() {
        return deque.stream().findFirst();
    }

    public Optional<MusicBand> findById(int id) {
        return deque.stream()
                .filter(band -> band.getId() == id)
                .findFirst();
    }

    public long removeLower(MusicBand reference) {
        long removedCount = deque.stream()
                .filter(band -> band.compareTo(reference) < 0)
                .count();
        
        deque.removeIf(band -> band.compareTo(reference) < 0);
        sort();
        return removedCount;
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