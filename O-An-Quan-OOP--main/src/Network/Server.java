package Network;


import Da.*;
import Initialization.InitializationForTwo;
import OCo.*;
import Player.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    // Khai báo 2 người chơi
    public static Player player2 = new Player("Tún");
    public static Player player1 = new Player("Tuấn");

    private static int currentPlayer = player1.getPlayer_id(); //đếm người chơi => Xoay vòng chơi
    private static int count = 2; //Điều kiện ăn Quan ở vòng chơi thứ 3
    private static Scanner scanner = new Scanner(System.in);

    // Sử dụng Init
    public static InitializationForTwo init = new InitializationForTwo();

    // Initialize the board
    public static ArrayList<ODan> oDans = init.InitODan();
    public static ArrayList<Dan> dans = init.InitDan();
    public static ArrayList<OQuan> oQuans = init.InitOQuan();
    public static ArrayList<Quan> quans = init.InitQuan();

    // Tạo ServerSocket
    private static ServerSocket serverSocket;
    private static Socket player1Socket, player2Socket;

    // Streams để giao tiếp với các client
    private static BufferedReader reader1, reader2;
    private static BufferedWriter writer1, writer2;

    public static void main(String[] args) {

        //Thêm Dân vào Ô Dân
        try {
            serverSocket = new ServerSocket(13456);  // Lắng nghe kết nối tại port 12345
            System.out.println("Server đang chờ người chơi...");

            // Kết nối với người chơi 1
            player1Socket = serverSocket.accept();
            System.out.println("Người chơi 1 đã kết nối!");
            reader1 = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
            writer1 = new BufferedWriter(new OutputStreamWriter(player1Socket.getOutputStream()));

            // Kết nối với người chơi 2
            player2Socket = serverSocket.accept();
            System.out.println("Người chơi 2 đã kết nối!");
            reader2 = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()));
            writer2 = new BufferedWriter(new OutputStreamWriter(player2Socket.getOutputStream()));


            // Thêm Dân vào Ô Dân
            int danIndex = 0;
            for (int i = 0; i < oDans.size(); i++) {
                for (int j = 0; j < ODan.DEFAULT_STONES; j++) {
                    oDans.get(i).setDans(dans.get(danIndex));
                    danIndex++;
                }
            }

            // Thêm một ô Dân rỗng để xử lý logic
            oDans.add(5, null);

            // Thêm Quan và 0 Dân vào Ô Quan
            oQuans.get(0).setQuan(quans.get(0));
            oQuans.get(1).setQuan(quans.get(1));
            oQuans.get(0).setDans(null);
            oQuans.get(1).setDans(null);

            if (oDans == null || oQuans == null || oDans.size() < 11 || oQuans.size() < 2) {
                throw new IllegalStateException("Bàn cờ không hợp lệ. Kiểm tra lại việc khởi tạo!");
            }

            playGame();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hàm in bàn cờ
