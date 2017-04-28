package tone.analyzer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import tone.analyzer.config.WebSecurityConfig;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@Import(WebSecurityConfig.class)
public class ToneAnalyzerApplicationTests {

  public static final String REGISTRATION_VIEW_NAME = "registration";
  public static final String USER_REGISTRATION_URI = "/user-registration";
  @Autowired private WebApplicationContext context;

  @Autowired org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

  private MockMvc mockMvc;

  private MockMvc mvc;

  @Before
  public void setUp() {

    this.mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @After
  public void tearUp() {

    mongoTemplate.getDb().dropDatabase();
  }

  @Test
  public void contextLoads() {}

  @Test
  public void testRedirectUnauthorizedUser() throws Exception {
    this.mockMvc.perform(get("/")).andExpect(status().is3xxRedirection());
  }

  @Test
  @WithMockUser(username = "test", roles = "USER")
  public void testShouldReturnChat() throws Exception {

    this.mockMvc
        .perform(get("/live-chat"))
        .andExpect(status().isOk())
        .andExpect(view().name("chat"));
  }

  @Test
  public void testShouldReturnRegistration() throws Exception {

    this.mockMvc
        .perform(get(USER_REGISTRATION_URI))
        .andExpect(status().isOk())
        .andExpect(view().name(REGISTRATION_VIEW_NAME))
        .andDo(print());
  }

  @Test
  public void shouldReturnRegistrationIfUserNameLengthLessThanThreshold() throws Exception {

    RequestBuilder requestBuilder = getPostRequestBuilder("te", "121212121212");
    this.mvc.perform(requestBuilder).andExpect(view().name(REGISTRATION_VIEW_NAME));
  }

  private RequestBuilder getPostRequestBuilder(String name, String password) {
    return post("/user-registration")
        .param("name", name)
        .param("password", password)
        .param("role", "USER")
        .accept(MediaType.TEXT_PLAIN);
  }

  @Test
  public void testShouldCreateNewAccount() throws Exception {

    RequestBuilder requestBuilder = getPostRequestBuilder("test2", "test2test2");
    this.mvc
        .perform(requestBuilder)
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/chat"));
  }
}
