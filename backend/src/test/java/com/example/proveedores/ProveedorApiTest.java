package com.example.proveedores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:proveedores;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class ProveedorApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Test void cicloCompleto() throws Exception {
        String cuerpo = """
            {"empresa":"Andina","contacto":"Ana","telefono":"987654321",
             "correo":"ana@andina.pe","direccion":"Lima","rubro":"Suministros"}
            """;
        var respuesta = mvc.perform(post("/api/proveedores").contentType("application/json").content(cuerpo))
            .andExpect(status().isCreated()).andReturn();
        long id = json.readTree(respuesta.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(get("/api/proveedores").param("busqueda", "ANDINA"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get("/api/proveedores").param("busqueda", "inexistente"))
            .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(delete("/api/proveedores/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/proveedores").param("busqueda", "Andina"))
            .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(delete("/api/proveedores/" + id)).andExpect(status().isNotFound());
    }
    @Test void rechazaDatosInvalidos() throws Exception {
        mvc.perform(post("/api/proveedores").contentType("application/json").content("""
            {"empresa":"","contacto":"Ana","telefono":"abc","correo":"mal",
             "direccion":"Lima","rubro":"Suministros"}
            """)).andExpect(status().isBadRequest());
    }
}
