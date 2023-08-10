//Important imports for the project
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Console;



public class EduConnectSystem { //the mother class that contains all the methods of system
    private static Scanner scanner = new Scanner(System.in);
    private static Map<String, User> userMap = new HashMap<>();
    private static User user = null;
    private static boolean isLoggedIn = false; // Track login status
    public static int maxProgress = 8; // The maximum progress value based on the number of menu options
    private static List<School> schools = new ArrayList<>();

    static {
        schools.add(new School("KISU", "Bukoto", 1));//schools to be recommend
        schools.add(new School("NorthGreen", "Ntinda", 2));
        schools.add(new School("Kampala Parents", "Naguru", 3));
    }
    

    public static int getMaxProgress() {
        return maxProgress;
    }

    //main login implementation
    public static void main(String[] args) {
        displayWelcomeMessage();
        initializeUsers();
        boolean shouldExit = false;
        int loginAttempts = 0;

        while (!shouldExit) {
            if (!isLoggedIn) {
                isLoggedIn = login();
                loginAttempts++;

                if (!isLoggedIn) {
                    System.out.println("Invalid credentials.");

                    // Allow three login attempts before exit
                    if (loginAttempts >= 3) {
                        System.out.println("Too many login attempts. Exiting...");
                        shouldExit = true;
                    }
                }
            }

            if (isLoggedIn) {
                handleFunctionality(user);

                // Prompt user if they want to logout
                if (!shouldExit) {
                    System.out.println("Do you want to logout? (y/n)");
                    String response = scanner.nextLine();
                    if (response.equalsIgnoreCase("y")) {
                        if (logoutWithCredentials()) {
                            shouldExit = true; // Exit the program after successful logout
                            System.out.println("Logout successful!");
                        } else {
                            System.out.println("Invalid credentials. You cannot logout.");
                        }
                    }
                }
            }
        }
    }

   

    private static boolean logoutWithCredentials() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        return loginWithCredentials(username, password);
    }

    private static boolean loginWithCredentials(String username, String password) {
        User currentUser = userMap.get(username);
        if (currentUser != null && currentUser.authenticate(password)) {
            user = currentUser;
            return true;
        } else {
            return false;
        }
    }


//INITIALIZED USERS   
    private static void initializeUsers() {
        // Create some sample users 
        User user1 = new User("user1", "password1", "user1@example.com", "student", "10th");
        User user2 = new User("user2", "password2", "user2@example.com", "teacher", null);
        User user3 = new User("user3", "password3", "user3@example.com", "parent", null);

        userMap.put(user1.getUsername(), user1);
        userMap.put(user2.getUsername(), user2);
        userMap.put(user3.getUsername(), user3);
    }
//LOGOUT IMPLEMENTATION
    private static void logout() {
        if (user != null) {
            user = null;
            isLoggedIn = false;
            System.out.println("Logout successful!");
        }
    }
//WELCOME MESSAGE
    private static void displayWelcomeMessage() {
        System.out.println("Welcome to the EduConnectSystem! Where quality education matters to all!");
    }

