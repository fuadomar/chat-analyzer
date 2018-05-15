package chat.analyzer;

import chat.analyzer.auth.service.EmailInvitationServiceImpl;
import chat.analyzer.capcha.service.GoogleReCaptchaService;
import chat.analyzer.capcha.service.IReCaptchaService;
import chat.analyzer.dao.UserAccountDao;
import chat.analyzer.domain.entity.EmailInvitation;
import chat.analyzer.domain.entity.Role;
import chat.analyzer.domain.entity.UserAccount;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import chat.analyzer.config.WebSecurityConfig;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:test.properties")
@Import(WebSecurityConfig.class)
public class ChatAnalyzerApplicationTests {

  public static final String REGISTRATION_VIEW_NAME = "userRegistration";

  public static final String USER_REGISTRATION_URI = "/userRegistration";

  @Autowired private WebApplicationContext context;

  @Autowired org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

  @Autowired private GoogleReCaptchaService googleReCaptchaService;

  @Autowired private UserAccountDao userAccountDao;

  @Autowired private EmailInvitationServiceImpl emailInvitationService;

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
  public void testShouldRedirectUnauthorizedUser() throws Exception {
    this.mockMvc.perform(get("/")).andExpect(status().is3xxRedirection());
  }

  @Test
  //@WithMockUser(username = "test", password = "test", roles = {"USER", "ROLE_ANONYMOUS_CHAT"} )
  public void testShouldReturnChatViewForAnonymousUser() throws Exception {

    UserAccount userAccount = new UserAccount("test", "test");
    userAccount.setPassword(new BCryptPasswordEncoder().encode(userAccount.getPassword()));
    userAccount.setEnabled(true);
    userAccount.setRole(Arrays.asList(new Role("ROLE_ANONYMOUS_CHAT"), new Role("ROLE_USER")));
    EmailInvitation emailInvitation = new EmailInvitation("test", "test", "test");
    Mockito.when(userAccountDao.findByName("test")).thenReturn(userAccount);
    Mockito.when(emailInvitationService.findByToken("token")).thenReturn(emailInvitation);
    this.mockMvc.perform(get("/chat/anonymous")).andExpect(status().is3xxRedirection()).andExpect(view().name("chat"));
  }

  @Test
  @WithMockUser(username = "test", password = "test", roles = "USER")
  public void testShouldReturnChatView() throws Exception {

    UserAccount userAccount = new UserAccount("test", "test");
    Mockito.when(userAccountDao.findByName("test")).thenReturn(userAccount);
    this.mockMvc.perform(get("/chat")).andExpect(status().isOk()).andExpect(view().name("chat"));
  }

  @Test
  public void testShouldReturnRegistrationView() throws Exception {

    this.mockMvc
        .perform(get(USER_REGISTRATION_URI))
        .andExpect(status().isOk())
        .andExpect(view().name(REGISTRATION_VIEW_NAME))
        .andDo(print());
  }

  @Test
  public void testShouldReturnRegistrationIfUserNameLengthLessThanThreshold() throws Exception {

    String validReCaptcha = "validReCaptcha";
    Mockito.when(googleReCaptchaService.validate(validReCaptcha)).thenReturn(true);
    RequestBuilder requestBuilder = getPostRequestBuilder("te", "121212121212", "validCaptcha");
    this.mvc.perform(requestBuilder).andExpect(view().name(REGISTRATION_VIEW_NAME));
  }

  @Test
  public void testShouldCreateNewUserAccount() throws Exception {

    String validReCaptcha = "validReCaptcha";
    Mockito.when(googleReCaptchaService.validate(validReCaptcha)).thenReturn(true);
    RequestBuilder requestBuilder = getPostRequestBuilder("test2", "test2test2", validReCaptcha);
    this.mvc
        .perform(requestBuilder)
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/chat"));
  }

  @Test
  public void testShouldNotCreateNewUserAccount() throws Exception {

    String inValidReCaptcha = "inValidReCaptcha";
    Mockito.when(googleReCaptchaService.validate(inValidReCaptcha)).thenReturn(false);
    RequestBuilder requestBuilder = getPostRequestBuilder("test2", "test2test2", inValidReCaptcha);
    this.mvc.perform(requestBuilder).andExpect(view().name("userRegistration"));
  }

  private RequestBuilder getPostRequestBuilder(String name, String password, String recaptcha) {

    return post("/userRegistration")
        .param("name", name)
        .param("password", password)
        .param("g-recaptcha-response", recaptcha)
        .param("role", "USER")
        .accept(MediaType.TEXT_PLAIN);
  }
}
