package com.example.proveedores;

import com.example.proveedores.repository.ProveedorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
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
    @Autowired ProveedorRepository repository;
    private final String cuerpo = """
        {"empresa":"Empresa Andina","contacto":"Ana Torres","telefono":"987654321",
         "correo":"ana@andina.pe","direccion":"Lima","rubro":"Servicios"}
        """;
    @AfterEach void limpiarBaseAislada() { repository.deleteAll(); }
    private long registrar() throws Exception {
        var respuesta = mvc.perform(post("/api/proveedores").contentType("application/json").content(cuerpo))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.activo").value(true)).andReturn();
        return json.readTree(respuesta.getResponse().getContentAsString()).get("id").asLong();
    }
    @Test void registraConsultaYBusca() throws Exception {
        long id = registrar();
        mvc.perform(get("/api/proveedores/" + id)).andExpect(jsonPath("$.empresa").value("Empresa Andina"));
        mvc.perform(get("/api/proveedores").param("busqueda", "ANDINA")).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(get("/api/proveedores").param("busqueda", "inexistente")).andExpect(jsonPath("$.length()").value(0));
    }
    @Test void editaSinDetectarseComoDuplicadoYConservaEstado() throws Exception {
        long id = registrar();
        mvc.perform(put("/api/proveedores/" + id).contentType("application/json").content(cuerpo.replace("Ana Torres", "Ana Actualizada")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.contacto").value("Ana Actualizada"));
        mvc.perform(put("/api/proveedores/" + id + "/estado").contentType("application/json").content("{\"activo\":false}"))
            .andExpect(jsonPath("$.activo").value(false));
        mvc.perform(put("/api/proveedores/" + id).contentType("application/json").content(cuerpo)).andExpect(jsonPath("$.activo").value(false));
    }
    @Test void desactivaFiltraYReactivaSinBorrar() throws Exception {
        long id = registrar();
        mvc.perform(delete("/api/proveedores/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/proveedores/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.activo").value(false));
        mvc.perform(get("/api/proveedores").param("activo", "true")).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/proveedores").param("activo", "false")).andExpect(jsonPath("$[0].id").value(id));
        mvc.perform(put("/api/proveedores/" + id + "/estado").contentType("application/json").content("{\"activo\":true}"))
            .andExpect(jsonPath("$.activo").value(true));
    }
    @Test void rechazaDuplicadosIncluyendoInactivosYEdicion() throws Exception {
        long id = registrar();
        mvc.perform(put("/api/proveedores/" + id + "/estado").contentType("application/json").content("{\"activo\":false}"));
        mvc.perform(post("/api/proveedores").contentType("application/json").content(cuerpo.replace("Empresa Andina", " EMPRESA   ANDINA ")))
            .andExpect(status().isConflict());
        var otra = mvc.perform(post("/api/proveedores").contentType("application/json").content(cuerpo.replace("Empresa Andina", "Otra empresa")))
            .andExpect(status().isCreated()).andReturn();
        long otroId = json.readTree(otra.getResponse().getContentAsString()).get("id").asLong();
        mvc.perform(put("/api/proveedores/" + otroId).contentType("application/json").content(cuerpo)).andExpect(status().isConflict());
    }
    @Test void rechazaDatosInvalidosJsonYEstadosVacios() throws Exception {
        mvc.perform(post("/api/proveedores").contentType("application/json").content(cuerpo.replace("987654321", "-------")))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/api/proveedores").contentType("application/json").content("{invalido")).andExpect(status().isBadRequest());
        long id = registrar();
        mvc.perform(put("/api/proveedores/" + id + "/estado").contentType("application/json").content("{}")).andExpect(status().isBadRequest());
    }
    @Test void devuelve404ParaProveedorInexistente() throws Exception {
        mvc.perform(get("/api/proveedores/999999")).andExpect(status().isNotFound());
        mvc.perform(put("/api/proveedores/999999").contentType("application/json").content(cuerpo)).andExpect(status().isNotFound());
        mvc.perform(put("/api/proveedores/999999/estado").contentType("application/json").content("{\"activo\":false}"))
            .andExpect(status().isNotFound());
    }
}
