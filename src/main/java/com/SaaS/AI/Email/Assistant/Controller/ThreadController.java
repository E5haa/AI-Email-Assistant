package com.SaaS.AI.Email.Assistant.Controller;

import com.SaaS.AI.Email.Assistant.Entity.EmailThread;
import com.SaaS.AI.Email.Assistant.Service.ThreadService;
import com.SaaS.AI.Email.Assistant.dto.ThreadRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/threads")
public class ThreadController {

    public ThreadService threadService;
    public ThreadController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @PostMapping
    public ResponseEntity createThread(@RequestBody ThreadRequest threadRequest) {
        threadService.createThread(threadRequest);
        return ResponseEntity.status(201).body("Thread created successfully");
    }

    @GetMapping
    public ResponseEntity<List<EmailThread>> getThreads() {
        List<EmailThread> threads = threadService.getThreads();
        return ResponseEntity.ok(threads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailThread> getThread(@PathVariable Long id) {
        EmailThread thread = threadService.getThread(id);
        return ResponseEntity.ok(thread);
    }
}
