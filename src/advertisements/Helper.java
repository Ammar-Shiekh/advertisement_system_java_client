/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package advertisements;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author Ammar
 */
public class Helper {
    
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static final String HOST = "HOST";
    private static final String NAME = "NAME";
    private static final String PASSWORD = "PASSWORD";
    private static final Collection<String> REQUIRED_CONFIGURATIONS = 
            new HashSet<>(Arrays.asList(new String[] { HOST, NAME, PASSWORD }));
    private static final int MAXIMUM_AUTHENTICATION_ATTEMPTS = 5;
    
    public static boolean isReady() {
        try {
            Properties prop = getProperties();
            return prop.stringPropertyNames().containsAll(REQUIRED_CONFIGURATIONS);
        } catch (IOException e) {
            return false;
        }
    }
    
    public static Advertisement[] getAdvertisements(Authentication auth) 
            throws IOException {
        String json = request(auth.getHost(), Route.REQUEST_ADVERTISEMENTS, null, auth.getToken());
        Gson gson = new Gson();
        return gson.fromJson(json, Advertisement[].class);
    }
    
    public static Authentication getAuthentication() throws InitializingException {
        try {
            Properties prop = getProperties();
            for (int i = 0; i < MAXIMUM_AUTHENTICATION_ATTEMPTS; i++) {
                HashMap<String, String> parameters = new HashMap();
                parameters.put("name", prop.getProperty(NAME));
                parameters.put("password", prop.getProperty(PASSWORD));
                String json = request(prop.getProperty(HOST), Route.LOGIN, urlEncodeUTF8(parameters));
                Gson gson = new Gson();
                try {
                    Authentication auth = gson.fromJson(json, Authentication.class);
                    auth.setHost(prop.getProperty(HOST));
                    return auth;
                } catch (JsonSyntaxException e) {
                    throw new InitializingException();
                }
            }
        } catch (IOException e) {

        }
        throw new InitializingException();
    }
    
    public static void login(String host, String name, String password) 
            throws JsonSyntaxException, UnknownHostException, IOException {
        HashMap<String, String> parameters = new HashMap();
        parameters.put("name", name);
        parameters.put("password", password);
        Helper.request(host, Route.LOGIN, Helper.urlEncodeUTF8(parameters));
        Helper.setConfigurations(host, name, password);
    }
    
    public static void register(String host, String name, String password) 
            throws JsonSyntaxException, UnknownHostException, IOException {
        HashMap<String, String> parameters = new HashMap();
        parameters.put("name", name);
        parameters.put("password", password);
        Helper.request(host, Route.REGISTER, Helper.urlEncodeUTF8(parameters));
        Helper.setConfigurations(host, name, password);
    }
    
    public static void setConfigurations(String host, String name, String password) {
        try {
            Properties prop = new Properties();
            prop.setProperty(HOST, host);
            prop.setProperty(NAME, name);
            prop.setProperty(PASSWORD, password);
            File configFile = new File(CONFIG_FILE_NAME);
            configFile.createNewFile();
            prop.store(new FileOutputStream(configFile), null);
        } catch (IOException e) {
            
        }
    }
    
    public static String request(String host, Route route, String parameters) throws IOException {
        return request(host, route, parameters, null);
    }
    
    public static String request(String host, Route route, String parameters, String token) 
            throws IOException {
        URL url = getURL(host, route);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Accept", "application/json");
        if (token != null) {
            con.setRequestProperty("Authorization", String.format("Bearer %s", token));
        }
        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            if (parameters != null) {
                wr.writeBytes(parameters);
            }
            wr.flush();
        }

        StringBuilder response;
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }
    
    public static String urlEncodeUTF8(Map<?,?> map) {
        StringBuilder sb = new StringBuilder();
        map.entrySet().forEach((entry) -> {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        });
        return sb.toString();       
    }
    
    public static URL getURL(String host, Route route) throws MalformedURLException {
        return new URL("http", host, 8000, String.format("/api/%s", route.getRoute()));
    }
    
    private static Properties getProperties() throws IOException {
        InputStream in = new FileInputStream(CONFIG_FILE_NAME);
        Properties prop = new Properties();
        prop.load(in);
        return prop;
    }
    
    private static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
    
}
