//import java.io.*;
//import java.net.*;
//import java.util.Scanner;
//
//public class Client2 {
//    public static void main(String[] args) {
//        try (Socket socket = new Socket("localhost", 13456)) {
//            System.out.println("Đã kết nối đến server!");
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            Scanner scanner = new Scanner(System.in);
//
//            // Nhận và in thông điệp từ server
//            new Thread(() -> {
//                try {
//                    String message;
//                    while ((message = reader.readLine()) != null) {
//                        System.out.println(message);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }).start();
//
//            // Gửi dữ liệu đến server
//            String input;
//            while (true) {
//                input = scanner.nextLine();
//                writer.write(input);
//                writer.newLine();
//                writer.flush();
//                if (input.equalsIgnoreCase("exit")) break;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client2 {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 13456)) {
            System.out.println("Đã kết nối đến server!");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            Scanner scanner = new Scanner(System.in);

            // Thread để nhận bàn cờ và các thông điệp khác từ server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message); // Hiển thị bàn cờ hoặc thông điệp
                    }
                } catch (IOException e) {
                    System.err.println("Mất kết nối đến server.");
                }
            }).start();

            // Gửi lệnh hoặc thông tin đến server
            String input;
            while (true) {
                input = scanner.nextLine();
                writer.write(input);
                writer.newLine();
                writer.flush();
                if (input.equalsIgnoreCase("exit")) break; // Thoát chương trình khi nhập "exit"
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
