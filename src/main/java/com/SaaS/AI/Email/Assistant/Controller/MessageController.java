package com.SaaS.AI.Email.Assistant.Controller;

import com.SaaS.AI.Email.Assistant.Entity.Message;
import com.SaaS.AI.Email.Assistant.Service.AIService;
import com.SaaS.AI.Email.Assistant.dto.MessageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/generate")
public class MessageController {

    private AIService aiService;

    public MessageController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping
    public ResponseEntity<String> sendMessages(@RequestBody MessageRequest messageRequest) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        System.out.println("User: " + email);

        String aiReply = aiService.sendMessage(messageRequest);
        return ResponseEntity.status(201).body(aiReply);

    }
}
