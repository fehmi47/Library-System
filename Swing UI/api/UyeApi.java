package api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.UyeDao;
import entity.Uye;

import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class UyeApi {

    private static UyeDao uyeDao = new UyeDao();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/api/uye", new UyeHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started at http://localhost:8000");
    }

    static class UyeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");
                exchange.getResponseHeaders().add("Content-Type", "application/json");

                if (method.equals("GET")) {
                    if (pathParts.length == 3 && pathParts[2].matches("\\d+")) { // sadece sayı ise ID
                        int id = Integer.parseInt(pathParts[2]);
                        Uye uye = uyeDao.getByID(id);
                        sendResponse(exchange, uyeToJson(uye));
                    } else {
                        ArrayList<Uye> uyeler = uyeDao.findAll();
                        sendResponse(exchange, uyelerToJson(uyeler));
                    }
                } else if (method.equals("POST")) {
                    Uye yeniUye = parseRequestBody(exchange);
                    boolean saved = uyeDao.save(yeniUye);
                    sendResponse(exchange, "{\"success\":" + saved + "}");
                } else if (method.equals("PUT")) {
                    Uye guncelUye = parseRequestBody(exchange);
                    boolean updated = uyeDao.update(guncelUye);
                    sendResponse(exchange, "{\"success\":" + updated + "}");
                } else if (method.equals("DELETE")) {
                    if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        boolean deleted = uyeDao.delete(id);
                        sendResponse(exchange, "{\"success\":" + deleted + "}");
                    } else {
                        sendResponse(exchange, "{\"error\":\"ID eksik\"}");
                    }
                } else {
                    sendResponse(exchange, "{\"error\":\"Unsupported method\"}");
                }
            } catch (Exception e) {
                try {
                    sendResponse(exchange, "{\"error\":\"" + e.getMessage() + "\"}");
                } catch (Exception ignored) {}
            }
        }

        private Uye parseRequestBody(HttpExchange exchange) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            String body = sb.toString();

            // Basit parsing (manuel JSON, "" ile yazılan değerler)
            Uye uye = new Uye();
            if (body.contains("\"ID\"")) {
                uye.setID(Integer.parseInt(getJsonValue(body, "ID")));
            }
            uye.setAd(getJsonValue(body, "ad"));
            uye.setSoyad(getJsonValue(body, "soyad"));
            uye.setTelefonNo(getJsonValue(body, "telefonNo"));
            uye.setEposta(getJsonValue(body, "eposta"));
            uye.setCinsiyet(Uye.CINSIYET.valueOf(getJsonValue(body, "cinsiyet")));
            return uye;
        }

        private String getJsonValue(String json, String key) {
            String pattern = "\"" + key + "\"\\s*:\\s*\"(.*?)\"";
            java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
            if (m.find()) return m.group(1);
            return "";
        }

        private String uyeToJson(Uye uye) {
            if (uye == null) return "null";
            return "{"
                    + "\"ID\":" + uye.getID() + ","
                    + "\"ad\":\"" + uye.getAd() + "\","
                    + "\"soyad\":\"" + uye.getSoyad() + "\","
                    + "\"telefonNo\":\"" + uye.getTelefonNo() + "\","
                    + "\"eposta\":\"" + uye.getEposta() + "\","
                    + "\"cinsiyet\":\"" + uye.getCinsiyet() + "\""
                    + "}";
        }

        private String uyelerToJson(ArrayList<Uye> uyeler) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < uyeler.size(); i++) {
                sb.append(uyeToJson(uyeler.get(i)));
                if (i < uyeler.size() - 1) sb.append(",");
            }
            sb.append("]");
            return sb.toString();
        }

        private void sendResponse(HttpExchange exchange, String response) throws Exception {
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
