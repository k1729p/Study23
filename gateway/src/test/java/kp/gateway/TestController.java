package kp.gateway;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kp.gateway.TestConstants.*;

/**
 * Controller used by gateway tests.
 */
@RestController
@RequestMapping(ROOT_PATH)
class TestController {

    /**
     * The authorized endpoint.
     *
     * @return the result
     */
    @PreAuthorize("hasAuthority('SCOPE_TEST')")
    @GetMapping(PING_PATH)
    public String ping() {
        return RESULT;
    }
}
