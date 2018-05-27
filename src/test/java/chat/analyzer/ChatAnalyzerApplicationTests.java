package chat.analyzer;

import chat.analyzer.service.EmailInvitationServiceImpl;
import chat.analyzer.auth.service.UserDetailsServiceImpl;
import chat.analyzer.capcha.service.GoogleReCaptchaService;
import chat.analyzer.dao.UserAccountDao;
import chat.analyzer.domain.DTO.ToneAnalyzerFeedBackDTO;
import chat.analyzer.domain.entity.EmailInvitation;
import chat.analyzer.domain.entity.Role;
import chat.analyzer.domain.entity.UserAccount;
import chat.analyzer.domain.DTO.ChatMessageDTO;
import chat.analyzer.gateway.ChatAnalyzerGateway;
import chat.analyzer.service.tone.recognizer.ToneAnalyzerService;
import com.google.common.collect.Maps;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
@Import(WebSecurityConfig.class)
public class ChatAnalyzerApplicationTests {

  private static final String REGISTRATION_VIEW_NAME = "userRegistration";

  private static final String USER_REGISTRATION_URI = "/userRegistration";

  @Autowired private WebApplicationContext context;

  @Autowired org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

  @Autowired private GoogleReCaptchaService googleReCaptchaService;

  @Autowired private UserAccountDao userAccountDao;

  @Autowired private EmailInvitationServiceImpl emailInvitationService;

  @Autowired private UserDetailsServiceImpl userDetailsService;

  @Autowired private ChatAnalyzerGateway chatAnalyzerGateway;

  @Autowired ToneAnalyzerService toneAnalyzerService;

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
  public void testShouldReturnRegistrationViewForAnonymousUser() throws Exception {

    EmailInvitation emailInvitation = new EmailInvitation("test123", "test123", "test123");
    Mockito.when(emailInvitationService.findByToken(anyString())).thenReturn(emailInvitation);
    this.mockMvc
        .perform(get("/chat/anonymous").param("token", "test123"))
        .andExpect(view().name("anonymousUserRegistration"));
  }

  @Test
  public void testShouldReturnChatViewForAnonymousUser() throws Exception {

    UserAccount userAccount = new UserAccount("test123", "test123");
    userAccount.setPassword(new BCryptPasswordEncoder().encode(userAccount.getPassword()));
    userAccount.setEnabled(true);
    userAccount.setRole(Arrays.asList(new Role("ROLE_ANONYMOUS"), new Role("ROLE_USER")));

    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    for (Role rol : userAccount.getRole()) {
      grantedAuthorities.add(new SimpleGrantedAuthority(rol.getName()));
    }

    UserDetails userDetails =
        new org.springframework.security.core.userdetails.User(
            userAccount.getName(), userAccount.getPassword(), grantedAuthorities);

    EmailInvitation emailInvitation = new EmailInvitation("test123", "test123", "test123");
    Mockito.when(userAccountDao.findByName(anyString())).thenReturn(null);
    Mockito.when(emailInvitationService.findByToken(anyString())).thenReturn(emailInvitation);
    Mockito.when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
    Mockito.when(userAccountDao.findByName(anyString())).thenReturn(userAccount);

    RequestBuilder requestBuilder =
        createPostRequestBuilderChatAnonymous("test123", "test123", "test123");

    this.mockMvc
        .perform(requestBuilder)
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/chat?invited=test123"));
  }

  @Test
  @WithMockUser(username = "test", password = "test", roles = "USER")
  public void testShouldReturnAnonymousChatUri() throws Exception {

    String fakeAnonymousUri = "http://localhost/chat/anonymous?token=";
    this.mockMvc
        .perform(get("/anonymousChatUri"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(fakeAnonymousUri)));
  }

  @Test
  @WithMockUser(username = "test", password = "test", roles = "USER")
  public void testShouldReturnChatToneBetweenTwoUser() throws Exception {

    ChatMessageDTO chatMessageDTO = new ChatMessageDTO("test", "testing chat tone");
    ToneAnalyzerFeedBackDTO toneAnalyzerFeedBackDTO = new ToneAnalyzerFeedBackDTO();
    toneAnalyzerFeedBackDTO.put("Anger", 0.159056);
    toneAnalyzerFeedBackDTO.put("Disgust", 0.143912);
    Mockito.when(toneAnalyzerService.analyzeChatToneBetweenSenderAndReceiver(chatMessageDTO))
        .thenReturn(toneAnalyzerFeedBackDTO);
    ToneAnalyzerFeedBackDTO toneAnalyzerFeedBackDTO1 =
        chatAnalyzerGateway.analyzeChatToneBetweenSenderAndReceiver(chatMessageDTO);

    int size = toneAnalyzerFeedBackDTO.size();
    int size1 = toneAnalyzerFeedBackDTO1.size();
    Assert.assertEquals(size, size1);
    Assert.assertTrue(
        Maps.difference(toneAnalyzerFeedBackDTO1, toneAnalyzerFeedBackDTO).areEqual());
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
    RequestBuilder requestBuilder =
        createPostRequestBuilderUserRegistration("te", "121212121212", "validCaptcha");
    this.mvc.perform(requestBuilder).andExpect(view().name(REGISTRATION_VIEW_NAME));
  }

  @Test
  public void testShouldCreateNewUserAccount() throws Exception {

    String validReCaptcha = "validReCaptcha";

    UserAccount userAccount = new UserAccount("test123", "test123123");
    userAccount.setPassword(new BCryptPasswordEncoder().encode(userAccount.getPassword()));
    userAccount.setEnabled(true);
    userAccount.setRole(Arrays.asList(new Role("ROLE_USER")));

    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    for (Role rol : userAccount.getRole()) {
      grantedAuthorities.add(new SimpleGrantedAuthority(rol.getName()));
    }

    UserDetails userDetails =
        new org.springframework.security.core.userdetails.User(
            userAccount.getName(), userAccount.getPassword(), grantedAuthorities);

    Mockito.when(googleReCaptchaService.validate(validReCaptcha)).thenReturn(true);
    Mockito.when(userAccountDao.findByName(anyString())).thenReturn(userAccount);
    Mockito.when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
    Mockito.when(userAccountDao.findByName(anyString())).thenReturn(userAccount);

    RequestBuilder requestBuilder =
        createPostRequestBuilderUserRegistration("test123", "test123123", validReCaptcha);
    this.mvc
        .perform(requestBuilder)
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/chat"));
  }

  @Test
  public void testShouldNotCreateNewUserAccount() throws Exception {

    String inValidReCaptcha = "inValidReCaptcha";
    Mockito.when(googleReCaptchaService.validate(inValidReCaptcha)).thenReturn(false);
    RequestBuilder requestBuilder =
        createPostRequestBuilderUserRegistration("test2", "test2test2", inValidReCaptcha);
    this.mvc.perform(requestBuilder).andExpect(view().name("userRegistration"));
  }

  private RequestBuilder createPostRequestBuilderUserRegistration(
      String name, String password, String recaptcha) {

    return post("/userRegistration")
        .param("name", name)
        .param("password", password)
        .param("g-recaptcha-response", recaptcha)
        .param("role", "USER")
        .with(csrf())
        .accept(MediaType.TEXT_PLAIN);
  }

  private RequestBuilder createPostRequestBuilderChatAnonymous(
      String name, String token, String invitedBy) {

    return post("/chat/anonymous")
        .param("name", name)
        .param("token", token)
        .with(csrf())
        .accept(MediaType.TEXT_PLAIN);
  }
}
