package com.mycompany.inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnector {
    
    private static DBConnector instance = null;
    private Connection connection;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/almacen";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private DBConnector() {
        try {
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("✅ Conexión con la base de datos 'almacen' establecida.");
        } catch (SQLException e) {
            System.err.println("❌ ERROR: No se pudo conectar a la base de datos 'almacen'.");
            System.err.println("Detalle: " + e.getMessage());
        }
    }

    public static DBConnector getInstance() {
        if (instance == null) {
            instance = new DBConnector();
        }
        return instance;
    }

    public Connection getConnection() {
        return this.connection;
    }
    
    public void closeConnection() {
        if (this.connection != null) {
            try {
                this.connection.close();
                System.out.println("Cerrando conexión de base de datos.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión.");
            }
        }
    }
    
    public void initializeSchema() {
        try (Statement stmt = this.connection.createStatement()) {
            
            System.out.println("Inicializando esquema de tablas...");
            
            stmt.execute("DROP TABLE IF EXISTS Productos_Fav");
            stmt.execute("DROP TABLE IF EXISTS Pedidos");
            stmt.execute("DROP TABLE IF EXISTS Empleados");
            stmt.execute("DROP TABLE IF EXISTS Productos");

            String sqlCreateProductos = "CREATE TABLE Productos ("
                    + "id INT PRIMARY KEY, "
                    + "nombre VARCHAR(255) NOT NULL, "
                    + "descripcion TEXT, "
                    + "cantidad INT, "
                    + "precio DECIMAL(10, 2)"
                    + ")";
            stmt.execute(sqlCreateProductos);

            String sqlCreateFavoritos = "CREATE TABLE Productos_Fav ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "id_producto INT, "
                    + "FOREIGN KEY (id_producto) REFERENCES Productos(id) ON DELETE CASCADE"
                    + ")";
            stmt.execute(sqlCreateFavoritos);

            String sqlCreateEmpleados = "CREATE TABLE Empleados ("
                    + "id INT PRIMARY KEY, "
                    + "nombre VARCHAR(255) NOT NULL, "
                    + "puesto VARCHAR(100)"
                    + ")";
            stmt.execute(sqlCreateEmpleados);
            
            String sqlCreatePedidos = "CREATE TABLE Pedidos ("
                    + "id INT PRIMARY KEY, "
                    + "id_producto INT, "
                    + "cantidad_pedida INT, "
                    + "FOREIGN KEY (id_producto) REFERENCES Productos(id) ON DELETE RESTRICT"
                    + ")";
            stmt.execute(sqlCreatePedidos);
            
            System.out.println("Esquema creado y listo para la carga de datos.");

        } catch (SQLException e) {
            System.err.println("❌ ERROR al inicializar el esquema: " + e.getMessage());
        }
    }
}