//LOGIN IMPLEMENTATION
    private static boolean login() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        Console console = System.console();
        if (console == null) {
            System.out.println("Console not available. Please run the program from the command line.");
            return false;
        }

        char[] passwordChars = console.readPassword("Enter your password: ");
        String password = new String(passwordChars);

        User currentUser = userMap.get(username);
        if (currentUser != null && currentUser.authenticate(password)) {
            user = currentUser;
            System.out.println("\nLogin successful. Welcome, " + user.getRole() + " " + user.getUsername() + "!");
            return true;
        } else {
            System.out.println("\nInvalid username or password.");
    
            System.out.print("Do you want to create an account? (y/n): ");
            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("y")) {
                System.out.print("Enter your email: ");
                String email = scanner.nextLine();
    
                
                // Validate the email format and lowercase requirement
            if (!isValidCredentials(email)) {
                System.out.println("Invalid email format or email does not start with lowercase letters. Account creation failed.");
                return false;
            }
    
                System.out.print("Enter your role (student, teacher, or parent): ");
                String role = scanner.nextLine();
    
                // Check if the role is valid
                if (!role.equalsIgnoreCase("student") && !role.equalsIgnoreCase("teacher") && !role.equalsIgnoreCase("parent")) {
                    System.out.println("Invalid role. Account creation failed.");
                    return false;
                }
    
                // For student role, ask for the grade
                String grade = null;
                if (role.equalsIgnoreCase("student")) {
                    System.out.print("Enter the grade of the student: ");
                    grade = scanner.nextLine();
                }
    
                // Check if the username is already taken
                if (userMap.containsKey(username)) {
                    System.out.println("Username already taken. Account creation failed.");
                    return false;
                }
    
                // Create a new user account with the provided grade (can be null for non-student roles)
                User newUser = new User(username, password, email, role, grade);
                userMap.put(newUser.getUsername(), newUser);
                user = newUser;
                System.out.println("\nAccount created successfully. Welcome, " + user.getRole() + " " + user.getUsername() + "!");
                return true;
            } else {
                return false;
            }
        }
    }
    
        
    private static boolean isValidCredentials(String email) {
        String emailPattern = "^[a-z0-9._%+-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

//EDUCONNECT HANDLE FUNCTIONALITY  
    private static void handleFunctionality(User user) {
        boolean isExit = false;
    
        while (!isExit) {
            displayMainMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character
    
            switch (choice) {
                case 1:
                    handleScholarshipFunctionality();
                    break;
                case 2:
                recommendSchools(user);
                break;
                case 3:
                    selectMaterial();
                    break;
                case 4:
                    displayUserProfile();
                    break;
                case 5:
                    collectFeedback();
                    break;
                case 6:
                    if (user.getRole().equalsIgnoreCase("student")) {
                        viewStudentProgress(user);
                    } else if (user.getRole().equalsIgnoreCase("teacher")) {
                        viewStudentProgressAsTeacher();
                    } else if (user.getRole().equalsIgnoreCase("parent")) {
                        User parent = loginAsParent();
                        if (parent != null) {
                            viewStudentProgressAsParent(parent);
                        }
                    }
                    break;
                case 7:
                    claimScholasticMaterials(user); // Pass the user object
                    break;
                case 8:
                    System.out.println("Do you want to exit? (y/n)");
                    String response = scanner.nextLine();
                    if (response.equalsIgnoreCase("y")) {
                        logout();
                        isExit = true;
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
       
//MAIN MENU
    private static void displayMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Scholarship Management");
        System.out.println("2. Recommend Schools");
        System.out.println("3. Select Materials");
        System.out.println("4. User Profile");
        System.out.println("5. Provide Feedback");
        System.out.println("6. View Student Progress");
        System.out.println("7. Claim Scholastic Material");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }

//SCHOLARSHIPS
    private static void handleScholarshipFunctionality() {
        boolean isExit = false;
    
        while (!isExit) {
            displayScholarshipMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character
    
            switch (choice) {
                case 1:
                    displayScholarships();
                    break;
                case 2:
                    applyForScholarship(); // Call the applyForScholarship method directly
                    break;
                case 3:
                    displayUserScholarships();
                    break;
                case 4:
                    isExit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private static void applyForScholarship() {
        List<Scholarship> scholarships = getAvailableScholarships();
    
        System.out.println("\nAvailable Scholarships:");
        for (int i = 0; i < scholarships.size(); i++) {
            System.out.println((i + 1) + ". " + scholarships.get(i).getName());
        }
    
        System.out.print("Enter the index of the scholarship you want to apply for: ");
        int scholarshipIndex = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
    
        if (scholarshipIndex >= 1 && scholarshipIndex <= scholarships.size()) {
            Scholarship selectedScholarship = scholarships.get(scholarshipIndex - 1);
            user.applyForScholarship(selectedScholarship); // Use the existing applyForScholarship method
        } else {
            System.out.println("Invalid scholarship index. Please try again.");
        }
    }

    private static void displayScholarshipMenu() {
        System.out.println("\nScholarship Menu:");
        System.out.println("1. View Available Scholarships");
        System.out.println("2. Apply for a Scholarship");
        System.out.println("3. View Applied Scholarships");
        System.out.println("4. Go Back");
        System.out.print("Enter your choice: ");
    }

    private static void displayScholarships() {
        List<Scholarship> scholarships = getAvailableScholarships();

        System.out.println("\nAvailable Scholarships:");
        for (Scholarship scholarship : scholarships) {
            System.out.println("Name: " + scholarship.getName());
            System.out.println("Amount: $" + scholarship.getAmount());
            System.out.println();
        }
    }

    private static List<Scholarship> getAvailableScholarships() {
        List<Scholarship> scholarships = new ArrayList<>();
        scholarships.add(new Scholarship("Scholarship 1", "Scholarship for Excellence", 1000.0));
        scholarships.add(new Scholarship("Scholarship 2", "Merit-Based Scholarship", 2000.0));
        scholarships.add(new Scholarship("Scholarship 3", "Financial Need Scholarship", 3000.0));
        return scholarships;
    }


    private static void displayUserScholarships() {
        List<Scholarship> appliedScholarships = user.getAppliedScholarships();
        if (appliedScholarships.isEmpty()) {
            System.out.println("\nYou have not applied for any scholarships.");
        } else {
            System.out.println("\nApplied Scholarships:");
            for (Scholarship scholarship : appliedScholarships) {
                System.out.println("Name: " + scholarship.getName());
                System.out.println("Amount: $" + scholarship.getAmount());
                System.out.println();
            }
        }
    }
//SELECT MATERIAL HUB
    private static void selectMaterial() {
        System.out.println("\nMaterial Selection");
        System.out.println("1. Study Materials");
        System.out.println("2. Scholastic Materials");
        System.out.println("3. Go Back");
        System.out.print("Enter your choice: ");
        int materialChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        switch (materialChoice) {
            case 1:
                handleStudyMaterial();
                break;
            case 2:
                claimScholasticMaterials(user);
                break;
            case 3:
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }
//STUDY MATERIALS
    private static void handleStudyMaterial() {
        System.out.println("\nStudy Materials");

        List<String> studyMaterials = getStudyMaterials();

        System.out.println("Available Study Materials:");
        for (int i = 0; i < studyMaterials.size(); i++) {
            System.out.println((i + 1) + ". " + studyMaterials.get(i));
        }

        System.out.print("Enter the number of the study material you want to access: ");
        int studyMaterialChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (studyMaterialChoice >= 1 && studyMaterialChoice <= studyMaterials.size()) {
            String selectedStudyMaterial = studyMaterials.get(studyMaterialChoice - 1);
            System.out.println("Selected Study Material: " + selectedStudyMaterial);
            displayStudyMaterialDetails(selectedStudyMaterial);
        } else {
            System.out.println("Invalid study material index. Please try again.");
        }
    }

    private static List<String> getStudyMaterials() {
        // Return a list of study materials 
        List<String> studyMaterials = new ArrayList<>();
        studyMaterials.add("English Dictionary");
        studyMaterials.add("School Atlas");
        studyMaterials.add("Chemistry Book");
        return studyMaterials;
    }

    private static void displayStudyMaterialDetails(String studyMaterial) {
        
        switch (studyMaterial) {
            case "English Dictionary":
                System.out.println("Study Material Details:");
                System.out.println("Content: Contains various English words with their meanings.");
                System.out.println("Size: 1500 pages");
                System.out.println("Publisher: Oxford University Press");
                break;
            case "School Atlas":
                System.out.println("Study Material Details:");
                System.out.println("Content: Detailed maps of different countries and regions.");
                System.out.println("Size: 100 pages");
                System.out.println("Publisher: National Geographic");
                break;
            case "Chemistry Book":
                System.out.println("Study Material Details:");
                System.out.println("Content: Covers various topics in chemistry.");
                System.out.println("Size: 500 pages");
                System.out.println("Publisher: Pearson");
                break;
            default:
                System.out.println("Additional details for " + studyMaterial + " are not available.");
        }
    }

//SCHOLAR MATERIALS
    private static void claimScholasticMaterials(User user) {
    System.out.println("\nClaim Scholastic Materials");

    List<String> ScholasticMaterials = getScholasticMaterials();

    System.out.println("Available Scholastic Materials for " + user.getUsername() + ":");
    for (int i = 0; i < ScholasticMaterials.size(); i++) {
        System.out.println((i + 1) + ". " + ScholasticMaterials.get(i));
    }

    System.out.print("Enter the number of the Scholastic material you want to claim: ");
    int ScholasticMaterialChoice = scanner.nextInt();
    scanner.nextLine(); // Consume the newline character

    if (ScholasticMaterialChoice >= 1 && ScholasticMaterialChoice <= ScholasticMaterials.size()) {
        String selectedScholasticMaterial = ScholasticMaterials.get(ScholasticMaterialChoice - 1);
        System.out.println("Selected Scholastic Material: " + selectedScholasticMaterial);

        // Obtain the selected scholarship from the available scholarships list
        List<Scholarship> scholarships = getAvailableScholarships();
        System.out.println("Available Scholarships:");
        for (int i = 0; i < scholarships.size(); i++) {
            System.out.println((i + 1) + ". " + scholarships.get(i).getName());
        }

        System.out.print("Enter the number of the scholarship you want to claim the Scholastic material for: ");
        int scholarshipIndex = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        if (scholarshipIndex >= 1 && scholarshipIndex <= scholarships.size()) {
            Scholarship selectedScholarship = scholarships.get(scholarshipIndex - 1);
            // Check if the user is a student and has applied for scholarships
            if (user.getRole().equalsIgnoreCase("student") && !user.getAppliedScholarships().isEmpty()) {
                // Check if the user has applied for the selected scholarship
                if (user.getAppliedScholarships().contains(selectedScholarship)) {
                    // Call the displayScholasticMaterialDetails method to show the details of the selected Scholastic material
                    displayScholasticMaterialDetails(selectedScholasticMaterial);
                    // Now, call the claimScholasticMaterial method from the User class
                    user.claimScholasticMaterial(selectedScholarship, selectedScholasticMaterial);
                } else {
                    System.out.println("You must apply for the selected scholarship before claiming this Scholastic material.");
                }
            } else {
                System.out.println("You are not eligible to claim any Scholastic materials.");
            }
        } else {
            System.out.println("Invalid scholarship index. Please try again.");
        }
    } else {
        System.out.println("Invalid Scholastic material index. Please try again.");
    }
}

    

    private static List<String> getScholasticMaterials() {
        // Return a list of Scholastic materials 
        List<String> ScholasticMaterials = new ArrayList<>();
        ScholasticMaterials.add("Rotatrim");
        ScholasticMaterials.add("Ruled Papers");
        ScholasticMaterials.add("Toilet Tissues");
        return ScholasticMaterials;
    }

    private static void displayScholasticMaterialDetails(String ScholasticMaterial) {
        System.out.println("Scholastic Material Details:");
        System.out.println("Content: They must be taken to school.");
    }

//RECOMMEND SCHOOLS
   
    private static void recommendSchools(User user) {
        if (user.getRole().equalsIgnoreCase("student")) {
            recommendSchoolsForStudent(user);
        } else if (user.getRole().equalsIgnoreCase("teacher")) {
            recommendSchoolsForTeacher(user);
        } else if (user.getRole().equalsIgnoreCase("parent")) {
            recommendSchoolsForParent(user);
        } else {
            System.out.println("School recommendation is not available for this user role.");
        }
    }



    private static void recommendSchoolsForStudent(User student) {
        System.out.println("Schools recommended for student " + student.getUsername() + ":");
        
        for (School school : schools) {
            System.out.println(school.getName() + " (" + school.getLocation() + ") - Rating: " + school.getRating());
        }
    }
    
    
private static void recommendSchoolsForTeacher(User teacher) {
    System.out.println("Schools recommended for teacher " + teacher.getUsername() + ":");
    
    for (School school : schools) {
        System.out.println(school.getName() + " (" + school.getLocation() + ") - Rating: " + school.getRating());
    }
}

private static void recommendSchoolsForParent(User parent) {
    System.out.println("Schools recommended for parent " + parent.getUsername() + ":");
    
    for (School school : schools) {
        System.out.println(school.getName() + " (" + school.getLocation() + ") - Rating: " + school.getRating());
    }
}


//DISPLAY PROFILE
    private static void displayUserProfile() {
        System.out.println("\nUser Profile");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Role: " + user.getRole());


        // Display the grade if the user is a student
    if (user.getRole().equalsIgnoreCase("student")) {
        System.out.println("Grade: " + user.getGrade());
    }
    }
//COLLECT FEEDBACK
    private static void collectFeedback() {
        System.out.print("\nEnter your feedback: ");
        String feedback = scanner.nextLine();
        user.addFeedback(feedback);
        System.out.println("Feedback submitted successfully!");
    }

//VIEW STUDENT PROGRESS
    private static User loginAsParent() {
        System.out.print("Enter your username (parent): ");
        String username = scanner.nextLine();
        System.out.print("Enter your password (parent): ");
        String password = scanner.nextLine();
    
        // Check if the user exists and the credentials are correct
        User parent = userMap.get(username);
        if (parent != null && parent.getPassword().equals(password) && parent.getRole().equalsIgnoreCase("parent")) {
            System.out.println("Login successful as a parent.");
            return parent;
        } else {
            System.out.println("Invalid username or password for parent.");
            return null;
        }
    }


    private static void viewStudentProgressAsTeacher() {
        System.out.print("Enter the username of the student whose progress you want to view: ");
        String studentUsername = scanner.nextLine();
    
        // Check if the student exists in the userMap
        User student = userMap.get(studentUsername);
        if (student != null && student.getRole().equalsIgnoreCase("student")) {
            // View academic progress of the student
            int academicProgress = student.getAcademicProgress();
            System.out.println("Academic progress of " + student.getUsername() + ": " + academicProgress + "%");
        } else {
            System.out.println("Student with username " + studentUsername + " not found.");
        }
    }
    
    private static void viewStudentProgressAsParent(User parent) {
        System.out.print("Enter the username of the student whose progress you want to view: ");
        String studentUsername = scanner.nextLine();
    
        // Check if the student exists in the userMap
        User student = userMap.get(studentUsername);
        if (student != null && student.getRole().equalsIgnoreCase("student")) {
            // View academic progress of the student
            int academicProgress = student.getAcademicProgress();
            System.out.println("Academic progress of " + student.getUsername() + ": " + academicProgress + "%");
        } else {
            System.out.println("Student with username " + studentUsername + " not found.");
        }
    }




    private static void viewStudentProgress(User viewer) {
        System.out.print("Enter your username: ");
        String viewerUsername = scanner.nextLine();
    
        // Check if the viewer exists in the userMap
        User viewerUser = userMap.get(viewerUsername);
        if (viewerUser != null) {
            if (viewer.getRole().equalsIgnoreCase("student") && viewerUser.equals(viewer)) {
                // The viewer is a student and is viewing their own progress
                
                int academicProgress = viewer.getAcademicProgress();
                System.out.println("Your academic progress: " + academicProgress + "%");
            } else if (viewer.getRole().equalsIgnoreCase("teacher") || viewer.getRole().equalsIgnoreCase("parent")) {
                // The viewer is a teacher or parent and wants to view the progress of a student
                System.out.print("Enter the username of the student whose progress you want to view: ");
                String studentUsername = scanner.nextLine();
    
                // Check if the student exists in the userMap
                User student = userMap.get(studentUsername);
                if (student != null) {
                    // View academic progress of the student
                    
                    int academicProgress = student.getAcademicProgress();
                    System.out.println("Academic progress of " + student.getUsername() + ": " + academicProgress + "%");
                } else {
                    System.out.println("Student with username " + studentUsername + " not found.");
                }
            } else {
                System.out.println("You don't have permission to view student progress.");
            }
        } else {
            System.out.println("Viewer with username " + viewerUsername + " not found.");
        }
    }
}

//DEFINED SINGLE CLASSES; User, school, scholarship
class User {
    private String username;
    private String hashedPassword; // Store the hashed password
    private String email;
    private String role;
    private List<Scholarship> appliedScholarships;
    private List<String> feedback;
    private int progress;
    private String grade;
    private int academicProgress;
    private Scholarship scholarship;
    public Map<String, Scholarship> claimedScholarships; // Keep track of the claimed scholarships and associated materials

    public User(String username, String password, String email, String role, String grade) {
        this.username = username;
        this.hashedPassword = hashPassword(password); // Store the hashed password
        this.email = email;
        this.role = role;
        this.grade = grade;
        this.academicProgress = 0;
        this.appliedScholarships = new ArrayList<>();
        this.feedback = new ArrayList<>();
        this.progress = 0;
        this.claimedScholarships = new HashMap<>();
        this.scholarship = null; // Initialize the scholarship as null by default
    }


    // Method to update academic progress
    public void updateAcademicProgress(int progress) {
        this.academicProgress = progress;
        
    }

    // Method to get academic progress
    public int getAcademicProgress() {
        return academicProgress;
    }


    public String getGrade() {
        return grade;}



    public boolean authenticate(String password) {
        return hashedPassword.equals(hashPassword(password));
    }

    

    public Scholarship getScholarship() {
        return scholarship;
    }


    // Hash the password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed.", e);
        }
    }




    public void claimScholasticMaterial(Scholarship scholarship, String ScholasticMaterial) {
        if (claimedScholarships.containsKey(ScholasticMaterial)) {
            System.out.println("You have already claimed " + ScholasticMaterial + " from this scholarship.");
            return;
        }

        incrementProgress(); // Update user's progress
        collectFeedback(ScholasticMaterial); // Add the material to the user's claimed Scholastic materials

        // Store the scholarship information
        claimedScholarships.put(ScholasticMaterial, scholarship);

        System.out.println("Claiming " + ScholasticMaterial + " for " + getUsername() + " from Scholarship: " + scholarship.getName() + " is Done!");
    }




    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return "********"; 
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public List<Scholarship> getAppliedScholarships() {
        return appliedScholarships;
    }

    public int getProgress() {
        return progress;
    }

    public void applyForScholarship(Scholarship scholarship) {
        appliedScholarships.add(scholarship);
    }

    public void incrementProgress() {
        if (progress < EduConnectSystem.maxProgress) {
            progress++;
        }
    }

    public void addFeedback(String feedback) {
        this.feedback.add(feedback);
    }

    public void collectFeedback(String ScholasticMaterial) {
        System.out.println("Collecting feedback for " + ScholasticMaterial + "...");
    }
}

class Scholarship {
    private String name;
    private String description;
    private double amount;

    public Scholarship(String name, String description, double amount) {
        this.name = name;
        this.description = description;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }
}

    class School {
            private String name;
            private String location;
            private double rating;
        
            public School(String name, String location, double rating) {
                this.name = name;
                this.location = location;
                this.rating = rating;
            }
        
            public String getName() {
                return name;
            }
        
            public String getLocation() {
                return location;
            }
        
            public double getRating() {
                return rating;
            }
        }
