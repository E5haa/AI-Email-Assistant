package com.SaaS.AI.Email.Assistant.Service;

import com.SaaS.AI.Email.Assistant.Entity.EmailThread;
import com.SaaS.AI.Email.Assistant.Entity.User;
import com.SaaS.AI.Email.Assistant.dto.ThreadRequest;
import com.SaaS.AI.Email.Assistant.Repository.EmailThreadRepo;
import com.SaaS.AI.Email.Assistant.Repository.UserRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ThreadService {
    private EmailThreadRepo emailThreadRepo;
    private UserRepo userRepo;

    public ThreadService(EmailThreadRepo emailThreadRepo, UserRepo userRepo) {
        this.emailThreadRepo = emailThreadRepo;
        this.userRepo = userRepo;
    }

    public void createThread(ThreadRequest request) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        EmailThread thread = EmailThread.builder()
                .title(request.getTitle())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        emailThreadRepo.save(thread);
    }

    public List<EmailThread> getThreads() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return emailThreadRepo.findByUserId(user.getId());
    }

    public EmailThread getThread(Long threadId) {
        EmailThread emailThread = emailThreadRepo.findById(threadId).orElseThrow(() -> new RuntimeException("Thread not found"));


        return emailThread;
    }
}

