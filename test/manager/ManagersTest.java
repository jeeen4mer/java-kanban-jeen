package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {
    @Test
    void classManagersReturnNotNullManagers() {
        assertNotNull(Managers.getDefault());
        assertNotNull(Managers.getDefaultHistory());
    }
}
