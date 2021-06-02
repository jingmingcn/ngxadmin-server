package com.iotqlu.ngxadmin.api;

import com.iotqlu.ngxadmin.domain.dto.AuthorView;
import com.iotqlu.ngxadmin.domain.dto.BookView;
import com.iotqlu.ngxadmin.domain.dto.EditBookRequest;
import com.iotqlu.ngxadmin.domain.dto.ListResponse;
import com.iotqlu.ngxadmin.domain.dto.SearchBooksQuery;
import com.iotqlu.ngxadmin.domain.dto.SearchRequest;
import com.iotqlu.ngxadmin.domain.model.Role;
import com.iotqlu.ngxadmin.service.AuthorService;
import com.iotqlu.ngxadmin.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@Tag(name = "Book")
@RestController @RequestMapping(path = "api/book")
public class BookApi {

    private final BookService bookService;
    private final AuthorService authorService;

    public BookApi(BookService bookService,
                   AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @RolesAllowed(Role.BOOK_ADMIN)
    @PostMapping
    public BookView create(@RequestBody @Valid EditBookRequest request) {
        return bookService.create(request);
    }

    @RolesAllowed(Role.BOOK_ADMIN)
    @PutMapping("{id}")
    public BookView edit(@PathVariable String id, @RequestBody @Valid EditBookRequest request) {
        return bookService.update(new ObjectId(id), request);
    }

    @RolesAllowed(Role.BOOK_ADMIN)
    @DeleteMapping("{id}")
    public BookView delete(@PathVariable String id) {
        return bookService.delete(new ObjectId(id));
    }

    @GetMapping("{id}")
    public BookView get(@PathVariable String id) {
        return bookService.getBook(new ObjectId(id));
    }

    @GetMapping("{id}/author")
    public ListResponse<AuthorView> getAuthors(@PathVariable String id) {
        return new ListResponse<>(authorService.getBookAuthors(new ObjectId(id)));
    }

    @PostMapping("search")
    public ListResponse<BookView> search(@RequestBody @Valid SearchRequest<SearchBooksQuery> request) {
        return new ListResponse<>(bookService.searchBooks(request.getPage(), request.getQuery()));
    }

}
