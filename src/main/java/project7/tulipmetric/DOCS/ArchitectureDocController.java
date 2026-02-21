package project7.tulipmetric.DOCS;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArchitectureDocController {

    @GetMapping("/architecture")
    public String architecture() {
        return "DOCS/architecture";
    }
}