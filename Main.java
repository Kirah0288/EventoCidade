import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String STORAGE_FILE = "events.data";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        EventManager manager = new EventManager(STORAGE_FILE);

        System.out.println("=== Sistema de Eventos (Console) ===");
        User currentUser = null;

        while (true) {
            System.out.println("\nMenu principal:");
            System.out.println("1) Cadastrar usuário");
            System.out.println("2) Login (por username)");
            System.out.println("3) Criar evento");
            System.out.println("4) Listar todos os eventos");
            System.out.println("5) Listar próximos eventos (ordenados)");
            System.out.println("6) Listar eventos passados");
            System.out.println("7) Participar de evento");
            System.out.println("8) Minhas participações");
            System.out.println("9) Cancelar participação");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");
            String op = sc.nextLine().trim();

            try {
                switch (op) {
                    case "1":
                        System.out.print("Nome: ");
                        String name = sc.nextLine();
                        System.out.print("Username (único): ");
                        String uname = sc.nextLine();
                        System.out.print("Email: ");
                        String mail = sc.nextLine();
                        User u = new User(name, uname, mail);
                        if (manager.registerUser(u)) {
                            System.out.println("Usuário cadastrado com sucesso.");
                        } else {
                            System.out.println("Username já existe. Tente outro.");
                        }
                        break;
                    case "2":
                        System.out.print("Digite username: ");
                        String lu = sc.nextLine().trim();
                        User uu = manager.getUser(lu);
                        if (uu == null) {
                            System.out.println("Usuário não encontrado. Cadastre primeiro.");
                        } else {
                            currentUser = uu;
                            System.out.println("Logado como: " + currentUser);
                        }
                        break;
                    case "3":
                        if (currentUser == null) {
                            System.out.println("Faça login antes de criar eventos.");
                            break;
                        }
                        System.out.print("Nome do evento: ");
                        String en = sc.nextLine();
                        System.out.print("Endereço: ");
                        String ea = sc.nextLine();
                        System.out.println("Categorias: " + Category.all());
                        System.out.print("Categoria (digite exatamente): ");
                        String ec = sc.nextLine();
                        Category cat = Category.fromString(ec);
                        System.out.println("Formato de data/hora: " + LocalDateTime.now().format(fmt) + " (use este formato: yyyy-MM-ddTHH:mm)");
                        System.out.print("Data/hora de início (ex: 2025-09-25T20:30): ");
                        LocalDateTime start = LocalDateTime.parse(sc.nextLine().trim(), fmt);
                        System.out.print("Data/hora de término (ex: 2025-09-25T23:00): ");
                        LocalDateTime end = LocalDateTime.parse(sc.nextLine().trim(), fmt);
                        System.out.print("Descrição: ");
                        String desc = sc.nextLine();
                        Event e = new Event(en, ea, cat, start, end, desc);
                        manager.addEvent(e);
                        System.out.println("Evento criado e salvo.");
                        break;
                    case "4":
                        List<Event> all = manager.getAllEvents();
                        if (all.isEmpty()) {
                            System.out.println("Nenhum evento cadastrado.");
                        } else {
                            for (int i = 0; i < all.size(); i++) {
                                Event ev = all.get(i);
                                System.out.println("[" + i + "] " + ev.getName() + " | " + ev.getCategory() + " | " + ev.getStart().format(fmt) + " -> " + ev.getEnd().format(fmt)
                                        + (ev.isHappeningNow() ? " [OCORRENDO AGORA]" : ev.alreadyHappened() ? " [JÁ OCORREU]" : ""));
                            }
                        }
                        break;
                    case "5":
                        List<Event> up = manager.getUpcomingSorted();
                        if (up.isEmpty()) {
                            System.out.println("Sem próximos eventos.");
                        } else {
                            for (int i = 0; i < up.size(); i++) {
                                Event ev = up.get(i);
                                System.out.println("[" + i + "] " + ev.getName() + " | " + ev.getCategory() + " | " + ev.getStart().format(fmt) + " -> " + ev.getEnd().format(fmt)
                                        + (ev.isHappeningNow() ? " [OCORRENDO AGORA]" : ""));
                            }
                        }
                        break;
                    case "6":
                        List<Event> past = manager.getPastEvents();
                        if (past.isEmpty()) {
                            System.out.println("Nenhum evento passado.");
                        } else {
                            for (int i = 0; i < past.size(); i++) {
                                Event ev = past.get(i);
                                System.out.println("[" + i + "] " + ev.getName() + " | " + ev.getCategory() + " | " + ev.getStart().format(fmt));
                            }
                        }
                        break;
                    case "7":
                        if (currentUser == null) {
                            System.out.println("Faça login antes de participar.");
                            break;
                        }
                        List<Event> eventsToJoin = manager.getAllEvents();
                        if (eventsToJoin.isEmpty()) {
                            System.out.println("Nenhum evento disponível.");
                            break;
                        }
                        for (int i = 0; i < eventsToJoin.size(); i++) {
                            Event ev = eventsToJoin.get(i);
                            System.out.println("[" + i + "] " + ev.getName() + " | " + ev.getStart().format(fmt) + (ev.isHappeningNow()? " [OCORRENDO AGORA]":""));
                        }
                        System.out.print("Digite o índice do evento para participar: ");
                        int idx = Integer.parseInt(sc.nextLine());
                        if (idx < 0 || idx >= eventsToJoin.size()) {
                            System.out.println("Índice inválido.");
                        } else {
                            Event ev = eventsToJoin.get(idx);
                            manager.joinEvent(currentUser, ev);
                            System.out.println("Participação confirmada.");
                        }
                        break;
                    case "8":
                        if (currentUser == null) {
                            System.out.println("Faça login antes.");
                            break;
                        }
                        List<Event> mine = manager.eventsForUser(currentUser);
                        if (mine.isEmpty()) {
                            System.out.println("Você não confirmou presença em nenhum evento.");
                        } else {
                            for (int i = 0; i < mine.size(); i++) {
                                Event ev = mine.get(i);
                                System.out.println("[" + i + "] " + ev.getName() + " | " + ev.getStart().format(fmt) + " -> " + ev.getEnd().format(fmt)
                                        + (ev.isHappeningNow() ? " [OCORRENDO AGORA]" : ev.alreadyHappened() ? " [JÁ OCORREU]" : ""));
                            }
                        }
                        break;
                    case "9":
                        if (currentUser == null) {
                            System.out.println("Faça login antes.");
                            break;
                        }
                        List<Event> myEv = manager.eventsForUser(currentUser);
                        if (myEv.isEmpty()) {
                            System.out.println("Nenhum evento para cancelar.");
                            break;
                        }
                        for (int i = 0; i < myEv.size(); i++) {
                            Event ev = myEv.get(i);
                            System.out.println("[" + i + "] " + ev.getName() + " | " + ev.getStart().format(fmt));
                        }
                        System.out.print("Digite índice do evento para cancelar participação: ");
                        int idc = Integer.parseInt(sc.nextLine());
                        if (idc < 0 || idc >= myEv.size()) {
                            System.out.println("Índice inválido.");
                        } else {
                            Event ev = myEv.get(idc);
                            manager.cancelParticipation(currentUser, ev);
                            System.out.println("Participação cancelada.");
                        }
                        break;
                    case "0":
                        System.out.println("Encerrando. Até mais!");
                        sc.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (Exception ex) {
                System.out.println("Erro: " + ex.getMessage());
            }
        }
    }
}