//    private static void printBoard() {
//        System.out.print("     | ");
//        for (int k = 10; k > 5; k--) {
//            System.out.print(ODan.sumDans(oDans.get(k).getDans()) + " | ");
//        }
//        System.out.println();
//        System.out.println("["+ OQuan.sumQuanAndDans(oQuans.get(1).getQuan(),oQuans.get(1).getDans()) + "," + oQuans.get(1).getDans().size() + "]                   [" + oQuans.get(0).getDans().size() + "," + OQuan.sumQuanAndDans(oQuans.get(0).getQuan(),oQuans.get(0).getDans())+ "]");
//        System.out.print("     | ");
//        for (int k = 0; k < 5; k++) {
//            System.out.print(ODan.sumDans(oDans.get(k).getDans()) + " | ");
//        }
//        System.out.println("\n");
//    }
    private static void printBoard() {
        StringBuilder board = new StringBuilder();

        // Dòng 1: Hiển thị hàng trên của Ô Dân
        board.append("     | ");
        for (int k = 10; k > 5; k--) {
            board.append(ODan.sumDans(oDans.get(k).getDans())).append(" | ");
        }
        board.append("\n");

        // Dòng 2: Hiển thị Ô Quan và các chỉ số
        board.append("[").append(OQuan.sumQuanAndDans(oQuans.get(1).getQuan(), oQuans.get(1).getDans()))
                .append(",").append(oQuans.get(1).getDans().size())
                .append("]                   [")
                .append(oQuans.get(0).getDans().size()).append(",")
                .append(OQuan.sumQuanAndDans(oQuans.get(0).getQuan(), oQuans.get(0).getDans())).append("]\n");

        // Dòng 3: Hiển thị hàng dưới của Ô Dân
        board.append("     | ");
        for (int k = 0; k < 5; k++) {
            board.append(ODan.sumDans(oDans.get(k).getDans())).append(" | ");
        }
        board.append("\n");

        // Gửi bàn cờ đến cả hai người chơi
        try {
            writer1.write(board.toString());
            writer1.newLine();
            writer1.flush();

            writer2.write(board.toString());
            writer2.newLine();
            writer2.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // In bàn cờ ra console (nếu muốn kiểm tra từ server)
        System.out.println(board);
    }



//    private static void playGame() {
//        try {
//            while (true) {
//                if (currentPlayer == 2) {
//                    currentPlayer = 0;
//                }
//
//                printBoard(); // Hiển thị bảng trò chơi
//
//                // Kiểm tra điều kiện kết thúc trò chơi
//                if (oQuans.get(0).sumQuanAndDans(oQuans.get(0).getQuan(), oQuans.get(0).getDans()) == 0 &&
//                        oQuans.get(1).sumQuanAndDans(oQuans.get(1).getQuan(), oQuans.get(1).getDans()) == 0) {
//
//                    player1.setDans(sumRange(0, 5));
//                    player2.setDans(sumRange(6, 11));
//
//                    // In điểm của cả hai người chơi
//                    System.out.println("Điểm của " + player1.getName() + ": " + player1.sumQuanAndDans());
//                    System.out.println("Điểm của " + player2.getName() + ": " + player2.sumQuanAndDans());
//
//                    // So sánh điểm và thông báo kết quả
//                    if (player1.sumQuanAndDans() < player2.sumQuanAndDans()) {
//                        System.out.println("Player: " + player2.getName() + ": Win!");
//                        writer1.write("Player " + player2.getName() + " thắng!\n");
//                        writer1.flush();
//                    } else if (player2.sumQuanAndDans() < player1.sumQuanAndDans()) {
//                        System.out.println("Player: " + player1.getName() + ": Win!");
//                        writer2.write("Player " + player1.getName() + " thắng!\n");
//                        writer2.flush();
//                    } else {
//                        System.out.println("Hòa");
//                        writer1.write("Hòa!\n");
//                        writer1.flush();
//                        writer2.write("Hòa!\n");
//                        writer2.flush();
//                    }
//                    break;
//                }
//
//                int hole;
//                if (currentPlayer == player1.getPlayer_id()) {
//                    System.out.println("Player1: " + player1.getName());
//                    writer1.write("Chọn lỗ (0-4) <=> (1-5): ");
//                    writer1.flush();
//                    hole = Integer.parseInt(reader1.readLine());  // Nhận dữ liệu từ người chơi 1 qua mạng
//                    if (hole == 5 || hole == 11 || oDans.get(hole).sumDans(oDans.get(hole).getDans()) == 0) {
//                        writer1.write("Lựa chọn không hợp lệ. Vui lòng chọn lại!\n");
//                        writer1.flush();
//                        continue;
//                    }
//                } else {
//                    System.out.println("Player2: " + player2.getName());
//                    writer2.write("Chọn lỗ (6-10) <=> (1-5): ");
//                    writer2.flush();
//                    hole = Integer.parseInt(reader2.readLine());  // Nhận dữ liệu từ người chơi 2 qua mạng
//                    if (hole == 5 || hole == 11 || oDans.get(hole).sumDans(oDans.get(hole).getDans()) == 0) {
//                        writer2.write("Lựa chọn không hợp lệ. Vui lòng chọn lại!\n");
//                        writer2.flush();
//                        continue;
//                    }
//                }
//
//                // Chọn chiều di chuyển
//                writer1.write("Chọn chiều Phải - Trái (p/t): ");
//                writer1.flush();
//                writer2.write("Chọn chiều Phải - Trái (p/t): ");
//                writer2.flush();
//                String chieu1 = reader1.readLine();  // Nhận chiều di chuyển từ người chơi 1
//                String chieu2 = reader2.readLine();  // Nhận chiều di chuyển từ người chơi 2
//                String chieu = currentPlayer == player1.getPlayer_id() ? chieu1 : chieu2;
//
//                int i = chieu.equals("t") ? hole - 1 : hole + 1;
//                phanphoi(hole, chieu, i);  // Logic lan truyền quân và đá trong trò chơi
//
//                printBoard(); // Hiển thị lại bảng trò chơi sau mỗi lượt
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//  lan2:  private static void playGame() {
//        try {
//            while (true) {
//                // Nếu đến lượt người chơi tiếp theo
//                if (currentPlayer == 2) {
//                    currentPlayer = 0;
//                }
//
//                // Hiển thị bàn cờ cho cả hai người chơi
//                printBoard();
//
//                // Kiểm tra điều kiện kết thúc trò chơi
//                if (oQuans.get(0).sumQuanAndDans(oQuans.get(0).getQuan(), oQuans.get(0).getDans()) == 0 &&
//                        oQuans.get(1).sumQuanAndDans(oQuans.get(1).getQuan(), oQuans.get(1).getDans()) == 0) {
//
//                    // Tính điểm cho mỗi người chơi
//                    player1.setDans(sumRange(0, 5));
//                    player2.setDans(sumRange(6, 11));
//
//                    String scoreMessage = "Điểm của " + player1.getName() + ": " + player1.sumQuanAndDans() + "\n" +
//                            "Điểm của " + player2.getName() + ": " + player2.sumQuanAndDans() + "\n";
//
//                    // Gửi điểm và thông báo kết quả đến cả hai người chơi
//                    sendToBothClients(scoreMessage);
//
//                    if (player1.sumQuanAndDans() < player2.sumQuanAndDans()) {
//                        sendToBothClients("Player: " + player2.getName() + ": Win!\n");
//                    } else if (player2.sumQuanAndDans() < player1.sumQuanAndDans()) {
//                        sendToBothClients("Player: " + player1.getName() + ": Win!\n");
//                    } else {
//                        sendToBothClients("Hòa\n");
//                    }
//                    break;
//                }
//
//                // Xử lý lượt chơi của từng người chơi
//                int hole;
//                if (currentPlayer == player1.getPlayer_id()) {
//                    writer1.write("Lượt của bạn, Player1: " + player1.getName() + "\n");
//                    writer1.write("Chọn lỗ (0-4) <=> (1-5): ");
//                    writer1.flush();
//                    hole = Integer.parseInt(reader1.readLine());
//                    if (!isValidHole(hole)) {
//                        writer1.write("Lựa chọn không hợp lệ. Vui lòng chọn lại!\n");
//                        writer1.flush();
//                        continue;
//                    }
//                } else {
//                    writer2.write("Lượt của bạn, Player2: " + player2.getName() + "\n");
//                    writer2.write("Chọn lỗ (6-10) <=> (1-5): ");
//                    writer2.flush();
//                    hole = Integer.parseInt(reader2.readLine());
//                    if (!isValidHole(hole)) {
//                        writer2.write("Lựa chọn không hợp lệ. Vui lòng chọn lại!\n");
//                        writer2.flush();
//                        continue;
//                    }
//                }
//
//                // Chọn chiều di chuyển
//                BufferedReader currentReader = currentPlayer == player1.getPlayer_id() ? reader1 : reader2;
//                BufferedWriter currentWriter = currentPlayer == player1.getPlayer_id() ? writer1 : writer2;
//
//                currentWriter.write("Chọn chiều Phải - Trái (p/t): ");
//                currentWriter.flush();
//                String direction = currentReader.readLine();
//
//                int i = direction.equals("t") ? hole - 1 : hole + 1;
//                phanphoi(hole, direction, i);
//
//                // Hiển thị lại bàn cờ sau mỗi lượt
//                printBoard();
//
//                // Chuyển lượt
//                currentPlayer++;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    private static void sendToBothClients(String message) {
//        try {
//            writer1.write(message);
//            writer1.flush();
//
//            writer2.write(message);
//            writer2.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    private static boolean isValidHole(int hole) {
//        // Kiểm tra lỗ nằm ngoài phạm vi hoặc lỗ rỗng
//        return !(hole == 5 || hole == 11 || oDans.get(hole).sumDans(oDans.get(hole).getDans()) == 0);
//    }
    private static void playGame() {
        try {
            while (true) {
                if (currentPlayer == 2) {
                    currentPlayer = 0;
                }

                printBoard(); // Gửi trạng thái bàn cờ tới cả hai client

                // Kiểm tra điều kiện kết thúc trò chơi
                if (oQuans.get(0).sumQuanAndDans(oQuans.get(0).getQuan(), oQuans.get(0).getDans()) == 0 &&
                        oQuans.get(1).sumQuanAndDans(oQuans.get(1).getQuan(), oQuans.get(1).getDans()) == 0) {

                    // Cập nhật điểm cho người chơi
                    player1.setDans(sumRange(0, 5));
                    player2.setDans(sumRange(6, 11));

                    // Tạo thông báo kết quả
                    String result = "Kết quả:\n" +
                            "Điểm của " + player1.getName() + ": " + player1.sumQuanAndDans() + "\n" +
                            "Điểm của " + player2.getName() + ": " + player2.sumQuanAndDans() + "\n";
                    if (player1.sumQuanAndDans() < player2.sumQuanAndDans()) {
                        result += "Người chiến thắng: " + player2.getName() + "!\n";
                    } else if (player2.sumQuanAndDans() < player1.sumQuanAndDans()) {
                        result += "Người chiến thắng: " + player1.getName() + "!\n";
                    } else {
                        result += "Hòa!\n";
                    }

                    // Gửi kết quả tới cả hai client
                    writer1.write(result);
                    writer1.newLine();
                    writer1.flush();

                    writer2.write(result);
                    writer2.newLine();
                    writer2.flush();

                    break;
                }

                // Xử lý lượt của người chơi hiện tại
                BufferedWriter currentWriter = currentPlayer == 0 ? writer1 : writer2;
                BufferedReader currentReader = currentPlayer == 0 ? reader1 : reader2;

                currentWriter.write("Lượt của bạn (" + (currentPlayer == 0 ? player1.getName() : player2.getName()) + "):");
                currentWriter.newLine();
                currentWriter.write("Chọn lỗ (0-4) hoặc (6-10): ");
                currentWriter.newLine();
                currentWriter.flush();

                int hole = Integer.parseInt(currentReader.readLine()); // Nhận lựa chọn từ client
                if ((currentPlayer == 0 && (hole < 0 || hole > 4)) ||
                        (currentPlayer == 1 && (hole < 6 || hole > 10)) ||
                        oDans.get(hole).sumDans(oDans.get(hole).getDans()) == 0) {
                    currentWriter.write("Lựa chọn không hợp lệ. Vui lòng chọn lại!\n");
                    currentWriter.newLine();
                    currentWriter.flush();
                    continue;
                }

                currentWriter.write("Chọn chiều Phải - Trái (p/t): ");
                currentWriter.newLine();
                currentWriter.flush();

                String chieu = currentReader.readLine(); // Nhận hướng đi từ client

                int i = chieu.equals("t") ? hole - 1 : hole + 1;
                phanphoi(hole, chieu, i); // Phân phối dân

                printBoard(); // Gửi trạng thái bàn cờ sau khi phân phối
                currentPlayer++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void phanphoi(int hole, String chieu, int i) {
        if (chieu.equals("t")) {

            // Distribute stones to the left
            ArrayList stones = (ArrayList) (oDans.get(hole).getDans()).clone();

            oDans.get(hole).getDans().clear();
            while (!stones.isEmpty()) {
                Dan dan_temp = (Dan) stones.get(stones.size()-1);

                if (i < 0) i = 11;
                if (i==oQuans.get(0).getIndex()) {
                    oQuans.getFirst().setDans(dan_temp);
                }
                else if (i==oQuans.get(1).getIndex()) {
                    oQuans.get(1).setDans(dan_temp);
                }
                else {
                    oDans.get(i).setDans(dan_temp);
                }
                stones.remove(stones.size() - 1);

                if (stones.isEmpty()) {
                    if (i - 1 == -1){
                        stones = (ArrayList) oQuans.get(1).getDans().clone();
                        oQuans.get(1).getDans().clear();
                        i--;
                    }
                    else if (i - 1 == 5) {
                        stones = (ArrayList) oQuans.get(0).getDans().clone();
                        oQuans.get(0).getDans().clear();
                        i--;
                    }
                    else {
                        stones = (ArrayList) oDans.get(i-1).getDans().clone();
                        oDans.get(i-1).getDans().clear();
                        i--;
                    }
                    if (i < 0) i = 11;
                }
                i--;

                printBoard();
            }

            // Capture stones
            ArrayList diemCong = new ArrayList();
            int luu_diemCong = 0;
            ArrayList quanCong = new ArrayList();
            int luu_quanCong = 0;

            if (i < 0) i = 11;

            if (i+1==oQuans.get(0).getIndex()) {
                stones = (ArrayList) oQuans.get(0).getDans().clone();
                oQuans.get(0).getDans().clear();
            }
            else if (i+1==oQuans.get(1).getIndex()) {
                stones = (ArrayList) oQuans.get(1).getDans().clone();
                oQuans.get(1).getDans().clear();
            }

            else if (i+1 == 12){
                stones = (ArrayList) oDans.get(0).getDans().clone();
                oDans.get(0).getDans().clear();
            }

            else {
                stones = (ArrayList) oDans.get(i+1).getDans().clone();
                oDans.get(i+1).getDans().clear();
            }

            //Vòng while để ăn liên tục
            while (stones.isEmpty()) {
                if (i < 0) i = 11;

                //Điều kiện dừng khi dân trong Ô Quan trống nhưng Ô Quan vẫn chứa Quan
                if (i+1==oQuans.get(0).getIndex() && !oQuans.get(0).getQuan().isEmpty()) {
                    break;
                }
                else if (i+1==oQuans.get(1).getIndex()) {
                    if (!oQuans.get(1).getQuan().isEmpty()){
                        break;
                    }
                }


                //Ăn quân. Thêm các Dân thuộc Ô Quan vào điểm của người chơi
                if (i==oQuans.get(0).getIndex()) {
                    if (OQuan.sumQuanAndDans(oQuans.get(0).getQuan(),oQuans.get(0).getDans()) >= 15 || count > 2) {
                        quanCong = (ArrayList) oQuans.get(0).getQuan().clone();
                        luu_quanCong += quanCong.size();
                        oQuans.get(0).getQuan().clear();
                    }
                    stones = oQuans.get(0).getDans();
                }
                else if (i==oQuans.get(1).getIndex()) {
                    if (OQuan.sumQuanAndDans(oQuans.get(1).getQuan(),oQuans.get(1).getDans()) >= 15 || count > 2) {
                        quanCong = (ArrayList) oQuans.get(1).getQuan().clone();
                        luu_quanCong += quanCong.size();
                        oQuans.get(1).getQuan().clear();
                    }
                    stones = oQuans.get(1).getDans();
                }
                else {
                    if (i==5) i=4;
                    stones = oDans.get(i).getDans() ;
                }

                if (!stones.isEmpty() || !quanCong.isEmpty()) {

                    diemCong =(ArrayList) stones.clone();
                    luu_diemCong += diemCong.size();

                    if (currentPlayer == 0) {
                        player1.setQuans(quanCong);
                        player1.setDans(diemCong);
                    }
                    else if (currentPlayer == 1) {
                        player2.setQuans(quanCong);
                        player2.setDans(diemCong);
                    }

                    // Làm mới Stones và quanCong
                    stones.clear();
                    quanCong.clear();

                    i--;
                    if (i < 0) i = 11;
                    if (i==oQuans.get(0).getIndex()) {
                        stones = oQuans.get(0).getDans();
                    }
                    else if (i==oQuans.get(1).getIndex()) {
                        stones = oQuans.get(1).getDans();
                    }
                    else {
                        stones = oDans.get(i).getDans() ;
                    }
                    i--;
                } else if (stones.size() == 0) {
                    break;
                }

                if (i < 0) i = 11;

                printBoard();
            }

            printScore(luu_diemCong, luu_quanCong );

        } else if (chieu.equals("p")) {
            // Distribute stones to the right
            ArrayList stones = (ArrayList) (oDans.get(hole).getDans()).clone();
            oDans.get(hole).getDans().clear();
            while (!stones.isEmpty()) {
                Dan dan_temp = (Dan) stones.get(stones.size()-1);

                if (i > 11) i = 0;
                if (i==oQuans.get(0).getIndex()) {
                    oQuans.getFirst().setDans(dan_temp);
                }
                else if (i==oQuans.get(1).getIndex()) {
                    oQuans.get(1).setDans(dan_temp);
                }
                else {
                    oDans.get(i).setDans(dan_temp);
                }
                stones.remove(stones.size() - 1);


                if (stones.isEmpty()) {
                    if (i + 1 == 12){
                        stones = (ArrayList) oDans.get(0).getDans().clone();
                        oDans.get(0).getDans().clear();
                        i++;
                    }
                    else if (i + 1 == 5) {
                        stones = (ArrayList) oQuans.get(0).getDans().clone();
                        oQuans.get(0).getDans().clear();
                        i++;
                    }
                    else if (i + 1 == 11) {
                        stones = (ArrayList) oQuans.get(1).getDans().clone();
                        oQuans.get(1).getDans().clear();
                        i++;
                    }
                    else {
                        stones = (ArrayList) oDans.get(i+1).getDans().clone();
                        oDans.get(i+1).getDans().clear();
                        i++;
                    }
                    if (i>11) i = 0;
                }
                i++;
                printBoard();
            }

            // Capture stones
            ArrayList diemCong = new ArrayList<>();
            int luu_diemCong = 0;
            ArrayList quanCong = new ArrayList();
            int luu_quanCong = 0;
            if (i > 11) i = 0;

            if (i-1==oQuans.get(0).getIndex()) {
                stones = (ArrayList) oQuans.get(0).getDans().clone();
                oQuans.get(0).getDans().clear();
            }
            else if (i-1==oQuans.get(1).getIndex()) {
                stones = (ArrayList) oQuans.get(1).getDans().clone();
                oQuans.get(1).getDans().clear();
            }
            else if (i-1==-1) {
                stones = (ArrayList) oQuans.get(1).getDans().clone();
                oQuans.get(1).getDans().clear();

            }
            else {
                stones = (ArrayList) oDans.get(i-1).getDans().clone();
                oDans.get(i-1).getDans().clear();
            }

            //Vòng while để ăn liên tục
            while (stones.isEmpty()) {
                if (i > 11) i = 0;

                //Điều kiện dừng khi dân trong Ô Quan trống nhưng Ô Quan vẫn chứa Quan
                if (i-1==oQuans.get(0).getIndex() && !oQuans.get(0).getQuan().isEmpty()) {
                    break;
                }
                else if (i-1==oQuans.get(1).getIndex() || i - 1 == -1) {
                    if (!oQuans.get(1).getQuan().isEmpty()){
                        break;
                    }
                }

                //Ăn quân. Thêm các Dân thuộc Ô Quan vào điểm của người chơi
                if (i==oQuans.get(0).getIndex()) {
                    if (OQuan.sumQuanAndDans(oQuans.get(0).getQuan(),oQuans.get(0).getDans()) >= 15 || count > 2) {
                        quanCong = (ArrayList) oQuans.get(0).getQuan().clone();
                        luu_quanCong += quanCong.size();
                        oQuans.get(0).getQuan().clear();
                    }
                    stones = oQuans.get(0).getDans();
                }
                else if (i==oQuans.get(1).getIndex()) {
                    if (OQuan.sumQuanAndDans(oQuans.get(1).getQuan(),oQuans.get(1).getDans()) >= 15 || count > 2) {
                        quanCong = (ArrayList) oQuans.get(1).getQuan().clone();
                        luu_quanCong += quanCong.size();
                        oQuans.get(1).getQuan().clear();
                    }
                    stones = oQuans.get(1).getDans();
                }
                else {
                    if (i==5) i=4;
                    stones = oDans.get(i).getDans() ;
                }

                if (!stones.isEmpty() || !quanCong.isEmpty()) {
                    diemCong =(ArrayList) stones.clone();
                    luu_diemCong += diemCong.size();

                    if (currentPlayer == 0) {
                        player1.setQuans(quanCong);
                        player1.setDans(diemCong);
                    }
                    else if (currentPlayer == 1) {
                        player2.setQuans(quanCong);
                        player2.setDans(diemCong);
                    }

                    // Làm mới Stones và quanCong
                    stones.clear();
                    quanCong.clear();

                    i++;
                    if (i > 11) i = 0;
                    if (i==oQuans.get(0).getIndex()) {
                        stones = oQuans.get(0).getDans();
                    }
                    else if (i==oQuans.get(1).getIndex()) {
                        stones = oQuans.get(1).getDans();
                    }
                    else {
                        stones = oDans.get(i).getDans() ;
                    }
                    i++;

                } else if (stones.size() == 0) {
                    break;
                }

                if (i > 11) i = 0;

                printBoard();
            }

            printScore(luu_diemCong, luu_quanCong);
        }

//        currentPlayer++;
        count ++;
        raithhem();
    }

    //Rải thêm Dân vào ô khi rơi vào trường hợp còn game, đến lượt nhưng không có Dân trên bàn cờ phía mình để rải.
    private static void raithhem() {
        if (currentPlayer == player1.getPlayer_id() || currentPlayer == player1.getPlayer_id() + 2) {
            if (sumRange(0, 5).isEmpty()) {
                if (player1.getDans().size() == 0) {
                    printFinalScore();
                }
                if (player1.getDans().size() > 0 && player1.getDans().size() <= 5) {
                    ArrayList newStones = (ArrayList) player1.getDans().clone();
                    player1.getDans().clear();
                    for (int z = oDans.getFirst().getIndex(); z < oDans.get(5).getIndex(); z++) {
                        Dan newDan_temp = (Dan) newStones.getLast();

                        oDans.get(z).setDans(newDan_temp);
                        newStones.removeLast();
                    }
                }
                if (player1.getDans().size() > 5) {
                    ArrayList newStones = new ArrayList();
                    for (int i = 0; i < 5 ; i++){
                        newStones.add(player1.getDans().get(i));
                        player1.getDans().remove(i);
                    }
                    for (int z = oDans.getFirst().getIndex(); z <= oDans.get(4).getIndex(); z++) {
                        Dan newDan_temp = (Dan) newStones.getLast();

                        oDans.get(z).setDans(newDan_temp);
                        newStones.removeLast();
                    }
                }
            }
        }

        if (currentPlayer == player2.getPlayer_id()) {
            if (sumRange(6, 11).isEmpty()) {
                if (player2.getDans().size() == 0) {
                    printFinalScore();
                }
                if (player2.getDans().size() > 0 && player2.getDans().size() <= 5) {
                    ArrayList newStones = (ArrayList) player2.getDans().clone();
                    player2.getDans().clear();
                    for (int z = oDans.get(6).getIndex(); z <= oDans.getLast().getIndex(); z++) {
                        Dan newDan_temp = (Dan) newStones.getLast();

                        oDans.get(z).setDans(newDan_temp);
                        newStones.removeLast();
                    }
                }
                if (player2.getDans().size() > 5) {
                    ArrayList newStones = new ArrayList();
                    for (int i = 0; i < 5 ; i++){
                        newStones.add(player2.getDans().get(i));
                        player2.getDans().remove(i);
                    }
                    for (int z = oDans.get(6).getIndex(); z <= oDans.getLast().getIndex(); z++) {
                        Dan newDan_temp = (Dan) newStones.getLast();

                        oDans.get(z).setDans(newDan_temp);
                        newStones.removeLast();
                    }
                }
            }
        }
    }


    //Hàm tính toán điểm (số dân, thêm số dân vào các thuộc tính lưu điểm của người chơi) còn lại trên bàn cờ ngay khi hai Ô Quan không còn điểm nữa
    private static ArrayList<Dan> sumRange(int start, int end) {
        ArrayList<Dan> sumRange = new ArrayList<>();
        for (int i = start; i < end; i++) {
            ArrayList<Dan> dans = (ArrayList<Dan>) oDans.get(i).getDans().clone();

            sumRange.addAll(dans);
            dans.clear();
            printBoard();
        }
        return sumRange;
    }

    // Hiển thị số điểm được cộng của người chơi sau mỗi lượt rải Dân
    private static void printScore(int luu_diemCong, int luu_quanCong) {
        if (currentPlayer == 0) {
            System.out.println("Người chơi " + player1.getName() + " nhận được: " + luu_diemCong + " Dân, " + luu_quanCong + " Quan");
            System.out.println("Điểm của " + player1.getName() + ": " + player1.sumQuanAndDans());
        }
        if (currentPlayer == 1) {
            System.out.println("Người chơi " + player2.getName() + " nhận được: " +  luu_diemCong + " Dân, " + luu_quanCong + " Quan");
            System.out.println("Điểm của " + player2.getName() + ": " + player2.sumQuanAndDans());
        }
    }

    // In ra điểm cuối cùng và tuyên bố kết quả kết thúc màn chơi.
    private static void printFinalScore() {
        System.out.println("Điểm của " + player1.getName() + ": " + player1.sumQuanAndDans());
        System.out.println("Điểm của " + player2.getName() + ": " + player2.sumQuanAndDans());
        if (player1.sumQuanAndDans() < player2.sumQuanAndDans()) {
            System.out.println(player1.getName() +": Win!");
        } else if (player2.sumQuanAndDans() < player1.sumQuanAndDans()) {
            System.out.println(player2.getName() +": Win!");
        } else {
            System.out.println("Hòa");
        }
    }
}