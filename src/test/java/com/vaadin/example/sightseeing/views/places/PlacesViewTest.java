package com.vaadin.example.sightseeing.views.places;

import com.vaadin.example.sightseeing.views.map.MapView;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
public class PlacesViewTest extends SpringUIUnitTest {

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminUser_placesView_viewShown() {
        navigate(PlacesView.class);

        Assertions.assertTrue($(Grid.class).first().isVisible(),
                "Grid should be visible for admin users");
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void user_placesView_viewNotShown() {
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            navigate(PlacesView.class);
        });

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminUser_placesView_navigate() {
        navigate(PlacesView.class);
        $(Button.class).withCaption("navigate").first().click();
        Assertions.assertEquals(getCurrentView().getClass(), MapView.class);

    }

}
