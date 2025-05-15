import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
class Alumni {
    private int id;
    private String name;
    private int graduationYear;
    private String major;
    private String email;
    private String phone;
    private String currentCompany;
    public Alumni(String name, int y, String m, String e, String p, String c) 
{
        this.name = name; 
        this.graduationYear = y;
        this.major = m;
        this.email = e;
        this.phone = p;
        this.currentCompany = c;
    }
    public Alumni(int id, String name, int y, String m, String e, String p, String c) 
{
        this(name, y, m, e, p, c); this.id = id;
    }
    
    public int getId()
 {
 return id;
 }
    public String getName()
 {
 return name;
 }
    public int getGraduationYear()
 {
 return graduationYear;
 }
    public String getMajor() 
{
 return major;
 }
    public String getEmail()
 {
 return email;
 }
    public String getPhone()
 {
 return phone;
 }
    public String getCurrentCompany()
 {
 return currentCompany;
 }
    public void setId(int id)
 { 
this.id = id; 
}
    public void setName(String name)
 {
 this.name = name;
 }
    public void setGraduationYear(int y) 
{
 this.graduationYear = y;
 }
    public void setMajor(String major)
 {
 this.major = major;
 }
    public void setEmail(String email)
 {
 this.email = email;
 }
    public void setPhone(String phone) 
{
 this.phone = phone;
 }
    public void setCurrentCompany(String c)
 {
 this.currentCompany = c;
 }
    public String toString() 
{
        return String.format("Alumni [ID=%d, Name=%s, Grad=%d, Major=%s, Email=%s, Phone=%s, Company=%s]”, id, name, graduationYear, major, email, phone, currentCompany);
    }
}
class Event {
    private int id;
    private String name;
    private String description;
    private LocalDate eventDate;
    private String location;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public Event(String n, String d, LocalDate date, String l) 
{
        this.name = n;
       this.description = d;
       this.eventDate = date;
       this.location = l;
    }
    public Event(int id, String n, String d, LocalDate date, String l) {
        this(n, d, date, l); 
        this.id = id;
    }
    public int getId() 
{ 
return id;
 }
    public String getName()
 { 
return name; 
}
    public String getDescription()
 {
 return description;
 }
    public LocalDate getEventDate()
 {
 return eventDate;
 }
    public String getLocation() 
{
 return location;
 }
    public String getFormattedEventDate()
 {
 return eventDate != null ? eventDate.format(DATE_FORMATTER) : "N/A";
 }
    public void setId(int id)
 {
 this.id = id;
 }
    public void setName(String name)
 {
 this.name = name;
 }
    public void setDescription(String d)
 {
 this.description = d;
 }
    public void setEventDate(LocalDate d)
 {
 this.eventDate = d; 
}
    public void setLocation(String l)
 {
 this.location = l;
 }
    @Override
    public String toString()
 {
        return String.format("Event [ID=%d, Name=%s, Date=%s, Location=%s, Desc=%s]",
                             id, name, getFormattedEventDate(), location, description);
    }
    public static LocalDate parseDate(String dateString) throws DateTimeParseException
 {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }
}
class DatabaseConnector 
{
    private static final String DB_URL = "jdbc:mysql://localhost:3306/alumni_db"; 
    private static final String DB_USER = "mysql";       
    private static final String DB_PASSWORD = "dbms"; 
    private static Connection connection = null;
    private DatabaseConnector() {} 

