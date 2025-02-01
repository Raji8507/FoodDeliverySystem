package FoodDeliverySystem;
import java.util.Scanner;
import java.sql.*;

public class FoodDelivery {
    static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/FoodDB", "root", "raji@8507");
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    static void createTables() throws SQLException {
        Connection con = connect();
        String userTable = "CREATE TABLE IF NOT EXISTS users (user_id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50), phone VARCHAR(15), password VARCHAR(50))";
        String restaurantTable = "CREATE TABLE IF NOT EXISTS restaurants (restaurant_id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50), phone VARCHAR(15), password VARCHAR(50))";
        String menuTable = "CREATE TABLE IF NOT EXISTS menu (food_id INT PRIMARY KEY AUTO_INCREMENT, food_name VARCHAR(50), price DECIMAL(10,2))";
        String orderTable = "CREATE TABLE IF NOT EXISTS orders (order_id INT PRIMARY KEY AUTO_INCREMENT, user_id INT, food_id INT, quantity INT, total_price DECIMAL(10,2), extra_info TEXT, status VARCHAR(20), FOREIGN KEY (user_id) REFERENCES users(user_id), FOREIGN KEY (food_id) REFERENCES menu(food_id))";
        String feedbackTable = "CREATE TABLE IF NOT EXISTS feedback (feedback_id INT PRIMARY KEY AUTO_INCREMENT, user_id INT, order_id INT, rating INT, comments TEXT, FOREIGN KEY (user_id) REFERENCES users(user_id), FOREIGN KEY (order_id) REFERENCES orders(order_id))";
        
        con.createStatement().executeUpdate(userTable);
        con.createStatement().executeUpdate(restaurantTable);
        con.createStatement().executeUpdate(menuTable);
        con.createStatement().executeUpdate(orderTable);
        con.createStatement().executeUpdate(feedbackTable);
        
        System.out.println("Tables created successfully!");
        con.close();
    }

    static void addUser(String name, String phone, String password) throws SQLException {
        Connection con = connect();
        String qry = "INSERT INTO users (name, phone, password) VALUES (?, ?, ?)";
        PreparedStatement pre = con.prepareStatement(qry);
        pre.setString(1, name);
        pre.setString(2, phone);
        pre.setString(3, password);
        pre.executeUpdate();
        System.out.println("User added successfully!");
        con.close();
    }

    static void addRestaurant(String name, String phone, String password) throws SQLException {
        Connection con = connect();
        String qry = "INSERT INTO restaurants (name, phone, password) VALUES (?, ?, ?)";
        PreparedStatement pre = con.prepareStatement(qry);
        pre.setString(1, name);
        pre.setString(2, phone);
        pre.setString(3, password);
        pre.executeUpdate();
        System.out.println("Restaurant added successfully!");
        con.close();
    }

    static void addFoodItem(String foodName, double price) throws SQLException {
        Connection con = connect();
        String qry = "INSERT INTO menu (food_name, price) VALUES (?, ?)";
        PreparedStatement pre = con.prepareStatement(qry);
        pre.setString(1, foodName);
        pre.setDouble(2, price);
        pre.executeUpdate();
        System.out.println("Food item added successfully!");
    }

