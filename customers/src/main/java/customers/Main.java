package customers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

	public static void main(String[] args) {
		
		
        // Datenbankverbindung herstellen
        String url = "jdbc:mysql://localhost:3306/customers_data";  // Verbindung anpassen
        String user = "root";
        String password = "12my!sql34"; // Passwort anpassen

        
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // JSON-Datei einlesen
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonData = objectMapper.readTree(new File("data.json"));

            
            // Daten aus der JSON-Datei auslesen und in die Datenbank einfügen
            insertDataIntoDatabase(connection, jsonData);

            
            // Daten aus der Datenbank abrufen
            retrieveDataFromDatabase(connection);

            
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
	}
	
	
	
	// Methode zum Einfügen von Daten in die Datenbank
    public static void insertDataIntoDatabase(Connection connection, JsonNode jsonData) throws SQLException {


        String insertCustomerSql = "INSERT INTO customer (firstName, lastName, age) VALUES (?, ?, ?)";
        PreparedStatement customerPreparedStatement = connection.prepareStatement(insertCustomerSql, PreparedStatement.RETURN_GENERATED_KEYS);

        customerPreparedStatement.setString(1, jsonData.get("firstName").asText());
        customerPreparedStatement.setString(2, jsonData.get("lastName").asText());
        customerPreparedStatement.setInt(3, jsonData.get("age").asInt());


        // Abrufen der "id"
        ResultSet generatedKeys = customerPreparedStatement.getGeneratedKeys();
        
        if (generatedKeys.next()) {
            int customerId = generatedKeys.getInt(1);


            String insertAddressSql = "INSERT INTO address (customerId, streetAddress, city, state, postalCode) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement addressPreparedStatement = connection.prepareStatement(insertAddressSql);

            addressPreparedStatement.setInt(1, customerId);
            addressPreparedStatement.setString(2, jsonData.get("address").get("streetAddress").asText());
            addressPreparedStatement.setString(3, jsonData.get("address").get("city").asText());
            addressPreparedStatement.setString(4, jsonData.get("address").get("state").asText());
            addressPreparedStatement.setString(5, jsonData.get("address").get("postalCode").asText());

            addressPreparedStatement.executeUpdate();

            String insertPhoneNumberSql = "INSERT INTO phoneNumber (customerId, type, number) VALUES (?, ?, ?)";
            PreparedStatement phonePreparedStatement = connection.prepareStatement(insertPhoneNumberSql);


            for (JsonNode phoneNumber : jsonData.get("phoneNumber")) {
                phonePreparedStatement.setInt(1, customerId);
                phonePreparedStatement.setString(2, phoneNumber.get("type").asText());
                phonePreparedStatement.setString(3, phoneNumber.get("number").asText());
                phonePreparedStatement.executeUpdate();
            } 
        }
        
        System.out.println("Daten eingefügt");
        


      }
        
        
        // Methode zum Abrufen von Daten aus der Datenbank
        public static void retrieveDataFromDatabase(Connection connection) throws SQLException {
        	System.out.println("-----------------------------");
        	System.out.println("Kundendaten");
        	System.out.println("-----------------------------");
            String selectCustomersSql = "SELECT * FROM customer";
            PreparedStatement selectCustomersStatement = connection.prepareStatement(selectCustomersSql);
            ResultSet customerResultSet = selectCustomersStatement.executeQuery();
            
            // Kundendaten ausgeben
            while (customerResultSet.next()) {
            	
                int customerId = customerResultSet.getInt("id");
                String firstName = customerResultSet.getString("firstName");
                String lastName = customerResultSet.getString("lastName");
                int age = customerResultSet.getInt("age");

                System.out.println("Kunde ID: " + customerId);
                System.out.println("Vorname: " + firstName);
                System.out.println("Nachname: " + lastName);
                System.out.println("Alter: " + age);


                String selectAddressSql = "SELECT * FROM address WHERE customerId = ?";
                PreparedStatement selectAddressStatement = connection.prepareStatement(selectAddressSql);
                selectAddressStatement.setInt(1, customerId);
                ResultSet addressResultSet = selectAddressStatement.executeQuery();

                // Adressinformationen ausgeben
                while (addressResultSet.next()) {
                    String streetAddress = addressResultSet.getString("streetAddress");
                    String city = addressResultSet.getString("city");
                    String state = addressResultSet.getString("state");
                    String postalCode = addressResultSet.getString("postalCode");

                    System.out.println("Straßenadresse: " + streetAddress);
                    System.out.println("Stadt: " + city);
                    System.out.println("Bundesland: " + state);
                    System.out.println("Postleitzahl: " + postalCode);
                }
                

                String selectPhoneNumbersSql = "SELECT * FROM phoneNumber WHERE customerId = ?";
                PreparedStatement selectPhoneNumbersStatement = connection.prepareStatement(selectPhoneNumbersSql);
                selectPhoneNumbersStatement.setInt(1, customerId);
                ResultSet phoneNumbersResultSet = selectPhoneNumbersStatement.executeQuery();

                // Telefonnummern ausgeben
                while (phoneNumbersResultSet.next()) {
                    String type = phoneNumbersResultSet.getString("type");
                    String number = phoneNumbersResultSet.getString("number");

                    System.out.println("Telefonnummer-Typ: " + type);
                    System.out.println("Telefonnummer: " + number);
                }
                

                System.out.println("-----------------------------");
            }
    }

}
