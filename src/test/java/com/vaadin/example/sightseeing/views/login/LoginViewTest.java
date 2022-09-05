package com.vaadin.example.sightseeing.views.login;

import com.vaadin.example.sightseeing.views.map.MapView;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
// when creating Spring Boot Tests, remember to extend SpringUIUnitTests, not UIUnitTest
public class LoginViewTest extends SpringUIUnitTest {

    @Test
    public void loginAsUser() {
        LoginView loginView = navigate(LoginView.class);
        LoginOverlay overlay = $(LoginOverlay.class).first();
        Assertions.assertTrue(overlay != null);
        test(overlay).login("user", "user");
        Assertions.assertEquals(MapView.class, getCurrentView().getClass());
    }


}
