package edu.escuelaing.arem.ASE.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class HttpServer {
    public static String httpClienthtml() {

        return "HTTP/1.1 200 OK\r\n" +
        "Content-Type: text/html\r\n" +
        "\r\n" +
        "<!DOCTYPE html>\n" +
        "<html>\n" +
        "<head>\n" +
        "<title>Búsqueda de Películas</title>\n" +
        "<meta charset=\"UTF-8\">\n" +
        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
        "</head>\n" +
        "<body>\n" +
        "<h1>Introduce un nombre para buscar</h1>\n" +
        "<form action=\"/get\">\n" +
        "<label for=\"name\">Name:</label><br>\n" +
        "<input type=\"text\" id=\"name\" value=\"John\"><br><br>\n" +
        "<input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
        "</form>Busqueda\n" +
        "<div id=\"getrespmsg\"></div>\n" +
        "\n" +
        "<script>\n" +
        "function loadGetMsg(){\n" +
        "let name = document.getElementById(\"name\");\n" +
        "let url = \"get/?t=\" + name.value;\n" +
        "fetch (url, {method: 'GET'})\n" +
        ".then(x => x.text())\n" +
        ".then(y => document.getElementById(\"getrespmsg\").innerHTML = y);\n" +
        "}\n" +
        "</script>\n" +
        "</body>\n" +
        "</html>";
    }
    public static void main(String[] args) throws IOException {
       
        HttpConnectionExample conApi = new HttpConnectionExample();
        ServerSocket serverSocket = null;
        HashMap <String, String> cache = new HashMap<>();
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        Socket clientSocket = null;
        while (running) {

            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine;
            String outputLine = " ";
            boolean firstLine = true;
            String URIstr = "/HOME";
            while ((inputLine = in.readLine()) != null) {
                if (firstLine) {
                    URIstr = inputLine.split(" ")[1];
                    firstLine = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            String response = "";
            if (URIstr.split("/").length > 1){
                if (URIstr.split("/")[1].equals("get")) {
                    if(cache.containsKey(URIstr.split("=")[1])){
                        System.out.println("Guardado en cache");
                        response = cache.get(URIstr.split("=")[1]);
                    }
                    else{
                        response = conApi.getApi(URIstr.split("/")[2]);
                        cache.put(URIstr.split("=")[1],response);
                    }

                }

            }
            else {
                response = httpClienthtml();
            }

            outputLine = response;

            out.println(outputLine);

            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    
}
