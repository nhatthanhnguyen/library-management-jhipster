package com.library.app.web.rest;

import com.library.app.domain.*;
import com.library.app.domain.enumeration.Type;
import com.library.app.repository.*;
import com.library.app.security.SecurityUtils;
import com.library.app.service.MailService;
import com.library.app.service.dto.ResponseMessage;
import com.library.app.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.library.app.domain.Checkout}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CheckoutResource {

    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private static final String ENTITY_NAME = "checkout";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CheckoutRepository checkoutRepository;

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    private final WaitListRepository waitListRepository;

    private final NotificationRepository notificationRepository;

    private final MailService mailService;

    public CheckoutResource(
        CheckoutRepository checkoutRepository,
        UserRepository userRepository,
        AuthorityRepository authorityRepository,
        WaitListRepository waitListRepository,
        NotificationRepository notificationRepository,
        MailService mailService
    ) {
        this.checkoutRepository = checkoutRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.waitListRepository = waitListRepository;
        this.notificationRepository = notificationRepository;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /checkouts} : Create a new checkout.
     *
     * @param checkout the checkout to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new checkout, or with status {@code 400 (Bad Request)} if the checkout has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/checkouts")
    public ResponseEntity<Checkout> createCheckout(@RequestBody Checkout checkout) throws URISyntaxException {
        log.debug("REST request to save Checkout : {}", checkout);
        if (checkout.getId() != null) {
            throw new BadRequestAlertException("A new checkout cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Checkout result = checkoutRepository.save(checkout);
        return ResponseEntity
            .created(new URI("/api/checkouts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /checkouts/:id} : Updates an existing checkout.
     *
     * @param id       the id of the checkout to save.
     * @param checkout the checkout to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated checkout,
     * or with status {@code 400 (Bad Request)} if the checkout is not valid,
     * or with status {@code 500 (Internal Server Error)} if the checkout couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/checkouts/{id}")
    public ResponseEntity<Checkout> updateCheckout(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Checkout checkout
    ) throws URISyntaxException {
        log.debug("REST request to update Checkout : {}, {}", id, checkout);
        if (checkout.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, checkout.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!checkoutRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Checkout result = checkoutRepository.save(checkout);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, checkout.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /checkouts/:id} : Partial updates given fields of an existing checkout, field will ignore if it is null
     *
     * @param id       the id of the checkout to save.
     * @param checkout the checkout to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated checkout,
     * or with status {@code 400 (Bad Request)} if the checkout is not valid,
     * or with status {@code 404 (Not Found)} if the checkout is not found,
     * or with status {@code 500 (Internal Server Error)} if the checkout couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/checkouts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Checkout> partialUpdateCheckout(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Checkout checkout
    ) throws URISyntaxException {
        log.debug("REST request to partial update Checkout partially : {}, {}", id, checkout);
        if (checkout.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, checkout.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!checkoutRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Checkout> result = checkoutRepository
            .findById(checkout.getId())
            .map(existingCheckout -> {
                if (checkout.getStartTime() != null) {
                    existingCheckout.setStartTime(checkout.getStartTime());
                }
                if (checkout.getEndTime() != null) {
                    existingCheckout.setEndTime(checkout.getEndTime());
                }
                if (checkout.getIsReturned() != null) {
                    existingCheckout.setIsReturned(checkout.getIsReturned());
                }

                return existingCheckout;
            })
            .map(checkoutRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, checkout.getId().toString())
        );
    }

    /**
     * {@code GET  /checkouts} : get all the checkouts.
     *
     * @param pageable  the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of checkouts in body.
     */
    @GetMapping("/checkouts")
    public ResponseEntity<List<Checkout>> getAllCheckouts(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Checkouts");
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        Authority authority = authorityRepository.findById("ROLE_ADMIN").get();
        Page<Checkout> page;
        if (user.getAuthorities().contains(authority)) {
            page = checkoutRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = checkoutRepository.findAllByUser(user.getId(), pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /checkouts/:id} : get the "id" checkout.
     *
     * @param id the id of the checkout to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the checkout, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/checkouts/{id}")
    public ResponseEntity<Checkout> getCheckout(@PathVariable Long id) {
        log.debug("REST request to get Checkout : {}", id);
        Optional<Checkout> checkout = checkoutRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(checkout);
    }

    @PostMapping("/checkouts/{id}/return")
    public ResponseMessage returnBook(@PathVariable Long id) {
        Checkout checkout = checkoutRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        log.debug("REST request to return BookCopy : {}", checkout.getBookCopy().getId());
        checkout.setEndTime(Instant.now());
        checkout.setIsReturned(true);
        checkoutRepository.save(checkout);
        Book book = checkout.getBookCopy().getBook();
        List<WaitList> waitLists = waitListRepository.findByBook(book.getId());
        for (WaitList wait : waitLists) {
            waitListRepository.deleteById(wait.getId());
            Notification notification = new Notification();
            notification.setUser(wait.getUser());
            notification.setType(Type.AVAILABLE);
            notification.setSentAt(Instant.now());
            notificationRepository.save(notification);

            log.debug("REST send email book available {} to user {}", wait.getBook().getTitle(), wait.getUser().getLogin());
            mailService.sendBookAvailable(wait.getUser(), wait.getBook());
        }
        return new ResponseMessage(200, "Return book successfully");
    }

    /**
     * {@code DELETE  /checkouts/:id} : delete the "id" checkout.
     *
     * @param id the id of the checkout to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/checkouts/{id}")
    public ResponseEntity<Void> deleteCheckout(@PathVariable Long id) {
        log.debug("REST request to delete Checkout : {}", id);
        checkoutRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
