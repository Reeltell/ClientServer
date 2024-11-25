import java.io.*;
import java.net.*;
import java.util.Scanner;

class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        startClient();
    }

    private static void startClient() {
        while (true) {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("Подключено к серверу чата");

                // Поток для получения сообщений от сервера
                new Thread(() -> {
                    try (BufferedWriter logWriter = new BufferedWriter(new FileWriter("chat_log.txt", true))) {
                        String response;
                        while ((response = in.readLine()) != null) {
                            System.out.println("\n" + response);
                            logWriter.write(response + System.lineSeparator());
                        }
                    } catch (IOException ignored) {
                    }
                }).start();

                // Основной цикл для отправки сообщений
                Scanner scanner = new Scanner(System.in);
                System.out.println("Введите \\help для списка команд.");

                while (true) {
                    String message = scanner.nextLine();
                    if (message.equalsIgnoreCase("\\rps")) {
                        out.println(message); // Отправляем команду серверу

                        while (true) {
                            String input = scanner.nextLine(); // Ввод пользователя
                            out.println(input); // Отправляем выбор серверу

                            if (input.equalsIgnoreCase("stop")) {
                                System.out.println("Вы завершили игру.");
                                break;
                            }

                            // Ожидаем ответ от сервера
                            String serverResponse = in.readLine();
                            if (serverResponse != null) {
                                System.out.println(serverResponse);
                            } else {
                                System.out.println("Соединение с сервером потеряно.");
                                break;
                            }
                        }
                    }
                    else  if (message.equalsIgnoreCase("\\exit")) {
                        System.out.println("Выход из чата...");
                        out.println(message);
                        break;
                    } else if (message.equalsIgnoreCase("\\help")) {
                        System.out.println("\nКоманды чата:");
                        System.out.println("\\help - показать доступные команды");
                        System.out.println("\\exit - выйти из чата");
                        System.out.println("\\list - показать список участников");
                        System.out.println("\\pm <username> <message> - отправить приватное сообщение");
                        System.out.println("\\typing - уведомить других участников, что вы набираете сообщение");
                        System.out.println("\\rps - игра в КМН");;
                    } else {
                        out.println(message);
                    }
                }
                break;
            } catch (IOException e) {
                System.err.println("Ошибка подключения к серверу. Переподключение...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }
        }

    }

}