    static void displayMenu() throws SQLException {
        Connection con = connect();
        String qry = "SELECT * FROM menu";
        PreparedStatement pre = con.prepareStatement(qry);
        ResultSet rs = pre.executeQuery();
        System.out.println("\n--- Available Food Items ---");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("food_id") + ", Name: " + rs.getString("food_name") + ", Price: $" + rs.getDouble("price"));
        }
    }

    static void placeOrder(int userId, int foodId, int quantity, String extraInfo) throws SQLException {
        Connection con = connect();
        String qry = "SELECT price FROM menu WHERE food_id = ?";
        PreparedStatement pre = con.prepareStatement(qry);
        pre.setInt(1, foodId);
        ResultSet rs = pre.executeQuery();
        
        if (rs.next()) {
            double price = rs.getDouble("price");
            double totalPrice = price * quantity;
            
            System.out.println("Total Price: $" + totalPrice);
            System.out.print("Confirm Order? (yes/no): ");
            Scanner sc = new Scanner(System.in);
            String confirmation = sc.nextLine();
            
            if (confirmation.equalsIgnoreCase("yes")) {
                String orderQry = "INSERT INTO orders (user_id, food_id, quantity, total_price, extra_info, status) VALUES (?, ?, ?, ?, ?, 'Pending')";
                PreparedStatement orderPre = con.prepareStatement(orderQry);
                orderPre.setInt(1, userId);
                orderPre.setInt(2, foodId);
                orderPre.setInt(3, quantity);
                orderPre.setDouble(4, totalPrice);
                orderPre.setString(5, extraInfo);
                orderPre.executeUpdate();
                System.out.println("Order placed successfully! Total Price: $" + totalPrice);
            } else {
                System.out.println("Order canceled.");
            }
        } else {
            System.out.println("Food item not found!");
        }
        con.close();
    }


    static void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        Connection con = connect();
        String qry = "UPDATE orders SET status = ? WHERE order_id = ?";
        PreparedStatement pre = con.prepareStatement(qry);
        pre.setString(1, newStatus);
        pre.setInt(2, orderId);
        int updated = pre.executeUpdate();
        if (updated > 0) {
            System.out.println("Order status updated to: " + newStatus);
        }
        con.close();
    }

    static void giveFeedback(int userId, int orderId, int rating, String comments) throws SQLException {
        Connection con = connect();
        String qry = "INSERT INTO feedback (user_id, order_id, rating, comments) VALUES (?, ?, ?, ?)";
        PreparedStatement pre = con.prepareStatement(qry);
        pre.setInt(1, userId);
        pre.setInt(2, orderId);
        pre.setInt(3, rating);
        pre.setString(4, comments);
        pre.executeUpdate();
        System.out.println("Feedback submitted successfully!");
        con.close();
    }

    public static void main(String args[]) throws SQLException {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n1. Create Tables\n2. Add User\n3. Add Restaurant\n4. Add Food Item\n5. View Menu\n6. Place Order\n7. Update Order Status\n8. Give Feedback\n9. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            
            switch (choice) {
                case 1:
                    createTables();
                    break;
                case 2:
                    System.out.print("Enter User Name: ");
                    sc.nextLine();
                    String userName = sc.nextLine();
                    System.out.print("Enter User Phone: ");
                    String userPhone = sc.nextLine();
                    System.out.print("Enter User Password: ");
                    String userPassword = sc.nextLine();
                    addUser(userName, userPhone, userPassword);
                    break;
                case 3:
                    System.out.print("Enter Restaurant Name: ");
                    sc.nextLine();
                    String restaurantName = sc.nextLine();
                    System.out.print("Enter Restaurant Phone: ");
                    String restaurantPhone = sc.nextLine();
                    System.out.print("Enter Restaurant Password: ");
                    String restaurantPassword = sc.nextLine();
                    addRestaurant(restaurantName, restaurantPhone, restaurantPassword);
                    break;
                case 4:
                    System.out.print("Enter Food Name: ");
                    sc.nextLine();
                    String foodName = sc.nextLine();
                    System.out.print("Enter Food Price: ");
                    double foodPrice = sc.nextDouble();
                    addFoodItem(foodName, foodPrice);
                    break;
                case 5:
                    displayMenu();
                    break;
                case 6:
                    System.out.print("Enter User ID: ");
                    int userId = sc.nextInt();
                    System.out.print("Enter Food ID: ");
                    int foodId = sc.nextInt();
                    System.out.print("Enter Quantity: ");
                    int quantity = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Extra Information: ");
                    String extraInfo = sc.nextLine();
                    placeOrder(userId, foodId, quantity, extraInfo);
                    break;
                case 7:
                    System.out.print("Enter Order ID: ");
                    int orderId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter New Status: ");
                    String newStatus = sc.nextLine();
                    updateOrderStatus(orderId, newStatus);
                    break;
                case 8:
                    System.out.print("Enter User ID: ");
                    int feedbackUserId = sc.nextInt();
                    System.out.print("Enter Order ID: ");
                    int feedbackOrderId = sc.nextInt();
                    System.out.print("Enter Rating (1-5): ");
                    int rating = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Comments: ");
                    String comments = sc.nextLine();
                    giveFeedback(feedbackUserId, feedbackOrderId, rating, comments);
                    break;
                case 9:
                    System.out.println("Exiting...");
                    sc.close();
                    System.exit(0);
            }
        }
    }
}