import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Event {
    private static final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String name;
    private String address;
    private Category category;
    private LocalDateTime start;
    private LocalDateTime end;
    private String description;
    private Set<String> participantsUsernames = new HashSet<>(); // store username strings

    public Event() {}

    public Event(String name, String address, Category category, LocalDateTime start, LocalDateTime end, String description) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.start = start;
        this.end = end;
        this.description = description;
    }

    // getters
    public String getName() { return name; }
    public String getAddress() { return address; }
    public Category getCategory() { return category; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
    public String getDescription() { return description; }
    public Set<String> getParticipantsUsernames() { return participantsUsernames; }

    public boolean isHappeningNow() {
        LocalDateTime now = LocalDateTime.now();
        return (start != null && end != null) && ( !now.isBefore(start) && now.isBefore(end) );
    }

    public boolean alreadyHappened() {
        LocalDateTime now = LocalDateTime.now();
        return end != null && now.isAfter(end);
    }

    public boolean addParticipant(String username) {
        return participantsUsernames.add(username);
    }

    public boolean removeParticipant(String username) {
        return participantsUsernames.remove(username);
    }

    public String serializeForFile() {
        // Use '|' as main separator. Replace any '|' in text by space to avoid corrupting file
        String safe = s -> s == null ? "" : s.replace("|", " ");
        String parts = String.join("|",
                safe.apply(name),
                safe.apply(address),
                category.name(),
                start.format(fmt),
                end.format(fmt),
                safe.apply(description),
                participantsUsernames.stream().collect(Collectors.joining(",")) // maybe empty
        );
        return parts;
    }

    public static Event fromSerialized(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 7) return null;
            String name = parts[0];
            String address = parts[1];
            Category cat = Category.fromString(parts[2]);
            LocalDateTime start = LocalDateTime.parse(parts[3], fmt);
            LocalDateTime end = LocalDateTime.parse(parts[4], fmt);
            String desc = parts[5];
            Event e = new Event(name, address, cat, start, end, desc);
            if (!parts[6].trim().isEmpty()) {
                String[] us = parts[6].split(",");
                for (String u : us) if (!u.trim().isEmpty()) e.participantsUsernames.add(u.trim());
            }
            return e;
        } catch (Exception ex) {
            System.err.println("Erro ao ler linha de event: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s -> %s\n%s\nParticipantes: %s",
                name, address, category.name(),
                start.format(fmt), end.format(fmt),
                description,
                participantsUsernames.isEmpty() ? "(nenhum)" : String.join(", ", participantsUsernames));
    }
}
