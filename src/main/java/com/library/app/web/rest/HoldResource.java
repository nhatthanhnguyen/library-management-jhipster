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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.library.app.domain.Hold}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class HoldResource {

    private final Logger log = LoggerFactory.getLogger(HoldResource.class);

    private static final String ENTITY_NAME = "hold";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HoldRepository holdRepository;

    private final WaitListRepository waitListRepository;

    private final UserRepository userRepository;

    private final NotificationRepository notificationRepository;

    private final CheckoutRepository checkoutRepository;

    private final MailService mailService;

    public HoldResource(
        HoldRepository holdRepository,
        WaitListRepository waitListRepository,
        UserRepository userRepository,
        NotificationRepository notificationRepository,
        CheckoutRepository checkoutRepository,
        MailService mailService
    ) {
        this.holdRepository = holdRepository;
        this.waitListRepository = waitListRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.checkoutRepository = checkoutRepository;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /holds} : Create a new hold.
     *
     * @param hold the hold to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new hold, or with status {@code 400 (Bad Request)} if the hold has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/holds")
    public ResponseEntity<Hold> createHold(@RequestBody Hold hold) throws URISyntaxException {
        log.debug("REST request to save Hold : {}", hold);
        if (hold.getId() != null) {
            throw new BadRequestAlertException("A new hold cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Hold result = holdRepository.save(hold);
        return ResponseEntity
            .created(new URI("/api/holds/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /holds/:id} : Updates an existing hold.
     *
     * @param id the id of the hold to save.
     * @param hold the hold to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hold,
     * or with status {@code 400 (Bad Request)} if the hold is not valid,
     * or with status {@code 500 (Internal Server Error)} if the hold couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/holds/{id}")
    public ResponseEntity<Hold> updateHold(@PathVariable(value = "id", required = false) final Long id, @RequestBody Hold hold)
        throws URISyntaxException {
        log.debug("REST request to update Hold : {}, {}", id, hold);
        if (hold.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hold.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!holdRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Hold result = holdRepository.save(hold);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, hold.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /holds/:id} : Partial updates given fields of an existing hold, field will ignore if it is null
     *
     * @param id the id of the hold to save.
     * @param hold the hold to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hold,
     * or with status {@code 400 (Bad Request)} if the hold is not valid,
     * or with status {@code 404 (Not Found)} if the hold is not found,
     * or with status {@code 500 (Internal Server Error)} if the hold couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/holds/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Hold> partialUpdateHold(@PathVariable(value = "id", required = false) final Long id, @RequestBody Hold hold)
        throws URISyntaxException {
        log.debug("REST request to partial update Hold partially : {}, {}", id, hold);
        if (hold.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hold.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!holdRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Hold> result = holdRepository
            .findById(hold.getId())
            .map(existingHold -> {
                if (hold.getStartTime() != null) {
                    existingHold.setStartTime(hold.getStartTime());
                }
                if (hold.getEndTime() != null) {
                    existingHold.setEndTime(hold.getEndTime());
                }

                return existingHold;
            })
            .map(holdRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, hold.getId().toString())
        );
    }

    /**
     * {@code GET  /holds} : get all the holds.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of holds in body.
     */
    @GetMapping("/holds")
    public ResponseEntity<List<Hold>> getAllHolds(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Holds");
        Page<Hold> page;
        if (eagerload) {
            page = holdRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = holdRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /holds/:id} : get the "id" hold.
     *
     * @param id the id of the hold to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the hold, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/holds/{id}")
    public ResponseEntity<Hold> getHold(@PathVariable Long id) {
        log.debug("REST request to get Hold : {}", id);
        Optional<Hold> hold = holdRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(hold);
    }

    @PostMapping("/holds/{id}/issue")
    public ResponseMessage issueBook(@PathVariable Long id) {
        log.debug("");
        Hold hold = holdRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        Instant instant = Instant.now();
        hold.setEndTime(instant);
        holdRepository.save(hold);
        Checkout checkout = new Checkout();
        checkout.setUser(hold.getUser());
        checkout.setBookCopy(hold.getBookCopy());
        checkout.setStartTime(instant);
        checkout.setIsReturned(false);
        checkoutRepository.save(checkout);
        return new ResponseMessage(200, "Issue book successfully");
    }

    /**
     * {@code DELETE  /holds/:id} : delete the "id" hold.
     *
     * @param id the id of the hold to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/holds/{id}")
    public ResponseEntity<Void> deleteHold(@PathVariable Long id) {
        log.debug("REST request to delete Hold : {}", id);
        Hold hold = holdRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        holdRepository.deleteById(id);
        Book book = hold.getBookCopy().getBook();
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
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
