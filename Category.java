public enum Category {
    FESTA,
    ESPORTES,
    SHOW,
    CONFERENCIA,
    TEATRO,
    OUTROS;

    public static Category fromString(String s) {
        try {
            return Category.valueOf(s.trim().toUpperCase());
        } catch (Exception e) {
            return OUTROS;
        }
    }

    public static String all() {
        StringBuilder sb = new StringBuilder();
        for (Category c : Category.values()) {
            sb.append(c.name()).append(" ");
        }
        return sb.toString().trim();
    }
}