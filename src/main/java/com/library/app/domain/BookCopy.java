package com.library.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BookCopy.
 */
@Entity
@Table(name = "book_copy")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookCopy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "year_published", nullable = false)
    private Integer yearPublished;

    @OneToMany(mappedBy = "bookCopy")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "bookCopy" }, allowSetters = true)
    private Set<Checkout> checkouts = new HashSet<>();

    @OneToMany(mappedBy = "bookCopy")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "bookCopy" }, allowSetters = true)
    private Set<Hold> holds = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "bookCopies" }, allowSetters = true)
    private Publisher publisher;

    @ManyToOne
    @JsonIgnoreProperties(value = { "bookCopies", "waitLists", "category", "authors" }, allowSetters = true)
    private Book book;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BookCopy id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYearPublished() {
        return this.yearPublished;
    }

    public BookCopy yearPublished(Integer yearPublished) {
        this.setYearPublished(yearPublished);
        return this;
    }

    public void setYearPublished(Integer yearPublished) {
        this.yearPublished = yearPublished;
    }

    public Set<Checkout> getCheckouts() {
        return this.checkouts;
    }

    public void setCheckouts(Set<Checkout> checkouts) {
        if (this.checkouts != null) {
            this.checkouts.forEach(i -> i.setBookCopy(null));
        }
        if (checkouts != null) {
            checkouts.forEach(i -> i.setBookCopy(this));
        }
        this.checkouts = checkouts;
    }

    public BookCopy checkouts(Set<Checkout> checkouts) {
        this.setCheckouts(checkouts);
        return this;
    }

    public BookCopy addCheckout(Checkout checkout) {
        this.checkouts.add(checkout);
        checkout.setBookCopy(this);
        return this;
    }

    public BookCopy removeCheckout(Checkout checkout) {
        this.checkouts.remove(checkout);
        checkout.setBookCopy(null);
        return this;
    }

    public Set<Hold> getHolds() {
        return this.holds;
    }

    public void setHolds(Set<Hold> holds) {
        if (this.holds != null) {
            this.holds.forEach(i -> i.setBookCopy(null));
        }
        if (holds != null) {
            holds.forEach(i -> i.setBookCopy(this));
        }
        this.holds = holds;
    }

    public BookCopy holds(Set<Hold> holds) {
        this.setHolds(holds);
        return this;
    }

    public BookCopy addHold(Hold hold) {
        this.holds.add(hold);
        hold.setBookCopy(this);
        return this;
    }

    public BookCopy removeHold(Hold hold) {
        this.holds.remove(hold);
        hold.setBookCopy(null);
        return this;
    }

    public Publisher getPublisher() {
        return this.publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public BookCopy publisher(Publisher publisher) {
        this.setPublisher(publisher);
        return this;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public BookCopy book(Book book) {
        this.setBook(book);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookCopy)) {
            return false;
        }
        return id != null && id.equals(((BookCopy) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookCopy{" +
            "id=" + getId() +
            ", yearPublished=" + getYearPublished() +
            "}";
    }
}
