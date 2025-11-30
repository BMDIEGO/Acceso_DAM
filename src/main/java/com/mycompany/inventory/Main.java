package com.mycompany.inventory;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        
        DBConnector connector = DBConnector.getInstance();
        
        if (connector.getConnection() == null) {
            System.err.println("Error de conexion. Saliendo.");
            return;
        }

        InventoryRepository repository = new InventoryRepository();
        ProductAPIService apiService = new ProductAPIService();

        try {
            System.out.println("--- INICIO TAREA AD ---");
            
            System.out.println("Inicializando tablas...");
            connector.initializeSchema();
            
            System.out.println("Cargando productos de la API...");
            List<Product> products = apiService.fetchProductsFromAPI();
            
            if (products != null && !products.isEmpty()) {
                repository.bulkInsertProducts(products);
            } else {
                System.out.println("AVISO: No se pudieron cargar productos de la API.");
                return;
            }

            System.out.println("Insertando empleados y pedidos de prueba...");
            repository.insertSampleEmployees();
            repository.insertSampleOrders();
            
            System.out.println("--- OPERACIONES ---");
            
            repository.displayAllProducts();

            repository.insertHighValueFavorites(1000.00);

            repository.displayFavoriteProductDetails();

            repository.displayProductsUnderPrice(600.00);

        } catch (Exception e) {
            System.err.println("Error inesperado en el main.");
            e.printStackTrace();
        } finally {
            connector.closeConnection();
            System.out.println("FIN DE LA APLICACION.");
        }
    }
}
