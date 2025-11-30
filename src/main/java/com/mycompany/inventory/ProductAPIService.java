package com.mycompany.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class ProductAPIService {
    
    private static final String API_URL = "https://dummyjson.com/products";

    public List<Product> fetchProductsFromAPI() {
        System.out.println("Conectando a la API de productos para obtener datos...");
        try {
            URL url = new URL(API_URL);
            
            try (InputStream is = url.openStream()) {
                
                ObjectMapper mapper = new ObjectMapper();
                
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                
                @SuppressWarnings("unchecked")
                Map<String, Object> jsonRoot = mapper.readValue(is, Map.class);
                
                @SuppressWarnings("unchecked")
                List<Product> productsList = (List<Product>) mapper.convertValue(
                    jsonRoot.get("products"), 
                    mapper.getTypeFactory().constructCollectionType(List.class, Product.class)
                );
                
                System.out.println("✅ " + productsList.size() + " productos obtenidos y listos para la inserción.");
                return productsList;
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR al obtener/procesar productos de la API: " + e.getMessage());
            return null;
        }
    }
}