    public static Connection getConnection() throws SQLException 
{
        if (connection == null || connection.isClosed())
 {
            try {
                System.out.println("Connecting to database...");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connection successful!");
            } 
         catch (SQLException e) {
                System.err.println("Database Connection Error: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    private static void createTablesIfNotExists(Connection conn) {
        String createAlumni = "CREATE TABLE IF NOT EXISTS alumni (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, graduation_year INT, major VARCHAR(100), email VARCHAR(255) UNIQUE, phone VARCHAR(20), current_company VARCHAR(255));";
        String createEvents = "CREATE TABLE IF NOT EXISTS events (
        id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
       description TEXT,
       event_date DATE,
       location VARCHAR(255));”;
        try (Statement stmt = conn.createStatement()) 
{
            stmt.execute(createAlumni);
            stmt.execute(createEvents);
            System.out.println("Tables checked/created.");
        } 
catch (SQLException e) 
{
            System.err.println("Error checking/creating tables: " + e.getMessage());
        }
    }

    public static void closeConnection()
 {
        if (connection != null) 
{
            try
 {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            }
              catch (SQLException e) 
                {
                       System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}

class AlumniDAO 
{
    private Alumni mapRow(ResultSet rs) throws SQLException
 {
        return new Alumni(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("graduation_year"),
            rs.getString("major"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("current_company")
        );
    }
    public boolean addAlumni(Alumni alumni) 
{
        String sql = "INSERT INTO alumni (name, graduation_year, major, email, phone, current_company) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
 {
            pstmt.setString(1, alumni.getName());
            pstmt.setInt(2, alumni.getGraduationYear());
            pstmt.setString(3, alumni.getMajor());
            pstmt.setString(4, alumni.getEmail());
            pstmt.setString(5, alumni.getPhone());
            pstmt.setString(6, alumni.getCurrentCompany());
            return pstmt.executeUpdate() > 0;
        }
 catch (SQLException e)
 {
            System.err.println("Error adding alumni: " + e.getMessage() + (e.getMessage().contains("Duplicate entry") ? " (Email exists?)" : ""));
            return false;
        }
    }
    public Alumni getAlumniById(int id) 
{
        String sql = "SELECT * FROM alumni WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql))
 {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        } catch (SQLException e)
 {
            System.err.println("Error fetching alumni by ID: " + e.getMessage());
            return null;
        }
    }
     public Alumni getAlumniByEmail(String email)
      {
        String sql = "SELECT * FROM alumni WHERE email = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) 
      {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        } 
         catch (SQLException e) 
       {
            System.err.println("Error fetching alumni by Email: " + e.getMessage());
            return null;
        }
    }
    public List<Alumni> getAllAlumni() {
        String sql = "SELECT * FROM alumni ORDER BY name";
        List<Alumni> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             while (rs.next())
            {
                list.add(mapRow(rs));
            }
           }
        catch (SQLException e)
        {
            System.err.println("Error fetching all alumni: " + e.getMessage());
        }
        return list;
    }

     public List<Alumni> searchAlumni(String searchTerm)
    {
        String sql = "SELECT * FROM alumni WHERE LOWER(name) LIKE ? OR LOWER(major) LIKE ? OR LOWER(email) LIKE ? OR LOWER(current_company) LIKE ? ORDER BY name";
        List<Alumni> list = new ArrayList<>();
        String likeTerm = "%" + searchTerm.toLowerCase() + "%";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt =      conn.prepareStatement(sql))
       {
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            pstmt.setString(3, likeTerm);
            pstmt.setString(4, likeTerm);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) 
            {
                list.add(mapRow(rs));
            }
            } 
            catch (SQLException e)
              {
            System.err.println("Error searching alumni: " + e.getMessage());
             }
             return list;
            }
             public boolean updateAlumni(Alumni alumni)
          {
              if (alumni.getId() <= 0)
        {
             System.err.println("Update error: Invalid ID.");
             return false; 
       }
        String sql = "UPDATE alumni SET name=?, graduation_year=?, major=?, email=?, phone=?, current_company=? WHERE id=?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
     {
            pstmt.setString(1, alumni.getName());
            pstmt.setInt(2, alumni.getGraduationYear());
            pstmt.setString(3, alumni.getMajor());
            pstmt.setString(4, alumni.getEmail());
            pstmt.setString(5, alumni.getPhone());
            pstmt.setString(6, alumni.getCurrentCompany());
            pstmt.setInt(7, alumni.getId());
            return pstmt.executeUpdate() > 0;
        }
       catch (SQLException e)
      {
            System.err.println("Error updating alumni: " + e.getMessage() + (e.getMessage().contains("Duplicate entry") ? " (Email conflict?)" : ""));
            return false;
        }
    }
    public boolean deleteAlumni(int id) {
        String sql = "DELETE FROM alumni WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } 
           catch (SQLException e) 
          {
            System.err.println("Error deleting alumni: " + e.getMessage());
            return false;
        }
    }
}
class EventDAO {
    private Event mapRow(ResultSet rs) throws SQLException {
         Date sqlDate = rs.getDate("event_date");
         LocalDate eventDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;
         return new Event(
             rs.getInt("id"),
             rs.getString("name"),
             rs.getString("description"),
             eventDate,
             rs.getString("location")
         );
    }
    public boolean addEvent(Event event) {
        String sql = "INSERT INTO events (name, description, event_date, location) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, event.getEventDate() != null ? Date.valueOf(event.getEventDate()) : null);
            pstmt.setString(4, event.getLocation());
            int rows = pstmt.executeUpdate();
             if (rows > 0) { 
                 try (ResultSet keys = pstmt.getGeneratedKeys()) { if (keys.next())   event.setId(keys.getInt(1)); }
             }
            return rows > 0;
        } 
           catch (SQLException e) {
            System.err.println("Error adding event: " + e.getMessage());
            return false;
        }
    }
    public Event getEventById(int id) {
        String sql = "SELECT * FROM events WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
        {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        } catch (SQLException e) {
            System.err.println("Error fetching event by ID: " + e.getMessage());
            return null;
        }
    }
    public List<Event> getAllEvents() {
        String sql = "SELECT * FROM events ORDER BY event_date DESC";
        List<Event> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all events: " + e.getMessage());
        }
        return list;
    }

    public boolean updateEvent(Event event) {
         if (event.getId() <= 0)
          {
              System.err.println("Update error: Invalid ID.");
              return false;
          }
        String sql = "UPDATE events SET name=?, description=?, event_date=?, location=? WHERE id=?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.getName());
            pstmt.setString(2, event.getDescription());
            pstmt.setDate(3, event.getEventDate() != null ? Date.valueOf(event.getEventDate()) : null);
            pstmt.setString(4, event.getLocation());
            pstmt.setInt(5, event.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating event: " + e.getMessage());
            return false;
        }
    }
    public boolean deleteEvent(int id) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting event: " + e.getMessage());
            return false;
        }
    }
}
public class AlumniManagementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AlumniDAO alumniDAO = new AlumniDAO();
    private static final EventDAO eventDAO = new EventDAO();

    public static void main(String[] args) {
        try {
            DatabaseConnector.getConnection(); 
             // if (DatabaseConnector.DB_URL.contains(":h2:")) 
{
 DatabaseConnector.createTablesIfNotExists(DatabaseConnector.getConnection());
 }
        } catch (SQLException e) {
            System.err.println("FATAL: DB Connection failed. Exiting."); 
            return;
        }
        while (true) {
            displayMainMenu();
            int choice = readIntInput("Enter choice: ");
            switch (choice) {
                case 1: 
               manageAlumni(); 
               break;
                case 2:
              manageEvents();
              break;
                case 0:
              System.out.println("Exiting... Goodbye!");
             DatabaseConnector.closeConnection(); 
             scanner.close();
             return;
             default:
            System.out.println("Invalid choice.");
            }
            System.out.println("\n----------------------------------------\n");
        }
    }
    private static void displayMainMenu() {
        System.out.println("\n===== Main Menu =====");
        System.out.println("1. Manage Alumni");
        System.out.println("2. Manage Events");
        System.out.println("0. Exit");
        System.out.println("=====================");
    }
    private static void manageAlumni() {
        while (true) {
            System.out.println("\n--- Manage Alumni ---");
            System.out.println("1. Add | 2. View | 3. List All | 4. Update | 5. Delete | 6. Search | 0. Back");
            int choice = readIntInput("Alumni choice: ");
            switch (choice) {
                case 1: 
                 addAlumni();
                 break;
                case 2:
                 viewAlumni(); 
                  break;
                case 3: 
                listAllAlumni();
                break;
                case 4:
                updateAlumni();
                break;
                case 5:
                deleteAlumni(); 
                 break;
                case 6: 
                searchAlumni();
                break;
                case 0: 
                return;
                default:
                System.out.println("Invalid choice.");
            }
        }
    }
     private static void manageEvents() {
         while (true) {
            System.out.println("\n--- Manage Events ---");
            System.out.println("1. Add | 2. View | 3. List All | 4. Update | 5. Delete | 0. Back");
            int choice = readIntInput("Event choice: ");
            switch (choice) {
                case 1: 
                addEvent(); 
                break;
                case 2:
                viewEvent(); 
                break;
                case 3:
                listAllEvents();
                break;
                case 4:
                updateEvent(); 
                break;
                case 5: 
                deleteEvent(); 
                 break;
                case 0:
                return;
                default:
                System.out.println("Invalid choice.");
            }
        }
    }
    private static void addAlumni() {
        System.out.println("\n--- Add Alumni ---");
        String name = readStringInput("Name: ");
        String email = readStringInput("Email: ");
        if (name.isEmpty() || email.isEmpty())
         {
           System.out.println("Name/Email required.");
           return;
         }
        int gradYear = readIntInput("Grad Year: ");
       String major = readStringInput("Major: ");
       String phone = readStringInput("Phone (opt): ");
       String company = readStringInput("Company (opt): ");
        Alumni newAlumni = new Alumni(name, gradYear, major, email, phone, company);
        System.out.println(alumniDAO.addAlumni(newAlumni) ? "Alumni added." : "Failed to add alumni.");
    }
    private static void viewAlumni() {
        int id = readIntInput("Enter Alumni ID to view: ");
        Alumni a = alumniDAO.getAlumniById(id);
        System.out.println(a != null ? a : "Alumni ID " + id + " not found.");
    }
    private static void listAllAlumni() {
        System.out.println("\n--- All Alumni ---");
        List<Alumni> list = alumniDAO.getAllAlumni();
        if (list.isEmpty()) 
             System.out.println("No alumni found.");
        else list.forEach(System.out::println);
    }
     private static void searchAlumni() {
        String term = readStringInput("Search term (name, major, email, company): ");
        if(term.trim().isEmpty())
           { 
              System.out.println("Search term empty.");
             return;
          }
        List<Alumni> results = alumniDAO.searchAlumni(term);
        System.out.println(results.isEmpty() ? "No matches found." : "Search Results:");
        results.forEach(System.out::println);
    }

    private static void updateAlumni() 
   {
        int id = readIntInput("Enter Alumni ID to update: ");
        Alumni existing = alumniDAO.getAlumniById(id);
        if (existing == null)
      {
              System.out.println("Alumni ID " + id + " not found."); 
              return; 
         }
        System.out.println("Current: " + existing);
        System.out.println("Enter new info (blank keeps current):");
        String name = readStringInput("Name ["+existing.getName()+"]: "); 
        if (!name.isEmpty()) existing.setName(name);
        String yearStr = readStringInput("Grad Year ["+existing.getGraduationYear()+"]: ");
        if (!yearStr.isEmpty()) 
        try 
         {
          existing.setGraduationYear(Integer.parseInt(yearStr)); 
         } 
         catch (NumberFormatException e)
              {
                 System.out.println("Invalid year.");
              }
        String major = readStringInput("Major ["+existing.getMajor()+"]: "); 
             if (!major.isEmpty()) existing.setMajor(major);
                String email = readStringInput("Email ["+existing.getEmail()+"]: ");
             if (!email.isEmpty()) existing.setEmail(email);
               String phone = readStringInput("Phone ["+existing.getPhone()+"]: "); 
            if (!phone.isEmpty()) existing.setPhone(phone);
              String company = readStringInput("Company ["+existing.getCurrentCompany()+"]: ");
           if (!company.isEmpty()) existing.setCurrentCompany(company);
                 System.out.println(alumniDAO.updateAlumni(existing) ? "Update successful." : "Update failed.");
    }
    private static void deleteAlumni() {
        int id = readIntInput("Enter Alumni ID to delete: ");
        Alumni a = alumniDAO.getAlumniById(id); 
        if (a == null) 
           { 
           System.out.println("Alumni ID " + id + " not found."); 
           return;
           }
        System.out.println("Deleting: " + a);
        if (readStringInput("Sure? (yes/no): ").equalsIgnoreCase("yes")) {
            System.out.println(alumniDAO.deleteAlumni(id) ? "Deleted." : "Deletion failed.");
        } else System.out.println("Cancelled.");
    }
    private static void addEvent() {
        System.out.println("\n--- Add Event ---");
        String name = readStringInput("Event Name: ");
        LocalDate date = readDateInput("Date (YYYY-MM-DD): ");
        if (name.isEmpty() || date == null)
          {
             System.out.println("Name/Date required.");
             return; 
         }
        String desc = readStringInput("Description: ");
        String loc = readStringInput("Location: ");
        Event newEvent = new Event(name, desc, date, loc);
        if(eventDAO.addEvent(newEvent))
       System.out.println("Event added (ID: " + newEvent.getId() + ").");
        else 
         System.out.println("Failed to add event.");
    }
    private static void viewEvent() {
        int id = readIntInput("Enter Event ID to view: ");
        Event e = eventDAO.getEventById(id);
        System.out.println(e != null ? e : "Event ID " + id + " not found.");
    }
    private static void listAllEvents() {
        System.out.println("\n--- All Events ---");
        List<Event> list = eventDAO.getAllEvents();
        if (list.isEmpty()) 
        System.out.println("No events found.");
        Else
        list.forEach(System.out::println);
    }
    private static void updateEvent() {
        int id = readIntInput("Enter Event ID to update: ");
        Event existing = eventDAO.getEventById(id);
        if (existing == null) 
          { 
             System.out.println("Event ID " + id + " not found.");
              return;
          }
        System.out.println("Current: " + existing);
        System.out.println("Enter new info (blank keeps current):");
        String name = readStringInput("Name ["+existing.getName()+"]: ");
        if (!name.isEmpty()) existing.setName(name);
        String desc = readStringInput("Desc ["+existing.getDescription()+"]: ");
        if (!desc.isEmpty()) existing.setDescription(desc);
        LocalDate date = readDateInputOptional("Date ["+existing.getFormattedEventDate()+"]: ");           if (date != null) existing.setEventDate(date);
        String loc = readStringInput("Location ["+existing.getLocation()+"]: "); 
              if (!loc.isEmpty()) existing.setLocation(loc);
                 System.out.println(eventDAO.updateEvent(existing) ? "Update successful." : "Update failed.");
    }
    private static void deleteEvent() {
        int id = readIntInput("Enter Event ID to delete: ");
        Event e = eventDAO.getEventById(id); 
        if (e == null)
        {
           System.out.println("Event ID " + id + " not found.");
           return; 
        }
        System.out.println("Deleting: " + e);
        if (readStringInput("Sure? (yes/no): ").equalsIgnoreCase("yes")) {
             System.out.println(eventDAO.deleteEvent(id) ? "Deleted." : "Deletion failed.");
        }
 else
       System.out.println("Cancelled.");
    }
    private static String readStringInput(String prompt)
   {
        System.out.print(prompt); 
        return scanner.nextLine(); 
    }
    private static int readIntInput(String prompt) {
        while (true) { 
            System.out.print(prompt);
        try { 
           return Integer.parseInt(scanner.nextLine());
             } 
     catch (NumberFormatException e)
 {
    System.out.println("Invalid number.");
 } 
}
   }
    private static LocalDate readDateInput(String prompt) {
        while (true) {
        String ds = readStringInput(prompt);
            if (ds.trim().isEmpty()) {
               System.out.println("Date required."); 
                continue; 
} 
      try { 
         return Event.parseDate(ds);
           } 
     catch (DateTimeParseException e) { 
         System.out.println("Invalid date (YYYY-MM-DD).");
     } }
    }
    private static LocalDate readDateInputOptional(String prompt) { 
        while (true) { 
            String ds = readStringInput(prompt);
               if (ds.trim().isEmpty())
                 return null;
              try {
                return Event.parseDate(ds);
               } 
             catch (DateTimeParseException e) {
                    System.out.println("Invalid date (YYYY-MM-DD) or blank.");
             }
            }
    }
}
