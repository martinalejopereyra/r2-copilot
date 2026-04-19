package org.example.onboardingcopilot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OAuthCallbackController {

    @GetMapping("/callback")
    @ResponseBody
    public String callback() {
        return "<html><body><p>Authentication successful. You can close this window.</p></body></html>";
    }
}