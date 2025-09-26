import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class EventManager {
    private List<Event> events = new ArrayList<>();
    private Map<String, User> users = new HashMap<>();
    private final Path storage;

    public EventManager(String filename) {
        this.storage = Paths.get(filename);
        loadFromFile();
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(events);
    }

    public List<Event> getUpcomingSorted() {
        LocalDateTime now = LocalDateTime.now();
        return events.stream()
                .filter(e -> !e.alreadyHappened())
                .sorted(Comparator.comparing(Event::getStart))
                .collect(Collectors.toList());
    }

    public List<Event> getPastEvents() {
        return events.stream()
                .filter(Event::alreadyHappened)
                .sorted(Comparator.comparing(Event::getStart).reversed())
                .collect(Collectors.toList());
    }

    public void addEvent(Event e) {
        events.add(e);
        saveToFile();
    }

    public void removeEvent(Event e) {
        events.remove(e);
        saveToFile();
    }

    public boolean registerUser(User u) {
        if (users.containsKey(u.getUsername())) return false;
        users.put(u.getUsername(), u);
        return true;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void joinEvent(User u, Event e) {
        e.addParticipant(u.getUsername());
        saveToFile();
    }

    public void cancelParticipation(User u, Event e) {
        e.removeParticipant(u.getUsername());
        saveToFile();
    }

    public List<Event> eventsForUser(User u) {
        return events.stream()
                .filter(e -> e.getParticipantsUsernames().contains(u.getUsername()))
                .sorted(Comparator.comparing(Event::getStart))
                .collect(Collectors.toList());
    }

    private void loadFromFile() {
        events.clear();
        try {
            if (!Files.exists(storage)) {
                Files.createFile(storage);
                return;
            }
            List<String> lines = Files.readAllLines(storage);
            for (String l : lines) {
                if (l.trim().isEmpty()) continue;
                Event e = Event.fromSerialized(l);
                if (e != null) events.add(e);
            }
        } catch (IOException ex) {
            System.err.println("Erro ao carregar events.data: " + ex.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter bw = Files.newBufferedWriter(storage)) {
            for (Event e : events) {
                bw.write(e.serializeForFile());
                bw.newLine();
            }
        } catch (IOException ex) {
            System.err.println("Erro ao salvar events.data: " + ex.getMessage());
        }
    }
}
