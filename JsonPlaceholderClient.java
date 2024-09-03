import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class JsonPlaceholderClient {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/users";

    public static void main(String[] args) {
        try {
            String allUsers = getAllUsers();
            System.out.println("All Users:\n" + allUsers);

            String userById = getUserById(1);
            System.out.println("\nUser by ID:\n" + userById);

            String userByUsername = getUserByUsername("Bret");
            System.out.println("\nUser by Username:\n" + userByUsername);

            String newUserJson = "{\"name\":\"John Doe\",\"username\":\"johndoe\",\"email\":\"john.doe@example.com\"}";
            String createdUser = createUser(newUserJson);
            System.out.println("\nCreated User:\n" + createdUser);

            String updatedUserJson = "{\"name\":\"John Doe\",\"username\":\"johndoe\",\"email\":\"john.doe@updated.com\"}";
            int updateStatus = updateUser(1, updatedUserJson);
            System.out.println("\nUpdate Status: " + updateStatus);

            int deleteStatus = deleteUser(1);
            System.out.println("\nDelete Status: " + deleteStatus);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAllUsers() throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return in.lines().collect(Collectors.joining("\n"));
        }
    }

    public static String getUserById(int id) throws Exception {
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return in.lines().collect(Collectors.joining("\n"));
        }
    }

    public static String getUserByUsername(String username) throws Exception {
        URL url = new URL(BASE_URL + "?username=" + username);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return in.lines().collect(Collectors.joining("\n"));
        }
    }

    public static String createUser(String jsonInputString) throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return in.lines().collect(Collectors.joining("\n"));
        }
    }

    public static int updateUser(int id, String jsonInputString) throws Exception {
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        return connection.getResponseCode();
    }

    public static int deleteUser(int id) throws Exception {
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        return connection.getResponseCode();
    }
}
