import java.io.*;
import java.util.*;

/*
 * Bakery Management System
 * Features:
 * 1. Add, display, search, update, delete items
 * 2. Automated unique ID for each item
 * 3. Billing using item ID
 * 4. File-based persistent storage
 * 5. Input validation and error handling
 */

class Item implements Serializable {
    int id;          // Unique ID
    String name;
    double price;

    Item(int id, String name, double price) {
        this.id = id;
        this.name = name.trim();
        this.price = price;
    }
}

public class BakeryManagement {

    static Scanner sc = new Scanner(System.in);

    // Ensure file exists
    static void ensureFileExists() {
        File file = new File("bakery.txt");
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    // Load all items from file
    static ArrayList<Item> loadItems() {
        ArrayList<Item> itemList = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("bakery.txt"))) {
            while (true) {
                Item item = (Item) ois.readObject();
                itemList.add(item);
            }
        } catch (EOFException e) {
            // End of file reached
        } catch (FileNotFoundException e) {
            // File not found, return empty list
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return itemList;
    }

    // Save all items to file
    static void saveItems(ArrayList<Item> itemList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("bakery.txt"))) {
            for (Item item : itemList) {
                oos.writeObject(item);
            }
        } catch (Exception e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    // Generate next unique ID
    static int getNextId(ArrayList<Item> itemList) {
        int maxId = 0;
        for (Item item : itemList) {
            if (item.id > maxId) maxId = item.id;
        }
        return maxId + 1;
    }

    // Add new item
    static void addItem() {
        ensureFileExists();
        ArrayList<Item> itemList = loadItems();
        try {
            System.out.print("Enter item name: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Item name cannot be empty!");
                return;
            }

            // Prevent duplicate names
            for (Item item : itemList) {
                if (item.name.equalsIgnoreCase(name)) {
                    System.out.println("Item already exists!");
                    return;
                }
            }

            System.out.print("Enter item price: ");
            double price = Double.parseDouble(sc.nextLine());
            if (price < 0) {
                System.out.println("Price cannot be negative!");
                return;
            }

            int id = getNextId(itemList);  // Auto-generated unique ID
            itemList.add(new Item(id, name, price));
            saveItems(itemList);
            System.out.println("Item added successfully! ID: " + id);

        } catch (NumberFormatException e) {
            System.out.println("Invalid price! Please enter a valid number.");
        }
    }

    // Display all items
    static void displayItems() {
        ensureFileExists();
        ArrayList<Item> itemList = loadItems();
        if (itemList.isEmpty()) {
            System.out.println("No items found!");
            return;
        }
        System.out.println("\n--- Bakery Items ---");
        System.out.println("ID\tName\tPrice");
        for (Item item : itemList) {
            System.out.println(item.id + "\t" + item.name + "\t" + item.price);
        }
    }

    // Search item by ID or Name
    static void searchItem() {
        ensureFileExists();
        ArrayList<Item> itemList = loadItems();
        System.out.print("Enter item name or ID to search: ");
        String input = sc.nextLine().trim();
        boolean found = false;

        try {
            int id = Integer.parseInt(input);
            for (Item item : itemList) {
                if (item.id == id) {
                    System.out.println("Item Found: " + item.id + " | " + item.name + " | " + item.price);
                    found = true;
                }
            }
        } catch (NumberFormatException e) {
            for (Item item : itemList) {
                if (item.name.equalsIgnoreCase(input)) {
                    System.out.println("Item Found: " + item.id + " | " + item.name + " | " + item.price);
                    found = true;
                }
            }
        }

        if (!found) System.out.println("Item not found!");
    }

    // Update item
    static void updateItem() {
        ensureFileExists();
        ArrayList<Item> itemList = loadItems();
        System.out.print("Enter item ID to update: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            boolean found = false;
            for (Item item : itemList) {
                if (item.id == id) {
                    System.out.print("Enter new name: ");
                    String newName = sc.nextLine().trim();
                    if (!newName.isEmpty()) item.name = newName;

                    System.out.print("Enter new price: ");
                    try {
                        double newPrice = Double.parseDouble(sc.nextLine());
                        if (newPrice >= 0) item.price = newPrice;
                        else System.out.println("Price cannot be negative, keeping old price.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price, keeping old price.");
                    }
                    found = true;
                    break;
                }
            }
            if (found) {
                saveItems(itemList);
                System.out.println("Item updated successfully!");
            } else {
                System.out.println("Item ID not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID!");
        }
    }

    // Delete item
    static void deleteItem() {
        ensureFileExists();
        ArrayList<Item> itemList = loadItems();
        System.out.print("Enter item ID to delete: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            boolean found = itemList.removeIf(item -> item.id == id);
            if (found) {
                saveItems(itemList);
                System.out.println("Item deleted successfully!");
            } else {
                System.out.println("Item ID not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID!");
        }
    }

    // Billing using ID
    static void billing() {
        ensureFileExists();
        ArrayList<Item> itemList = loadItems();
        if (itemList.isEmpty()) {
            System.out.println("No items available for billing!");
            return;
        }

        displayItems();
        double total = 0;

        while (true) {
            System.out.print("Enter item ID to buy (or 0 to finish): ");
            int id;
            try {
                id = Integer.parseInt(sc.nextLine());
                if (id == 0) break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID!");
                continue;
            }

            Optional<Item> optItem = itemList.stream().filter(item -> item.id == id).findFirst();
            if (optItem.isPresent()) {
                Item item = optItem.get();
                System.out.print("Enter quantity: ");
                try {
                    int qty = Integer.parseInt(sc.nextLine());
                    if (qty < 0) {
                        System.out.println("Quantity cannot be negative!");
                        continue;
                    }
                    total += item.price * qty;
                    System.out.println("Added " + qty + " x " + item.name);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity! Skipping item.");
                }
            } else {
                System.out.println("Item ID not found!");
            }
        }

        System.out.println("Total Bill: " + total);
    }

    public static void main(String[] args) {
        ensureFileExists();
        while (true) {
            System.out.println("\n--- Bakery Management System ---");
            System.out.println("1. Add Item");
            System.out.println("2. Display Items");
            System.out.println("3. Search Item");
            System.out.println("4. Update Item");
            System.out.println("5. Delete Item");
            System.out.println("6. Billing");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1: addItem(); break;
                    case 2: displayItems(); break;
                    case 3: searchItem(); break;
                    case 4: updateItem(); break;
                    case 5: deleteItem(); break;
                    case 6: billing(); break;
                    case 7: System.exit(0); break;
                    default: System.out.println("Invalid choice!"); break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number for choice.");
            }
        }
    }
}
