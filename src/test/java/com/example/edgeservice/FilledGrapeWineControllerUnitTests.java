package com.example.edgeservice;

import com.example.edgeservice.model.Grape;
import com.example.edgeservice.model.Wine;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FilledGrapeWineControllerUnitTests {
    @Value("${wineservice.baseurl}")
    private String wineServiceBaseurl;

    @Value("${grapeservice.baseurl}")
    private String grapeServiceBaseurl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    private Grape grape1 = new Grape( "Testgrape1", "Testregion1", "Testcountry1");
    private Grape grape2 = new Grape( "Testgrape2", "Testregion2", "Testcountry2");

    private Wine wine1 = new Wine( "Testwine1",  "Testregion1", "Testcountry1", 4.0, "Testgrape1");
    private Wine wine2 = new Wine( "Testwine2",  "Testregion2", "Testcountry2", 5.0, "Testgrape2");

    private List<Wine> allWines = Arrays.asList(wine1, wine2);
    private List<Wine> allWinesFromTestregion1 = Arrays.asList(wine1);
    private List<Wine> allWinesFromTestcountry1 = Arrays.asList(wine1);

    @BeforeEach
    public void initializeMockserver() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void whenGetGrapeByWineName_thenReturnFilledGrapeWineJson() throws Exception{
        //GET Wine1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + wineServiceBaseurl + "/wines/name/Testwine1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(wine1)));

        //GET Grape1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + grapeServiceBaseurl + "/grapes/grapename/TestGrape1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(grape1)));

        mockMvc.perform(get("/combo/wine/{name}", "Testwine1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grapeName", is("Testgrape1")))
                .andExpect(jsonPath("$.region", is("Testregion1")))
                .andExpect(jsonPath("$.country", is("Testcountry1")))
                .andExpect(jsonPath("$.name", is("Testwine1")))
                .andExpect(jsonPath("$.score", is(4.0)));
    }

    @Test
    void whenGetWinesAndGrapes_thenReturnFilledGrapeWineJson() throws Exception{
        //GET all wines
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + wineServiceBaseurl + "/wines")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allWines)));

        //GET Grape1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + grapeServiceBaseurl + "/grapes/grapename/TestGrape1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(grape1)));

        //GET Grape2 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + grapeServiceBaseurl + "/grapes/grapename/TestGrape2")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(grape2)));

        mockMvc.perform(get("/combo/wines"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].grapeName", is("Testgrape1")))
                .andExpect(jsonPath("$[0].region", is("Testregion1")))
                .andExpect(jsonPath("$[0].country", is("Testcountry1")))
                .andExpect(jsonPath("$[0].name", is("Testwine1")))
                .andExpect(jsonPath("$[0].score", is(4.0)))
                .andExpect(jsonPath("$[1].grapeName", is("Testgrape2")))
                .andExpect(jsonPath("$[1].region", is("Testregion2")))
                .andExpect(jsonPath("$[1].country", is("Testcountry2")))
                .andExpect(jsonPath("$[1].name", is("Testwine2")))
                .andExpect(jsonPath("$[1].score", is(5.0)));
    }

    @Test
    void whenGetWinesAndGrapesByRegion_thenReturnFilledGrapeWineJson() throws Exception{
        //GET all wines from Testregion1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + wineServiceBaseurl + "/wines/region/Testregion1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allWinesFromTestregion1)));

        //GET Grape1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + grapeServiceBaseurl + "/grapes/grapename/TestGrape1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(grape1)));

        mockMvc.perform(get("/combo/region/{region}", "Testregion1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].grapeName", is("Testgrape1")))
                .andExpect(jsonPath("$[0].region", is("Testregion1")))
                .andExpect(jsonPath("$[0].country", is("Testcountry1")))
                .andExpect(jsonPath("$[0].name", is("Testwine1")))
                .andExpect(jsonPath("$[0].score", is(4.0)));
    }

    @Test
    void whenGetWinesAndGrapesByCountry_thenReturnFilledGrapeWineJson() throws Exception{
        //GET all wines from Testcountry1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + wineServiceBaseurl + "/wines/country/Testcountry1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allWinesFromTestcountry1)));

        //GET Grape1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + grapeServiceBaseurl + "/grapes/grapename/TestGrape1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(grape1)));

        mockMvc.perform(get("/combo/country/{country}", "Testcountry1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].grapeName", is("Testgrape1")))
                .andExpect(jsonPath("$[0].region", is("Testregion1")))
                .andExpect(jsonPath("$[0].country", is("Testcountry1")))
                .andExpect(jsonPath("$[0].name", is("Testwine1")))
                .andExpect(jsonPath("$[0].score", is(4.0)));
    }

    @Test
    void whenAddWine_thenReturnFilledGrapeWineJson() throws Exception{
        Wine wine3 = new Wine( "Testwine3",  "Testregion2", "Testcountry2", 3.0, "Testgrape2");

        //POST wine3 with grape2
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + wineServiceBaseurl + "/wines")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(wine3)));

        //GET Grape2 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + grapeServiceBaseurl + "/grapes/grapename/TestGrape2")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(grape2)));

        mockMvc.perform(post("/combo")
                .param("name", wine3.getName())
                .param("region", wine3.getRegion())
                .param("country", wine3.getCountry())
                .param("score", Double.toString(wine3.getScore()))
                .param("grapeName", wine3.getGrapeName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grapeName", is("Testgrape2")))
                .andExpect(jsonPath("$.region", is("Testregion2")))
                .andExpect(jsonPath("$.country", is("Testcountry2")))
                .andExpect(jsonPath("$.name", is("Testwine3")))
                .andExpect(jsonPath("$.score", is(3.0)));
    }

    @Test
    void whenUpdateWine_thenReturnFilledGrapeWineJson() throws Exception{
        Wine updatedWine1 = new Wine( "Testwine1",  "Testregion1", "Testcountry1", 3.0, "Testgrape1");

        //GET Testwine1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + wineServiceBaseurl + "/wines/name/Testwine1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(wine1)));

        //PUT wine1 with new score 3
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + wineServiceBaseurl + "/wines")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(updatedWine1)));

        //GET Grape1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + grapeServiceBaseurl + "/grapes/grapename/TestGrape1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(grape1)));

        mockMvc.perform(put("/combo")
                .param("name", updatedWine1.getName())
                .param("score", Double.toString(updatedWine1.getScore()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grapeName", is("Testgrape1")))
                .andExpect(jsonPath("$.region", is("Testregion1")))
                .andExpect(jsonPath("$.country", is("Testcountry1")))
                .andExpect(jsonPath("$.name", is("Testwine1")))
                .andExpect(jsonPath("$.score", is(3.0)));
    }

    @Test
    void whenDeleteWine_thenReturnStatusOk() throws Exception{
        //DELETE wine999
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + wineServiceBaseurl + "/wines/name/Testwine999")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK));

        mockMvc.perform(delete("/combo/{name}", "Testwine999"))
                .andExpect(status().isOk());

    }
}