package com.example.unittest;

import com.example.unittest.controllers.UserController;
import com.example.unittest.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    //serve a trasformare gli oggetti in Json
    private ObjectMapper objectMapper;
    //verifico chje il mio controller non sia nullo
    @Test
    void userControllerNotNUll(){
        assertThat(userController).isNotNull();
    }

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
    }
    //per evitare di duplicare codice ho creato una classe private che prende l user dall'id
    private User getUserFromId(Long id) throws Exception {
        //lo leggiamo dal nostro freamework
        MvcResult mvcResult = this.mockMvc.perform(get("/user/"+id))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //non la lunghezza della risposta e 0 mi torni nullo
        try {
            String userJSon= mvcResult.getResponse().getContentAsString();
            //verifichiamo che l'utente inserito sia uguale a quello creato
            return objectMapper.readValue(userJSon, User.class);
        }catch (Exception e) {
            return null;
        }
    }
    //funzione accessoria per creare useer
    private User createUser() throws Exception {
        User user = new User();
        user.setName("Domenico");
        user.setSurname("Cdelan");
        return createUser(user);
    }

    private User createUser(User user) throws Exception {
        MvcResult mvcResult = createUserRequest();
        //leggi il valore (mvcResult.getResponse().getContentAsString()) convertilo in user (User.class)
        User userFromResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        return userFromResponse;
    }

    private MvcResult createUserRequest() throws Exception {
        User user = new User();
        user.setName("Domenico");
        user.setSurname("Cdelan");
        return createUserRequest(user);
    }

    private MvcResult createUserRequest(User user) throws Exception {
        if (user == null) return null;
        //trasformo l'oogetto in stringa con link per spiegazione
        //https://www.baeldung.com/jackson-object-mapper-tutorial
        String json = objectMapper.writeValueAsString(user);
        //dico di fare il post di user a mockMVC
        MvcResult mvcResult = this.mockMvc.perform((post("/user")
                        //gli dico che è un json
                        .contentType(MediaType.APPLICATION_JSON)
                        //gli do il contenuto
                        .content(json)))
                //gli dico di stampare tutta la rispons
                .andDo(print())
                //aspettati che sia tutto ok
                .andExpect(status().isOk())
                //fai return e spegni la chiamata post
                .andReturn();
        return mvcResult;
    }

    @Test
    void createUserTest() throws Exception {
        User userFromResponse = createUser();
        //controlla che l'id non è nulla infatti se cosi è l'entita è stata persistita
        assertThat(userFromResponse.getId()).isNotNull();
    }

    @Test
    void readUserList() throws Exception {
        createUserRequest();
        //dico a MVC di fare un get
        MvcResult mvcResult = this.mockMvc.perform(get("/user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<User> usersFromResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), List.class);
        System.out.println("User data base are " + usersFromResponse.size());
        assertThat(usersFromResponse.size()).isNotZero();
    }

    @Test
    void readSingleUser() throws Exception{
        //creo utente
        User user = createUser();
        //verifico che esista l'id e non sia nullo
        assertThat(user.getId()).isNotNull();
        //lo leggiamo dal nostro frameworf
        User userFromResponse= getUserFromId(user.getId());
        assertThat(userFromResponse.getId()).isNotNull();
        assertThat(userFromResponse.getId()).isEqualTo(user.getId());

    }
    @Test
    void updateUser() throws Exception {
        User user= createUser();
        assertThat(user.getId()).isNotNull();

        String newName = "Giovanni";
        user.setName(newName);
        String json = objectMapper.writeValueAsString(user);
        //dico di fare il post di user a mockMVC
        MvcResult mvcResult = this.mockMvc.perform((put("/user/"+user.getId())
                        //gli dico che è un json
                        .contentType(MediaType.APPLICATION_JSON)
                        //gli do il contenuto
                        .content(json)))
                //gli dico di stampare tutta la rispons
                .andDo(print())
                //aspettati che sia tutto ok
                .andExpect(status().isOk())
                //fai return
                .andReturn();
        User usersFromResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);

        //qui stiamo controllando che il nostro put sia andato a buon fine
        assertThat(usersFromResponse.getId()).isEqualTo(user.getId());
        assertThat(usersFromResponse.getName()).isEqualTo(newName);

        //prendiamo l 'user con il get
        User userFromResponseGet= getUserFromId(user.getId());
        assertThat(usersFromResponse.getId()).isNotNull();
        assertThat(userFromResponseGet.getId()).isEqualTo(user.getId());
        assertThat(userFromResponseGet.getName()).isEqualTo(newName);
    }
    @Test
    void deleteUser() throws Exception{
        User user= createUser();
        assertThat(user.getId()).isNotNull();
        //dico di fare il delete di user a mockMVC
        MvcResult mvcResult = this.mockMvc.perform((delete("/user/"+user.getId())))
                //gli dico di stampare tutta la rispons
                .andDo(print())
                //aspettati che sia tutto ok
                .andExpect(status().isOk())
                //fai return
                .andReturn();
        User userFromResponseGet = getUserFromId(user.getId());
        assertThat(userFromResponseGet).isNull();
    }

}
