package com.mycompany.inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class InventoryRepository {

    private final Connection connection;

    public InventoryRepository() {
        this.connection = DBConnector.getInstance().getConnection();
    }
    
    public void bulkInsertProducts(List<Product> products) {
        String sql = "INSERT INTO Productos (id, nombre, descripcion, cantidad, precio) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            for (Product p : products) {
                pstmt.setInt(1, p.getId());
                pstmt.setString(2, p.getName());
                pstmt.setString(3, p.getDescription());
                pstmt.setInt(4, p.getStockQuantity());
                pstmt.setDouble(5, p.getPrice());
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            long insertedCount = 0;
            for (int result : results) {
                if (result > 0) insertedCount += result;
            }
            System.out.println("✅ Carga masiva completada. Total de registros insertados: " + insertedCount);
            
        } catch (SQLException e) {
            System.err.println("❌ ERROR en inserción masiva de productos: " + e.getMessage());
        }
    }

    public void insertSampleEmployees() {
        try (Statement stmt = connection.createStatement()) {
            
            String sql1 = "INSERT INTO Empleados (id, nombre, puesto) VALUES (1, 'Silvia Ramos', 'Jefa de Compras')";
            String sql2 = "INSERT INTO Empleados (id, nombre, puesto) VALUES (2, 'Carlos Ruiz', 'Especialista en Sistemas')";
            String sql3 = "INSERT INTO Empleados (id, nombre, puesto) VALUES (3, 'Elena Vidal', 'Gestora de Cuentas')";
            
            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
            stmt.executeUpdate(sql3);
            
            System.out.println("✅ Datos de 'Empleados' agregados (3 registros).");
            
        } catch (SQLException e) {
            System.err.println("❌ ERROR al insertar empleados: " + e.getMessage());
        }
    }
    
    public void insertSampleOrders() {
        try (Statement stmt = connection.createStatement()) {
            
            String sqlA = "INSERT INTO Pedidos (id, id_producto, cantidad_pedida) VALUES (2001, 3, 1)";
            String sqlB = "INSERT INTO Pedidos (id, id_producto, cantidad_pedida) VALUES (2002, 7, 3)";
            String sqlC = "INSERT INTO Pedidos (id, id_producto, cantidad_pedida) VALUES (2003, 15, 1)";
            String sqlD = "INSERT INTO Pedidos (id, id_producto, cantidad_pedida) VALUES (2004, 28, 50)";
            
            stmt.executeUpdate(sqlA);
            stmt.executeUpdate(sqlB);
            stmt.executeUpdate(sqlC);
            stmt.executeUpdate(sqlD);
            
            System.out.println("✅ Datos de 'Pedidos' agregados (4 registros).");
            
        } catch (SQLException e) {
            System.err.println("❌ ERROR al insertar pedidos: " + e.getMessage());
        }
    }

    public void displayAllProducts() {
        String sql = "SELECT id, nombre, precio, cantidad FROM Productos ORDER BY id ASC";

        System.out.println("\n--- 🛒 LISTADO COMPLETO DE PRODUCTOS ---");
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nombre");
                double price = rs.getDouble("precio");
                int stock = rs.getInt("cantidad");
                System.out.printf("ID: %-3d | Nombre: %-50s | Precio: %7.2f€ | Stock: %d\n", 
                                  id, name, price, stock);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ ERROR al listar todos los productos: " + e.getMessage());
        }
    }

    public void insertHighValueFavorites(double minPrice) {
        String sql = "INSERT INTO Productos_Fav (id_producto) SELECT id FROM Productos WHERE precio > ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, minPrice);
            int rowsAffected = pstmt.executeUpdate();
            
            System.out.printf("✅ Operación 'Favoritos' completada. Se han marcado %d productos con precio superior a %.2f€.\n", 
                              rowsAffected, minPrice);
            
        } catch (SQLException e) {
             if (!e.getSQLState().startsWith("23")) { 
                 System.err.println("❌ ERROR inesperado al insertar favoritos: " + e.getMessage());
             }
        }
    }

    public void displayFavoriteProductDetails() {
        String sql = "SELECT prod.id, prod.nombre, prod.precio "
                   + "FROM Productos prod JOIN Productos_Fav fav "
                   + "ON prod.id = fav.id_producto";

        System.out.println("\n--- ⭐️ PRODUCTOS FAVORITOS CON DETALLE ---");
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nombre");
                double price = rs.getDouble("precio");
                System.out.printf("[FAV - ID %d] %s: %.2f€\n", id, name, price);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ ERROR al mostrar productos favoritos: " + e.getMessage());
        }
    }

    public void displayProductsUnderPrice(double maxPrice) {
        String sql = "SELECT id, nombre, precio FROM Productos WHERE precio < ? ORDER BY precio DESC";

        System.out.printf("\n--- 📉 PRODUCTOS CON PRECIO INFERIOR A %.2f€ ---\n", maxPrice);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            
            pstmt.setDouble(1, maxPrice);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("nombre");
                    double price = rs.getDouble("precio");
                    System.out.printf("   ID: %d | Precio: %.2f€ | Nombre: %s\n", id, price, name);
                    count++;
                }
                System.out.println("\n✅ Total de resultados encontrados: " + count);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ ERROR al consultar productos por precio: " + e.getMessage());
        }
    }
}