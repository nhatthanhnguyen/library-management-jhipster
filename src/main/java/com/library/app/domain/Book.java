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
 * A Book.
 */
@Entity
@Table(name = "book")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @OneToMany(mappedBy = "book")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "checkouts", "holds", "publisher", "book" }, allowSetters = true)
    private Set<BookCopy> bookCopies = new HashSet<>();

    @OneToMany(mappedBy = "book")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "book" }, allowSetters = true)
    private Set<WaitList> waitLists = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Category category;

    @ManyToMany(mappedBy = "books")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Set<Author> authors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Book id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Book title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<BookCopy> getBookCopies() {
        return this.bookCopies;
    }

    public void setBookCopies(Set<BookCopy> bookCopies) {
        if (this.bookCopies != null) {
            this.bookCopies.forEach(i -> i.setBook(null));
        }
        if (bookCopies != null) {
            bookCopies.forEach(i -> i.setBook(this));
        }
        this.bookCopies = bookCopies;
    }

    public Book bookCopies(Set<BookCopy> bookCopies) {
        this.setBookCopies(bookCopies);
        return this;
    }

    public Book addBookCopy(BookCopy bookCopy) {
        this.bookCopies.add(bookCopy);
        bookCopy.setBook(this);
        return this;
    }

    public Book removeBookCopy(BookCopy bookCopy) {
        this.bookCopies.remove(bookCopy);
        bookCopy.setBook(null);
        return this;
    }

    public Set<WaitList> getWaitLists() {
        return this.waitLists;
    }

    public void setWaitLists(Set<WaitList> waitLists) {
        if (this.waitLists != null) {
            this.waitLists.forEach(i -> i.setBook(null));
        }
        if (waitLists != null) {
            waitLists.forEach(i -> i.setBook(this));
        }
        this.waitLists = waitLists;
    }

    public Book waitLists(Set<WaitList> waitLists) {
        this.setWaitLists(waitLists);
        return this;
    }

    public Book addWaitList(WaitList waitList) {
        this.waitLists.add(waitList);
        waitList.setBook(this);
        return this;
    }

    public Book removeWaitList(WaitList waitList) {
        this.waitLists.remove(waitList);
        waitList.setBook(null);
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Book category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Set<Author> getAuthors() {
        return this.authors;
    }

    public void setAuthors(Set<Author> authors) {
        if (this.authors != null) {
            this.authors.forEach(i -> i.removeBook(this));
        }
        if (authors != null) {
            authors.forEach(i -> i.addBook(this));
        }
        this.authors = authors;
    }

    public Book authors(Set<Author> authors) {
        this.setAuthors(authors);
        return this;
    }

    public Book addAuthor(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
        return this;
    }

    public Book removeAuthor(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return id != null && id.equals(((Book) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            "}";
    }
}
