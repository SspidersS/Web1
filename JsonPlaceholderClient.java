import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JsonPlaceholderClient {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/users";

    public static void main(String[] args) throws Exception {
        String newUser = "{\"name\":\"John Doe\",\"username\":\"johndoe\",\"email\":\"johndoe@example.com\"}";
        String createdUser = createUser(newUser);
        System.out.println("Created User: " + createdUser);

        String updatedUserJson = "{\"id\":1,\"name\":\"John Updated Doe\",\"username\":\"johndoe\",\"email\":\"johndoe@example.com\"}";
        String updatedUser = updateUser(1, updatedUserJson);
        System.out.println("Updated User: " + updatedUser);

        int deleteStatus = deleteUser(1);
        System.out.println("Delete status: " + deleteStatus);

        String users = getAllUsers();
        System.out.println("All Users: " + users);

        String userById = getUserById(1);
        System.out.println("User by ID: " + userById);

        String userByUsername = getUserByUsername("Bret");
        System.out.println("User by Username: " + userByUsername);

        saveCommentsOfLastPost(1);

        String openTodos = getOpenTodos(1);
        System.out.println("Open Todos for User 1: " + openTodos);
    }

    public static String createUser(String userJson) throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = userJson.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return getResponse(conn);
    }

    public static String updateUser(int id, String userJson) throws Exception {
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = userJson.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return getResponse(conn);
    }

    public static int deleteUser(int id) throws Exception {
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");

        return conn.getResponseCode();  // Повертає код статусу (наприклад, 200)
    }

    public static String getAllUsers() throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        return getResponse(conn);
    }

    public static String getUserById(int id) throws Exception {
        URL url = new URL(BASE_URL + "/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        return getResponse(conn);
    }

    public static String getUserByUsername(String username) throws Exception {
        URL url = new URL(BASE_URL + "?username=" + username);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        return getResponse(conn);
    }

    public static void saveCommentsOfLastPost(int userId) throws Exception {
        URL url = new URL(BASE_URL + "/" + userId + "/posts");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        String postsResponse = getResponse(conn);
        String lastPostId = extractLastPostId(postsResponse);

        url = new URL("https://jsonplaceholder.typicode.com/posts/" + lastPostId + "/comments");
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        String commentsResponse = getResponse(conn);

        String filename = "user-" + userId + "-post-" + lastPostId + "-comments.json";
        try (FileWriter file = new FileWriter(filename)) {
            file.write(commentsResponse);
        }

        System.out.println("Comments saved to file: " + filename);
    }

    public static String getOpenTodos(int userId) throws Exception {
        URL url = new URL(BASE_URL + "/" + userId + "/todos");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        String todosResponse = getResponse(conn);
        String openTodos = extractOpenTodos(todosResponse);
        return openTodos;
    }

    private static String getResponse(HttpURLConnection conn) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    private static String extractLastPostId(String postsResponse) {
        int lastIndex = postsResponse.lastIndexOf("\"id\":");
        int startIndex = postsResponse.indexOf(":", lastIndex) + 1;
        int endIndex = postsResponse.indexOf(",", startIndex);
        return postsResponse.substring(startIndex, endIndex).trim();
    }

    private static String extractOpenTodos(String todosResponse) {
        StringBuilder openTodos = new StringBuilder("[");
        String[] todos = todosResponse.split("\\{");
        boolean first = true;

        for (String todo : todos) {
            if (todo.contains("\"completed\":false")) {
                if (!first) {
                    openTodos.append(",");
                } else {
                    first = false;
                }
                openTodos.append("{").append(todo.trim());
            }
        }
        openTodos.append("]");
        return openTodos.toString();
    }
}
