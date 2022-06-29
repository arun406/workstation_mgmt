package com.accelya.product.workstationmanagement.workstation.resource;

import com.accelya.product.workstationmanagement.workstation.transferobjects.GenericResponse;
import com.accelya.product.workstationmanagement.workstation.transferobjects.PagedData;
import com.accelya.product.workstationmanagement.workstation.transferobjects.WorkstationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = {"classpath:application-integrationtest.yml"})
@ActiveProfiles("integrationtest")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkstationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @LocalServerPort
    private int port;

    @Test
    @Order(1)
    void createWorkstation() throws Exception {
        WorkstationDTO workstationDTO = getWorkstationDTO();

        ResponseEntity<GenericResponse<WorkstationDTO>> response = post(new URI("/cargo/reference-data/v1/warehouse/locations"), workstationDTO, new ParameterizedTypeReference<GenericResponse<WorkstationDTO>>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        GenericResponse<WorkstationDTO> body = response.getBody();
        assertThat((body.getStatus())).isEqualTo("success");
        assertThat(body.getData().getId()).isNotNull();
        assertThat(body.getData().getId()).isEqualTo(1);
        assertThat(body.getData().getActive()).isTrue();
    }

    private WorkstationDTO getWorkstationDTO() {
        WorkstationDTO workstationDTO = WorkstationDTO.builder()
                .airport("DXB")
                .code("WORKSTATION1")
                .fixed(Boolean.TRUE)
                .name("Workstation 1")
                .multipleULDAllowed(Boolean.TRUE)
                .open(Boolean.TRUE)
                .productType("GCR")
                .section("SECTION1")
                .serviceable(Boolean.TRUE)
                .shc(Arrays.asList("HEA", "PIL"))
                .warehouse("WAREHOUSE1")
                .type("WORKSTATION")
                .build();
        return workstationDTO;
    }

    @Test
    @Order(2)
    public void getWorkstationById() throws Exception {
        ResponseEntity<GenericResponse<WorkstationDTO>> responseEntity = get(URI.create("/cargo/reference-data/v1/warehouse/locations/1"), new ParameterizedTypeReference<GenericResponse<WorkstationDTO>>() {
        });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo("success");
        assertThat(responseEntity.getBody().getData().getId()).isEqualTo(1);
    }

    @Test
    @Order(3)
    public void getNotExistingWorkstationById() throws Exception {
        ResponseEntity<GenericResponse<WorkstationDTO>> responseEntity = get(URI.create("/cargo/reference-data/v1/warehouse/locations/2"),
                new ParameterizedTypeReference<GenericResponse<WorkstationDTO>>() {
                });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo("failure");
    }

    @Test
    @Order(4)
    public void updateExistingWorkstationById() throws Exception {
        WorkstationDTO workstationDTO = WorkstationDTO.builder()
                .airport("DXB")
                .code("WORKSTATION_MODIFIED")
                .fixed(Boolean.TRUE)
                .name("Workstation 1")
                .multipleULDAllowed(Boolean.TRUE)
                .open(Boolean.TRUE)
                .productType("GCR")
                .section("SECTION2")
                .serviceable(Boolean.TRUE)
                .shc(Arrays.asList("HEA", "PIL"))
                .warehouse("WAREHOUSE2")
                .type("WORKSTATION")
                .build();

        ResponseEntity<GenericResponse<WorkstationDTO>> responseEntity = put(URI.create("/cargo/reference-data/v1/warehouse/locations/1"), workstationDTO, new ParameterizedTypeReference<GenericResponse<WorkstationDTO>>() {
        });

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo("success");
        assertThat(responseEntity.getBody().getData().getWarehouse()).isEqualTo("WAREHOUSE2");
        assertThat(responseEntity.getBody().getData().getCode()).isEqualTo("WORKSTATION_MODIFIED");
        assertThat(responseEntity.getBody().getData().getSection()).isEqualTo("SECTION2");
    }

    @Test
    @Order(5)
    public void deactivateWorkstationById() throws Exception {
        ResponseEntity<GenericResponse<String>> responseEntity = put(URI.create("/cargo/reference-data/v1/warehouse/locations/1/actions/deactivate"), new ParameterizedTypeReference<GenericResponse<String>>() {
        });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo("success");
        assertThat(responseEntity.getBody().getData()).isEqualTo("workstation is deactivated successfully");
    }

    @Test
    @Order(6)
    public void activateWorkstationById() throws Exception {
        ResponseEntity<GenericResponse<String>> responseEntity = put(URI.create("/cargo/reference-data/v1/warehouse/locations/1/actions/activate"), new ParameterizedTypeReference<GenericResponse<String>>() {
        });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo("success");
        assertThat(responseEntity.getBody().getData()).isEqualTo("workstation is activated successfully");
    }

    @Test
    @Order(7)
    public void deactivateWorkstationsInBulk() throws Exception {
        List<Integer> ids = Arrays.asList(1);
        ResponseEntity<GenericResponse<String>> responseEntity = put(URI.create("/cargo/reference-data/v1/warehouse/locations/actions/deactivate"), ids, new ParameterizedTypeReference<GenericResponse<String>>() {
        });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo("success");
        assertThat(responseEntity.getBody().getData()).isEqualTo("workstations are deactivated successfully");
    }

    @Test
    @Order(8)
    public void activateWorkstationsInBulk() throws Exception {
        List<Integer> ids = Arrays.asList(1);
        ResponseEntity<GenericResponse<String>> responseEntity = put(URI.create("/cargo/reference-data/v1/warehouse/locations/actions/activate"), ids, new ParameterizedTypeReference<GenericResponse<String>>() {
        });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo("success");
        assertThat(responseEntity.getBody().getData()).isEqualTo("workstations are activated successfully");
    }

    @Test
    @Order(9)
    public void deleteWorkstationsInBulk() throws Exception {
        List<Integer> ids = Arrays.asList(1);
        ResponseEntity<GenericResponse<String>> responseEntity = put(URI.create("/cargo/reference-data/v1/warehouse/locations/actions/delete"), ids, new ParameterizedTypeReference<GenericResponse<String>>() {
        });
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo("success");
        assertThat(responseEntity.getBody().getData()).isEqualTo("workstations are deleted successfully");
    }

    @Test
    @Order(10)
    public void deleteWorkstationById() throws Exception {
        // setup : create workstation
        WorkstationDTO workstationDTO = getWorkstationDTO();
        ResponseEntity<GenericResponse<WorkstationDTO>> response = post(new URI("/cargo/reference-data/v1/warehouse/locations"), workstationDTO, new ParameterizedTypeReference<GenericResponse<WorkstationDTO>>() {
        });
        // execute
        ResponseEntity<GenericResponse<String>> responseEntity = delete(URI.create("/cargo/reference-data/v1/warehouse/locations/" + response.getBody().getData().getId()), new ParameterizedTypeReference<GenericResponse<String>>() {
        });

        //verify
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getStatus()).isEqualTo("success");
        assertThat(responseEntity.getBody().getData()).isEqualTo("workstation deleted successfully");
    }


    @Test
    @Order(11)
    public void searchWorkstationsByCriteria() throws Exception {
        // setup : create workstation
        WorkstationDTO workstationDTO = getWorkstationDTO();
        ResponseEntity<GenericResponse<WorkstationDTO>> response = post(new URI("/cargo/reference-data/v1/warehouse/locations"), workstationDTO, new ParameterizedTypeReference<GenericResponse<WorkstationDTO>>() {
        });

        String urlTemplate = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/cargo/reference-data/v1/warehouse/locations")
                .queryParam("code", "{code}")
                .queryParam("airport", "{airport}")
                .queryParam("warehouse", "{warehouse}")
                .queryParam("section", "{section}")
                .queryParam("type", "{type}")
                .queryParam("name", "{name}")
                .queryParam("productType", "{productType}")
                .queryParam("open", "{open}")
                .queryParam("serviceable", "{serviceable}")
                .queryParam("fixed", "{fixed}")
                .queryParam("active", "{active}")
                .queryParam("shc", "{shc}")
                .queryParam("multipleULDAllowed", "{multipleULDAllowed}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("code", "WORKSTATION1");
        params.put("airport", "DXB");
        params.put("warehouse", "WAREHOUSE1");
        params.put("section", "SECTION1");
        params.put("type", "WORKSTATION");
        params.put("name", "Workstation 1");
        params.put("productType", "GCR");
        params.put("shc", "HEA");
        params.put("open", String.valueOf(Boolean.TRUE));
        params.put("serviceable", String.valueOf(Boolean.TRUE));
        params.put("fixed", String.valueOf(Boolean.TRUE));
        params.put("active", String.valueOf(Boolean.TRUE));
        params.put("multipleULDAllowed", String.valueOf(Boolean.TRUE));
        //
        ResponseEntity<GenericResponse<PagedData<WorkstationDTO>>> responseEntity =
                get(urlTemplate, new ParameterizedTypeReference<GenericResponse<PagedData<WorkstationDTO>>>() {
                }, params);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    private <T> ResponseEntity<GenericResponse<PagedData<WorkstationDTO>>> get(String urlTemplate, ParameterizedTypeReference<GenericResponse<PagedData<WorkstationDTO>>> responseType, Map<String, String> params) {
        return restTemplate.exchange(urlTemplate, GET, null, responseType, params);
    }

    public <T> ResponseEntity<GenericResponse<T>> post(URI uri, WorkstationDTO data, ParameterizedTypeReference<GenericResponse<T>> responseType) {
        HttpEntity<WorkstationDTO> request = new HttpEntity<>(data);
        return restTemplate.exchange(uri, POST, request, responseType);
    }

    public <T> ResponseEntity<GenericResponse<T>> get(URI uri, ParameterizedTypeReference<GenericResponse<T>> responseType) {
        return restTemplate.exchange(uri, GET, null, responseType);
    }

    public <T> ResponseEntity<GenericResponse<T>> put(URI uri, WorkstationDTO data, ParameterizedTypeReference<GenericResponse<T>> responseType) {
        HttpEntity<WorkstationDTO> request = new HttpEntity<>(data);
        return restTemplate.exchange(uri, PUT, request, responseType);
    }

    public <T> ResponseEntity<GenericResponse<T>> put(URI uri, List<Integer> data, ParameterizedTypeReference<GenericResponse<T>> responseType) {
        HttpEntity<List<Integer>> request = new HttpEntity<>(data);
        return restTemplate.exchange(uri, PUT, request, responseType);
    }

    public <T> ResponseEntity<GenericResponse<T>> put(URI uri, ParameterizedTypeReference<GenericResponse<T>> responseType) {
        return restTemplate.exchange(uri, PUT, null, responseType);
    }

    public <T> ResponseEntity<GenericResponse<T>> delete(URI uri, ParameterizedTypeReference<GenericResponse<T>> responseType) {
        return restTemplate.exchange(uri, DELETE, null, responseType);
    }
}