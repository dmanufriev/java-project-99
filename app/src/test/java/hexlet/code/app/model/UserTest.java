package hexlet.code.app.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {

    @Test
    public void testSetPasswordDigest() throws Exception {
        String password = "SimplePassword";
        var user = new User();

        user.setPasswordDigest(password);
        String passwordDigest = user.getPasswordDigest();

        assertThat(passwordDigest.length()).isGreaterThanOrEqualTo(3);
        assertThat(passwordDigest).isNotEqualTo(password);
    }

}
