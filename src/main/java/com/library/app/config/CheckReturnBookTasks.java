package com.library.app.config;

import com.library.app.domain.Checkout;
import com.library.app.domain.User;
import com.library.app.repository.CheckoutRepository;
import com.library.app.repository.UserRepository;
import com.library.app.service.MailService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CheckReturnBookTasks {

    private final UserRepository userRepository;

    private final CheckoutRepository checkoutRepository;

    private final MailService mailService;

    public CheckReturnBookTasks(UserRepository userRepository, CheckoutRepository checkoutRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.checkoutRepository = checkoutRepository;
        this.mailService = mailService;
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    @Async
    public void runTask() {
        List<Checkout> checkouts = checkoutRepository.findCheckoutsThatNotReturned();
        for (Checkout checkout : checkouts) {
            Instant currentTime = Instant.now();
            long minutesDifference = ChronoUnit.MINUTES.between(checkout.getStartTime(), currentTime);
            if (minutesDifference >= 8 && minutesDifference <= 10 && checkout.getUser().isActivated()) {
                mailService.sendBookReturnReminder(checkout.getUser(), checkout.getBookCopy().getBook());
            }
            if (minutesDifference > 10 && checkout.getUser().isActivated()) {
                User user = checkout.getUser();
                user.setActivated(false);
                userRepository.save(user);
            }
        }
    }
}
